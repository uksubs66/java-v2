/***************************************************************************************
 *   Proshop_sheet_checkin: This servlet will implement the check in/out and
 *                          POS charge sending funtionality.  This will initially only
 *                          handle check in/out calls for the Club Prophet Systems POS,
 *                          but will eventually handle ALL check in/out calls.
 *
 *
 *   Called by:     called by Proshop_sheet
 *
 *
 *   Created:       2/23/2009
 *
 *
 *   Last Updated:
 *
 *        8/29/13  Colorado Springs GC (coloradospringscountryclub) - Updated URL for their CPV3 interface.
 *        4/24/13  Added Tedesco CC (tedescocc) to CPV3 interface.
 *        4/09/13  Edison Club (edisonclub) - Updated CPV3 interface URL.
 *        3/14/13  Fairlawn CC (fairlawncc) - Updated CPV3 interface URL.
 *        3/11/13  Added Fairlawn CC (fairlawncc) to CPV3 interface.
 *        2/28/13  Improved error handling in the IBS code path.
 *        2/06/13  Ledgemont CC (ledgemontcc) - Updated CPV3 interface URL.
 *       12/06/12  CPS - Save the error in retrunCode so the pro sees it immediately when member is missing a posid (buildChargeCPS).
 *       12/06/12  CPS - Do not attempt to build charges if the posid is not set (buildChargeCPS). Instead add a history entry for POS Report.
 *                       Also, set failed = true if URL is not found for club in sendChargeCPS.  This corrects a problem where players were 
 *                       flaged as having charges sent when they weren't actually sent because of a missing posid. 
 *                       In checkCPV3charges do not set posSent flag until we actually send the charge (in cases where player charges are combined) in 
 *                       case the send fails or charges are rejected.
 *       11/30/12  CPS - set 'POS Processed' when charges sent for a single player like we were doing for the whole group.  
 *                       This should prevent duplicate charges being sent when the response from CPS is slow.
 *                       Also, set the processed flgs prior to sending the charges rather than after since we don't get control back immediately.
 *       11/19/12  Edison Club (edisonclub) - Upadted CPV3 interface URL.
 *       11/15/12  Added Ledgemont CC (ledgemontcc) to the CPV3 interface.
 *       10/31/12  Marshfield CC (marshfieldcc) - Updated IP for their CPV3 interface.
 *        7/18/12  Added Marshfield CC (marshfieldcc) to the CPV3 interface.
 *        7/12/12  Add processing for Handicap No Post button on tee sheet - enable/disable the nopost flags in tee time.
 *        6/23/12  Updated IP addy for olympiafieldscc 
 *        6/12/12  Removed redundant ccrockies conditional clause in CPV3 code.
 *        6/05/12  Add custom POS processing for Olympia Fields (multiple courses).
 *        5/18/12   Update Edison's IP.
 *        5/15/12  Added Fox Den CC (foxdencountryclub) and CC of the Rockies (ccrockies) to the CPV3 interface.
 *        5/01/12  Added Olympia Fields CC (olympiafieldscc) to the CPV3 interface.
 *        4/26/12  Changed the IP address for Edison Club.
 *        4/26/12  Changed the IP address for Bracketts.
 *        4/18/12  Added Bracketts Crossing (bracketts) to the CPV3 interface.
 *        4/06/12  Forest Highlands GC (foresthighlands) - Updated CPV3 interface URL.
 *        4/04/12  Added Sugar Valley GC (sugarvalleygc) to the CPV3 interface.
 *        4/04/12  Edison Club (edisonclub) - Upadted CPV3 interface URL.
 *        3/23/12  Added Hillwood CC (hillwoodcc) to CPV3 interface.
 *        1/17/12  Added Champions Run (championsrun) to CPV3 interface.
 *       12/15/11  IBS - sendChargeIBS - add a temp check for a meaningless error that IBS returns.
 *       10/13/11  Crane Creek CC (cranecreek) - Updated URL to the CPV3 interface.
 *       10/04/11  Race Brook CC (racebrook) - Added doRaceBrookCheckIn() method to perform the custom check-in process for Race Brook's custom proshop view.
 *        9/08/11  Dallas Athletic Club (dallasathleticclub) - Updated URL to the CPV3 interface.
 *        8/23/11  CPS POS - correct the setPOSprocessed method so only the players that had charges sent are set to processed, and
 *                           add calls to setPOSprocessed for all players in checkCPV3charge method (was only calling for player1).
 *        8/22/11  CPS POS - set waiting = 1 in pos_hist entry when sending a charge, then clear it when a response is received.
 *                 This will prevent duplicate entries in the POS Report when a charge is not processed and the user sends it again later.
 *        8/10/11  Crane Creek CC (cranecreek) - Added URL to the CPV3 interface.
 *        8/01/11  Updated pos_hist insert statement to include the new date_time field.
 *        7/15/11  Forest Highlands GC (foresthighlands) - Updated Club Prophet POS URL.
 *        7/01/11  Added setPOSprocessed method for CPS processing.  This will set the pos flags to 'processed' (1) as soon
 *                 as we get a response from CPS (for each individual player).  This will prevent some double sends when CPS
 *                 fails to respond to our send for one or more players in the group.  The player(s) previous to the failed
 *                 player will now be set as processed, preventing them from being processed again later.  (for CC of Rockies)
 *        3/31/11  Weston GC (westongolfclub) - Added URL to the CPV3 interface.
 *        2/06/11  Add sendChargeIBS method for sending charges to the new IBS interface.
 *        8/02/10  Fort Collins/Fox Hill CC - if Fox Hill CC course then do not do POS (PSK).  Only Fort Collins uses the POS I/F.
 *        7/21/10  Add pwgolf URL to the CPV3 interface
 *        7/20/10  Update checkCPV3charges to check for players that have paid at the counter.  If charges to send
 *                 and the next player has the same posid as the current player, then we must also check if that
 *                 next player already paid.  This fixes a problem where charges for player 3 or 4 were getting added 
 *                 to the charges for player 1 when player 2 paid at the counter (Forest Highlands & Colorado Springs). 
 *        6/11/10  Add coloradospringscountryclub to the CPV3 interface 
 *        6/07/10  ClubProphet - do not set pos sent on Check All if Pay Now (Paid) is already set.
 *        5/10/10  Added tracePOS method to trace the CPS V3 interface.
 *        4/13/10  Added oldoaks to the new Club Prophet interface.
 *        4/08/10  PSK (old version) - fix some errors. Double charges occurred whenever a guest was included.  The POS
 *                                     charges sent flag was never getting set.
 *        3/31/10  Fort Collins/Greeley - if Greeley course then do not do POS (PSK).  Only Fort Collins uses the POS I/F.
 *        3/29/10  Add Edison Club to the new Club Prophet interface
 *        1/08/10  CC Rockies - custom to add the member name and guest name to the POS charge when passed to CPS (case 1772).
 *       10/09/09  Add Dallas Athletic Club to the Club Prophet interface.
 *        8/22/09  Added checkin support for GenRez
 *        6/17/09  Add a new check-in image to show when player is checked in and POS charges have been sent.
 *        6/16/09  Update the new PSK (CPS) POS processing to combine all charges in a tee time for the same member.
 *        5/14/09  Add the old PSK POS Interface processing.
 *        5/13/09  Add Pay Now processing and allow all clubs to use the Check-in feature.
 *        5/12/09  Add Saucon Valley URL to the CPV3 interface
 *        5/12/09  Add ccrockies URL to the CPV3 interface
 *        5/12/09  If Check-All selected, check if any players are not yet checked in to determine if we need
 *                 to check for pending POS charges.
 *        5/12/09  Allow demo sites to use the new PSK interface (CPV3).  Also, verify that club has been configured
 *                 and warn user if not.
 *        4/28/09  Added URL's for Cherry Hills and Forest Highlands
 *        3/11/09  Add tempoary check for Cherry Hills for POS I/F test.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.net.URL;
import javax.jws.WebService;
import javax.xml.ws.WebServiceRef;
import javax.xml.namespace.QName;

import org.tempuri.*;       // CPS
import org.ibsservices.*;   // IBS

   
// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;


public class Proshop_sheet_checkin extends HttpServlet {

    static String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)

    static IntegrationServiceSoap service;                    // SOAP for Club Prophet Systems POS Interface


 //*****************************************************
 // Process the doget method for this page
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setContentType("text/html");

    PrintWriter out = null;


    try { out = resp.getWriter(); }
    catch (Exception ignore) {}

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    HttpSession session = req.getSession(false);

    if (session == null) return;

    con = SystemUtils.getCon(session);

    if (con == null) return;

    // get the club name for any custom processing
    String club = (String)session.getAttribute("club");

    //
    //  Go process check-in for Activities if request from FlxRez
    //
    if (req.getParameter("psid") != null) {
        
        handleActivities(req, club, session, out, con);    // Go process check-in for Activities
        return;
    }

    
    //
    //  Go process request to toggle the No-Post Handicap flags for specified tee time
    //
    if (req.getParameter("nopost") != null) {
        
        handleNoPost(req, club, session, out, con);    // Go process No-Post request
        return;
    }

    
    int new_show = 0;       // will hold the NEW value for show
    int new_show_image = 0;
    int teecurr_id = 0;
    int playerNum = 0;
    int show = 0;
    int posvalue = 0;

    int [] showA = new int [6];      // array to hold the show values for selected tee time (at indexes 1 - 5)
    int [] posA = new int [6];       // array to hold the pos values for selected tee time (at indexes 1 - 5)

    String [] playerA = new String [6];       // array to hold the players

    String course = "";
    String payNow = "";
    String image = "";
    String title = "";

    String updShow = "";

    //
    //  Statements to update show and pos values
    //
    //  pos values:  0 = not processed or sent, 1 = charges processed, 2 = paid at counter, 3 = processed and sent
    //
    String updShow1 = "UPDATE teecurr2 SET show1 = ? WHERE teecurr_id = ?";
    String updShow1c = "UPDATE teecurr2 SET show1 = ?, pos1 = 1 WHERE teecurr_id = ?";    // set pos processed
    String updShow1s = "UPDATE teecurr2 SET show1 = ?, pos1 = 3 WHERE teecurr_id = ?";    // set pos processed and sent
    //String updShow1r = "UPDATE teecurr2 SET show1 = ?, pos1 = 0 WHERE teecurr_id = ?";
    String updShow2 = "UPDATE teecurr2 SET show2 = ? WHERE teecurr_id = ?";
    String updShow2c = "UPDATE teecurr2 SET show2 = ?, pos2 = 1 WHERE teecurr_id = ?";
    String updShow2s = "UPDATE teecurr2 SET show2 = ?, pos2 = 3 WHERE teecurr_id = ?";
    //String updShow2r = "UPDATE teecurr2 SET show2 = ?, pos2 = 0 WHERE teecurr_id = ?";
    String updShow3 = "UPDATE teecurr2 SET show3 = ? WHERE teecurr_id = ?";
    String updShow3c = "UPDATE teecurr2 SET show3 = ?, pos3 = 1 WHERE teecurr_id = ?";
    String updShow3s = "UPDATE teecurr2 SET show3 = ?, pos3 = 3 WHERE teecurr_id = ?";
    //String updShow3r = "UPDATE teecurr2 SET show3 = ?, pos3 = 0 WHERE teecurr_id = ?";
    String updShow4 = "UPDATE teecurr2 SET show4 = ? WHERE teecurr_id = ?";
    String updShow4c = "UPDATE teecurr2 SET show4 = ?, pos4 = 1 WHERE teecurr_id = ?";
    String updShow4s = "UPDATE teecurr2 SET show4 = ?, pos4 = 3 WHERE teecurr_id = ?";
    //String updShow4r = "UPDATE teecurr2 SET show4 = ?, pos4 = 0 WHERE teecurr_id = ?";
    String updShow5 = "UPDATE teecurr2 SET show5 = ? WHERE teecurr_id = ?";
    String updShow5c = "UPDATE teecurr2 SET show5 = ?, pos5 = 1 WHERE teecurr_id = ?";
    String updShow5s = "UPDATE teecurr2 SET show5 = ?, pos5 = 3 WHERE teecurr_id = ?";
    //String updShow5r = "UPDATE teecurr2 SET show5 = ?, pos5 = 0 WHERE teecurr_id = ?";

    // String updShowAll = "UPDATE teecurr2 SET show1=1, show2=1, show3=1, show4=1, show5=1 WHERE teecurr_id = ?";
    // String updShowAll2 = "UPDATE teecurr2 SET show1=1, show2=1, show3=1, show4=1, show5=1, pos1=1, pos2=1, pos3=1, pos4=1, pos5=1 " +
    //                     "WHERE teecurr_id = ?";
  
    String [] updShowA = { updShow, updShow1, updShow2, updShow3, updShow4, updShow5 };      
    String [] updShowcA = { updShow, updShow1c, updShow2c, updShow3c, updShow4c, updShow5c };      
    String [] updShowsA = { updShow, updShow1s, updShow2s, updShow3s, updShow4s, updShow5s };      

    
    boolean checkPOS = false;
    boolean charges = false;
    boolean pos_done = false;
    boolean pos_sent = false;
    boolean skipPos = false;
      
    boolean [] posSentA = new boolean [6];       // array to hold the pos sent flags

    //
    //  Make sure we receive the required parms
    //
    if (req.getParameter("tid") == null || req.getParameter("pNum") == null) {

       Utilities.logError("Parm error in Proshop_sheet_checkin - Parm missing (tid or pNum).");
       return;
    }


    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(0, con);   // this portion is golf only

    //
    //  parm block to hold the POS parameters
    //
    parmPOS parmp = new parmPOS();          // allocate a parm block for POS parms


    //
    //  Get the parms passed from Proshop_sheet
    //
    if (req.getParameter("course") != null) {

       course = req.getParameter("course");          // course for the selected tee time
    }

    String refImage = req.getParameter("imgId");       // current image

    //
    //  Must be a 'Pay Now' or 'Check-in' request
    //
    payNow = "no";
    
    if (refImage.startsWith("paybox")) {

       payNow = "yes";
 
    } else if (!refImage.startsWith("chkbox") && req.getParameter("custom") == null) {
       
       Utilities.logError("Parm error in Proshop_sheet_checkin - imgId parm is invalid. " +refImage);
       return;       
    }
    
    // get the teecurr_id
    String tmp = req.getParameter("tid");
    try {
        teecurr_id = Integer.parseInt(tmp);
    } catch (NumberFormatException exc) {}

    // get the player number (0=all)
    tmp = req.getParameter("pNum");
    try {
        playerNum = Integer.parseInt(tmp);
    } catch (NumberFormatException exc) {}


    if (playerNum > 5) {

        out.println("Error: Player Number is Greater Than 5.");
        return;
    }

    if ((club.equals("racebrook")) && req.getParameter("custom") != null) {
        
        doRaceBrookCheckIn(teecurr_id, playerNum, refImage, out, con);
        return;
    }
    
    //
    //  Check for "Pay Now" call
    //
    if (payNow.equalsIgnoreCase("yes")) {

       doPayNow(club, course, refImage, teecurr_id, playerNum, out, con);                         
       return;
    }
    
    
    //
    //  Custom check
    //
    if (club.equals("fortcollins") && (course.equals("Greeley CC") || course.equals("Fox Hill CC"))) {
            
       skipPos = true;       // do not process POS charges for this course (Greeley Club - they share one site with Fort Collins)
    }
    
     
    
    try {

        getClub.getParms(con, parm);               // get the club parms

        getClub.getPOS(con, parmp, course);        // get POS parms

    }
     catch (Exception e1) {
    }

    parmp.course = course;                         // save course name
    parmp.club = club;                             // save club name

     //    Get current show values for the selected tee time
    try {

        pstmt = con.prepareStatement("" +
                "SELECT date, time, fb, player1, player2, player3, player4, player5, " +
                "show1, show2, show3, show4, show5, pos1, pos2, pos3, pos4, pos5 " +
                "FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, teecurr_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

           parmp.date = rs.getLong("date");
           parmp.time = rs.getInt("time");
           parmp.hist_fb = rs.getInt("fb");
           playerA[1] = rs.getString("player1");    // get players in this tee time
           playerA[2] = rs.getString("player2");
           playerA[3] = rs.getString("player3");
           playerA[4] = rs.getString("player4");
           playerA[5] = rs.getString("player5");
           showA[1] = rs.getInt("show1");        // get current show values for this tee time
           showA[2] = rs.getInt("show2");
           showA[3] = rs.getInt("show3");
           showA[4] = rs.getInt("show4");
           showA[5] = rs.getInt("show5");
           posA[1] = rs.getInt("pos1");
           posA[2] = rs.getInt("pos2");
           posA[3] = rs.getInt("pos3");
           posA[4] = rs.getInt("pos4");
           posA[5] = rs.getInt("pos5");
        }

        pstmt.close();

    } catch (Exception e) {

       Utilities.logError("Error in Proshop_sheet_checkin - exception getting tee time data. Exc=" + e.getMessage() );

    } finally {

         try { rs.close(); }
         catch (SQLException ignored) {}

         try { pstmt.close(); }
         catch (SQLException ignored) {}
    }
   
   
    //
    //  If Club Prophet Systems POS configured and user has not yet been prompted to skip or send charges - check for charges
    //
    if (parmp.posType.equals( "ClubProphetV3" ) && req.getParameter("CPV3skip") == null) {  // if user has NOT replied to skip POS charges - process POS checks
    
       //
       //  make sure club has been configured (see buildChargeCPS below)
       //
       if (club.equals("foresthighlands") || club.equals("cherryhills") || club.equals("ccrockies") || club.equals("sauconvalleycc") ||
           club.equals("edisonclub") || club.startsWith("demo") || club.equals("dallasathleticclub") || club.equals("oldoaks") ||
           club.equals("coloradospringscountryclub") || club.equals("pwgolf") || club.equals("westongolfclub") || club.equals("cranecreek") ||
           club.equals("championsrun") || club.equals("hillwoodcc") || club.equals("sugarvalleygc") || club.equals("bracketts") || 
           club.equals("olympiafieldscc") || club.equals("foxdencountryclub") || club.equals("marshfieldcc") || club.equals("ledgemontcc") || 
           club.equals("fairlawncc") || club.equals("tedescocc")) {

          //
          //  See if we should prompt user for further instructions
          //
          checkPOS = true;                    // default to check for POS charges

          if (playerNum > 0) {               // if individual player selected

             if (showA[playerNum] == 2 || showA[playerNum] == 1) {     // if this is a pre-checkin OR player already checked in

                checkPOS = false;                                      // skip check for POS charges
             }

          } else {               // call was for Check All in this tee time - see if already checked in

             checkPOS = false;                // default = skip check for POS charges

             if ((!playerA[1].equalsIgnoreCase( "x" ) && !playerA[1].equals( "" ) && showA[1] != 1) ||
                 (!playerA[2].equalsIgnoreCase( "x" ) && !playerA[2].equals( "" ) && showA[2] != 1) ||
                 (!playerA[3].equalsIgnoreCase( "x" ) && !playerA[3].equals( "" ) && showA[3] != 1) ||
                 (!playerA[4].equalsIgnoreCase( "x" ) && !playerA[4].equals( "" ) && showA[4] != 1) ||
                 (!playerA[5].equalsIgnoreCase( "x" ) && !playerA[5].equals( "" ) && showA[5] != 1)) {

                checkPOS = true;                 // check for POS charges if one or more players not yet checked in
             }
          }

          if (checkPOS == true) {

             if (req.getParameter("CPV3continue") == null) {     // if Club Prophet POS and Continue not specified (from prompt)

                charges = checkCPV3charges(teecurr_id, playerNum, parm, parmp, club, false, con);     // are there any charges to send?

                if (charges == true) {                   // yes - prompt user to see if they want to send them

                   promptCPV3user(teecurr_id, playerNum, course, refImage, out);       // go prompt user to continue
                   return;                              // exit and wait for response (do charges or just check-in/out)
                }

             } else {

                //
                //  User has elected to send charges - send them now!
                //
                charges = checkCPV3charges(teecurr_id, playerNum, parm, parmp, club, true, con);     // send the charges

             }      // end of IF 'continue'

          }         // end of IF checkPOS

       } else {             // club not yet configured

          out.println("<html><body>");
          out.println("<script type=\"text/javascript\">");
          out.println("alert('NOTICE:  Your club has not been configured to use this interface.\\n\\nPlease contact ForeTees Pro Support for assistance.');");
          out.println("</script>");
          out.println("</body></html>");
       }

       
    } else if (parmp.posType.equals( "Pro-ShopKeeper" ) && skipPos == false) {     //  NOT new PSK, is it the Old PSK ?
       
       //
       //  Old PSK Interface - similar to above, but sends a status to local computer
       //
       checkPOS = true;                    // default to check for POS charges

       if (playerNum > 0) {               // if individual player selected

          if (showA[playerNum] == 1 || showA[playerNum] == 2) {     // if this is a pre-checkin OR player already checked in

             checkPOS = false;                                      // skip check for POS charges
          }

       } else {               // call was for Check All in this tee time - see if already checked in

          checkPOS = false;                // default = skip check for POS charges

          if ((!playerA[1].equalsIgnoreCase( "x" ) && !playerA[1].equals( "" ) && showA[1] != 1) ||
              (!playerA[2].equalsIgnoreCase( "x" ) && !playerA[2].equals( "" ) && showA[2] != 1) ||
              (!playerA[3].equalsIgnoreCase( "x" ) && !playerA[3].equals( "" ) && showA[3] != 1) ||
              (!playerA[4].equalsIgnoreCase( "x" ) && !playerA[4].equals( "" ) && showA[4] != 1) ||
              (!playerA[5].equalsIgnoreCase( "x" ) && !playerA[5].equals( "" ) && showA[5] != 1)) {

             checkPOS = true;                 // check for POS charges if one or more players not yet checked in
          }
       }

       if (checkPOS == true) {          

         //
         //  If POS must be updated then we must first prompt the user for confirmation
         //
         if (req.getParameter("POScontinue") == null) {     // if we haven't prompted the user yet

            charges = promptPOS(playerNum, teecurr_id, refImage, parmp, parm, out, con);   // go prompt user

            if (charges == true) {              // were there any charges to process?
               return;                          // Yes - exit and wait for reply (sends POScontinue parm)
            }
            
         } else {         // the user was prompted (this is a continue) - now check if charges were sent
            
            if (req.getParameter("POSskip") == null) {     // if charges were sent (skip NOT passed)
               
               //  Check to see if charges sent for each player

               if (req.getParameter("POSsent1") != null) {     // if charges were sent for this player
                  
                  parmp.posSent1 = true;
               }
               if (req.getParameter("POSsent2") != null) {   
                  
                  parmp.posSent2 = true;
               }
               if (req.getParameter("POSsent3") != null) {   
                  
                  parmp.posSent3 = true;
               }
               if (req.getParameter("POSsent4") != null) {   
                  
                  parmp.posSent4 = true;
               }
               if (req.getParameter("POSsent5") != null) {   
                  
                  parmp.posSent5 = true;
               }
            }
         }
       }
    }            // end of POS checks


    //
    //   Process the Check In/Out request
    //
    if (playerNum > 0) {               // if individual player selected

      //
      //  Single Player - Get the correct SQL statement
      //
      // if (req.getParameter("CPV3continue") != null || (req.getParameter("POScontinue") != null && req.getParameter("POSskip") == null) ) {  // if PSK of CPV# charges sent
      if (req.getParameter("CPV3continue") != null || req.getParameter("POScontinue") != null ) {  // if PSK of CPV# charges sent

         pos_done = true;              // set pos flags too
      }
      
      switch (playerNum) {
          case 1:
              if (pos_done == true && parmp.posSent1 == true) {
                 updShow = updShow1s;
                 pos_sent = true;                 // indicate pos charges sent
              } else if (pos_done == true) {
                 updShow = updShow1c;
              } else {
                 updShow = updShow1;
              }  
              // updShow = (!pos_done) ? updShow1 : updShow1c;
              break;
          case 2:
              if (pos_done == true && parmp.posSent2 == true) {
                 updShow = updShow2s;
                 pos_sent = true;                 // indicate pos charges sent
              } else if (pos_done == true) {
                 updShow = updShow2c;
              } else {
                 updShow = updShow2;
              }  
              // updShow = (!pos_done) ? updShow2 : updShow2c;
              break;
          case 3:
              if (pos_done == true && parmp.posSent3 == true) {
                 updShow = updShow3s;
                 pos_sent = true;                 // indicate pos charges sent
              } else if (pos_done == true) {
                 updShow = updShow3c;
              } else {
                 updShow = updShow3;
              }  
              // updShow = (!pos_done) ? updShow3 : updShow3c;
              break;
          case 4:
              if (pos_done == true && parmp.posSent4 == true) {
                 updShow = updShow4s;
                 pos_sent = true;                 // indicate pos charges sent
              } else if (pos_done == true) {
                 updShow = updShow4c;
              } else {
                 updShow = updShow4;
              }  
              // updShow = (!pos_done) ? updShow4 : updShow4c;
              break;
          case 5:
              if (pos_done == true && parmp.posSent5 == true) {
                 updShow = updShow5s;
                 pos_sent = true;                 // indicate pos charges sent
              } else if (pos_done == true) {
                 updShow = updShow5c;
              } else {
                 updShow = updShow5;
              }  
              // updShow = (!pos_done) ? updShow5 : updShow5c;
      }

      //
      //  Determine new show value
      //
      show = showA[playerNum];        // get the current show value for the selected player
      
      posvalue = posA[playerNum];

      if (show == 0) {                // if not checked in yet

         new_show = 1;                // player now checked in

         if (pos_sent == true || posvalue == 3) {   // if we just sent charges OR charges already sent
            
            new_show_image = 3;       // player checked in and POS charges sent       
         
         } else {
            
            new_show_image = 1;       // player checked in                
         }

      } else {

         if (show == 1) {                // if player was checked in

            new_show = 0;                // player now checked out

         } else {                        // must be pre-checkin

            new_show = 0;                // player now checked out
         }

         new_show_image = 0;             // player NOT checked in       
      }

      //
      //  update teecurr to reflect the new 'show' setting for this player
      //
      try {

         pstmt = con.prepareStatement (updShow);  // use selected stmt

         pstmt.clearParameters();
         pstmt.setInt(1, new_show);
         pstmt.setInt(2, teecurr_id);
         pstmt.executeUpdate();

         pstmt.close();

       }
       catch (Exception ignore) {
       }


       //
       //  Warn the user if pos charges had been previously sent for this player
       //
       if (posvalue == 3) {         // if POS charges had previously been sent for this player

          out.println("<html><body>");
          out.println("<script type=\"text/javascript\">");
          out.println("alert('Warning:  Charges had been previously sent to the POS system for this player.\\n\\nIf changes to those charges are required, please refer to the POS system.');");
          out.println("</script>");
          out.println("</body></html>");
       }


       //
       //  the new_show_image int is now set to its new value - we'll update
       //  the image on the tee sheet accordingly
       //
       image = "/" +rev+ "/images/";
       title = "";

       switch (new_show_image) {
        case 1:
           title = "Click here to set as a no-show (blank).";       // if checked in
           image += "xbox.gif";
           break;
        case 2:
           title = "Click here to acknowledge new signup (pre-check in).";   // if pre-check in and not in yet
           image += "rmtbox.gif";
           break;
        case 3:
           title = "Click here to set as a no-show (blank).";       // if checked in and charges sent
           image += "xboxsent.gif";
           break;
        default:
           title = "Click here to check player in (x).";            // NOT checked in
           image += "mtbox.gif";
           break;
       }

           
        // to toggle the image to checked in
        out.println("<script type=\"text/javascript\">");
        out.println(" parent.document.getElementById('"+refImage+"').src=\""+image+"\";");
        out.println(" parent.document.getElementById('"+refImage+"').title=\""+title+"\";");
        out.println("</script>");
 
           
    } else {    
       
       // process Check All ****************
       
       if (req.getParameter("CPV3continue") != null || req.getParameter("POScontinue") != null) {   // if we sent charges (or tried)

          pos_done = true;           // indicate POS charges processed

       } else {

          pos_done = false;           // indicate POS charges NOT processed
       }

       //
       //  Update each player in the tee time according to current settings and actions
       //
       posSentA[1] = parmp.posSent1;     // put pos sent flags in an array for loop
       posSentA[2] = parmp.posSent2;
       posSentA[3] = parmp.posSent3;
       posSentA[4] = parmp.posSent4;
       posSentA[5] = parmp.posSent5;

       
       try {
          
          StringTokenizer tok = new StringTokenizer( refImage, "_" );  // should be 3 tokens - remove last

          if ( tok.countTokens() > 2 ) {     // enough data ?

             refImage = tok.nextToken();          // get "chkbox"
             tmp = tok.nextToken();               // get teecurrid - skip last token (0)

             refImage += "_" + tmp + "_";         // create new value
          }

          out.println("<script type=\"text/javascript\">");

          for (int i=1; i<6; i++) {            // do each player

             if (!playerA[i].equalsIgnoreCase( "x" ) && !playerA[i].equals( "" )) {   // if player exists
                
                pos_sent = false;       // init
                
                if (pos_done == true && posSentA[i] == true) {   // get appropriate sql statement
                    updShow = updShowsA[i];                      // set checked in and pos sent
                    pos_sent = true;                             // indicate pos charges sent
                } else if (pos_done == true && posA[i] != 2) {         // if pos processed and NOT pay now
                    updShow = updShowcA[i];                            // set checked in and pos processed
                } else {
                    updShow = updShowA[i];                             // set checked in only
                }  
                
                //
                //  Update player's show value (checked in)
                //
                pstmt = con.prepareStatement (updShow);  // use selected stmt

                pstmt.clearParameters();
                pstmt.setInt(1, 1);              // always 1 when check in all selected
                pstmt.setInt(2, teecurr_id);
                pstmt.executeUpdate();

                pstmt.close();

                //
                //  Now determine which image to show on the tee sheet of this player
                //
                if (pos_sent == true || posA[i] == 3) {   // if we just sent charges OR charges already sent

                   new_show_image = 3;       // player checked in and POS charges sent       

                } else {

                   new_show_image = 1;       // player checked in                
                }
                
                //
                //  the new_show_image int is now set to its new value - we'll update
                //  the image on the tee sheet accordingly
                //
                image = "/" +rev+ "/images/";
                title = "";

                switch (new_show_image) {
                    case 1:
                       title = "Click here to set as a no-show (blank).";       // if checked in
                       image += "xbox.gif";
                       break;
                    case 2:
                       title = "Click here to acknowledge new signup (pre-check in).";   // if pre-check in and not in yet
                       image += "rmtbox.gif";
                       break;
                    case 3:
                       title = "Click here to set as a no-show (blank).";       // if checked in and charges sent
                       image += "xboxsent.gif";
                       break;
                    default:
                       title = "Click here to check player in (x).";            // NOT checked in
                       image += "mtbox.gif";
                       break;
                }

                out.println(" parent.document.getElementById('"+ refImage + i +"').src=\""+image+"\";");
                out.println(" parent.document.getElementById('"+ refImage + i +"').title=\""+title+"\";");
             }
          }         // check all 5 players

          out.println("</script>");
          
       }
       catch (Exception ignore) {
       }

    }


    //
    //  Now check if there were any errors from Club Prophet when POS charges sent
    //
    if (parmp.posType.equals( "ClubProphetV3" )) {           // if Club Prophet POS
    
       if (!parmp.returnCode1.equals( "" ) || !parmp.returnCode2.equals( "" ) || !parmp.returnCode3.equals( "" ) || 
           !parmp.returnCode4.equals( "" ) || !parmp.returnCode5.equals( "" )) {


          if (parmp.returnCode1.equalsIgnoreCase( "Success" )) parmp.returnCode1 = "";       // remove good return codes

          if (parmp.returnCode2.equalsIgnoreCase( "Success" )) parmp.returnCode2 = ""; 

          if (parmp.returnCode3.equalsIgnoreCase( "Success" )) parmp.returnCode3 = ""; 

          if (parmp.returnCode4.equalsIgnoreCase( "Success" )) parmp.returnCode4 = ""; 

          if (parmp.returnCode5.equalsIgnoreCase( "Success" )) parmp.returnCode5 = ""; 

          //
          //  Check if any errors returned
          //
          if (!parmp.returnCode1.equals( "" ) || !parmp.returnCode2.equals( "" ) || !parmp.returnCode3.equals( "" ) || 
              !parmp.returnCode4.equals( "" ) || !parmp.returnCode5.equals( "" )) {

             //
             //  One or more errors - Output an alert to notify the pro      
             //
             String errorMsg = "";

             if (playerNum == 0) {             // if Check ALL

                errorMsg = "*** POS Transaction Failed ***\\n\\nCharges for one or more of the selected players failed. " +
                           " Please verify the charges on the POS System.\\n\\n" +
                           "Note that the charges sent indicators for this group may not be accurate.\\n\\n" +
                           "Error(s):\\n\\n";

                if (!parmp.returnCode1.equals( "" )) { 

                   errorMsg += " - " +parmp.returnCode1+ "\\n";
                }
                if (!parmp.returnCode2.equals( "" )) { 

                   errorMsg += "\\n - " +parmp.returnCode2+ "\\n";
                }
                if (!parmp.returnCode3.equals( "" )) { 

                   errorMsg += "\\n - " +parmp.returnCode3+ "\\n";
                }
                if (!parmp.returnCode4.equals( "" )) { 

                   errorMsg += "\\n - " +parmp.returnCode4+ "\\n";
                }
                if (!parmp.returnCode5.equals( "" )) { 

                   errorMsg += "\\n - " +parmp.returnCode5+ "\\n";
                }

             } else {

                errorMsg = "POS Transaction Failed\\n\\nCharges for the following member resulted in error when transferred to the POS system.\\n\\n" +
                            "Player - " +parmp.player+ ", Error = \\n\\n" +parmp.returnCode1;
             }

             out.println("<html><body>");
             out.println("<script type=\"text/javascript\">");
             out.println("alert('" +errorMsg+ "');");
             out.println("</script>");
             out.println("</body></html>");
             out.close();   
          }
       }
    }            // end of IF Club Prophet

 } // end of doGet routine

 
  
 
 // ********************************************************************
 //  No-Post request for tee time - toggle the nopost flags (members do or don't have to post scores for this round)
 // ********************************************************************

 private void handleNoPost(HttpServletRequest req, String club, HttpSession session, PrintWriter out, Connection con) {
        

    PreparedStatement pstmt = null;
    ResultSet rs = null;
   
    boolean show_x_image = false;
    boolean oldsheets = false;
    
    String sql = "";
    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";
        
    int nopost1 = 0;
    int nopost2 = 0;
    int nopost3 = 0;
    int nopost4 = 0;
    int nopost5 = 0;
    int tee_id = 0;
    
     
    //
    //  Make sure we receive the required parms
    //
    if (req.getParameter("tid") == null) {

       Utilities.logError("Parm error in Proshop_sheet_checkin.handleNoPost - Parm missing (tid).");
       return;
    }
    
    String refImage = req.getParameter("imgId");       // current image
    
    // get the teecurr_id
    String tmp = req.getParameter("tid");
    try {
        tee_id = Integer.parseInt(tmp);         // get teecurr_id or teepast_id
    } catch (NumberFormatException exc) {}
    
    if (req.getParameter("oldsheet") != null) {        // call for old tee sheets ?

        oldsheets = true;            // indicate call is from Proshop_oldsheets
    }

        
    //
    //   Get tee time info to process the nopost indicators
    //
    if (oldsheets == false) {
        
        sql = "SELECT username1, username2, username3, username4, username5, nopost1, nopost2, nopost3, nopost4, nopost5 " +
              "FROM teecurr2 WHERE teecurr_id = ?";    // for teecurr
        
    } else {
        
        sql = "SELECT username1, username2, username3, username4, username5, nopost1, nopost2, nopost3, nopost4, nopost5 " +
              "FROM teepast2 WHERE teepast_id = ?";    // for teepast
    }
    
    try {

      pstmt = con.prepareStatement (sql);

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, tee_id);

      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         user5 = rs.getString("username5");
         nopost1 = rs.getInt("nopost1");
         nopost2 = rs.getInt("nopost2");
         nopost3 = rs.getInt("nopost3");
         nopost4 = rs.getInt("nopost4");
         nopost5 = rs.getInt("nopost5");
      }
    
      pstmt.close();

    }
    catch (Exception e1) {

        Utilities.logError("Error in Proshop_sheet_checkin.handleNoPost - exception getting tee time info. Exc=" + e1.getMessage() );

    } finally {

         try { pstmt.close(); }
         catch (SQLException ignored) {}
    }
    

    //
    //  Determine if request is to Enable or Disable the nopost flags
    //
    //if (!nopostS1.equals("0") || !nopostS2.equals("0") || !nopostS3.equals("0") || !nopostS4.equals("0") || !nopostS5.equals("0")) {   // if any are set
    if (nopost1 > 0 || nopost2 > 0 || nopost3 > 0 || nopost4 > 0 || nopost5 > 0) {   // if any are set
 
       show_x_image = false;        //  toggle to the empty box and clear the flags
       
       nopost1 = 0;                 // clear them all
       nopost2 = 0;
       nopost3 = 0;
       nopost4 = 0;
       nopost5 = 0;
       
    } else {
  
       show_x_image = true;        //  toggle to the X box and set the flags for any members in the tee time
       
       if (!user1.equals("")) nopost1 = 1;     // set nopost if player is a member
       if (!user2.equals("")) nopost2 = 1;
       if (!user3.equals("")) nopost3 = 1;
       if (!user4.equals("")) nopost4 = 1;
       if (!user5.equals("")) nopost5 = 1;
    }
    
    //
    //  Build the SQL statement to update the tee time
    //
    if (oldsheets == false) {
        
        sql = "UPDATE teecurr2 SET nopost1 = ?, nopost2 = ?, nopost3 = ?, nopost4 = ?, nopost5 = ? WHERE teecurr_id = ?";    // for teecurr
        
    } else {
        
        sql = "UPDATE teepast2 SET nopost1 = ?, nopost2 = ?, nopost3 = ?, nopost4 = ?, nopost5 = ? WHERE teepast_id = ?";    // for teepast
    }
    
    
    //
    //  Toggle the flags in the tee time
    //
    try {

       pstmt = con.prepareStatement (sql); 

       pstmt.clearParameters();       
       pstmt.setInt(1, nopost1);     
       pstmt.setInt(2, nopost2);     
       pstmt.setInt(3, nopost3);     
       pstmt.setInt(4, nopost4);     
       pstmt.setInt(5, nopost5);     
       pstmt.setInt(6, tee_id);
       pstmt.executeUpdate();      

       pstmt.close();

    }
    catch (Exception e2) {

        Utilities.logError("Error in Proshop_sheet_checkin.handleNoPost - exception setting the nopost flags. Exc=" + e2.getMessage() );

    } finally {

         try { pstmt.close(); }
         catch (SQLException ignored) {}
    }

    
    //
    //  the new_show_image int is now set to its new value - we'll update
    //  the image on the tee sheet accordingly
    //
    String image = "/" +rev+ "/images/";
    String title = "";

    if (show_x_image) {
    
        title = "Click here to clear the No-Post flags so members must post their scores.";       // show the X box - nopost flags are now set
        image += "xbox.gif";
     
    } else {
       
        title = "Click here to mark these rounds as No Post rounds.";            // show the empty box (flags are cleared)
        image += "mtbox.gif";
    }

    // to toggle the image to checked in
    out.println("<script type=\"text/javascript\">");
    out.println(" parent.document.getElementById('"+refImage+"').src=\""+image+"\";");
    out.println(" parent.document.getElementById('"+refImage+"').title=\""+title+"\";");
    out.println("</script>");
  
 
 } // end of handleNoPost method

 
  
 
 // ********************************************************************
 //  Check-in processing for Activities
 // ********************************************************************

 private void handleActivities(HttpServletRequest req, String club, HttpSession session, PrintWriter out, Connection con) {
        

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean new_show_image = false;
    boolean doAll = (req.getParameter("all") != null);
    
    //
    //  Make sure we receive the required parms
    //
    if (req.getParameter("psid") == null) {

       Utilities.logError("Parm error in Proshop_sheet_checkin - Parm missing (psid).");
       return;
    }
    
    String refImage = req.getParameter("imgId");       // current image
    int player_slot_id = 0;
    
    // get the teecurr_id
    String tmp = req.getParameter("psid");
    try {
        player_slot_id = Integer.parseInt(tmp);
    } catch (NumberFormatException exc) {}
    
    int new_show = 1;
    int current_show = 0;

    if (doAll) {

        try {

            pstmt = con.prepareStatement ("UPDATE activity_sheets_players SET `show` = 1 WHERE activity_sheet_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, player_slot_id); // contain the slot id NOT the player slot id
            pstmt.executeUpdate();

        }  catch (Exception e) {

           Utilities.logError("Error in Proshop_sheet_checkin updating show value for activity slot. Exc=" + e.getMessage() );

        } finally {

             try { pstmt.close(); }
             catch (SQLException ignored) {}
        }

        out.println("<script type=\"text/javascript\">");

        // get the player slot ids for all player (excluding 'x') in this time slot and lets update their image
        try {

            pstmt = con.prepareStatement("" +
                    "SELECT activity_sheets_player_id " +
                    "FROM activity_sheets_players " +
                    "WHERE activity_sheet_id = ? AND player_name <> 'x'");
            pstmt.clearParameters();
            pstmt.setInt(1, player_slot_id);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                out.println(" parent.document.getElementById('chkbox_" + rs.getInt(1) + "').src=\"/" +rev+ "/images/xbox.gif\";");
                out.println(" parent.document.getElementById('chkbox_" + rs.getInt(1) + "').title=\"Click here to set as a no-show (blank).\";");

            }

        } catch (Exception e) {

           Utilities.logError("Error in Proshop_sheet_checkin - exception getting activity player data. Exc=" + e.getMessage() );

        } finally {

             try { rs.close(); }
             catch (SQLException ignored) {}

             try { pstmt.close(); }
             catch (SQLException ignored) {}
        }

        out.println("</script>");
        
    } else {

        //    Get current show values for the selected tee time
        try {

            pstmt = con.prepareStatement("" +
                    "SELECT * " +
                    "FROM activity_sheets_players WHERE activity_sheets_player_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, player_slot_id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                current_show = rs.getInt("show");
            }

        } catch (Exception e) {

           Utilities.logError("Error in Proshop_sheet_checkin - exception getting activity player data. Exc=" + e.getMessage() );

        } finally {

             try { rs.close(); }
             catch (SQLException ignored) {}

             try { pstmt.close(); }
             catch (SQLException ignored) {}
        }


        // determin the new show value (no pos support for now, pre-checkin supported)
        if (current_show == 2) {
            new_show = 0;
        } else if (current_show == 1) {
            new_show = 0;
        } else if (current_show == 0) {
            new_show = 1;
        }


        try {

            pstmt = con.prepareStatement ("UPDATE activity_sheets_players SET `show` = ? WHERE activity_sheets_player_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, new_show);
            pstmt.setInt(2, player_slot_id);
            pstmt.executeUpdate();

        }  catch (Exception e) {

           Utilities.logError("Error in Proshop_sheet_checkin updating show value for activity player slot. Exc=" + e.getMessage() );

        } finally {

             try { pstmt.close(); }
             catch (SQLException ignored) {}
        }

        //out.println("<script>alert('Updated player slot #" + player_slot_id + "');</script>");

        //
        //  the new_show_image int is now set to its new value - we'll update
        //  the image on the tee sheet accordingly
        //
        String image = "/" +rev+ "/images/";
        String title = "";

        switch (new_show) {
            case 1:
               title = "Click here to set as a no-show (blank).";       // if checked in
               image += "xbox.gif";
               break;
            case 2:
               title = "Click here to acknowledge new signup (pre-check in).";   // if pre-check in and not in yet
               image += "rmtbox.gif";
               break;
            case 3:
               title = "Click here to set as a no-show (blank).";       // if checked in and charges sent
               image += "xboxsent.gif";
               break;
            default:
               title = "Click here to check player in (x).";            // NOT checked in
               image += "mtbox.gif";
               break;
        }

        out.println("<script type=\"text/javascript\">");
        out.println(" parent.document.getElementById('"+ refImage +"').src=\""+image+"\";");
        out.println(" parent.document.getElementById('"+ refImage +"').title=\""+title+"\";");
        out.println("</script>");
    
    } // end if do all or single player
    
 }    // end of Activity processing
 
  
 
 // ********************************************************************
 //  Process the Pay Now request - toggle the image
 // ********************************************************************

 public void doPayNow(String club, String course, String refImage, int teecurr_id, int playerNum, PrintWriter out, Connection con) { 
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;


    int [] posA = new int [6];       // array to hold the pos values for selected tee time (at indexes 1 - 5)
    
    //    Get current pos (paynow) values for the selected tee time
    try {

        pstmt = con.prepareStatement("" +
                "SELECT pos1, pos2, pos3, pos4, pos5 " +
                "FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, teecurr_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

           posA[1] = rs.getInt("pos1");
           posA[2] = rs.getInt("pos2");
           posA[3] = rs.getInt("pos3");
           posA[4] = rs.getInt("pos4");
           posA[5] = rs.getInt("pos5");
        }

        pstmt.close();

    } catch (Exception e) {

       Utilities.logError("Error in Proshop_sheet_checkin.doPayNow - exception getting tee time data. Exc=" + e.getMessage() );

    } finally {

         try { rs.close(); }
         catch (SQLException ignored) {}

         try { pstmt.close(); }
         catch (SQLException ignored) {}
    }
   
   
    //
    //  Determine new pos value (0 = not paid, pos charges not sent;  1 = pos charges sent;  2 = Paid at Counter)
    //
    int new_pos = 0;
 
    if (posA[playerNum] == 0 || posA[playerNum] == 2) {    // if POS charges have not been sent yet

       if (posA[playerNum] == 0) {    // if not paid

          new_pos = 2;                // player now paid

       } else {

          new_pos = 0;                // player NOT paid
       }

      //
      //  get statement based on player number requested
      //
      String pospayStmt = "";

      switch (playerNum) {
          case 1:
              pospayStmt = "UPDATE teecurr2 SET pos1 = ? WHERE teecurr_id = ?";
              break;
          case 2:
              pospayStmt = "UPDATE teecurr2 SET pos2 = ? WHERE teecurr_id = ?";
              break;
          case 3:
              pospayStmt = "UPDATE teecurr2 SET pos3 = ? WHERE teecurr_id = ?";
              break;
          case 4:
              pospayStmt = "UPDATE teecurr2 SET pos4 = ? WHERE teecurr_id = ?";
              break;
          case 5:
              pospayStmt = "UPDATE teecurr2 SET pos5 = ? WHERE teecurr_id = ?";
      }

       //
       //  update teecurr to reflect the new 'pay now' setting for this player
       //
       try {

          pstmt = con.prepareStatement (pospayStmt);  // use selected stmt

          pstmt.clearParameters();       
          pstmt.setInt(1, new_pos);     
          pstmt.setInt(2, teecurr_id);
          pstmt.executeUpdate();      

          pstmt.close();

       }
       catch (Exception e2) {

           Utilities.logError("Error in Proshop_sheet_checkin.doPayNow - exception setting the pos flag. Exc=" + e2.getMessage() );
          
       } finally {

            try { rs.close(); }
            catch (SQLException ignored) {}

            try { pstmt.close(); }
            catch (SQLException ignored) {}
       }

      
       //
       //  the new_pos int is now set to its new value - we'll update
       //  the image on the tee sheet accordingly
       //
       String image = "/" +rev+ "/images/";
       String title = "";

       if (new_pos == 0) {
      
           title = "Click here to mark player as Paid.";
           image += "pospaynow.gif";
         
       } else {
          
           title = "Click here to mark player as NOT Paid.";
           image += "pospaid.gif";
       }

        // to toggle the image
        out.println("<script type=\"text/javascript\">");
        out.println(" parent.document.getElementById('"+refImage+"').src=\""+image+"\";");
        out.println(" parent.document.getElementById('"+refImage+"').title=\""+title+"\";");
        out.println("</script>");
     
       
    }   // end of IF pos ok to change
    
 }       // end of doPayNow
 
 
 // ********************************************************************
 //  Prompt the pro to continue to send charges to Club Prophet Systems V3
 // ********************************************************************

 public void promptCPV3user(int teecurr_id, int playerNum, String course, String refImage, PrintWriter out) {


   //
   //  Output a prompt to see how pro wants to proceed
   //
    /*
   out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
   out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
   out.println("<head>");
   out.println("<title> Confirm Modal Dialog </title>");
   
   //
   //  Use a modal to display a confirm box (scripts, etc are in v5/web utilities/confirm)
   //
   out.println("<script src=\"/" +rev+ "/web utilities/confirm/js/jquery.js\" type=\"text/javascript\"></script>");
   out.println("<script src=\"/" +rev+ "/web utilities/confirm/js/jquery.simplemodal.js\" type=\"text/javascript\"></script>");


   // Confirm JS and CSS files
   out.println("<script src=\"/" +rev+ "/web utilities/confirm/js/confirm.js\" type=\"text/javascript\"></script>");
   out.println("<link type=\"text/css\" href=\"/" +rev+ "/web utilities/confirm/css/confirm.css\" rel=\"stylesheet\" media=\"screen\" />");

      out.println("<script type=\"text/javascript\">");          // submits the form below
      out.println("function goconfirm() {");
      out.println(" var f = document.forms['confirmForm'];");
      out.println(" f.submit();");
      out.println("}");
      out.println("</script>");
   
   out.println("</head><body onLoad=goconfirm()>");      // execute the above form on load
   
   out.println("<div id=\"confirmDialog\">");
   out.println("<form name=\"confirmForm\" action=\"#\"><input type=\"hidden\" name=\"confirm\" value=\"Demo\" class=\"confirm\">");   // executes the confirm script
   out.println("</form>");                                         
   out.println("</div>");
     
   
   out.println("<div id=\"confirm\" style=\"display:none\">");
	out.println("<a href=\"#\" title=\"Close\" class=\"modalCloseX simplemodal-close\">x</a>");
	out.println("<div class=\"header\"><span>Confirm POS Charges</span></div>");
	out.println("<p class=\"message\"></p>");
	out.println("<div class=\"buttons\">");
	out.println("<div class=\"no simplemodal-close\">No</div>");         // change this later!!!!!!!
        out.println("<div class=\"yes\">Yes</div>");
   out.println("</div>");
   */
   
   
   
   
   
   out.println("<html><body>");    
   out.println("<form method=\"get\" action=\"Proshop_sheet_checkin\" name=\"frmYes\" id=\"frmYes\">");
   out.println("<input type=\"hidden\" name=\"CPV3continue\" value=\"yes\">");          // continue POS process on return
   out.println("<input type=\"hidden\" name=\"pNum\" value=\"" + playerNum + "\">");
   out.println("<input type=\"hidden\" name=\"tid\" value=\"" + teecurr_id + "\">");
   out.println("<input type=\"hidden\" name=\"imgId\" value=\"" + refImage + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
   //out.println("<input type=\"submit\" value=\"Yes - Continue\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form>");

   out.println("<form method=\"get\" action=\"Proshop_sheet_checkin\" name=\"frmNo\" id=\"frmNo\">");
   out.println("<input type=\"hidden\" name=\"CPV3skip\" value=\"yes\">");   // continue POS process on return
   out.println("<input type=\"hidden\" name=\"pNum\" value=\"" + playerNum + "\">");
   out.println("<input type=\"hidden\" name=\"tid\" value=\"" + teecurr_id + "\">");
   out.println("<input type=\"hidden\" name=\"imgId\" value=\"" + refImage + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
   //out.println("<input type=\"submit\" value=\"No - Just Process Check-in/out\" name=\"return\" ONCLICK=\"window.status='';return true;\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form>");

   out.println("<script type=\"text/javascript\">");
   out.println(" if(confirm('There are charges for the player(s) you are checking in.\\n\\nWould you like to send these charges to the Club Prophet POS system?\\n\\nSelect OK to Send Charges. \\n\\nSelect CANCEL if you DO NOT wish to send charges.')) {");
   out.println("  document.forms[0].submit();");
   out.println(" } else {");
   out.println("  document.forms[1].submit();");        // submit the above forms (Cancel or OK)
   out.println(" }");
   out.println("</script>");
   
   

   out.println("</body></html>");
   out.close();

 }                   // end of promptCPV3user



 // ***************************************************************************************
 //  Check for POS charges for this tee time or individual player (Club Prophet Systems)
 // ***************************************************************************************

 private boolean checkCPV3charges(int teecurr_id, int playerNum, parmClub parm, parmPOS parmp, String club, boolean sendCharges, Connection con) {


   ResultSet rs = null;

   String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String user6 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String p6cw = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String userg6 = "";
   String stime = "";
   String show = "";
   String sfb = "";
   String num = "";
   String j = "";

   int i = 0;
   int time = 0;
   int index = 0;
   int guest = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int p96 = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int show6 = 0;
   int pos1 = 0;
   int pos2 = 0;
   int pos3 = 0;
   int pos4 = 0;
   int pos5 = 0;
   int pos6 = 0;
   int p = 0;
   int included1 = 0;     // player included in charges (when sending multiple charges)
   int included2 = 0;
   int included3 = 0;
   int included4 = 0;
   int included5 = 0;

   short fb = 0;

   boolean charges = false;
   boolean newCharges = false;
   boolean failed = false;
   boolean sendNow = false;


   //
   //  Determine if there will be any charges for this request
   //
   try {

      PreparedStatement pstmt2s = con.prepareStatement (
         "SELECT * " +
         "FROM teecurr2 WHERE teecurr_id = ?");

      pstmt2s.clearParameters();        // clear the parms
      pstmt2s.setInt(1, teecurr_id);

      rs = pstmt2s.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         p1cw = rs.getString("p1cw");
         p2cw = rs.getString("p2cw");
         p3cw = rs.getString("p3cw");
         p4cw = rs.getString("p4cw");
         show1 = rs.getInt("show1");
         show2 = rs.getInt("show2");
         show3 = rs.getInt("show3");
         show4 = rs.getInt("show4");
         player5 = rs.getString("player5");
         user5 = rs.getString("username5");
         p5cw = rs.getString("p5cw");
         show5 = rs.getInt("show5");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");
         p91 = rs.getInt("p91");
         p92 = rs.getInt("p92");
         p93 = rs.getInt("p93");
         p94 = rs.getInt("p94");
         p95 = rs.getInt("p95");
         pos1 = rs.getInt("pos1");
         pos2 = rs.getInt("pos2");
         pos3 = rs.getInt("pos3");
         pos4 = rs.getInt("pos4");
         pos5 = rs.getInt("pos5");
      }
      pstmt2s.close();
      
   }
   catch (Exception ignore) {
   }

   parmp.player1 = player1;        // save players in case we need them (Club Prophet)
   parmp.player2 = player2;
   parmp.player3 = player3;
   parmp.player4 = player4;
   parmp.player5 = player5;
   
   
   if (playerNum == 0 && sendCharges == true) {   // if we are sending charges and it is for ALL players in the tee time

      //
      //  get each member's name and mship info
      //
      parmp.posid1 = "";      // init them all
      parmp.posid2 = "";
      parmp.posid3 = "";
      parmp.posid4 = "";
      parmp.posid5 = "";
      parmp.mship1 = "";
      parmp.mship2 = "";
      parmp.mship3 = "";
      parmp.mship4 = "";
      parmp.mship5 = "";
      parmp.lname1 = "";
      parmp.lname2 = "";
      parmp.lname3 = "";
      parmp.lname4 = "";
      parmp.lname5 = "";
      parmp.fname1 = "";
      parmp.fname2 = "";
      parmp.fname3 = "";
      parmp.fname4 = "";
      parmp.fname5 = "";
      parmp.item = "";          // normally used for Jonas POS, but we will use it here for a custom (CC of Rockies)
         
      //
      //  Get Player1's info
      //
      parmp.user = "";
      
      if (!user1.equals( "" )) {            // if member

         parmp.user = user1;

      } else {                           

         if (!userg1.equals( "" )) {       // if member associated with this guest

            parmp.user = userg1;
         }
      }
      
      if (!parmp.user.equals("")) {       // if member found
         
         getMemInfo(parmp, con);          // get this member's pos info
         
         parmp.posid1 = parmp.posid;
         parmp.mship1 = parmp.mship0;
         parmp.lname1 = parmp.lname;
         parmp.fname1 = parmp.fname;     
      }
  
      //
      //  Get Player2's info
      //
      if (!user2.equals( "" )) {            // if member

         parmp.user = user2;

      } else {        
         
         if (!userg2.equals( "" )) {       // if member associated with this guest

            if (userg2.equals( parmp.user )) {
               
               parmp.user = "";            // no need to get member info
               
               parmp.posid2 = parmp.posid;   // use the last member info
               parmp.mship2 = parmp.mship0;
               parmp.lname2 = parmp.lname;
               parmp.fname2 = parmp.fname;     
         
            } else {
               
               parmp.user = userg2;        // new member associated with this guest
            }
            
         } else {
            
            parmp.user = "";               // no one here
         }
      }
      
      if (!parmp.user.equals("")) {       // if member found
         
         getMemInfo(parmp, con);          // get this member's pos info
         
         parmp.posid2 = parmp.posid;
         parmp.mship2 = parmp.mship0;
         parmp.lname2 = parmp.lname;
         parmp.fname2 = parmp.fname;     
      }
  
      //
      //  Get Player3's info
      //
      if (!user3.equals( "" )) {            // if member

         parmp.user = user3;

      } else {        
         
         if (!userg3.equals( "" )) {       // if member associated with this guest

            if (userg3.equals( parmp.user )) {
               
               parmp.user = "";            // no need to get member info
               
               parmp.posid3 = parmp.posid;   // use the last member info
               parmp.mship3 = parmp.mship0;
               parmp.lname3 = parmp.lname;
               parmp.fname3 = parmp.fname;     
         
            } else {
               
               parmp.user = userg3;        // new member associated with this guest
            }
            
         } else {
            
            parmp.user = "";               // no one here
         }
      }
      
      if (!parmp.user.equals("")) {       // if member found
         
         getMemInfo(parmp, con);          // get this member's pos info
         
         parmp.posid3 = parmp.posid;
         parmp.mship3 = parmp.mship0;
         parmp.lname3 = parmp.lname;
         parmp.fname3 = parmp.fname;     
      }
  
      //
      //  Get Player4's info
      //
      if (!user4.equals( "" )) {            // if member

         parmp.user = user4;

      } else {        
         
         if (!userg4.equals( "" )) {       // if member associated with this guest

            if (userg4.equals( parmp.user )) {
               
               parmp.user = "";            // no need to get member info
               
               parmp.posid4 = parmp.posid;   // use the last member info
               parmp.mship4 = parmp.mship0;
               parmp.lname4 = parmp.lname;
               parmp.fname4 = parmp.fname;     
         
            } else {
               
               parmp.user = userg4;        // new member associated with this guest
            }
            
         } else {
            
            parmp.user = "";               // no one here
         }
      }
      
      if (!parmp.user.equals("")) {       // if member found
         
         getMemInfo(parmp, con);          // get this member's pos info
         
         parmp.posid4 = parmp.posid;
         parmp.mship4 = parmp.mship0;
         parmp.lname4 = parmp.lname;
         parmp.fname4 = parmp.fname;     
      }
  
      //
      //  Get Player5's info
      //
      if (!user5.equals( "" )) {            // if member

         parmp.user = user5;

      } else {        
         
         if (!userg5.equals( "" )) {       // if member associated with this guest

            if (userg5.equals( parmp.user )) {
               
               parmp.user = "";            // no need to get member info
               
               parmp.posid5 = parmp.posid;   // use the last member info
               parmp.mship5 = parmp.mship0;
               parmp.lname5 = parmp.lname;
               parmp.fname5 = parmp.fname;     
         
            } else {
               
               parmp.user = userg5;        // new member associated with this guest
            }
            
         } else {
            
            parmp.user = "";               // no one here
         }
      }
      
      if (!parmp.user.equals("")) {       // if member found
         
         getMemInfo(parmp, con);          // get this member's pos info
         
         parmp.posid5 = parmp.posid;
         parmp.mship5 = parmp.mship0;
         parmp.lname5 = parmp.lname;
         parmp.fname5 = parmp.fname;     
      }      
   }        // end of IF send charges for all
   
   
   //
   //   if call was for Check All, then process all players in the specified slot
   //
   if (playerNum == 0) {

      //
      //  Process one player at a time to determine any charges
      //
      if (!player1.equalsIgnoreCase( "x" ) && !player1.equals( "" ) && pos1 == 0) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;

         if (user1.equals( "" )) {            // if no username for this player

            ploop1:
            while (i < parm.MAX_Guests) {
               if (player1.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player1 is a guest name
                  break ploop1;
               }
               i++;
            }
         }
         parmp.pcw = p1cw;
         parmp.p9 = p91;

         if (guest == 0) {        // if member

            if (!user1.equals( "" )) {      // skip if no user name found or already processed

               parmp.player = "";   // indicate member
               parmp.user = user1;

               if (sendCharges == true) {             // send charges?

                  parmp.posid = parmp.posid1;
                  parmp.mship0 = parmp.mship1;
                  parmp.lname = parmp.lname1;
                  parmp.fname = parmp.fname1;  
         
                  charges = buildChargeCPS(parmp, con);         // yes - go build charges
                  
               } else {       // just checking for charges - do not send yet

                  charges = checkCharge(parmp, con);  
               }
            }

         } else {          // else guest

            if (!userg1.equals( "" )) {      // skip if no member associated with this guest

               parmp.player = player1;       // indicate guest - pass the guest type
               parmp.user = userg1;

               if (sendCharges == true) {             // send charges?

                  parmp.posid = parmp.posid1;
                  parmp.mship0 = parmp.mship1;
                  parmp.lname = parmp.lname1;
                  parmp.fname = parmp.fname1;  
         
                  charges = buildChargeCPS(parmp, con);         // yes - go build 

               } else {          // just checking for charges - do not send yet

                  charges = checkCharge(parmp, con);  
               }
            }
         }   // end of IF member or guest
         
         //
         //  Send any charges if we are not just checking and done with this member
         //
         if (sendCharges == true) {             // send charges?

            if (charges == true) {              // if any charges were built

               newCharges = true;               // indicate new charges waiting to be sent
               //parmp.posSent1 = true;           // indicate charges built/sent for this player - moved to below to prevent premature setting
               included1 = 1;                   // indicate this player included in charges to be sent

               if (parmp.club.equals("ccrockies")) {     

                  parmp.item = player1;         // save player name to send to CPS
               }
            }

            charges = false;                   // reset so we check next player

            if (newCharges == true) {          // if charge built - check if we should send now or if more to add for this member

               sendNow = false;                // init send flag to NOT send now

               if (!parmp.posid1.equals(parmp.posid2)) {  // if next player is not the same id

                  sendNow = true;                         // then send charges now

               } else if (pos2 == 2) {                    // next player has same posid - check if paid at counter

                  if (!parmp.posid1.equals(parmp.posid3)) {  // yes - if next player is not the same id

                     sendNow = true;                         // then send charges now

                  } else if (pos3 == 2) {                    // next player has same posid - check if paid at counter

                     if (!parmp.posid1.equals(parmp.posid4)) {  // yes - if next player is not the same id

                        sendNow = true;                         // then send charges now

                     } else if (pos4 == 2) {                    // next player has same posid - check if paid at counter

                        if (!parmp.posid1.equals(parmp.posid5) || pos5 == 2) {  // yes - if last player is different or already paid

                           sendNow = true;                         // then send charges now
                        }
                     }
                  }                        
               }

               if (sendNow == true) {                    
                  
                  parmp.posSent1 = true;           // indicate charges built/sent for this player
                  
                  //
                  //  Set the pos flag to 'processed' to prevent charges for this player being sent again - in case CPS fails to respond to one of the following sends
                  //
                  setPOSprocessed(included1, included2, included3, included4, included5, teecurr_id, con);  
                  
                  included1 = 0;       // indicate no longer included in charge string

                  failed = sendChargeCPS(parmp, con);              // go send the charges now

                  parmp.codeA = new org.tempuri.ArrayOfString();      // init POS arrays
                  parmp.qtyA = new org.tempuri.ArrayOfInt();

                  newCharges = false;                     // init charges built indicator (ONLY reset IF SENT!!!)

                  if (failed == true) {       // if any charges failed

                     parmp.posSent1 = false;   // indicate charges NOT sent for this player
                  }    
               }
            }            
         }       // end of IF sending charges
         
      }      // end of IF player not X and not null

      
      //
      //    Check Next Player
      //
      if (!player2.equalsIgnoreCase( "x" ) && !player2.equals( "" ) && pos2 == 0 && charges == false) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;

         if (user2.equals( "" )) {            // if no username for this player

            ploop2:
            while (i < parm.MAX_Guests) {
               if (player2.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player2 is a guest name
                  break ploop2;
               }
               i++;
            }
         }
         parmp.pcw = p2cw;
         parmp.p9 = p92;

         if (guest == 0) {        // if member

            if (!user2.equals( "" )) {      // skip if no user name found

               parmp.player = "";   // indicate member
               parmp.user = user2;

               if (sendCharges == true) {             // send charges?

                  parmp.posid = parmp.posid2;
                  parmp.mship0 = parmp.mship2;
                  parmp.lname = parmp.lname2;
                  parmp.fname = parmp.fname2;  
         
                  charges = buildChargeCPS(parmp, con);         // yes - go build 

               } else {

                  charges = checkCharge(parmp, con);   // no - just check for them
               }
            }

         } else {          // else guest

            if (!userg2.equals( "" )) {      // skip if no member associated with this guest

               parmp.player = player2;   // indicate guest - pass the guest type
               parmp.user = userg2;

               if (sendCharges == true) {             // send charges?

                  charges = buildChargeCPS(parmp, con);         // yes - go build 

               } else {

                  charges = checkCharge(parmp, con);   // no - just check for them
               }
            }
         }   // end of IF member or guest
         
         //
         //  Send any charges if we are not just checking and done with this member
         //
         if (sendCharges == true) {             // send charges?

            if (charges == true) {      // if any charges were built

               newCharges = true;       // indicate new charges waiting to be sent
               //parmp.posSent2 = true;   // indicate charges built/sent for this player     - moved to below to prevent premature setting
               included2 = 1;                   // indicate this player included in charges to be sent

               if (parmp.club.equals("ccrockies")) {     

                  if (parmp.item.equals("")) {
                     parmp.item = player2;       // save player name to send to CPS
                  } else {
                     parmp.item = parmp.item + ", " + player2;       // add to previous player
                  }
               }
            }

            charges = false;            // reset so we check next player

            if (newCharges == true) {   // if charge built - check if we should send now or if more to add for this member

               sendNow = false;            // init send flag to NOT send now

               if (!parmp.posid2.equals(parmp.posid3)) {  // yes - if next player is not the same id

                  sendNow = true;                         // then send charges now

               } else if (pos3 == 2) {                    // next player has same posid - check if paid at counter

                  if (!parmp.posid2.equals(parmp.posid4)) {  // yes - if next player is not the same id

                     sendNow = true;                         // then send charges now

                  } else if (pos4 == 2) {                    // next player has same posid - check if paid at counter

                     if (!parmp.posid2.equals(parmp.posid5) || pos5 == 2) {  // yes - if last player is different or already paid

                        sendNow = true;                         // then send charges now
                     }
                  }
               }

               if (sendNow == true) {     
                   
                  parmp.posSent2 = true;   // indicate charges built/sent for this player
                  
                  if (included1 > 0) parmp.posSent1 = true;     // flag that as sent too
                  
                  //
                  //  Set the pos flag to 'processed' to prevent charges for this player being sent again - in case CPS fails to respond to one of the following sends
                  //
                  setPOSprocessed(included1, included2, included3, included4, included5, teecurr_id, con);  

                  failed = sendChargeCPS(parmp, con);     // go send the charges now

                  parmp.codeA = new org.tempuri.ArrayOfString();      // init POS arrays
                  parmp.qtyA = new org.tempuri.ArrayOfInt();

                  newCharges = false;                     // init charges built indicator (ONLY reset IF SENT!!!)

                  if (failed == true) {                   // if any charges failed

                     parmp.posSent2 = false;              // indicate charges NOT sent for this player

                     if (included1 > 0) parmp.posSent1 = false;     // flag that as NOT sent too
                  }    
                  
                  included1 = 0;       // indicate no longer included in charge string
                  included2 = 0;
               }
            }
         }       // end of IF sending charges
         
      }      // end of IF player not X and not null

      
      //
      //    Check Next Player
      //
      if (!player3.equalsIgnoreCase( "x" ) && !player3.equals( "" ) && pos3 == 0 && charges == false) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;

         if (user3.equals( "" )) {            // if no username for this player

            ploop3:
            while (i < parm.MAX_Guests) {
               if (player3.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player3 is a guest name
                  break ploop3;
               }
               i++;
            }
         }
         parmp.pcw = p3cw;
         parmp.p9 = p93;

         if (guest == 0) {        // if member

            if (!user3.equals( "" )) {      // skip if no user name found

               parmp.player = "";   // indicate member
               parmp.user = user3;

               if (sendCharges == true) {             // send charges?

                  parmp.posid = parmp.posid3;
                  parmp.mship0 = parmp.mship3;
                  parmp.lname = parmp.lname3;
                  parmp.fname = parmp.fname3;  
         
                  charges = buildChargeCPS(parmp, con);         // yes - go build and send

               } else {

                  charges = checkCharge(parmp, con);   // no - just check for them
               }
            }

         } else {          // else guest

            if (!userg3.equals( "" )) {      // skip if no member associated with this guest

               parmp.player = player3;   // indicate guest - pass the guest type
               parmp.user = userg3;

               if (sendCharges == true) {             // send charges?

                  charges = buildChargeCPS(parmp, con);         // yes - go build and send

               } else {

                  charges = checkCharge(parmp, con);   // no - just check for them
               }
            }
         }   // end of IF member or guest
         
         //
         //  Send any charges if we are not just checking and done with this member
         //
         if (sendCharges == true) {             // send charges?

            if (charges == true) {      // if any charges were built

               newCharges = true;       // indicate new charges waiting to be sent
               //parmp.posSent3 = true;   // indicate charges built/sent for this player    - moved to below
               included3 = 1;                   // indicate this player included in charges to be sent

               if (parmp.club.equals("ccrockies")) {     

                  if (parmp.item.equals("")) {
                     parmp.item = player3;       // save player name to send to CPS
                  } else {
                     parmp.item = parmp.item + ", " + player3;       // add to previous player
                  }
               }
            }

            charges = false;            // reset so we check next player

            if (newCharges == true) {   // if charge built - check if we should send now or if more to add for this member

               sendNow = false;            // init send flag to NOT send now

               if (!parmp.posid3.equals(parmp.posid4)) {  // yes - if next player is not the same id

                  sendNow = true;                         // then send charges now

               } else if (pos4 == 2) {                    // next player has same posid - check if paid at counter

                  if (!parmp.posid3.equals(parmp.posid5) || pos5 == 2) {  // yes - if last player is different or already paid

                     sendNow = true;                         // then send charges now
                  }
               }

               if (sendNow == true) {                    
                  
                  parmp.posSent3 = true;   // indicate charges built/sent for this player
                  
                  if (included1 > 0) parmp.posSent1 = true;     // flag that as sent too
                  if (included2 > 0) parmp.posSent2 = true;     // flag that as sent too
                  
                  //
                  //  Set the pos flag to 'processed' to prevent charges for this player being sent again - in case CPS fails to respond to one of the following sends
                  //
                  setPOSprocessed(included1, included2, included3, included4, included5, teecurr_id, con);  

                  failed = sendChargeCPS(parmp, con);     // go send the charges now

                  parmp.codeA = new org.tempuri.ArrayOfString();      // init POS arrays
                  parmp.qtyA = new org.tempuri.ArrayOfInt();

                  newCharges = false;                     // init charges built indicator (ONLY reset IF SENT!!!)

                  if (failed == true) {        // if any charges failed

                     parmp.posSent3 = false;   // indicate charges NOT sent for this player

                     if (included1 > 0) parmp.posSent1 = false;     // flag that as NOT sent too
                     if (included2 > 0) parmp.posSent2 = false;     // flag that as NOT sent too
                  }    
                  
                  included1 = 0;       // indicate no longer included in charge string
                  included2 = 0;
                  included3 = 0;
               }
            }
         }       // end of IF sending charges
         
      }      // end of IF player not X and not null

      
      //
      //    Check Next Player
      //
      if (!player4.equalsIgnoreCase( "x" ) && !player4.equals( "" ) && pos4 == 0 && charges == false) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;

         if (user4.equals( "" )) {            // if no username for this player

            ploop4:
            while (i < parm.MAX_Guests) {
               if (player4.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player4 is a guest name
                  break ploop4;
               }
               i++;
            }
         }
         parmp.pcw = p4cw;
         parmp.p9 = p94;

         if (guest == 0) {        // if member

            if (!user4.equals( "" )) {      // skip if no user name found

               parmp.player = "";           // indicate member
               parmp.user = user4;

               if (sendCharges == true) {             // send charges?

                  parmp.posid = parmp.posid4;
                  parmp.mship0 = parmp.mship4;
                  parmp.lname = parmp.lname4;
                  parmp.fname = parmp.fname4;  
         
                  charges = buildChargeCPS(parmp, con);         // yes - go build and send

               } else {

                  charges = checkCharge(parmp, con);   // no - just check for them
               }
            }

         } else {          // else guest

            if (!userg4.equals( "" )) {      // skip if no member associated with this guest

               parmp.player = player4;   // indicate guest - pass the guest type
               parmp.user = userg4;

               if (sendCharges == true) {             // send charges?

                  charges = buildChargeCPS(parmp, con);         // yes - go build and send

               } else {

                  charges = checkCharge(parmp, con);   // no - just check for them
               }
            }
         }   // end of IF member or guest
         
         //
         //  Send any charges if we are not just checking and done with this member
         //
         if (sendCharges == true) {             // send charges?

            if (charges == true) {      // if any charges were built

               newCharges = true;       // indicate new charges waiting to be sent
               //parmp.posSent4 = true;   // indicate charges built/sent for this player   - moved to below
               included4 = 1;                   // indicate this player included in charges to be sent

               if (parmp.club.equals("ccrockies")) {     

                  if (parmp.item.equals("")) {
                     parmp.item = player4;       // save player name to send to CPS
                  } else {
                     parmp.item = parmp.item + ", " + player4;       // add to previous player
                  }
               }
            }

            charges = false;            // reset so we check next player

            if (newCharges == true) {   // if charge built - check if we should send now or if more to add for this member

               sendNow = false;            // init send flag to NOT send now

               if (!parmp.posid4.equals(parmp.posid5) || pos5 == 2) {  // yes - if last player is different or already paid

                  sendNow = true;                         // then send charges now
               }

               if (sendNow == true) {                    
                  
                  parmp.posSent4 = true;   // indicate charges built/sent for this player
                  
                  if (included1 > 0) parmp.posSent1 = true;     // flag that as sent too
                  if (included2 > 0) parmp.posSent2 = true;     // flag that as sent too
                  if (included3 > 0) parmp.posSent3 = true;     // flag that as sent too
                  
                  //
                  //  Set the pos flag to 'processed' to prevent charges for this player being sent again - in case CPS fails to respond to one of the following sends
                  //
                  setPOSprocessed(included1, included2, included3, included4, included5, teecurr_id, con);  

                  failed = sendChargeCPS(parmp, con);     // go send the charges now

                  parmp.codeA = new org.tempuri.ArrayOfString();      // init POS arrays
                  parmp.qtyA = new org.tempuri.ArrayOfInt();

                  newCharges = false;                     // init charges built indicator (ONLY reset IF SENT!!!)

                  if (failed == true) {        // if any charges failed

                     parmp.posSent4 = false;   // indicate charges NOT sent for this player

                     if (included1 > 0) parmp.posSent1 = false;     // flag that as NOT sent too
                     if (included2 > 0) parmp.posSent2 = false;     // flag that as NOT sent too
                     if (included3 > 0) parmp.posSent3 = false;     // flag that as NOT sent too
                  }    
                  
                  included1 = 0;       // indicate no longer included in charge string
                  included2 = 0;
                  included3 = 0;
                  included4 = 0;
               }
            }
         }       // end of IF sending charges
         
      }      // end of IF player not X and not null

      
      //
      //    Check Last Player
      //
      if (!player5.equalsIgnoreCase( "x" ) && !player5.equals( "" ) && pos5 == 0 && charges == false) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;

         if (user5.equals( "" )) {            // if no username for this player

            ploop5:
            while (i < parm.MAX_Guests) {
               if (player5.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player5 is a guest name
                  break ploop5;
               }
               i++;
            }
         }
         parmp.pcw = p5cw;
         parmp.p9 = p95;

         if (guest == 0) {        // if member

            if (!user5.equals( "" )) {      // skip if no user name found

               parmp.player = "";   // indicate member
               parmp.user = user5;

               if (sendCharges == true) {             // send charges?

                  parmp.posid = parmp.posid5;
                  parmp.mship0 = parmp.mship5;
                  parmp.lname = parmp.lname5;
                  parmp.fname = parmp.fname5;  
         
                  charges = buildChargeCPS(parmp, con);         // yes - go build and send

               } else {

                  charges = checkCharge(parmp, con);   // no - just check for them
               }
            }

         } else {          // else guest

            if (!userg5.equals( "" )) {      // skip if no member associated with this guest

               parmp.player = player5;   // indicate guest - pass the guest type
               parmp.user = userg5;

               if (sendCharges == true) {             // send charges?

                  charges = buildChargeCPS(parmp, con);         // yes - go build and send

               } else {

                  charges = checkCharge(parmp, con);   // no - just check for them
               }
            }
         }   // end of IF member or guest
         
         //
         //  Send any charges if we are not just checking and done with this member
         //
         if (sendCharges == true) {             // send charges?

            if (charges == true) {      // if any charges were built

               newCharges = true;       // indicate new charges waiting to be sent
               //parmp.posSent5 = true;   // indicate charges built/sent for this player   - moved to below
               included5 = 1;                   // indicate this player included in charges to be sent

               if (parmp.club.equals("ccrockies")) {     

                  if (parmp.item.equals("")) {
                     parmp.item = player5;       // save player name to send to CPS
                  } else {
                     parmp.item = parmp.item + ", " + player5;       // add to previous player
                  }
               }
            }

            charges = false;            // reset so we check next player

            if (newCharges == true) {   // if charge built - check if we should send now or if more to add for this member
                  
               parmp.posSent5 = true;   // indicate charges built/sent for this player

               if (included1 > 0) parmp.posSent1 = true;     // flag that as sent too
               if (included2 > 0) parmp.posSent2 = true;     // flag that as sent too
               if (included3 > 0) parmp.posSent3 = true;     // flag that as sent too
               if (included4 > 0) parmp.posSent4 = true;     // flag that as sent too
                  
               //
               //  Set the pos flag to 'processed' to prevent charges for this player being sent again 
               //
               setPOSprocessed(included1, included2, included3, included4, included5, teecurr_id, con);  

               failed = sendChargeCPS(parmp, con);     // go send the charges now

               parmp.codeA = new org.tempuri.ArrayOfString();      // init POS arrays
               parmp.qtyA = new org.tempuri.ArrayOfInt();

               if (failed == true) {        // if any charges failed

                  parmp.posSent5 = false;   // indicate charges NOT sent for this player

                  if (included1 > 0) parmp.posSent1 = false;     // flag that as NOT sent too
                  if (included2 > 0) parmp.posSent2 = false;     // flag that as NOT sent too
                  if (included3 > 0) parmp.posSent3 = false;     // flag that as NOT sent too
                  if (included4 > 0) parmp.posSent4 = false;     // flag that as NOT sent too
               }    
            }
         }       // end of IF sending charges
         
      }      // end of IF player not X and not null
      

   } else {        // this was an individual check-in/out

      included1 = 0;     // init
      included2 = 0;
      included3 = 0;
      included4 = 0;
      included5 = 0;
      
      //
      //  Determine which player is to be processed
      //
      if (playerNum == 1) {              // get player based on player #
         player6 = player1;
         user6 = user1;
         userg6 = userg1;
         p6cw = p1cw;
         p96 = p91;
         pos6 = pos1;
         show6 = show1;
         included1 = 1;       // this player checked
      }
      if (playerNum == 2) {
         player6 = player2;
         user6 = user2;
         userg6 = userg2;
         p6cw = p2cw;
         p96 = p92;
         pos6 = pos2;
         show6 = show2;
         included2 = 1;       // this player checked
      }
      if (playerNum == 3) {
         player6 = player3;
         user6 = user3;
         userg6 = userg3;
         p6cw = p3cw;
         p96 = p93;
         pos6 = pos3;
         show6 = show3;
         included3 = 1;       // this player checked
      }
      if (playerNum == 4) {
         player6 = player4;
         user6 = user4;
         userg6 = userg4;
         p6cw = p4cw;
         p96 = p94;
         pos6 = pos4;
         show6 = show4;
         included4 = 1;       // this player checked
      }
      if (playerNum == 5) {
         player6 = player5;
         user6 = user5;
         userg6 = userg5;
         p6cw = p5cw;
         p96 = p95;
         pos6 = pos5;
         show6 = show5;
         included5 = 1;       // this player checked
      }

      //
      //  Make sure we should process the charge/return.
      //  User may have done a check-in/out without doing the charge prior to this.
      //  If so, the show & pos flags will be out of sync.  Only do charge if in sync.
      //
      if (show6 == 0 && pos6 == 0) {

         if (!player6.equalsIgnoreCase( "x" ) && !player6.equals( "" )) {

            //
            //  Check if player name is member or guest
            //
            i = 0;
            guest = 0;

            if (user6.equals( "" )) {            // if no username for this player

               ploop6:
               while (i < parm.MAX_Guests) {
                  if (player6.startsWith( parm.guest[i] )) {

                     guest = 1;       // indicate player is a guest name
                     break ploop6;
                  }
                  i++;
               }
            }
            parmp.pcw = p6cw;
            parmp.p9 = p96;

            if (guest == 0) {        // if member

               if (!user6.equals( "" )) {      // skip if no user name found

                  parmp.player = "";   // indicate member
                  parmp.user = user6;

                  if (sendCharges == true) {                  // send charges?
                     
                     getMemInfo(parmp, con);                  // yes - go get member info
                     
                     charges = buildChargeCPS(parmp, con);    // go build the charges

                     if (parmp.club.equals("ccrockies") && charges == true) {     
            
                        parmp.item = player6;       // save player name to send to CPS
                      }
                     
                  } else {

                     charges = checkCharge(parmp, con);       // no - just check for them
                  }
               }

            } else {          // else guest

               if (!userg6.equals( "" )) {      // skip if no member associated with this guest

                  parmp.player = player6;   // indicate guest - pass the guest type
                  parmp.user = userg6;

                  if (sendCharges == true) {                   // send charges?

                     getMemInfo(parmp, con);                   // yes - go get member info
                     
                     charges = buildChargeCPS(parmp, con);     // go build the charges

                     if (parmp.club.equals("ccrockies") && charges == true) {     
            
                        parmp.item = parmp.fname + " " + parmp.lname + ", " + player6;   // save member & guest name for CPS
                     }

                  } else {

                     charges = checkCharge(parmp, con);        // no - just check for them
                  }
               }
            }   // end of IF member or guest

            
            //
            //  Now send the charges if there are some to send
            //
            if (sendCharges == true && charges == true) {     // send charges and charges to send?
                  
               //
               //  Set the pos flag to 'processed' to prevent charges for this player being sent again  (do before sending in case of return delay!!)
               //
               setPOSprocessed(included1, included2, included3, included4, included5, teecurr_id, con);  

               //
               //  Send the charges
               //
               failed = sendChargeCPS(parmp, con);                     // yes - go send them
               
               
               switch (playerNum) {
                case 1:
                   if (failed == false) {
                     parmp.posSent1 = true;         // indicate charges built/sent for this player
                   } else {
                     parmp.posSent1 = false;        // or they failed        
                   }
                   break;
                case 2:
                   if (failed == false) {
                     parmp.posSent2 = true;         // indicate charges built/sent for this player
                   } else {
                     parmp.posSent2 = false;        // or they failed        
                   }
                   break;
                case 3:
                   if (failed == false) {
                     parmp.posSent3 = true;         // indicate charges built/sent for this player
                   } else {
                     parmp.posSent3 = false;        // or they failed        
                   }
                   break;
                case 4:
                   if (failed == false) {
                     parmp.posSent4 = true;         // indicate charges built/sent for this player
                   } else {
                     parmp.posSent4 = false;        // or they failed        
                   }
                   break;
                case 5:
                   if (failed == false) {
                     parmp.posSent5 = true;         // indicate charges built/sent for this player
                   } else {
                     parmp.posSent5 = false;        // or they failed        
                   }
               }
            }
            
            parmp.player = player6;                        // save player name for error msg if needed
            
         }      // end of IF player not X and not null
      }      // end of IF we should do a pos charge/return
   }

   return(charges);
 }                   // end of checkCPV3charges



 // ********************************************************************
 //  Check for POS charges for an individual member (Club Prophet Systems)
 //
 //    Check if there are any POS related charges for this player
 // ********************************************************************

 public boolean checkCharge(parmPOS parmp, Connection con) {


   ResultSet rs = null;

   String fname = "";
   String lname = "";
   String mship = "";
   String posid = "";
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";
   String returnCode = "";

   int i = 0;
   int p9c = 0;

   boolean charges = false;


   try {

      //
      //  First check if there is a charge code associated with this member's mode of trans
      //
      i = 0;
      loop1:
      while (i < parmp.MAX_Tmodes) {

         if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

            if (parmp.club.equals("olympiafieldscc")) {        // Custom charges for Olympia Fields (2 courses)
                
                tpos = "";
                
                if (parmp.pcw.equals("CRT")) {
                    
                    tpos = "CTSC004";    // use any code to set charges = true !!
                }
                
            } else {    // NOT a custom
                    
                if (parmp.p9 == 0) {                  // if 18 hole round

                   tpos = parmp.tpos[i];               // get 18 hole charge

                } else {

                   tpos = parmp.t9pos[i];              // get 9 hole charge
                }
            }
            break loop1;
         }
         i++;
      }

      if (!tpos.equals( "" )) {          // if Mode of Trans pos charge found

         charges = true;
      }                            // end of trans mode processing

      if (charges == false) {      // if no charges found yet - keep looking

         //
         //  get the member's name and mship info
         //
         PreparedStatement pstmtc = con.prepareStatement (
            "SELECT name_last, name_first, m_ship, posid FROM member2b WHERE username= ?");

         pstmtc.clearParameters();        // clear the parms
         pstmtc.setString(1, parmp.user);

         rs = pstmtc.executeQuery();

         if (rs.next()) {

            lname = rs.getString(1);
            fname = rs.getString(2);
            mship = rs.getString(3);
            posid = rs.getString(4);
         }
         pstmtc.close();

         lname = lname.replace("'", "-");    //  replace any single quotes (O'Brien = O-Brien)

         //
         //  get the mship class and charge code, if any
         //
         i = 0;
         loop2:
         while (i < parmp.MAX_Mships) {

            if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

               mpos = parmp.mpos[i];               // get mship charge class

               if (parmp.p9 == 0) {                  // if 18 hole round

                  mposc = parmp.mposc[i];             // get mship charge code

               } else {

                  mposc = parmp.m9posc[i];             // get mship charge code
               }
               break loop2;
            }
            i++;
         }

         if (!mposc.equals( "" )) {          // if pos charge found for membership (non-golf mship charge)

            charges = true;
         }                             // end of Mship Charge processing
      }


      if (charges == false) {      // if no charges found yet - keep looking

         //
         //  if the player passed is a guest, charge the member for this too
         //
         if (!parmp.player.equals( "" )) {

            //
            //  Check if there is a charge code associated with this guest type
            //
            i = 0;
            loop3:
            while (i < parmp.MAX_Guests) {

               if (parmp.player.startsWith( parmp.gtype[i] )) {

                    if (parmp.club.equals("olympiafieldscc")) {        // Custom guest charges for Olympia Fields (2 courses)

                        if (parmp.gtype[i].equals("Comp") || parmp.gtype[i].equals("PGA") || parmp.gtype[i].equals("Prospective Member") || 
                            parmp.gtype[i].equals("Regular Twlight") || parmp.gtype[i].equals("Family Twlight") || parmp.gtype[i].equals("Reg Guest") || 
                            parmp.gtype[i].equals("Fam Guest")) {

                            gpos = "NCGF005";        // use any code to set charges = true                    
                        }

                    } else {    // NOT a custom

                      if (parmp.p9 == 0) {                  // if 18 hole round

                         gpos = parmp.gpos[i];               // get 18 hole charge

                      } else {

                         gpos = parmp.g9pos[i];              // get 9 hole charge
                      }
                    }
                    
                    break loop3;
               }
               i++;
            }

            if (!gpos.equals( "" )) {          // if pos charge found

               charges = true;
            }
         }     // end of guest processing
      }

   }
   catch (Exception ignore) {
   }

   return(charges);
 }                   // end of checkCharge


 // ********************************************************************
 //  Process the POS charges for an individual member (Club Prophet Systems)
 //
 //    This interface uses SOAP and sends charges directly to their POS.
 //
 //    Check the mode of trans for charges
 // ********************************************************************

 public boolean buildChargeCPS(parmPOS parmp, Connection con) {


   ResultSet rs = null;

   String fname = parmp.fname;
   String lname = parmp.lname;
   String mship = parmp.mship0;
   String posid = parmp.posid;
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";

   int i = 0;
   int p9c = 0;

   boolean charges = false;


   // these two object contain the charge codes and the qty of each (NOW IN PARMPOS)
   // ArrayOfString code = new ArrayOfString();
   // ArrayOfInt qty = new ArrayOfInt();

   if (!posid.equals("") && posid != null) {      // we MUST have a posid to send charges

        try {

            //
            //  if the player passed is a member, check if they should be charged to play
            //
            if (parmp.player.equals( "" )) {      // if member

                //
                //  get the mship class and charge code, if any
                //
                i = 0;
                loop2:
                while (i < parmp.MAX_Mships) {

                    if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

                    mpos = parmp.mpos[i];               // get mship charge class

                    if (parmp.p9 == 0) {                  // if 18 hole round

                        mposc = parmp.mposc[i];             // get mship charge code

                    } else {

                        mposc = parmp.m9posc[i];             // get mship charge code
                    }
                    break loop2;
                    }
                    i++;
                }

                if (!mposc.equals( "" )) {          // if pos charge found for membership (non-golf mship charge)

                    //
                    //  We can now build the charge string - append it to the existing string
                    //
                    parmp.count++;                         // bump charge counter

                    charges = true;                        // indicate some charges added

                    parmp.codeA.getString().add(mposc);           // add this charge
                    parmp.qtyA.getInt().add(1);


                    //
                    //  Save charge data in pos_hist for reports
                    //
                    parmp.hist_posid = posid;
                    parmp.hist_player = fname + " " + lname;
                    parmp.hist_price = "";
                    parmp.hist_item_name = "Green Fee";
                    parmp.hist_item_num = mposc;

                    add_POS_hist(parmp, con);       // go make the entry

                }      // end of Mship Charge processing
            }


            //
            //  Now check if there is a charge code associated with this member's mode of trans
            //
            i = 0;
            loop1:
            while (i < parmp.MAX_Tmodes) {

                if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

                    if (parmp.club.equals("olympiafieldscc")) {        // Custom charges for Olympia Fields (2 courses) - see also checkCharge above !!!!!!!!!!!!!!

                        tpos = "";

                        if (parmp.pcw.equals("CRT")) {

                            if (parmp.course.equals("North Course")) {

                                tpos = "CTNC002";

                                if (parmp.p9 > 0) {                  // if 9 hole round

                                    tpos = "CTNC004";
                                }

                            } else {    // South Course

                                tpos = "CTSC002";

                                if (parmp.p9 > 0) {                  // if 9 hole round

                                    tpos = "CTSC004";
                                }                        
                            }
                        }

                    } else {    // NOT a custom

                        if (parmp.p9 == 0) {                  // if 18 hole round

                        tpos = parmp.tpos[i];               // get 18 hole charge

                        } else {

                        tpos = parmp.t9pos[i];              // get 9 hole charge
                        }
                    }

                    break loop1;     // matching mode found - done
                }
                i++;
            }           // end of loop1 WHILE

            if (!tpos.equals( "" )) {          // if Mode of Trans pos charge found

                //
                //  We can now build the charge string - append it to the existing string
                //
                parmp.count++;          // bump charge counter

                charges = true;                        // indicate some charges added

                parmp.codeA.getString().add(tpos);            // add this charge
                parmp.qtyA.getInt().add(1);


                //
                //  Save charge data in pos_hist for reports
                //
                parmp.hist_posid = posid;
                if (!parmp.player.equals( "" )) {
                    parmp.hist_player = parmp.player;          // if guest
                } else {
                    parmp.hist_player = fname + " " + lname;   // else use member name
                }
                parmp.hist_price = "";
                parmp.hist_item_name = parmp.pcw;
                parmp.hist_item_num = tpos;

                add_POS_hist(parmp, con);       // go make the entry

            }      // end of trans mode processing


            //
            //  if the player passed is a guest, charge the member for this too
            //
            if (!parmp.player.equals( "" )) {

                //
                //  First check if there is a charge code associated with this guest type
                //
                i = 0;
                loop3:
                while (i < parmp.MAX_Guests) {

                    if (parmp.player.startsWith( parmp.gtype[i] )) {

                        gpos = "";  

                        if (parmp.club.equals("olympiafieldscc")) {        // Custom guest charges for Olympia Fields (2 courses)

                            if (parmp.course.equals("North Course")) {

                                if (parmp.gtype[i].equals("Comp") || parmp.gtype[i].equals("PGA") || parmp.gtype[i].equals("Prospective Member")) {

                                    gpos = "NCGF004";

                                } else if (parmp.gtype[i].equals("Regular Twlight")) {

                                    gpos = "NCGF007";

                                } else if (parmp.gtype[i].equals("Family Twlight")) {

                                    gpos = "NCGF008";

                                } else if (parmp.gtype[i].equals("Reg Guest")) {

                                    gpos = "NCGF001";

                                    if (parmp.p9 > 0) {                  // if 9 hole round

                                    gpos = "NCGF002";       
                                    }

                                } else if (parmp.gtype[i].equals("Fam Guest")) {

                                    gpos = "NCGF005";                            

                                    if (parmp.p9 > 0) {                  // if 9 hole round

                                    gpos = "NCGF006";       
                                    }
                                }

                            } else {     // South Course

                                if (parmp.gtype[i].equals("Comp") || parmp.gtype[i].equals("PGA") || parmp.gtype[i].equals("Prospective Member")) {

                                    gpos = "SCGF004";

                                } else if (parmp.gtype[i].equals("Regular Twlight")) {

                                    gpos = "SCGF007";

                                } else if (parmp.gtype[i].equals("Family Twlight")) {

                                    gpos = "SCGF008";

                                } else if (parmp.gtype[i].equals("Reg Guest")) {

                                    gpos = "SCGF001";

                                    if (parmp.p9 > 0) {                  // if 9 hole round

                                    gpos = "SCGF002";       
                                    }

                                } else if (parmp.gtype[i].equals("Fam Guest")) {

                                    gpos = "SCGF005";         

                                    if (parmp.p9 > 0) {                  // if 9 hole round

                                    gpos = "SCGF006";       
                                    }
                                }                    
                            }

                        } else {    // NOT a custom

                        if (parmp.p9 == 0) {                  // if 18 hole round

                            gpos = parmp.gpos[i];               // get 18 hole charge

                        } else {

                            gpos = parmp.g9pos[i];              // get 9 hole charge
                        }
                        }

                        break loop3;      // found the guest type - done with this guest
                    }
                    i++;
                }        // end of loop3 WHILE

                if (!gpos.equals( "" )) {          // if pos charge found

                    //
                    //  We can now build the charge string - append it to the existing string
                    //
                    parmp.count++;          // bump charge counter

                    charges = true;                        // indicate some charges added

                    parmp.codeA.getString().add(gpos);            // add this charge
                    parmp.qtyA.getInt().add(1);


                    //
                    //  Save charge data in pos_hist for reports
                    //
                    parmp.hist_posid = posid;
                    parmp.hist_player = parmp.player;          // guest
                    parmp.hist_price = "";
                    parmp.hist_item_num = gpos;
                    parmp.hist_item_name = "Guest Fee";

                    add_POS_hist(parmp, con);               // go make the entry
                }

            }     // end of guest processing

        }
        catch (Exception exc) {
            Utilities.logError("Proshop_sheet_checkin - exception from buildChargeCPS. Error = " +exc.getMessage());
        }
        
   } else {       // posid missing - trace it so pro can see this error in the report
       
        parmp.hist_posid = "MISSING";
        parmp.hist_player = fname + " " + lname;   // use member name
        parmp.hist_price = "";
        parmp.hist_item_num = "Invalid Request";
        parmp.hist_item_name = "*** Member missing a posid.";

        add_POS_hist(parmp, con);               // go make the entry
        
        String returnCode = "NOTICE: Member " +fname+ " " +lname+ " is missing a POSID in his/her ForeTees database record. POS Charge was NOT SENT.";
        
        // save error in the return code so it is displayed to the pro
        
        if (parmp.returnCode1.equals("")) {

            parmp.returnCode1 = returnCode;

        } else if (parmp.returnCode2.equals("")) {

            parmp.returnCode2 = returnCode;

        } else if (parmp.returnCode3.equals("")) {

            parmp.returnCode3 = returnCode;

        } else if (parmp.returnCode4.equals("")) {

            parmp.returnCode4 = returnCode;

        } else if (parmp.returnCode5.equals("")) {

            parmp.returnCode5 = returnCode;
        }
   }       // end of IF posid  

   return(charges);
 }
   
   
 // ********************************************************************
 //  Send the POS charges to Club Prophet Systems
 //
 //    This interface uses SOAP and sends charges directly to their POS.
 //
 // ********************************************************************

 public boolean sendChargeCPS(parmPOS parmp, Connection con) {


   String returnCode = "";
   
   boolean failed = false;
   
   try {
                            
      URL url = null;

      // set the url depending on the club (this must point to each clubs dedicated PSK/Web server) - check for DEMO below
      //
      if (parmp.club.startsWith("demo")) {

          url = new URL("http://75.148.33.99:45678/demo");    // anthing to get past next test - real url is embedded in org.tempuri.IntegrationService

      } else if (parmp.club.equals("cherryhills")) {

          url = new URL("http://75.148.33.99:45678/POSExpressWS3/IntegrationService.asmx?WSDL");

      } else if (parmp.club.equals("foresthighlands")) {

          url = new URL("http://24.156.39.173:8181/posexpressws3/IntegrationService.asmx");
          //url = new URL("http://24.156.39.173:8083/POSExpressWS3/IntegrationService.asmx");
          //url = new URL("http://24.121.33.249:8083/POSExpressWS3/IntegrationService.asmx?WSDL");

      } else if (parmp.club.equals("ccrockies")) {
          url = new URL("http://70.90.119.117:8080/posexpressws3/integrationservice.asmx");               
        //url = new URL("http://70.90.119.117:8080/POSExpressWS3/IntegrationService.asmx");
        //url = new URL("http://70.89.104.149/CPSTestService/IntegrationService.asmx?WSDL");

      } else if (parmp.club.equals("sauconvalleycc")) {

          url = new URL("http://207.96.31.123/POSExpressWS3/IntegrationService.asmx?WSDL");

      } else if (parmp.club.equals("dallasathleticclub")) {

          url = new URL("http://66.196.214.196/POSExpressWS3/IntegrationService.asmx");

      } else if (parmp.club.equals("edisonclub")) {
          
          url = new URL("http://24.97.30.118/posexpressws3/IntegrationService.asmx");
          //url = new URL("http://24.97.30.118/StarterHutWS3/OnlineReservationService.asmx");
          //url = new URL("http://edisonclub.no-ip.net/posexpressws3/IntegrationService.asmx");
          //url = new URL("http://108.176.76.187/posexpressws3/IntegrationService.asmx");
          //url = new URL("http://69.193.10.203/posexpressws3/IntegrationService.asmx");
          //url = new URL("http://69.193.10.205/POSExpressWS3/IntegrationService.asmx?WSDL");

      } else if (parmp.club.equals("oldoaks")) {

          url = new URL("http://74.89.198.184:8000/posexpressws3/integrationservice.asmx");

      } else if (parmp.club.equals("coloradospringscountryclub")) {

          //url = new URL("http://74.93.237.209/posexpressws3/integrationservice.asmx");
          url = new URL("http://74.93.237.209:8080/posexpressws3/integrationservice.asmx");

      } else if (parmp.club.equals("pwgolf")) {

          url = new URL("http://208.188.248.162:8080/POSExpressWS3/IntegrationService.asmx");

      } else if (parmp.club.equals("westongolfclub")) {

          url = new URL("http://75.150.72.113/POSExpressWS3/IntegrationService.asmx");
          
      } else if (parmp.club.equals("cranecreek")) {

          url = new URL("http://71.39.114.116/POSExpressWS3/IntegrationService.asmx");
          //url = new URL("http://71.39.114.117/POSExpressWS3/IntegrationService.asmx");
          
      } else if (parmp.club.equals("championsrun")) {

          url = new URL("http://mail.championsomaha.com:8080/PosExpressWS3/integrationservice.asmx");

      } else if (parmp.club.equals("hillwoodcc")) {

          url = new URL("http://remote.hillwoodcc.org:8080/POSExpressWS3/IntegrationService.asmx");
          
      } else if (parmp.club.equals("sugarvalleygc")) {
          
          url = new URL("http://24.123.70.110/posexpressws3/IntegrationService.asmx");
          
      } else if (parmp.club.equals("bracketts")) {
          
          url = new URL("http://66.191.141.194:17253/POSExpressWS3/IntegrationService.asmx");
          //url = new URL("http://24.159.223.34:17253/POSExpressWS3/IntegrationService.asmx");   // changed to above on 4/26/12 per CPS/Bracketts' request
          
      } else if (parmp.club.equals("olympiafieldscc")) {
          
          url = new URL("http://173.15.55.154/POSExpressWS3/IntegrationService.asmx");
          
      } else if (parmp.club.equals("foxdencountryclub")) {
          
          url = new URL("http://184.61.99.34/posexpressws3/integrationservice.asmx");
          
      } else if (parmp.club.equals("marshfieldcc")) {
          
          url = new URL("http://96.237.244.52/POSExpressWS3/IntegrationService.asmx");
          
      } else if (parmp.club.equals("ledgemontcc")) {
          
          url = new URL("http://173.166.16.181:8181/posexpressws3/integrationservice.asmx");
          
      } else if (parmp.club.equals("fairlawncc")) {
          
          url = new URL("http://74.218.111.6/POSExpressWS3/IntegrationService.asmx");
          
      } else if (parmp.club.equals("tedescocc")) {
          
          url = new URL("http://70.91.141.10:8080/posexpressws3/integrationservice.asmx");
          
      }

      if (url != null) {
         
         //
         //  Trace the charges being sent !!!!!!!!!!!!
         //
         tracePOS(1, "", parmp, con);       // type = send, no return code
         

         org.tempuri.IntegrationService service = null;
         //org.tempuri.OnlineSaleResult result = null;

         if (parmp.club.startsWith("demo")) {        // if this is a demo site

            service = new org.tempuri.IntegrationService();    // use default settings in IntegrationService (PSK's test server)

         } else {

            // this is their main object
            service = new org.tempuri.IntegrationService(url, new QName("http://tempuri.org/", "IntegrationService"));
         }

         // their port is the getIntegrationServiceSoap object
         org.tempuri.IntegrationServiceSoap port = service.getIntegrationServiceSoap();

         try {

             if (parmp.club.equals("ccrockies")) {

                //
                //  CC of the Rockies - custom (case 1772) to pass the Member Name and Guest Name (if present) - saved in parmp.item
                //
                //   NOTE:  this requires a custom in the CPS system as well to handle the extra parm on the addSaleOnlineV2 function
                //
                // the result oject will contain the response we receive
                org.tempuri.OnlineSaleResult result = port.addSaleOnlineV2WithNotes(parmp.posid, parmp.codeA, parmp.qtyA, parmp.item);   // send the charges

                // get the response
                returnCode = result.getReturnText();

             } else {

                // the result oject will contain the response we receive
                org.tempuri.OnlineSaleResult result = port.addSaleOnlineV2(parmp.posid, parmp.codeA, parmp.qtyA);      // send the charges

                // get the response
                returnCode = result.getReturnText();
             }

         } catch (Exception exc) {

             Utilities.logError("Proshop_sheet_checkin.sendChargeCPS(): Likely communication error for club=" + parmp.club + ", err=" +exc.getMessage());

             returnCode = "ERROR COMMUNICATING WITH CPSV3 WEB SERVICE AT YOUR CLUB. PLEASE CONTACT PSK OR FORETEES FOR ASSISTANCE OR TRY AGAIN LATER.";

         } finally {

             parmp.item = "";         // init name field

         }

         //
         //  Trace the response !!!!!!!!!!!!
         //
         tracePOS(2, returnCode, parmp, con);      // type = response with return code
         
         clearWaiting(parmp, con);       // indicate in pos_hist entry that we are no longer waiting for a reply
            

         // save the return code
         if (parmp.returnCode1.equals("")) {

            parmp.returnCode1 = returnCode;

         } else if (parmp.returnCode2.equals("")) {

            parmp.returnCode2 = returnCode;

         } else if (parmp.returnCode3.equals("")) {

            parmp.returnCode3 = returnCode;

         } else if (parmp.returnCode4.equals("")) {

            parmp.returnCode4 = returnCode;

         } else if (parmp.returnCode5.equals("")) {

            parmp.returnCode5 = returnCode;
         }

         if (!returnCode.equals("") && !returnCode.equals("Success")) {      // if failed

            //
            //  Save error code in pos_hist for reports
            //
            parmp.hist_posid = parmp.posid;
            if (!parmp.player.equals( "" )) {
               parmp.hist_player = parmp.player;          // if guest
            } else {
               parmp.hist_player = parmp.fname + " " + parmp.lname;   // else use member name
            }
            parmp.hist_price = "";
            parmp.hist_item_num = "POS Rejected Charge";
            returnCode = truncate(returnCode, 45);            // make sure it fits
            parmp.hist_item_name = returnCode;

            add_POS_hist(parmp, con);               // go make the entry
            
            failed = true;
            
         } else {
            
            failed = false;
         }   
         
      } else {
          
          failed = true;      // no URL found - return failed status
      }

   }
   catch (Exception exc) {
      Utilities.logError("Proshop_sheet_checkin - exception from sendChargeCPS. club=" + parmp.club + ", Error = " +exc.getMessage());
      failed = true;
   }

   return(failed);
 }                   // end of sendChargeCPS


 // ********************************************************************
 //  Send the POS charges to the Integrated Business Systems
 //
 //    This interface uses SOAP and sends charges directly to their POS.
 //
 //  Notes: the return value of this method is not currently used.
 // ********************************************************************

 public static boolean sendChargeIBS(parmPOS parmp, Connection con) {


    String returnCode = ""; // this is not uesd?!  using parmp.return1 which is checked back in Proshop_sheet_pos

    boolean failed = false;

    Statement stmt = null;
    ResultSet rs = null;

    String ws_url = "", ws_user = "", ws_pass = "", deptID = "", tenderID = "", invMenuID = "", taxID = "";
    
    
    //
    //  IBS is returning an error that is evidently meaningless as the charges go through just fine at Baltsurol.  We reported the problem but they have been
    //  unable to resolve it.  Therefore we are going to ignore this error.  We will trace it in the v5 db pos table, but we will not return it or add it to
    //  the POS Report.   12-15-2011 BP
    //
    String tempErr = "ProcessRequestData failed.  Last Error: An error occurred while processing Webres3 request 'qryXMLCreateMemberAPITickets '";
    
    

    // LOAD UP ALL IBS CONFIG DATA FROM club5
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT pos_ws_url, pos_ws_user, pos_ws_pass, ibs_uidDeptID, ibs_uidTenderID, ibs_uidInvMenuID, ibs_uidTaxID FROM club5");

        if (rs.next()) {

            ws_url = rs.getString("pos_ws_url");
            ws_user = rs.getString("pos_ws_user");
            ws_pass = rs.getString("pos_ws_pass");
            deptID = rs.getString("ibs_uidDeptID");
            tenderID = rs.getString("ibs_uidTenderID");
            invMenuID = rs.getString("ibs_uidInvMenuID");
            taxID = rs.getString("ibs_uidTaxID");

        }

    } catch (Exception exc) {

        parmp.returnCode1 = "" +
                "<p>No charges sent! Fatal error loading up IBS interface values." +
                "<br>Error: " + exc.toString() + "</p>";
        return false;

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { stmt.close(); }
        catch (SQLException ignored) {}

        if (ws_url.equals("") || ws_user.equals("") || ws_pass.equals("") ||
            deptID.equals("") || tenderID.equals("") || taxID.equals("")) {     // || invMenuID.equals("")

            parmp.returnCode1 = "" +
                    "<p>No charges sent! One or more IBS interface values are missing.</p>";

            return false;

        }
    }

    //
    // IF WE ARE STILL HERE THEN THE INTERFACE SPECS HAVE BEEN LOADED
    //

    String err = "";

    try {

        URL url = new URL(ws_url);

        if (url != null) {

            IBSWebMemberAPI service = null;

            javax.xml.ws.Holder<String> holderMessage = new javax.xml.ws.Holder<String>();
            javax.xml.ws.Holder<Boolean> holderResult = new javax.xml.ws.Holder<Boolean>();

            service = new IBSWebMemberAPI(url, new QName("http://ibsservices.org/", "IBSWebMemberAPI"));

            // their port is the getIBSWebMemberAPISoap object
            IBSWebMemberAPISoap port = service.getIBSWebMemberAPISoap();

            int i = 0;
            ArrayList<ArrayList<String>> batch = new ArrayList<ArrayList<String>>();

            try {

                batch = buildXmlFileForIBS(parmp, deptID, tenderID, taxID, con);

                for (i = 0; i < batch.size(); i++) {

                    // trace the charges being sent
                    tracePOS(1, "", parmp, con);       // type = send, no return code

                    //
                    //  TEMP for debug
                    //
                    /*
                    if (parmp.club.equals("woodranch")) {    // trace this
                       
                        try {
                              Utilities.logDebug("BP", "Wood Ranch CC - sending IBS batch " + (i + 1) + "/" + batch.size() + ": " + batch.get(i).get(1));
                        } catch (Exception ignore) {}
                    }
                    * 
                    */

                    

                    //try {
                    
                        port.createTickets(ws_user, ws_pass, batch.get(i).get(1), holderMessage, holderResult);
                        
                    //} catch (Exception exc) {
                    //    Utilities.logError("sendChargeIBS(): createTickets failed. club=" + parmp.club + ", loop " + i + " of " + batch.size() + ", err=" +exc.getMessage() + ", err=" +exc.toString());
                    //}
                    
                    if (holderResult.value == false) {
                       
                       //  Check for meaningless error from IBS - a bug they haven't fixed - skip it, but trace it below
                       
                       if (!holderMessage.value.startsWith( tempErr )) {     // if NOT the meaningless error - track it (TEMP check until IBS fixes this bug)

                           // failed - identify the member and the related message
                           if (holderMessage.value != null) parmp.returnCode1 += "<p>Charge for " + batch.get(i).get(0) + " failed. Reason: " + holderMessage.value + "</p>";
                           //parmp.returnCode1 += "<p>Charge for " + batch.get(i).get(0) + " failed. Reason: Testing</p>";

                           //
                           //  Save error code in pos_hist for reports
                           //
                           parmp.hist_posid = parmp.posid;

                           if (!parmp.player.equals( "" )) {
                              parmp.hist_player = parmp.player;          // if guest
                           } else {
                              parmp.hist_player = parmp.fname + " " + parmp.lname;   // else use member name
                           }

                           parmp.hist_price = "";
                           parmp.hist_item_num = "POS Rejected Charge";
                           returnCode = truncate(returnCode, 45);            // make sure it fits
                           parmp.hist_item_name = returnCode;

                           add_POS_hist(parmp, con);               // go make the entry
                       }
                    }
                        
                    // trace the charges being sent
                    tracePOS(2, holderMessage.value, parmp, con);       // type = response, no return code

                }     // end of FOR loop - send all charges
                
               // clearWaiting(parmp, con);              // indicate we are no longer waiting for a reply (NOTE: may have to do this diefferently as several charges sent)!!!
            
            } catch (Exception exc) {

                 Utilities.logError("sendChargeIBS(): " + err + " Likely communication error for club=" + parmp.club + ", loop " + i + " of " + batch.size() + ", err=" +exc.getMessage() + ", err=" +exc.toString());

                 //returnCode = "ERROR COMMUNICATING WITH THE IBS WEB SERVICE AT YOUR CLUB. PLEASE CONTACT IBS OR FORETEES FOR ASSISTANCE OR TRY AGAIN LATER.";

                 // incase there are existing error messages already in returnCode1 lets prefix it with this failure message
                 parmp.returnCode1 = "ERROR COMMUNICATING WITH THE IBS WEB SERVICE AT YOUR CLUB. PLEASE CONTACT IBS OR FORETEES FOR ASSISTANCE OR TRY AGAIN LATER." + parmp.returnCode1;
                 failed = true;

            } finally {

                 parmp.item = "";         // init name field (DO WE NEED THIS FOR IBS?)

            }

/*

            // save the return code
            if (parmp.returnCode1.equals("")) {

                parmp.returnCode1 = returnCode;

            } else if (parmp.returnCode2.equals("")) {

                parmp.returnCode2 = returnCode;

            } else if (parmp.returnCode3.equals("")) {

                parmp.returnCode3 = returnCode;

            } else if (parmp.returnCode4.equals("")) {

                parmp.returnCode4 = returnCode;

            } else if (parmp.returnCode5.equals("")) {

                parmp.returnCode5 = returnCode;
            }

            if (holderResult.value == false) { // if failed
            
                //
                //  Save error code in pos_hist for reports
                //
                parmp.hist_posid = parmp.posid;

                if (!parmp.player.equals( "" )) {
                   parmp.hist_player = parmp.player;          // if guest
                } else {
                   parmp.hist_player = parmp.fname + " " + parmp.lname;   // else use member name
                }

                parmp.hist_price = "";
                parmp.hist_item_num = "POS Rejected Charge";
                returnCode = truncate(returnCode, 45);            // make sure it fits
                parmp.hist_item_name = returnCode;

                add_POS_hist(parmp, con);               // go make the entry

                failed = true;

            } else {

                failed = false;
            }
*/
        } // end if URL != null

    } catch (Exception exc) {

        Utilities.logError("Proshop_sheet_checkin - exception from sendChargeIBS. club=" + parmp.club + ", Error = " +exc.getMessage());
        failed = true;

    }

    return(failed);

 }  // end of sendChargeIBS


 // ********************************************************************
 //  Process the POS charges for this tee time or individual player (Pro-ShopKeeper)
 // ********************************************************************

 public boolean promptPOS(int p, int teecurr_id, String refImage, parmPOS parmp, parmClub parm, PrintWriter out, Connection con) { 
   

   ResultSet rs = null;
   PreparedStatement pstmt = null;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String user6 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String p6cw = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String userg6 = "";
   //String num = "";
   //String j = "";
   String course = parmp.course;

   int i = 0;
   int time = parmp.time;
   int guest = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int p96 = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int show6 = 0;
   int pos1 = 0;
   int pos2 = 0;
   int pos3 = 0;
   int pos4 = 0;
   int pos5 = 0;
   int pos6 = 0;

   long date = parmp.date;

   boolean charges = false;
   boolean chargesSent = false;
   boolean returnchg = false;

   //
   //  String and fields to provide the charge info to the POS
   //
   //    format = count, memid, date time, 9/18, lname, fname, *zip, *phone, *email, mship_class, charge_code
   //
   //                1 entry per 'count' (only 1 count field),  * = skip these fields
   //
   String sdate = "";         // date and time field for POS string

   //
   //  Build date/time string
   //
   long year = date/10000;
   long month = (date - (year * 10000))/100;
   long day = date - ((year * 10000) + (month * 100));

   sdate = month + "/" + day + "/" + year + " " + time/100 + ":" + SystemUtils.ensureDoubleDigit(time - ((time/100) * 100));  // create date and time string
   
   //
   //  Determine if there will be any charges for this request
   //
   try {

      pstmt = con.prepareStatement (
            "SELECT * FROM teecurr2 WHERE teecurr_id = ?");

      pstmt.clearParameters();
      pstmt.setInt(1, teecurr_id);

      rs = pstmt.executeQuery();

      if (rs.next()) {

         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         p1cw = rs.getString("p1cw");
         p2cw = rs.getString("p2cw");
         p3cw = rs.getString("p3cw");
         p4cw = rs.getString("p4cw");
         show1 = rs.getInt("show1");
         show2 = rs.getInt("show2");
         show3 = rs.getInt("show3");
         show4 = rs.getInt("show4");
         player5 = rs.getString("player5");
         user5 = rs.getString("username5");
         p5cw = rs.getString("p5cw");
         show5 = rs.getInt("show5");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");
         p91 = rs.getInt("p91");
         p92 = rs.getInt("p92");
         p93 = rs.getInt("p93");
         p94 = rs.getInt("p94");
         p95 = rs.getInt("p95");
         pos1 = rs.getInt("pos1");
         pos2 = rs.getInt("pos2");
         pos3 = rs.getInt("pos3");
         pos4 = rs.getInt("pos4");
         pos5 = rs.getInt("pos5");
      }

   } catch (Exception exc) {

        Utilities.logError("Error in Proshop_sheet_checkin.promptPOS() - Error loading teecurr2 data, err=" + exc.getMessage() );

   } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { pstmt.close(); }
        catch (SQLException ignored) {}

   }
     
   //
   //   Init fields in pos parm block
   //
   parmp.count = 0;       
   parmp.sdate = sdate;
   parmp.poslist = "";
     
   //
   //   if call was for Check All, then process all players in the specified slot
   //
   if (p == 0) {

      //
      //  Process one player at a time to determine any charges
      //
      if (!player1.equalsIgnoreCase( "x" ) && !player1.equals( "" )) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;
           
         if (user1.equals( "" )) {            // if no username for this player
           
            ploop1:
            while (i < parm.MAX_Guests) {
               if (player1.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player1 is a guest name
                  break ploop1;
               }
               i++;
            }
         }
         parmp.pcw = p1cw;
         parmp.p9 = p91;
           
         if (guest == 0) {        // if member

            if (!user1.equals( "" ) && pos1 == 0) {      // skip if no user name found or already processed

               parmp.player = "";   // indicate member
               parmp.user = user1;

               charges = buildCharge(parmp, con);
               
               if (charges == true) {      // if any charges were built

                  parmp.posSent1 = true;   // indicate charges built/sent for this player
               }                  
            }

         } else {          // else guest

            if (!userg1.equals( "" ) && pos1 == 0) {      // skip if no member associated with this guest

               parmp.player = player1;   // indicate guest - pass the guest type
               parmp.user = userg1;

               charges = buildCharge(parmp, con);
               
               if (charges == true) {      // if any charges were built

                  parmp.posSent1 = true;   // indicate charges built/sent for this player
               }                  
            }
         }   // end of IF member or guest
      }      // end of IF player not X and not null

      if (!player2.equalsIgnoreCase( "x" ) && !player2.equals( "" )) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;
           
         if (user2.equals( "" )) {            // if no username for this player

            ploop2:
            while (i < parm.MAX_Guests) {
               if (player2.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player2 is a guest name
                  break ploop2;
               }
               i++;
            }
         }
         parmp.pcw = p2cw;
         parmp.p9 = p92;

         if (guest == 0) {        // if member

            if (!user2.equals( "" ) && pos2 == 0) {      // skip if no user name found

               parmp.player = "";   // indicate member
               parmp.user = user2;

               charges = buildCharge(parmp, con);
               
               if (charges == true) {      // if any charges were built

                  parmp.posSent2 = true;   // indicate charges built/sent for this player
               }                  
            }

         } else {          // else guest

            if (!userg2.equals( "" ) && pos2 == 0) {      // skip if no member associated with this guest

               parmp.player = player2;   // indicate guest - pass the guest type
               parmp.user = userg2;

               charges = buildCharge(parmp, con);
               
               if (charges == true) {      // if any charges were built

                  parmp.posSent2 = true;   // indicate charges built/sent for this player
               }                  
            }
         }   // end of IF member or guest
      }      // end of IF player not X and not null

      if (!player3.equalsIgnoreCase( "x" ) && !player3.equals( "" )) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;

         if (user3.equals( "" )) {            // if no username for this player

            ploop3:
            while (i < parm.MAX_Guests) {
               if (player3.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player3 is a guest name
                  break ploop3;
               }
               i++;
            }
         }
         parmp.pcw = p3cw;
         parmp.p9 = p93;

         if (guest == 0) {        // if member

            if (!user3.equals( "" ) && pos3 == 0) {      // skip if no user name found

               parmp.player = "";   // indicate member
               parmp.user = user3;

               charges = buildCharge(parmp, con);
               
               if (charges == true) {      // if any charges were built

                  parmp.posSent3 = true;   // indicate charges built/sent for this player
               }                  
            }

         } else {          // else guest

            if (!userg3.equals( "" ) && pos3 == 0) {      // skip if no member associated with this guest

               parmp.player = player3;   // indicate guest - pass the guest type
               parmp.user = userg3;

               charges = buildCharge(parmp, con);
               
               if (charges == true) {      // if any charges were built

                  parmp.posSent3 = true;   // indicate charges built/sent for this player
               }                  
            }
         }   // end of IF member or guest
      }      // end of IF player not X and not null

      if (!player4.equalsIgnoreCase( "x" ) && !player4.equals( "" )) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;

         if (user4.equals( "" )) {            // if no username for this player

            ploop4:
            while (i < parm.MAX_Guests) {
               if (player4.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player4 is a guest name
                  break ploop4;
               }
               i++;
            }
         }
         parmp.pcw = p4cw;
         parmp.p9 = p94;

         if (guest == 0) {        // if member

            if (!user4.equals( "" ) && pos4 == 0) {      // skip if no user name found

               parmp.player = "";   // indicate member
               parmp.user = user4;

               charges = buildCharge(parmp, con);
               
               if (charges == true) {      // if any charges were built

                  parmp.posSent4 = true;   // indicate charges built/sent for this player
               }                  
            }

         } else {          // else guest

            if (!userg4.equals( "" ) && pos4 == 0) {      // skip if no member associated with this guest

               parmp.player = player4;   // indicate guest - pass the guest type
               parmp.user = userg4;

               charges = buildCharge(parmp, con);
               
               if (charges == true) {      // if any charges were built

                  parmp.posSent4 = true;   // indicate charges built/sent for this player
               }                  
            }
         }   // end of IF member or guest
      }      // end of IF player not X and not null

      if (!player5.equalsIgnoreCase( "x" ) && !player5.equals( "" )) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;

         if (user5.equals( "" )) {            // if no username for this player

            ploop5:
            while (i < parm.MAX_Guests) {
               if (player5.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player5 is a guest name
                  break ploop5;
               }
               i++;
            }
         }
         parmp.pcw = p5cw;
         parmp.p9 = p95;

         if (guest == 0) {        // if member

            if (!user5.equals( "" ) && pos5 == 0) {      // skip if no user name found

               parmp.player = "";   // indicate member
               parmp.user = user5;

               charges = buildCharge(parmp, con);
               
               if (charges == true) {      // if any charges were built

                  parmp.posSent5 = true;   // indicate charges built/sent for this player
               }                  
            }

         } else {          // else guest

            if (!userg5.equals( "" ) && pos5 == 0) {      // skip if no member associated with this guest

               parmp.player = player5;   // indicate guest - pass the guest type
               parmp.user = userg5;

               charges = buildCharge(parmp, con);
               
               if (charges == true) {      // if any charges were built

                  parmp.posSent5 = true;   // indicate charges built/sent for this player
               }                  
            }
         }   // end of IF member or guest
      }      // end of IF player not X and not null

   } else {        // this was an individual check-in/out

      //
      //  Determine which player is to be processed
      //
      if (p == 1) {              // get player based on player #
         player6 = player1;
         user6 = user1;
         userg6 = userg1;
         p6cw = p1cw;
         p96 = p91;
         pos6 = pos1;
         show6 = show1;
      }
      if (p == 2) {
         player6 = player2;
         user6 = user2;
         userg6 = userg2;
         p6cw = p2cw;
         p96 = p92;
         pos6 = pos2;
         show6 = show2;
      }
      if (p == 3) {
         player6 = player3;
         user6 = user3;
         userg6 = userg3;
         p6cw = p3cw;
         p96 = p93;
         pos6 = pos3;
         show6 = show3;
      }
      if (p == 4) {
         player6 = player4;
         user6 = user4;
         userg6 = userg4;
         p6cw = p4cw;
         p96 = p94;
         pos6 = pos4;
         show6 = show4;
      }
      if (p == 5) {
         player6 = player5;
         user6 = user5;
         userg6 = userg5;
         p6cw = p5cw;
         p96 = p95;
         pos6 = pos5;
         show6 = show5;
      }

      //
      //  Make sure we should process the charge/return.
      //  User may have done a check-in/out without doing the charge prior to this.
      //  If so, the show & pos flags will be out of sync.  Only do charge if in sync. 
      //
      if ((show6 == 0 && pos6 == 0) || (show6 == 1 && pos6 == 3)) { 

         //
         //  Request to check-in or check-out a single player
         //
         if (pos6 == 3) {

            returnchg = true;      // this is a return (credit)
         }

         if (!player6.equalsIgnoreCase( "x" ) && !player6.equals( "" )) {

            //
            //  Check if player name is member or guest
            //
            i = 0;
            guest = 0;

            if (user6.equals( "" )) {            // if no username for this player

               ploop6:
               while (i < parm.MAX_Guests) {
                  if (player6.startsWith( parm.guest[i] )) {

                     guest = 1;       // indicate player is a guest name
                     break ploop6;
                  }
                  i++;
               }
            }
            parmp.pcw = p6cw;
            parmp.p9 = p96;

            if (guest == 0) {        // if member

               if (!user6.equals( "" )) {      // skip if no user name found

                  parmp.player = "";   // indicate member
                  parmp.user = user6;

                  charges = buildCharge(parmp, con);
               
                  if (charges == true) {      // if any charges were built

                     switch (p) {
                      case 1:
                          parmp.posSent1 = true;    // indicate charges built/sent for this player
                          break;
                      case 2:
                          parmp.posSent2 = true;             
                          break;
                      case 3:
                          parmp.posSent3 = true;             
                          break;
                      case 4:
                          parmp.posSent4 = true;             
                          break;
                      case 5:
                          parmp.posSent5 = true;             
                     }
                  }                  
               }

            } else {          // else guest

               if (!userg6.equals( "" )) {      // skip if no member associated with this guest

                  parmp.player = player6;   // indicate guest - pass the guest type
                  parmp.user = userg6;

                  charges = buildCharge(parmp, con);

                  if (charges == true) {      // if any charges were built

                     switch (p) {
                      case 1:
                          parmp.posSent1 = true;    // indicate charges built/sent for this player
                          break;
                      case 2:
                          parmp.posSent2 = true;             
                          break;
                      case 3:
                          parmp.posSent3 = true;             
                          break;
                      case 4:
                          parmp.posSent4 = true;             
                          break;
                      case 5:
                          parmp.posSent5 = true;             
                     }
                  }                  
               }
            }   // end of IF member or guest
              
            //
            //  if charge/credit was built, toggle the pos indicator for this player
            //
            if (parmp.count > 0) {
              
               if (pos6 == 0) {
                  pos6 = 3;
               } else {
                  pos6 = 0;
               }
            }
         }      // end of IF player not X and not null
      }      // end of IF we should do a pos charge/return
   }

   if (parmp.count > 0) {        // if charges found

      chargesSent = true;        // indicate charges to process

      String counts = String.valueOf( parmp.count );     // create string value from count

      if (returnchg == true) {         // credit ??

         parmp.poslist = "-" + counts + "," + parmp.poslist;   // create credit string

      } else {

         parmp.poslist = counts + "," + parmp.poslist;   // create full charge string
      }

      parmp.poslist = "ChangeStatus(" + parmp.poslist + ")";  // wrap pos list with command

      
      // TEMP !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      // Utilities.logError("Proshop_sheet_checkin - promptPOS charges found. " +parmp.poslist);

   
   
      //
      //  Output a prompt to see how pro wants to proceed
      //
      out.println("<html><body>");
      //
      //*******************************************************************
      //  Send Status to Process POS Charge
      //       The window.status will generate a StatusTextChange browser control.
      //       Do this once to send the pos charge, then again to clear it.
      //*******************************************************************
      //
      out.println("<script type=\"text/javascript\">");         
      out.println("<!--");

      out.println("function sendstatus(list) {");

      out.println("parent.window.status = list;");
      out.println("parent.window.status = 'Transaction Complete';");
      out.println("return true;");

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script
    
      out.println("<form method=\"get\" action=\"Proshop_sheet_checkin\" name=\"frmYes\" id=\"frmYes\">");
      out.println("<input type=\"hidden\" name=\"POScontinue\" value=\"yes\">");          // indicate prompt and pos charges sent
      out.println("<input type=\"hidden\" name=\"pNum\" value=\"" + p + "\">");
      out.println("<input type=\"hidden\" name=\"tid\" value=\"" + teecurr_id + "\">");      
      out.println("<input type=\"hidden\" name=\"imgId\" value=\"" + refImage + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      if (parmp.posSent1 == true) {
         out.println("<input type=\"hidden\" name=\"POSsent1\" value=\"yes\">");       // include POS charges sent for this player
      }
      if (parmp.posSent2 == true) {
         out.println("<input type=\"hidden\" name=\"POSsent2\" value=\"yes\">");             
      }
      if (parmp.posSent3 == true) {
         out.println("<input type=\"hidden\" name=\"POSsent3\" value=\"yes\">");             
      }
      if (parmp.posSent4 == true) {
         out.println("<input type=\"hidden\" name=\"POSsent4\" value=\"yes\">");              
      }
      if (parmp.posSent5 == true) {
         out.println("<input type=\"hidden\" name=\"POSsent5\" value=\"yes\">");              
      }
      //out.println("<input type=\"submit\" value=\"Yes - Continue\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");

      out.println("<form method=\"get\" action=\"Proshop_sheet_checkin\" name=\"frmNo\" id=\"frmNo\">");
      out.println("<input type=\"hidden\" name=\"POScontinue\" value=\"yes\">");          // indicate prompt sent
      out.println("<input type=\"hidden\" name=\"POSskip\" value=\"yes\">");              // skip pos flags - just process the check-in
      out.println("<input type=\"hidden\" name=\"pNum\" value=\"" + p + "\">");
      out.println("<input type=\"hidden\" name=\"tid\" value=\"" + teecurr_id + "\">");      
      out.println("<input type=\"hidden\" name=\"imgId\" value=\"" + refImage + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      //out.println("<input type=\"submit\" value=\"No - Just Process Check-in/out\" name=\"return\" ONCLICK=\"window.status='';return true;\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");

      out.println("<script type=\"text/javascript\">");
      out.println(" if(confirm('There are charges for the player(s) you are checking in.\\n\\nWould you like to send these charges to the " +
                      "Pro-ShopKeeper POS system?\\n\\nSelect OK to send charges and check player(s) in. \\n\\nSelect CANCEL to only check " +
                      "player(s) in.')) {");
      out.println("  sendstatus('" +parmp.poslist+ "');");   // SEND the Status (POS Charge String) to the PSK system  
      // out.println("  alert('POS Charges Sent: " +parmp.poslist+ "');");   // use this for testing !!!!!!! 
      out.println("  document.forms[0].submit();");          // submit the 1st form above (OK)
      out.println(" } else {");
      out.println("  document.forms[1].submit();");          // submit the 2nd form above (Cancel)
      out.println(" }");
      out.println("</script>");

      out.println("</body></html>");
      out.close();
   }

   return(chargesSent);
   
 }                   // end of promptPOS


 // ********************************************************************
 //  Process the POS charges for an individual member (Pro-ShopKeeper)
 //
 //    Check the mode of trans for charges
 // ********************************************************************

 public boolean buildCharge(parmPOS parmp, Connection con) {


   boolean charges = false;
   
   ResultSet rs = null;
   PreparedStatement pstmt = null;

   String fname = "";
   String lname = "";
   String mship = "";
   String posid = "";
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";

   int i = 0;
   int p9c = 0;


   try {

      //
      //  First check if there is a charge code associated with this member's mode of trans
      //
      i = 0;
      loop1:
      while (i < parmp.MAX_Tmodes) {

         if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

            if (parmp.p9 == 0) {                  // if 18 hole round

               tpos = parmp.tpos[i];               // get 18 hole charge

            } else {

               tpos = parmp.t9pos[i];              // get 9 hole charge
            }
            break loop1;
         }
         i++;
      }

      //
      //  get the member's name and mship info
      //
      pstmt = con.prepareStatement (
                "SELECT name_last, name_first, m_ship, posid FROM member2b WHERE username= ?");

      pstmt.clearParameters();
      pstmt.setString(1, parmp.user);

      rs = pstmt.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mship = rs.getString(3);
         posid = rs.getString(4);
      }
      pstmt.close();

      lname = lname.replace("'", "-");    //  replace any single quotes (O'Brien = O-Brien)

      //
      //  get the mship class and charge code, if any
      //
      i = 0;
      loop2:
      while (i < parmp.MAX_Mships) {

         if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

            mpos = parmp.mpos[i];               // get mship charge class
              
            if (parmp.p9 == 0) {                  // if 18 hole round

               mposc = parmp.mposc[i];             // get mship charge code
                  
            } else {
              
               mposc = parmp.m9posc[i];             // get mship charge code
            }
            break loop2;
         }
         i++;
      }

      if (!mposc.equals( "" )) {          // if pos charge found for membership (non-golf mship charge)

         //
         //  We can now build the charge string - append it to the existing string
         //
         parmp.count++;          // bump charge counter

         if (parmp.p9 == 1) {        // 9 hole round ?
            p9c = 9;
         } else {
            p9c = 18;
         }

         if (parmp.count > 1) {      // if others already exist

            parmp.poslist = parmp.poslist + ",";      // add comma for seperator
         }

         parmp.poslist = parmp.poslist + posid + ",";          // player id
         parmp.poslist = parmp.poslist + parmp.sdate + ",";    // date and time of tee time
         parmp.poslist = parmp.poslist + p9c + ",";             // 9 or 18 holes
         parmp.poslist = parmp.poslist + lname + ",";          // last name
         parmp.poslist = parmp.poslist + fname + ",,,,";       // first name - skip zip, phone, email
         parmp.poslist = parmp.poslist + mpos + ",";           // mship class code
         parmp.poslist = parmp.poslist + mposc;                // charge code for non-golf mship
         
         charges = true;                 //indicate charges built for this player
         
         //
         //  Save charge data in pos_hist for reports
         //
         parmp.hist_posid = posid;
         parmp.hist_player = fname + " " + lname;
         parmp.hist_price = "";
         parmp.hist_item_name = "Green Fee";
         parmp.hist_item_num = mposc;
         
         add_POS_hist(parmp, con);       // go make the entry      

      }      // end of Mship Charge processing
      

      if (!tpos.equals( "" )) {          // if Mode of Trans pos charge found

         //
         //  We can now build the charge string - append it to the existing string
         //
         parmp.count++;          // bump charge counter

         if (parmp.p9 == 1) {        // 9 hole round ?
            p9c = 9;
         } else {
            p9c = 18;
         }

         if (parmp.count > 1) {      // if others already exist

            parmp.poslist = parmp.poslist + ",";      // add comma for seperator
         }

         parmp.poslist = parmp.poslist + posid + ",";          // player id
         parmp.poslist = parmp.poslist + parmp.sdate + ",";    // date and time of tee time
         parmp.poslist = parmp.poslist + p9c + ",";             // 9 or 18 holes
         parmp.poslist = parmp.poslist + lname + ",";          // last name
         parmp.poslist = parmp.poslist + fname + ",,,,";       // first name - skip zip, phone, email
         parmp.poslist = parmp.poslist + mpos + ",";           // mship class code
         parmp.poslist = parmp.poslist + tpos;                 // charge code for item (caddy, cart, etc.)

         charges = true;                 //indicate charges built for this player
                 
         //
         //  Save charge data in pos_hist for reports
         //
         parmp.hist_posid = posid;
         if (!parmp.player.equals( "" )) {
            parmp.hist_player = parmp.player;          // if guest
         } else {
            parmp.hist_player = fname + " " + lname;   // else use member name
         }
         parmp.hist_price = "";
         parmp.hist_item_name = parmp.pcw;
         parmp.hist_item_num = tpos;
         
         add_POS_hist(parmp, con);       // go make the entry 
         
      }      // end of trans mode processing

      //
      //  if the player passed is a guest, charge the member for this too
      //
      if (!parmp.player.equals( "" )) {

         //
         //  First check if there is a charge code associated with this guest type
         //
         i = 0;
         loop3:
         while (i < parmp.MAX_Guests) {

            if (parmp.player.startsWith( parmp.gtype[i] )) {

               if (parmp.p9 == 0) {                  // if 18 hole round

                  gpos = parmp.gpos[i];               // get 18 hole charge

               } else {

                  gpos = parmp.g9pos[i];              // get 9 hole charge
               }
               break loop3;
            }
            i++;
         }

         if (!gpos.equals( "" )) {          // if pos charge found

            //
            //  We can now build the charge string - append it to the existing string
            //
            parmp.count++;          // bump charge counter

            if (parmp.p9 == 1) {        // 9 hole round ?
               p9c = 9;
            } else {
               p9c = 18;
            }

            if (parmp.count > 1) {      // if others already exist

               parmp.poslist = parmp.poslist + ",";      // add comma for seperator
            }

            parmp.poslist = parmp.poslist + posid + ",";           // player id
            parmp.poslist = parmp.poslist + parmp.sdate + ",";     // date and time of tee time
            parmp.poslist = parmp.poslist + p9c + ",";             // 9 or 18 holes
            parmp.poslist = parmp.poslist + lname + ",";          // last name
            parmp.poslist = parmp.poslist + fname + ",,,,";       // first name - skip zip, phone, email
            parmp.poslist = parmp.poslist + mpos + ",";           // mship class code
            parmp.poslist = parmp.poslist + gpos;                 // charge code for guest
                 
            charges = true;                 //indicate charges built for this player

            //
            //  Save charge data in pos_hist for reports
            //
            parmp.hist_posid = posid;
            parmp.hist_player = parmp.player;          // guest
            parmp.hist_price = "";
            parmp.hist_item_num = gpos;
            parmp.hist_item_name = "Guest Fee";

            add_POS_hist(parmp, con);               // go make the entry        
         }

      } // end of guest processing

   } catch (Exception exc) {

        Utilities.logError("Error in Proshop_sheet_checkin - exception in buildCharge(). Exc=" + exc.getMessage() );

   } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { pstmt.close(); }
        catch (SQLException ignored) {}

    }

   return(charges);
 }                   // end of buildCharge


 // *********************************************************
 //  Get member info
 // *********************************************************
 private void getMemInfo(parmPOS parmp, Connection con) {

    
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    String lname = "";

    try {

        pstmt = con.prepareStatement (
            "SELECT name_last, name_first, m_ship, posid FROM member2b WHERE username= ?");

        pstmt.clearParameters();
        pstmt.setString(1, parmp.user);

        rs = pstmt.executeQuery();

        if (rs.next()) {

            lname = rs.getString(1);
            parmp.fname = rs.getString(2);
            parmp.mship0 = rs.getString(3);
            parmp.posid = rs.getString(4);
        }

        parmp.lname = lname.replace("'", "-");    //  replace any single quotes (O'Brien = O-Brien)

    } catch (Exception exc) {

        Utilities.logError("Error in Proshop_sheet_checkin - exception in getMemInfo(). Exc=" + exc.getMessage() );

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { pstmt.close(); }
        catch (SQLException ignored) {}

    }

 }
 

 // *********************************************************
 //  Create POS History entry in pos_hist table
 // *********************************************************
 private static void add_POS_hist(parmPOS parmp, Connection con) {

    
    PreparedStatement pstmt = null;
    
    int waiting = 0;
    
    
    //
    //  If PSK (CPS) or IBS, then set the waiting flag when we are sending a charge.  This flag will be cleared (set to 0) when
    //  we receive a reply from CPS.  This is in case we do not receive a reply (timeout) and the user sends the charges
    //  aggain.  We only want to display the charges that recevied a reply in the reports.
    //
  //  if (parmp.posType.equals( "ClubProphetV3" ) || parmp.posType.equals( "IBS" )) {       // if Club Prophet or IBS POS
    if (parmp.posType.equals( "ClubProphetV3" )) {            // if Club Prophet (do IBS later - it might be different than CPS!!)
    
       if (!parmp.hist_item_num.equals( "POS Rejected Charge" ) && !parmp.hist_item_num.equals( "Invalid Request" )) {    // if not an error entry
          
          waiting = 1;      // sending a charge - set the waiting flag
       }
    }
    

    //
    //   Use the info in parmp to create a history entry
    //
    try {

        pstmt = con.prepareStatement (
                    "INSERT INTO pos_hist " +
                      "(date, time, course, fb, member_id, player, item_num, item_name, price, p9, date_time, waiting) " +
                    "VALUES " +
                      "(?,?,?,?,?,?,?,?,?,?,now(),?)");

        pstmt.clearParameters();
        pstmt.setLong(1, parmp.date);
        pstmt.setInt(2, parmp.time);
        pstmt.setString(3, parmp.course);
        pstmt.setInt(4, parmp.hist_fb);
        pstmt.setString(5, parmp.hist_posid);
        pstmt.setString(6, parmp.hist_player);
        pstmt.setString(7, parmp.hist_item_num);
        pstmt.setString(8, parmp.hist_item_name);
        pstmt.setString(9, parmp.hist_price);
        pstmt.setInt(10, parmp.p9);
        pstmt.setInt(11, waiting);

        pstmt.executeUpdate();

    } catch (Exception exc) {

        Utilities.logError("Proshop_sheet_checkin - exception from add_POS_hist. Error = " + exc.getMessage());

    } finally {

        try { pstmt.close(); }
        catch (SQLException ignored) {}

    }

 }

 
 
 // *********************************************************
 //  Clear the waiting flag(s) in pos_hist table for the charges just sent
 // *********************************************************
 private static void clearWaiting(parmPOS parmp, Connection con) {

    
    PreparedStatement pstmt = null;
    

    //
    //   Club Prohet Systems - clear the waiting flag when a reply is received
    //
    try {

        pstmt = con.prepareStatement (
                    "UPDATE pos_hist " +
                    "SET waiting = 0 " +
                    "WHERE waiting = 1 AND date = ? AND time = ? AND course = ? AND fb = ? AND member_id = ?");

        pstmt.clearParameters();
        pstmt.setLong(1, parmp.date);
        pstmt.setInt(2, parmp.time);
        pstmt.setString(3, parmp.course);
        pstmt.setInt(4, parmp.hist_fb);
        pstmt.setString(5, parmp.posid);

        pstmt.executeUpdate();

    } catch (Exception exc) {

        Utilities.logError("Proshop_sheet_checkin - exception from clearWaiting. Error = " + exc.getMessage());

    } finally {

        try { pstmt.close(); }
        catch (SQLException ignored) {}

    }

 }

 
 
 
 // ***************************************************************************
 //  Trace a POS transmission (send or response) for Club Prophet Systems V3 and IBS
 // ***************************************************************************
 private static void tracePOS(int type, String returnCode, parmPOS parmp, Connection con) {

    
    PreparedStatement pstmt = null;
    
    String player = "";
   
    int i = 0;
    //int max = 0;
    
    //
    //  Gather some trace info
    //
    if (!parmp.player.equals( "" )) {
       player = parmp.player;                      // if guest
    } else {
       player = parmp.fname + " " + parmp.lname;   // else use member name
    }
    
    //max = parmp.count;           //  get the number of charges
    
    //if (type == 2) max = 1;      // only trace once if this is a response from CPS

    String codes = "";

    List<String> codeList = parmp.codeA.getString(); 

    for (i = 0; i < codeList.size(); i++) {

       codes += codeList.get(i) + ", ";

    }

    if (i >= 1) codes = codes.substring(0, codes.length() - 2); // trim the last comma & space

    
    //
    //   Use the info in parmp to create a trace entry
    //
    try {

        pstmt = con.prepareStatement (
                    "INSERT INTO v5.pos_trace " +
                      "(club, date, time, course, fb, p9, pcw, type, returnCode, member_id, player, codes, count, date_time) " +
                    "VALUES " +
                      "(?,?,?,?,?,?,?,?,?,?,?,?,?,now())");

        pstmt.clearParameters();
        pstmt.setString(1, parmp.club);
        pstmt.setLong(2, parmp.date);
        pstmt.setInt(3, parmp.time);
        pstmt.setString(4, parmp.course);
        pstmt.setInt(5, parmp.hist_fb);
        pstmt.setInt(6, parmp.p9);
        pstmt.setString(7, parmp.pcw);
        pstmt.setInt(8, type); // 1 = send, 2 = response
        pstmt.setString(9, returnCode);
        pstmt.setString(10, parmp.posid);
        pstmt.setString(11, player);
        pstmt.setString(12, codes);
        pstmt.setInt(13, codeList.size()); // parmp.count

        pstmt.executeUpdate();

    } catch (Exception exc) {

        Utilities.logError("Proshop_sheet_checkin - exception in tracePOS method. Error = " + exc.getMessage());

    } finally {

        try { pstmt.close(); }
        catch (SQLException ignored) {}
    }
 }
 
 

 // *********************************************************
 //  Return a string with the specified length from a possibly longer field
 // *********************************************************

 private final static String truncate( String s, int slength ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [slength];


      if (slength < ca.length) {       // if string is longer than allowed

         for ( int i=0; i<slength; i++ ) {
            ca2[i] = ca[i];
         } // end for

      } else {

         return (s);
      }

      return new String (ca2);

 } // end truncate


 private static ArrayList<ArrayList<String>> buildXmlFileForIBS(parmPOS parm, String deptID, String tenderID, String taxID, Connection con) {

    
    double price = 0;
    double tax = 0;
    double total_tax = 0;
    double total_price = 0;

    int qty = 0;
    int trans_id = 0;

    String tmp[];
    String batch = "";

    ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

    int i = 0;

    try {

        for (i = 0; i < parm.charges.size(); i++) {

            result.add(new ArrayList<String>());
            result.get(result.size() - 1).add(parm.charges.get(i).get(0)); // plug in the posid

            tmp = parm.charges.get(i).get(0).split("-"); // this should go ok now since only members with proper pos ids are being added 2-28-13

            trans_id = logBatchFile(con);

            batch = "" +
                "<Root>" +
                    "<Details>" +
                        "<BatchDate>" + parm.sdate + "</BatchDate>" +
                        "<BatchName>ForeTees Batch</BatchName>" +
                        "<uidDeptID>" + deptID + "</uidDeptID>" +
                        "<BatchDetails>" +
                            "<TranID>" + trans_id + "</TranID>" +
                            "<MemberNumber>" + tmp[0] + "</MemberNumber>" +
                            "<MemberExtension>" + tmp[1] + "</MemberExtension>" +
                            "<EmplNumber>99</EmplNumber>";


            for (int i2 = 1; i2 < parm.charges.get(i).size(); i2++) {

                tmp = parm.charges.get(i).get(i2).split("\\|");

                try {

                    price = Double.parseDouble(tmp[1]);
                    tax = Double.parseDouble(tmp[2]);
                    qty = Integer.parseInt(tmp[3]);

                } catch (NumberFormatException ignore) { }

                batch += "" +
                     "<Items>" +
                        ((tmp[0].indexOf("-") == -1) ? "<InvNumber>" + tmp[0] + "</InvNumber>" : "<uidInvItemID>" + tmp[0] + "</uidInvItemID>") +
                        "<Price>" + price + "</Price>" +
                        "<Quantity>" + qty + "</Quantity>" +
                     "</Items>";

                total_tax += (tax * qty);
                total_price += (price * qty) + (tax * qty);

            }

            batch += "" +
                            "<Taxes>" +
                                "<uidTaxID>" + taxID + "</uidTaxID>" +
                                "<TaxAmount>" + total_tax + "</TaxAmount>" + // Math.scalb(total_tax, 2)
                            "</Taxes>" +
                            "<Tenders>" +
                                "<uidTenderID>" + tenderID + "</uidTenderID>" +
                                "<TenderAmount>" + total_price + "</TenderAmount>" + // Math.scalb(total_price, 2)
                            "</Tenders>" +
                        "</BatchDetails>" +
                    "</Details>" +
                "</Root>";

            result.get(result.size() - 1).add(batch);   // plug in the batch file

            //Utilities.logError("batch=" + batch + "<br>total_tax=" + total_tax + ", total_price=" + total_price);

            logBatchFile(trans_id, batch, con);

            //Utilities.logError("wrote log file for " + trans_id + " : " + i);

            total_tax = 0;
            total_price = 0;

        }

    } catch (Exception exc) {

        Utilities.logError("buildXmlFileForIBS: err=" + exc.toString());

    }

    return result;

 }


 // create a holder entry and return the record id
 private static int logBatchFile(Connection con) {

    int trans_id = 0;  // return value

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con.prepareStatement (
                    "INSERT INTO pos_batch_log " +
                      "(batch, date_time) " +
                    "VALUES " +
                      "(?, now())");

        pstmt.clearParameters();
        pstmt.setString(1, "holder");

        pstmt.executeUpdate();

        pstmt = con.prepareStatement("SELECT LAST_INSERT_ID()");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            trans_id = rs.getInt(1);
        }

    } catch (Exception exc) {

        Utilities.logError("Proshop_sheet_checkin.logBatchFile() Error = " + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { pstmt.close(); }
        catch (SQLException ignored) {}

    }

    return trans_id;

 }


 // place the batch file contents in the table using the id returned previously
 private static void logBatchFile(int trans_id, String batch, Connection con) {


    PreparedStatement pstmt = null;

    try {

        pstmt = con.prepareStatement (
                    "UPDATE pos_batch_log " +
                    "SET batch = ?, date_time = now() " +
                    "WHERE pos_batch_log_id = ?");

        pstmt.clearParameters();
        pstmt.setString(1, batch);
        pstmt.setInt(2, trans_id);

        pstmt.executeUpdate();

    } catch (Exception exc) {

        Utilities.logError("Proshop_sheet_checkin.logBatchFile(batch) id=" + trans_id + ", batch=" + batch + ", Error=" + exc.getMessage());

    } finally {

        try { pstmt.close(); }
        catch (SQLException ignored) {}

    }

 }

 
 
 //
 // set the POS flags to processed according to which charges were just sent
 //
 private static void setPOSprocessed(int sent1, int sent2, int sent3, int sent4, int sent5, int teecurr_id, Connection con) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    int pos1 = 0;
    int pos2 = 0;
    int pos3 = 0;
    int pos4 = 0;
    int pos5 = 0;
    
    //
    //  Get current pos values of players
    //
    try {

        pstmt = con.prepareStatement("" +
                "SELECT pos1, pos2, pos3, pos4, pos5 " +
                "FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, teecurr_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

           pos1 = rs.getInt("pos1");
           pos2 = rs.getInt("pos2");
           pos3 = rs.getInt("pos3");
           pos4 = rs.getInt("pos4");
           pos5 = rs.getInt("pos5");
        }

        pstmt.close();

    } catch (Exception e) {

       Utilities.logError("Error in Proshop_sheet_checkin.setPOSprocessed - exception getting pos values. Exc=" + e.getMessage() );

    } finally {

         try { rs.close(); }
         catch (SQLException ignored) {}

         try { pstmt.close(); }
         catch (SQLException ignored) {}
    }   
    
    // determine which players to flag as processed (this allows for multiple players' charges to be combined into one send)
    
    if (sent1 > 0 && pos1 == 0) {
       
       pos1 = 1;
    }
    if (sent2 > 0 && pos2 == 0) {
       
       pos2 = 1;
    }
    if (sent3 > 0 && pos3 == 0) {
       
       pos3 = 1;
    }
    if (sent4 > 0 && pos4 == 0) {
       
       pos4 = 1;
    }
    if (sent5 > 0 && pos5 == 0) {
       
       pos5 = 1;
    }
    
    
    try {

        pstmt = con.prepareStatement ("UPDATE teecurr2 SET pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ? WHERE teecurr_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, pos1);
        pstmt.setInt(2, pos2);
        pstmt.setInt(3, pos3);
        pstmt.setInt(4, pos4);
        pstmt.setInt(5, pos5);
        pstmt.setInt(6, teecurr_id);

        pstmt.executeUpdate();

    } catch (Exception exc) {

        Utilities.logError("Proshop_sheet_checkin.setPOSprocessed teecurr_id=" + teecurr_id + ", Error=" + exc.getMessage());

    } finally {

        try { pstmt.close(); }
        catch (SQLException ignored) {}
    }
 }
 
 

 public static void doRaceBrookCheckIn(int teecurr_id, int playerNum, String refImage, PrintWriter out, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     String custom_disp = "";
     String image = "";
     String title = "";
     
     //    Get current show values for the selected tee time
     try {

        pstmt = con.prepareStatement("" +
                "SELECT custom_disp" + playerNum + " " +
                "FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, teecurr_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            if (!rs.getString(1).equals("1")) {
                custom_disp = "1";
            } else {
                custom_disp = "0";
            }
        }

     } catch (Exception e) {

       Utilities.logError("Error in Proshop_sheet_checkin.doRaceBrookCheckIn - exception getting custom_disp data. Exc=" + e.toString() );

     } finally {

         try { rs.close(); }
         catch (SQLException ignored) {}

         try { pstmt.close(); }
         catch (SQLException ignored) {}
     }

     //    Get current show values for the selected tee time
     try {

        pstmt = con.prepareStatement("" +
                "UPDATE teecurr2 SET custom_disp" + playerNum + " = ? WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setString(1, custom_disp);
        pstmt.setInt(2, teecurr_id);
        
        pstmt.executeUpdate();

     } catch (Exception e) {

       Utilities.logError("Error in Proshop_sheet_checkin.doRaceBrookCheckIn - exception setting custom_disp data. Exc=" + e.toString() );

     } finally {

         try { rs.close(); }
         catch (SQLException ignored) {}

         try { pstmt.close(); }
         catch (SQLException ignored) {}
     }
     
     image = "/" +rev+ "/images/";
     title = "";
     
     if (custom_disp.equals("1")) {
         title = "Click here to set as a no-show for tee sheet display custom (blank).";       // if checked in
         image += "xbox.gif";
     } else {
         title = "Click here to check player in for tee sheet display custom(x).";            // NOT checked in
         image += "mtbox.gif";
     }
     
     // to toggle the image to checked in
     out.println("<script type=\"text/javascript\">");
     out.println(" parent.document.getElementById('"+refImage+"').src=\""+image+"\";");
     out.println(" parent.document.getElementById('"+refImage+"').title=\""+title+"\";");
     out.println("</script>");
 }

 
 
 

/*
 private static void addCharge(parmPOS parm, String posid, String invNumber, double price, double tax, int qty) {

    if (posid != null) {

        parm.charges.add(new ArrayList<String>());

        parm.charges.get(parm.charges.size() - 1).add(posid);

    }

    parm.charges.get(parm.charges.size() - 1).add(invNumber + "|" + price + "|" + tax + "|" + qty);

 }
*/
 /*
    parmPOS parm = new parmPOS();

    parm.charges.clear();
    parm.sdate = "2011-03-01";

    addCharge(parm, "6700", "100001", 12.77, .13, 1);
    addCharge(parm, null, "100002", 22.77, 2.13, 2);
    addCharge(parm, "6701", "100003", 32.77, 3.13, 3);
    addCharge(parm, "6702", "100004", 42.77, 4.13, 4);
    addCharge(parm, null, "100005", 52.77, 5.13, 5);
    addCharge(parm, null, "100006", 62.77, 6.13, 6);
    addCharge(parm, null, "100007", 72.77, 7.13, 7);
  */

} // end servlet public class
