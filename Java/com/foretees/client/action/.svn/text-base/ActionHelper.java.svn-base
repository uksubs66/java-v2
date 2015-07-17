/***************************************************************************************
 *   ActionHelper:  This utility class will draw action models
 *
 *
 *   created: 10/03/2003   jag
 *
 *
 *   last updated:
 *
 *    4/14/10  Updated an ArrayList decleration so that it's not using unchecked raw type
 *    7/14/08  Added variables for use with limited access proshop user feature
 *    1/08/05  JAG Add constant for sending an email to the tee sheet members
 *
 *
 ***************************************************************************************
 */

package com.foretees.client.action;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.foretees.common.ProcessConstants;
import com.foretees.client.HTMLConstants;
//import com.foretees.client.action.*;

/**
 ***************************************************************************************
 *
 *  This helper class contains static action definitions for commonly used Actions/
 *  ActionModels and methods to draw these commonly used ActionModels.
 *
 ***************************************************************************************
 **/

public class ActionHelper {

  private static HTMLConstants hc = new HTMLConstants();
  private static String versionId = ProcessConstants.CODEBASE;

  /***************************************************************************************
  * Identifier for the add action.
  ***************************************************************************************/
  public static String ADD = "add";
  /***************************************************************************************
  * Identifier for the edit action.
  ***************************************************************************************/
  public static String EDIT = "edit";
  /***************************************************************************************
  * Identifier for the Club Setup action.
  ***************************************************************************************/
  public static String CLUB_SETUP = "clbSetup";
  /***************************************************************************************
  * Identifier for the Course Parameters action.
  ***************************************************************************************/
  public static String COURSE_PARAMS = "crsParams";
  /***************************************************************************************
  * Identifier for the Double Tees action.
  ***************************************************************************************/
  public static String DBL_TEES = "dbleTees";
  /***************************************************************************************
  * Identifier for the Event Setup action.
  ***************************************************************************************/
  public static String EVT_SETUP = "envSetup";
  /***************************************************************************************
  * Identifier for the Lottery Setup action.
  ***************************************************************************************/
  public static String LOTTERY_SETUP = "lttrySetup";
  /***************************************************************************************
  * Identifier for the Member Type Restrictions action.
  ***************************************************************************************/
  public static String MEM_TYPE_REST = "mbrTypeRest";
  /***************************************************************************************
  * Identifier for the Member Number Restrictions action.
  ***************************************************************************************/
  public static String MEM_NUM_REST = "mbrNumRest";
  /***************************************************************************************
  * Identifier for the Five Some Restrictions action.
  ***************************************************************************************/
  public static String FIVE_SOME_REST = "fvSmeRest";
  /***************************************************************************************
  * Identifier for the Block Tee Times action.
  ***************************************************************************************/
  public static String BLOCK_TEE_TIMES = "blkTeeTmes";
  /***************************************************************************************
  * Identifier for the Block Tee Sheets action.
  ***************************************************************************************/
  public static String BLOCK_TEE_SHEETS = "bldTeeShts";
  /***************************************************************************************
  * Identifier for the Announcements upload action.
  ***************************************************************************************/
  public static String ANNOUNCEMENTS_UPLOAD = "ancMntsUpload";
  /***************************************************************************************
  * Identifier for the Guest Restrictions  action.
  ***************************************************************************************/
  public static String GUEST_REST = "gstRest";



  //Action model and actions for the main navigation
  private static ActionModel mainNavBarActionModelForProshop = new ActionModel();
  /***************************************************************************************
  * Identifier for the Home action.
  ***************************************************************************************/
  public static String HOME = "home";
  /***************************************************************************************
  * Identifier for the Tee Sheets action.
  ***************************************************************************************/
  public static String TEE_SHEETS = "teeShts";
  /***************************************************************************************
  * Identifier for the Event Signup action.
  ***************************************************************************************/
  public static String EVENT_SIGNUP = "evntSignUp";
  /***************************************************************************************
  * Identifier for the Lottery action.
  ***************************************************************************************/
  public static String LOTTERY = "lottery";
  /***************************************************************************************
  * Identifier for the System Configuration action.
  ***************************************************************************************/
  public static String SYS_CONFIG = "sysConfig";
  /***************************************************************************************
  * Identifier for the Search action.
  ***************************************************************************************/
  public static String SEARCH = "search";
  /***************************************************************************************
  * Identifier for the Support action.
  ***************************************************************************************/
  public static String SUPPORT = "support";
  /***************************************************************************************
  * Identifier for the Settings action.
  ***************************************************************************************/
  public static String SETTINGS = "settings";
  /***************************************************************************************
  * Identifier for the Reports action.
  ***************************************************************************************/
  public static String REPORTS = "reports";
  /***************************************************************************************
  * Identifier for the Logout action.
  ***************************************************************************************/
  public static String LOGOUT = "logout";
  /***************************************************************************************
  * Identifier for the global Help action.
  ***************************************************************************************/
  public static String GBL_HELP = "globalHelp";
  /***************************************************************************************
  * Identifier for the Announcements action.
  ***************************************************************************************/
  public static String ANNOUNCEMENTS = "announcements";

