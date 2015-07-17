/***************************************************************************************
 *   Member:  This class represents a member
 *
 *
 *   created: 1/03/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *       3/29/10   Add support for USTA Number & NTRP Rating values
 *       9/03/09   Added assorted labels used in admin side proshop user pages when dealing with activities
 *       2/15/09   Add Tee Sheet Flag (tflag) labels.
 *       1/16/09   Add Walk/Cart Short label.
 *       7/30/08   Changed isEmailValid to static - UNDONE!
 *       7/14/08   Variable additions and isProshopUserNameValid() method for use with limited acccess proshop user management
 *       1/05/08   Add gender definitions
 *       7/19/07   Change non-golf (billing) flag labels from NON_GOLF to EXCLUDE.
 *       7/18/07   Add non-golf (billing) flag labels.
 *       5/17/07   Add inact flag labels.  Also change multiedit rows from 10 to 15.
 *       3/15/07   Add hdcp club & assoc numbers
 *       6/27/06   Add defns for webid field.
 *       2/09/06   Change max length of password from 10 to 15.
 *      11/09/05   Check for spaces in first and last names.
 *       9/02/05   Update the isEmailValid method to only require the email address and increase the number of checks.
 *       4/22/04   Add custom processing for Hazeltine Natl
 *       3/13/04   Bug fixes for checking if the member exists
 *       3/10/04   Added validation method for days in advance
 *
 ***************************************************************************************
 */

package com.foretees.member;

import java.sql.*;
import java.util.*;
import javax.servlet.http.*;

import com.foretees.common.FeedBack;


/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly validate member information
 *   added/edited through the user interface
 *
 ***************************************************************************************
 **/

public class Member {

  // Initialize the attributes

  public static final int FIRST_NAME_MIN_LENGTH = 1;
  public static final int FIRST_NAME_MAX_LENGTH = 20;
  public static final int LAST_NAME_MIN_LENGTH = 1;
  public static final int LAST_NAME_MAX_LENGTH = 20;
  public static final int MIDDLE_INITIAL_MIN_LENGTH = 0;
  public static final int MIDDLE_INTIAL_MAX_LENGTH = 1;
  public static final int USER_NAME_MIN_LENGTH = 1;
  public static final int USER_NAME_MAX_LENGTH = 15;
  public static final int PASSWORD_MIN_LENGTH = 4;
  public static final int PASSWORD_MAX_LENGTH = 15;
  public static final int EMAIL_MIN_LENGTH = 5;
  public static final int EMAIL_MAX_LENGTH = 50;
  public static final int PHONE_MAX_LENGTH = 24;
  public static final String DAYS_IN_ADVANCE_DEFAULT = "0";
  public static final boolean MEMBERSHIP_TYPE_REQUIRED = true;

