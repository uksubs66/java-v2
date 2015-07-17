
/***************************************************************************************
 *   Common_slot:  Methods to generate slot page html
 *
 *
 *   called by:  Member_slotm
 *
 *   created:    14/12/2011   John K.
 *
 *   last updated:
 * 
 *         4/16/2014  Jefferson CC (jeffersoncountryclub) - Only display the "Event Guest" guest type when members are signing up for events (case 2408).
 *         3/04/2014  Governors Club (governorsclub) - Set default MoT to 'GCT' for all guest types.
 *         1/28/2014  El Camino CC (elcaminocc) - Set default MoT to 'GC' for all guest types.
 *        12/12/2013  Pecan Plantation (pecanplantation) - Set default MoT to 'W' for the 'No 5th Player' guest type.
 *        11/20/2013  The Country Club (tcclub) - Set default MoT to 'WLK' for all guest types.
 *        11/01/2013  Olympic Club (olyclub) - changed guest type "MNHGP w/guest" to "Member w/Guest" per club's request.
 *        10/30/2013  Sanctuary GC (sanctuarygc) - Set default MoT to "C" for all guest types.
 *        10/15/2013  Boca Woods CC (bocawoodscc) - Set default MoT for 'Reciprocal' to 'RCT', 'Immediate Family' to 'IFC', and all others to 'GCT' (case 2312).
 *         7/16/2013  Desert Mountain (desertmountain) - Updated guest custom to only allow the selection of "Event Guest" for events (case 2282).
 *         5/02/2013  Overlake G&CC (overlakegcc) - Set default MoT to 'WLK' for all guest types.
 *         1/17/2013  Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *         1/11/2013  verifyMassConsec will now send an email notification to the member the mass consec times are booked in the name of.
 *         1/02/2013  verifyMassConsec will now properly set the proNew or memNew value for each tee time.
 *         1/02/2013  Updated verifyMassConsec to add a history entry for each tee time booked.
 *        12/27/2012  Desert Mountain - setGuestTypes - split guest types based on course.
 *        12/25/2012  Added verifyMassConsec method to be used when booking 6-15 tee times.
 *        12/05/2012  Lakewood CC - CO (lakewood) - Set default MoT to "WA" for "Family Guest" and "Off Season Guest" guest types.
 *         9/06/2012  Updated outputTopNav calls to also pass the HttpServletRequest object.
 *         7/27/2012  Oronoque CC (oronoquecc) - Set default MoT to 'CRT' for all guest types (case 2178).
 *         5/22/2012  Do not build nav panels if from external login (event linnk in email).
 *         5/09/2012  Teton Pines (tetonpines) - Set default MoT to 'GC' for all guest types.
 *         3/13/2012  Congressional - only allow one guest type when beyond the member's normal days in advance.
 *         3/13/2012  Lakewood Ranch G&CC (lakewoodranch) - Set default MoT to 'CRT' for all guest types.
 *         1/20/2012  Westwood CC (westwoodcc) - Set default MoT to 'GC' for all guest types.
 *        12/14/2011  Created
 *
 *
 *
 ***************************************************************************************
 */
import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

// foretees imports
import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.nameLists;
import com.foretees.common.formUtil;
import com.foretees.common.verifySlot;
import com.foretees.common.parmSlotPage;
import com.foretees.common.parmCourse;
import com.foretees.common.parmSlot;
import com.foretees.common.parmSlotm;
import com.foretees.common.parmClub;
import com.foretees.common.reqUtil;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.getActivity;

public class Common_slot {

    public static void displaySlotPage(PrintWriter out, parmSlotPage parm, HttpServletRequest req, Connection con) {

        
        //
        //   Build the slot page - check if user came from an external login (a link in an email)
        //
        boolean ext_login = false;        // from external login

        if (reqUtil.getSessionString(req, "ext-user", null) != null || req.getParameter("ext-login") != null || req.getParameter("ext-dReq") != null) {    // make sure we check for both (we only need one, but somehow ended up using 2 parms for this)

            ext_login = true;           // indicate external login (from event link in email)
        }
        
            
        //
        //  Begin new-skin HTML page
        //
        int sess_activity_id = 0;
        try {
            sess_activity_id = getActivity.getRootIdFromActivityId(parm.activity_id, con);
        } catch(Exception e) {
            sess_activity_id = parm.activity_id;
        }
        
        Common_skin.outputHeader(parm.club, sess_activity_id, parm.page_title, true, out, req);
        Common_skin.outputBody(parm.club, sess_activity_id, out, req);
        if (ext_login == false) Common_skin.outputTopNav(req, parm.club, sess_activity_id, out, con);
        Common_skin.outputBanner(parm.club, sess_activity_id, parm.club_name, (String) parm.zip_code, out, req); // no zip code for Dining
        if (ext_login == false) Common_skin.outputSubNav(parm.club, sess_activity_id, out, con, req);
        Common_skin.outputPageStart(parm.club, sess_activity_id, out, req);
        Common_skin.outputBreadCrumb(parm.club, sess_activity_id, out, parm.bread_crumb, req);
        Common_skin.outputLogo(parm.club, sess_activity_id, out, req);

        Gson gson_obj = new Gson();
        
        

        //
        //  Output slot container.  javascript will build the slot page of passed parameters
        //

        out.println("<div class=\"slot_container\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(parm)) + "\"></div>");


        //
        //  End of new-skin HTML page
        //
        Common_skin.outputPageEnd(parm.club, sess_activity_id, out, req);
        out.close();

    }

    public static String slotJson(parmSlotPage parm) {
        Gson gson_obj = new Gson();
        if(parm.process_postback == null){
            // Slot Page start up (user is still in initial startup of slot page)
            return gson_obj.toJson(parm);
        } else {
            // Slot Page submit postback (user submitted slot page, and we're returning more information
            return gson_obj.toJson(parm.process_postback);
        }
    }

