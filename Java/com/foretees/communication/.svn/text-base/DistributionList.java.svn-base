/***************************************************************************************
 *   DistributionList:  This class represents a distribution list
 *
 *
 *   created: 1/17/2004   jag
 *
 *   last updated:       ******* keep this accurate *******
 *      
 *      11/27/07  Mirasol CC - Override max_list_size for members to allow 60  (Case# 1305)
 *
 ***************************************************************************************
 */

package com.foretees.communication;

import java.io.*;
import java.sql.*;

import javax.servlet.http.*;

import com.foretees.client.form.FormModel;
import com.foretees.common.FeedBack;
import com.foretees.common.ProcessConstants;

/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly validate communication information
 *   added/edited through the user interface
 *
 ***************************************************************************************
 **/

public class DistributionList {

  // Initialize the attributes

  public static final int LIST_NAME_MIN_LENGTH = 1;
  public static final int LIST_NAME_MAX_LENGTH = 50;
  public static final int MAX_NUM_LISTS = 25;
  public static final int MAX_LIST_SIZE = 30;
  public static final int MAX_LIST_SIZE_PROSHOP = 100;
  public static final String TABLE_NAME = "dist4";
  public static final String TABLE_NAME_PROSHOP = "dist4p";


  //constants for labels used for attributes of a list
  public static final String LIST_NAME_LABEL = "Distribution List Name";

  //constants for labels used for the pages for member administration
  public static final String VIEW_LIST_LABEL = "View Distribution Lists";
  public static final String ADD_LIST_LABEL = "Add Distribution List";
  public static final String EDIT_LIST_LABEL = "Edit Distribution List";
  public static final String COMMUNICATION_HEADER = "ForeTees Distribution Lists";
  public static final String ADD_DIST_LIST_HEADER = "ForeTees Add Distribution List to Database Main Page";
  public static final String EDIT_DIST_LIST_HEADER = "ForeTees Edit Distribution List Main Page";
  public static final String LIST_OF_NAMES_HEADER = "Distribution List Members";
  public static final String SEARCH_LISTS_LABEL = "Distribution Lists";

  //constants for help text used picking distribution lists to add to an email
  public static final String CHECKBOX_HELP = "Select the checkboxes next to the distribution list(s) that include the members to which you want to send an email.";
  public static final String OK_HELP = "Click the OK button at the bottom of the page to have the members of the selected lists added as recipients of your email.  You can use the Cancel button to exit out of this window without having members of a list added.";

  //constants for help text used when adding a distribution list
  public static final String NAME_HELP = "Enter the name for this distribution list";
  public static final String EDIT_NAME_HELP = "Enter a new name for the distribution list if desired.";
  public static final String ADD_TO_LIST_HELP = "Use the Select Members... option in the Distribution List Members table to pick members to add to this list.";
  public static final String ADD_OK_HELP = "Click the 'Save' or 'Save and Close'  button at the bottom of the page to save the list and return to this page to add another or to return to the send email page.  You can use the Cancel button to exit out of this window without saving the list.";
  public static final String EDIT_OK_HELP = "Click the 'Save' or 'Save and Close'  button at the bottom of the page to save the list and return to this page to make more changes to this distribution list or to return to the send email page.  You can use the Cancel button to exit out of this window without saving the list.";
  public static final String NOTE_MEMBER = "<b>Note: </b>The maximum number of members per distribution list is " + MAX_LIST_SIZE + ".  If you attempt to add more than " + MAX_LIST_SIZE + " you will recieve an error message and the \"Select Members...\" link will be disabled.  Also note that only members that have an email address registered with the system will be allowed to be added to a distribution list.";
  public static final String NOTE_PROSHOP = "<b>Note: </b>The maximum number of members per distribution list is " + MAX_LIST_SIZE_PROSHOP + ".  If you attempt to add more than " + MAX_LIST_SIZE_PROSHOP + " you will recieve an error message and the \"Select Members...\" link will be disabled.  Also note that only members that have an email address registered with the system will be allowed to be added to a distribution list.";

  //constants for help text used when adding a distribution list
  public static final String DELETE_LIST_HELP = "To modify a distribution list, select the Edit link in the Actions column of the row for that list";
  public static final String EDIT_LIST_HELP = "To permanently remove a distribution list, select the Delete link in the Actions column of the row for that list.";



