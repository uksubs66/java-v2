/***************************************************************************************
 *   alphaTable:  This servlet will output the member name selection tables.
 *
 *
 *   created:  12/09/2004   Bob P.
 *
 *
 *   last updated:
 *
 *      11/01/13   CC of York (ccyork) - Display usernames instead of member numbers next to names in the name list (case 2319).
 *      11/01/13   Olympic Club (olyclub) - changed guest type "MNHGP w/guest" to "Member w/Guest" per club's request.
 *       7/16/13   Desert Mountain (desertmountain) - Updated guest custom to only allow the selection of "Event Guest" for events (case 2282).
 *       6/20/13   Denver CC (denvercc) - Added custom to display Dependent mtype names in green in the nameList_simple.
 *       4/23/13   Increased size of arrays in nameList from 20 to 40 to fix dersertmoutain exceeding 20
 *       2/20/13   Denver CC (denvercc) - Added custom to colorcode dependents green in the name list.
 *       1/31/13   Desert Mountain (desertmountain) - Updated nameList to sort by the new member2b "sort_by" field, which gets populated each morning after the sync runs.
 *       1/13/13   Fixed previous alphTable change from 1/10
 *       1/10/13   Updated alphaTable to use onchange instead of onclick for guest list
 *       1/04/13   Desert Mountain (desertmountain) - Updated nameList to order names by member number and username instead of alphabetically.
 *      12/27/12   Desert Mountain (desertmountain) - guestList - split guest types based on course.
 *      12/21/12   Desert Mountain (desertmountain) - do custom name list to identify the family members (case 2208).
 *       4/30/12   Bearpath CC (bearpath) - Display usernames instead of member numbers next to member's names for Proshop users (case 2148).
 *      11/17/11   Added ghin and gender to guest json result
 *      11/17/11   Added map generation to guestdbList for use with new skin
 *      10/14/11   Hillwood CC (hillwoodcc) - Display username instead of member number next to name (case 2051).
 *      10/07/11   Updated nameList to pass an includeInact boolean to allow for inactive members to be included or not.
 *       9/20/11   Olympic Club (olyclub) - Updated custom guest processing to show/hide 'Twilight Gst' based on DST and date.
 *       5/31/11   Olympic Club - include the guest types according to the course.  Also, add the member sub-type in nameList.
 *       4/05/11   Princess Anne CC (princessannecc) - Display username instead of member number next to name.
 *       4/01/11   In guestList method - change onChange to onBlur for mobile (iPad) devices - allows multiple simultaneous selections.
 *      12/03/10   Monterey Peninsula CC (mpccpb) - Updated custom to work off username values instead of member types (case 1916).
 *      12/02/10   Monterey Peninsula CC (mpccpb) - Display custom name-list as done with Oswego Lake custom (case 1916).
 *      11/01/10   Updated guestList - moved the 'X' to its own table like we did on member side.
 *       9/23/10   Updated guestdbList to pass -99 as the guest_id for 'TBA' when on mobile side (previously was 0)
 *       9/21/10   Changes to guestdbList to accommodate for Hotel users in the guest tracking system.
 *       9/10/10   Added quick search box to guestdb list (non-mobile callers only)
 *       9/08/10   Updated guestdbList() method to allow for more modularity when called from different locations
 *       6/24/10   Modified nameList & guestList methods to accept the new enableAdvAssist parameter which is used for iPad compatability
 *       6/16/10   Check to see if 'TBA' option is allowed when creating guest tracking select box
 *       5/18/10   Congressional CC (congressional) - update sorting custom to use last_only value instead of name_last (case 1181)
 *       3/24/10   Added guestdbList method
 *       3/23/10   Add new 'member_side' boolean parameter when calilng guestList - if true the list will not display pro-only gtypes
 *       3/19/10   Central Washington Chapter PGA (cwcpga) - Display ghin # in name list
 *       1/20/09   Druid Hills GC (dhgc) - Display tflag in the name list for anyone with a tflag other than 'GOLF'
 *      12/31/09   Pinehurst CC - nameList method - do custom name list like we did for Oswego Lake and Medinah (case 1767).
 *      12/17/09   Change to nameList_simple to allow the use of the enter key within the select box to function
 *      12/10/09   Change 'Quick Search Box' text to 'Type Last Name' in nameList_simple()
 *      12/02/09   Added displayPartnerList() method as a common method call to display the partner list
 *      11/19/09   Added option to nameList_simple method call to determine whether or not mship will be included in the name list
 *      10/06/09   MountainGate CC (mtngatecc) - no longer display posid in name list instead of mnum
 *       9/14/09   Added nameList_simple method that returns a greatly simplified version of the namelist.
 *       9/04/09   Minor db clean up added - change calls to verifySlot.logError to use Utilities.logError
 *       8/26/09   MountainGate CC (mtngatecc) - display posid in name list instead of mnum
 *       7/30/09   Disable the enter key within the Quick Select box and add
 *                 support for hitting enter within select box to move names over
 *       6/12/09   Modified guestList method to return ativity specific guests if activity_id provided
 *       4/16/09   Add logerror calls to exceptions and finalize the try-catches.
 *       4/02/09   Baltimore - REMOVE custom to hide one guest type on Fri & Sat (case 1463).
 *       2/10/09   added nameList2 method
 *      11/04/08   Added ghin and gender to nameList option values
 *      10/13/08   Change the query string for member2b so that all family members are listed together.
 *                 Last names with suffixes appended were being listed out of order.
 *      10/08/08   Loxahatchee - Display username instead of member number next to name.
 *       9/18/08   Charlotte CC - Display username instead of member number next to name.
 *       9/09/08   Always display the member number next to name as all clubs should benefit from this.
 *       8/22/08   Charlotte CC - display the member number next to each member name (case 1536).
 *       6/07/08   Los Coyotes - change the flags on the member names (case 1482).
 *       4/25/08   Baltimore - hide one guest type on Fri & Sat (case 1463).
 *       9/18/07   Congressional - sort member names by last, first, mi (case 1181)
 *       9/18/07   Belle Haven CC - display the member number next to each member name (case 1253).
 *       8/28/07   Tavistock CC - display the member number next to each member name (case 1232).
 *       8/28/07   Mirasol CC - display the member number next to each member name (case 1227).
 *       7/19/07   Do not include members that are 'excluded' (new billable flag in member2b).
 *       6/01/07   Los Coyotes - remove this - change the order that names are listed.
 *       5/31/07   Los Coyotes - change the order that names are listed.
 *       5/24/07   Blackhawk CC (CA) - display the member number next to each member name (case 1177).
 *       5/11/07   CC of Virginia - do custom name list like Medinah (case 1166).
 *       4/27/07   Muirfield - display the member number next to each member name (case 1145).
 *       4/27/07   Congressional CC - display the member number next to each member name (case 1153).
 *       4/20/07   Los Coyotes - add gender to end of name for display - nameList (case #1120).
 *       4/09/07   Do not show the X under guestList for lotteries
 *       4/06/07   Do not include members that are inactive (new inact flag in member2b).
 *       3/28/07   CC of Virginia - add custom to add mnum to end of name for display only.
 *       2/15/07   Updated nameList method to accept new boolean ghinOnly for only showing members w/ ghin #
 *       2/07/07   Change Greeley CC to Fort Collins (they share).
 *       1/16/07   Greeley CC - add custom to add mnum to end of name for display only.
 *       1/11/07   Royal Oaks Houston - add custom to add mnum to end of name for display only.
 *       9/11/06   Wellesley - add custom to force 'Tourney Guest' as the only guest type.
 *       7/18/06   nameList method - do custom name list for Oswego Lake like we did for Medinah.
 *
 ***************************************************************************************
 */
package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;

public class alphaTable {

    private static String rev = ProcessConstants.REV;

    /**
    //************************************************************************
    //
    //  Output Table for Member Name Selection
    //
    //      called by:  Member_slot
    //                  Member_lott         (not yet)
    //                  Member_evntSignUp   (not yet)
    //                  Member_buddy        (not yet)
    //
    //                  Proshop_slot
    //                  Proshop_lott
    //                  Proshop_evntSignUp
    //
    //************************************************************************
     **/
    public static void getTable(PrintWriter out, String user) {


        out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
        out.println("<tr bgcolor=\"#336633\">");
        out.println("<td colspan=\"6\" align=\"center\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<b>Member List</b>");
        out.println("</font></td>");
        out.println("</tr><tr>");
        out.println("<input type=\"hidden\" name=\"letter\" value=\"\">");  // empty for script to complete
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterA.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('A')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterB.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('B')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterC.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('C')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterD.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('D')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterE.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('E')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterF.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('F')\"></td>");
        out.println("</tr><tr>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterG.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('G')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterH.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('H')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterI.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('I')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterJ.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('J')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterK.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('K')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterL.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('L')\"></td>");
        out.println("</tr><tr>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterM.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('M')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterN.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('N')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterO.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('O')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterP.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('P')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterQ.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('Q')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterR.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('R')\"></td>");
        out.println("</tr><tr>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterS.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('S')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterT.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('T')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterU.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('U')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterV.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('V')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterW.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('W')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterX.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('X')\"></td>");
        out.println("</tr><tr>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterY.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('Y')\"></td>");
        out.println("<td align=\"center\">");
        out.println("<img src=\"/" + rev + "/images/letterZ.jpg\" border=\"0\" width=\"15\" height=\"16\" onClick=\"subletter('Z')\"></td>");