  //constants for labels used for attributes of a member
  public static final String FIRST_NAME_LABEL = "First Name";
  public static final String LAST_NAME_LABEL = "Last Name";
  public static final String MIDDLE_INITIAL_LABEL = "MI";
  public static final String USER_NAME_LABEL = "Username (for login)";
  public static final String USER_NAME_LABEL_SHORT = "Username";
  public static final String ACTIVITY_NAME_LABEL_SHORT = "Activity";
  public static final String PASSWORD_LABEL = "Password (for login)";
  public static final String EMAIL_LABEL = "Email Address 1";
  public static final String EMAIL_LABEL_2 = "Email Address 2";
  public static final String EMAIL_LABEL_SHORT = "Email";
  public static final String MEMSHIP_TYPE_LABEL = "Membership Type";
  public static final String MEM_TYPE_LABEL = "Member Type";
  public static final String MEM_NUM_LABEL = "Member Number (Family Membership #)";
  public static final String MEM_NUM_LABEL_SHORT = "Mem#";
  public static final String WEB_ID_LABEL = "Web Site Member Id";
  public static final String WEB_ID_LABEL_SHORT = "Web Id";
  public static final String MEM_SUB_TYPE_LABEL = "Member Sub-Type";
  public static final String HANDICAP_HEADER = "Handicap: Use '+' for positive hndcp's (i.e. +2), '-' assumed";
  public static final String COURSE_HANDICAP_LABEL = "Course";
  public static final String USGA_HANDICAP_LABEL = "USGA";
  public static final String GHIN_LABEL = "Handicap System Number (GHIN or other)";
  public static final String GHIN_LABEL_SHORT = "Handicap Number";
  public static final String BAG_SLOT_LABEL = "Bag Storage Number";
  public static final String BAG_SLOT_LABEL_SHORT = "Bag Slot";
  public static final String GUEST_TYPE_LABEL = "Guest Type";
  public static final String POS_ID_LABEL = "POS Member Id (optional)";
  public static final String POS_ID_LABEL_SHORT = "POS Id";
  public static final String PHONE_NUMBER_LABEL = "Phone Number 1";
  public static final String PHONE_NUMBER_LABEL_2 = "Phone Number 2";
  public static final String BIRTH_DATE_LABEL = "Date of Birth";
  public static final String HDCP_CLUB_NUM_LABEL = "Club Number";
  public static final String HDCP_ASSOC_NUM_LABEL = "Association Number";
  public static final String INACT_LABEL = "Member Inactive";
  public static final String INACT_LABEL_SHORT = "Inactive";
  public static final String EXCLUDE_LABEL = "Exclude Member";
  public static final String EXCLUDE_LABEL_SHORT = "Exclude";
  public static final String GENDER_LABEL = "Gender";
  public static final String T_FLAG_LABEL = "Member Flag (For Tee Sheet)";
  public static final String DEFAULT_ACTIVITY_SYMBOL = "*";
  public static final String ACTIVITY_ID_LABEL = "activity_id";
//public static final String T_FLAG_LABEL_SHORT = "Member Flag";

  public static final String NTRP_RATING_LABEL = "NTRP Rating";
  public static final String USTA_NUM_LABEL = "USTA Number";
  
  public static final String WALK_CART_LABEL = "Walk/Cart Preference";
  public static final String WALK_CART_LABEL_SHORT = "Walk/Cart";

  //constants for help text used picking members to add to an email
  public static final String SEARCH_MEMSHIP_MEM_TYPE_HELP = "You can find members by searching on membership type and member type.  You can leave either of them blank.  Click on Search. The members that match the search criteria will be added to the Member List table below.<center><br>--or--<br></center>";
  public static final String SEARCH_LETTER_HELP = "Select a letter to search for members by last name.  Once you choose a letter a box will appear with the names of the members.  Select a name to add it to the Member List table below.";
  public static final String SEARCH_OK_HELP = "Once you have selected the members you want, click the OK button at the bottom of the page.";

  //constants for labels used for the pages for member administration
  public static final String VIEW_MEMBERS_LABEL = "View Members";
  public static final String ADD_MEMBER_LABEL = "Add Member";
  public static final String MEMBER_LIST_LABEL = "Member List";
  public static final String MEMBER_LIST_SUB_LABEL = "Last name begins with:";
  public static final String SEARCH_MEMBERS_LABEL = "Search for Members";

  //constants for the labels, params for windows to pick a membership type or member type
  public static final String SELECT_MEM_TYPE_LABEL = "Select a Member Type...";
  public static final String SELECT_MEM_SUB_TYPE_LABEL = "Select a Member Sub-Type...";
  public static final String SELECT_MEMSHIP_TYPE_LABEL = "Select a Membership Type...";
  public static final String SELECT_MEM_TYPE_WINDOW_PARAMS = "width=400, height=300, directories=no, location=no, menubar=no, scrollbars=yes, status=no, toolbar=no, resizable=yes";
  public static final String SELECT_MEM_TYPE_WINDOW_URL = "servlet/MemberType_searchsel";
  public static final String SELECT_MEM_TYPE_WINDOW_NAME = "searchMemberType";
  public static final String SELECT_MEMSHIP_TYPE_WINDOW_PARAMS = "width=400, height=300, directories=no, location=no, menubar=no, scrollbars=yes, status=no, toolbar=no, resizable=yes";
  public static final String SELECT_MEMSHIP_TYPE_WINDOW_URL = "servlet/MembershipType_searchsel";
  public static final String SELECT_MEMSHIP_TYPE_WINDOW_NAME = "searchMembershipType";


