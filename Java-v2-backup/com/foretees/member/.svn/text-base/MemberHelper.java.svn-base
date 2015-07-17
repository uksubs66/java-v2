/***************************************************************************************
 *   MemberHelper:  This utility class contains methods to get information for gathering
 *                 information necessary to create and edit members.
 *
 *
 *   created: 10/03/2003   jag
 *
 *
 *   last updated:
 *
 *            11/22/13  Add a send email type field to getEmailAddress(es) when getting email addresses so they can be filtered by the member's subscription settings.
 *             9/12/13  Overlake G&CC (overlakegcc) - Added "Past President" to the member sub-type list (case 2298).
 *             5/01/13  Brookside CC (brooksidecountryclub) - Added "League" to the member sub-type list (case 2251).
 *             1/03/13  Add Handicap Chair member subtype.
 *             5/10/12  Riviera CC (rivieracc) - Added "RWGA" to the member sub-type list.
 *             2/01/12  Added Employee subtype value to use as a new skin indicator for members to test the new skin.
 *             6/02/11  Added "GOM" and "Friday Boys" to the member sub-type list.
 *             2/10/11  Add SYSCONFIG_MANAGECONTENT to proshop users for Custom Email Content feature.
 *             4/21/10  Changes to add support for unlimited guest types
 *             9/03/09  Updated 2 locations to pull mships from mehip5 table instead of club5
 *             8/31/09  Rearranged the order of checkboxes in the addLimitedAccessTypesToForm() method so they are listed in roughly alphabetical order
 *             8/31/09  Added addActivitySelectorToForm() method to print out the proper activity selection box for the action being taken
 *             6/05/09  Interlachen - add mem_subtype of "Member Guest Pass" (case 1686).
 *             4/24/09  Treesdale Golf - Don't display Ben Roethlisberger's email in member selection window, adding to dist. lists, 
 *                      and don't import from partner list for members only (case 1660).
 *             2/03/09  Add DEMOCLUBS_CHECKIN, DEMOCLUBS_MANAGE, DINING_REQUEST, and DINING_CONFIG fields to addLimitedAccessTypesToForm
 *             1/26/09  Hazeltine - add subtype of Invite Priority for their Invitiational Event (case 1585).
 *             9/03/08  Fix related to 9/02/08 update, Check/Uncheck All button fixed
 *             9/02/08  Commented out TS_CTRL_EMAIL limited access proshop restriction
 *             8/19/08  Added addTeeSheetOptionsToForm() method to add checkboxes for each proshop tee sheet option
 *             8/11/08  SYSCONFIG access types added to addLimitedAccessTypesToForm() and NUM_LIMITED_ACCESS_TYPES raised from 27 to 33
 *             8/08/08  Updated javascript in addLimitedAccessTypesToForm() method.
 *             7/14/08  Do not include excluded members (billable = 0) in email lists.
 *             7/14/08  Added addLimitedAccessTypesToForm() method to add Checkboxes for each proshop limited access feature
 *             5/16/08  Do not include inactive members in email lists.
 *             4/24/08  Update ArrayList to use String instead of raw types
 *             3/15/07  Add hdcp club & association numbers
 *            10/06/06  pts modified email retrieving query to only return if _bounced flags are 0
 *             9/02/05  rdp verify the email addresses before adding to list
 *             1/24/05  rdp changed cub db table from club2 to club5 for ver 5
 *             1/11/05  jag added method to get all email addresses for a particular user
 *             1/20/04  jag added method to query database for member names
 *
 *
 *
 ***************************************************************************************
 */

package com.foretees.member;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import javax.servlet.http.*;


import com.foretees.client.attribute.Checkbox;
import com.foretees.client.attribute.SelectionList;
import com.foretees.client.action.ActionHelper;
import com.foretees.client.ScriptHelper;
import com.foretees.client.form.FormModel;
import com.foretees.client.layout.LayoutModel;
import com.foretees.member.Member;
import com.foretees.client.table.RowModel;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.FeedBack;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Labels;
import com.foretees.common.reqUtil;
import com.foretees.common.Utilities;


/**
 ***************************************************************************************
 *
 *  This helper class contains methods for commonly used functions related to
 *  creating and editing members.
 *
 ***************************************************************************************
 **/

public class MemberHelper {

  public static String rev = ProcessConstants.REV;

  public static final int NUM_MEMBER_TYPES = Labels.MAX_MEMS;
  public static final int NUM_MEMBERSHIPS = Labels.MAX_MSHIPS;
  public static final int NUM_HOTEL_GUEST_TYPES = Labels.MAX_GUESTS;
  public static final int NUM_GUEST_TYPES = Labels.MAX_GUESTS;
  public static final int NUM_MEMBER_SUB_TYPES = 22;        // for Hazeltine Natl & Interlachen & others - Custom
  public static final int NUM_LIMITED_ACCESS_TYPES = 42;    // used for limited access proshop users
  public static final int NUM_TEE_SHEET_OPTIONS = 3;        // used for limited access proshop users


  /**
  ***************************************************************************************
  *
  * This method will return a selection list of all the  hdcp club numbers for the club
  *
  * @param sl the selection list that contains the member types
  *
  ***************************************************************************************
  **/
  
  public static SelectionList getHdcpClubNums(Connection con, String selected, String type) throws SQLException {

    Statement stmtc = con.createStatement();        // create a statement

    if (selected == null) selected = "";
    if (con == null) throw new SQLException("Invalid connection");

    Member member = new Member();
    String [] clubNums = new String [20];

    if (type == null || type.equals("")) type = member.HDCP_CLUB_NUM;

    SelectionList sl = new SelectionList(type, member.HDCP_CLUB_NUM_LABEL, true);
    boolean isSelected = false;

    ResultSet rs = stmtc.executeQuery("SELECT * FROM hdcp_club_num");

    sl.addOption("", "0", isSelected);
    
    while (rs.next()) {

          isSelected = false;
          if (!selected.equals( "" ) && selected.equals( rs.getString("hdcp_club_num_id") )) isSelected = true;
          sl.addOption(rs.getString("club_num"), rs.getString("hdcp_club_num_id"), isSelected);
        
    }

    stmtc.close();

    return sl;

  }
  
  
  /**
  ***************************************************************************************
  *
  * This method will return a selection list of all the hdcp club association numbers for the club
  *
  * @param sl the selection list that contains the member types
  *
  ***************************************************************************************
  **/
  
  public static SelectionList getHdcpAssocNums(Connection con, String selected, String type) throws SQLException {

    Statement stmtc = con.createStatement();        // create a statement

    if (selected == null) selected = "";
    if (con == null) throw new SQLException("Invalid connection");

    Member member = new Member();
    String [] assocNums = new String [20];

    if (type == null || type.equals("")) type = member.HDCP_ASSOC_NUM;

    SelectionList sl = new SelectionList(type, member.HDCP_ASSOC_NUM_LABEL, true);
    boolean isSelected = false;

    ResultSet rs = stmtc.executeQuery("SELECT * FROM hdcp_assoc_num");

    sl.addOption("", "0", isSelected);
          
    while (rs.next()) {

          isSelected = false;
          if (!selected.equals( "" ) && selected.equals( rs.getString("hdcp_assoc_num_id") )) isSelected = true;
          sl.addOption(rs.getString("assoc_num"), rs.getString("hdcp_assoc_num_id"), isSelected);
        
    }

    stmtc.close();

    return sl;

  }
  
  
  public static SelectionList getGenderTypes(String selected, String type) {
        
    Member member = new Member();
    
    if (selected == null) selected = "";
    if (type == null || type.equals("")) type = member.GENDER;
    
    SelectionList sl = new SelectionList(type, member.GENDER_LABEL, true);
    
    sl.addOption("", "0", selected.equals(""));
    sl.addOption("M", "M", selected.equals("M"));
    sl.addOption("F", "F", selected.equals("F"));
    
    return sl;
    
  }
  
  
  /**
  ***************************************************************************************
  *
  * This method will return a selection list of all the member types for the club
  *
  * @param sl the selection list that contains the member types
  *
  ***************************************************************************************
  **/

  public static SelectionList getMemberTypes(Connection con, String selected, String type) throws SQLException {

    Statement stmtc = con.createStatement();        // create a statement

    if (selected == null) selected = "";
    if (con == null) throw new SQLException("Invalid connection");

    Member member = new Member();
    String [] memType = new String [NUM_MEMBER_TYPES];     // member types

    if (type == null || type.equals("")) type = member.MEM_TYPE;

    SelectionList sl = new SelectionList(type, member.MEM_TYPE_LABEL, true);
    boolean isSelected = false;

    //
    //  Get the Member Types and save them in a Selection List
    //
    ResultSet rs = stmtc.executeQuery("SELECT mem1, mem2, mem3, mem4, mem5, mem6, mem7, mem8, " +
                                      "mem9, mem10, mem11, mem12, mem13, mem14, mem15, mem16, " +
                                      "mem17, mem18, mem19, mem20, mem21, mem22, mem23, mem24 " +
                              "FROM club5 WHERE clubName != ''");

    if (rs.next()) {

      for (int i=0; i<NUM_MEMBER_TYPES; i++) {
        memType[i] = rs.getString(i+1);
        if (memType[i] != null && !(memType[i].equals("")))
        {
          isSelected = false;
          if (!selected.equals( "" ) && selected.equals( memType[i] )) isSelected = true;
          sl.addOption(memType[i], memType[i], isSelected);
        }
      }
    }

    stmtc.close();

    return sl;

  }