 /***************************************************************************************
  * Identifier for the Create Event action.
  ***************************************************************************************/
  public static String CREATE_EVENT = "createEvent";
  /***************************************************************************************
  * Identifier for the Create Double Tee action.
  ***************************************************************************************/
  public static String CREATE_DBL_TEE = "createDblTee";
  /***************************************************************************************
  * Identifier for the page Help action.
  ***************************************************************************************/
  public static String HELP = "help";
  /***************************************************************************************
  * Identifier for the Add Members action.
  ***************************************************************************************/
  public static String ADD_MEMBERS = "addMembers";
  /***************************************************************************************
  * Identifier for the Edit Members action.
  ***************************************************************************************/
  public static String EDIT_MEMBERS = "editMembers";
  /***************************************************************************************
  * Identifier for the Member List action.
  ***************************************************************************************/
  public static String MEMBER_LIST = "memberList";
  /***************************************************************************************
  * Identifier for the Add Member action.
  ***************************************************************************************/
  public static String ADD_MEMBER = "addMember";
  /***************************************************************************************
  * Identifier for the Edit All action.
  ***************************************************************************************/
  public static String EDIT_ALL = "editAll";
  public static String EDIT_ALL2 = "editAll2";
  /***************************************************************************************
  * Identifier for the Update and Close action.
  ***************************************************************************************/
  public static String UPDATE_AND_CLOSE = "updateAndClose";
  /***************************************************************************************
  * Identifier for the Update and Return action.
  ***************************************************************************************/
  public static String UPDATE_AND_RETURN = "updateAndReturn";
  /***************************************************************************************
  * Identifier for the Cancel action.
  ***************************************************************************************/
  public static String CANCEL = "cancel";
  /***************************************************************************************
  * Identifier for the Cleanup action.
  ***************************************************************************************/
  public static String CLEANUP = "cleanup";
  /***************************************************************************************
  * Identifier for the Delete action.
  ***************************************************************************************/
  public static String DELETE = "delete";
  /***************************************************************************************
  * Identifier for the Remove action.
  ***************************************************************************************/
  public static String REMOVE = "remove";
  /***************************************************************************************
  * Identifier for the Hotel User List action.
  ***************************************************************************************/
  public static String HOTEL_USER_LIST = "hotelUserList";
  /***************************************************************************************
  * Identifier for the Proshop User List action.
  ***************************************************************************************/
  public static String PROSHOP_USER_LIST = "proshopUserList";
  /***************************************************************************************
  * Identifier for the view Distribution List action.
  ***************************************************************************************/
  public static String VIEW_DIST_LIST = "viewDistList";
  /***************************************************************************************
  * Identifier for the Construct email action.
  ***************************************************************************************/
  public static String SEND_EMAIL_TO_DIST_LIST = "constructEmail";
  /***************************************************************************************
  * Identifier for the Add to List action.
  ***************************************************************************************/
  public static final String ADD_TO_LIST = "addToList";
  /***************************************************************************************
  * Identifier for the Remove from List action.
  ***************************************************************************************/
  public static final String REMOVE_FROM_LIST = "removeFromList";
  /***************************************************************************************
  * Identifier for the Send action.
  ***************************************************************************************/
  public static final String SEND = "sendEmail";
  /***************************************************************************************
  * Identifier for the Search lists action.
  ***************************************************************************************/
  public static final String SEARCH_LISTS = "searchLists";
  /***************************************************************************************
  * Identifier for the Search members action.
  ***************************************************************************************/
  public static final String SEARCH_MEMBERS = "searchMembers";
  /***************************************************************************************
  * Identifier for the Search events action.
  ***************************************************************************************/
  public static final String SEARCH_EVENTS = "searchEvents";
  /***************************************************************************************
  * Identifier for the Search type action.
  ***************************************************************************************/
  public static final String SEARCH_TYPE = "searchType";
  /***************************************************************************************
  *Identifier for the Search by member type action.
  ***************************************************************************************/
  public static final String SEARCH_BY_MEMBER_TYPE = "srchByMemberType";
  /***************************************************************************************
  * Identifier for the Search by member ship type action
  ***************************************************************************************/
  public static final String SEARCH_BY_MEMBERSHIP_TYPE = "srchByMemberShipType";
  /***************************************************************************************
  * Identifier for the Search by member ship type action
  ***************************************************************************************/
  public static final String SEARCH_BY_MEMTYPE_MEMSHIP_TYPE = "srchByMemTypeMemShipType";
  /***************************************************************************************
  * Identifier for the hidden input field to post selected items.
  ***************************************************************************************/
  public static final String SELECTED_ITEMS_STRING = "selItemsStr";
  /***************************************************************************************
  * Identifier for the save list action
  ***************************************************************************************/
  public static final String SAVE_LIST = "saveList";
  /***************************************************************************************
  * Identifier for the add partners action
  ***************************************************************************************/
  public static final String ADD_PARTNERS = "addPartners";
  /***************************************************************************************
  * Identifier for sending members of a particular tee sheet action
  ***************************************************************************************/
  public static final String SEARCH_TEESHEET = "emailTeeSheet";
  /***************************************************************************************
  * Identifier for the Search membership type action.
  ***************************************************************************************/
  public static final String SEARCH_MEMSHIP_TYPE = "searchMembershipTypes";
  /***************************************************************************************
  * Identifier for the Search member type action.
  ***************************************************************************************/
  public static final String SEARCH_MEM_TYPE = "searchMemberTypes";
  /***************************************************************************************
  * Identifier for the next page action.
  ***************************************************************************************/
  public static final String SAVE_AND_NEXT = "saveAndNext";
  /***************************************************************************************
  * Identifier for the previous page action.
  ***************************************************************************************/
  public static final String SAVE_AND_PREV = "saveAndPrev";
  /***************************************************************************************
  * Identifier for sending members of a particular time sheet action (For Activities)
  ***************************************************************************************/
  public static final String SEARCH_TIMESHEET = "emailTimeSheet";