  public static final String VIEW_HOTEL_USERS_LABEL = "View Hotel Users";
  public static final String ADD_HOTEL_USER_LABEL = "Add Hotel User";
  public static final String HOTEL_USER_LIST_LABEL = "Hotel Users List";
  public static final String ADD_PROSHOP_USER_LABEL = "Add New Proshop User";
  public static final String VIEW_PROSHOP_USERS_LABEL = "View Proshop Users";


  //constants for request parameter names
  public static final String COURSE_HANDICAP = "c_hancap";
  public static final String USGA_HANDICAP = "g_hancap";
  public static final String FIRST_NAME = "memname_first";
  public static final String LAST_NAME = "memname_last";
  public static final String MIDDLE_INITIAL = "memname_mi";
  public static final String USER_NAME = "mem_username";
  public static final String PASSWORD = "mem_password";
  public static final String MEMSHIP_TYPE = "memship_type";
  public static final String MEM_TYPE = "mem_type";
  public static final String MEM_SUB_TYPE = "mem_subtype";
  public static final String EMAIL = "mem_email";
  public static final String EMAIL2 = "mem_email2";
  public static final String WALK_CART = "walk_cart";
  public static final String MEM_NUM = "memNum";
  public static final String WEBID = "webid";
  public static final String GHIN = "ghin";
  public static final String LOCKER = "locker";
  public static final String BAG_SLOT = "bag";
  public static final String BIRTH_MONTH = "bmm";
  public static final String BIRTH_DAY = "bdd";
  public static final String REQ_USER_NAME = "username";
  public static final String REQ_ACTIVITY_ID = "activity_id";
  public static final String EXISTING_USER = "existing_user";
  public static final String GUEST_TYPE = "guest_type";
  public static final String POS_ID = "posid";
  public static final String PHONE_NUM = "phoneNum";
  public static final String PHONE_NUM2 = "phoneNum2";
  public static final String BIRTH_DATE = "birthDate";
  public static final String HDCP_CLUB_NUM = "hdcpClubNum";
  public static final String HDCP_ASSOC_NUM = "hdcpAssocNum";
  public static final String GENDER = "gender";
  public static final String MEM_INACT = "mem_inact";
  public static final String EXCLUDE = "exclude";
  public static final String T_FLAG = "tflag";                 // tee sheet flag
  public static final String USTA_NUM = "usta_num";
  public static final String NTRP_RATING = "ntrp_rating";

  //constants for guest types
  public static final String GUEST_TYPE_REQ = "guest";

  //constants for feedback
  public static final String USER_ALREADY_EXISTS = "Sorry, the username you specified already exists in the database.  " +
    "Please use the edit feature to change an existing member record.";

