/***************************************************************************************
 *   nameLists:  This servlet will output the member name selection tables.
 *
 *
 *   created:  12/09/2004   Bob P.
 *
 *
 * 
 *   NOTE: *************** refer to alphaTable.nameList for proshop side ***********************
 * 
 *   last updated:
 * 
 *      11/22/13   Add checks to generateNameList when building a name list for member emails to skip members that have elected to NOT receive member emails.
 *       8/15/13   Add handicap index display in generatePartnerList for members that select the option.  The partners' handicap
 *                 index is added next to their name (golf and members only).  Requested by Ballanisles.
 *       6/26/13   Fixed issue with generatePartnerList and generateNameList where they were using a format of "name_last, name_mi name_first" instead of "name_last, name_first name_mi".
 *       6/03/13   Add a mode 3 to generateNameList for excluding members w/o ghin numbers
 *       5/10/13   Denver CC (denvercc) - Fixed custom so that dependent names in the partner list (on the reservation page), as well as in the dining reservation page, will also display green.
 *       2/27/13   Denver CC (denvercc) - Added custom to colorcode dependents in green in the member-side name list.
 *       2/27/13   generateNameList updated so that styles can now be included to apply to the member name list.
 *      12/21/12   Desert Mountain - do custom name list to identify the family members (case 2208).
 *      11/14/12   Fixed issue with displayNameList that caused selecting a name to do nothing when on a FlxRez activity.
 *      12/15/11   Fix SQL injection hole in generateNameList
 *      12/14/11   Update dislpayNameList and getTable for new skin
 *      12/13/11   Update dislpayPartnerList and getTable for new skin
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
import javax.servlet.http.HttpSession;

import com.foretees.communication.DistributionList;

public class nameLists {

    private static String rev = ProcessConstants.REV;

    /**
    //************************************************************************
    //
    //  Output Table for Member Name Selection
    //
    //      called by:  Member_slot 
    //                  Member_slotm (using simple_table)
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
    /**
     * getTable - Prints the letter box
     *
     * @param out Ouput stream
     * @param user Username of the current user
     */
    public static void getTable(PrintWriter out, String user) {
        getTable(out, user, false);
    }

    /**
     * getTable - Prints the letter box
     *
     * @param out Ouput stream
     * @param user Username of the current user
     * @param simple_table If set, output a simple table that will be activated later with jQuery
     */
    public static void getTable(PrintWriter out, String user, boolean simple_table) {

        /*
         * Note: Any customs here should also be made to jquery.foreTeesSlot.js / generateSlotPage
         */

        if (simple_table) {
            out.println("<div class=\"sub_instructions member_search_container\">"); // start member search table container
            out.println("<h3>Member List</h3>");
            out.println("<div class=\"element_group\">");  // Start element group
            out.print("<table class=\"member_search_letter_table\"><tbody><tr>");
            for (int i2 = 0; i2 < 26; i2++) { // output memberletter search table table
                if ((i2 % 6 == 0) && i2 > 0) {  // start new row of letters every 6 letters
                    out.print("</tr><tr>");
                }
                out.print("<td><a class=\"member_search_letter_button\" data-ftinteger=\"" + (65 + i2) + "\" href=\"#\">&#" + (65 + i2) + ";</a></td>");  // Output html number for letters A to Z
            }

        } else {
            out.println("<div class=\"sub_instructions\"><strong>Member List</strong>");
            out.println("<div class=\"res_select_ltr\">");
            out.println("<table cellpadding=\"0\">");
            out.println("<tr valign=\"top\">");
            outputLetterTD("A", out);
            outputLetterTD("B", out);
            outputLetterTD("C", out);
            outputLetterTD("D", out);
            outputLetterTD("E", out);
            outputLetterTD("F", out);
            out.println("</tr>");
            out.println("<tr valign=\"top\">");
            outputLetterTD("G", out);
            outputLetterTD("H", out);
            outputLetterTD("I", out);
            outputLetterTD("J", out);
            outputLetterTD("K", out);
            outputLetterTD("L", out);
            out.println("</tr>");
            out.println("<tr valign=\"top\">");
            outputLetterTD("M", out);
            outputLetterTD("N", out);
            outputLetterTD("O", out);
            outputLetterTD("P", out);
            outputLetterTD("Q", out);
            outputLetterTD("R", out);
            out.println("</tr>");
            out.println("<tr valign=\"top\">");
            outputLetterTD("S", out);
            outputLetterTD("T", out);
            outputLetterTD("U", out);
            outputLetterTD("V", out);
            outputLetterTD("W", out);
            outputLetterTD("X", out);
            out.println("</tr>");
            out.println("<tr valign=\"top\">");
            outputLetterTD("Y", out);
            outputLetterTD("Z", out);
        }
        if (user.startsWith("proshop")) {
            if (simple_table) {
                out.print("<td colspan=\"4\"><a class=\"member_search_list_all_button\" href=\"#\">List All</a></td>");
            } else {
                out.println("<td align=\"center\" colspan=\"4\"><a href=\"javascript:void(0);\" onclick=\"subletter('listall')\">List All</a></td>");
            }

        } else if (user.equals("buddy")) {       // if from Member_buddy

            out.println("<td></td>");
            out.println("<td></td>");
            out.println("<td></td>");
            out.println("<td></td>");

        } else {                            // a member
            if (simple_table) {
                out.print("<td colspan=\"4\"><a class=\"member_search_partners_button\" href=\"#\">Partners</a></td>");
            } else {
                out.println("<td align=\"center\" colspan=\"4\"><a href=\"javascript:void(0);\" onclick=\"subletter('partners')\">Partners</a></td>");
            }

        }
        if (simple_table) {
            out.println("</tr></tbody></table></div></div>"); // end simple table
        } else {
            out.println("</tr>");
            out.println("</table>");
            out.println("</div>"); // end res_select_ltr div
            out.println("</div>"); // end sub_instructions
        }

    } // end of getTable

    /**
     * outputLetter - Prints each letter for the letter box
     *
     * @param letter Letter to print
     * @param out Ouput stream
     */
    private static void outputLetterTD(String letter, PrintWriter out) {

        out.print("<td><a href=\"javascript:void(0);\" onclick=\"subletter('" + letter + "')\">" + letter + "</a></td>");

    }

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

            Utilities.logError("Error in nameLists.typeOptions - Club = " + club + ", Exception = " + e.toString());
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
    public static void nameList(String club, String letter, String mshipOpt, String mtypeOpt, boolean ghinOnly,
            parmCourse parmc, PrintWriter out, Connection con) {

        nameList(club, letter, mshipOpt, mtypeOpt, ghinOnly, parmc, true, out, con);

    }

    //
    // teecurr_id was only being passed in to assist in debuging a problem we had in 2009.  Problem was never
    // tracked down but a kernel update on the servers corrected the problem with the processing hanging up
    // within this method.
    //
    public static void nameList(String club, String letter, String mshipOpt, String mtypeOpt, boolean ghinOnly,
            parmCourse parmc, boolean enableAdvAssist, PrintWriter out, Connection con) {

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

        String[] mtypeA = new String[20];            // arrays to hold family names to put in order (Medinah)
        String[] mNameA = new String[20];
        String[] dNameA = new String[20];
        String[] userA = new String[20];

        int i = 0;
        int indexM = 0;

        // include the dynamic search box scripts
        //if (enableAdvAssist) {
            //out.println("<script type=\"text/javascript\" src=\"/" + rev + "/dyn-search.js\"></script>");
        //}
        Utilities.proSlotScripts(out);

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
            }

            //  if (club.equals( "loscoyotes" )) {

            //     orderby = "name_last, name_first, name_mi";                  // special order for Los Coyotes
            //  }


            if ((mshipOpt.equals("") || mshipOpt.equals("ALL") || mshipOpt == null)
                    && (mtypeOpt.equals("") || mtypeOpt.equals("ALL") || mtypeOpt == null)) {   // if both are ALL or not provided

                sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, wc, memNum, username, ghin, gender, posid "
                        + "FROM member2b "
                        + "WHERE " + ((ghinOnly) ? "ghin <> '' AND" : "")
                        + ((letter.equals("%") ? "" : " name_last LIKE '" + letter + "' AND")) + " inact = 0 AND billable = 1 "
                        + "ORDER BY " + orderby;

            } else {      // at least one is specified

                if (!mshipOpt.equals("") && !mshipOpt.equals("ALL") && mshipOpt != null
                        && !mtypeOpt.equals("") && !mtypeOpt.equals("ALL") && mtypeOpt != null) {   // if both were specified

                    sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, wc, memNum, username, ghin, gender, posid "
                            + "FROM member2b "
                            + "WHERE " + ((ghinOnly) ? "ghin <> '' AND" : "") + " name_last LIKE '" + letter + "' AND m_ship = '" + mshipOpt + "' AND m_type = '" + mtypeOpt + "' AND inact = 0 AND billable = 1 "
                            + "ORDER BY " + orderby;

                } else {      // only one is specified

                    if (!mshipOpt.equals("") && !mshipOpt.equals("ALL") && mshipOpt != null) {     // its mship

                        sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, wc, memNum, username, ghin, gender, posid "
                                + "FROM member2b "
                                + "WHERE " + ((ghinOnly) ? "ghin <> '' AND" : "") + " name_last LIKE '" + letter + "' AND m_ship = '" + mshipOpt + "' AND inact = 0 AND billable = 1 "
                                + "ORDER BY " + orderby;

                    } else {             // its mtype

                        sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, m_type, wc, memNum, username, ghin, gender, posid "
                                + "FROM member2b "
                                + "WHERE " + ((ghinOnly) ? "ghin <> '' AND" : "") + " name_last LIKE '" + letter + "' AND m_type = '" + mtypeOpt + "' AND inact = 0 AND billable = 1 "
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

                out.print(" onclick=\"movename(this.form.bname.value)\" onkeypress=\"DYN_moveOnEnterKey(event); return false\">");

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

                i = 0;

                if (parmc != null && parmc.tmode_limit > 0) {

                    loopi3:
                    while (i < parmc.tmode_limit) {             // make sure wc is supported

                        if (parmc.tmodea[i].equals(wc)) {

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

                    //
                    //  Desert Mountain - group by Primary, Spouse, then Dependents
                    //
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
                        if (club.equals("charlottecc") || club.equals("loxahatchee") || club.equals("princessannecc")) {

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

            } // end of WHILE members

            stmt2.close();

        } catch (Exception ex) {

            //doDebug(id, club, teecurr_id, "ERR Inside nameLists.nameList - " + ex.toString());
            Utilities.logError("Error in nameLists.nameList - Club = " + club + ", Exception = " + ex.toString());

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

            Utilities.logError("Error in nameLists.nameList2 - Club = " + club + ", Exception = " + ex.toString());

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
        String username = "";

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
                    "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, username "
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
                username = rs.getString("username");

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

                out.println("<option value=\"" + wname + "\">" + dname + "</option>");    // Normal display

            }            // end of WHILE members

            //
            //  End the row
            //
            out.println("</select>");
            out.println("</font></td></tr>");

        } catch (Exception ex) {

            Utilities.logError("Error in nameLists.nameList_simple - Club = " + club + ", Exception = " + ex.toString());

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
    public static void guestList(String club, String course, String day_name, int time, parmClub parm, boolean lottery, boolean member_side, int activity_id, boolean enableAdvAssist, PrintWriter out, Connection con) {


        int i = 0;
        int x = 0;
        int xCount = 0;


        //
        //     Check the club db table for X and Guest parms specified by proshop
        //
        try {

            getClub.getParms(con, parm, activity_id);        // get the club parms

        } catch (Exception exc) {             // SQL Error - ignore guest and x

            Utilities.logError("Error in nameLists calling getClub.getParms for activity_id=" + activity_id + ", Club = " + club + ", Exception = " + exc.toString());

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

        } else {

            //if (x != 0) xCount = 1;

            for (i = 0; i < parm.MAX_Guests; i++) {

                if (!parm.guest[i].equals("")) {
                    xCount++;
                }
            }

        }

        i = 0;
        if (xCount != 0) {

            if (xCount < 2) {

                xCount = 2;             // set size to at least 2
            }
            if (xCount > 10) {

                xCount = 10;             // set size to no more than 10 showing at once (it will scroll)
            }
            out.println("<tr><td align=\"left\"><font size=\"1\" face=\"Helvetica, Arial, Sans-serif\">");
            out.println("<b>**</b> Add guests immediately<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>after</b> host member.<br>");
            out.println("</font><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

            out.print("<select size=\"" + xCount + "\" name=\"gname\" style=\"cursor:hand\"");

            if (enableAdvAssist) {
                out.print("onClick=\"moveguest(this.form.gname.value)\"");
            } else {
                //         out.print("onChange=\"moveguest(this.form.gname.value)\"");      // this only works once on the iPad
                out.print("onBlur=\"moveguest(this.form.gname.value)\"");          // this works better on the iPad
            }
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

    }          // end of guestList method

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

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String checkUsername = "";
        String checkUsername2 = "";
        String guest_name = "";
        String display_name = "";
        String filterGuestId = "";

        int guest_id = 0;
        int offset = 0;

        boolean isProshop = false;
        boolean guestFound = false;
        boolean guestdbTbaAllowed = false;

        if (ProcessConstants.isProshopUser(user) || Utilities.isHotelUser(user, con)) {
            isProshop = true;
        }

        guestdbTbaAllowed = Utilities.isGuestTrackingTbaAllowed(activity_id, isProshop, con);

        if (caller != 1) {

            // Include the dynamic search box scripts
            //out.println("<script type=\"text/javascript\" src=\"/" + rev + "/dyn-search.js\"></script>");
            Utilities.proSlotScripts(out);

            // Start table
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" width=\"125px\">");

            // Start guest form
            out.println("<form name=\"playerform\">");

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
            out.println("<input style=\"width:125px;\" type=text name=DYN_search onkeyup=\"DYN_triggerChange()\" onkeypress=\"DYN_moveOnEnterKey(event); return DYN_disableEnterKey(event)\" onclick=\"this.select()\" value=\"Quick Search Box\">"); // return DYN_disableEnterKey(event)
            out.println("</td></tr>");

            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("Click on name to select");
            out.println("</font></td></tr>");

            // Guest select
            out.println("<tr><td align=\"center\">");
        }

        // Get applicable guests from database and loop through to display them

        if (caller != 1) {
            if (getClub.getClubName(con).equals("demobrad")) {
                out.println("<select size=\"15\" name=\"bname\" style=\"width:125px; cursor:hand;\" onclick=\"showGUESTPopup(this)\" ondblclick=\"passguest(this.form.bname.value);\" onkeypress=\"DYN_moveOnEnterKey(event); return false;\" style=\"\">");
            } else {
                out.println("<select size=\"15\" name=\"bname\" style=\"width:125px; cursor:hand;\" onClick=\"passguest(this.form.bname.value);\" onkeypress=\"DYN_moveOnEnterKey(event); return false;\" style=\"\">");
            }
            if (guestdbTbaAllowed && caller != 2) {
                out.println("<option value=\"TBA:0\">TBA</option>");
            }
        } else {
            out.println("<select size=\"1\" name=\"guest_id\" style=\"width:125px; cursor:hand;\">");
            if (guestdbTbaAllowed) {
                out.println("<option value=\"-99\">TBA</option>");
            }
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
                    + "CONCAT(d.name_first, ' ', IF(d.name_mi != '', CONCAT(d.name_mi, ' '), ''), d.name_last) as guest_name "
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

                if (caller != 1) {
                    out.println("<option value=\"" + guest_name + ":" + guest_id + "\">" + display_name + "</option>");
                } else {
                    out.println("<option value=\"" + guest_id + "\">" + display_name + "</option>");
                }
            }

            pstmt.close();

            // If no guests found for this member, print [none] option so we can blank out the player upon returning to the slot page on the mobile side
            if (caller == 1 && !guestFound && !guestdbTbaAllowed) {
                out.println("<option value=\"-1\">[none]</option>");
            }

        } catch (Exception exc) {
            out.println("<!-- Error: " + exc.getMessage() + " -->");
        }

        out.println("</select>");

        if (caller != 1) {
            out.println("</td></tr>");
            out.println("</form>");
            out.println("</table>");
        }
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

        Map<String, Map<String, Object>> tempMap = generatePartnerList(user_id, activity_id, mode, con, out, false, false);

    }

    /**
     * displayPartnerList - Prints the partner list select box
     *
     * @param user_id Username of the current user
     * @param activity_id Activity id to get the partner list for
     * @param mode Run mode:  0 = normal, 1 = event (to allow for special needs)
     * @param con Connection to club database
     * @param out Ouput stream
     * @param jquery Flag for indicating if caller is jquery
     */
    public static void displayPartnerList(String user_id, int activity_id, int mode, Connection con, PrintWriter out, boolean jquery) {

        Map<String, Map<String, Object>> tempMap = generatePartnerList(user_id, activity_id, mode, con, out, jquery, false);

    }

    /**
     * getPartnerList - Returns a map object of the partner list
     *
     * @param user_id Username of the current user
     * @param activity_id Activity id to get the partner list for
     * @param mode Run mode:  0 = normal, 1 = event (to allow for special needs)
     * @param con Connection to club database
     */
    public static Map<String, Map<String, Object>> getPartnerList(String user_id, int activity_id, int mode, Connection con, PrintWriter out) {

        return generatePartnerList(user_id, activity_id, mode, con, out, false, true);

    }

    /**
     * generatePartnerList - Prints the partner list select box, or returns a map object of the partner list
     *
     * @param user_id Username of the current user
     * @param activity_id Activity id to get the partner list for
     * @param mode Run mode:  0 = normal, 1 = event (to allow for special needs)
     * @param con Connection to club database
     * @param out Ouput stream
     * @param jquery Flag for indicating if caller is jquery
     * @param generateMap Flag to return Map object of partner list, and not output HTML
     */
    public static Map<String, Map<String, Object>> generatePartnerList(String user_id, int activity_id, int mode, Connection con, PrintWriter out, boolean jquery, boolean generateMap) {

        Map<String, Map<String, Object>> partnerlist_map = new LinkedHashMap<String, Map<String, Object>>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int partner_count = 0;
        int display_partner_hndcp = 0;
        
        float g_hancap = 0;

        String partner_name = "";
        String user_name = "";
        String partner_display = "";
        String partner_wc = "";
        String partner_gender = "";
        String partner_hdcp = "";
        String email = "";
        String email2 = "";
        String user_key = "";
        String style = "";
        String club = getClub.getClubName(con);
        String h_index = "";
        
        boolean isPro = ProcessConstants.isProshopUser(user_id);

        boolean skip = false;


        if (generateMap) {
            jquery = false;
        }

        if (activity_id == 0 && isPro == false) {      // if Golf and a member
           
           // Get this user's option to display the partners' handicap index next to name
           try {
                  pstmt = con.prepareStatement("SELECT display_partner_hndcp FROM member2b WHERE username = ?");
                  pstmt.clearParameters();
                  pstmt.setString(1, user_id);
                  rs = pstmt.executeQuery();

                  if (rs.next()) {
                     
                     display_partner_hndcp = rs.getInt("display_partner_hndcp");
                  }

                  pstmt.close();

           } catch (Exception exc) {
           }
        }

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


        /*
        if(!generateMap){
        out.println("<div class=\"sub_instructions\">");
        out.println("<strong>Partner List</strong> <br />");
        out.println("Click on a name to add");
        out.println("<div class=\"res_select\">");
        out.println("<SELECT NAME=\"partners\" SIZE=\"10\">");
        out.println("<OPTION VALUE=\"####\">McManus, Tom</OPTION>");
        out.println("<OPTION VALUE=\"####\">Parise, Bob</OPTION>");
        out.println("<OPTION VALUE=\"####\">Parise, Joey</OPTION>");
        out.println("<OPTION VALUE=\"####\">Parise, Ryan</OPTION>");
        out.println("<OPTION VALUE=\"####\">Moen, Adam</OPTION>");
        out.println("<OPTION VALUE=\"####\">Moen, Dave</OPTION>");
        out.println("<OPTION VALUE=\"####\">Moen, Sam</OPTION>");
        out.println("<OPTION VALUE=\"####\">Aspenson, Larry</OPTION>");
        out.println("<OPTION VALUE=\"####\">Haubach, Ben</OPTION>");
        out.println("<OPTION VALUE=\"####\">Stepchew, Dan</OPTION>");
        out.println("</SELECT>");
        out.println("</div>");
        out.println("</div>");
        }
         */


        // Start table for partner list display
        if (!jquery && !generateMap) {
            out.println("<div class=\"sub_instructions\">");
            out.println("<strong>Partner List</strong> <br />");
            out.println("Click on a name to add");
            out.println("<div class=\"res_select\">");
        }

        // If 1 or more partners are in this list, print the selection box, otherwise print a message saying no partners exist
        if (partner_count > 0) {

            if (!generateMap) {
                // Print the select box to display all partners for this user/activity_id
                out.println("<select size=\"" + (partner_count < 20 ? (partner_count > 2 ? partner_count : 2) : "20") + "\" name=\"bud\" onclick=\"if(!ftIsMobile()){move_name(this.value)}\" onchange=\"if(ftIsMobile()){move_name(this.value)}\">");
            }
            try {
                pstmt = con.prepareStatement(
                        "SELECT "
                        + "CONCAT(m.name_last, ', ', m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi), '')) AS partner_name, "
                        + "CONCAT(m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi, ' '), ' '), m.name_last) AS partner_display, "
                        + "m.dining_id, m.wc, m.gender, m.ghin, m.username, m.m_ship, m.m_type, m.email, m.email2, m.g_hancap, !ISNULL(ms.mship) as mship_access "
                        + "FROM partner p "
                        + "LEFT OUTER JOIN member2b m ON p.partner_id = m.username "
                        + "LEFT OUTER JOIN mship5 ms ON m.m_ship = ms.mship AND ms.activity_id = ? "
                        + "WHERE p.user_id = ? AND p.activity_id = ? AND m.billable = '1' AND m.inact = '0' "
                        + "ORDER BY p.priority, m.name_last, m.name_first");
                pstmt.clearParameters();
                pstmt.setInt(1, activity_id);
                pstmt.setString(2, user_id);
                pstmt.setInt(3, activity_id);

                rs = pstmt.executeQuery();

                while (rs.next()) {
                    partner_name = rs.getString("partner_name");
                    partner_display = rs.getString("partner_display");
                    partner_wc = rs.getString("m.wc");
                    user_name = rs.getString("username");
                    user_key = "user_" + user_name;
                    partner_gender = rs.getString("m.gender");
                    partner_hdcp = rs.getString("ghin");
                    email = rs.getString("email"); // Note, for security reasons, never add the email address to the response object
                    email2 = rs.getString("email2"); // Note, for security reasons, never add the email address to the response object
                    g_hancap = rs.getFloat("g_hancap");      // get member's handicap index value
                    style = "";
                    skip = false;

                    if (generateMap && mode == 2) {  // email-list and json mode
                        // Exclude this user if no email address, or other custom casea
                        if (((email == null || email.equals("")) && (email2 == null || email2.equals("")))
                                || (!isPro
                                && (club == "treesdalegolf" || user_name == "R0084"))) {
                            skip = true;
                        }
                    }
                
                    if (rs.getInt("mship_access") == 0) {
                        skip = true;
                    }

                    if (club.equals("denvercc")) {
                        if (rs.getString("m_type").equalsIgnoreCase("Dependent")) {
                            style = "color:green;";
                        }
                    }

                    if (!skip) {

                        h_index = "";     // init to not wanted

                        if (display_partner_hndcp > 0) {    // if member wants handicap indexes attached to partner names

                              // convert handicap index to string

                              if (g_hancap == -99) {

                                 h_index = " (N/A)";

                              } else {

                                 if (g_hancap > 0) {
                                       h_index = " (+" +g_hancap+ ")";
                                 } else {
                                       if (g_hancap <= 0) {
                                          g_hancap = 0 - g_hancap;            // convert to non-negative
                                       }
                                       h_index = " (" +g_hancap+ ")";
                                 }    
                              } 
                        }

                        if (generateMap) {
                            partnerlist_map.put(user_key, new LinkedHashMap<String, Object>());
                            ((Map<String, Object>) partnerlist_map.get(user_key)).put("name", partner_name + h_index);
                            ((Map<String, Object>) partnerlist_map.get(user_key)).put("display", partner_display);
                            ((Map<String, Object>) partnerlist_map.get(user_key)).put("wc", partner_wc);
                            ((Map<String, Object>) partnerlist_map.get(user_key)).put("username", user_name);
                            ((Map<String, Object>) partnerlist_map.get(user_key)).put("gender", rs.getString("gender"));
                            ((Map<String, Object>) partnerlist_map.get(user_key)).put("ghin", partner_hdcp);
                            ((Map<String, Object>) partnerlist_map.get(user_key)).put("m_ship", rs.getString("m_ship"));
                            ((Map<String, Object>) partnerlist_map.get(user_key)).put("m_type", rs.getString("m_type"));
                            ((Map<String, Object>) partnerlist_map.get(user_key)).put("style", style);
                        }

                        if (mode == 0) {
                            if (generateMap) {
                                ((Map) partnerlist_map.get(user_key)).put("dining_id", rs.getInt("dining_id"));
                            } else {
                                if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {
                                    // include the member's id in the dining system
                                    out.println("<option" + (!style.equals("") ? " style=\"" + style + "\"" : "") + " value=\"" + partner_display + ":" + rs.getInt("dining_id") + "\">" + partner_name + "</option>");
                                } else {
                                    out.println("<option" + (!style.equals("") ? " style=\"" + style + "\"" : "") + " value=\"" + partner_display + ":" + partner_wc + "\">" + partner_name + h_index + "</option>");
                                }
                            }
                        } else if (mode == 1) {

                            // mode 1 = event.  Need to grab gender and hdcp num


                            while (partner_hdcp.length() < 7) {
                                partner_hdcp = "0" + partner_hdcp;
                            }
                            if (!generateMap) {
                                out.println("<option value=\"" + partner_display + ":" + partner_wc + ":" + partner_gender + ":" + partner_hdcp + "\">" + partner_name + h_index + "</option>");
                            }
                        }
                    }
                }

                pstmt.close();

            } catch (Exception exc) {
                out.println(exc.getMessage());
            }

            if (!generateMap) {
                out.println("</select>");
            }

        } else if (!generateMap) {
            out.println("No names - <br>");
            out.println("Select 'Partner List' <br>");
            out.println("on main menu to add.");
        }

        if (!generateMap) {
            out.println("</div>");
            out.println("</div>");
        }

        return partnerlist_map;

    }      // end of generatePartnerList()

    //******************************************************************************************************
    //   getMemebersFromDistributionList
    //******************************************************************************************************
    //
    public static Map<String, Map<String, Object>> getMemebersFromDistributionList(List<String> lists, int mode, HttpSession session, Connection con)
            throws IOException {

        // get the database table name based on the user
        String table_name = DistributionList.getTableName(session);    // 'dist4' (members) or 'distribution_lists' (proshop) 

        //
        //  Get this user's distribution lists, if any, and list them by name
        //
        String user = (String) session.getAttribute("user");     // get username ('proshop' or member's username)
        String club = (String) session.getAttribute("club");     // get club
        int list_size = DistributionList.getMaxListSize(session) + 1;

        if (user.startsWith("proshop")) {
            list_size = 1;     // unlimited 
        }
        ResultSet rs = null;
        PreparedStatement stmt = null;

        ResultSet rs2 = null;
        PreparedStatement stmt2 = null;

        boolean skip = false;
        String email = "", email2 = "";

        Map<String, Map<String, Object>> namelist_map = new LinkedHashMap<String, Map<String, Object>>();

        String first = "", mid = "", last = "", name_data = "", name_list = "", data = "", user_name = "", partner_display = "", partner_name = "", partner_wc = "";

        for (String list_name : lists) {
            try {

                if (!user.startsWith("proshop")) {     // if member

                    String[] users = new String[list_size];                     // max of 30 users per dist list (start with 1)
                    String uname = "";

                    stmt2 = con.prepareStatement(
                            "SELECT * FROM " + table_name + " WHERE name = ? AND owner = ?");

                    stmt2.clearParameters();               // clear the parms
                    stmt2.setString(1, list_name);
                    stmt2.setString(2, user);
                    rs2 = stmt2.executeQuery();            // execute the prepared stmt

                    if (rs2.next()) {

                        for (int i2 = 1; i2 < list_size; i2++) {               // check all 30 (start with 1)

                            uname = rs2.getString("user" + i2);

                            // add each member from the distribution list
                            if (!uname.equals("")) {

                                // Get member information
                                stmt = con.prepareStatement(
                                        "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, dining_id, wc, username, m_ship, m_type, ghin, gender, "
                                        + "CONCAT(m.name_last, ',', IF(m.name_mi <> '', CONCAT(' ', m.name_mi, ' '), ' '), m.name_first) AS partner_name, "
                                        + "CONCAT(m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi, ' '), ' '), m.name_last) AS partner_display, m.email, m.email2 "
                                        + "FROM member2b AS m "
                                        + "WHERE username = ? AND inact = 0 "
                                        + "ORDER BY last_only, name_first, name_mi");

                                stmt.clearParameters();
                                stmt.setString(1, uname);
                                rs = stmt.executeQuery();

                                if (rs.next()) {
                                    last = rs.getString("name_last");
                                    first = rs.getString("name_first");
                                    mid = rs.getString("name_mi");
                                    user_name = rs.getString("username");
                                    partner_name = rs.getString("partner_name");
                                    partner_display = rs.getString("partner_display");
                                    partner_wc = rs.getString("wc");
                                    email = rs.getString("email"); // Note, for security reasons, never add the email address to the response object
                                    email2 = rs.getString("email2"); // Note, for security reasons, never add the email address to the response object
                                    skip = false;

                                    if (mode == 2) {  // email-list and json mode
                                        // Exclude this user if no email address, or other custom casea
                                        if (((email == null || email.equals("")) && (email2 == null || email2.equals("")))
                                                || ((club.equals("treesdalegolf") || user_name.equals("R0084")))) {
                                            skip = true;
                                        }
                                    }

                                    namelist_map.put(user_name, new LinkedHashMap<String, Object>());
                                    ((Map<String, Object>) namelist_map.get(user_name)).put("name", partner_name);
                                    ((Map<String, Object>) namelist_map.get(user_name)).put("display", partner_display);
                                    ((Map<String, Object>) namelist_map.get(user_name)).put("wc", partner_wc);
                                    ((Map<String, Object>) namelist_map.get(user_name)).put("username", user_name);
                                    ((Map<String, Object>) namelist_map.get(user_name)).put("ghin", rs.getString("ghin"));
                                    ((Map<String, Object>) namelist_map.get(user_name)).put("m_ship", rs.getString("m_ship"));
                                    ((Map<String, Object>) namelist_map.get(user_name)).put("m_type", rs.getString("m_type"));
                                    ((Map<String, Object>) namelist_map.get(user_name)).put("gender", rs.getString("gender"));

                                }

                            }
                        }
                    }

                } else {        // proshop user
                    // This code-path is untested
                    // Get members from distribution list
                    stmt = con.prepareStatement(
                            "SELECT SUBSTRING_INDEX(m.name_last, '_', 1) AS last_only, m.name_last, m.name_first, m.name_mi, m.dining_id, m.wc, "
                            + "m.username, m.m_ship, m.m_type, m.ghin, m.gender, "
                            + "CONCAT(m.name_last, ',', IF(m.name_mi <> '', CONCAT(' ', m.name_mi, ' '), ' '), m.name_first) AS partner_name, "
                            + "CONCAT(m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi, ' '), ' '), m.name_last) AS partner_display, m.email, m.email2 "
                            + "FROM distribution_lists AS dl, distribution_lists_entries AS dle, member2b AS m "
                            + "WHERE dl.name = ? AND dl.owner = ? "
                            + " AND dle.distribution_list_id = dl.distribution_list_id "
                            + " AND m.username = dle.username "
                            + " AND m.inact = 0 "
                            + " ORDER BY last_only, m.name_first, m.name_mi");


                    stmt.clearParameters();
                    stmt.setString(1, list_name);
                    stmt.setString(2, user);

                    rs = stmt.executeQuery();


                    while (rs.next()) {

                        last = rs.getString("name_last");
                        first = rs.getString("name_first");
                        mid = rs.getString("name_mi");
                        user_name = rs.getString("username");
                        partner_name = rs.getString("partner_name");
                        partner_display = rs.getString("partner_display");
                        partner_wc = rs.getString("wc");
                        email = rs.getString("email"); // Note, for security reasons, never add the email address to the response object
                        email2 = rs.getString("email2"); // Note, for security reasons, never add the email address to the response object
                        skip = false;

                        if (mode == 2) {  // email-list and json mode
                            // Exclude this user if no email address, or other custom casea
                            if (((email == null || email.equals("")) && (email2 == null || email2.equals("")))
                                    || ((club.equals("treesdalegolf") || user_name.equals("R0084")))) {
                                skip = true;
                            }
                        }

                        namelist_map.put(user_name, new LinkedHashMap<String, Object>());
                        ((Map<String, Object>) namelist_map.get(user_name)).put("name", partner_name);
                        ((Map<String, Object>) namelist_map.get(user_name)).put("display", partner_display);
                        ((Map<String, Object>) namelist_map.get(user_name)).put("wc", partner_wc);
                        ((Map<String, Object>) namelist_map.get(user_name)).put("username", user_name);
                        ((Map<String, Object>) namelist_map.get(user_name)).put("ghin", rs.getString("ghin"));
                        ((Map<String, Object>) namelist_map.get(user_name)).put("m_ship", rs.getString("m_ship"));
                        ((Map<String, Object>) namelist_map.get(user_name)).put("m_type", rs.getString("m_type"));
                        ((Map<String, Object>) namelist_map.get(user_name)).put("gender", rs.getString("gender"));

                    }

                }

            } catch (Exception exc) {

                Utilities.logError("nameLists.getMemebersFromDistributionList: Error gathering the users for club " + club + ", err=" + exc.toString());

            } finally {

                try {
                    rs.close();
                } catch (Exception ignore) {
                }

                try {
                    stmt.close();
                } catch (Exception ignore) {
                }

                try {
                    rs2.close();
                } catch (Exception ignore) {
                }

                try {
                    stmt2.close();
                } catch (Exception ignore) {
                }

            }
        }
        return namelist_map;
    }
    
     /*
     * Delete Member distribution list 
     */
    public static void deleteMemberDistributionList(String list_name, HttpSession session, Connection con) {
     
        PreparedStatement pstmt = null;

        String user = (String) session.getAttribute("user");     // get username ('proshop' or member's username)
        String table_name = DistributionList.getTableName(session);

        // Check if we already have a list by this name
        try {
            pstmt = con.prepareStatement(
                    "DELETE FROM " + table_name + " WHERE owner = ? and name = ?");

            pstmt.clearParameters();            // clear the parms
            pstmt.setString(1, user);           // put username in statement
            pstmt.setString(2, list_name);           // put username in statement
            int result_count = pstmt.executeUpdate();          // execute the prepared stmt
        } catch (Exception exc) {             // SQL Error
        } finally {
            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }

    }
    
    /*
     * Save Member distribution list 
     */
    public static Map<String, Object> saveMemberDistributionList(String list_name, String new_list_name, boolean new_list_mode, List<String> names, HttpSession session, Connection con) {

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", false);

        String user = (String) session.getAttribute("user");     // get username ('proshop' or member's username)
        String table_name = DistributionList.getTableName(session);

        String name_set = "";
        String sql = "";

        Boolean add_new = true;
        Boolean name_exists_error = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int list_size = DistributionList.getMaxListSize(session);

        if (names.size() > list_size) {

            result.put("message", "Sorry, the maximum number of recipients for distribution lists is " + list_size + ".");

        } else if (names.size() < 1) {

            result.put("message", "Sorry, you must add at least one recipient.");

        } else {

            for (int i = 0; i < list_size; i++) {
                if (i > 0) {
                    name_set += ", ";
                } else {
                    name_set += " SET ";
                }
                name_set += "user" + (i + 1) + " = ?";
            }

            // Check if we already have a list by this name
            try {

                pstmt = con.prepareStatement(
                        "SELECT name FROM " + table_name + " WHERE owner = ? and name = ? ORDER BY name");

                pstmt.clearParameters();            // clear the parms
                pstmt.setString(1, user);           // put username in statement
                pstmt.setString(2, list_name);           // put username in statement
                rs = pstmt.executeQuery();          // execute the prepared stmt

                if (rs.next()) {
                    // This lists exists, so update it
                    if (!new_list_mode) {
                        // check if we are renameing, and if so, check if the rename exists
                        if (new_list_name != null) {
                            pstmt = con.prepareStatement(
                                    "SELECT name FROM " + table_name + " WHERE owner = ? and name = ? ORDER BY name");

                            pstmt.clearParameters();            // clear the parms
                            pstmt.setString(1, user);           // put username in statement
                            pstmt.setString(2, new_list_name);           // put username in statement
                            rs = pstmt.executeQuery();          // execute the prepared stmt
                            if (rs.next()) {
                                name_exists_error = true;
                            }
                        }
                        sql = "UPDATE " + table_name + name_set + ", name = ? WHERE owner = ? and name = ? ORDER BY name";
                    } else {
                        name_exists_error = true;
                        new_list_name = list_name;
                    }

                } else {
                    // List does not exist, insert it
                    new_list_mode = true;
                    sql = "INSERT INTO " + table_name + name_set + ", name = ?, owner = ?";
                }
                if (!name_exists_error) {
                    pstmt = con.prepareStatement(sql);
                    pstmt.clearParameters();            // clear the parms
                    for (int i = 0; i < list_size; i++) {
                        String username = "";
                        try {
                            username = names.get(i);
                        } catch (Exception ignore) {
                        }
                        pstmt.setString(i + 1, username);
                    }
                    if (new_list_name == null) {
                        new_list_name = list_name;
                    }
                    pstmt.setString(list_size + 1, new_list_name);
                    pstmt.setString(list_size + 2, user);
                    if (!new_list_mode) {
                        pstmt.setString(list_size + 3, list_name);
                    }
                    int result_count = pstmt.executeUpdate();          // execute the prepared stmt

                    result.put("success", true);
                } else {
                    result.put("message", "This list name &quot" + new_list_name + "&quot is in use.  Please select a different name.");
                }

            } catch (Exception exc) {             // SQL Error

                //result.put("message", exc.toString() + " :: " + sql);
                result.put("message", "Unknown error trying to save list.  Please try again.  If this continues, contact your golf shop.");

            } finally {
                try {
                    rs.close();
                } catch (Exception ignore) {
                }

                try {
                    pstmt.close();
                } catch (Exception ignore) {
                }
            }
        }

        return result;

    }

    /*
     * Get Member's distribution list names
     */
    public static List<String> getMemberDistributionLists(HttpSession session, Connection con) {

        List<String> result = new ArrayList<String>();
        //
        //  Get this user's distribution lists, if any, and list them by name
        //
        String user = (String) session.getAttribute("user");     // get username ('proshop' or member's username)
        String table_name = DistributionList.getTableName(session);

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con.prepareStatement(
                    "SELECT name FROM " + table_name + " WHERE owner = ? ORDER BY name");

            pstmt.clearParameters();            // clear the parms
            pstmt.setString(1, user);           // put username in statement
            rs = pstmt.executeQuery();          // execute the prepared stmt

            while (rs.next()) {
                result.add(rs.getString("name"));
            }

        } catch (Exception exc) {             // SQL Error
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;
    }

    /**
     * displayNameList - Prints the partner list select box
     *
     * @param letter Letter to use for filtering names
     * @param club Club name for use in future customs
     * @param activity_id Activity id to get the partner list for
     * @param mode Run mode:  0 = normal, other values allow for special needs
     * @param con Connection to club database
     * @param out Ouput stream
     * @param jquery Flag for indicating if caller is jquery
     */
    public static void displayNameList(String letter, String club, int activity_id, int mode, Connection con, PrintWriter out, boolean jquery) {
        Map<String, Map<String, Object>> tempMap = generateNameList(letter, club, activity_id, mode, con, out, jquery, false);
    }

    /**
     * getNameList - Returns name list in map object
     *
     * @param letter Letter to use for filtering names
     * @param club Club name for use in future customs
     * @param activity_id Activity id to get the partner list for
     * @param mode Run mode:  0 = normal, other values allow for special needs
     * @param con Connection to club database
     */
    public static Map<String, Map<String, Object>> getNameList(String letter, String club, int activity_id, int mode, Connection con, PrintWriter out) {
        return generateNameList(letter, club, activity_id, mode, con, out, false, true);
    }

    /**
     * generateNameList - Prints the partner list select box
     *
     * @param letter Letter to use for filtering names
     * @param club Club name for use in future customs
     * @param activity_id Activity id to get the partner list for
     * @param mode Run mode:  0 = normal, other values allow for special needs
     * @param con Connection to club database
     * @param out Ouput stream
     * @param jquery Flag for indicating if caller is jquery
     * @param generateMap Flag to return Map object of partner list, and not output HTML
     */
    public static Map<String, Map<String, Object>> generateNameList(String letter, String club, int activity_id, int mode, Connection con, PrintWriter out, boolean jquery, boolean generateMap) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean skip = false;
        
        int memEmailOpt1 = 0;
        int memEmailOpt2 = 0;

        Map<String, Map<String, Object>> namelist_map = new LinkedHashMap<String, Map<String, Object>>();

        String user_key = "", first = "", mid = "", last = "", name_data = "", name_list = "", data = "", user_name = "", partner_display = "", partner_name = "", partner_wc = "", email = "", email2 = "", style = "";

        if (!generateMap) {
            out.println("<div class=\"sub_instructions\">");
            out.println("<strong>Name List</strong> <br />");
            out.println("Click on a name to add");
            out.println("<div class=\"res_select\">");
        }
        try {

            pstmt = con.prepareStatement(
                    "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, dining_id, wc, username, m_ship, m_type, ghin, gender, "
                    + "CONCAT(m.name_last, ', ', m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi), '')) AS partner_name, "
                    + "CONCAT(m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi, ' '), ' '), m.name_last) AS partner_display, "
                    + "m.email, m.email2, m.memEmailOpt1, m.memEmailOpt2, !ISNULL(ms.mship) as mship_access "
                    + "FROM member2b AS m "
                    + "LEFT OUTER JOIN mship5 AS ms ON m.m_ship = ms.mship AND ms.activity_id = ? "
                    + "WHERE name_last LIKE ? AND inact = 0 "
                    + "ORDER BY last_only, name_first, name_mi");

            pstmt.clearParameters();
            pstmt.setInt(1, activity_id);
            pstmt.setString(2, letter + "%");
            rs = pstmt.executeQuery();
            if (!generateMap) {
                out.println("<select size=\"20\" name=\"bname\" onclick=\"if(!ftIsMobile()){move_name(this.value)}\" onchange=\"if(ftIsMobile()){move_name(this.value)}\">"); // movename(this.form.bname.value)
            }
                               
            while (rs.next()) {         

                last = rs.getString("name_last");
                first = rs.getString("name_first");
                mid = rs.getString("name_mi");
                user_name = rs.getString("username");
                user_key = "user_" + user_name;
                partner_name = rs.getString("partner_name");
                partner_display = rs.getString("partner_display");
                partner_wc = rs.getString("wc");
                email = rs.getString("email"); // Note, for security reasons, never add the email address to the response object
                email2 = rs.getString("email2"); // Note, for security reasons, never add the email address to the response object
                memEmailOpt1 = rs.getInt("memEmailOpt1");
                memEmailOpt2 = rs.getInt("memEmailOpt2");
                style = "";
                skip = false;

                if (generateMap && mode == 2) {  // email-list and json mode
                    // Exclude this user if no email address, or other custom casea
                    if (((email == null || email.equals("")) && (email2 == null || email2.equals("")))
                            || ((club.equals("treesdalegolf") || user_name.equals("R0084")))) {
                        skip = true;
                    }
                    if (skip == false) {   // keep checking?
                       skip = true;        // default to skip this one
                       if ((email != null && !email.equals("") && memEmailOpt1 > 0) || (email2 != null && !email2.equals("") && memEmailOpt2 > 0)) {
                          skip = false;     // ok to add
                       } 
                    }
                }
                
                if (rs.getInt("mship_access") == 0) {
                    skip = true;
                }

                // for name list on handicap pages - only include members that have a ghin value.
                if (mode == 3 && rs.getString("ghin").equals("")) skip = true;
                
                if (club.equals("denvercc")) {
                    if (rs.getString("m_type").equalsIgnoreCase("Dependent")) {
                        style = "color:green;";
                    }
                }

                if (!skip) {

                    if (mid.equals("")) {

                        name_data = first + " " + last;     // name parts to be put in the value for each option (gets moved to name boxes)
                        name_list = last + ", " + first;    // name parts to display in the select box (last first for alpha sorting)

                    } else {

                        name_data = first + " " + mid + " " + last;
                        name_list = last + ", " + first + " " + mid;
                    }

                    if (activity_id == 0) {

                        // include members tmode option
                        data = name_data + ":" + rs.getString("wc");

                    } else if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

                        // include the member's id in the dining system
                        data = name_data + ":" + rs.getInt("dining_id");

                    } else {    // FlxRez activity_id
                        
                        // include the member's name
                        data = name_data;
                    }
                    if (generateMap) {
                        namelist_map.put(user_key, new LinkedHashMap<String, Object>());
                        ((Map<String, Object>) namelist_map.get(user_key)).put("name", partner_name);
                        ((Map<String, Object>) namelist_map.get(user_key)).put("display", partner_display);
                        ((Map<String, Object>) namelist_map.get(user_key)).put("wc", partner_wc);
                        ((Map<String, Object>) namelist_map.get(user_key)).put("username", user_name);
                        ((Map<String, Object>) namelist_map.get(user_key)).put("ghin", rs.getString("ghin"));
                        ((Map<String, Object>) namelist_map.get(user_key)).put("m_ship", rs.getString("m_ship"));
                        ((Map<String, Object>) namelist_map.get(user_key)).put("m_type", rs.getString("m_type"));
                        ((Map<String, Object>) namelist_map.get(user_key)).put("gender", rs.getString("gender"));
                        ((Map<String, Object>) namelist_map.get(user_key)).put("style", style);
                    } else {
                        out.println("<option" + (!style.equals("") ? " style=\"" + style + "\"" : "") + " value=\"" + data + "\">" + name_list + "</option>");
                    }
                }
            }
            if (!generateMap) {
                out.println("</select>");

                out.println("</div>"); // closing res_select div

                out.println("</div>"); // closing sub_instructions div
            }
        } catch (Exception exc) {

            Utilities.logError("nameLists.displayNameList Error: " + exc.toString());
            out.println("nameLists.displayNameList Error: " + exc.toString());



        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }
        return namelist_map;

    }
    /*
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
    
    }
    catch (Exception e2) {
    
    }
    
    }  // end of doDebug
     */
}  // end of nameLists class