        if (user.startsWith("proshop")) {
            out.println("<td align=\"center\" colspan=\"4\">");
            out.println("<img src=\"/" + rev + "/images/letterAll.jpg\" border=\"0\" width=\"53\" height=\"12\" onClick=\"subletter('List All')\"></td>");

        } else {

            if (user.equals("buddy")) {       // if from Member_buddy

                out.println("<td align=\"center\"></td>");
                out.println("<td align=\"center\"></td>");
                out.println("<td align=\"center\"></td>");
                out.println("<td align=\"center\"></td>");

            } else {                            // a member

                out.println("<td align=\"center\" colspan=\"4\">");
                out.println("<img src=\"/" + rev + "/images/letterPL.jpg\" border=\"0\" onClick=\"subletter('Partner List')\"></td>");
            }
        }
        out.println("</tr>");
        out.println("</table>");

    }       // end of getTable

    /**
    //************************************************************************
    //
    //  Output Table for Member Class Selection
    //
    //      called by:
    //                  Proshop_slot
    //                  Proshop_slotm
    //                  Proshop_lott
    //                  Proshop_evntSignUp
    //
    //************************************************************************
     **/
    public static void typeOptions(String club, String mshipOpt, String mtypeOpt, PrintWriter out, Connection con) {


        mTypeArrays mArrays = new mTypeArrays();     // setup for mship and mtype arrays

        int maxMems = mArrays.MAX_Mems;
        int maxMships = mArrays.MAX_Mships;
        int i = 0;


        try {

            //
            //  Get the mship types and mtypes for this club
            //
            mArrays = getClub.getMtypes(mArrays, con);         // skip it all if this fails

            //
            //  Now build a table with the drop-down box selections
            //
            out.println("<br><table border=\"1\" bgcolor=\"#F5F5DC\">");
            out.println("<tr bgcolor=\"#336633\">");
            out.println("<td colspan=\"6\" align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<b>By Member Class</b>");
            out.println("</font></td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td align=\"left\">");
            out.println("<font size=\"1\">");
            out.println("Membership: ");
//           out.println("<select size=\"1\" name=\"mshipopt\" onChange=\"document.playerform.submit()\">");
            out.println("<select size=\"1\" name=\"mshipopt\" onChange=\"this.form.submit()\">");
            if (mshipOpt.equals("ALL")) {
                out.println("<option selected value=\"ALL\">ALL</option>");
            } else {
                out.println("<option value=\"ALL\">ALL</option>");
            }
            i = 0;
            while (i < maxMships && !mArrays.mship[i].equals("")) {

                if (mshipOpt.equals(mArrays.mship[i])) {
                    out.println("<option selected value=\"" + mArrays.mship[i] + "\">" + mArrays.mship[i] + "</option>");
                } else {
                    out.println("<option value=\"" + mArrays.mship[i] + "\">" + mArrays.mship[i] + "</option>");
                }
                i++;
            }
            out.println("</select>");
            out.println("</font></td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td align=\"left\">");
            out.println("<font size=\"1\">");

            out.println("Member Type: ");
//           out.println("<select size=\"1\" name=\"mtypeopt\" onChange=\"document.playerform.submit()\">");
            out.println("<select size=\"1\" name=\"mtypeopt\" onChange=\"this.form.submit()\">");
            if (mtypeOpt.equals("ALL")) {
                out.println("<option selected value=\"ALL\">ALL</option>");
            } else {
                out.println("<option value=\"ALL\">ALL</option>");
            }
            i = 0;
            while (i < maxMems && !mArrays.mem[i].equals("")) {

                if (mtypeOpt.equals(mArrays.mem[i])) {
                    out.println("<option selected value=\"" + mArrays.mem[i] + "\">" + mArrays.mem[i] + "</option>");
                } else {
                    out.println("<option value=\"" + mArrays.mem[i] + "\">" + mArrays.mem[i] + "</option>");
                }
                i++;
            }
            out.println("</select>");
            out.println("</font></td>");
            out.println("</tr>");
            out.println("</table>");

        } catch (Exception e) {

            Utilities.logError("Error in alphaTable.typeOptions - Club = " + club + ", Exception = " + e.toString());
        }

    }          // end of typeOptions method

    /**
    //************************************************************************
    //
    //  Output Table for Member Name List
    //
    //      called by:
    //                  Proshop_slot
    //                  Proshop_slotm
    //                  Proshop_lott
    //                  Proshop_evntSignUp
    //
    //************************************************************************
     **/
    /*
    public static void nameList(String club, String letter, String mshipOpt, String mtypeOpt, boolean ghinOnly,
    parmCourse parmc, PrintWriter out, Connection con) {
    
    nameList(club, letter, mshipOpt, mtypeOpt, ghinOnly, parmc, true, out, con, -1, -1);
    
    }
     */
    public static void nameList(String club, String letter, String mshipOpt, String mtypeOpt, boolean ghinOnly,
            parmCourse parmc, int activity_id, PrintWriter out, Connection con) {

        nameList(club, letter, mshipOpt, mtypeOpt, ghinOnly, parmc, true, false, activity_id, out, con);

    }

    //
    // teecurr_id was only being passed in to assist in debuging a problem we had in 2009.  Problem was never
    // tracked down but a kernel update on the servers corrected the problem with the processing hanging up
    // within this method.
    //
    public static void nameList(String club, String letter, String mshipOpt, String mtypeOpt, boolean ghinOnly,
            parmCourse parmc, boolean enableAdvAssist, boolean includeInact, int activity_id, PrintWriter out, Connection con) {

        // Note: removed "double id, int teecurr_id" from incoming parameters on 2010-05-26

        PreparedStatement pstmt = null;
        Statement stmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        String sql = "";
        String first = "";
        String mid = "";
        String last = "";
        String name = "";
        String wname = "";
        String dname = "";
        String mtype = "";
        String mship = "";
        String mnum = "";
        String lastMnum = "";
        String wc = "";
        String username = "";
        String ghin = "";
        String gender = "";
        String posid = "";
        String sub_type = "";
        String inactOpt = "";

        String[] mtypeA = new String[40];            // arrays to hold family names to put in order (Medinah)
        String[] mNameA = new String[40];
        String[] dNameA = new String[40];
        String[] userA = new String[40];

        int i = 0;
        int indexM = 0;

        if (includeInact) {
            inactOpt = "";
        } else {
            inactOpt = "inact = 0 AND";
        }

        // include the dynamic search box scripts
        //if (enableAdvAssist) {
            //out.println("<script type=\"text/javascript\" src=\"/" + rev + "/dyn-search.js\"></script>");
            Utilities.proSlotScripts(out);
        //}

        out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\" valign=\"top\">");      // name list
        out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<b>Name List</b>");
        out.println("</font></td>");
        out.println("</tr>");


        if (enableAdvAssist) {
            // output dynamic search box
            out.println("<tr>");
            out.println("<td align=\"center\">");
            out.println("<input type=text name=DYN_search onkeyup=\"DYN_triggerChange()\" onkeypress=\"DYN_moveOnEnterKey(event); return DYN_disableEnterKey(event)\" onclick=\"this.select()\" value=\"Quick Search Box\">"); // return DYN_disableEnterKey(event)
            out.println("</td></tr>");
        }

        out.println("<tr><td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("Click on name to add");
        out.println("</font></td></tr>");

        try {

            //
            //  Normal sort string for queries below - last_only contains the last name without any suffix!!!
            //
            String orderby = "last_only, memNum, name_first, name_mi";      // normal order of names

            if (club.equals("congressional")) {
                orderby = "last_only, name_first, name_mi";                  // special order for Congressional
            } else if (club.equals("desertmountain")) {
                orderby = "sort_by";
            }

            //  if (club.equals( "loscoyotes" )) {

            //     orderby = "name_last, name_first, name_mi";                  // special order for Los Coyotes
            //  }


            if ((mshipOpt.equals("") || mshipOpt.equals("ALL") || mshipOpt == null)
                    && (mtypeOpt.equals("") || mtypeOpt.equals("ALL") || mtypeOpt == null)) {   // if both are ALL or not provided

                sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, wc, memNum, username, ghin, gender, posid, msub_type "
                        + "FROM member2b m "
                        + "LEFT OUTER JOIN mship5 ms ON m.m_ship = ms.mship AND ms.activity_id = ? "
                        + "WHERE " + ((ghinOnly) ? "ghin <> '' AND" : "")
                        + ((club.equals("interlachen") ? " m_ship <> 'Social' AND " : ""))
                        + ((letter.equals("%") ? "" : " name_last LIKE '" + letter + "' AND")) + " " + inactOpt + " billable = 1 "
                        + "ORDER BY " + orderby;

            } else {      // at least one is specified

                if (!mshipOpt.equals("") && !mshipOpt.equals("ALL") && mshipOpt != null
                        && !mtypeOpt.equals("") && !mtypeOpt.equals("ALL") && mtypeOpt != null) {   // if both were specified

                    sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, wc, memNum, username, ghin, gender, posid, msub_type "
                            + "FROM member2b "
                            + "WHERE " + ((ghinOnly) ? "ghin <> '' AND" : "") + " name_last LIKE '" + letter + "' AND "
                            + ((club.equals("interlachen") ? " m_ship <> 'Social' AND " : ""))
                            + "m_ship = '" + mshipOpt + "' AND m_type = '" + mtypeOpt + "' AND " + inactOpt + " billable = 1 "
                            + "ORDER BY " + orderby;

                } else {      // only one is specified

                    if (!mshipOpt.equals("") && !mshipOpt.equals("ALL") && mshipOpt != null) {     // its mship

                        sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, wc, memNum, username, ghin, gender, posid, msub_type "
                                + "FROM member2b "
                                + "WHERE " + ((ghinOnly) ? "ghin <> '' AND" : "") + " name_last LIKE '" + letter + "' AND "
                                + ((club.equals("interlachen") ? " m_ship <> 'Social' AND " : ""))
                                + "m_ship = '" + mshipOpt + "' AND " + inactOpt + " billable = 1 "
                                + "ORDER BY " + orderby;

                    } else {             // its mtype

                        sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, wc, memNum, username, ghin, gender, posid, msub_type "
                                + "FROM member2b "
                                + "WHERE " + ((ghinOnly) ? "ghin <> '' AND" : "") + " name_last LIKE '" + letter + "' AND "
                                + ((club.equals("interlachen") ? " m_ship <> 'Social' AND " : ""))
                                + "m_type = '" + mtypeOpt + "' AND " + inactOpt + " billable = 1 "
                                + "ORDER BY " + orderby;

                    }
                }
            }

            stmt2 = con.createStatement();
            rs = stmt2.executeQuery(sql);

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<div id=\"awmobject1\">");
            out.print("<select size=\"25\" name=\"bname\" style=\"cursor:hand\"");

            if (enableAdvAssist) {

                out.print(" onclick=\"movename(this.form.bname.value)\" onkeypress=\"DYN_moveOnEnterKey(event); return false\"");

            } else {

                out.print(" onchange=\"movename(this.form.bname.value)\"");

            }

            out.print(">");

            while (rs.next()) {

                last = rs.getString("name_last");          // full last name (with suffix, if appended)
                first = rs.getString("name_first");
                mid = rs.getString("name_mi");
                mship = rs.getString("m_ship");
                mtype = rs.getString("m_type");
                wc = rs.getString("wc");           // walk/cart preference
                mnum = rs.getString("memNum");
                username = rs.getString("username");
                gender = rs.getString("gender");
                ghin = rs.getString("ghin");
                posid = rs.getString("posid");
                sub_type = rs.getString("msub_type");

                i = 0;

                if (parmc != null && parmc.tmode_limit > 0) {
                
                    loopi3:
                    while (i < parmc.tmode_limit) {             // make sure wc is supported

                        if (parmc.tmodea[i] != null && parmc.tmodea[i].equals(wc)) {

                            break loopi3;
                        }
                        i++;
                    }
                    if (i > (parmc.tmode_limit - 1)) {       // if we went all the way without a match

                        wc = parmc.tmodea[0];    // use default option
                    }
                }

                if (mid.equals("")) {

                    name = first + " " + last;
                    dname = last + ", " + first;
                } else {

                    name = first + " " + mid + " " + last;
                    dname = last + ", " + first + " " + mid;
                }

                wname = name + ":" + wc + ":" + gender + ":" + ghin;              // combine name:wc for script


                //
                // BEGIN CUSTOMS THAT CONTROL THE DISPLAYING OF THE NAME LIST
                //


                //
                //  If club wants ordered display, then group family members together in an array, then display them
                //              according to their mtype (primary, spouse or dependent).
                //
                if (club.equals("medinahcc") || club.equals("oswegolake") || club.equals("virginiacc") || club.equals("pinehurstcountryclub") || 
                    club.equals("mpccpb") || club.equals("desertmountain")) {

                    //
                    //  Medinah - group by Primary Member, Spouse, then Dependents
                    //
                    if (club.equals("medinahcc")) {

                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].endsWith("Member")) {    // find Primary member

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + lastMnum + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].endsWith("Spouse")) {    // find the Spouse

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name

                    } else if (club.equals("desertmountain")) {

                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Primary")) {    // find Primary member

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + lastMnum + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Spouse")) {    // find the Spouse

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name

                    } else if (club.equals("oswegolake")) {

                        //
                        //  If Oswego Lake, Adult Male, Adult Female, then Dependents
                        //
                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Male")) {    // find Primary member

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + lastMnum + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && (mtypeA[i].endsWith("Ladies") || mtypeA[i].equals("Adult Female"))) {  // find the Spouse

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name

                    } else if (club.equals("virginiacc")) {

                        //
                        //  If Virginia CC, Adult Male, Adult Female, Senior Male, Senior Female, Student Male, Student Female,
                        //                  Young Adult Male, Young Adult Female, Junior Male, Junior Female
                        //
                        dname = dname + " " + mnum;                                    // add mnum for display

                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Male")) {             // Adult Male

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Female")) {          // Adult Female

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            mloop3:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Senior Male")) {          // Senior Male

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop3;              // exit loop
                                }
                            }

                            mloop4:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Senior Female")) {          // Senior Female

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop4;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Student Male")) {          // Student Male

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Student Female")) {          // Student Female

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Young Adult Male")) {          // Young Adult Male

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Young Adult Female")) {          // Young Adult Female

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Junior Male")) {          // Junior Male

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be Junior Females

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name

                    } else if (club.equals("pinehurstcountryclub")) {

                        //
                        //  If Pinehurst CC - Primary, Spouse, then Juniors
                        //
                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Primary")) {    // find Primary member

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">" + dNameA[i] + "&nbsp;&nbsp;" + lastMnum + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Secondary")) {  // find the Spouse

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            mloop3:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && (mtypeA[i].startsWith("Qualified"))) {  // find the Spouse

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop3;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:black\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name

                    } else if (club.equals("mpccpb")) {

                        //
                        //  If Monterey Peninsula, Adult Male, Adult Female, then Dependents
                        //
                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && !userA[i].endsWith(".1") && !userA[i].endsWith(".2") && !userA[i].endsWith(".3") && !userA[i].endsWith(".4")
                                        && !userA[i].endsWith(".5") && !userA[i].endsWith(".6") && !userA[i].endsWith(".7") && !userA[i].endsWith(".8") && !userA[i].endsWith(".9")) {    // find Primary member

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + lastMnum + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && userA[i].endsWith(".1")) {  // find the Spouse

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        userA[indexM] = username;             // save username for this member
                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name

                    }

                } else {   // NOT a club that wants special order


                    //
                    //  Add Member Number for all other clubs
                    //
                    if (!mnum.equals("")) {

                        // Add username instead of mnum for Charlotte CC
                        if (club.equals("charlottecc") || club.equals("loxahatchee") || club.equals("princessannecc") || club.equals("hillwoodcc") 
                                || club.equals("bearpath") || club.equals("ccyork")) {

                            dname = dname + " " + username;

                        } else if (club.equals("cwcpga")) {

                            dname = dname + " " + ghin;

                        } else {

                            dname = dname + " " + mnum;        // add mnum for display - do not move to slot

                        }

                    }


                    //
                    //  Los Coyotes - add pri/sce/jr and gender to end of name for display only
                    //
                    if (club.equals("loscoyotes")) {

                        if (mtype.startsWith("Primary")) {

                            if (mtype.endsWith("Female") || mtype.endsWith("Ladies")) {

                                dname = dname + "-PF";            // Primary Female

                            } else {

                                dname = dname + "-PM";            // Primary Male
                            }

                        } else {

                            if (mtype.startsWith("Secondary")) {

                                if (mtype.endsWith("Female") || mtype.endsWith("Ladies")) {

                                    dname = dname + "-SF";            // Seconary Female

                                } else {

                                    dname = dname + "-SM";            // Secondary Male
                                }

                            } else {    // Juniors

                                if (mtype.endsWith("Female")) {

                                    dname = dname + "-JF";            // Jr Female

                                } else {

                                    dname = dname + "-JM";            // Jr Male
                                }
                            }
                        }

                    } else if (club.equals("dhgc")) {

                        String tflag = "";

                        pstmt = con.prepareStatement("SELECT tflag FROM mship5 WHERE mship = ?");
                        pstmt.clearParameters();
                        pstmt.setString(1, mship);

                        rs2 = pstmt.executeQuery();

                        if (rs2.next()) {
                            tflag = rs2.getString("tflag");
                        }

                        pstmt.close();

                        if (!tflag.equals("") && !tflag.equalsIgnoreCase("GOLF")) {
                            dname += " " + tflag;
                        }

                    } else if (club.equals("olyclub")) {       // The Olympic Club - add the 2-character memeber code

                        dname = dname + " " + sub_type;
                    }

                    //
                    //  Now add the name to the selection list
                    //
                    if (club.equals("merion") && mship.equals("House")) {        // if Merion and member is a House member

                        out.println("<option value=\"" + wname + "\" style=\"color:red\">" + dname + "</option>");

                    } else if (club.equals("denvercc") && mtype.equals("Dependent")) {

                        out.println("<option value=\"" + wname + "\" style=\"color:green\">" + dname + "</option>");
                        
                    } else {

                        out.println("<option value=\"" + wname + "\">" + dname + "</option>");    // Normal display
                    }
                }

            } // end of WHILE members

            stmt2.close();

        } catch (Exception ex) {

            //doDebug(id, club, teecurr_id, "ERR Inside alphaTable.nameList - " + ex.toString());
            Utilities.logError("Error in alphaTable.nameList - Club = " + club + ", Exception = " + ex.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                stmt2.close();
            } catch (Exception ignore) {
            }

        }


        //
        // Finish customs
        //
        if (club.equals("medinahcc")) {

            //
            //  Medinah - make sure we get the last family done
            //
            mloop3:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].endsWith("Member")) {    // find Primary member

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + mnum + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop3;              // exit loop
                }
            }

            mloop4:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].endsWith("Spouse")) {    // find the Spouse

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop4;              // exit loop
                }
            }

            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                }
            }

        } else if (club.equals("oswegolake")) {

            //
            //  Oswego Lake - make sure we get the last family done
            //
            mloop5:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Male")) {    // find Primary member

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + mnum + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop5;              // exit loop
                }
            }

            mloop6:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && (mtypeA[i].endsWith("Ladies") || mtypeA[i].equals("Adult Female"))) {  // find the Spouse

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop6;              // exit loop
                }
            }

            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                }
            }

        } else if (club.equals("virginiacc")) {

            //
            //  CC of Virginia - make sure we get the last family done
            //
            mloop7:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Male")) {             // Adult Male

                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop7;              // exit loop
                }
            }

            mloop8:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Female")) {          // Adult Female

                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop8;              // exit loop
                }
            }

            mloop9:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].equals("Senior Male")) {          // Senior Male

                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop9;              // exit loop
                }
            }

            mloop10:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("Senior Female") && mtypeA[i].equals("")) {          // Senior Female

                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop10;              // exit loop
                }
            }

            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].equals("Student Male")) {          // Student Male

                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                }
            }

            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].equals("Student Female")) {          // Student Female

                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                }
            }

            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].equals("Young Adult Male")) {          // Young Adult Male

                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                }
            }

            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].equals("Young Adult Female")) {          // Young Adult Female

                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                }
            }

            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].equals("Junior Male")) {          // Junior Male

                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                }
            }

            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("")) {                    // the rest must be Junior Females

                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                }
            }

        } else if (club.equals("pinehurstcountryclub")) {

            //
            //  Pinehurst CC - make sure we get the last family done
            //
            mloop5:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Primary")) {    // find Primary member

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">" + dNameA[i] + "&nbsp;&nbsp;" + mnum + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop5;              // exit loop
                }
            }

            mloop6:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Primary")) {  // find the Spouse

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop6;              // exit loop
                }
            }

            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                }
            }

        } else if (club.equals("mpccpb")) {

            //
            //  Monterey Peninsula - make sure we get the last family done
            //
            mloop5:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && !userA[i].endsWith(".1") && !userA[i].endsWith(".2") && !userA[i].endsWith(".3") && !userA[i].endsWith(".4")
                        && !userA[i].endsWith(".5") && !userA[i].endsWith(".6") && !userA[i].endsWith(".7") && !userA[i].endsWith(".8") && !userA[i].endsWith(".9")) {    // find Primary member

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + mnum + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop5;              // exit loop
                }
            }

            mloop6:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && userA[i].endsWith(".1")) {  // find the Spouse

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop6;              // exit loop
                }
            }

            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                }
            }

        } else if (club.equals("desertmountain")) {

            mloop11:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Primary")) {    // find Primary member

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + mnum + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop11;              // exit loop
                }
            }

            mloop12:
            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Spouse")) {    // find the Spouse

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                    break mloop12;              // exit loop
                }
            }

            for (i = 0; i < indexM; i++) {

                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                    mtypeA[i] = "";            // remove it
                }
            }

        } // end if club for customs

        //
        //  End the row
        //
        out.print("</select>");
        out.println("</div>");
        out.println("</font></td></tr>");

        out.println("</table></td>");

    }          // end of nameList method

    /**
    //************************************************************************
    //
    //  Output Table for Member Name List
    //
    //      called by:
    //                  Proshop_dining
    //
    //************************************************************************
     **/
    public static void nameList2(String club, String letter, String mshipOpt, String mtypeOpt, PrintWriter out, Connection con) {


        PreparedStatement stmt2 = null;
        ResultSet rs = null;

        String first = "";
        String mid = "";
        String last = "";
        String name = "";
        String wname = "";
        String dname = "";
        String mtype = "";
        String mship = "";
        String mnum = "";
        String lastMnum = "";
        String username = "";
        String email = "";
        String email2 = "";
        String phone1 = "";
        String phone2 = "";

        String[] mtypeA = new String[20];            // arrays to hold family names to put in order (Medinah)
        String[] mNameA = new String[20];
        String[] dNameA = new String[20];
        String[] userA = new String[20];

        int i = 0;
        int indexM = 0;

        // include the dynamic search box scripts
        //out.println("<script type=\"text/javascript\" src=\"/" + rev + "/dyn-search.js\"></script>");
        Utilities.proSlotScripts(out);

        out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\" valign=\"top\">");      // name list
        out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<b>Name List</b>");
        out.println("</font></td>");
        out.println("</tr>");

        // output dynamic search box
        out.println("<tr>");
        out.println("<td align=\"center\">");
        out.println("<input type=text name=DYN_search onkeyup=\"DYN_triggerChange()\" onkeypress=\"DYN_moveOnEnterKey(event); return DYN_disableEnterKey(event)\" onclick=\"this.select()\" value=\"Quick Search Box\">"); // return DYN_disableEnterKey(event)
        out.println("</td></tr>");

        out.println("<tr><td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("Click on name to add");
        out.println("</font></td></tr>");

        try {

            //
            //  Normal sort string for queries below - last_only contains the last name without any suffix!!!
            //
            String orderby = "last_only, memNum, name_first, name_mi";      // normal order of names

            if (club.equals("congressional")) {

                orderby = "last_only, name_first, name_mi";                  // special order for Congressional
            }

            if ((mshipOpt.equals("") || mshipOpt.equals("ALL") || mshipOpt == null)
                    && (mtypeOpt.equals("") || mtypeOpt.equals("ALL") || mtypeOpt == null)) {   // if both are ALL or not provided

                stmt2 = con.prepareStatement(
                        "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, memNum, username, email, email2, phone1, phone2 "
                        + "FROM member2b "
                        + "WHERE name_last LIKE ? AND inact = 0 AND billable = 1 "
                        + "ORDER BY " + orderby);

                stmt2.clearParameters();
                stmt2.setString(1, letter);

            } else {      // at least one is specified

                if (!mshipOpt.equals("") && !mshipOpt.equals("ALL") && mshipOpt != null
                        && !mtypeOpt.equals("") && !mtypeOpt.equals("ALL") && mtypeOpt != null) {   // if both were specified

                    stmt2 = con.prepareStatement(
                            "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, memNum, username, email, email2, phone1, phone2 "
                            + "FROM member2b "
                            + "WHERE name_last LIKE ? AND m_ship = ? AND m_type = ? AND inact = 0 AND billable = 1 "
                            + "ORDER BY " + orderby);

                    stmt2.clearParameters();
                    stmt2.setString(1, letter);
                    stmt2.setString(2, mshipOpt);
                    stmt2.setString(3, mtypeOpt);

                } else {      // only one is specified

                    if (!mshipOpt.equals("") && !mshipOpt.equals("ALL") && mshipOpt != null) {     // its mship

                        stmt2 = con.prepareStatement(
                                "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, memNum, username, email, email2, phone1, phone2 "
                                + "FROM member2b "
                                + "WHERE name_last LIKE ? AND m_ship = ? AND inact = 0 AND billable = 1 "
                                + "ORDER BY " + orderby);

                        stmt2.clearParameters();
                        stmt2.setString(1, letter);
                        stmt2.setString(2, mshipOpt);

                    } else {             // its mtype

                        stmt2 = con.prepareStatement(
                                "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, memNum, username, email, email2, phone1, phone2 "
                                + "FROM member2b "
                                + "WHERE name_last LIKE ? AND m_type = ? AND inact = 0 AND billable = 1 "
                                + "ORDER BY " + orderby);

                        stmt2.clearParameters();               // clear the parms
                        stmt2.setString(1, letter);            // put the parm in stmt
                        stmt2.setString(2, mtypeOpt);
                    }
                }
            }

            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"25\" name=\"bname\" onclick=\"movename(this.form.bname.value)\" style=\"cursor:hand\">");

            while (rs.next()) {

                last = rs.getString("name_last");          // full last name (with suffix, if appended)
                first = rs.getString("name_first");
                mid = rs.getString("name_mi");
                mship = rs.getString("m_ship");
                mtype = rs.getString("m_type");
                mnum = rs.getString("memNum");
                username = rs.getString("username");
                email = rs.getString("email");
                email2 = rs.getString("email2");
                phone1 = rs.getString("phone1");
                phone2 = rs.getString("phone2");

                i = 0;

                if (mid.equals("")) {

                    name = first + " " + last;
                    dname = last + ", " + first;
                } else {

                    name = first + " " + mid + " " + last;
                    dname = last + ", " + first + " " + mid;
                }

                wname = name + " - " + mnum + ":" + email + ":" + email2 + ":" + phone1 + ":" + phone2;              // combine name:wc for script


                /*       // standard for all clubs now (9/09/2008)
                //
                //  Custom - add mnum to end of name for display only
                //
                if (club.equals( "royaloakscc" ) || club.equals( "fortcollins" ) || club.equals( "virginiacc" ) ||
                club.equals( "congressional" ) || club.equals( "muirfield" ) || club.equals( "blackhawk" ) ||
                club.equals( "mirasolcc" ) || club.equals( "tavistockcc" ) || club.equals( "bellehaven" ) ||
                club.equals( "loscoyotes" ) || club.equals( "charlottecc" ) ) {
                
                dname = dname + " " + mnum;              // add mnum for display - do not move to slot
                }
                 */

                //
                //  If club wants ordered display, then group family members together in an array, then display them
                //              according to their mtype (primary, spouse or dependent).
                //
                if (club.equals("medinahcc") || club.equals("oswegolake") || club.equals("virginiacc") || club.equals("pinehurstcountryclub") || 
                    club.equals("mpccpb") || club.equals("desertmountain")) {

                    //
                    //  Medinah - group by Primary Member, Spouse, then Dependents
                    //
                    if (club.equals("medinahcc")) {

                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].endsWith("Member")) {    // find Primary member

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + lastMnum + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].endsWith("Spouse")) {    // find the Spouse

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name

                    } else if (club.equals("desertmountain")) {

                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Primary")) {    // find Primary member

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + lastMnum + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Spouse")) {    // find the Spouse

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name

                    } else if (club.equals("oswegolake")) {

                        //
                        //  If Oswego Lake, Adult Male, Adult Female, then Dependents
                        //
                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Male")) {    // find Primary member

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + lastMnum + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && (mtypeA[i].endsWith("Ladies") || mtypeA[i].equals("Adult Female"))) {  // find the Spouse

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name

                    } else if (club.equals("virginiacc")) {

                        //
                        //  If Virginia CC, Adult Male, Adult Female, Senior Male, Senior Female, Student Male, Student Female,
                        //                  Young Adult Male, Young Adult Female, Junior Male, Junior Female
                        //
                        dname = dname + " " + mnum;                                    // add mnum for display

                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Male")) {             // Adult Male

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Female")) {          // Adult Female

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            mloop3:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Senior Male")) {          // Senior Male

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop3;              // exit loop
                                }
                            }

                            mloop4:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Senior Female")) {          // Senior Female

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop4;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Student Male")) {          // Student Male

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Student Female")) {          // Student Female

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Young Adult Male")) {          // Young Adult Male

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Young Adult Female")) {          // Young Adult Female

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].equals("Junior Male")) {          // Junior Male

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be Junior Females

                                    out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name

                    } else if (club.equals("pinehurstcountryclub")) {

                        //
                        //  If Pinehurst CC - Primary, Spouse, then Juniors
                        //
                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Primary")) {    // find Primary member

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">" + dNameA[i] + "&nbsp;&nbsp;" + lastMnum + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Secondary")) {  // find the Spouse

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name
                    } else if (club.equals("mpccpb")) {

                        //
                        //  If Oswego Lake, Adult Male, Adult Female, then Dependents
                        //
                        if (!lastMnum.equals("") && !mnum.equals(lastMnum)) {      // if new family

                            //
                            //  New family - output the last family members and save the new one
                            //
                            mloop1:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && !userA[i].endsWith(".1") && !userA[i].endsWith(".2") && !userA[i].endsWith(".3") && !userA[i].endsWith(".4")
                                        && !userA[i].endsWith(".5") && !userA[i].endsWith(".6") && !userA[i].endsWith(".7") && !userA[i].endsWith(".8") && !userA[i].endsWith(".9")) {    // find Primary member

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + lastMnum + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop1;              // exit loop
                                }
                            }

                            mloop2:
                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("") && userA[i].endsWith(".1")) {  // find the Spouse

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                    break mloop2;              // exit loop
                                }
                            }

                            for (i = 0; i < indexM; i++) {

                                if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                                    out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                                    mtypeA[i] = "";            // remove it
                                }
                            }

                            indexM = 0;                         // start over - new family
                        }

                        userA[indexM] = username;             // save username for this member
                        mtypeA[indexM] = mtype;               // save mtype for this member
                        mNameA[indexM] = wname;               // save member name and wc value
                        dNameA[indexM] = dname;               // save display name
                        lastMnum = mnum;                      // set this family's mnum

                        indexM++;                             // prepare for next name

                    }


                } else {   // NOT a club that wants special order


                    //
                    //  Add Member Number for all other clubs
                    //
                    if (!mnum.equals("")) {

                        // Add username instead of mnum for Charlotte CC
                        if (club.equals("charlottecc") || club.equals("loxahatchee")) {
                            dname = dname + " " + username;
                        } else {
                            dname = dname + " " + mnum;        // add mnum for display - do not move to slot
                        }
                    }


                    //
                    //  Los Coyotes - add pri/sce/jr and gender to end of name for display only
                    //
                    if (club.equals("loscoyotes")) {

                        if (mtype.startsWith("Primary")) {

                            if (mtype.endsWith("Female") || mtype.endsWith("Ladies")) {

                                dname = dname + "-PF";            // Primary Female

                            } else {

                                dname = dname + "-PM";            // Primary Male
                            }

                        } else {

                            if (mtype.startsWith("Secondary")) {

                                if (mtype.endsWith("Female") || mtype.endsWith("Ladies")) {

                                    dname = dname + "-SF";            // Seconary Female

                                } else {

                                    dname = dname + "-SM";            // Secondary Male
                                }

                            } else {    // Juniors

                                if (mtype.endsWith("Female")) {

                                    dname = dname + "-JF";            // Jr Female

                                } else {

                                    dname = dname + "-JM";            // Jr Male
                                }
                            }
                        }
                    }


                    //
                    //  Now add the name to the selection list
                    //
                    if (club.equals("merion") && mship.equals("House")) {        // if Merion and member is a House member

                        out.println("<option value=\"" + wname + "\" style=\"color:red\">" + dname + "</option>");

                    } else {

                        out.println("<option value=\"" + wname + "\">" + dname + "</option>");    // Normal display
                    }
                }

            }            // end of WHILE members


            if (club.equals("medinahcc")) {

                //
                //  Medinah - make sure we get the last family done
                //
                mloop3:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].endsWith("Member")) {    // find Primary member

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + mnum + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop3;              // exit loop
                    }
                }

                mloop4:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].endsWith("Spouse")) {    // find the Spouse

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop4;              // exit loop
                    }
                }

                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                    }
                }

            } else if (club.equals("desertmountain")) {

                mloop3:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Primary")) {    // find Primary member

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + mnum + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop3;              // exit loop
                    }
                }

                mloop4:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Spouse")) {    // find the Spouse

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop4;              // exit loop
                    }
                }

                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                    }
                }

            } else if (club.equals("oswegolake")) {

                //
                //  Oswego Lake - make sure we get the last family done
                //
                mloop5:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Male")) {    // find Primary member

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + mnum + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop5;              // exit loop
                    }
                }

                mloop6:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && (mtypeA[i].endsWith("Ladies") || mtypeA[i].equals("Adult Female"))) {  // find the Spouse

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop6;              // exit loop
                    }
                }

                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                    }
                }

            } else if (club.equals("virginiacc")) {

                //
                //  CC of Virginia - make sure we get the last family done
                //
                mloop7:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Male")) {             // Adult Male

                        out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop7;              // exit loop
                    }
                }

                mloop8:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].equals("Adult Female")) {          // Adult Female

                        out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop8;              // exit loop
                    }
                }

                mloop9:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].equals("Senior Male")) {          // Senior Male

                        out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop9;              // exit loop
                    }
                }

                mloop10:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("Senior Female") && mtypeA[i].equals("")) {          // Senior Female

                        out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop10;              // exit loop
                    }
                }

                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].equals("Student Male")) {          // Student Male

                        out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                    }
                }

                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].equals("Student Female")) {          // Student Female

                        out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                    }
                }

                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].equals("Young Adult Male")) {          // Young Adult Male

                        out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                    }
                }

                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].equals("Young Adult Female")) {          // Young Adult Female

                        out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                    }
                }

                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].equals("Junior Male")) {          // Junior Male

                        out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                    }
                }

                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("")) {                    // the rest must be Junior Females

                        out.println("<option value=\"" + mNameA[i] + "\">" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                    }
                }

            } else if (club.equals("pinehurstcountryclub")) {

                //
                //  Pinehurst CC - make sure we get the last family done
                //
                mloop5:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Primary")) {    // find Primary member

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">" + dNameA[i] + "&nbsp;&nbsp;" + mnum + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop5;              // exit loop
                    }
                }

                mloop6:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && mtypeA[i].startsWith("Primary")) {  // find the Spouse

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop6;              // exit loop
                    }
                }

                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                    }
                }
            } else if (club.equals("mpccpb")) {

                //
                //  Monterey Peninsula - make sure we get the last family done
                //
                mloop5:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && !userA[i].endsWith(".1") && !userA[i].endsWith(".2") && !userA[i].endsWith(".3") && !userA[i].endsWith(".4")
                            && !userA[i].endsWith(".5") && !userA[i].endsWith(".6") && !userA[i].endsWith(".7") && !userA[i].endsWith(".8") && !userA[i].endsWith(".9")) {    // find Primary member

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:red\">" + dNameA[i] + "&nbsp;&nbsp;" + mnum + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop5;              // exit loop
                    }
                }

                mloop6:
                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("") && userA[i].endsWith(".1")) {  // find the Spouse

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:blue\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                        break mloop6;              // exit loop
                    }
                }

                for (i = 0; i < indexM; i++) {

                    if (!mtypeA[i].equals("")) {                    // the rest must be dependents

                        out.println("<option value=\"" + mNameA[i] + "\" style=\"color:green\">&nbsp;&nbsp;&nbsp;" + dNameA[i] + "</option>");
                        mtypeA[i] = "";            // remove it
                    }
                }

            }

            //
            //  End the row
            //
            out.println("</select>");
            out.println("</div>");
            out.println("</font></td></tr>");

        } catch (Exception ex) {

            Utilities.logError("Error in alphaTable.nameList2 - Club = " + club + ", Exception = " + ex.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                stmt2.close();
            } catch (Exception ignore) {
            }
        }

        out.println("</table></td>");

    }          // end of nameList2 method

    /**
    //************************************************************************
    //
    //  Output Table for Member Name List
    //
    //      called by:
    //                  Proshop_dining
    //                  Member_partner
    //
    //************************************************************************
     **/
    public static void nameList_simple(String club, boolean includeMship, PrintWriter out, Connection con) {

        nameList_simple(club, 25, includeMship, out, con);
    }

    public static void nameList_simple(String club, int size, boolean includeMship, PrintWriter out, Connection con) {


        Statement stmt = null;
        ResultSet rs = null;

        String first = "";
        String mid = "";
        String last = "";
        String name = "";
        String wname = "";
        String dname = "";
        String mship = "";
        String mtype = "";
        String username = "";
        String style = "";

        // include the dynamic search box scripts
        //out.println("<script type=\"text/javascript\" src=\"/" + rev + "/dyn-search.js\"></script>");
        Utilities.proSlotScripts(out);

        out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\" valign=\"top\">");      // name list
        out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<b>Name List</b>");
        out.println("</font></td>");
        out.println("</tr>");

        // output dynamic search box
        out.println("<tr>");
        out.println("<td align=\"center\">");
        out.println("<input type=text name=DYN_search onkeyup=\"DYN_triggerChange()\" onkeypress=\"DYN_moveOnEnterKey(event); return DYN_disableEnterKey(event)\" onclick=\"this.select()\" value=\"Type Last Name\">"); // return DYN_disableEnterKey(event)
        out.println("</td></tr>");

        out.println("<tr><td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("Click on name to add");
        out.println("</font></td></tr>");

        try {

            //
            //  Normal sort string for queries below - last_only contains the last name without any suffix!!!
            //
            String orderby = "last_only, memNum, name_first, name_mi";      // normal order of names

            if (club.equals("congressional")) {

                orderby = "last_only, name_first, name_mi";                  // special order for Congressional
            }

            stmt = con.createStatement();

            rs = stmt.executeQuery(
                    "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, username "
                    + "FROM member2b "
                    + "WHERE inact = 0 AND billable = 1 "
                    + "ORDER BY " + orderby);             // execute the stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"" + size + "\" name=\"bname\" onclick=\"movename(this.form.bname.value)\" onkeypress=\"DYN_moveOnEnterKey(event); return false\" style=\"cursor:hand\">");

            while (rs.next()) {
                  
                last = rs.getString("name_last");          // full last name (with suffix, if appended)
                first = rs.getString("name_first");
                mid = rs.getString("name_mi");
                mship = rs.getString("m_ship");
                mtype = rs.getString("m_type");
                username = rs.getString("username");
                style = "";

                if (mid.equals("")) {

                    name = first + " " + last;
                    dname = last + ", " + first;
                } else {

                    name = first + " " + mid + " " + last;
                    dname = last + ", " + first + " " + mid;
                }

                if (includeMship) {
                    wname = name + ":" + mship + ":" + username;              // combine name:mship:username for script
                } else {
                    wname = name + ":" + username;       // combined name:username for script
                }
                
                if (club.equals("denvercc") && mtype.equalsIgnoreCase("Dependent")) {
                    style = "color:green;";
                }

                out.println("<option value=\"" + wname + "\"" + (!style.equals("") ? " style=\"" + style + "\"" : "") + ">" + dname + "</option>");    // Normal display

            }            // end of WHILE members

            //
            //  End the row
            //
            out.println("</select>");
            out.println("</font></td></tr>");

        } catch (Exception ex) {

            Utilities.logError("Error in alphaTable.nameList_simple - Club = " + club + ", Exception = " + ex.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                stmt.close();
            } catch (Exception ignore) {
            }
        }

        out.println("</table>");

    }          // end of nameList_simple method

    public static void guestList(String club, String course, String day_name, int time, parmClub parm, boolean lottery, PrintWriter out, Connection con) {

        guestList(club, course, day_name, time, parm, false, false, 0, true, out, con);

    }

    public static void guestList(String club, String course, String day_name, int time, parmClub parm, boolean lottery, boolean member_side, int activity_id, boolean enableAdvAssist, PrintWriter out, Connection con) {

        guestList(club, course, day_name, 0, time, parm, lottery, member_side, activity_id, enableAdvAssist, out, con);

    }

    /**
    //************************************************************************
    //
    //  Output Table for Guest List (and X)
    //
    //      called by:
    //                  Proshop_slot
    //                  Proshop_slotm
    //                  Proshop_lott
    //                  Proshop_evntSignUp
    //
    //************************************************************************
     **/
    public static void guestList(String club, String course, String day_name, long date, int time, parmClub parm, boolean lottery, boolean member_side, int activity_id, boolean enableAdvAssist, PrintWriter out, Connection con) {


        int i = 0;
        int x = 0;
        int xCount = 0;


        //
        //     Check the club db table for X and Guest parms specified by proshop
        //
        try {

            getClub.getParms(con, parm, activity_id);        // get the club parms

        } catch (Exception exc) {             // SQL Error - ignore guest and x

            Utilities.logError("Error in alphaTable calling getClub.getParms for activity_id=" + activity_id + ", Club = " + club + ", Exception = " + exc.toString());

        }

        x = parm.x;                 // get 'X' option value (0 = do NOT use X's)

        if (lottery) {
            x = 0;         // X's not allowed in lottery requests
        }
        //
        //  If X's are allowed, then add table for the X option
        //
        if (x > 0) {

            out.println("<br><table border=\"1\" bgcolor=\"#F5F5DC\">");
            out.println("<tr bgcolor=\"#336633\">");
            out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\"><b>Member TBD</b>");
            out.println("</font></td>");
            out.println("</tr>");
            out.println("<tr><td align=\"left\" bgcolor=\"#FFFFFF\"><font size=\"2\" color=\"#000000\" face=\"Helvetica, Arial, Sans-serif\">");
            if (enableAdvAssist) {
                out.println("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onClick=\"moveguest('X')\">X</a>");
            } else {
                //  out.println("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onChange=\"moveguest('X')\">X</a>");
                //  out.println("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onBlur=\"moveguest('X')\">X</a>");
                //  out.println("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onClick=\"moveguest('X')\">X</a>");   // works best on iPad, but not 100%
                out.println("<select size=\"1\" name=\"tbd\" onBlur=\"moveguest('X')\">");                       // this works great on iPad
                out.println("<option value=\"X\">X</option>");
                out.println("</select>");
            }
            out.println("</font></td></tr></table>");      // end of this table and column
        }

        //
        //  Add table for Guest Types
        //
        out.println("<br><table border=\"1\" bgcolor=\"#F5F5DC\">");
        out.println("<tr bgcolor=\"#336633\">");
        out.println("<td align=\"center\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        if (club.equals("lakewood")) {
            out.println("<b>Player Options</b>");
        } else {
            out.println("<b>Guest Types</b>");
            try {
                activity_id = getActivity.getRootIdFromActivityId(activity_id, con);
            } catch (Exception exc) {
            }
        }
        out.println("</font></td>");
        out.println("</tr>");


        //
        //  first we must count how many fields there will be
        //
        xCount = 0;

        if (club.equals("wellesley") && day_name.equals("")) {      // if Wellesley and an event

            xCount = 2;            // only 2 guest types

        } else if (club.equals("olyclub")) {      // if Olympic Club - different guest types per course

            if (course.equals("Lake")) {

                xCount = 10;    // Lakes uses 10 types

                if (member_side == true) {
                    xCount = 3;    // only 3 for members
                }
            } else if (course.equals("Ocean")) {

                xCount = 15;    // Ocean uses all 15 types

                if (member_side == true) {
                    xCount = 7;    // only 7 for members
                }
            } else {

                xCount = 7;     // Cliffs uses 7 types

                if (member_side == true) {
                    xCount = 3;    // only 3 for members         
                }
            }

        } else {

            //if (x != 0) xCount = 1;

            for (i = 0; i < parm.MAX_Guests; i++) {

                if (!parm.guest[i].equals("")) {
                    
                    if (club.equals("desertmountain")) {     // split by course name
                        
                        if (course.equals("Apache") && parm.guest[i].startsWith("A-")) {
                        
                            xCount++;
                            
                        } else if (course.equals("Chiricahua") && parm.guest[i].startsWith("CH-")) {
                        
                            xCount++;
                         
                        } else if (course.equals("Cochise") && parm.guest[i].startsWith("C-")) {
                        
                            xCount++;
                         
                        } else if (course.equals("Geronimo") && parm.guest[i].startsWith("G-")) {
                        
                            xCount++;
                         
                        } else if (course.equals("Outlaw") && parm.guest[i].startsWith("O-")) {
                        
                            xCount++;
                         
                        } else if (course.equals("Renegade") && parm.guest[i].startsWith("R-")) {
                        
                            xCount++;                            
                        }
                        
                    } else {
                        
                        xCount++;
                    }
                }
            }

        }

        i = 0;
        if (xCount != 0) {

            if (xCount < 2) {

                xCount = 2;             // set size to at least 2
            }
            if (xCount > 10 && !club.equals("olyclub")) {

                xCount = 10;             // set size to no more than 10 showing at once (it will scroll)
            }
            out.println("<tr><td align=\"left\"><font size=\"1\" face=\"Helvetica, Arial, Sans-serif\">");
            out.println("<b>**</b> Add guests immediately<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>after</b> host member.<br>");
            out.println("</font><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

            out.print("<select size=\"" + xCount + "\" name=\"gname\" style=\"cursor:hand\" " +
                       //"onblur=\"if(navigator.userAgent.match(/(iPhone|iPad|iPod|Android|BlackBerry)/)){moveguest(this.value)};\" onclick=\"if(!navigator.userAgent.match(/(iPhone|iPad|iPod|Android|BlackBerry)/)){moveguest(this.value)};\">");
                        "onchange=\"DYN_selectChange(this, moveguest);\" onclick=\"DYN_selectClick(this, moveguest);\" onblur=\"DYN_selectBlur(this, moveguest);\">");
                    
                    
            
            //if (enableAdvAssist) {
            //    out.print(" onclick=\"moveguest(this.value);\"");
            //} else {
                //out.print("onChange=\"moveguest(this.form.gname.value)\"");      // this only works once on the iPad
            //    out.print(" onBlur=\"moveguest(this.form.gname.value)\"");          // this works better on the iPad
            //}
            out.print(">");
            

            /*               // moved to own table above
            if (x != 0) {
            out.println("<option value=\"X\">X</option>");
            }
             */

            if (club.equals("wellesley") && day_name.equals("")) {      // if Wellesley and an event

                out.println("<option value=\"Outing Guest\">Outing Guest</option>");     // only 2 guest types
                out.println("<option value=\"Tourney Guest\">Tourney Guest</option>");

            } else {

                boolean guestSkip = false;

                for (i = 0; i < parm.MAX_Guests; i++) {

                    if (!parm.guest[i].equals("")) {   // if guest name

                        guestSkip = false; // reset

                        //
                        //  If Medinah, then only display guest types for the specified course
                        //
                        if (club.equals("medinahcc") && !day_name.equals("")) {      // if Medinah and NOT an event

                            if (parm.guest[i].equals("B4 11am on no3")
                                    || parm.guest[i].startsWith("*Guest") || parm.guest[i].equals("Unaccom1")
                                    || parm.guest[i].equals("Unaccom2") || parm.guest[i].equals("Unaccom3")) {

                                guestSkip = true;
                            }
                        }

                        //
                        //  If Olympic Club, then only display guest types for the specified course
                        //
                        if (club.equals("olyclub")) {

                            if (course.equals("Lake")) {

                                if (parm.guest[i].startsWith("Coach Staff")
                                        || parm.guest[i].startsWith("Junior GWL") || parm.guest[i].equals("MHGP Guest Outing")
                                        || parm.guest[i].startsWith("Member w/Guest") || parm.guest[i].equals("Twilight Gst")) {

                                    guestSkip = true;
                                }

                            } else if (course.equals("Cliffs")) {

                                if (parm.guest[i].equals("Clergy") || parm.guest[i].startsWith("Coach Staff")
                                        || parm.guest[i].startsWith("Junior GWL") || parm.guest[i].equals("MHGP Guest Outing")
                                        || parm.guest[i].startsWith("Twilight Gst") || parm.guest[i].equals("Unacc Low Fee")
                                        || parm.guest[i].equals("Unacc Gst") || parm.guest[i].equals("USGA")) {

                                    guestSkip = true;
                                }

                            } else {       // Ocean

                                if (parm.guest[i].equals("Twilight Gst")) {     // Twilight Gst is only used after 4:00 PM

                                    // Check to see if we're in DST or not, and, if so, if it's before or after 9/01
                                    int yy = (int) date / 10000;
                                    int temp = yy * 10000;
                                    int mm = (int) date - temp;
                                    temp = mm / 100;
                                    temp = temp * 100;
                                    int dd = mm - temp;
                                    mm = mm / 100;
                                    mm--;                        // back up 1 for calendar object 

                                    int twilightTime = 0;

                                    /*
                                    try {
                                    } catch (Exception exc) {
                                        Utilities.logError("Proshop_sheet_pos.buildCSG - " + club + " - Error determining DST dates - Err: " + exc.toString());
                                    }
                                     */

                                    Calendar cal = new GregorianCalendar();

                                    cal.set(yy, mm, dd);

                                    boolean isDST = false;

                                    if (cal.get(Calendar.DST_OFFSET) != 0) {
                                        isDST = true;
                                    }

                                    if (isDST) {

                                        int shortDate = ((mm + 1) * 100) + dd;

                                        if (shortDate <= 831) {
                                            twilightTime = 1600;
                                        } else {
                                            twilightTime = 1500;
                                        }

                                    } else {
                                        twilightTime = 1400;
                                    }

                                    if (time < twilightTime) {
                                        guestSkip = true;
                                    }
                                }
                            }
                        }
                        
                        //
                        //  Desert Mountain - seperate the guest types based on course name
                        //
                        if (club.equals("desertmountain")) {     // split by course name

                            if (day_name.equals("")) {
                                
                                // They now allow only "Event Guest" guests for events that allow guests
                                if (!parm.guest[i].equalsIgnoreCase("Event Guest")) {
                                    guestSkip = true;
                                }
                                
                            } else {
                                
                                if (course.equals("Apache") && !parm.guest[i].startsWith("A-")) {

                                    guestSkip = true;

                                } else if (course.equals("Chiricahua") && !parm.guest[i].startsWith("CH-")) {

                                    guestSkip = true;

                                } else if (course.equals("Cochise") && !parm.guest[i].startsWith("C-")) {

                                    guestSkip = true;

                                } else if (course.equals("Geronimo") && !parm.guest[i].startsWith("G-")) {

                                    guestSkip = true;

                                } else if (course.equals("Outlaw") && !parm.guest[i].startsWith("O-")) {

                                    guestSkip = true;

                                } else if (course.equals("Renegade") && !parm.guest[i].startsWith("R-")) {

                                    guestSkip = true;                            
                                }
                            }
                        }
                        

                        /*
                        //
                        //  If Baltimore and Fri or Sat, skip 'Non Member East' guest type
                        //
                        if (club.equals( "baltimore" ) && (day_name.equals( "Friday" ) || day_name.equals( "Saturday" ))) {
                        
                        if (parm.guest[i].equalsIgnoreCase( "Non Member East" )) {
                        
                        guestSkip = true;
                        }
                        }
                         */

                        // if this is being called from the member side only show this gtype if gOpt is zero (otherwise it's a pro-only gtype)
                        if (guestSkip == false && (!member_side || (member_side && parm.gOpt[i] == 0))) {

                            out.println("<option value=\"" + parm.guest[i] + "|" + parm.gDb[i] + "\">" + parm.guest[i] + "</option>");
                        }

                    } // end if not empty
                } // end for loop
            }
            out.println("</select>");
            out.println("</font></td></tr></table>");      // end of this table and column

        } else {

            out.println("</table>");      // end the table and column if none specified
        }

        out.println("<script type=\"text/javascript\">");
        out.println("function clearGstSel(el){");
        out.println(" if(document.all){");
        // all ie browsers
        out.println("  el.options.selectedIndex=-1;");
        out.println(" } else {");
        // detect ios and its version
        out.println("  var version = 0;");
        out.println("  var res = navigator.userAgent.match(/; CPU.*OS (\\d_\\d)/);");
        out.println("  if (res) {");
        out.println("    var strVer = res[res.length - 1];");
        out.println("    strVer = strVer.replace(\"_\", \".\");");
        out.println("    version = strVer * 1;");
        out.println("  }");
        out.println("  var iOS = false,");
        out.println("  iDevice = ['iPad', 'iPhone', 'iPod'];");

        out.println("  for (i = 0; i < iDevice.length; i++) {");
        out.println("    if (navigator.platform === iDevice[i]) { iOS = true; break; }");
        out.println("  }");

        out.println("  if (iOS = true && version == 6) {");
        // hack for iOS6 - change to be safari only - chrome seemed to be unaffected.
        out.println("    setTimeout(function(){el.selectedIndex=-1;},200);"); // document.getElementById('gname')
        out.println("  } else {");
        // all other non-ie, non-ios6 browsers
        out.println("    el.selectedIndex=-1");
        out.println("  }");

        out.println(" }");
        out.println("}");
        out.println("</script>");

    } // end of guestList method

    
    /**
     * guestdbList - Prints a select box containing tracked guests in the system.
     *
     * @param user Username of the current user
     * @param caller Location list invoked from:  0 = default (modal window), 1 = mobile, 2 = guest mgmt merge page
     * @param activity_id Current activity_id
     * @param cur_guest_id Currently in-use guest_id, if applicable.  Do not include in list.
     * @param out Output stream
     * @param con Connection to club database
     */
    public static void guestdbList(String user, int caller, int activity_id, int cur_guest_id, PrintWriter out, Connection con) {
        Map<String, Map<String, Object>> temp_map = guestdbList(user, caller, activity_id, cur_guest_id, out, con, false);
    }

    /**
     * guestdbList - Prints a select box or returns a Map object containing tracked guests in the system.
     *
     * @param user Username of the current user
     * @param caller Location list invoked from:  0 = default (modal window), 1 = mobile, 2 = guest mgmt merge page
     * @param activity_id Current activity_id
     * @param cur_guest_id Currently in-use guest_id, if applicable.  Do not include in list.
     * @param out Output stream
     * @param con Connection to club database
     * @param generateMap return a map object, or print results to out
     */
    public static Map<String, Map<String, Object>> guestdbList(String user, int caller, int activity_id, int cur_guest_id, PrintWriter out, Connection con, boolean generateMap) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Map<String, Map<String, Object>> result_map = new LinkedHashMap<String, Map<String, Object>>();

        String checkUsername = "";
        String checkUsername2 = "";
        String guest_name = "";
        String display_name = "";
        String filterGuestId = "";
        String address = "";

        int guest_id = 0;
        int offset = 0;

        boolean isProshop = false;
        boolean guestFound = false;
        boolean guestdbTbaAllowed = false;

        if (ProcessConstants.isProshopUser(user) || Utilities.isHotelUser(user, con)) {
            isProshop = true;
        }

        guestdbTbaAllowed = Utilities.isGuestTrackingTbaAllowed(activity_id, isProshop, con);

        if (caller != 1 && (!generateMap)) {

            // Include the dynamic search box scripts
            //out.println("<script type=\"text/javascript\" src=\"/" + rev + "/dyn-search.js\"></script>");
            Utilities.proSlotScripts(out);

            // Start guest form
            out.println("<form name=\"playerform\">");
            
            // Start table
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" width=\"125px\">");

            // Header row
            out.println("<tr bgcolor=\"#336633\">");
            out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<b>Guests</b>");
            out.println("</font></td>");
            out.println("</tr>");

            // output dynamic search box
            out.println("<tr>");
            out.println("<td align=\"center\">");
            out.println("<input style=\"width:125px;\" type=text name=DYN_search onkeyup=\"DYN_triggerChange()\" onkeypress=\"DYN_moveOnEnterKey(event); return DYN_disableEnterKey(event)\" onclick=\"this.focus();this.select();\" value=\"Quick Search Box\">"); // return DYN_disableEnterKey(event)
            out.println("</td></tr>");

            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("Click on name to select");
            out.println("</font></td></tr>");

            // Guest select
            out.println("<tr><td align=\"center\">");
        }

        // Get applicable guests from database and loop through to display them

        if (!generateMap) {
            if (caller != 1) {
                //if (getClub.getClubName(con).equals("demobrad")) {
                 //   out.println("<select size=\"15\" name=\"bname\" style=\"width:125px; cursor:hand;\" onchange=\"showGUESTPopup(this)\" ondblclick=\"passguest(this.form.bname.value);\" onkeypress=\"DYN_moveOnEnterKey(event); return false;\" style=\"\">");
                //} else {
                    out.println("<select size=\"15\" name=\"bname\" style=\"width:125px; cursor:hand;\" onchange=\"DYN_selectChange(this, passguest);\" onclick=\"DYN_selectClick(this, passguest);\" onblur=\"DYN_selectBlur(this, passguest);\" style=\"\">");
                //}
                if (guestdbTbaAllowed && caller != 2) {
                    out.println("<option value=\"TBA:0\">TBA</option>");
                }
            } else {
                out.println("<select size=\"1\" name=\"guest_id\" style=\"width:125px; cursor:hand;\">");
                if (guestdbTbaAllowed) {
                    out.println("<option value=\"-99\">TBA</option>");
                }
            }
        } else if (guestdbTbaAllowed) {
            result_map.put("guest_tba", new LinkedHashMap<String, Object>());
            result_map.get("guest_tba").put("guest_id", 0);
            result_map.get("guest_tba").put("display_name", "TBA");
            result_map.get("guest_tba").put("guest_name", "TBA");
            result_map.get("guest_tba").put("gender", "");
            result_map.get("guest_tba").put("homeclub", "");
            result_map.get("guest_tba").put("phone", "");
            result_map.get("guest_tba").put("email", "");
            result_map.get("guest_tba").put("address", "");
            result_map.get("guest_tba").put("ghin", "");
            //result_map.get("guest_tba").put("hdcp_index", "");
        }

        try {
            if (!isProshop) {
                checkUsername = "LEFT OUTER JOIN guestdb_hosts h ON d.guest_id = h.guest_id ";
                checkUsername2 = "AND h.username = ? ";
            } else {
                checkUsername = "";
                checkUsername2 = "";
            }

            if (caller == 2) {
                filterGuestId = "AND guest_id <> ? ";
            }

            // If proshop user, gather all
            pstmt = con.prepareStatement(
                    "SELECT d.guest_id, CONCAT(d.name_last, ', ', d.name_first, IF(d.name_mi != '', CONCAT(' ', d.name_mi), '')) as display_name, "
                    + "CONCAT(d.name_first, ' ', IF(d.name_mi != '', CONCAT(d.name_mi, ' '), ''), d.name_last) as guest_name, "
                    + "d.address1, d.address2, d.city, d.state, d.zip, d.email1, d.email2, d.phone1, d.phone2, d.home_club, d.gender, d.hdcp_num "
                    + "FROM guestdb_data d "
                    + checkUsername
                    + "WHERE d.inact = 0 "
                    + checkUsername2
                    + filterGuestId
                    + "ORDER BY d.name_last, d.name_first");

            pstmt.clearParameters();

            if (!isProshop) {
                offset = 1;
                pstmt.setString(1, user);
            }

            if (caller == 2) {
                pstmt.setInt(1 + offset, cur_guest_id);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {

                guestFound = true;

                guest_id = rs.getInt("d.guest_id");
                guest_name = rs.getString("guest_name");
                display_name = rs.getString("display_name");
                //address = rs.getString("address1");
                /*
                if(rs.getString("address2").length() > 0){
                    address += "\n"+ rs.getString("address2");
                }
                if((rs.getString("city").length() > 0) || (rs.getString("state").length() > 0) || (rs.getString("zip").length() > 0)){
                    address += "\n";
                    if((rs.getString("city").length() > 0)){
                        address += rs.getString("city") + ", ";
                    }
                    if((rs.getString("state").length() > 0)){
                        address += rs.getString("state") + " ";
                    }
                    address += rs.getString("zip");
                }
                 */
                if (!generateMap) {
                    if (caller != 1) {
                        out.println("<option value=\"" + guest_name + ":" + guest_id + "\">" + display_name + "</option>");
                    } else {
                        out.println("<option value=\"" + guest_id + "\">" + display_name + "</option>");
                    }
                } else {
                    result_map.put("guest_" + guest_id, new LinkedHashMap<String, Object>());
                    result_map.get("guest_" + guest_id).put("guest_id", guest_id);
                    result_map.get("guest_" + guest_id).put("display_name", display_name);
                    result_map.get("guest_" + guest_id).put("guest_name", guest_name);
                    result_map.get("guest_" + guest_id).put("gender", rs.getString("gender"));
                    result_map.get("guest_" + guest_id).put("ghin", rs.getString("hdcp_num"));
                    result_map.get("guest_" + guest_id).put("homeclub", rs.getString("home_club"));
                    result_map.get("guest_" + guest_id).put("email1", rs.getString("email1"));
                    result_map.get("guest_" + guest_id).put("email2", rs.getString("email2"));
                    result_map.get("guest_" + guest_id).put("phone1", rs.getString("phone1"));
                    result_map.get("guest_" + guest_id).put("phone2", rs.getString("phone2"));
                    result_map.get("guest_" + guest_id).put("address1", rs.getString("address1"));
                    result_map.get("guest_" + guest_id).put("address2", rs.getString("address2"));
                    result_map.get("guest_" + guest_id).put("city", rs.getString("city"));
                    result_map.get("guest_" + guest_id).put("state", rs.getString("state"));
                    result_map.get("guest_" + guest_id).put("zip", rs.getString("zip"));
                    //result_map.get("guest_" + guest_id).put("hdcp_index", rs.getString("hdcp_index"));
                }
            }

            pstmt.close();

            if (!generateMap) {
                // If no guests found for this member, print [none] option so we can blank out the player upon returning to the slot page on the mobile side
                if (caller == 1 && !guestFound && !guestdbTbaAllowed) {
                    out.println("<option value=\"-1\">[none]</option>");
                }
            }

        } catch (Exception exc) {
            out.println("<!-- Error: " + exc.getMessage() + " -->");
        }

        if (!generateMap) {
            out.println("</select>");

            if (caller != 1) {
                out.println("</td></tr>");
                out.println("</table>");
                out.println("</form>");
            }
        }

        return result_map;
    }

    /**
     * displayPartnerList - Prints the partner list select box
     *
     * @param user_id Username of the current user
     * @param activity_id Activity id to get the partner list for
     * @param mode Run mode:  0 = normal, 1 = event (to allow for special needs)
     * @param con Connection to club database
     * @param out Ouput stream
     */
    public static void displayPartnerList(String user_id, int activity_id, int mode, Connection con, PrintWriter out) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int partner_count = 0;

        String partner_name = "";
        String partner_display = "";
        String partner_wc = "";

        // Get a count of the number of partners for this user/activity_id
        try {
            pstmt = con.prepareStatement("SELECT count(*) FROM partner WHERE user_id = ? AND activity_id = ?");
            pstmt.clearParameters();
            pstmt.setString(1, user_id);
            pstmt.setInt(2, activity_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                partner_count = rs.getInt(1);
            }

            pstmt.close();

        } catch (Exception exc) {
            partner_count = 0;
        }

        // Start table for partner list display
        out.println("<table border=\"1\" width=\"160\" bgcolor=\"#f5f5dc\">");      // buddy list
        out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
        out.println("<font  color=\"#ffffff\"size=\"2\">");
        out.println("<b>Partner List</b>");
        out.println("</font></td>");
        out.println("</tr><tr>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("Click on name to add");
        out.println("</font></td></tr>");

        // If 1 or more partners are in this list, print the selection box, otherwise print a message saying no partners exist
        if (partner_count > 0) {

            // Print the select box to display all partners for this user/activity_id
            out.println("<tr><td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("<select size=\"" + (partner_count < 20 ? (partner_count > 2 ? partner_count : 2) : "20") + "\" name=\"bud\" onClick=\"movename(this.value)\">");

            try {
                pstmt = con.prepareStatement(
                        "SELECT CONCAT(m.name_last, ',', IF(m.name_mi <> '', CONCAT(' ', m.name_mi, ' '), ' '), m.name_first) as partner_name, "
                        + "CONCAT(m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi, ' '), ' '), m.name_last) as partner_display, "
                        + "m.wc" + (mode == 1 ? ", m.gender, m.ghin" : "") + " from partner p "
                        + "LEFT OUTER JOIN member2b m ON p.partner_id = m.username "
                        + "WHERE p.user_id = ? AND p.activity_id = ? AND m.billable = '1' AND m.inact = '0' "
                        + "ORDER BY p.priority, m.name_last, m.name_first");
                pstmt.clearParameters();
                pstmt.setString(1, user_id);
                pstmt.setInt(2, activity_id);

                rs = pstmt.executeQuery();

                while (rs.next()) {
                    partner_name = rs.getString("partner_name");
                    partner_display = rs.getString("partner_display");
                    partner_wc = rs.getString("m.wc");

                    if (mode == 0) {
                        out.println("<option value=\"" + partner_display + ":" + partner_wc + "\">" + partner_name + "</option>");
                    } else if (mode == 1) {

                        // mode 1 = event.  Need to grab gender and hdcp num
                        String partner_gender = rs.getString("m.gender");
                        String partner_hdcp = rs.getString("ghin");

                        while (partner_hdcp.length() < 7) {
                            partner_hdcp = "0" + partner_hdcp;
                        }

                        out.println("<option value=\"" + partner_display + ":" + partner_wc + ":" + partner_gender + ":" + partner_hdcp + "\">" + partner_name + "</option>");
                    }
                }

                pstmt.close();

            } catch (Exception exc) {
                out.println(exc.getMessage());
            }

            out.println("</select>");

        } else {
            out.println("<tr><td align=\"center\" bgcolor=\"white\">");
            out.println("<font size=\"2\">");
            out.println("No names - <br>");
            out.println("Select 'Partner List' <br>");
            out.println("on main menu to add.");
        }

        out.println("</font>");
        out.println("</td></tr>");
        out.println("</table>");

    }      // end of displayPartnerList()

    private static void doDebug(double id, String club, int teecurrid, String msg) {


        Calendar cal = new GregorianCalendar();                // get todays date
        int hr = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        int ms = cal.get(Calendar.MILLISECOND);


        try {
            //
            //  Dir path for the real server
            //
            PrintWriter fout1 = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//v5//debug-N" + ProcessConstants.SERVER_ID + ".csv", true));

            //
            //  Put header line in text file
            //
            fout1.print(id + ", " + club + ", " + teecurrid + ", " + msg + ", " + hr + ":" + min + ":" + sec + "." + ms); // "N" + ProcessConstants.SERVER_ID + ", " +

            fout1.println();      // output the line

            fout1.close();

        } catch (Exception e2) {
        }

    }  // end of doDebug
}  // end of alphaTable class