  public static final String EDIT_MEM_FRM = "editMemFrm";
  public static final String ADD_MEM_FRM = "addMemFrm";
  public static final String SEARCH_MEM_FRM = "srchMemSel";
  public static final int MULTI_EDIT_PAGE_SIZE = 15;               // was 10 ????
  public static final String PAGE_NUMBER = "pgNum";
  public static final String NUMBER_PAGES = "numPages";
  public static final String NAME_SELECTOR_CELL_ID = "srchMemNameSelCell";
  public static final String SELECTION_LIST_OF_NAMES = "listOfNames";
  public static final String LIST_OF_NAMES_TO_SELECT_FROM = "listOfNamesToSelectFrom";
  public static final String SELECTED_MEMBERS = "selMemTable";
  public static final String SELECTED_MEMBERS_STRING = "selMemStr";
  public static final String MULTI_EDIT_MEM_FRM ="multiMemListFrm";
  public static final String MEM_FEEDBACK = "memFdb";
  public static final String MULTI_MEM_FEEDBACK = "multiEditFeedback";
  public static final String LETTER = "letter";
  public static final String VIEW_ALL = "view all";
  public static final String MEM_LIST_TABLE = "memberList";
  public static final String LIST_OF_NAMES = "srchMemSelLst";
  public static final String ADD_HOTEL_USER_FRM = "addHotelUserFrm";
  public static final String EDIT_HOTEL_USER_FRM = "editHotelUserFrm";
  public static final String ADD_PROSHOP_USER_FRM = "addProshopUserFrm";
  public static final String EDIT_PROSHOP_USER_FRM = "editProshopUserFrm";
  public static final String HOTEL_USER_FEEDBACK = "hotelUserFdb";
  public static final String PROSHOP_USER_FEEDBACK = "proshopUserFeedback";
  public static final String SEARCH_WINDOW_PARAMS = "width=700, height=600, directories=no, location=no, menubar=no, scrollbars=yes, status=no, toolbar=no, resizable=yes";
  public static final String SEARCH_WINDOW_URL = "servlet/Member_searchsel";
  public static final String SEARCH_WINDOW_NAME = "searchMemberList";
  public static final String LETTER_CHOOSER_ROW = "nameChooserRow";



  /**
  ***************************************************************************************
  *
  * Validates the first name of the member provided in the request.
  *
  ***************************************************************************************
  **/

  public FeedBack isFirstNameValid(String theParamName, HttpServletRequest theRequest){

    FeedBack feedback = new FeedBack();

    String firstName = theRequest.getParameter(theParamName);

    if (firstName != null)
    {
      if (firstName.length() < FIRST_NAME_MIN_LENGTH || firstName.length() > FIRST_NAME_MAX_LENGTH)
      {
        feedback.setPositive(false);
        feedback.addMessage("The length of the first name must be between " + FIRST_NAME_MIN_LENGTH + " and " + FIRST_NAME_MAX_LENGTH + "  characters.");
        feedback.setAffectedField(theParamName);
      }
      else
      {
        //
        //  Check for spaces
        //
        StringTokenizer tok = new StringTokenizer( firstName, " " );     // delimiters are space

        if ( tok.countTokens() > 1 ) {

          feedback.setPositive(false);
          feedback.addMessage("Spaces are not allowed in name fields. Please use a hyphen if necessary.");
          feedback.setAffectedField(theParamName);
        }
      }
    }
    else
    {
      feedback.setPositive(false);
      feedback.addMessage("The first name is a required field.");
      feedback.setAffectedField(theParamName);
    }

    return feedback;
  }

  /**
  ***************************************************************************************
  *
  * Validates the email address provided 
  *
  ***************************************************************************************
  **/