  /**
  ***************************************************************************************
  *
  * This method will return a selection list of all the member sub-types for clubs
  * that need to identify a group of members (mainly used for customs).  
  *
  * @param sl the selection list that contains the member types
  *
  ***************************************************************************************
  **/

  public static SelectionList getMemberSubTypes(Connection con, String selected, String type) throws SQLException {


    if (selected == null) selected = "";

    Member member = new Member();
    String [] memSubType = new String [NUM_MEMBER_SUB_TYPES];     // member sub-types

    if (type == null || type.equals("")) type = member.MEM_SUB_TYPE;

    SelectionList sl = new SelectionList(type, member.MEM_SUB_TYPE_LABEL, true);
    boolean isSelected = false;

    //
    //  Get the Member Types and save them in a Selection List
    //
    memSubType[0] = "";

       isSelected = false;
       sl.addOption(memSubType[0], memSubType[0], isSelected);

    memSubType[1] = "After Hours";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[1] )) isSelected = true;
       sl.addOption(memSubType[1], memSubType[1], isSelected);

    memSubType[2] = "18 Holer";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[2] )) isSelected = true;
       sl.addOption(memSubType[2], memSubType[2], isSelected);

    memSubType[3] = "9 Holer";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[3] )) isSelected = true;
       sl.addOption(memSubType[3], memSubType[3], isSelected);

    memSubType[4] = "AH-18 Holer";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[4] )) isSelected = true;
       sl.addOption(memSubType[4], memSubType[4], isSelected);

    memSubType[5] = "AH-9 Holer";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[5] )) isSelected = true;
       sl.addOption(memSubType[5], memSubType[5], isSelected);

    memSubType[6] = "AH-9/18 Holer";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[6] )) isSelected = true;
       sl.addOption(memSubType[6], memSubType[6], isSelected);

    memSubType[7] = "9/18 Holer";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[7] )) isSelected = true;
       sl.addOption(memSubType[7], memSubType[7], isSelected);

    memSubType[8] = "Ladies";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[8] )) isSelected = true;
       sl.addOption(memSubType[8], memSubType[8], isSelected);
       
    memSubType[9] = "Invite Priority";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[9] )) isSelected = true;
       sl.addOption(memSubType[9], memSubType[9], isSelected);
       
    memSubType[10] = "Member Guest Pass";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[10] )) isSelected = true;
       sl.addOption(memSubType[10], memSubType[10], isSelected);

    memSubType[11] = "GOM";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[11] )) isSelected = true;
       sl.addOption(memSubType[11], memSubType[11], isSelected);

    memSubType[12] = "Friday Boys";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[12] )) isSelected = true;
       sl.addOption(memSubType[12], memSubType[12], isSelected);
       
    memSubType[13] = "Employee";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[13] )) isSelected = true;
       sl.addOption(memSubType[13], memSubType[13], isSelected);
       
    memSubType[14] = "RWGA";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[14] )) isSelected = true;
       sl.addOption(memSubType[14], memSubType[14], isSelected);
       
    memSubType[15] = "Handicap Chair";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[15] )) isSelected = true;
       sl.addOption(memSubType[15], memSubType[15], isSelected);
       
    memSubType[16] = "League";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[16] )) isSelected = true;
       sl.addOption(memSubType[16], memSubType[16], isSelected);
       
    memSubType[17] = "Past President";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[17] )) isSelected = true;
       sl.addOption(memSubType[17], memSubType[17], isSelected);
       
    memSubType[18] = "MGA";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[18] )) isSelected = true;
       sl.addOption(memSubType[18], memSubType[18], isSelected);  
       
    memSubType[19] = "BCM";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[19] )) isSelected = true;
       sl.addOption(memSubType[19], memSubType[19], isSelected); 

    memSubType[20] = "App Tester";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[20] )) isSelected = true;
       sl.addOption(memSubType[20], memSubType[20], isSelected);

    memSubType[21] = "Hide Member";

       isSelected = false;
       if (!selected.equals( "" ) && selected.equals( memSubType[21] )) isSelected = true;
       sl.addOption(memSubType[21], memSubType[21], isSelected);
       
    return sl;

  }

  /**
  ***************************************************************************************
  *
  * This method will return a selection list of all the membership types for the club
  *
  * @param sl the selection list that contains the membership types
  *
  ***************************************************************************************
  **/

  public static SelectionList getMemberShips(Connection con, String selected, String type) throws SQLException {

    Statement stmt = con.createStatement();        // create a statement

    if (selected == null) selected = "";
    if (con == null) throw new SQLException("Invalid connection");
    Member member = new Member();
    String [] memship = new String [NUM_MEMBERSHIPS];     // member types

    if (type == null || type.equals("")) type = member.MEMSHIP_TYPE;

    SelectionList sl = new SelectionList(type, member.MEMSHIP_TYPE_LABEL, true);
    boolean isSelected = false;

    //
    //  Get the Member Types and save them in a Selection List
    //
    ResultSet rs = stmt.executeQuery("SELECT mship FROM mship5 GROUP BY mship LIMIT " + NUM_MEMBERSHIPS);

    int i = 0;

    while (rs.next()) {

        memship[i] = rs.getString("mship");
        if (memship[i] != null && !(memship[i].equals("")))
        {
            isSelected = false;
            if (!selected.equals( "" ) && selected.equals( memship[i] )) isSelected = true;
            sl.addOption(memship[i], memship[i], isSelected);
        }
        
        i++;
    }

    stmt.close();

    return sl;

  }

  /**
  ***************************************************************************************
  *
  * This method will return a selection list that contains all the transportation options
  * available for the club
  *
  * @param con the database connection to use to query for the member types and membership types
  * @param selected the string containing the selected transportation option
  *
  ***************************************************************************************
  **/

  public static SelectionList getWalkCartOptions(Connection con, String selected) throws Exception
  {

    if (selected == null) selected = "";
    if (con == null) throw new SQLException("Invalid connection");
    
    Member member = new Member();

    //
    //  parm block to hold the course parameters
    //
    parmCourse parmc = new parmCourse();          // allocate a parm block

    //
    //  Get the walk/cart options available for this club
    //
    getParms.getCourseTrans(con, parmc);



    SelectionList walkCart = new SelectionList(member.WALK_CART, member.WALK_CART_LABEL, false);
    boolean isSelected = false;

    for (int i=0; i<16; i++) {        // get all c/w options (tmodea = acronym, tmode = full name)

       if (!parmc.tmodea[i].equals( "" )) {

          isSelected = selected.equals( parmc.tmodea[i] );
          walkCart.addOption(parmc.tmode[i], parmc.tmodea[i], isSelected);
       }
    }

    return walkCart;
  }


  /**
  ***************************************************************************************
  *
  * This method will return a selection list of all the wlak/cart options for the club
  *
  * @param walkCart the selection list that contains the walk/cart options
  *
  ***************************************************************************************
  **/

  public static SelectionList getWalkCartOptionsAll(Connection con, String selected, String wcLstName) throws Exception {


    if (selected == null) selected = "";
    if (con == null) throw new SQLException("Invalid connection");
    
    Member member = new Member();

    //
    //  parm block to hold the course parameters
    //
    parmCourse parmc = new parmCourse();          // allocate a parm block

    //
    //  Get the walk/cart options available for this club
    //
    getParms.getCourseTrans(con, parmc);

    SelectionList walkCart = new SelectionList(wcLstName, member.WALK_CART_LABEL, false);
    boolean isSelected = false;

    for (int i=0; i<16; i++) {        // get all c/w options (tmodea = acronym, tmode = full name)

       if (!parmc.tmodea[i].equals( "" )) {

          isSelected = selected.equals( parmc.tmodea[i] );
          walkCart.addOption(parmc.tmode[i], parmc.tmodea[i], isSelected);
       }
    }

    return walkCart;
  }

  
  
  /**
  ***************************************************************************************
  *
  * This method will return a selection list that contains all the guest types available
  * for the club
  *
  * @param con the database connection to use to query for the member types and membership types
  * @param selected the string array containing all the values selected for this hotel user
  ***************************************************************************************
  **/

  public static RowModel getGuestTypes(Connection con, String[] selected) throws SQLException
  {

    if (con == null) throw new SQLException("Invalid connection");
    Member member = new Member();

    String [] guestTypes; // = new String [NUM_GUEST_TYPES];     // guest types

    // Get Guest Types from the club db
    guestTypes = getGuestTypesFromDB(con);

    RowModel guests = new RowModel();
    guests.setId("guestRow");

    if (guestTypes.length > 0) {

      boolean isSelected = false;

      for (int i=0; i<guestTypes.length; i++)
      {
        String type = guestTypes[i];
        if (type != null && !(type.equals("")))
        {
          isSelected = isSelectedGuestType(selected, type);

          Checkbox cb = new Checkbox((Member.GUEST_TYPE_REQ + (i+1)), type, type, isSelected);
          guests.add(cb, "frm");
        }
      }

    }
    return guests;
  }


  /**
  ***************************************************************************************
  *
  * This method true or false whether the value specified is one that is selected
  *
  * @param selected the selected values
  * @param theValue the value to test if selected
  ***************************************************************************************
  **/

  private static boolean isSelectedGuestType(String[] selected, String theValue)
  {
    boolean isSelected = false;

    if (selected != null && !(selected.equals("")))
    {

      for (int i=0; i<selected.length; i++)
      {
        if ((selected[i]).equals(theValue))
        {
          isSelected = true;
        }

      }
    }

    return isSelected;
  }

  /**
  ***************************************************************************************
  *
  * This method true or false whether the value specified is one that is selected
  *
  * @param selected the selected values
  * @param theValue the value to test if selected
  ***************************************************************************************
  **/

  private static boolean isSelectedMemType(String[] selected, String theValue)
  {
    boolean isSelected = false;

    if (selected != null && !(selected.equals("")))
    {

      for (int i=0; i<selected.length; i++)
      {
        if ((selected[i]).equals(theValue))
        {
          isSelected = true;
        }

      }
    }

    return isSelected;
  }

  /**
  ***************************************************************************************
  *
  * This method true or false whether the value specified is one that is selected
  *
  * @param selected the string array containing the selected values
  * @param theValue the value to scan the string array with
  ***************************************************************************************
  **/

  private static boolean isSelectedMShipType(String[] selected, String theValue)
  {
    boolean isSelected = false;

    if (selected != null && !(selected.equals("")))
    {

      for (int i=0; i<selected.length; i++)
      {
        if ((selected[i]).equals(theValue))
        {
          isSelected = true;
        }

      }
    }

    return isSelected;
  }

  /**
  ***************************************************************************************
  *
  * This method will take all of the selected guest types and set them as values in the
  * sql statement (for Hotel Users)
  *
  * @param req the http request object reference 
  * @param stmt reference to the stmt object that is being manipulated by this method
  * @param out the PrintWriter object reference
  *
  ***************************************************************************************
  **/
