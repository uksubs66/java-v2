/***************************************************************************************
*   Admin_updateproshopuser:  This servlet will process the the request to update a proshop
*                             users information
*
*   created: 11/22/2003  JAG (Reworked for Proshop Users - 7/01/2008 BSK)
*
*   last updated:
*
*  2/10/11  Add SYSCONFIG_MANAGECONTENT to proshop users for Custom Email Content feature.
*   9/03/09  Added processing for handling activities
*   2/03/09  Add DEMOCLUBS_CHECKIN, DEMOCLUBS_MANAGE, DINING_REQUEST, and DINING_CONFIG fields to limited access proshop system
*   9/02/08  Commented out TS_CTRL_EMAIL limited access proshop restriction
*   8/19/08  Added Limited Access Proshop User tee sheet display options (hdcp, mnum, bag)
*   8/11/08  Adjusted updateMem to account for additional SYSCONFIG limited access types in SQL stmt
*   7/14/08  Updated/Modified to update proshop user information in the database when called by Admin_editproshopuser
*   4/24/08  Update Connection object to use SystemUtils.getCon()
*   3/10/05  Changed to validate the days in advance.
*
*
***************************************************************************************
*/


//third party imports
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

//foretees imports
import com.foretees.client.action.ActionHelper;
import com.foretees.client.form.FormModel;
import com.foretees.common.FeedBack;
import com.foretees.common.ProcessConstants;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;

/**
***************************************************************************************
*
* This servlet will process the form data for editing information about an
* existing proshop user
*
***************************************************************************************
**/

public class Admin_updateproshopuser extends HttpServlet {

  //initialize the attributes
  private static String versionId = ProcessConstants.CODEBASE;