  public FeedBack isEmailValid(String email){

    FeedBack feedback = new FeedBack();

    String addrField = "";


    if (email != null && !email.equals( "" )) {

      if (email.length() < EMAIL_MIN_LENGTH || email.length() > EMAIL_MAX_LENGTH) {

         feedback.setPositive(false);
         feedback.addMessage("The length of the address must be between " + EMAIL_MIN_LENGTH + " and " + EMAIL_MAX_LENGTH + "  characters.");
         feedback.setAffectedField("email");

       } else {         // length ok, check for invalid characters

         char[] ca = email.toCharArray();         // create char array

         char[] userA = { '\'',  '.',  '%',  '-',  '!',  '#',  '$',  '&',  '*',  '+',  '/',  '=', '?',  '^',  '_',  '{',  '}', '|',  '~', '`' };  // valid chars for username

         int count = 0;
           
         boolean invalid = false;
         boolean charFound = false;
         boolean atFound = false;
         boolean periodFound = false;

         //
         //  validate the email address as follows:
         //
         //     before the '@' (username) -
         //
         //                    valid: a - z A - Z 0 - 9 ' . % - ! # $ & * + / = ? ^ _ { } | ~ `   
         //
         //     after the '@' and before '.' (host name) -
         //
         //                    valid: a - z A - Z 0 - 9 . _ % -
         //
         //     after the '@' and '.' (domain name extension - 'com', etc)) -
         //
         //                    valid: a - z A - Z (can only be 2, 3 or 4 characters!)
         //
         addrField = "user";         // start with username

         eloop1:
         for ( int i=0; i<ca.length; i++ ) {

            char letter = ca[i];            // get the next character

            if (invalid == false) {         // while no errors

               if (letter == ' ') {         // if a space

                  invalid = true;           // always an error

                  feedback.setPositive(false);
                  feedback.addMessage("The address contains a space character - please remove.");
                  feedback.setAffectedField("email");
                  break eloop1;

               } else {

                  if (addrField.equals( "user" )) {      // processing username?

                     //
                     //  validate the username (  bparise@  )
                     //
                     //      valid: a - z A - Z 0 - 9 ' . % - ! # $ & * + / = ? ^ _ { } | ~ `
                     //
                     if ( letter == '@' ) {

                        if (charFound == false) {        // if other char not already found

                           feedback.setPositive(false);
                           feedback.addMessage("The address does not contain any valid characters before the @ character - please correct.");
                           feedback.setAffectedField("email");
                           invalid = true;           // error
                           break eloop1;

                        } else {
                          
                           addrField = "host";       // done with username - check host name
                           atFound = true;           // indicate @ found
                           charFound = false;        // reset for host processing
                        }
                          
                     } else {

                        charFound = true;           // char found
                        invalid = true;             // init to error

                        eloop2:
                        for ( int i2=0; i2<userA.length; i2++ ) {

                           if (letter == userA[i2]) {          // valid character ?

                              invalid = false;                 // yes, letter is ok
                              break eloop2;
                           }
                        }
                          
                        if (invalid == true) {       // if letter not found yet - check others
                          
                           if ( (letter >= 'A' && letter <= 'Z') || 
                                (letter >= 'a' && letter <= 'z') ||
                                (letter >= '0' && letter <= '9') ) {

                              invalid = false;                 // letter is ok

                           } else {
                             
                              feedback.setPositive(false);
                              feedback.addMessage("The address contains one or more invalid characters (" +letter+ ") - please correct.");
                              feedback.setAffectedField("email");
                              break eloop1;
                           }
                        }
                     }

                  } else {

                     //
                     //  validate the host address (  @foretees.  )
                     //
                     //         valid: a - z A - Z 0 - 9 . _ % -
                     //
                     if ( letter == '.' ) {

                        if (charFound == false) {        // if other char not already found after the @

                           feedback.setPositive(false);
                           feedback.addMessage("The address does not contain any valid characters between the @ (at sign) and period - please correct.");
                           feedback.setAffectedField("email");
                           invalid = true;           // error
                           break eloop1;

                        } else {

                           periodFound = true;         // indicate . found
                           charFound = false;          // reset to prevent 2 periods in a row
                           count = 0;                  // reset counter (chars after a period)
                        }

                     } else {                      // not a period

                        charFound = true;           // char found
                        invalid = true;             // init to error

                        if ( (letter >= 'A' && letter <= 'Z') ||
                             (letter >= 'a' && letter <= 'z') ||
                             (letter >= '0' && letter <= '9') ||
                              letter == '_' || letter == '%' || letter == '-' ) {

                           invalid = false;          // letter is ok

                           if (periodFound == true) {

                              count++;               // count # of chars after period
                           }

                        } else {                     // invalid letter

                           feedback.setPositive(false);
                           feedback.addMessage("The address contains one or more invalid characters (" +letter+ ") - please correct.");
                           feedback.setAffectedField("email");
                           break eloop1;
                        }
                     }
                  }
               }
            }
              
         } // end for (eloop1)


         if (invalid == false) {

            if (atFound == false || periodFound == false) {

               feedback.setPositive(false);
               feedback.setAffectedField("email");
               if (atFound == false) {
                  feedback.addMessage("The address does not contain an at-sign (@) character - required.");
               } else {
                  if (periodFound == false) {
                     feedback.addMessage("The address does not contain a period (.) character after the @ - required.");
                  }
               }
                 
            } else {
           
               if (charFound == false) {     // if domain ext not found

                  feedback.setPositive(false);
                  feedback.addMessage("The address cannot end with a period - please correct.");
                  feedback.setAffectedField("email");

               } else {

                  if (count < 2 || count > 4) {     // if domain ext too short or too long

                     feedback.setPositive(false);
                     feedback.addMessage("The domain extension (i.e. '.com') is missing or invalid - please correct.");
                     feedback.setAffectedField("email");
                  }
               }
            }
         }
       }
    }

    return feedback;
  }