    public static void setTransportModes(parmSlotPage parm, parmCourse parmc) {
        // gather list of Non-Pro-Only tmodes locally
        for (int j = 0; j < parmc.tmode_limit; j++) {
            if (parmc.tmodea[j] != null && parmc.tOpt[j] == 0 && !parmc.tmodea[j].equals("") && !(parmc.tmodea[j] == null)) {
                parm.tmodes_list.add(parmc.tmodea[j]);
                //nonProCount++;
            }
        }
    }
    
    public static void setEventTransportModes(parmSlotPage parm, parmCourse parmc) {
        // gather list of Non-Pro-Only tmodes locally
        for (int j = 0; j < parmc.tmode_limit; j++) {
            if (parmc.tmodea[j] != null && !parmc.tmodea[j].equals("")) {
                parm.tmodes_list.add(parmc.tmodea[j]);
                //nonProCount++;
            }
        }
    }

    public static void setTransportLegend(parmSlotPage parm, parmCourse parmc, boolean new_skin) {
        // Set transport legend
        for (int i = 0; i < parmc.tmode_limit; i++) {
            if (parmc.tmodea[i] != null && (!parmc.tmodea[i].equals("") && parmc.tOpt[i] == 0)) {
                if (new_skin) {
                    parm.transport_legend += " <span>";
                }

                if (parm.club.equals("peninsula") && parmc.tmodea[i].equals("WLK")) {

                    parm.transport_legend += "WLK = <b>Walking Permitted Daily After 3:00 PM</b>";

                } else {

                    parm.transport_legend += "<span>" + parmc.tmodea[i] + "</span> = " + parmc.tmode[i];
                }
                if (new_skin) {
                    parm.transport_legend += "</span>";
                } else {
                    parm.transport_legend += "&nbsp;&nbsp;";
                }
            }

        }
        if (new_skin) {
            parm.transport_legend += " <span>(9 = 9 holes)</span>";
        } else {
            parm.transport_legend += "(9 = 9 holes)";
        }
    }

