/***************************************************************************************
 *   Admin_addproshopuser:  This servlet will process the 'add proshop user' request  *
 *
 *   created: 11/22/2003   JAG (Reworked for Proshop Users - 7/01/2008 BSK)
 *
 *   last updated:
 *
 *   2/10/11  Add SYSCONFIG_MANAGECONTENT to limited access proshop system.
 *   9/03/09  Added processing for handling activities
 *   2/03/09  Add DEMOCLUBS_CHECKIN, DEMOCLUBS_MANAGE, DINING_REQUEST, and DINING_CONFIG fields to limited access proshop system
 *   9/02/08  removed TS_CTRL_EMAIL limited access proshop restriction code
 *   8/19/08  Added Limited Access Proshop User tee sheet display options (hdcp, mnum, bag)
 *   7/22/08  Changed so focus is given to "Username" field instead of "First Name"
 *   7/14/08  Updated/Modified to allow creation of new proshop users and set attributes
 *   4/24/08  Update Connection object to use SystemUtils.getCon()
 *   3/10/04  Changed the "Days in Advance" to be text input fields instead
 *                     of dropdowns to allow for 365 days.
 *
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
import com.foretees.client.StyleSheetConstants;
import com.foretees.client.action.Action;
import com.foretees.client.action.ActionHelper;
import com.foretees.client.action.ActionModel;

import com.foretees.client.attribute.Attribute;
import com.foretees.client.attribute.Checkbox;
import com.foretees.client.attribute.SelectionList;

import com.foretees.client.form.FormModel;
import com.foretees.client.form.FormRenderer;

import com.foretees.client.layout.LayoutHelper;
import com.foretees.client.layout.LayoutModel;
import com.foretees.client.layout.Separator;

import com.foretees.client.table.RowModel;

import com.foretees.common.FeedBack;
import com.foretees.common.Labels;
import com.foretees.common.ProcessConstants;
import com.foretees.common.getActivity;

import com.foretees.common.help.Help;

import com.foretees.member.Member;
import com.foretees.member.MemberHelper;

/**
***************************************************************************************
*
* This servlet will draw the form for adding a new proshop user as well as process the form to
* add the proshop user to the database
*
***************************************************************************************
**/

public class Admin_addproshopuser extends HttpServlet {


  private static String versionId = ProcessConstants.CODEBASE;

  /**
  ***************************************************************************************
  *
  * This method will build the form for creating a new member
  *
  ***************************************************************************************
  **/

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

    if (session == null) {

      return;
    }

    String club = (String)session.getAttribute("club");

    Connection con = SystemUtils.getCon(session);            // get DB connection

    if (con == null) {

      out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"" +versionId+ "servlet/Admin_announce\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
    }



    drawBeginningOfPageBeforeForm(null, out);
    FormModel form = null;

    try
    {
      form = buildNewForm(req, resp, session, con, out);
    }
    catch (Exception e)
    {
     return;
    }

    //add the form to the session to retrieve later in case the user puts in bad data
    req.getSession().setAttribute(Member.ADD_PROSHOP_USER_FRM, form);

    FormRenderer.render(form, out);