  /**
  ***************************************************************************************
  *
  * Validates the last name of the member provided in the request.
  *
  ***************************************************************************************
  **/

  public FeedBack isLastNameValid(String theParamName, HttpServletRequest theRequest){

    FeedBack feedback = new FeedBack();

    String lastName = theRequest.getParameter(theParamName);

    if (lastName != null)
    {
      if (lastName.length() < LAST_NAME_MIN_LENGTH || lastName.length() > LAST_NAME_MAX_LENGTH)
      {
        feedback.setPositive(false);
        feedback.addMessage("The length of the last name must be between " + LAST_NAME_MIN_LENGTH + " and " + LAST_NAME_MAX_LENGTH + "  characters.");
        feedback.setAffectedField(theParamName);
      }
      else
      {
        //
        //  Check for spaces
        //
        StringTokenizer tok = new StringTokenizer( lastName, " " );     // delimiters are space

        if ( tok.countTokens() > 1 ) {

          feedback.setPositive(false);
          feedback.addMessage("Spaces are not allowed in name fields. Please use a hyphen if necessary.");
          feedback.setAffectedField(theParamName);
        }
      }
    }
    else
    {
      feedback.setPositive(false);
      feedback.addMessage("The last name is a required field.");
      feedback.setAffectedField(theParamName);
    }

    return feedback;
  }

  /**
  ***************************************************************************************
  *
  * Validates the user name of the member provided in the request.
  *
  ***************************************************************************************
  **/

  public FeedBack isUserNameValid(String theParamName, HttpServletRequest theRequest){

    FeedBack feedback = new FeedBack();
    String userName = theRequest.getParameter(theParamName);

    if (userName != null)
    {
      if (userName.length() < USER_NAME_MIN_LENGTH || userName.length() > USER_NAME_MAX_LENGTH)
      {
        feedback.setPositive(false);
        feedback.addMessage("The length of the user name must be between " + USER_NAME_MIN_LENGTH + " and " + USER_NAME_MAX_LENGTH + "  characters.");
        feedback.setAffectedField(theParamName);
      }
    }
    else
    {
      feedback.setPositive(false);
      feedback.addMessage("The user name is a required field.");
      feedback.setAffectedField(theParamName);
    }

    return feedback;
  }  
  
  /**
  ***************************************************************************************
  *
  * Validates the user name of the proshop user provided in the request.
  *
  ***************************************************************************************
  **/