    public static void setDefaultTransportTypes(parmSlotPage parm) {

        boolean isTeeSlot = (parm.slot_url.equals("Member_slot") || parm.slot_url.equals("Member_slotm"));
        boolean isLotterySlot = (parm.slot_url.equals("Member_lott"));
        boolean isEventSlot = (parm.slot_url.equals("Member_events2"));
        boolean isDiningSlot = (parm.slot_url.equals("Dining_slot2") || parm.slot_url.equals("Dining_slot"));

        // Default member transport type
        if (isTeeSlot && parm.club.equals("merion") && parm.custom_caddie == true) {           // if Merion and Caddie to be the forced default
            parm.default_member_wc_override = "CAD";
        }

        if (isTeeSlot && parm.club.equals("marbellacc")) {
            parm.default_member_wc = "C";
        }

        // Set default guest transport type
        if (isTeeSlot && parm.club.equals("lakes")) {

            parm.guest_type_cw_map.put("_default_", "NON");      // set default Mode of Trans

        } else if (isTeeSlot && ((parm.club.equals("fortcollins") && parm.course.startsWith("Greeley")) || parm.club.equals("pmarshgc") || parm.club.equals("pinery")
                || parm.club.equals("trophyparm.clubcc") || parm.club.equals("midpacific") || parm.club.equals("estanciaparm.club") || parm.club.equals("omahacc") 
                || parm.club.equals("indianridgecc") || parm.club.equals("westwoodcc") || parm.club.equals("tetonpines") || parm.club.equals("elcaminocc") 
                || parm.club.equals("thedeuce") || parm.club.equals("thenationalgolfclub") || parm.club.equals("dmgcc") || parm.club.equals("bayclubstonetree"))) {

            parm.guest_type_cw_map.put("_default_", "GC");       // set default Mode of Trans

        } else if (isTeeSlot && parm.club.equals("redrocks")) {

            parm.guest_type_cw_map.put("_default_", "NA");       // set default Mode of Trans

        } else if (isTeeSlot && parm.club.equals("chartwellgcc") || parm.club.equals("bluebellcc")) {

            parm.guest_type_cw_map.put("_default_", "CAR");       // set default Mode of Trans

        } else if (isTeeSlot && parm.club.equals("admiralscove") || parm.club.equals("championhills")) {

            parm.guest_type_cw_map.put("_default_", "CF");       // set default Mode of Trans

        } else if (isTeeSlot && parm.club.equals("mediterra")) {

            parm.guest_type_cw_map.put("_default_", "R");      // set default Mode of Trans

        } else if (isTeeSlot && (parm.club.equals("marbellacc") || parm.club.equals("theparm.clubatnevillewood") || parm.club.equals("capecodnational") || parm.club.equals("sanctuarygc"))) {

            parm.guest_type_cw_map.put("_default_", "C");      // set default Mode of Trans

        } else if (((isLotterySlot || isTeeSlot) && parm.club.equals("rmgc")) || (isTeeSlot && (parm.club.equals("castlepines") || parm.club.equals("jonathanslanding") || parm.club.equals("gulfharbourgcc") || parm.club.equals("lakewoodcc")
                || parm.club.equals("sawgrass") || parm.club.equals("woodlandscountryparm.club") || parm.club.equals("tartanfields") || parm.club.equals("turnerhill")
                || parm.club.equals("theplantationgc") || parm.club.equals("lakewoodranch") || parm.club.equals("oronoquecc") || parm.club.equals("laplaya")))) {

            parm.guest_type_cw_map.put("_default_", "CRT");       // set default Mode of Trans

        } else if (isTeeSlot && parm.club.equals("mnvalleycc")) {

            parm.guest_type_cw_map.put("_default_", "WK");       // set default Mode of Trans

        } else if (isTeeSlot && (parm.club.equals("tpcsugarloaf") || parm.club.equals("governorsclub"))) {

            parm.guest_type_cw_map.put("_default_", "GCT");

        } else if (isTeeSlot && parm.club.equals("northridge")) {

            parm.guest_type_cw_map.put("_default_", "CC");

        } else if (isTeeSlot && (parm.club.equals("minnetonkacc") || parm.club.equals("olyparm.club") || parm.club.equals("mpccpb") || parm.club.equals("demobrad") || parm.club.equals("demolarry")
                || parm.club.equals("overlakegcc") || parm.club.equals("tcclub"))) {

            parm.guest_type_cw_map.put("_default_", "WLK");

        } else if ((isTeeSlot || isLotterySlot) && parm.club.equals("blackdiamondranch")) {

            parm.guest_type_cw_map.put("Turn Time", "TT");
            parm.guest_type_cw_map.put("_default_", "CCT");

        } else if (isTeeSlot && parm.club.equals("oceanreef")) {

            parm.guest_type_cw_map.put("_default_", "CT");

        } else if ((isTeeSlot || isLotterySlot) && parm.club.equals("dataw")) {

            parm.guest_type_cw_map.put("Blank", "TF");
            parm.guest_type_cw_map.put("Comp", "TF");
            parm.guest_type_cw_map.put("Experience Dataw", "TF");
            parm.guest_type_cw_map.put("Guest of Member", "CR");
            parm.guest_type_cw_map.put("Reciprocal", "CR");
            parm.guest_type_cw_map.put("Unaccompanied", "CR");
            parm.guest_type_cw_map.put("_default_", ""); // Use  default

        } else if (isTeeSlot && parm.club.equals("interlachen")) {

            parm.guest_type_cw_map.put("X - No Fivesome", "MC");
            parm.guest_type_cw_map.put("_default_", ""); // Use  default

        } else if (isTeeSlot && parm.club.equals("stcloudcc")) {

            parm.guest_type_cw_map.put("High School Apollo", "WLK");
            parm.guest_type_cw_map.put("College $$$", "WLK");
            parm.guest_type_cw_map.put("College - SCSU", "WLK");
            parm.guest_type_cw_map.put("_default_", ""); // Use  default

        } else if (isTeeSlot && parm.club.equals("tanoan")) {

            parm.guest_type_cw_map.put("_default_", "TC");

        } else if (isTeeSlot && parm.club.equals("capecodnational")) {

            parm.guest_type_cw_map.put("_default_", "C");

        } else if (isTeeSlot && parm.club.equals("wildcatruncc")) {

            parm.guest_type_cw_map.put("_default_", "CCT");

        } else if ((isTeeSlot || isLotterySlot) && parm.club.equals("ranchobernardo")) {

            parm.guest_type_cw_map.put("_default_", "CCH");

        } else if (isTeeSlot && (parm.club.equals("tcparm.club") || (parm.club.equals("merion") && parm.custom_caddie == true))) {

            parm.guest_type_cw_map.put("_default_", "CAD");

        } else if (isTeeSlot && parm.club.equals("roundhill")) {

            parm.guest_type_cw_map.put("_default_", "RHC");

        } else if (isTeeSlot && parm.club.equals("oceanreef")) {

            parm.guest_type_cw_map.put("_default_", "CT");

        } else if (isTeeSlot && parm.club.equals("silvercreekcountryparm.club")) {

            parm.guest_type_cw_map.put("_default_", "GR");

        } else if ((isTeeSlot || isLotterySlot) && parm.club.equals("braeburncc")) {

            parm.guest_type_cw_map.put("_default_", "NAP");

        } else if (isTeeSlot && parm.club.equals("cherrycreek")) {

            parm.guest_type_cw_map.put("_default_", "CI"); // Use  default

        } else if (isTeeSlot && parm.club.equals("foresthighlands")) {

            parm.guest_type_cw_map.put("PGA", "CMP");
            parm.guest_type_cw_map.put("Token", "CMP");
            parm.guest_type_cw_map.put("Tourney", "CMP");
            parm.guest_type_cw_map.put("Gift Certificate", "CMP");
            parm.guest_type_cw_map.put("Comp", "CMP");
            parm.guest_type_cw_map.put("Employee", "EMP");
            parm.guest_type_cw_map.put("_default_", ""); // Use  default

        } else if (isTeeSlot && parm.club.equals("burloaks")) {

            parm.guest_type_cw_map.put("Corp", "Car");
            parm.guest_type_cw_map.put("_default_", ""); // Use  default

        } else if (isTeeSlot && parm.club.equals("ccrockies")) {

            parm.guest_type_cw_map.put("_default_", "GCF");
            
        } else if (isTeeSlot && parm.club.equals("lakewood")) {

            parm.guest_type_cw_map.put("Family Guest", "WA");
            parm.guest_type_cw_map.put("Off Season Guest", "WA");

        } else if (isTeeSlot && parm.club.equals("bocawoodscc")) {

            parm.guest_type_cw_map.put("Reciprocal", "RCT");
            parm.guest_type_cw_map.put("Immediate Family", "IFC");
            parm.guest_type_cw_map.put("_default_", "GCT"); // Use  default
            
        } else if (isTeeSlot && parm.club.equals("pecanplantation")) {
          
            parm.guest_type_cw_map.put("No 5th Player", "W");
            
        } else if (isTeeSlot && parm.club.equals("coldspringcc")) {
            
            parm.guest_type_cw_map.put("_default_", "GLF");
            

        } else if (isTeeSlot && parm.club.equals("blackdiamondranch")) {
            
            parm.guest_type_cw_map.put("_default_", "CRT");
             

        } else if (isTeeSlot && parm.club.equals("winchestercc")) {
            
            parm.guest_type_cw_map.put("_default_", "INC");
             

        } else if (isTeeSlot && parm.club.equalsIgnoreCase("philcricket")) {
//            if (parm.course.equals("Militia Hill")) {
            parm.guest_type_cw_map.put("_default_", "WLK");
//            }
//            if (parm.course.equals("Militia Hill")) {
//                parm.guest_type_cw_map.put("_default_", "CAR");
//            }
        } else if (isTeeSlot && (parm.club.equals("lacumbrecc") || parm.club.equals("marincountryclub"))) {
            
            parm.guest_type_cw_map.put("_default_", "N/A");
            

        } else if (isTeeSlot && parm.club.equals("mosscreek")) {

            parm.guest_type_cw_map.put("10 Round", "ACP");
            parm.guest_type_cw_map.put("30 Day Renter", "ACP");
            parm.guest_type_cw_map.put("365 Day Renter", "ACP");
            parm.guest_type_cw_map.put("Comp", "ACP");
            parm.guest_type_cw_map.put("Discovery Package", "ACP");
            parm.guest_type_cw_map.put("Guest for a Day", "ACP");
            parm.guest_type_cw_map.put("M4D Dinner", "ACP");
            parm.guest_type_cw_map.put("M4D Lunch", "ACP");
            parm.guest_type_cw_map.put("Sunday Supper", "ACP");
            parm.guest_type_cw_map.put("Tournament", "ACP");
            parm.guest_type_cw_map.put("Accompanied", "CRT");
            parm.guest_type_cw_map.put("Junior", "CRT");
            parm.guest_type_cw_map.put("Local Reciprocal", "CRT");
            parm.guest_type_cw_map.put("Renter", "CRT");
            parm.guest_type_cw_map.put("Unaccom", "CRT");
            parm.guest_type_cw_map.put("Afternoon Rate", "PM");
            parm.guest_type_cw_map.put("_default_", ""); // Use  default
            
        } else if ((isTeeSlot || isLotterySlot) && parm.club.equals("gallerygolf")) {
            
            parm.guest_type_cw_map.put("Accompanied", "AG");
            parm.guest_type_cw_map.put("Unaccompanied", "UG");
            parm.guest_type_cw_map.put("Extended Family", "FAM");
            parm.guest_type_cw_map.put("Afternoon", "AFN");
            parm.guest_type_cw_map.put("Referred Guest", "WS");
            parm.guest_type_cw_map.put("Comp Golf", "COM");
            parm.guest_type_cw_map.put("Event", "EVE");
            parm.guest_type_cw_map.put("Outside Special", "OS");
            parm.guest_type_cw_map.put("Standard", "STD");
            parm.guest_type_cw_map.put("PGA/CartFee", "CF");
            parm.guest_type_cw_map.put("Troon Program", "TRN");
            parm.guest_type_cw_map.put("_default_", ""); // Use  default

        } else if (isTeeSlot && parm.club.equals("reserveclubatwoodside")) {
            
            if (parm.course.equals("Nicklaus Course")) {
                parm.guest_type_cw_map.put("_default_", "CT");
            }
            
            if (parm.course.equals("Zoeller Course")) {
                parm.guest_type_cw_map.put("_default_", "CRT");
            }

        } else {

            parm.guest_type_cw_map.put("_default_", "");  // Use  default

        }

        }