    drawEndOfPageAfterForm(out);
 }   // end of doGet


  /**
  ***************************************************************************************
  *
  * This method will process the form data for a new proshop user
  *
  ***************************************************************************************
  **/

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    ResultSet rs = null;
    Member member = new Member();

    HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

     if (session == null) {

        return;
     }

     String club = (String)session.getAttribute("club");

     Connection con = SystemUtils.getCon(session);            // get DB connection

     if (con == null) {

        out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"" +versionId+ "servlet/Admin_announce\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        return;
     }

     FormModel addProshopUserForm = null;
     Object theForm = session.getAttribute(Member.ADD_PROSHOP_USER_FRM);

     if (theForm != null && theForm instanceof FormModel)
     {
       addProshopUserForm = (FormModel)theForm;
     }
     else
     {
      try
      {
        //It is possible that the user hit the back button and got to an old form and then
        //clicked save, in this case there isn't one in session, so create one
        addProshopUserForm = buildNewForm(req, resp, session, con, out);
        session.setAttribute(Member.ADD_PROSHOP_USER_FRM, addProshopUserForm);
        addProshopUserForm.update(req, resp, out);
      }
      catch (Exception e)
      {
        return;
      }
     }

     FeedBack feedback = new FeedBack();

     // Get all the parameters entered and validate them

     //  first name (optional field! only validate if not blank)
     String fname = req.getParameter(member.FIRST_NAME);
     if (!fname.equals("")) {
         feedback = (member.isFirstNameValid(member.FIRST_NAME, req));
         if (!feedback.isPositive())
         {
           //the first name has invalid data, update the form and return with error message
           updateFormAndReturnUser(addProshopUserForm, feedback, req, resp, session, out);
           return;
         }
     }

     //  last name (optional field! only validate if not blank)
     String lname = req.getParameter(member.LAST_NAME);
         if (!lname.equals("")) {
         feedback = (member.isLastNameValid(member.LAST_NAME, req));
         if (!feedback.isPositive())
         {
           //the last name has invalid data, update the form and return with error message
           updateFormAndReturnUser(addProshopUserForm, feedback, req, resp, session, out);
           return;
         }
     }

     //  mid initial
     String mname = req.getParameter(member.MIDDLE_INITIAL);

     //  username
     String user = req.getParameter(member.USER_NAME);
     feedback = (member.isProshopUserNameValid(member.USER_NAME, req));
     if (!feedback.isPositive())
     {
       //the user name has invalid data, update the form and return with error message
       updateFormAndReturnUser(addProshopUserForm, feedback, req, resp, session, out);
       return;
     }

     feedback = member.memberExists(user, member.USER_NAME, con);
     if (!feedback.isPositive() )
     {
       //the member number already exists, update the form and return with error message
       feedback.setAffectedField(member.USER_NAME);
       updateFormAndReturnUser(addProshopUserForm, feedback, req, resp, session, out);
       return;
     }

     //  password
     String password = req.getParameter(member.PASSWORD);
     feedback = (member.isPasswordValid(member.PASSWORD, req));
     if (!feedback.isPositive())
     {
       //the password has invalid data, update the form and return with error message
       updateFormAndReturnUser(addProshopUserForm, feedback, req, resp, session, out);
       return;
     }

     int activity_id = Integer.parseInt(req.getParameter("activity_id"));
     int default_entry = 0;

     if (req.getParameter("defaultEntryCb") != null) {
         default_entry = 1;
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

     //  add proshop user to the database
     try {
         
         PreparedStatement stmt = con.prepareStatement (
             "INSERT INTO login2 (" +
                     "username, activity_id, default_entry, password, inact, name_last, name_first, name_mi, message, display_bag, " +
                     "display_mnum, display_hdcp, " +
                     "DEMOCLUBS_MANAGE, DEMOCLUBS_CHECKIN, DINING_CONFIG, DINING_REQUEST, EVNTSUP_UPDATE, EVNTSUP_MANAGE, EVNTSUP_VIEW, LESS_UPDATE, LESS_CONFIG, LESS_VIEW, " +
                     "LOTT_UPDATE, LOTT_APPROVE, REPORTS, REST_OVERRIDE, SYSCONFIG_CLUBCONFIG, SYSCONFIG_EVENT, SYSCONFIG_LOTTERY, SYSCONFIG_MEMBERNOTICES, SYSCONFIG_RESTRICTIONS, SYSCONFIG_TEESHEETS, " +
                     "SYSCONFIG_WAITLIST, TS_CHECKIN, TS_PRINT, TS_POS, TS_UPDATE, TS_VIEW, TS_PAST_UPDATE, TS_PAST_VIEW, TS_CTRL_TSEDIT, TS_CTRL_FROST, " +
                     "TS_PACE_UPDATE, TS_PACE_VIEW, TOOLS_ANNOUNCE, TOOLS_HDCP, TOOLS_SEARCHTS, TOOLS_EMAIL, SYSCONFIG_MANAGECONTENT, WAITLIST_UPDATE, WAITLIST_MANAGE, WAITLIST_VIEW" +
                     ") " +
                     "VALUES (" +
                     "?,?,?,?,0,?,?,?,'',?," +
                     "?,?," +
                     "?,?,?,?,?,?,?,?,?,?," +
                     "?,?,?,?,?,?,?,?,?,?," +
                     "?,?,?,?,?,?,?,?,?,?," +
                     "?,?,?,?,?,?,?,?,?,?)");
         
         stmt.clearParameters();               // clear the parms
         stmt.setString(1, user);              // put the parm in stmt
         stmt.setInt(2, activity_id);
         stmt.setInt(3, default_entry);
         stmt.setString(4, password);
         stmt.setString(5, lname);
         stmt.setString(6, fname);
         stmt.setString(7, mname);
         
         int offsetVal = 8;             // start initial loop at this value
         for (int i=0; i<MemberHelper.NUM_TEE_SHEET_OPTIONS; i++) {
             stmt.setInt(i + offsetVal, tsOpts[i]);
         }
         int offsetVal2 = offsetVal + MemberHelper.NUM_TEE_SHEET_OPTIONS;    // start 2nd loop at this value
         for (int i=0; i<MemberHelper.NUM_LIMITED_ACCESS_TYPES; i++) {
             stmt.setInt(i + offsetVal2, ltdAccess[i]);
         }
         
         stmt.executeUpdate();          // execute the prepared stmt
         
         stmt.close();   // close the stmt
         
        //User was successfully added to the database, remove the form from session so that
        //the data is not reused.
        session.removeAttribute(Member.ADD_PROSHOP_USER_FRM);

        String nextAction = req.getParameter(ActionHelper.NEXT_ACTION);
        if (nextAction.equals(ActionHelper.UPDATE_AND_RETURN))
        {
          resp.sendRedirect(versionId + "servlet/Admin_addproshopuser");
        }
        else
        {
          resp.sendRedirect(versionId + "servlet/Admin_proshopusers");

        }

     }
     catch (Exception exc) {

      exc.printStackTrace();
        out.println(SystemUtils.HeadTitleAdmin("Database Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Sorry, we encountered an error while updating the database.");
        out.println("<BR>Error = " + exc.getMessage());
        out.println("<BR><BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<br><br><font size=\"2\">");
        out.println("<form method=\"get\" action=\"" +versionId+ "servlet/Admin_announce\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        return;

     }

 }   // end of doPost


         
  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that comes before the add member
  * form
  *
  * @param feedback the feedback model that contains any messages to present to the user
  *                 upon loading the page
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private void drawBeginningOfPageBeforeForm(FeedBack feedback, PrintWriter out)
  {
    ActionModel pageActions = new ActionModel();
    Action addMemHelp = new Action(ActionHelper.HELP, Labels.HELP);
    addMemHelp.setUrl("javascript:openNewWindow('" + versionId + Help.ADD_PROSHOP_USER + "', 'AddProshopUserOnlineHelp', '" + Help.WINDOW_SIZE + "')");

    pageActions.add(addMemHelp);

    out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Add Proshop User to Database Main Page"));
    String onLoad = "";

    if (feedback != null)
    {
      onLoad = "javascript:displayFeedbackMessage('" + feedback.get(0) + "');document.pgFrm." + feedback.getAffectedField() + ".focus()";
    }
    else
    {
      onLoad = "document.pgFrm." + Member.USER_NAME + ".focus()";

    }

    LayoutHelper.drawBeginPageContentWrapper(onLoad, null, out);
    ActionHelper.drawAdminHotelUserNavBar(null, out);
    LayoutHelper.drawBeginMainBodyContentWrapper("Add new proshop user", pageActions, out);

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that comes after the add member
  * form
  *
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private void drawEndOfPageAfterForm(PrintWriter out)
  {
    LayoutHelper.drawEndMainBodyContentWrapper(out);
    LayoutHelper.drawFooter(out);
    LayoutHelper.drawEndPageContentWrapper(out);
    out.flush();

  }

  /**
  ***************************************************************************************
  *
  * This method will redraw the the page if the validation of one of the input fields for
  * the add proshop user form fails.  It will update the form with the data from the request.
  *
  * @pararm addProshopUserForm the form model that contains the components to render in the page
  * @param feedback the feedback model that contains any messages to present to the user
  *                 upon loading the page
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private void updateFormAndReturnUser(FormModel addProshopUserForm, FeedBack feedback, HttpServletRequest req,HttpServletResponse resp, HttpSession session, PrintWriter out)
  {
    if (addProshopUserForm != null)
    {
      addProshopUserForm.update(req, resp, out);
      session.setAttribute(Member.ADD_PROSHOP_USER_FRM, addProshopUserForm);
    }

    drawBeginningOfPageBeforeForm(feedback, out);
    FormRenderer.render(addProshopUserForm, out);
    drawEndOfPageAfterForm(out);


  }

  /**
  ***************************************************************************************
  *
  * This method will build a new form model for creating a new proshop user
  *
  * @param req the servlet request for this request
  * @param resp the servlet response for this request
  * @param session the session for the user
  * @param con the database connection for the user
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private FormModel buildNewForm(HttpServletRequest req,HttpServletResponse resp, HttpSession session, Connection con, PrintWriter out) throws Exception
  {
      ResultSet rs = null;
      Member member = new Member();
      RowModel guestTypes = null;
    
      boolean isSelected = true;
      boolean isProshop = true;
      boolean existing_user = false;

      // See if this is an existing user (adding new activity)
      if (req.getParameter("existing_user") != null && Integer.parseInt(req.getParameter("existing_user")) == 1) {
          existing_user = true;
      }

      String club = (String)session.getAttribute("club");
      String user = "";
      String pass = "";

      //create the form
      FormModel form = new FormModel("pgFrm", FormModel.POST, "bot");
      form.setNumColumns(4);
      form.addHiddenInput("formId", Member.ADD_PROSHOP_USER_FRM);
      form.addHiddenInput(ActionHelper.NEXT_ACTION, "");

      //create the action model for this form and add it to the form model
      ActionModel formActions = new ActionModel();
      
      // get the activity id
      int activity_id = 0;

      String okUrl = "javascript:update('" + versionId + "servlet/Admin_addproshopuser', 'updateAndClose')";
      Action okAction = new Action("updateAndClose", Labels.OK, "Save the changes and return to Proshop Users menu.", okUrl);
      formActions.add(okAction);

      String cancelUrl = "javascript:cancel('" + versionId + "servlet/Admin_proshopusers', 'cancel', '" + Labels.WARNING_CHANGES_MAY_BE_LOST + "')";
      Action cancelAction = new Action("cancel", Labels.CANCEL, Labels.RETURN_NO_CHANGES_SAVED, cancelUrl);
      formActions.add(cancelAction);

      form.setActions(formActions);

      RowModel loginRow = new RowModel();
      loginRow.setId("loginRow");


      if (existing_user) {

          // For existing users, get the passed username/pass values and pass them as hidden variables.  We don't allow user to modify them.
          PreparedStatement pstmt = null;
          ResultSet rs2 = null;

          user = req.getParameter("username");

          pstmt = con.prepareStatement("SELECT password FROM login2 WHERE username = ? LIMIT 1");
          pstmt.clearParameters();
          pstmt.setString(1, user);
          rs = pstmt.executeQuery();

          if (rs.next()) {
              pass = rs.getString("password");
          }

          // Display the values as plain non-editable text
          loginRow.add("Username (for login): " + user, "frm");

          // Add the values as hidden form elements
          form.addHiddenInput(member.USER_NAME, user);
          form.addHiddenInput(member.PASSWORD, pass);

      } else {

          // For brand new users, use normal setup
          Attribute userName = new Attribute(member.USER_NAME, member.USER_NAME_LABEL, "", Attribute.EDIT);
          userName.setSize("15");
          userName.setMaxLength("15");
          Attribute password = new Attribute(member.PASSWORD, member.PASSWORD_LABEL, "", Attribute.EDIT);
          password.setSize("10");
          password.setMaxLength("10");
          
          loginRow.add(userName, "frm", 2);
          loginRow.add(password, "frm", 2);
      }


      form.addRow(loginRow);

      //Construct a row for the name
      RowModel nameRow = new RowModel();
      nameRow.setId("nameRow");

      Attribute firstName = new Attribute(member.FIRST_NAME, member.FIRST_NAME_LABEL, "", Attribute.EDIT);
      firstName.setSize("20");
      firstName.setMaxLength("20");
      Attribute middleInitial = new Attribute(member.MIDDLE_INITIAL, member.MIDDLE_INITIAL_LABEL, "", Attribute.EDIT);
      middleInitial.setSize("1");
      middleInitial.setMaxLength("1");
      Attribute lastName = new Attribute(member.LAST_NAME, member.LAST_NAME_LABEL, "", Attribute.EDIT);
      lastName.setSize("20");
      lastName.setMaxLength("20");

      nameRow.add(firstName, "frm");
      nameRow.add(middleInitial, "frm");
      nameRow.add(lastName, "frm");
      form.addRow(nameRow);

      //Add a separator
      form.addSeparator(new Separator());

      if (getActivity.isConfigured(con)) {

          boolean newUser = false;
          boolean newActivity = false;

          if (existing_user) {
              newUser = false;
              newActivity = true;
          } else {
              newUser = true;
              newActivity = false;
          }

          MemberHelper.addActivitySelectorToForm(form, user, activity_id, out, con, newUser, newActivity);

          //Add a separator
          form.addSeparator(new Separator());

      } else {

          form.addHiddenInput(Member.ACTIVITY_ID_LABEL, "0");
      }
      
      //Add checkboxes for Tee Sheet Options
      MemberHelper.addTeeSheetOptionsToForm(form, rs, out, true);
      
      //Add a separator
      form.addSeparator(new Separator());
      
      //Add checkboxes for Feature Access options
      MemberHelper.addLimitedAccessTypesToForm(form, rs, out, true);

      return form;
  }
}
