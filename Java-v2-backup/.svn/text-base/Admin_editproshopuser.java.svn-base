/***************************************************************************************
 *   Admin_editproshopuser:  This servlet will process the 'edit proshop user' request
 *
 *
 *   created: 11/22/2003   JAG (Reworked for Proshop Users - 7/01/2008 BSK)
 *
 *   last updated:
 *
 *   9/03/09  Added processing for handling activities
 *   8/19/08  Added Limited Access Proshop User tee sheet display options (hdcp, mnum, bag)
 *   8/15/08  Removed unused getSelectedLimitedAccessTypes() method
 *   7/14/08  Updated/Modified to display proshop user information and allow editing of them
 *   4/24/08  Update Connection object to use SystemUtils.getCon()
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

import com.foretees.client.layout.LayoutModel;
import com.foretees.client.layout.LayoutHelper;
import com.foretees.client.layout.Separator;

import com.foretees.client.table.RowModel;

import com.foretees.common.FeedBack;
import com.foretees.common.help.Help;
import com.foretees.common.Labels;
import com.foretees.common.ProcessConstants;
import com.foretees.common.getActivity;

import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.common.Connect;

/**
***************************************************************************************
*
* This servlet will build the form for editing an existing member
*
***************************************************************************************
**/

public class Admin_editproshopuser extends HttpServlet {

  String omit = "";
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
  * This method will forward the request and response onto the the post method
  *
  ***************************************************************************************
  **/

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    ResultSet rs = null;
    String omit = "";

    HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

     if (session == null) {

        return;
     }

     String club = (String)session.getAttribute("club");

     Connection con = Connect.getCon(req);            // get DB connection

     if (con == null) {

        out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR><a href=\"" +versionId+ "servlet/Admin_announce\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        return;
     }

     //
     // Get the parameters entered
     //
     String user = req.getParameter(Member.REQ_USER_NAME);         //  username

     int activity_id = Integer.parseInt(req.getParameter(Member.REQ_ACTIVITY_ID));

     FormModel form = null;
//
//     Object theForm = req.getSession().getAttribute(Member.EDIT_PROSHOP_USER_FRM);
//
//     if (theForm != null && theForm instanceof FormModel)
//     {
//       form = (FormModel)theForm;
//     }


     if (form == null || req.getParameter("reload") != null){
       try {

          PreparedStatement stmt = con.prepareStatement (
                   "SELECT * FROM login2 WHERE username = ? AND activity_id = ?");

          stmt.clearParameters();        // clear the parms
          stmt.setString(1, user);       // put the parm in stmt
          stmt.setInt(2, activity_id);   //
          rs = stmt.executeQuery();      // execute the prepared stmt

          if (!rs.next()) {

             noMem(out);    // member does not exist - inform the user and return
             return;
          }

          form = buildNewForm(req, resp, session, con, rs, out);

          //add the form to the session to retrieve later
          req.getSession().setAttribute(Member.EDIT_PROSHOP_USER_FRM, form);

          stmt.close();              // close the stmt

       }
       catch (Exception exc) {

          dbError(out, exc);
          return;
       }
     }

    FeedBack feedback = null;

    Object theFeedback = session.getAttribute(Member.PROSHOP_USER_FEEDBACK);

    if (theFeedback != null && theFeedback instanceof FeedBack)
    {
      feedback = (FeedBack)theFeedback;
    }

    drawBeginningOfPageBeforeForm(feedback, out);

    FormRenderer.render(form, out);