  //constants for request parameter names
  public static final String LIST_NAME = "listName";
  public static final String ORIGINAL_LIST_NAME = "orgListName";
  public static final String LIST_OF_NAMES = "listOfNames";

  //constants for feedback
  public static final String LIST_ALREADY_EXISTS = "Sorry, the name you specified for the list already exists in the database.  ";

  public static final String EDIT_DIST_LIST_FRM = "editListFrm";
  public static final String ADD_DIST_LIST_FRM = "addListFrm";
  public static final String SEARCH_DIST_LIST_FRM = "searchListFrm";
  public static final String SELECTED_LISTS_STRING = "selLstStr";
  public static final String EMAIL_FRM = "emailFrm";
  public static final String LIST_FEEDBACK = "listFdb";
  public static final String LIST_TABLE = "distListTble";
  public static final String NAME_SELECTOR_CELL_ID = "nameSelector";
  public static final String SEARCH_WINDOW_PARAMS = "width=400, height=300, directories=no, location=no, menubar=no, scrollbars=yes, status=no, toolbar=no, resizable=yes";
  public static final String SEARCH_WINDOW_URL = "servlet/DistList_searchsel";
  public static final String SEARCH_WINDOW_NAME = "searchDistList";



  /**
  ***************************************************************************************
  *
  * Validates the name of the list provided in the request.
  *
  ***************************************************************************************
  **/

  public FeedBack isListNameValid(String theParamName, HttpServletRequest theRequest)
  {

    FeedBack feedback = new FeedBack();

    String name = theRequest.getParameter(theParamName);

    if (name != null)
    {
      if (name.length() < LIST_NAME_MIN_LENGTH || name.length() > LIST_NAME_MAX_LENGTH)
      {
        feedback.setPositive(false);
        feedback.addMessage("The length of the list name must be between " + LIST_NAME_MIN_LENGTH + " and " + LIST_NAME_MAX_LENGTH + "  characters.");
        feedback.setAffectedField(theParamName);
      }
    }
    else
    {
      feedback.setPositive(false);
      feedback.addMessage("A name for the list is a required field.");
      feedback.setAffectedField(theParamName);
    }

    return feedback;
  }

  /**
  ***************************************************************************************
  *
  * Determines if the distribution list name entered is already in the database
  *
  ***************************************************************************************
  **/

  public static FeedBack listExists(String theListName, String theUserName, Connection con, HttpSession session)
  {

    FeedBack feedback = new FeedBack();
    boolean listExists = false;
    String table_name = getTableName(session);

    try {

      PreparedStatement stmt = con.prepareStatement (
               "SELECT name FROM " + table_name + " WHERE owner = ? AND name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, theUserName);       // put the parm in stmt
      stmt.setString(2, theListName);       // put the parm in stmt

      ResultSet rs = stmt.executeQuery();      // execute the prepared stmt


      if (rs.next()) {

        feedback.setPositive(false);
        feedback.addMessage("The list name you entered already exists in the system.");
        feedback.setAffectedField(LIST_NAME);
      }
      stmt.close();

    }
    catch (Exception ignored) {
      //should probably handle this, but what to do?
      ignored.printStackTrace();
      feedback.setPositive(false);
      feedback.addMessage("An error occurred while accessing the database.");
    }

    return feedback;
  }

  public static int getMaxListSize(HttpSession session)
  {
    int max_list_size = DistributionList.MAX_LIST_SIZE;
    String user = (String)session.getAttribute("user");
    String club = (String)session.getAttribute("club");
    
    if (club.equals("mirasolcc")) max_list_size = 60; // Case# 1305
    
    if (ProcessConstants.isProshopUser(user))
    {
      max_list_size = DistributionList.MAX_LIST_SIZE_PROSHOP;
    }

    return max_list_size;
  }

  public static String getTableName(HttpSession session)
  {
    String table_name = DistributionList.TABLE_NAME;
    String user = (String)session.getAttribute("user");

    if (ProcessConstants.isProshopUser(user))
    {
      table_name = DistributionList.TABLE_NAME_PROSHOP;
    }

    return table_name;
  }

}