  /**
  ***************************************************************************************
  *
  * This method will forward the request and response onto the the post method
  *
  ***************************************************************************************
  **/

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    doPost(req, resp);

  }

  /**
  ***************************************************************************************
  *
  * This method will process the request to update a proshop users information
  *
  ***************************************************************************************
  **/

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

   //check for intruder
    HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

    if (session == null) {

      return;
    }

    // Process request according to the next action

    String nextAction = (String)(req.getAttribute(ActionHelper.NEXT_ACTION));
    String redirectURL = "";
    
    int activity_id = 0;
    
    if (nextAction == null || nextAction.equals("")){
      nextAction = req.getParameter(ActionHelper.NEXT_ACTION);
    }

    if (nextAction.equals(ActionHelper.CLEANUP) || nextAction.equals(ActionHelper.CANCEL))
    {
      //user left the page with out hitting a button or hit the cancel button
      redirectURL = cleanup(req, resp, session, out);
    }
    else if (nextAction.equals(ActionHelper.DELETE)) {
      //remove the record from the database and return them to the page they came from
      removeMem(req, out, session);
      redirectURL = cleanup(req, resp, session, out);
    } else {

        activity_id = Integer.parseInt(req.getParameter(Member.REQ_ACTIVITY_ID));

        boolean updateSuccessful = updateMem(req, resp, out, session);

        if (updateSuccessful){


            if (nextAction.equals(ActionHelper.UPDATE_AND_RETURN))
            {
                String username = req.getParameter(Member.REQ_USER_NAME);
                session.removeAttribute(Member.PROSHOP_USER_FEEDBACK);
                redirectURL = versionId + "servlet/Admin_editproshopuser?username=" + username + "&activity_id=" + activity_id;
            }
            else
            {
                redirectURL = cleanup(req, resp, session, out);
            }
        }
        else
        {
            String username = req.getParameter(Member.REQ_USER_NAME);
            redirectURL = versionId + "servlet/Admin_editproshopuser?username=" + username + "&activity_id=" + activity_id;
        }

    }

    resp.sendRedirect(redirectURL);

  }   // end of doPost

  /**
  ***************************************************************************************
  *
  * This method will remove a member record from the member table in the database
  *
  ***************************************************************************************
  **/

  private void removeMem(HttpServletRequest req, PrintWriter out, HttpSession sess) {


    String p_user = "";

    String club = (String)sess.getAttribute("club");

    Connection con = SystemUtils.getCon(sess);            // get DB connection

    if (con == null) {

      out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"" +versionId+ "servlet/Admin_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
    }

    // Get the parameters entered, original username (from hidden field)
    p_user = req.getParameter(Member.REQ_USER_NAME);

    // Delete the member from the member table
    int count = 0;
    try {

      PreparedStatement stmt = con.prepareStatement (
               "DELETE FROM login2 WHERE username = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, p_user);            // put the parm in stmt
      count = stmt.executeUpdate();     // execute the prepared stmt

      stmt.close();

    }
    catch (Exception exc) {

      dbError(out);
      return;
    }

    if (count == 0) {

      noMem(out);    // member does not exist - inform the user and return
      return;
    }
  }  // done with delete function

  /**
  ***************************************************************************************
  *
  * This method will update a member record in the member table in the database
  *
  ***************************************************************************************
  **/

  private boolean updateMem(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) {

    String club = (String)session.getAttribute("club");

    Connection con = SystemUtils.getCon(session);            // get DB connection

    if (con == null) {

      out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"" +versionId+ "servlet/Admin_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return false;
    }

    FormModel editProshopUserForm = null;
    Member member = new Member();

    Object theForm = session.getAttribute(Member.EDIT_PROSHOP_USER_FRM);

    if (theForm != null && theForm instanceof FormModel)
    {
      editProshopUserForm = (FormModel)theForm;
    }
    else
    {
      try
      {
        //editProshopUserForm = new FormModel();
        //session.setAttribute(Member.EDIT_PROSHOP_USER_FRM, editProshopUserForm);
        //editProshopUserForm.update(req, resp, out);
      }
      catch (Exception e)
      {
        return false;
      }
    }

    FeedBack feedback = new FeedBack();

    // Get all the parameters entered and validate them

    //  first name
    String fname = req.getParameter(member.FIRST_NAME);
    if (!fname.equals("")) {
        feedback = (member.isFirstNameValid(member.FIRST_NAME, req));
        if (!feedback.isPositive())
        {
         //the first name has invalid data, update the form and return with error message
         updateFormAndReturnUser(editProshopUserForm, feedback, req, resp, session, out);
         return false;
        }
    }

    //  last name
    String lname = req.getParameter(member.LAST_NAME);
    if (!lname.equals("")) {
        feedback = (member.isLastNameValid(member.LAST_NAME, req));
        if (!feedback.isPositive())
        {
         //the last name has invalid data, update the form and return with error message
         updateFormAndReturnUser(editProshopUserForm, feedback, req, resp, session, out);
         return false;
        }
    }

    //  mid initial
    String mname = req.getParameter(member.MIDDLE_INITIAL);

    //  username
    String user = req.getParameter("username");

    //  activity id
    int activity_id = Integer.parseInt(req.getParameter("activity_id"));
    int default_entry = 0;

    if (req.getParameter("defaultEntryCb") != null) default_entry = 1;
    
    //  inact
    int inact = 0;
    if (req.getParameter("inactive") != null) {
        inact = 1;
    }
    
    // create and populate int array containing tee sheet option selections
    int[] tsOpts = new int[MemberHelper.NUM_TEE_SHEET_OPTIONS];
    for (int i=0; i<MemberHelper.NUM_TEE_SHEET_OPTIONS; i++) {
        if (req.getParameter("tsOptsCb" + String.valueOf(i+1)) != null) {
            tsOpts[i] = 1;
        } else {
            tsOpts[i] = 0;
        }
    }
     
    // create and populate int array containing limited access features selections
    int[] ltdAccess = new int[MemberHelper.NUM_LIMITED_ACCESS_TYPES];
    for (int i=0; i<MemberHelper.NUM_LIMITED_ACCESS_TYPES; i++) {
        if (req.getParameter("ltd" + String.valueOf(i+1)) != null) {
            ltdAccess[i] = 1;
        } else {
            ltdAccess[i] = 0;
        }
    }

    //  Update the member in the member table
    //
    int count = 0;
    try {

      PreparedStatement stmt = con.prepareStatement (
      "UPDATE login2 SET inact = ?, name_last = ?, name_first = ?, name_mi = ?, message = '', default_entry = ?, " +
              "display_hdcp = ?, display_mnum = ?, display_bag = ?, " +
              "DEMOCLUBS_MANAGE = ?, DEMOCLUBS_CHECKIN = ?, DINING_CONFIG = ?, DINING_REQUEST = ?, EVNTSUP_UPDATE = ?, " +
              "EVNTSUP_MANAGE = ?, EVNTSUP_VIEW = ?, LESS_UPDATE = ?, LESS_CONFIG = ?, LESS_VIEW = ?, " +
              "LOTT_UPDATE = ?, LOTT_APPROVE = ?, REPORTS = ?, REST_OVERRIDE = ?, SYSCONFIG_CLUBCONFIG = ?, " +
              "SYSCONFIG_EVENT = ?, SYSCONFIG_LOTTERY = ?, SYSCONFIG_MEMBERNOTICES = ?, SYSCONFIG_RESTRICTIONS = ?, SYSCONFIG_TEESHEETS = ?, " +
              "SYSCONFIG_WAITLIST = ?, TS_CHECKIN = ?, TS_PRINT = ?, TS_POS = ?, TS_UPDATE = ?, " +
              "TS_VIEW = ?, TS_PAST_UPDATE = ?, TS_PAST_VIEW = ?, TS_CTRL_TSEDIT = ?, TS_CTRL_FROST = ?, " +
              "TS_PACE_UPDATE = ?, TS_PACE_VIEW = ?, TOOLS_ANNOUNCE = ?, TOOLS_HDCP = ?, TOOLS_SEARCHTS = ?, " +
              "TOOLS_EMAIL = ?, SYSCONFIG_MANAGECONTENT = ?, WAITLIST_UPDATE = ?, WAITLIST_MANAGE = ?, WAITLIST_VIEW = ? " +
      "WHERE username = ? AND activity_id = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setInt(1, inact);
      stmt.setString(2, lname);
      stmt.setString(3, fname);
      stmt.setString(4, mname);
      stmt.setInt(5, default_entry);
      
      int offsetVal = 6;    //start at this value
      for (int i=0; i<MemberHelper.NUM_TEE_SHEET_OPTIONS; i++) {
          stmt.setInt(i + offsetVal, tsOpts[i]);
      }
      
      int offsetVal2 = offsetVal + MemberHelper.NUM_TEE_SHEET_OPTIONS;
      for (int i=0; i<MemberHelper.NUM_LIMITED_ACCESS_TYPES; i++) {
          stmt.setInt(i + offsetVal2, ltdAccess[i]);
      }

      int offsetVal3 = offsetVal2 + MemberHelper.NUM_LIMITED_ACCESS_TYPES;
      stmt.setString(offsetVal3, user);
      stmt.setInt(offsetVal3 + 1, activity_id);
      
      count = stmt.executeUpdate();     // execute the prepared stmt

      stmt.close();


      // If default entry is set to 1, set all other activities for this proshop user to default_entry = 0
      if (default_entry == 1) {
          stmt = con.prepareStatement(
                  "UPDATE login2 SET default_entry = '0' " +
                  "WHERE username = ? AND activity_id <> ?");
          stmt.clearParameters();
          stmt.setString(1, user);
          stmt.setInt(2, activity_id);

          count = stmt.executeUpdate();
      }

    }
    catch (Exception exc) {
      exc.printStackTrace();
      dbError(out);
      return false;
    }

    editProshopUserForm.update(req, resp, out);
    session.setAttribute(Member.EDIT_PROSHOP_USER_FRM, editProshopUserForm);

    return true;
  }

  
  /**
  ***************************************************************************************
  *
  * This method will print an error message when there is a database error
  *
  ***************************************************************************************
  **/

  private void dbError(PrintWriter out) {

    out.println(SystemUtils.HeadTitleAdmin("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H3>Database Access Error</H3>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR>");
    out.println("<a href=\"javascript:history.back(1)\">Return</a>");
    out.println("</CENTER></BODY></HTML>");

  }

  /**
  ***************************************************************************************
  *
  * This method will print an error message when the user does not exist in the database
  *
  ***************************************************************************************
  **/

  private void noMem(PrintWriter out)
  {

    out.println(SystemUtils.HeadTitleAdmin("Input Error - Redirect"));
    out.println("<BODY><CENTER>");
    out.println("<p>&nbsp;</p>");
    out.println("<BR><H3>Input Error</H3><BR>");
    out.println("<BR><BR>Sorry, the member you specified does not exist in the database.<BR>");
    out.println("<BR>Please check your data and try again.<BR>");
    out.println("<BR><BR>");
    out.println("<a href=\"javascript:history.back(1)\">Return</a>");
    out.println("</CENTER></BODY></HTML>");

  }

  /**
  ***************************************************************************************
  *
  * This method update the form model with the data from the request and adds the form and
  * the feedback into the session
  *
  ***************************************************************************************
  **/

  private void updateFormAndReturnUser(FormModel editProshopUserForm, FeedBack feedback, HttpServletRequest req,HttpServletResponse resp, HttpSession session, PrintWriter out)
  {
    if (editProshopUserForm != null)
    {
      editProshopUserForm.update(req, resp, out);
      session.setAttribute(Member.EDIT_PROSHOP_USER_FRM, editProshopUserForm);
      session.setAttribute(Member.PROSHOP_USER_FEEDBACK, feedback);

     }
  }

  /**
  ***************************************************************************************
  *
  * This method remove the form model and the feedback from the session and return
  * the redirect url
  *
  ***************************************************************************************
  **/

  private String cleanup(HttpServletRequest req,HttpServletResponse resp, HttpSession session, PrintWriter out)
  {
    session.removeAttribute(Member.EDIT_PROSHOP_USER_FRM);
    session.removeAttribute(Member.PROSHOP_USER_FEEDBACK);
    return (versionId + "servlet/Admin_proshopusers");
  }


}