/**** NO LONGER USED
  public static void setGuestTypesInStatement(HttpServletRequest req,  PreparedStatement stmt, PrintWriter out) throws SQLException
  {

    int colEntryStart = 13;      // first guest type column in table

    for (int i=0; i<NUM_HOTEL_GUEST_TYPES; i++)
    {

        String type = req.getParameter(Member.GUEST_TYPE_REQ + (i + 1));

        if (type != null && !(type.equals("")))
        {
          stmt.setString(i+colEntryStart, type);
        }
        else
        {
          stmt.setString(i+colEntryStart, "");
        }

    }

  }
*/

  /**
  ***************************************************************************************
  *
  * This method will get all the guest types for a given club
  *
  * @param con the database connection
  *
  ***************************************************************************************
  **/

  private static String[] getGuestTypesFromDB (Connection con) throws SQLException
  {

    int i = 0;

   // Get Guest Types from the club db
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT guest FROM guest5 WHERE activity_id = 0");

    rs.last();

    String [] guestTypes = new String [rs.getRow()];

    rs.beforeFirst();

    while (rs.next()) {

      guestTypes[i] = rs.getString("guest");

      i++;
    }

    stmt.close();

    return guestTypes;
  }
  
  /**
  ***************************************************************************************
  *
  * This method will add the guest types to the form provided
  *
  * @param con the database connection
  * @param selected the string array containing the guest types currently selected
  * @param form the form to be updated
  *
  ***************************************************************************************
  **/

  public static void addGuestTypesToForm(Connection con, String[] selected, FormModel form) throws SQLException
  {

    String [] guestTypes; // = new String [NUM_GUEST_TYPES];     // guest types

    if (con == null) throw new SQLException("Invalid connection");

    // Get Guest Types from the club db
    guestTypes = getGuestTypesFromDB(con);

    RowModel guests1 = new RowModel();
    RowModel guests2 = new RowModel();
    RowModel guests3 = new RowModel();
    RowModel guests4 = new RowModel();
    RowModel guests5 = new RowModel();
    RowModel guests6 = new RowModel();
    guests1.setId("guestRow1");
    guests2.setId("guestRow2");
    guests3.setId("guestRow3");
    guests4.setId("guestRow4");
    guests5.setId("guestRow5");
    guests6.setId("guestRow6");

    LayoutModel layout = new LayoutModel();
    layout.setId("guestTypes");
    layout.setNumColumns(form.getNumColumns());


    if (guestTypes.length > 0) {

      boolean isSelected = false;

      for (int i=0; i<guestTypes.length; i++)
      {
        String type = guestTypes[i];
        if (type != null && !(type.equals("")))
        {
          isSelected = isSelectedGuestType(selected, type);

          Checkbox cb = new Checkbox((Member.GUEST_TYPE_REQ + (i+1)), type, type, isSelected);

          if (i < guestTypes.length - 30) {

            guests1.add(cb, "frm");

          } else {

            if (i < guestTypes.length - 24) {

              guests2.add(cb, "frm");

            } else {

              if (i < guestTypes.length - 18) {

                guests3.add(cb, "frm");

              } else {

                if (i < guestTypes.length - 12) {

                  guests4.add(cb, "frm");

                } else {

                  if (i < guestTypes.length - 6) {

                    guests5.add(cb, "frm");

                  } else {

                    guests6.add(cb, "frm");
                  }
                }
              }
            }
          }
        }   // end of IF type
      }     // end of FOR loop

    }      // end of IF length > 0

    layout.addRow(guests1);

    if (guests2.size() > 0)
    {
      layout.addRow(guests2);
    }
    if (guests3.size() > 0)
    {
      layout.addRow(guests3);
    }
    if (guests4.size() > 0)
    {
      layout.addRow(guests4);
    }
    if (guests5.size() > 0)
    {
      layout.addRow(guests5);
    }
    if (guests6.size() > 0)
    {
      layout.addRow(guests6);
    }

    form.addRow(layout);


  }  // end of addGuestTypesToForm

    /**
  ***************************************************************************************
  *
  * This method will add the limited access types to the form provided
  *
  * @param form the form to be updated
  *
  ***************************************************************************************
  **/

  public static void addLimitedAccessTypesToForm(FormModel form, ResultSet rs, PrintWriter out, boolean newUser) {
      
      boolean isSelected = true;
      boolean isProshop = true;
      
      int cb_num = 1;
      
      try {
          out.println("<script type=\"text/javascript\">");
          out.println("<!--");
          out.println("function toggleFeatureAccess() {");
          out.println("  var ltdBool = new Boolean();");
          out.println("  ltdBool = !(document.forms['pgFrm'].ltd1.checked);");
          out.println("  var i = 1;");
          out.println("  for(i=1;i<=" + MemberHelper.NUM_LIMITED_ACCESS_TYPES + ";i++) {");
          out.println("    eval(\"document.forms['pgFrm'].ltd\" + i + \".checked = ltdBool;\")");
          out.println("  }");
          out.println("}");
          out.println("// -->");
          out.println("</script>");
          
          //Construct rows for Limited Access Type checkboxes
          RowModel ltdAccessHeaderRow = new RowModel();
          ltdAccessHeaderRow.setId("ltdAccessHeaderRow");
          String ltdAccessHeader = "<b>Feature Access</b>:";
          ltdAccessHeaderRow.add(ltdAccessHeader, "frm");
          form.addRow(ltdAccessHeaderRow);

          
          RowModel cb = null;
          
          cb = new RowModel();
          cb.setId("DEMOCLUBS_MANAGE");
          if (!newUser) { isSelected = rs.getBoolean("DEMOCLUBS_MANAGE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Demo Clubs - Add/Remove Demo Clubs", "DEMOCLUBS_MANAGE", isSelected, isProshop), "frm");
          form.addRow(cb);     
          
          cb = new RowModel();
          cb.setId("DEMOCLUBS_CHECKIN");
          if (!newUser) { isSelected = rs.getBoolean("DEMOCLUBS_CHECKIN"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Demo Clubs - Check Demo Clubs In/Outx", "DEMOCLUBS_CHECKIN", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("DINING_CONFIG");
          isSelected = false;       // Off by default
          if (!newUser) { isSelected = rs.getBoolean("DINING_CONFIG"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Dining - Configuration", "DINING_CONFIG", isSelected, isProshop), "frm");
          isSelected = true;
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("DINING_REQUEST");
          if (!newUser) { isSelected = rs.getBoolean("DINING_REQUEST"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Dining - Submit Dining Requests", "DINING_REQUEST", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("EVNTSUP_UPDATE");
          if (!newUser) { isSelected = rs.getBoolean("EVNTSUP_UPDATE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Event Setup - Add/Change Event Registrations", "EVNTSUP_UPDATE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("EVNTSUP_MANAGE");
          if (!newUser) { isSelected = rs.getBoolean("EVNTSUP_MANAGE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Event Setup - Manage Event Registrations", "EVNTSUP_MANAGE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("EVNTSUP_VIEW");
          if (!newUser) { isSelected = rs.getBoolean("EVNTSUP_VIEW"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Event Setup - View Event Registrations", "EVNTSUP_VIEW", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("LESS_UPDATE");
          if (!newUser) { isSelected = rs.getBoolean("LESS_UPDATE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Lessons - Add/Change Member Lessons", "LESS_UPDATE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("LESS_CONFIG");
          if (!newUser) { isSelected = rs.getBoolean("LESS_CONFIG"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Lessons - Configuration of Lesson Books, etc.", "LESS_CONFIG", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("LESS_VIEW");
          if (!newUser) { isSelected = rs.getBoolean("LESS_VIEW"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Lessons - View Lesson Books", "LESS_VIEW", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("LOTT_UPDATE");
          if (!newUser) { isSelected = rs.getBoolean("LOTT_UPDATE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Lottery - Add/Change Lottery Requests", "LOTT_UPDATE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("LOTT_APPROVE");
          if (!newUser) { isSelected = rs.getBoolean("LOTT_APPROVE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Lottery - Approve Lotteries", "LOTT_APPROVE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("REPORTS");
          if (!newUser) { isSelected = rs.getBoolean("REPORTS"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Reports", "REPORTS", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("REST_OVERRIDE");
          if (!newUser) { isSelected = rs.getBoolean("REST_OVERRIDE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Restrictions - Override", "REST_OVERRIDE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("SYSCONFIG_CLUBCONFIG");
          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_CLUBCONFIG"); }
          cb.add(new Checkbox("ltd" + cb_num++, "System Configuration - Club Configuration", "SYSCONFIG_CLUBCONFIG", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("SYSCONFIG_EVENT");
          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_EVENT"); }
          cb.add(new Checkbox("ltd" + cb_num++, "System Configuration - Event", "SYSCONFIG_EVENT", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("SYSCONFIG_LOTTERY");
          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_LOTTERY"); }
          cb.add(new Checkbox("ltd" + cb_num++, "System Configuration - Lottery", "SYSCONFIG_LOTTERY", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("SYSCONFIG_MEMBERNOTICES");
          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_MEMBERNOTICES"); }
          cb.add(new Checkbox("ltd" + cb_num++, "System Configuration - Member Notices", "SYSCONFIG_MEMBERNOTICES", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("SYSCONFIG_RESTRICTIONS");
          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_RESTRICTIONS"); }
          cb.add(new Checkbox("ltd" + cb_num++, "System Configuration - Restrictions", "SYSCONFIG_RESTRICTIONS", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("SYSCONFIG_TEESHEETS");
          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_TEESHEETS"); }
          cb.add(new Checkbox("ltd" + cb_num++, "System Configuration - Tee Sheets", "SYSCONFIG_TEESHEETS", isSelected, isProshop), "frm");
          form.addRow(cb);
              
          cb = new RowModel();
          cb.setId("SYSCONFIG_WAITLIST");
          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_WAITLIST"); }
          cb.add(new Checkbox("ltd" + cb_num++, "System Configuration - Waitlist", "SYSCONFIG_WAITLIST", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_CHECKIN");
          if (!newUser) { isSelected = rs.getBoolean("TS_CHECKIN"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - All - Check Players In/Out", "TS_CHECKIN", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_PRINT");
          if (!newUser) { isSelected = rs.getBoolean("TS_PRINT"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - All - Print Tee Sheets", "TS_PRINT", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_POS");
          if (!newUser) { isSelected = rs.getBoolean("TS_POS"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - All - Send POS Charges", "TS_POS", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_NOTES_VIEW");
          if (!newUser) { isSelected = rs.getBoolean("TS_NOTES_VIEW"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - All - View Notes", "TS_NOTES_VIEW", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_NOTES_UPDATE");
          if (!newUser) { isSelected = rs.getBoolean("TS_NOTES_UPDATE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - All - Update Notes", "TS_NOTES_UPDATE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_UPDATE");
          if (!newUser) { isSelected = rs.getBoolean("TS_UPDATE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - Current - Add/Change Tee Times", "TS_UPDATE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_VIEW");
          if (!newUser) { isSelected = rs.getBoolean("TS_VIEW"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - Current - View Tee Sheets", "TS_VIEW", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_PAST_UPDATE");
          if (!newUser) { isSelected = rs.getBoolean("TS_PAST_UPDATE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - Past - Add/Change Tee Times", "TS_PAST_UPDATE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_PAST_VIEW");
          if (!newUser) { isSelected = rs.getBoolean("TS_PAST_VIEW"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - Past - View Tee Sheets", "TS_PAST_VIEW", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_CTRL_TSEDIT");
          if (!newUser) { isSelected = rs.getBoolean("TS_CTRL_TSEDIT"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - Control Panel - Edit Tee Sheets", "TS_CTRL_TSEDIT", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_CTRL_FROST");
          if (!newUser) { isSelected = rs.getBoolean("TS_CTRL_FROST"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - Control Panel - Frost Delay", "TS_CTRL_FROST", isSelected, isProshop), "frm");
          form.addRow(cb);

          /* // removed for now
          cb = new RowModel();
          cb.setId("TS_CTRL_EMAIL");
          if (!newUser) { isSelected = rs.getBoolean("TS_CTRL_EMAIL"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - Control Panel - Send Emails", "TS_CTRL_EMAIL", isSelected, isProshop), "frm");
          form.addRow(cb);
          */

          cb = new RowModel();
          cb.setId("TS_PACE_UPDATE");
          if (!newUser) { isSelected = rs.getBoolean("TS_PACE_UPDATE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - Pace of Play - Update PoP Entries", "TS_PACE_UPDATE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TS_PACE_VIEW");
          if (!newUser) { isSelected = rs.getBoolean("TS_PACE_VIEW"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tee Sheets - Pace of Play - View PoP Information", "TS_PACE_VIEW", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TOOLS_ANNOUNCE");
          if (!newUser) { isSelected = rs.getBoolean("TOOLS_ANNOUNCE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tools - Announcement Page Changes", "TOOLS_ANNOUNCE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TOOLS_HDCP");
          if (!newUser) { isSelected = rs.getBoolean("TOOLS_HDCP"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tools - Post and View Handicap Information", "TOOLS_HDCP", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TOOLS_SEARCHTS");
          if (!newUser) { isSelected = rs.getBoolean("TOOLS_SEARCHTS"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tools - Search Member Tee Times", "TOOLS_SEARCHTS", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("TOOLS_EMAIL");
          if (!newUser) { isSelected = rs.getBoolean("TOOLS_EMAIL"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tools - Send Emails", "TOOLS_EMAIL", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("SYSCONFIG_MANAGECONTENT");
          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_MANAGECONTENT"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Tools - Manage Custom Email Content", "SYSCONFIG_MANAGECONTENT", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("WAITLIST_UPDATE");
          if (!newUser) { isSelected = rs.getBoolean("WAITLIST_UPDATE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Wait List - Add/Change Wait List Signups", "WAITLIST_UPDATE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("WAITLIST_MANAGE");
          if (!newUser) { isSelected = rs.getBoolean("WAITLIST_MANAGE"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Wait List - Manage Wait List Signups", "WAITLIST_MANAGE", isSelected, isProshop), "frm");
          form.addRow(cb);

          cb = new RowModel();
          cb.setId("WAITLIST_VIEW");
          if (!newUser) { isSelected = rs.getBoolean("WAITLIST_VIEW"); }
          cb.add(new Checkbox("ltd" + cb_num++, "Wait List - View Wait List Signups", "WAITLIST_VIEW", isSelected, isProshop), "frm");
          form.addRow(cb);

//          RowModel cb1 = new RowModel();
//          cb1.setId("DEMOCLUBS_MANAGE");
//          if (!newUser) { isSelected = rs.getBoolean("DEMOCLUBS_MANAGE"); }
//          Checkbox DEMOCLUBS_MANAGE = new Checkbox("ltd1", "Demo Clubs - Add/Remove Demo Clubs", "DEMOCLUBS_MANAGE", isSelected, isProshop);
//          cb1.add(DEMOCLUBS_MANAGE, "frm");
//          form.addRow(cb1);
//          
//          RowModel cb2 = new RowModel();
//          cb2.setId("DEMOCLUBS_CHECKIN");
//          if (!newUser) { isSelected = rs.getBoolean("DEMOCLUBS_CHECKIN"); }
//          Checkbox DEMOCLUBS_CHECKIN = new Checkbox("ltd2", "Demo Clubs - Check Demo Clubs In/Out", "DEMOCLUBS_CHECKIN", isSelected, isProshop);
//          cb2.add(DEMOCLUBS_CHECKIN, "frm");
//          form.addRow(cb2);
//
//          RowModel cb3 = new RowModel();
//          cb3.setId("DINING_CONFIG");
//          isSelected = false;       // Off by default
//          if (!newUser) { isSelected = rs.getBoolean("DINING_CONFIG"); }
//          Checkbox DINING_CONFIG = new Checkbox("ltd3", "Dining - Configuration", "DINING_CONFIG", isSelected, isProshop);
//          isSelected = true;
//          cb3.add(DINING_CONFIG, "frm");
//          form.addRow(cb3);
//
//          RowModel cb4 = new RowModel();
//          cb4.setId("DINING_REQUEST");
//          if (!newUser) { isSelected = rs.getBoolean("DINING_REQUEST"); }
//          Checkbox DINING_REQUEST = new Checkbox("ltd4", "Dining - Submit Dining Requests", "DINING_REQUEST", isSelected, isProshop);
//          cb4.add(DINING_REQUEST, "frm");
//          form.addRow(cb4);
//
//          RowModel cb5 = new RowModel();
//          cb5.setId("EVNTSUP_UPDATE");
//          if (!newUser) { isSelected = rs.getBoolean("EVNTSUP_UPDATE"); }
//          Checkbox EVNTSUP_UPDATE = new Checkbox("ltd5", "Event Setup - Add/Change Event Registrations", "EVNTSUP_UPDATE", isSelected, isProshop);
//          cb5.add(EVNTSUP_UPDATE, "frm");
//          form.addRow(cb5);
//
//          RowModel cb6 = new RowModel();
//          cb6.setId("EVNTSUP_MANAGE");
//          if (!newUser) { isSelected = rs.getBoolean("EVNTSUP_MANAGE"); }
//          Checkbox EVNTSUP_MANAGE = new Checkbox("ltd6", "Event Setup - Manage Event Registrations", "EVNTSUP_MANAGE", isSelected, isProshop);
//          cb6.add(EVNTSUP_MANAGE, "frm");
//          form.addRow(cb6);
//
//          RowModel cb7 = new RowModel();
//          cb7.setId("EVNTSUP_VIEW");
//          if (!newUser) { isSelected = rs.getBoolean("EVNTSUP_VIEW"); }
//          Checkbox EVNTSUP_VIEW = new Checkbox("ltd7", "Event Setup - View Event Registrations", "EVNTSUP_VIEW", isSelected, isProshop);
//          cb7.add(EVNTSUP_VIEW, "frm");
//          form.addRow(cb7);
//
//          RowModel cb8 = new RowModel();
//          cb8.setId("LESS_UPDATE");
//          if (!newUser) { isSelected = rs.getBoolean("LESS_UPDATE"); }
//          Checkbox LESS_UPDATE = new Checkbox("ltd8", "Lessons - Add/Change Member Lessons", "LESS_UPDATE", isSelected, isProshop);
//          cb8.add(LESS_UPDATE, "frm");
//          form.addRow(cb8);
//
//          RowModel cb9 = new RowModel();
//          cb9.setId("LESS_CONFIG");
//          if (!newUser) { isSelected = rs.getBoolean("LESS_CONFIG"); }
//          Checkbox LESS_CONFIG = new Checkbox("ltd9", "Lessons - Configuration of Lesson Books, etc.", "LESS_CONFIG", isSelected, isProshop);
//          cb9.add(LESS_CONFIG, "frm");
//          form.addRow(cb9);
//
//          RowModel cb10 = new RowModel();
//          cb10.setId("LESS_VIEW");
//          if (!newUser) { isSelected = rs.getBoolean("LESS_VIEW"); }
//          Checkbox LESS_VIEW = new Checkbox("ltd10", "Lessons - View Lesson Books", "LESS_VIEW", isSelected, isProshop);
//          cb10.add(LESS_VIEW, "frm");
//          form.addRow(cb10);
//
//          RowModel cb11 = new RowModel();
//          cb11.setId("LOTT_UPDATE");
//          if (!newUser) { isSelected = rs.getBoolean("LOTT_UPDATE"); }
//          Checkbox LOTT_UPDATE = new Checkbox("ltd11", "Lottery - Add/Change Lottery Requests", "LOTT_UPDATE", isSelected, isProshop);
//          cb11.add(LOTT_UPDATE, "frm");
//          form.addRow(cb11);
//
//          RowModel cb12 = new RowModel();
//          cb12.setId("LOTT_APPROVE");
//          if (!newUser) { isSelected = rs.getBoolean("LOTT_APPROVE"); }
//          Checkbox LOTT_APPROVE = new Checkbox("ltd12", "Lottery - Approve Lotteries", "LOTT_APPROVE", isSelected, isProshop);
//          cb12.add(LOTT_APPROVE, "frm");
//          form.addRow(cb12);
//
//          RowModel cb13 = new RowModel();
//          cb13.setId("REPORTS");
//          if (!newUser) { isSelected = rs.getBoolean("REPORTS"); }
//          Checkbox REPORTS = new Checkbox("ltd13", "Reports", "REPORTS", isSelected, isProshop);
//          cb13.add(REPORTS, "frm");
//          form.addRow(cb13);
//
//          RowModel cb14 = new RowModel();
//          cb14.setId("REST_OVERRIDE");
//          if (!newUser) { isSelected = rs.getBoolean("REST_OVERRIDE"); }
//          Checkbox REST_OVERRIDE = new Checkbox("ltd14", "Restrictions - Override", "REST_OVERRIDE", isSelected, isProshop);
//          cb14.add(REST_OVERRIDE, "frm");
//          form.addRow(cb14);
//
//          RowModel cb15 = new RowModel();
//          cb15.setId("SYSCONFIG_CLUBCONFIG");
//          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_CLUBCONFIG"); }
//          Checkbox SYSCONFIG_CLUBCONFIG = new Checkbox("ltd15", "System Configuration - Club Configuration", "SYSCONFIG_CLUBCONFIG", isSelected, isProshop);
//          cb15.add(SYSCONFIG_CLUBCONFIG, "frm");
//          form.addRow(cb15);
//
//          RowModel cb16 = new RowModel();
//          cb16.setId("SYSCONFIG_EVENT");
//          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_EVENT"); }
//          Checkbox SYSCONFIG_EVENT = new Checkbox("ltd16", "System Configuration - Event", "SYSCONFIG_EVENT", isSelected, isProshop);
//          cb16.add(SYSCONFIG_EVENT, "frm");
//          form.addRow(cb16);
//
//          RowModel cb17 = new RowModel();
//          cb17.setId("SYSCONFIG_LOTTERY");
//          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_LOTTERY"); }
//          Checkbox SYSCONFIG_LOTTERY = new Checkbox("ltd17", "System Configuration - Lottery", "SYSCONFIG_LOTTERY", isSelected, isProshop);
//          cb17.add(SYSCONFIG_LOTTERY, "frm");
//          form.addRow(cb17);
//
//          RowModel cb18 = new RowModel();
//          cb18.setId("SYSCONFIG_MEMBERNOTICES");
//          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_MEMBERNOTICES"); }
//          Checkbox SYSCONFIG_MEMBERNOTICES = new Checkbox("ltd18", "System Configuration - Member Notices", "SYSCONFIG_MEMBERNOTICES", isSelected, isProshop);
//          cb18.add(SYSCONFIG_MEMBERNOTICES, "frm");
//          form.addRow(cb18);
//
//          RowModel cb19 = new RowModel();
//          cb19.setId("SYSCONFIG_RESTRICTIONS");
//          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_RESTRICTIONS"); }
//          Checkbox SYSCONFIG_RESTRICTIONS = new Checkbox("ltd19", "System Configuration - Restrictions", "SYSCONFIG_RESTRICTIONS", isSelected, isProshop);
//          cb19.add(SYSCONFIG_RESTRICTIONS, "frm");
//          form.addRow(cb19);
//
//          RowModel cb20 = new RowModel();
//          cb20.setId("SYSCONFIG_TEESHEETS");
//          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_TEESHEETS"); }
//          Checkbox SYSCONFIG_TEESHEETS = new Checkbox("ltd20", "System Configuration - Tee Sheets", "SYSCONFIG_TEESHEETS", isSelected, isProshop);
//          cb20.add(SYSCONFIG_TEESHEETS, "frm");
//          form.addRow(cb20);
//              
//          RowModel cb21 = new RowModel();
//          cb21.setId("SYSCONFIG_WAITLIST");
//          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_WAITLIST"); }
//          Checkbox SYSCONFIG_WAITLIST = new Checkbox("ltd21", "System Configuration - Waitlist", "SYSCONFIG_WAITLIST", isSelected, isProshop);
//          cb21.add(SYSCONFIG_WAITLIST, "frm");
//          form.addRow(cb21);
//
//          RowModel cb22 = new RowModel();
//          cb22.setId("TS_CHECKIN");
//          if (!newUser) { isSelected = rs.getBoolean("TS_CHECKIN"); }
//          Checkbox TS_CHECKIN = new Checkbox("ltd22", "Tee Sheets - All - Check Players In/Out", "TS_CHECKIN", isSelected, isProshop);
//          cb22.add(TS_CHECKIN, "frm");
//          form.addRow(cb22);
//
//          RowModel cb23 = new RowModel();
//          cb23.setId("TS_PRINT");
//          if (!newUser) { isSelected = rs.getBoolean("TS_PRINT"); }
//          Checkbox TS_PRINT = new Checkbox("ltd23", "Tee Sheets - All - Print Tee Sheets", "TS_PRINT", isSelected, isProshop);
//          cb23.add(TS_PRINT, "frm");
//          form.addRow(cb23);
//
//          RowModel cb24 = new RowModel();
//          cb24.setId("TS_POS");
//          if (!newUser) { isSelected = rs.getBoolean("TS_POS"); }
//          Checkbox TS_POS = new Checkbox("ltd24", "Tee Sheets - All - Send POS Charges", "TS_POS", isSelected, isProshop);
//          cb24.add(TS_POS, "frm");
//          form.addRow(cb24);
//
//          RowModel cb25 = new RowModel();
//          cb25.setId("TS_UPDATE");
//          if (!newUser) { isSelected = rs.getBoolean("TS_UPDATE"); }
//          Checkbox TS_UPDATE = new Checkbox("ltd25", "Tee Sheets - Current - Add/Change Tee Times", "TS_UPDATE", isSelected, isProshop);
//          cb25.add(TS_UPDATE, "frm");
//          form.addRow(cb25);
//
//          RowModel cb26 = new RowModel();
//          cb26.setId("TS_VIEW");
//          if (!newUser) { isSelected = rs.getBoolean("TS_VIEW"); }
//          Checkbox TS_VIEW = new Checkbox("ltd26", "Tee Sheets - Current - View Tee Sheets", "TS_VIEW", isSelected, isProshop);
//          cb26.add(TS_VIEW, "frm");
//          form.addRow(cb26);
//
//          RowModel cb27 = new RowModel();
//          cb27.setId("TS_PAST_UPDATE");
//          if (!newUser) { isSelected = rs.getBoolean("TS_PAST_UPDATE"); }
//          Checkbox TS_PAST_UPDATE = new Checkbox("ltd27", "Tee Sheets - Past - Add/Change Tee Times", "TS_PAST_UPDATE", isSelected, isProshop);
//          cb27.add(TS_PAST_UPDATE, "frm");
//          form.addRow(cb27);
//
//          RowModel cb28 = new RowModel();
//          cb28.setId("TS_PAST_VIEW");
//          if (!newUser) { isSelected = rs.getBoolean("TS_PAST_VIEW"); }
//          Checkbox TS_PAST_VIEW = new Checkbox("ltd28", "Tee Sheets - Past - View Tee Sheets", "TS_PAST_VIEW", isSelected, isProshop);
//          cb28.add(TS_PAST_VIEW, "frm");
//          form.addRow(cb28);
//
//          RowModel cb29 = new RowModel();
//          cb29.setId("TS_CTRL_TSEDIT");
//          if (!newUser) { isSelected = rs.getBoolean("TS_CTRL_TSEDIT"); }
//          Checkbox TS_CTRL_TSEDIT = new Checkbox("ltd29", "Tee Sheets - Control Panel - Edit Tee Sheets", "TS_CTRL_TSEDIT", isSelected, isProshop);
//          cb29.add(TS_CTRL_TSEDIT, "frm");
//          form.addRow(cb29);
//
//          RowModel cb30 = new RowModel();
//          cb30.setId("TS_CTRL_FROST");
//          if (!newUser) { isSelected = rs.getBoolean("TS_CTRL_FROST"); }
//          Checkbox TS_CTRL_FROST = new Checkbox("ltd30", "Tee Sheets - Control Panel - Frost Delay", "TS_CTRL_FROST", isSelected, isProshop);
//          cb30.add(TS_CTRL_FROST, "frm");
//          form.addRow(cb30);
//
//          /* // removed for now
//          RowModel cb31 = new RowModel();
//          cb31.setId("TS_CTRL_EMAIL");
//          if (!newUser) { isSelected = rs.getBoolean("TS_CTRL_EMAIL"); }
//          Checkbox TS_CTRL_EMAIL = new Checkbox("ltd31", "Tee Sheets - Control Panel - Send Emails", "TS_CTRL_EMAIL", isSelected, isProshop);
//          cb31.add(TS_CTRL_EMAIL, "frm");
//          form.addRow(cb31);
//          */
//
//          RowModel cb31 = new RowModel();
//          cb31.setId("TS_PACE_UPDATE");
//          if (!newUser) { isSelected = rs.getBoolean("TS_PACE_UPDATE"); }
//          Checkbox TS_PACE_UPDATE = new Checkbox("ltd31", "Tee Sheets - Pace of Play - Update PoP Entries", "TS_PACE_UPDATE", isSelected, isProshop);
//          cb31.add(TS_PACE_UPDATE, "frm");
//          form.addRow(cb31);
//
//          RowModel cb32 = new RowModel();
//          cb32.setId("TS_PACE_VIEW");
//          if (!newUser) { isSelected = rs.getBoolean("TS_PACE_VIEW"); }
//          Checkbox TS_PACE_VIEW = new Checkbox("ltd32", "Tee Sheets - Pace of Play - View PoP Information", "TS_PACE_VIEW", isSelected, isProshop);
//          cb32.add(TS_PACE_VIEW, "frm");
//          form.addRow(cb32);
//
//          RowModel cb33 = new RowModel();
//          cb33.setId("TOOLS_ANNOUNCE");
//          if (!newUser) { isSelected = rs.getBoolean("TOOLS_ANNOUNCE"); }
//          Checkbox TOOLS_ANNOUNCE = new Checkbox("ltd33", "Tools - Announcement Page Changes", "TOOLS_ANNOUNCE", isSelected, isProshop);
//          cb33.add(TOOLS_ANNOUNCE, "frm");
//          form.addRow(cb33);
//
//          RowModel cb34 = new RowModel();
//          cb34.setId("TOOLS_HDCP");
//          if (!newUser) { isSelected = rs.getBoolean("TOOLS_HDCP"); }
//          Checkbox TOOLS_HDCP = new Checkbox("ltd34", "Tools - Post and View Handicap Information", "TOOLS_HDCP", isSelected, isProshop);
//          cb34.add(TOOLS_HDCP, "frm");
//          form.addRow(cb34);
//
//          RowModel cb35 = new RowModel();
//          cb35.setId("TOOLS_SEARCHTS");
//          if (!newUser) { isSelected = rs.getBoolean("TOOLS_SEARCHTS"); }
//          Checkbox TOOLS_SEARCHTS = new Checkbox("ltd35", "Tools - Search Member Tee Times", "TOOLS_SEARCHTS", isSelected, isProshop);
//          cb35.add(TOOLS_SEARCHTS, "frm");
//          form.addRow(cb35);
//
//          RowModel cb36 = new RowModel();
//          cb36.setId("TOOLS_EMAIL");
//          if (!newUser) { isSelected = rs.getBoolean("TOOLS_EMAIL"); }
//          Checkbox TOOLS_EMAIL = new Checkbox("ltd36", "Tools - Send Emails", "TOOLS_EMAIL", isSelected, isProshop);
//          cb36.add(TOOLS_EMAIL, "frm");
//          form.addRow(cb36);
//
//          RowModel cb37 = new RowModel();
//          cb37.setId("SYSCONFIG_MANAGECONTENT");
//          if (!newUser) { isSelected = rs.getBoolean("SYSCONFIG_MANAGECONTENT"); }
//          Checkbox SYSCONFIG_MANAGECONTENT = new Checkbox("ltd37", "Tools - Manage Custom Email Content", "SYSCONFIG_MANAGECONTENT", isSelected, isProshop);
//          cb37.add(SYSCONFIG_MANAGECONTENT, "frm");
//          form.addRow(cb37);
//
//          RowModel cb38 = new RowModel();
//          cb38.setId("WAITLIST_UPDATE");
//          if (!newUser) { isSelected = rs.getBoolean("WAITLIST_UPDATE"); }
//          Checkbox WAITLIST_UPDATE = new Checkbox("ltd38", "Wait List - Add/Change Wait List Signups", "WAITLIST_UPDATE", isSelected, isProshop);
//          cb38.add(WAITLIST_UPDATE, "frm");
//          form.addRow(cb38);
//
//          RowModel cb39 = new RowModel();
//          cb39.setId("WAITLIST_MANAGE");
//          if (!newUser) { isSelected = rs.getBoolean("WAITLIST_MANAGE"); }
//          Checkbox WAITLIST_MANAGE = new Checkbox("ltd39", "Wait List - Manage Wait List Signups", "WAITLIST_MANAGE", isSelected, isProshop);
//          cb39.add(WAITLIST_MANAGE, "frm");
//          form.addRow(cb39);
//
//          RowModel cb40 = new RowModel();
//          cb40.setId("WAITLIST_VIEW");
//          if (!newUser) { isSelected = rs.getBoolean("WAITLIST_VIEW"); }
//          Checkbox WAITLIST_VIEW = new Checkbox("ltd40", "Wait List - View Wait List Signups", "WAITLIST_VIEW", isSelected, isProshop);
//          cb40.add(WAITLIST_VIEW, "frm");
//          form.addRow(cb40);

          //add button to check/uncheck all checkboxes at once
          RowModel selectAllRow2 = new RowModel();
          selectAllRow2.setId("selectAllRow");
          String selectAllButton2 = "&nbsp<table class=\"pgMnu\"><tbody><tr><td><table class=\"btn\"><tbody><tr><td><a class=\"btnHref\" href=\"javascript:toggleFeatureAccess()\">Check/Uncheck All</a></td></tr></tbody></table></td></tr></tbody></table>";
          selectAllRow2.add(selectAllButton2, "frm");
          form.addRow(selectAllRow2);
          
      } catch (Exception exc) {
          
      }
  }
  
  
  /**
  ***************************************************************************************
  *
  * This method will add the tee sheet options to the form provided
  *
  * @param form the form to be updated
  *
  ***************************************************************************************
  **/

  public static void addTeeSheetOptionsToForm(FormModel form, ResultSet rs, PrintWriter out, boolean newUser) {
      
      boolean isSelected = false;
      boolean isProshop = true;
      
      try {
          //Add checkboxes for Tee Sheet options
          RowModel tsOptsHeaderRow = new RowModel();
          tsOptsHeaderRow.setId("ltdAccessHeaderRow");
          String tsOptsHeader = "<b>Tee Sheet Options</b> (will display after player names on tee sheets):";
          tsOptsHeaderRow.add(tsOptsHeader, "frm");
          form.addRow(tsOptsHeaderRow);

          RowModel tsOpts1 = new RowModel();
          tsOpts1.setId("TEESHEET_OPTIONS1");
          if (!newUser) { isSelected = rs.getBoolean("display_hdcp"); }
          Checkbox tsOptsCb1 = new Checkbox("tsOptsCb1", "Handicap", "display_hdcp", isSelected, isProshop);
          tsOpts1.add(tsOptsCb1, "frm");
          form.addRow(tsOpts1);

          RowModel tsOpts2 = new RowModel();
          tsOpts2.setId("TEESHEET_OPTIONS2");
          if (!newUser) { isSelected = rs.getBoolean("display_mnum"); }
          Checkbox tsOptsCb2 = new Checkbox("tsOptsCb2", "Member Number", "display_mnum", isSelected, isProshop);
          tsOpts2.add(tsOptsCb2, "frm");
          form.addRow(tsOpts2);

          RowModel tsOpts3 = new RowModel();
          tsOpts3.setId("TEESHEET_OPTIONS3");
          if (!newUser) { isSelected = rs.getBoolean("display_bag"); }
          Checkbox tsOptsCb3 = new Checkbox("tsOptsCb3", "Bag Number", "display_bag", isSelected, isProshop);
          tsOpts3.add(tsOptsCb3, "frm");
          form.addRow(tsOpts3);
          
      } catch (Exception exc) {
          
      }
  }

  /**
   * This method prints out a select box containing all activities currently in the system
   *
   * @param activity_id id # for the currently chosen activity, make sure it's selected in the select element
   * @param out output printer
   * @param con connection to club database
   * @param newUser true if creating a new user, false if editing an existing user
   */
  public static void addActivitySelectorToForm(FormModel form, String user, int activity_id, PrintWriter out, Connection con, boolean newUser, boolean newActivity) {

      PreparedStatement pstmt = null;
      ResultSet rs = null;

      int count = 0;

      boolean found = false;
      boolean foundGolf = false;
      boolean defaultEntry = false;
      boolean isSelected = false;
      boolean isProshop = true;

      ArrayList<Integer> activity_ids = new ArrayList<Integer>();

      String activitySelect = "<b>Activity</b> - Select " + ((newUser || newActivity) ? (newActivity ? "a new " : "an ") + " activity for this user: " : "the activity you'd like to edit options for: ") + "" +
              ((!newUser && !newActivity) ? "<br>(WARNING: changing this will reload the page, unsaved changes will be lost!)" : "");

      String onChangeAction = "onChange=\"javascript:viewProshopUser('Admin_editproshopuser', '" + user + "', this.options[this.selectedIndex].value)\"";
      // print select box for activity options (add onChange action if editing a user)
      activitySelect += "<br><select name=\"activity_id\" " + ((!newUser && !newActivity) ? onChangeAction : "") + ">";

      // Retrieve all the activity ids/names from the database
      try {

          // For new users, add all activities not set up for this user
          pstmt = con.prepareStatement("SELECT activity_id, default_entry FROM login2 WHERE username = ? ORDER BY activity_id");
          pstmt.clearParameters();
          pstmt.setString(1, user);
          rs = pstmt.executeQuery();

          foundGolf = false;

          while (rs.next()) {
              activity_ids.add(rs.getInt("activity_id"));

              // See if this activity is set as the default entry
              if (activity_id == rs.getInt("activity_id") && rs.getInt("default_entry") == 1) {
                  defaultEntry = true;
              }

              // See if Golf is found in this user's activities
              if (rs.getInt("activity_id") == 0) {
                  foundGolf = true;
              }


          }
          
          if ((newActivity && !foundGolf) || (!newActivity && foundGolf) || newUser) {
              activitySelect += "\n<option " + (newUser ? "selected " : "") + "value=\"0\">Golf</option>";
          }


          pstmt.close();

          pstmt = con.prepareStatement("SELECT activity_id, activity_name FROM activities WHERE parent_id = '0' ORDER BY activity_id");
          rs = pstmt.executeQuery();

          // Loop through results and add option elements to select object
          while (rs.next()) {

              if (newUser) {

                  // For brand new users, add all activities
                  activitySelect += "\n<option " + (activity_id == rs.getInt("activity_id") ? "selected " : "") + "value=\"" + rs.getInt("activity_id") + "\">" + rs.getString("activity_name") + "</option>";

              } else if (newActivity) {

                  // For new activities for existing users, add all activities not set up for this user
                  found = false;

                  for (int i=0; i<activity_ids.size(); i++) {
                      if (activity_ids.get(i) == rs.getInt("activity_id")) found = true;
                  }

                  if (!found) {
                      activitySelect += "\n<option " + (activity_id == rs.getInt("activity_id") ? "selected " : "") + "value=\"" + rs.getInt("activity_id") + "\">" + rs.getString("activity_name") + "</option>";
                  }

              } else {

                  // For existing users, add all activities currently set up for this user
                  for (int i=0; i<activity_ids.size(); i++) {

                      if (activity_ids.get(i) == rs.getInt("activity_id")) {
                          activitySelect += "\n<option " + (activity_id == rs.getInt("activity_id") ? "selected " : "") + "value=\"" + rs.getInt("activity_id") + "\">" + rs.getString("activity_name") + "</option>";
                          break;
                      }
                  }
              }
          }

          pstmt.close();


          activitySelect += "\n</select><br><br>";

          RowModel activitySelectRow = new RowModel();
          activitySelectRow.setId("activitySelectRow");
          activitySelectRow.add(activitySelect, "frm", 2);
          form.addRow(activitySelectRow);

          // Add a checkbox for default login activity
          RowModel defaultEntryRow = new RowModel();
          defaultEntryRow.setId("default_entry");
          if (newUser) {
              isSelected = true;
          } else {
              isSelected = defaultEntry;
          }
          Checkbox defaultEntryCb = new Checkbox("defaultEntryCb", "Default activity (This activity will be loaded upon login)", "default_entry", isSelected, isProshop);
          defaultEntryRow.add(defaultEntryCb, "frm");
          form.addRow(defaultEntryRow);

          
      } catch (Exception exc) {

      }


  }
  
  /**
  ***************************************************************************************
  *
  * This method will retrieve members from the member database based on the letter provided.
  * if the letter passed in is null or empty, all members will be returned.
  *
  * @param con the database connection
  * @param theLetter the letter to use for the query
  * @param out the PrintWriter object reference
  *
  ***************************************************************************************
  **/

  public static SelectionList queryMembersWithEmailAddresses(HttpServletRequest req, Connection con, String club, String theLetter, PrintWriter out, boolean isPro) throws SQLException
  {

    boolean pushNotification = reqUtil.getParameterString(req, "push", "0").equals("1") && isPro;
    //out.println("<!-- queryMembersWithEmailAddresses: pushNotification=" + pushNotification + " -->");
    
    String letter = theLetter;

    if (letter.equalsIgnoreCase( ActionHelper.VIEW_ALL )) {

      letter = "%";        // all names
      
    } else {

       letter = letter + "%";
    }

    PreparedStatement stmt = null;
    SelectionList names = new SelectionList("listOfNames", "Name List", false);

    //
    //  Dtermine the query string based on caller
    //
    String sql = "";
    if (!pushNotification) {
        sql = "SELECT * FROM member2b WHERE name_last LIKE ? AND inact = 0 AND billable = 1 AND ";

        if (isPro == true) {

          sql += "((email_bounced = 0 AND email <> '' && clubEmailOpt1 > 0) || (email2_bounced = 0 AND email2 <> '' && clubEmailOpt2 > 0)) "
               + "ORDER BY name_last, name_first, name_mi";

        } else {

          sql += "((email_bounced = 0 AND email <> '' && memEmailOpt1 > 0) || (email2_bounced = 0 AND email2 <> '' && memEmailOpt2 > 0)) "
               + "ORDER BY name_last, name_first, name_mi";
        }
    } else {
        
        int club_id = Utilities.getClubId(club, con);
        sql = "SELECT m.* FROM member2b m "
            + "LEFT OUTER JOIN v5.mobile_auth t2 ON m.id = t2.member_id "
            + "WHERE t2.club_id = " + club_id + " AND m.name_last LIKE ? AND m.inact = 0 AND m.billable = 1 "
            + "GROUP BY m.username "
            + "ORDER BY m.name_last, m.name_first, m.name_mi";
    }

    //if(pushNotification) { out.println("<!-- sql=" + sql + " -->"); }
        
    try {                            // Get all columns from member table for names requested

      stmt = con.prepareStatement (sql);

      stmt.clearParameters();               
      stmt.setString(1, letter);            
      ResultSet rs = stmt.executeQuery();            


      while(rs.next()) {

        String lastname = rs.getString("name_last");
        String firstname = rs.getString("name_first");
        String middleinitial = rs.getString("name_mi");

        //escape special characters in the username
        String username = rs.getString("username");
        String escName = ScriptHelper.escapeSpecialCharacters(username);

        String displayName = lastname + ", " + firstname + " " + middleinitial;

        if (isPro || !club.equals("treesdalegolf") || !username.equals("R0084")) { // Don't show this user's email in the list
        
            names.addOption(displayName, username, false);
        }
      }
    }
    catch (SQLException exc) {

        throw exc;
    }
    finally{
       stmt.close();
    }

    return names;

  }

  /**
  ***************************************************************************************
  *
  * This method will return the display name for the specified user.
  *
  * @param con the database connection
  * @param theUserName the username for the query
  * @param out the PrintWriter object reference
  *
  ***************************************************************************************
  **/

  public static String getMemberDisplayName(Connection con, String theUserName, PrintWriter out) throws SQLException
  {

    PreparedStatement stmt = null;
    String displayName = "";

    try {                            // Get all columns from member table for names requested

      stmt = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, theUserName);
      ResultSet rs = stmt.executeQuery();            // execute the prepared stmt

      while(rs.next()) {


        String lastname = rs.getString("name_last");
        String firstname = rs.getString("name_first");
        String middleinitial = rs.getString("name_mi");

        displayName = lastname + ", " + firstname + " " + middleinitial;

      }
    }
    catch (SQLException exc) {

        throw exc;
    }
    finally{
       stmt.close();
    }

    return displayName;

  }

  /**
  ***************************************************************************************
  *
  * This method will return true if the specified user has an email address.
  *
  * @param username the username for the query
  * @param con the database connection reference
  * @param out the PrintWriter object reference
  *
  ***************************************************************************************
  **/

  public static boolean hasEmailAddress(String username, Connection con, PrintWriter out)
  {
      boolean hasEmailAddress = false;
      try
      {
            //"SELECT email, email2 FROM member2b WHERE username = ?"
        PreparedStatement stmt = con.prepareStatement (
            "SELECT (" +
                "SELECT email FROM member2b WHERE username = ? AND email_bounced = 0 AND inact = 0 AND billable = 1" +
            ") AS email1, (" +
                "SELECT email2 FROM member2b WHERE username = ? AND email2_bounced = 0 AND inact = 0 AND billable = 1" +
            ") AS email2");

        stmt.clearParameters();
        stmt.setString(1, username);
        stmt.setString(2, username);
        ResultSet mrs = stmt.executeQuery();

        if (mrs.next())
        {

          String email1 = mrs.getString("email1");
          String email2 = mrs.getString("email2");

          if ( (email1 != null && !(email1.equals(""))) || (email2 != null && !(email2.equals(""))) ) {

             hasEmailAddress = true;
          }
        }

        stmt.close();
      }
      catch (Exception exc)
      {
      }

      return hasEmailAddress;
  }

  /**
  ***************************************************************************************
  *
  * This method will return one email address for the given user.  IF the email one has a value
  * it will return that one.  If it doesn't have a value but the email2 does, it will return that value
  *
  * @param con the database connection
  * @param theUserName the username for the query
  * @param type the type of call - get the caller's address, pro sending email, member sending email
  *
  ***************************************************************************************
  **/

  public static String getEmailAddress(String username, Connection con, PrintWriter out, String type)
  {
    ArrayList <String> emailAddresses = getEmailAddresses (username, con, out, type);
    String email = "";
    int count = 0;
    while (count < emailAddresses.size() && email.equals(""))
    {
      email = (String)emailAddresses.get(count);
      count++;
    }

    return email;
  }

  /**
  ***************************************************************************************
  *
  * This method will return the email addresses for the given user.
  *
  * @param con the database connection
  * @param theUserName the username for the query
  * @param type the type of call - get the caller's address, pro sending email, member sending email
  *
  ***************************************************************************************
  **/

  public static ArrayList<String> getEmailAddresses(String username, Connection con, PrintWriter out, String type)
  {
    PreparedStatement stmt = null;
    ResultSet mrs = null;
    ArrayList <String> email = new ArrayList<String>();

    Member member = new Member();

    String emailAddress = "";
    String emailOpt1 = "";
    String emailOpt2 = "";


    try
    {
      if (username.startsWith( "proshop" )) {            // if proshop user

         stmt = con.prepareStatement (
              "SELECT email FROM club5 WHERE clubName != ''");

         stmt.clearParameters();                   // clear the parms
         mrs = stmt.executeQuery();      // execute the prepared stmt

         if (mrs.next())
         {
           email.add(mrs.getString("email"));
         }

         stmt.close();

      } else {
         
         //  Determine if we need to check the member's email subscription settings
         
         if (type.equals(Member.PRO_SEND_EMAIL)) {         // Pro sending an email to members?
            
            emailOpt1 = " AND clubEmailOpt1 > 0";
            emailOpt2 = " AND clubEmailOpt2 > 0";
            
         } else if (type.equals(Member.MEM_SEND_EMAIL)) {  // Member sending an email to members?
            
            emailOpt1 = " AND memEmailOpt1 > 0";
            emailOpt2 = " AND memEmailOpt2 > 0";
         }
         

              //"SELECT email, email2 FROM member2b WHERE username = ?"
         stmt = con.prepareStatement (
                 "SELECT (" +
                    "SELECT email FROM member2b WHERE username = ? AND email_bounced = 0 AND inact = 0 AND billable = 1" + emailOpt1 + 
                 ") AS email1, (" +
                    "SELECT email2 FROM member2b WHERE username = ? AND email2_bounced = 0 AND inact = 0 AND billable = 1" +  emailOpt2 +
                 ") AS email2");

         stmt.clearParameters();                   // clear the parms
         stmt.setString(1, username);             // put the parm in stmt
         stmt.setString(2, username);             // put the parm in stmt
         mrs = stmt.executeQuery();      // execute the prepared stmt

         if (mrs.next())
         {
           
           emailAddress = mrs.getString("email1");     // get email address

           if (!emailAddress.equals( "" ) && emailAddress != null) {     // if present
             
              FeedBack feedback = (member.isEmailValid(emailAddress));   // verify the address

              if (feedback.isPositive()) {          // if valid

                 email.add(emailAddress);           // add it
              }
           }

           emailAddress = mrs.getString("email2");     // get email address #2

           if (!emailAddress.equals( "" ) && emailAddress != null) {     // if present

              FeedBack feedback = (member.isEmailValid(emailAddress));   // verify the address

              if (feedback.isPositive()) {          // if valid

                 email.add(emailAddress);           // add it
              }
           }

         } // end if rs

         stmt.close();
      }
    }
    catch (Exception exc)
    {
    }

    return email;
  }


}