  public FeedBack isProshopUserNameValid(String theParamName, HttpServletRequest theRequest){

    FeedBack feedback = new FeedBack();
    String userName = theRequest.getParameter(theParamName);

    if (userName != null)
    {
      if (userName.length() < USER_NAME_MIN_LENGTH || userName.length() > USER_NAME_MAX_LENGTH)
      {
        feedback.setPositive(false);
        feedback.addMessage("The length of the user name must be between " + USER_NAME_MIN_LENGTH + " and " + USER_NAME_MAX_LENGTH + "  characters.");
        feedback.setAffectedField(theParamName);
      } else if (!userName.startsWith("proshop")) {
        feedback.setPositive(false);
        feedback.addMessage("The user name must begin with proshop.");
        feedback.setAffectedField(theParamName);
      }
    }
    else
    {
      feedback.setPositive(false);
      feedback.addMessage("The user name is a required field.");
      feedback.setAffectedField(theParamName);
    }

    return feedback;
  }

  /**
  ***************************************************************************************
  *
  * Validates the password entered for the member provided in the request.
  *
  ***************************************************************************************
  **/

  public FeedBack isPasswordValid(String theParamName, HttpServletRequest theRequest){

    FeedBack feedback = new FeedBack();
    String password = theRequest.getParameter(theParamName);

    if (password != null)
    {
      if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH)
      {
        feedback.setPositive(false);
        feedback.addMessage("The length of the password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH + "  characters.");
        feedback.setAffectedField(theParamName);
      }
    }
    else
    {
      feedback.setPositive(false);
      feedback.addMessage("The password is a required field.");
      feedback.setAffectedField(theParamName);
    }

    return feedback;
  }

  /**
  ***************************************************************************************
  *
  * Validates the course handicap entered for the member provided in the request.
  *
  ***************************************************************************************
  **/

  public FeedBack isCourseHandicapValid(String theParamName, HttpServletRequest theRequest)
  {

    FeedBack feedback = new FeedBack();
    String courseHandicap = theRequest.getParameter(theParamName);
    float course = -99;

    if (courseHandicap != null && !(courseHandicap.equals("")))
    {
      try
      {
        course = Float.parseFloat(courseHandicap);
      }
      catch (NumberFormatException e) {
         feedback.setPositive(false);
         feedback.addMessage("The course handicap must contain numeric data.");
         feedback.setAffectedField(theParamName);
      }

    }

    //not a required field so if the field was empty this is ok
    return feedback;

   }

  /**
  ***************************************************************************************
  *
  * Converts the course handicap entered for the member provided in the request to negative
  * if neither + or - is not specified.
  *
  ***************************************************************************************
  **/

  public float convertCourseHandicap(String theParamName, HttpServletRequest theRequest)
  {

    String courseHandicap = theRequest.getParameter(theParamName);
    float course = -99;

    if (courseHandicap != null && !(courseHandicap.equals("")))
    {
      course = Float.parseFloat(courseHandicap);               // course handicap

      if ((!courseHandicap.startsWith("+")) && (!courseHandicap.startsWith("-"))) {

         course = 0 - course;                    // make it a negative hndcp (normal)
      }
    }

    return course;
  }

  /**
  ***************************************************************************************
  *
  * Validates the USGA handicap entered for the member provided in the request.
  *
  ***************************************************************************************
  **/

  public FeedBack isUSGAHandicapValid(String theParamName, HttpServletRequest theRequest)
  {

    FeedBack feedback = new FeedBack();
    String usgaHandicap = theRequest.getParameter(theParamName);
    float usga = -99;

    if (usgaHandicap != null && !(usgaHandicap.equals("")))
    {
      try
      {
        usga = Float.parseFloat(usgaHandicap);
      }
      catch (NumberFormatException e) {
         feedback.setPositive(false);
         feedback.addMessage("The usga handicap must contain numeric data.");
         feedback.setAffectedField(theParamName);
      }

    }

    //not a required field so if the field was empty this is ok

    return feedback;
  }

  /**
  ***************************************************************************************
  *
  * Converts the USGA handicap entered for the member provided in the request to negative
  * if neither + or - is not specified.
  *
  ***************************************************************************************
  **/

