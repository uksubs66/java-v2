/***************************************************************************************
 *   Email:  This class represents a email
 *
 *
 *   created: 1/19/2004   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *       2/17/12  Added two new notes, NOTE_EVENT_LINKS, and NOTE_EDITOR_HELP to be displayed on the Proshop side email tool.
 *       6/03/08  Updated "NOTE" on member side
 *
 ***************************************************************************************
 */

package com.foretees.communication;

import java.io.*;
import java.sql.*;

import javax.servlet.http.*;

import com.foretees.client.form.FormModel;
import com.foretees.common.FeedBack;

/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly validate an email created through
 *   the ui.
 *
 ***************************************************************************************
 **/

public class Email {

  public static final String rev = "v5";
    
  // Initialize the attributes
  public static final int ADDR_MIN_LENGTH = 6;
  public static final int ADDR_MAX_LENGTH = 50;
  public static final int SUBJECT_MIN_LENGTH = 1;
  public static final int SUBJECT_MAX_LENGTH = 50;
  public static final int MESSAGE_MIN_LENGTH = 1;
  public static final int MESSAGE_MAX_LENGTH = 200;
  public static final int MAX_RECIPIENTS = 100;


  //constants for request parameters used for fields of an email
  public static final String TO = "toField";
  public static final String SUBJECT = "subjectField";
  public static final String MESSAGE = "messageField";

  //constants for help text used for fields of an email
  public static final String TO_HELP = "Use the Select... options or the Add Partners option in the Recipients table below to pick members to add to your email.";
  public static final String TO_HELP_PROSHOP = "Use the Select... options in the Recipients table below to pick members to add to your email.";
  public static final String SUBJECT_HELP = "Enter the subject for your email.";
  public static final String MESSAGE_HELP = "Enter the message for your email.";
  public static final String SEND_HELP = "When you have completed constructing your email, use the Send button at the bottom of the page to send the email.";
  public static final String NOTE = "<b>Note: </b>The maximum number of recipients is 100.  If you attempt to add more than 100 you will recieve an error message.  Also note that only members that have an email address registered with the system will be allowed to be added as a recipient.<p>&nbsp; &nbsp;<b>Tip:</b> You may compose longer emails in Word or another program of your choice, then cut-n-paste your email in to the message box.</p>";
  public static final String NOTE_PROSHOP = "<b>Note: </b>Only members that have an email address registered with the system will be allowed to be added as a recipient.";
  public static final String NOTE_EVENT_LINKS = "<font size=\"3\">Click here to <a href=\"Proshop_event_list\" target=\"_blank\">Get a Link URL for an Event</a></font>";
  public static final String NOTE_EDITOR_HELP = "<font size=\"3\">Click here for <a href=\"/" +rev+ "/web utilities/tiny_mce/TinyMCE-Content-User-Guide.pdf\" target=\"_blank\">Help with the Editor</a></font>";


  //constants for labels used for the pages for emails
  public static final String TO_LABEL = "Send To";
  public static final String RECIPIENTS_LABEL = "Recipients";
  public static final String SUBJECT_LABEL = "Subject";
  public static final String MESSAGE_LABEL = "Message";
  public static final String SEND_EMAIL_LIST_HEADER = "Send Email";

  //constants for feedback

  //constants for form elements
  public static final String EMAIL_FRM = "emailFrm";


  /**
  ***************************************************************************************
  *
  * Validates the subject of the email provided in the request.
  *
  ***************************************************************************************
  **/

  public FeedBack isSubjectValid(String theParamName, HttpServletRequest theRequest)
  {

    FeedBack feedback = new FeedBack();

    if (theParamName == null || theParamName.equals(""))
    {
      theParamName = SUBJECT;
    }

    String subject = theRequest.getParameter(theParamName);

    if (subject == null || (subject.length() < SUBJECT_MIN_LENGTH || subject.length() > SUBJECT_MAX_LENGTH))
    {
      feedback.setPositive(false);
      feedback.addMessage("The length of the subject must be between " + SUBJECT_MIN_LENGTH + " and " + SUBJECT_MAX_LENGTH + "  characters.");
      feedback.setAffectedField(theParamName);
    }

    return feedback;
  }

}