    public static void setGuestTypes(Connection con, parmSlotPage parm, parmClub clubParm) {
        setGuestTypes(con, parm, clubParm, null, null);
    }

    public static void setGuestTypes(Connection con, parmSlotPage parm, parmClub clubParm, parmSlot slotParms) {
        setGuestTypes(con, parm, clubParm, slotParms, null);
    }

    public static void setGuestTypes(Connection con, parmSlotPage parm, parmClub clubParm, parmSlotm slotmParms) {
        setGuestTypes(con, parm, clubParm, null, slotmParms);
    }

    public static void setGuestTypes(Connection con, parmSlotPage parm, parmClub clubParm, parmSlot slotParms, parmSlotm slotmParms) {

        String msubtype = "";
        ResultSet rs = null;
        long shortDate = parm.date - ((parm.date / 10000) * 10000);
        boolean isTeeSlot = (parm.slot_url.equals("Member_slot") || parm.slot_url.equals("Member_slotm"));
        boolean isLotterySlot = (parm.slot_url.equals("Member_lott"));
        boolean isEventSlot = (parm.slot_url.equals("Member_events2") || parm.slot_url.equals("Member_evntSignUp"));
        boolean isActivitySlot = (parm.slot_url.equals("Member_activity_slot"));
        boolean isDiningSlot = (parm.slot_url.equals("Dining_slot2") || parm.slot_url.equals("Dining_slot"));
        
        if(clubParm == null){
            return;
        }
        // Generate map of all guest types
        for (int i = 0; i < clubParm.MAX_Guests; i++) {

            if (clubParm.guest[i] != null && !clubParm.guest[i].equals("")) { 
                Map<String, Object> gtMap = new LinkedHashMap<String, Object>();
                gtMap.put("guest_type", clubParm.guest[i]);
                gtMap.put("guest_type_db", clubParm.gDb[i]);
                parm.guest_types_map_full.put("gt_" + i, gtMap);
            }

        }
        // Generate Map of guest types

        if (isEventSlot && (parm.club.equals("medinahcc") || parm.club.equals("olyclub"))) {

            Map<String, Object> gt_0 = new LinkedHashMap<String, Object>();
            gt_0.put("guest_type", "Event Guest");
            gt_0.put("guest_type_db", 0);
            parm.guest_types_map.put("gt_0", gt_0);
            return;

        }
        
        if (isEventSlot && parm.club.equals("jeffersoncountryclub")) {
            
            Map<String, Object> gt_0 = new LinkedHashMap<String, Object>();
            gt_0.put("guest_type", "Event Guest");
            gt_0.put("guest_type_db", 1);
            parm.guest_types_map.put("gt_0", gt_0);
            return;
        }
        
        if (isEventSlot && parm.club.equals("wellesley")) {

            Map<String, Object> gt_0 = new LinkedHashMap<String, Object>();
            gt_0.put("guest_type", "Tourney Guest");
            gt_0.put("guest_type_db", 0);
            parm.guest_types_map.put("gt_0", gt_0);
            return;

        }

        if (isTeeSlot && slotParms != null) {

            if (parm.club.equals("olyclub") || parm.club.equals("demov4")) {     // custom to make restricted guest types red and unavailable

                //  set up some parms for testing the guest types below
                slotParms.date = parm.date;
                slotParms.time = parm.time;
                slotParms.fb = parm.fb;
                slotParms.course = parm.course;
                slotParms.day = parm.day;
                slotParms.activity_id = 0;
                slotParms.members = 1;       // just check if restricted to zero guests per member or tee time
                slotParms.oldPlayer1 = "";   // set to empty for verifySlot processing 
                slotParms.oldPlayer2 = "";
                slotParms.oldPlayer3 = "";
                slotParms.oldPlayer4 = "";
                slotParms.oldPlayer5 = "";
                slotParms.player2 = "";
                slotParms.player3 = "";
                slotParms.player4 = "";
                slotParms.player5 = "";
            }
        }

        if (isTeeSlot && parm.club.equals("interlachen")) {

            try {

                //
                //  Get the member sub-type for this user
                //
                PreparedStatement pstmt1 = con.prepareStatement(
                        "SELECT msub_type FROM member2b WHERE username = ?");

                pstmt1.clearParameters();        // clear the parms
                pstmt1.setString(1, parm.user);
                rs = pstmt1.executeQuery();      // execute the prepared stmt

                if (rs.next()) {

                    msubtype = rs.getString(1);
                }

                pstmt1.close();
            } catch (Exception exc) {
            }
        }

        for (int i = 0; i < clubParm.MAX_Guests; i++) {

            if (clubParm.guest[i] != null && !clubParm.guest[i].equals("") && clubParm.gOpt[i] == 0) {   // if guest name is open for members

                boolean skipGuestType = false;

                // skip lottery guest types if Brookhaven - not allowed in lottery reqs
                if (isLotterySlot && parm.club.equals("brookhavenclub")) {
                    skipGuestType = true;
                }

                //
                //  If Merion, then skip "A rate Guest" guest type if this member is a "House" mship type
                //
                if (isTeeSlot && (parm.club.equals("merion") && parm.mship.equalsIgnoreCase("House"))) {

                    if (clubParm.guest[i].equalsIgnoreCase("A rate Guest")) {

                        skipGuestType = true;
                    }
                }
                //
                //  If Interlachen, then skip "Guest-Centennial" guest type if this member is NOT a "Member Guest Pass" msub type
                //
                if (isTeeSlot && (parm.club.equals("interlachen") && !msubtype.equalsIgnoreCase("Member Guest Pass"))) {

                    if (clubParm.guest[i].equalsIgnoreCase("Guest-Centennial")) {

                        skipGuestType = true;
                    }
                }
                //
                //  If Royal Oaks Dallas, then skip "Weekday 1" guest type if Friday, Sat or Sun
                //
                if (isTeeSlot && (parm.club.equals("roccdallas") && (parm.day.equals("Friday") || parm.day.equals("Saturday") || parm.day.equals("Sunday")))) {

                    if (clubParm.guest[i].startsWith("Weekday")) {

                        skipGuestType = true;
                    }
                }

                if (isTeeSlot && parm.club.equals("shadycanyongolfclub")) {

                    if (((parm.day.equals("Friday") || parm.day.equals("Saturday") || parm.day.equals("Sunday")) && clubParm.guest[i].equalsIgnoreCase("Tues-Thurs GST"))
                            || ((parm.day.equals("Monday") || parm.day.equals("Tuesday") || parm.day.equals("Wednesday") || parm.day.equals("Thursday")) && clubParm.guest[i].equalsIgnoreCase("Fri-Sun/Holiday GST"))) {

                        skipGuestType = true;
                    }
                }

                if (isTeeSlot && parm.club.equals("willowridgecc")) {

                    if (((parm.day.equals("Friday") || parm.day.equals("Saturday") || parm.day.equals("Sunday")) && clubParm.guest[i].equalsIgnoreCase("WkDay GST"))
                            || ((parm.day.equals("Monday") || parm.day.equals("Tuesday") || parm.day.equals("Wednesday") || parm.day.equals("Thursday")) && clubParm.guest[i].equalsIgnoreCase("WkEnd GST"))) {

                        skipGuestType = true;
                    }
                }

                if (isTeeSlot && parm.club.equals("talbotcc")) {

                    if (((clubParm.guest[i].equals("Wkday Guest") || clubParm.guest[i].equals("Fam Wkday")) && (parm.day.equals("Saturday") || parm.day.equals("Sunday")))
                            || (clubParm.guest[i].equals("Wkend Guest") || clubParm.guest[i].equals("Fam Wkend")) && (parm.day.equals("Monday") || parm.day.equals("Tuesday")
                            || parm.day.equals("Wednesday") || parm.day.equals("Thursday") || parm.day.equals("Friday"))) {

                        skipGuestType = true;
                    }
                }


                if (isTeeSlot && parm.club.equals("congressional")) {   // if tee time and Congressional
                    
                    //
                    //  If beyond normal days in advance, then only allow the "30 Day Advance Guest" guest type
                    //
                    int ind = Integer.parseInt(slotParms.index);    // get days in advance for this tee time
                        
                    if (ind > 9 && !clubParm.guest[i].startsWith("30 Day Advance")) {   // if outside the normal days in advance and NOT advance guest type
                        
                        skipGuestType = true;
                    }
                }
                
                
                //
                //  If Olympic Club, then only display guest types for the specified course
                //
                if (isTeeSlot && parm.club.equals("olyclub")) {

                    if (parm.course.equals("Lake")) {

                        if (clubParm.guest[i].startsWith("Coach Staff")
                                || clubParm.guest[i].startsWith("Junior GWL") || clubParm.guest[i].equals("MHGP Guest Outing")
                                || clubParm.guest[i].startsWith("Member w/Guest") || clubParm.guest[i].equals("Twilight Gst")) {

                            skipGuestType = true;
                        }

                    } else if (parm.course.equals("Cliffs")) {

                        if (clubParm.guest[i].equals("Clergy") || clubParm.guest[i].startsWith("Coach Staff")
                                || clubParm.guest[i].startsWith("Junior GWL") || clubParm.guest[i].equals("MHGP Guest Outing")
                                || clubParm.guest[i].startsWith("Twilight Gst") || clubParm.guest[i].equals("Unacc Low Fee")
                                || clubParm.guest[i].equals("Unacc Gst") || clubParm.guest[i].equals("USGA")) {

                            skipGuestType = true;
                        }

                    } else if (clubParm.guest[i].equals("Twilight Gst")) {     // Twilight Gst is only used after 4:00 PM

                        int twilightTime = 0;
                        
                        int yy = parm.date / 10000;
                        int mm = (parm.date - (yy * 10000)) / 100;
                        int dd = parm.date - ((yy * 10000) + (mm * 100));
                        
                        Calendar cal2 = new GregorianCalendar();

//                        cal2.add(Calendar.DATE, Integer.parseInt(parm.index));
                        cal2.set(yy, mm - 1, dd);

                        boolean isDST = false;

                        if (cal2.get(Calendar.DST_OFFSET) != 0) {
                            isDST = true;
                        }

                        if (isDST) {

                            if (shortDate <= 831) {
                                twilightTime = 1600;
                            } else {
                                twilightTime = 1500;
                            }

                        } else {
                            twilightTime = 1400;
                        }

                        if (parm.time < twilightTime) {
                            skipGuestType = true;
                        }
                    }

                    if ((clubParm.guest[i].equals("MHGP w/guest") && !parm.mship.equals("MHGP")) || clubParm.guest[i].equals("Event Guest")) {

                        skipGuestType = true;
                    }
                }      // end of IF olyclub
                
                //
                //  Desert Mountain - seperate the guest types based on course name
                //
                if (parm.club.equals("desertmountain")) {     // split by course name

                    if (isTeeSlot || isLotterySlot) {
                        
                        // For tee times, only display guest types specific to the current course
                        if (parm.course.equals("Apache") && !clubParm.guest[i].startsWith("A-")) {

                            skipGuestType = true;

                        } else if (parm.course.equals("Chiricahua") && !clubParm.guest[i].startsWith("CH-")) {

                            skipGuestType = true;

                        } else if (parm.course.equals("Cochise") && !clubParm.guest[i].startsWith("C-")) {

                            skipGuestType = true;

                        } else if (parm.course.equals("Geronimo") && !clubParm.guest[i].startsWith("G-")) {

                            skipGuestType = true;

                        } else if (parm.course.equals("Outlaw") && !clubParm.guest[i].startsWith("O-")) {

                            skipGuestType = true;

                        } else if (parm.course.equals("Renegade") && !clubParm.guest[i].startsWith("R-")) {

                            skipGuestType = true;                            
                        }
                    } else {
                        
                        // For events, only display the "Event Guest" guest type
                        if (!clubParm.guest[i].equalsIgnoreCase("Event Guest")) {
                            
                            skipGuestType = true;
                        }
                    }
                }    // end of IF Desert Mtn
                
                //Only display guest type Tournament Guest on Member side for events
                if (parm.club.equals("meridianhillscc")) {
                    if (isEventSlot && !clubParm.guest[i].equalsIgnoreCase("Tournament Guest")) {
                        skipGuestType = true;
                    } 
                }
                
                    
                
                

                if (!skipGuestType) {
                    Map<String, Object> gtMap = new LinkedHashMap<String, Object>();
                    
                    gtMap.put("guest_type", clubParm.guest[i]);
                    gtMap.put("guest_type_db", clubParm.gDb[i]);

                    if (isTeeSlot && slotParms != null && (parm.club.equals("olyclub") || parm.club.equals("demov4"))) {     // custom to mark some guest types disabled

                        slotParms.player1 = clubParm.guest[i];   // check this guest type
                        slotParms.g1 = clubParm.guest[i];
                        boolean olyclubGR = false;

                        try {

                            olyclubGR = verifySlot.checkMaxGuests(slotParms, con);   // check this guest type to see if not allowed at all

                        } catch (Exception ignore) {
                        }

                        if (olyclubGR == true) {    // if restricted - mark it disabled

                            gtMap.put("guest_type_disabled", true);

                        }
                    }
                    parm.guest_types_map.put("gt_" + i, gtMap);
                }
            }
        }
    }
    
    
    /**
     * Bypasses normal processing to book 6-15 tee times in as quick a manner as possible.
     * Called by both Proshop_slotm and Member_slotm
     * @param parm Parm block containing tee time data
     * @param username Username of member the tee times are to be booked in the name of
     * @param user Username of current user (Proshop or Member).  If member, should match 'username'
     * @param con
     * @return 
     */
    public static boolean verifyMassConsec(parmSlotm parm, String username, String user, Connection con) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String player1 = "";
        String player2 = "X";
        String player3 = "X";
        String player4 = "X";
        String player5 = "X";
        String mNum = "";
        String wc = "";
        String rest5 = "";
        String fullName = "";
        