  /***************************************************************************************
  * Request attribute for identifying the next action to execute
  ***************************************************************************************/
  public static String NEXT_ACTION = "nextAction";
  public static String ACTION = "currentAction";
  public static final String VIEW_ALL = "view all";



  /***************************************************************************************
  * Label for the Actions Column on tables
  ***************************************************************************************/

  public static String ACTIONS_LABEL = "Actions";


 static {



      mainNavBarActionModelForProshop.setLabel("Foretees Proshop Pages");

      mainNavBarActionModelForProshop.add(new Action(HOME, "Home", "Return to this page.", versionId + "servlet/Proshop_announce"));
      mainNavBarActionModelForProshop.add(new Action(TEE_SHEETS, "Tee Sheets", "View tee sheets and manage tee times. ", versionId + "servlet/Proshop_select"));
      mainNavBarActionModelForProshop.add(new Action(EVENT_SIGNUP, "Event Sign Up", "View and manage event registration sheets.", versionId + "servlet/Proshop_events1"));
      mainNavBarActionModelForProshop.add(new Action(LOTTERY, "Lottery", "View and manage active lottery registrations (not yet processed).", versionId + "servlet/Proshop_mlottery"));
      mainNavBarActionModelForProshop.add(new Action(SYS_CONFIG, "System Config", "Configure System Parameters, Restrictions, Events and Upload Announcements.", versionId + "servlet/Proshop_announce"));
      mainNavBarActionModelForProshop.add(new Action(SEARCH, "Search", "Search current tee sheets for a member's tee times.", versionId + "proshop_searchmain.htm"));
      mainNavBarActionModelForProshop.add(new Action(REPORTS, "Reports", "Generate handicap and tee time reports.", versionId + "proshop_reports.htm"));
      mainNavBarActionModelForProshop.add(new Action(SETTINGS, "Settings", "Change your password.", versionId + "proshop_services.htm"));
      mainNavBarActionModelForProshop.add(new Action(SUPPORT, "Support", "Report problems, submit comments or contact ForeTees.", versionId + "proshop_probs.htm"));
      mainNavBarActionModelForProshop.add(new Action(LOGOUT, "Logout", "Exit the system.", versionId + "servlet/Logout"));
      mainNavBarActionModelForProshop.add(new Action(GBL_HELP, "Help", "Get assistance.", versionId + "proshop_help.htm"));
      mainNavBarActionModelForProshop.add(new Action(ANNOUNCEMENTS, "Announcements", "View the club announcements page displayed when you logged in.", versionId + "servlet/Proshop_announce"));

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the System Config navigation bar as a set of links aligned
  * vertically.
  *
  * @param selectedAction the name of the action to display as disabled. An empty string
  *                       or null will result in all the actions displaying as enabled.
  * @param out the output stream in which to write the html.
  *
  ***************************************************************************************
  **/

  public static void drawSystemConfigNavBar(String selectedAction, PrintWriter out)
  {

    ActionModel sysConfigNavBarActionModel = buildSysConfigNavBar();
    NavBarRenderer.render(sysConfigNavBarActionModel, selectedAction, out);

  }

  private static ActionModel buildSysConfigNavBar()
  {
    ActionModel sysConfigNavBarActionModel = new ActionModel();

    sysConfigNavBarActionModel.setLabel("Proshop System Configuration");

    sysConfigNavBarActionModel.add(new Action(CLUB_SETUP, "Club Setup", "Identify the Club and Course(s)", versionId + "servlet/Proshop_club"));
    sysConfigNavBarActionModel.add(new Action(COURSE_PARAMS, "Course Parameters", "Maintain the specific Golf Course Parameters", versionId + "servlet/Proshop_parms"));
    sysConfigNavBarActionModel.add(new Action(DBL_TEES, "Double Tees", "Maintain the 'Double Tees' and 'Cross-Over' settings", versionId + "servlet/Proshop_dbltee"));
    sysConfigNavBarActionModel.add(new Action(EVT_SETUP, "Event Setup", "Setup and maintain events or tournaments", versionId + "servlet/Proshop_events"));
    sysConfigNavBarActionModel.add(new Action(LOTTERY_SETUP, "Lottery Setup", "Setup and maintain Lottery times", versionId + "servlet/Proshop_lottery"));
    sysConfigNavBarActionModel.add(new Action(MEM_TYPE_REST, "Member Type Restrictions", "Maintain member restrictions by Member Type or Membership Type", versionId + "servlet/Proshop_mrest"));
    sysConfigNavBarActionModel.add(new Action(MEM_NUM_REST, "Member Number Restrictions", "Maintain member restrictions by Member Number", versionId + "servlet/Proshop_mNumrest"));
    sysConfigNavBarActionModel.add(new Action(GUEST_REST, "Guest Restrictions", "Maintain guest restrictions", versionId + "servlet/Proshop_grest"));
    sysConfigNavBarActionModel.add(new Action(FIVE_SOME_REST, "5-Some Restrictions", "Maintain 5-some restrictions", versionId + "servlet/Proshop_fives"));
    sysConfigNavBarActionModel.add(new Action(BLOCK_TEE_TIMES, "Block Tee Times", "Block, or Hide, Tee Times from all users", versionId + "servlet/Proshop_block"));
    sysConfigNavBarActionModel.add(new Action(BLOCK_TEE_SHEETS, "Build Tee Sheets", "Configuration Complete - Build Tee Sheets (first time only)", versionId + "servlet/Proshop_buildTees"));
    sysConfigNavBarActionModel.add(new Action(ANNOUNCEMENTS_UPLOAD, "Announcements", "Upload an Announcements file", versionId + "proshop_upload.htm"));

    return sysConfigNavBarActionModel;

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the Admin Members navigation bar as a set of links aligned
  * vertically.
  *
  * @param selectedAction the name of the action to display as disabled. An empty string
  *                       or null will result in all the actions displaying as enabled.
  * @param out the output stream in which to write the html.
  *
  ***************************************************************************************
  **/

  public static void drawAdminMemberNavBar(String selectedAction, PrintWriter out)
  {

    //ActionModel memberNavBarActionModel = buildMemberNavBar();
    //NavBarRenderer.render(memberNavBarActionModel, selectedAction, out);

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the Admin Hotel Users navigation bar as a set of links aligned
  * vertically.
  *
  * @param selectedAction the name of the action to display as disabled. An empty string
  *                       or null will result in all the actions displaying as enabled.
  * @param out the output stream in which to write the html.
  *
  ***************************************************************************************
  **/
  public static void drawAdminHotelUserNavBar(String selectedAction, PrintWriter out)
  {
    //ActionModel memberNavBarActionModel = buildHotelUsersNavBar();
    //NavBarRenderer.render(memberNavBarActionModel, selectedAction, out);
  }

  private static ActionModel buildMemberNavBar()
  {
    ActionModel MemberNavBarActionModel = new ActionModel();

    MemberNavBarActionModel.setLabel("Administer Members");

    MemberNavBarActionModel.add(new Action(MEMBER_LIST, "View Members", "View the members' records in the database.", versionId + "servlet/Admin_members"));
    //MemberNavBarActionModel.add(new Action(ADD_MEMBERS, "Add Members", "Add members to the database.", versionId + "servlet/Admin_addmem"));
    //MemberNavBarActionModel.add(new Action(EDIT_MEMBERS, "Edit Members", "Change a member's database record.", versionId + "servlet/Admin_editmain"));

    return MemberNavBarActionModel;

  }

  public static ActionModel buildHotelUsersNavBar()
  {
    ActionModel HotelUserNavBarActionModel = new ActionModel();

    HotelUserNavBarActionModel.setLabel("Administer Hotel Users");

    HotelUserNavBarActionModel.add(new Action(HOTEL_USER_LIST, "View Hotel Users", "View the hotel user records in the database.", versionId + "servlet/Admin_hotelusers"));

    return HotelUserNavBarActionModel;
  }

  public static void drawTabs(String userType, String selectedTab, PrintWriter out)
  {

    out.println("<table class=\"mnTabs\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"ccccaa\">");
    out.print("<tr><td align=\"center\" valign=\"bottom\">");

    if (userType.equals(ProcessConstants.ADMIN))
    {
      drawAdminTabs(out);
    }
    //out.print("<br><hr class=\"pgHdrSep\">");
    out.print("</td></tr>");
    //out.print("<tr valign=\"top\"><td><hr class=\"pgHdrSep\"></td></tr>");
    out.print("</table>");

  }

   private static void drawAdminTabs(PrintWriter out)
  {
      out.print("<a href=\"" + versionId + "admin_mainleft.htm\" onMouseOver=\"document.images['AdminHome'].src = '" +versionId+ "images/AdminHome-over.png'\" onMouseOut=\"document.images['AdminHome'].src = '" +versionId+ "images/AdminHome.png'\"><img name=\"AdminHome\" src=\"" + versionId + "images/AdminHome.png\" hspace=\"0\" border=\"0\" alt=\"Return to Main Menu\"></a>" + hc.NON_BREAK_SPACE);
      out.print("<a href=\"" + versionId + "admin_members.htm\" onMouseOver=\"document.images['AdminMembers'].src = '" +versionId+ "images/AdminMembers-over.png'\" onMouseOut=\"document.images['AdminMembers'].src = '" +versionId+ "images/AdminMembers.png'\"><img name=\"AdminMembers\" src=\"" + versionId + "images/AdminMembers.png\" hspace=\"0\" border=\"0\" alt=\"Maintain Member Database\"></a>" + hc.NON_BREAK_SPACE);
      out.print("<a href=\"" + versionId + "servlet/Admin_usereport\" onMouseOver=\"document.images['AdminReports'].src = '" +versionId+ "images/AdminReports-over.png'\" onMouseOut=\"document.images['AdminReports'].src = '" +versionId+ "images/AdminReports.png'\"><img name=\"AdminReports\" src=\"" + versionId + "images/AdminReports.png\" hspace=\"0\" border=\"0\" alt=\"Generate Reports\"></a>" + hc.NON_BREAK_SPACE);
      out.print("<a href=\"" + versionId + "admin_upload.htm\" onMouseOver=\"document.images['AdminAnnouncements'].src = '" +versionId+ "images/AdminAnnouncements-over.png'\" onMouseOut=\"document.images['AdminAnnouncements'].src = '" +versionId+ "images/AdminAnnouncements.png'\"><img name=\"AdminAnnouncements\" src=\"" + versionId + "images/AdminAnnouncements.png\" hspace=\"0\" border=\"0\" alt=\"Upload the Announcements Page\"></a>" + hc.NON_BREAK_SPACE);
      out.print("<a href=\"" + versionId + "admin_services.htm\" onMouseOver=\"document.images['AdminSettings1'].src = '" +versionId+ "images/AdminSettings1-over.png'\" onMouseOut=\"document.images['AdminSettings1'].src = '" +versionId+ "images/AdminSettings1.png'\"><img name=\"AdminSettings1\" src=\"" + versionId + "images/AdminSettings1.png\" hspace=\"0\" border=\"0\" alt=\"Change Your Password\"></a>" + hc.NON_BREAK_SPACE);
      out.print("<a href=\"" + versionId + "admin_probs.htm\" onMouseOver=\"document.images['AdminSupport'].src = '" +versionId+ "images/AdminSupport-over.png'\" onMouseOut=\"document.images['AdminSupport'].src = '" +versionId+ "images/AdminSupport.png'\"><img name=\"AdminSupport\" src=\"" + versionId + "images/AdminSupport.png\" hspace=\"0\" border=\"0\" alt=\"Contact ForeTees Support\"></a>" + hc.NON_BREAK_SPACE);
      out.print("<a href=\"" + versionId + "servlet/Logout\" target=\"_top\" onMouseOver=\"document.images['AdminLogout'].src = '" +versionId+ "images/AdminLogout-over.png'\" onMouseOut=\"document.images['AdminLogout'].src = '" +versionId+ "images/AdminLogout.png'\"><img name=\"AdminLogout\" src=\"" + versionId + "images/AdminLogout.png\" hspace=\"0\" border=\"0\" alt=\"Exit ForeTees\"></a>" + hc.NON_BREAK_SPACE);
      out.print("<a href=\"" + versionId + "admin_help.htm\" target=\"_blank\" onMouseOver=\"document.images['AdminHelp'].src = '" +versionId+ "images/AdminHelp-over.png'\" onMouseOut=\"document.images['AdminHelp'].src = '" +versionId+ "images/AdminHelp.png'\"><img name=\"AdminHelp\" src=\"" + versionId + "images/AdminHelp.png\" hspace=\"0\" border=\"0\" alt=\"Get Help on Using ForeTees\"></a>");

  }

   /**
  ***************************************************************************************
  *
  * This method will draw the Email Distribution List navigation bar as a set of links aligned
  * vertically.
  *
  * @param selectedAction the name of the action to display as disabled. An empty string
  *                       or null will result in all the actions displaying as enabled.
  * @param out the output stream in which to write the html.
  *
  ***************************************************************************************
  **/

  public static void drawDistListNavBar(String selectedAction, PrintWriter out)
  {

    //ActionModel distListNavBarActionModel = buildDistListNavBar();
    //NavBarRenderer.render(distListNavBarActionModel, selectedAction, out);

  }

  private static ActionModel buildDistListNavBar()
  {
    ActionModel buildDistListNavBar = new ActionModel();

    buildDistListNavBar.setLabel("Email");

    buildDistListNavBar.add(new Action(SEND_EMAIL_TO_DIST_LIST, "Send Email", "Send an email to a list of members by choosing a distribution list", "javascript:constructEmail('" + versionId + "servlet/Send_email', '" + SEND_EMAIL_TO_DIST_LIST + "')"));
    buildDistListNavBar.add(new Action(VIEW_DIST_LIST, "View Distribution Lists", "View the list of your email distribution lists", versionId + "servlet/Communication"));

    return buildDistListNavBar;

  }

  public static ArrayList<String> getSelectedNames(String stringNames)
  {

    ArrayList<String> names = new ArrayList<String>();

    StringTokenizer str = new StringTokenizer(stringNames, ";");
    while (str.hasMoreTokens())
    {
      names.add(str.nextToken());
    }

    return names;

  }

}