  public float convertUSGAHandicap(String theParamName, HttpServletRequest theRequest)
  {

    String usgaHandicap = theRequest.getParameter(theParamName);
    float usga = -99;

    if (usgaHandicap != null && !(usgaHandicap.equals("")))
    {
      usga = Float.parseFloat(usgaHandicap);               // course handicap

      if ((!usgaHandicap.startsWith("+")) && (!usgaHandicap.startsWith("-"))) {

         usga = 0 - usga;                    // make it a negative hndcp (normal)
      }
    }

    return usga;
  }

  /**
  ***************************************************************************************
  *
  * Determines if the username entered is already in the database
  *
  ***************************************************************************************
  **/

  public FeedBack memberExists(String theUserName, String theParam, Connection con)
  {

    FeedBack feedback = new FeedBack();

    try {

      PreparedStatement stmt = con.prepareStatement (
               "SELECT password FROM member2b WHERE username = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, theUserName);       // put the parm in stmt
      ResultSet rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

        stmt.close();
        feedback.setPositive(false);
        feedback.addMessage("The user name you entered already exists in the system.");
        feedback.setAffectedField(theParam);
      }
      else{

        stmt = con.prepareStatement (
               "SELECT password FROM hotel3 WHERE username = ?");

        stmt.clearParameters();        // clear the parms
        stmt.setString(1, theUserName);       // put the parm in stmt
        rs = stmt.executeQuery();      // execute the prepared stmt

        if (rs.next()) {

          stmt.close();
          feedback.setPositive(false);
          feedback.addMessage("The user name you entered already exists in the system.");
          feedback.setAffectedField(theParam);

        }
        else
        {
          feedback.setPositive(true);
        }
      }
    }
    catch (Exception ignored) {
      //should probably handle this, but what to do?
      feedback.setPositive(false);
      feedback.addMessage("An error occurred while accessing the database.");
      feedback.setAffectedField(theParam);
    }

    return feedback;
  }

  /**
  ***************************************************************************************
  *
  * Determines if the user selected at least one guest type for the hotel user
  *
  ***************************************************************************************
  **/

  public FeedBack isGuestTypesValid(String theParamName, HttpServletRequest req)
  {

    FeedBack feedback = new FeedBack();
    boolean isOneSelected = false;

    for (int i=0; i<MemberHelper.NUM_GUEST_TYPES; i++)
    {

        String type = req.getParameter(Member.GUEST_TYPE_REQ + (i + 1));

        if (type != null && !(type.equals("")))
        {
          isOneSelected = true;
        }
    }


    if (!isOneSelected)
    {
      feedback.setPositive(false);
      feedback.addMessage("You must select at least one guest type this hotel user is allowed to use.");
      feedback.setAffectedField(theParamName);

    }
    else
    {
      feedback.setPositive(true);
    }

    return feedback;

  }

  /**
  ***************************************************************************************
  *
  * Validates the days in advance a hotel user can add a tee time provided in the request.
  *
  ***************************************************************************************
  **/

  public FeedBack isDaysInAdvanceValid(String[] theParamName, HttpServletRequest theRequest)
  {

    FeedBack feedback = new FeedBack();

    for (int i=0; i<theParamName.length;i++)
    {
      String day = theRequest.getParameter(theParamName[i]);

      //not a required field so if the field was empty this is ok
      if (day != null && !(day.equals("")))
      {
        try
        {
          int daysInAdvance = (new Integer(day)).intValue();

          if ( daysInAdvance < 0 || daysInAdvance > 365)
          {
             feedback.setPositive(false);
             feedback.addMessage("The days in advance must be a number between 0 and 365.");
             feedback.setAffectedField(theParamName[i]);
             break;
          }
        }
        catch (NumberFormatException e) {
           feedback.setPositive(false);
           feedback.addMessage("The days in advance must be a number between 0 and 365.");
           feedback.setAffectedField(theParamName[i]);
           break;
        }

      }
    }



    return feedback;
  }


}