        int fives = 1;
        int count = 0;
        int related_id = 0;;
        int proNew = 0;
        int memNew = 0;
        
        boolean success = true;
        
        int [] times = new int[15];
        
        // Plug times into an array
        times[0] = parm.time1;
        times[1] = parm.time2;
        times[2] = parm.time3;
        times[3] = parm.time4;
        times[4] = parm.time5;
        times[5] = parm.time6;
        times[6] = parm.time7;
        times[7] = parm.time8;
        times[8] = parm.time9;
        times[9] = parm.time10;
        times[10] = parm.time11;
        times[11] = parm.time12;
        times[12] = parm.time13;
        times[13] = parm.time14;
        times[14] = parm.time15;
        
        if (ProcessConstants.isProshopUser(user)) {
            proNew = 1;
        } else {
            memNew = 1;
        }
        
        
        // Verify that all tee times are still owned by the current user
        checkTimes:
        for (int i = 0; i < 15; i++) {

            if (times[i] != 0) {
                try {

                    pstmt = con.prepareStatement("SELECT teecurr_id FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND player1 = '' AND in_use > 0 AND in_use_by = ?");
                    pstmt.clearParameters();
                    pstmt.setLong(1, parm.date);
                    pstmt.setInt(2, times[i]);
                    pstmt.setInt(3, parm.fb);
                    pstmt.setString(4, parm.course);
                    pstmt.setString(5, user);

                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        if (i == 0) {
                            related_id = rs.getInt(1);
                        }
                    } else {
                                              
                        success = false;              
                        break checkTimes;
                    }

                } catch (Exception exc) {
                    success = false;
                    Utilities.logDebug("BSK", "Common_slot.verifyMassConsec - " + parm.club + " - Error verifying ownership of times: (" + parm.date + ", " + times[i] + ", " + parm.course + ", " + parm.fb + ") - ERR: " + exc.toString());
                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }
            }
        }
             
        // Don't bother continuing if we couldn't verify all the times
        if (success) {
          
            // Look up fives value from clubparm2 for this course
            try {

                pstmt = con.prepareStatement("SELECT CONCAT(name_first, ' ', IF(name_mi <> '', CONCAT(name_mi, ' '), ''), name_last) as display_name, memNum, wc FROM member2b WHERE username = ?");
                pstmt.clearParameters();
                pstmt.setString(1, username);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    player1 = rs.getString("display_name");
                    mNum = rs.getString("memNum");
                    
                    // If parm selection was passed from proshop side, use that, otherwise use member default
                    if (!parm.pcw1.equals("")) {
                        wc = parm.pcw1;
                    } else {
                        wc = rs.getString("wc");             
                    }
                }

            } catch (Exception exc) {
                success = false;
                Utilities.logDebug("BSK", "Common_slot.verifyMassConsec - " + parm.club + " - Error looking up member details for username (" + username + ") - ERR: " + exc.toString());
            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}
            }       

            // If member didn't have a default MoT set, look up the first non-pro only one from clubparm2 for this course
            if (wc.equals("")) {

                try {

                    pstmt = con.prepareStatement("SELECT * FROM clubparm2 WHERE courseName = ?");
                    pstmt.clearParameters();
                    pstmt.setString(1, parm.course);

                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        for (int i = 1; i < 16; i++) {
                            if (!rs.getString("tmodea" + i).equals("") && rs.getInt("tOpt" + i) == 0) {
                                wc = rs.getString("tmodea" + i);
                                break;
                            }
                        }
                    }

                } catch (Exception exc) {
                    Utilities.logDebug("BSK", "Common_slot.verifyMassConsec - " + parm.club + " - Error looking up default WC option for (" + parm.course + ") - ERR: " + exc.toString());
                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }
            }
       
            // Look up fives value from clubparm2 for this course
            try {

                pstmt = con.prepareStatement("SELECT fives FROM clubparm2 WHERE courseName = ?");
                pstmt.clearParameters();
                pstmt.setString(1, parm.course);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    fives = rs.getInt(1);
                }

            } catch (Exception exc) {
                fives = 1;
                Utilities.logDebug("BSK", "Common_slot.verifyMassConsec - " + parm.club + " - Error looking up fivesome allowance " + (parm.course.equals("") ? "(not-multi course)" : "(" + parm.course + ")") + " - ERR: " + exc.toString());
            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}
            }       

            // If we got a name for player1
            if (!player1.equals("")) {

                bookTimes:
                for (int i = 0; i < parm.slots; i++) { 

                    rest5 = "";
                    count = 0;

                    // Only continue if this time is populated in the array
                    if (times[i] != 0) {

                        // If fivesomes allowed on this course, look up whether or not a five-some restriction covers this time (Member side only)
                        if (fives == 1 && !ProcessConstants.isProshopUser(user)) {

                            try {

                                pstmt = con.prepareStatement("SELECT rest5 FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");
                                pstmt.clearParameters();
                                pstmt.setLong(1, parm.date);
                                pstmt.setInt(2, times[i]);
                                pstmt.setInt(3, parm.fb);
                                pstmt.setString(4, parm.course);

                                rs = pstmt.executeQuery();

                                if (rs.next()) {
                                    rest5 = rs.getString(1);
                                }

                            } catch (Exception exc) {
                                rest5 = "";
                                Utilities.logDebug("BSK", "Common_slot.verifyMassConsec - " + parm.club + " - Error looking up fivesome restriction for tee time: (" + parm.date + ", " + times[i] + ", " + parm.course + ", " + parm.fb + ") - ERR: " + exc.toString());
                            } finally {

                                try { rs.close(); }
                                catch (Exception ignore) {}

                                try { pstmt.close(); }
                                catch (Exception ignore) {}
                            }
                        }

                        // Clear out player5 value if fivesomes not allowed for this time, or set it to "X" if they are.
                        if (fives == 0 || (!rest5.equals("") && !ProcessConstants.isProshopUser(user))) {
                            player5 = "";
                        } else {
                            player5 = "X";
                        }

                        try {

                            pstmt = con.prepareStatement(
                                    "UPDATE teecurr2 "
                                    + "SET last_mod_date = now(), "
                                    + "player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, "
                                    + "username1 = ?, username2 = '', username3 = '', username4 = '', username5 = '', "
                                    + "p1cw = ?, p2cw = '', p3cw = '', p4cw = '', p5cw = '', "
                                    + "mNum1 = ?, mNum2 = '', mNum3 = '', mNum4 = '', mNum5 = '', "
                                    + "userg1 = '', userg2 = '', userg3 = '', userg4 = '', userg5 = '', "
                                    + "guest_id1 = '', guest_id2 = '', guest_id3 = '', guest_id4 = '', guest_id5 = '', "
                                    + "orig_by = ?, p91 = 0, p92 = 0, p93 = 0, p94 = 0, p95 = 0, "
                                    + "pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0, pos5 = 0, "
                                    + "custom_disp1 = '', custom_disp2 = '', custom_disp3 = '', custom_disp4 = '', custom_disp5 = '', "
                                    + "custom_string = '', custom_int = '', "
                                    + "tflag1 = '', tflag2 = '', tflag3 = '', tflag4 = '', tflag5 = '', "
                                    + "orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ?, "
                                    + "related_id = ?, in_use = 0, proNew = ?, memNew = ? "
                                    + "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                            pstmt.clearParameters();
                            pstmt.setString(1, player1);
                            pstmt.setString(2, player2);
                            pstmt.setString(3, player3);
                            pstmt.setString(4, player4);
                            pstmt.setString(5, player5);
                            pstmt.setString(6, username);
                            pstmt.setString(7, wc);
                            pstmt.setString(8, mNum);
                            pstmt.setString(9, username);
                            pstmt.setString(10, username);
                            pstmt.setString(11, username);
                            pstmt.setString(12, username);
                            pstmt.setString(13, username);
                            pstmt.setString(14, username);
                            pstmt.setInt(15, related_id);
                            pstmt.setInt(16, proNew);
                            pstmt.setInt(17, memNew);


                            pstmt.setLong(18, parm.date);
                            pstmt.setInt(19, times[i]);
                            pstmt.setInt(20, parm.fb);
                            pstmt.setString(21, parm.course);

                            count = pstmt.executeUpdate();

                            if (count == 0) {
                                success = false;
                                Utilities.logDebug("BSK", "Common_slot.verifyMassConsec - " + parm.club + " - Error booking tee time (TEST): (" + parm.date + ", " + times[i] + ", " + parm.course + ", " + parm.fb + ")");
                                break bookTimes;
                            } else {
                                
                                if (ProcessConstants.isProshopUser(user)) {
                                    fullName = "Proshop User";
                                } else {
                                    fullName = player1;
                                }
 
                                // Add a history entry for this time
                                SystemUtils.updateHist(parm.date, parm.day, times[i], parm.fb, parm.course, player1, player2, player3,
                                        player4, player5, user, fullName, 0, con);
                            }

                        } catch (Exception exc) {
                            success = false;
                            Utilities.logDebug("BSK", "Common_slot.verifyMassConsec - " + parm.club + " - Error booking tee time: (" + parm.date + ", " + times[i] + ", " + parm.course + ", " + parm.fb + ") - ERR: " + exc.toString());
                        } finally {

                            try { pstmt.close(); }
                            catch (Exception ignore) {}
                        }
                    }
                }
            }
        }
        
        if (success) {
            
            //
            //  allocate a parm block to hold the email parms
            //
            parmEmail parme = new parmEmail();          // allocate an Email parm block

            //
            //  Set the values in the email parm block
            //
            parme.activity_id = 0;
            parme.club = parm.club;
            parme.guests = 0;
            parme.type = "masstee";         // type = tee time
            parme.date = parm.date;
            parme.time = parm.time1;
            parme.fb = 0;
            parme.mm = (int) parm.mm;
            parme.dd = (int) parm.dd;
            parme.yy = (int) parm.yy;
            parme.etype = 0;
            parme.groups = parm.slots;

            parme.user = user;
            parme.emailNew = 1;
            parme.emailMod = 0;
            parme.emailCan = 0;

            parme.p91 = 0;
            parme.p92 = 0;
            parme.p93 = 0;
            parme.p94 = 0;
            parme.p95 = 0;

            parme.course = parm.course;
            parme.day = parm.day;
            parme.notes = "";

            parme.player1 = player1;
            parme.player2 = player2;
            parme.player3 = player3;
            parme.player4 = player4;
            parme.player5 = player5;

            parme.user1 = parm.user1;
            parme.user2 = "";
            parme.user3 = "";
            parme.user4 = "";
            parme.user5 = "";

            parme.pcw1 = wc;
            parme.pcw2 = "";
            parme.pcw3 = "";
            parme.pcw4 = "";
            parme.pcw5 = "";

            //
            //  Send the email
            //
            sendEmail.sendIt(parme, con);
        }
        
        return(success);
    }
}  // end of class