    drawEndOfPageAfterForm(out);

  }   // end of doPost


         
 // *********************************************************
 // Member does not exists
 // *********************************************************

 private void noMem(PrintWriter out) {

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

 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1) {

   out.println(SystemUtils.HeadTitleAdmin("Database Error"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR>" + e1.getMessage());
   out.println("<BR><BR>");
   out.println("<a href=\"javascript:history.back(1)\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that comes before the edit proshop user
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
    Action addHtUserHelp = new Action(ActionHelper.HELP, Help.LABEL);
    addHtUserHelp.setUrl("javascript:openNewWindow('" + versionId + Help.EDIT_PROSHOP_USER + "', 'EditProshopUserOnlineHelp', '" + Help.WINDOW_SIZE + "')");

    pageActions.add(addHtUserHelp);

    out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Edit Proshop User Main Page"));
    String onLoad = "";

    if (feedback != null)
    {
      onLoad = "javascript:displayFeedbackMessage('" + feedback.get(0) + "');document.pgFrm." + feedback.getAffectedField() + ".focus()";
    }
    else
    {
      onLoad = "document.pgFrm." + Member.FIRST_NAME + ".focus()";
    }

    String onUnLoad = "";//"javascript:cleanup('" + versionId + "servlet/Admin_updateproshopuser', 'cleanup', 'Any changes you have made since you last saved will be lost.')";

    LayoutHelper.drawBeginPageContentWrapper(onLoad, onUnLoad, out);
    ActionHelper.drawAdminHotelUserNavBar(null, out);
    LayoutHelper.drawBeginMainBodyContentWrapper("Edit proshop user", pageActions, out);

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that comes after the edit proshop user
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
  * This method will build a new form model for editing an existing proshop user
  *
  * @param req the servlet request for this request
  * @param resp the servlet response for this request
  * @param session the session for the user
  * @param con the database connection for the user
  * @param rs the resultset that contains the member from the database
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private FormModel buildNewForm(HttpServletRequest req,HttpServletResponse resp, HttpSession session, Connection con, ResultSet rs, PrintWriter out) throws Exception
  {

    Member member = new Member();
    RowModel guestTypes = null;

    String club = (String)session.getAttribute("club");

    //create the form
    FormModel form = new FormModel("pgFrm", FormModel.POST, "bot");
    form.setNumColumns(4);
    form.addHiddenInput("formId", member.EDIT_PROSHOP_USER_FRM);
    form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
    form.addHiddenInput(Member.REQ_ACTIVITY_ID, "0");
    form.addHiddenInput("reload", "");
    String username = rs.getString("username");             // get the proshop user name
    form.addHiddenInput(Member.REQ_USER_NAME, username);

    //create the action model for this form and add it to the form model
    ActionModel formActions = new ActionModel();

    // get the activity id
    int activity_id = Integer.parseInt(req.getParameter(Member.REQ_ACTIVITY_ID));

/*
    String applyUrl = "javascript:update('" + versionId + "servlet/Admin_updateproshopuser', 'updateAndReturn')";
    Action applyAction = new Action("updateAndReturn", Labels.APPLY, "Save the changes and return to this page to continue editing.", applyUrl);
    formActions.add(applyAction);
*/
    String okUrl = "javascript:updateProshopUser('" + versionId + "servlet/Admin_updateproshopuser', 'updateAndClose', " + activity_id + ")";
    Action okAction = new Action("updateAndClose", Labels.OK, "Save the changes and return to proshop users list.", okUrl);
    formActions.add(okAction);

    String cancelUrl = "javascript:cancel('" + versionId + "servlet/Admin_updateproshopuser', 'cancel', '" + Labels.WARNING_CHANGES_MAY_BE_LOST + "')";
    Action cancelAction = new Action("cancel", Labels.CANCEL, Labels.RETURN_NO_CHANGES_SAVED, cancelUrl);
    formActions.add(cancelAction);

    form.setActions(formActions);

    //Construct a row for the login info
    RowModel loginRow = new RowModel();
    loginRow.setId("loginRow");

    String userName = member.USER_NAME_LABEL + ": " + username;
    loginRow.add(userName, "frm");

    boolean isSelected = false;
    if (rs.getInt("inact") == 1) { isSelected = true; }
    Checkbox inact = new Checkbox("inactive", "Inactive", "inact", isSelected);
    loginRow.add(inact, "frm");
    
    form.addRow(loginRow);
    
    //Construct a row for the name
    RowModel nameRow = new RowModel();
    nameRow.setId("nameRow");

    Attribute firstName = new Attribute(member.FIRST_NAME, member.FIRST_NAME_LABEL, rs.getString("name_first"), Attribute.EDIT);
    firstName.setSize("20");
    firstName.setMaxLength("20");
    Attribute middleInitial = new Attribute(member.MIDDLE_INITIAL, member.MIDDLE_INITIAL_LABEL, rs.getString("name_mi"), Attribute.EDIT);
    middleInitial.setSize("1");
    middleInitial.setMaxLength("1");
    Attribute lastName = new Attribute(member.LAST_NAME, member.LAST_NAME_LABEL, rs.getString("name_last"), Attribute.EDIT);
    lastName.setSize("20");
    lastName.setMaxLength("20");

    nameRow.add(firstName, "frm");
    nameRow.add(middleInitial, "frm");
    nameRow.add(lastName, "frm");

    form.addRow(nameRow);
    
    //Add a separator
    form.addSeparator(new Separator());

    if (getActivity.isConfigured(con)) {
        MemberHelper.addActivitySelectorToForm(form, username, activity_id, out, con, false, false);

        //Add a separator
        form.addSeparator(new Separator());
    }

    //Add checkboxes for Tee Sheet Options
    MemberHelper.addTeeSheetOptionsToForm(form, rs, out, false);
    
    //Add a separator
    form.addSeparator(new Separator());
    
    //Add checkboxes for Feature Access options
    MemberHelper.addLimitedAccessTypesToForm(form, rs, out, false);

    return form;
  }
  
}
