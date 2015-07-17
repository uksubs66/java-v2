/***************************************************************************************
 *   Edit_distributionlist:  This servlet will display a page that allows the user to edit an existing
 *                          distribution list
 *
 *
 *   created: 2/08/2004   JAG
 *
 *   last updated:
 *
 *          4/24/08  Update ArrayList to use String instead of raw types
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
import com.foretees.client.action.Action;
import com.foretees.client.action.ActionModel;
import com.foretees.client.action.ActionHelper;
import com.foretees.client.attribute.Attribute;
import com.foretees.client.attribute.SelectionList;
import com.foretees.client.attribute.TextBox;
import com.foretees.client.form.FormModel;
import com.foretees.client.form.FormRenderer;
import com.foretees.client.layout.Separator;
import com.foretees.client.layout.LayoutHelper;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.client.misc.LetterChooser;
import com.foretees.client.misc.NameSelector;
import com.foretees.client.table.Cell;
import com.foretees.client.table.Column;
import com.foretees.client.table.RowModel;
import com.foretees.client.table.TableModel;
import com.foretees.common.FeedBack;
import com.foretees.common.Labels;
import com.foretees.common.ProcessConstants;
import com.foretees.common.help.Help;
import com.foretees.communication.CommunicationHelper;
import com.foretees.communication.DistributionList;
import com.foretees.communication.Email;
import com.foretees.event.Event;

/**
***************************************************************************************
*
* This servlet will display and process a page to send an email to a selected set of members
*
***************************************************************************************
**/

public class Edit_distributionlist extends HttpServlet {

  //initialize the attributes
  private static String versionId = ProcessConstants.CODEBASE;

  /**
  ***************************************************************************************
  *
  * This method will process the data from the Distributions List screen
  *
  ***************************************************************************************
  **/

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    doGet(req, resp);

  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();


    HttpSession session = null;

    //
    // This servlet can be called by both Proshop and Member users - find out which
    //
    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

       out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
       out.println("<BODY><CENTER>");
       out.println("<BR><H2>Access Error</H2><BR>");
       out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
       out.println("<BR>This site requires the use of Cookies for security purposes.");
       out.println("<BR>We use them to verify your session and prevent unauthorized access.");
       out.println("<BR><BR>Please check your 'Privacy' settings, under 'Tools', 'Internet Options'");
       out.println("<BR>(for MS Internet Explorer).  This must be set to 'Medium High' or lower.");
       out.println("<BR><BR>");
       out.println("<BR>If you have changed or verified the setting above and still receive this message,");
       out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
       out.println("<BR>Provide your name and the name of your club.  Thank you.");
       out.println("<BR><BR>");
       out.println("<a href=\""  + versionId +  "servlet/Logout\" target=\"_top\">Return</a>");
       out.println("</CENTER></BODY></HTML>");
    }

    //
    //  ***** get user id so we know if proshop or member
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)

    Connection con = SystemUtils.getCon(session);            // get DB connection

    String action = req.getParameter(ActionHelper.NEXT_ACTION);

    if (action == null || action.equals(""))
    {
      action = (String)(req.getAttribute(ActionHelper.NEXT_ACTION));
    }

    boolean refresh = false;

    if (action == null || action.equals(""))
    {
      action = "";
      refresh = true;
    }

            //
            //  ***** TEMP **************
            //
            //      1. action empty
            //      2. action = addToList                    
            //
            //
            //String errorMsg = "Edit_distributionlist: User = " +user+ ", Action = " +action+ ".";   // build error msg
            //SystemUtils.logError(errorMsg);                           // log it


    if (action.equals(ActionHelper.ADD_TO_LIST))
    {
      addToList(session, req, resp, con, out);
    }
    else if (action.equals(ActionHelper.REMOVE_FROM_LIST))
    {
      removeFromList(session, req, resp, con, out);
    }
    else if (action.equals(ActionHelper.DELETE))
    {
      deleteList(session, req, resp, con, out);
    }
    else if (action.equals(ActionHelper.UPDATE_AND_RETURN) || action.equals(ActionHelper.UPDATE_AND_CLOSE))
    {
      saveList(session, req, resp, con, out);
    }
    else
    {
      showPage(null, session, req, resp, con, out, refresh);
    }

  }


  private void showPage(FeedBack feedback, HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out, boolean refresh)
    throws IOException
  {

    if (refresh)
    {
      session.removeAttribute(DistributionList.EDIT_DIST_LIST_FRM);
    }

    FormModel form = retrieveFormFromSession(session, req, res, con, out);

    if (!refresh)
    {
      form.update(req, res, out);
    }

    drawBeginningOfPageBeforeForm(feedback, out, req, session);
    FormRenderer.render(form, out);
    drawEndOfPageAfterForm(out);

  }

  private void addToList(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

    FormModel form = retrieveFormFromSession(session, req, res, con, out);


    FeedBack feedback = CommunicationHelper.addToList(form, session, req, res, con, out);


    showPage(feedback, session, req, res, con, out, false);
  }

  private void removeFromList(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

    FormModel form = retrieveFormFromSession(session, req, res, con, out);

    CommunicationHelper.removeFromList(form, session, req, res, con, out);

    showPage(null, session, req, res, con, out, false);

  }

  private void saveList(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {


    //the user has submitted data to add a new list
    DistributionList list = new DistributionList();
    FormModel form = retrieveFormFromSession(session, req, res, con, out);
    form.update(req, res, out);
    String user_name = (String)session.getAttribute("user");
    String original_list_name = req.getParameter(DistributionList.ORIGINAL_LIST_NAME);
    String new_list_name = req.getParameter(DistributionList.LIST_NAME);

    boolean listNameChanged = false;
    if (!(original_list_name.equals(new_list_name)))
    {
      listNameChanged = true;

    }

    FeedBack feedback = CommunicationHelper.validate(req, user_name, form, con, session, listNameChanged);

    if (feedback.isPositive())
    {
      feedback = CommunicationHelper.update(req, form, out, session, con);     // create/save the dist list

      String next_action = req.getParameter(ActionHelper.NEXT_ACTION);
      if (next_action.equals(ActionHelper.UPDATE_AND_CLOSE))
      {
        session.removeAttribute(DistributionList.EDIT_DIST_LIST_FRM);
        String redirectURL = versionId + "servlet/Communication";
        res.sendRedirect(redirectURL);
      }
      else
      {

        showPage(null, session, req, res, con, out, true);

      }
    }
    else
    {
      showPage(feedback, session, req, res, con, out, false);
    }

  }

  private void deleteList(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
  throws IOException
  {

    FormModel form = retrieveFormFromSession(session, req, res, con, out);

    FeedBack feedback = CommunicationHelper.delete(req, form, out, session, con);     // create/save the dist list

    String redirectURL = versionId + "servlet/Communication";
    res.sendRedirect(redirectURL);


  }

  /**
  ***************************************************************************************
  *
  * This method will retrieve the search form from the session if one exists or will build
  * a new one and return it.
  *
  * @param session
  * @param req
  * @param resp
  * @param con
  * @param out
  * @return the form from session or a new one.
  *
  ***************************************************************************************
  **/

  private FormModel retrieveFormFromSession(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {
    
    Object theForm = session.getAttribute(DistributionList.EDIT_DIST_LIST_FRM);

    if ( theForm != null && theForm instanceof FormModel)
    {
      return (FormModel)theForm;
    }
    else
    {
      FormModel form = buildForm(req, res, session, con, out);
      session.setAttribute(DistributionList.EDIT_DIST_LIST_FRM, form);
      return form;
    }
  
  }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that comes before the send email
  * form
  *
  * @param feedback the feedback model that contains any messages to present to the user
  *                 upon loading the page
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private void drawBeginningOfPageBeforeForm(FeedBack feedback, PrintWriter out, HttpServletRequest request, HttpSession session)
  {
    ActionModel pageActions = new ActionModel();
    //Action addListHelp = new Action(ActionHelper.HELP, Labels.HELP);
    //addListHelp.setUrl("javascript:openNewWindow('" + versionId + Help.EDIT_DIST_LIST + "', 'EditDistListOnlineHelp', 'width=250, height=300, directories=no, location=no, menubar=no, scrollbars=yes, status=no, toolbar=no, resizable=yes')");

    //pageActions.add(addListHelp);

    out.println(SystemUtils.HeadTitleAdmin(DistributionList.EDIT_DIST_LIST_HEADER));
    String onLoad = "";

    if (feedback != null)
    {
      if (feedback.getAffectedField() != null && !(feedback.getAffectedField().equals("")))
      {
        onLoad = "javascript:displayFeedbackMessage('" + feedback.get(0) + "');document.pgFrm." + feedback.getAffectedField() + ".focus()";
      }
      else
      {
        onLoad = "javascript:displayFeedbackMessage('" + feedback.get(0) + "')";
      }
    }
    else
    {
      onLoad = "document.pgFrm." + DistributionList.LIST_NAME + ".focus()";

    }

    String onUnLoad = "javascript:cleanup('" + versionId + "servlet/Communication', 'cleanup', 'Any changes you have made since you last saved will be lost.')";


    LayoutHelper.drawBeginPageContentWrapper(onLoad, null, out);

    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
    String caller = (String)session.getAttribute("caller");     // get caller (web site?)

    if (ProcessConstants.isProshopUser(user))
    {
      String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
      int lottery = Integer.parseInt(templott);
      SystemUtils.getProshopSubMenu(request, out, lottery);
    }
    else if (ProcessConstants.isAdminUser(user))
    {
      //fix later
    }
    else
      SystemUtils.getMemberSubMenu(request, out, caller);

    LayoutHelper.drawBeginMainBodyContentWrapper(DistributionList.EDIT_LIST_LABEL, pageActions, out);

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that contains the distribution list form
  *
  * @param req the request that contains information submitted by the user
  * @param resp the response object
  * @param session the session object
  * @param con the database connection
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private FormModel buildForm(HttpServletRequest req, HttpServletResponse resp, HttpSession session, Connection con, PrintWriter out)
  {

    //get the name of the distribution list to edit and query the database for that list
    String dist_list = req.getParameter(DistributionList.LIST_NAME);       // listName

    String user = (String)session.getAttribute("user");
    FormModel form =  null;

    //get the database table based on the the type of user
    String table_name = DistributionList.getTableName(session);

    try {

      PreparedStatement pstmt = con.prepareStatement (
              "SELECT * FROM " + table_name + " WHERE owner = ? AND name = ?");

      pstmt.clearParameters();            // clear the parms
      pstmt.setString(1, user);           // put username in statement
      pstmt.setString(2, dist_list);      // put list name in statement

      ResultSet rs = pstmt.executeQuery();          // execute the prepared stmt

      if (rs.next()) {

        String dist_name = rs.getString("name");

        form =  new FormModel("pgFrm", FormModel.POST, "bot");

        form.setNumColumns(3);
        form.setStyleSheetClass("frm");
        form.addHiddenInput("formId", DistributionList.EDIT_DIST_LIST_FRM);
        form.addHiddenInput(ActionHelper.ACTION, ActionHelper.EDIT);
        form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
        form.addHiddenInput(Member.REQ_USER_NAME, "");
        form.addHiddenInput(ActionHelper.SELECTED_ITEMS_STRING, "");
        form.addHiddenInput(ActionHelper.SEARCH_TYPE, "");
        form.addHiddenInput(DistributionList.ORIGINAL_LIST_NAME,dist_list);

        //Add the help steps for the form
        ArrayList <String> helpSteps = new ArrayList<String>(3);
        helpSteps.add(DistributionList.EDIT_NAME_HELP);
        helpSteps.add(DistributionList.ADD_TO_LIST_HELP);
        helpSteps.add(DistributionList.EDIT_OK_HELP);

        if (ProcessConstants.isProshopUser((String)session.getAttribute("user")))
        {
          helpSteps.add(DistributionList.NOTE_PROSHOP);
        }
        else
        {
          helpSteps.add(DistributionList.NOTE_MEMBER);
        }

        form.setHelpSteps(helpSteps);

        //create the action model for this form and add it to the form model
        ActionModel formActions = new ActionModel();

        String applyUrl = "javascript:update('" + versionId + "servlet/Edit_distributionlist', '" + ActionHelper.UPDATE_AND_RETURN + "')";
        Action applyAction = new Action(ActionHelper.UPDATE_AND_RETURN, Labels.APPLY, "Save the list and return to this page to add another.", applyUrl);
        formActions.add(applyAction);

        String okUrl = "javascript:update('" + versionId + "servlet/Edit_distributionlist', '" + ActionHelper.UPDATE_AND_CLOSE + "')";
        Action okAction = new Action("updateAndClose", Labels.OK, "Save the changes and return to Distribution list menu.", okUrl);
        formActions.add(okAction);

        String cancelUrl = "javascript:cancel('" + versionId + "servlet/Communication', '" +  ActionHelper.CANCEL + "', '" + Labels.WARNING_CHANGES_MAY_BE_LOST + "')";
        Action cancelAction = new Action("cancel", Labels.CANCEL, Labels.RETURN_NO_CHANGES_SAVED, cancelUrl);
        formActions.add(cancelAction);

        form.setActions(formActions);

        Attribute list_name = new Attribute(DistributionList.LIST_NAME, DistributionList.LIST_NAME_LABEL, dist_name, Attribute.EDIT);
        list_name.setSize((new Integer (DistributionList.LIST_NAME_MAX_LENGTH)).toString());

        RowModel name_row = new RowModel();
        name_row.setId(DistributionList.LIST_NAME);
        name_row.add(list_name, "frm", 3);

        form.addRow(name_row);
        form.addSeparator(new Separator());

        RowModel list_row = new RowModel();
        list_row.setId(DistributionList.LIST_OF_NAMES);

        TableModel names = new TableModel(DistributionList.LIST_OF_NAMES_HEADER);
        names.setId("dstLstTbl");
        names.addColumn(new Column("name", "Member"));
        names.addColumn(new Column("actions", ActionHelper.ACTIONS_LABEL));

        //for each name on the distribution list, add it to the table
        int max_list_size = DistributionList.getMaxListSize(session);
        for (int i=0; i<max_list_size;i++)
        {
          String column_name = "user" + (i+1);
          String user_name = rs.getString(column_name);

          if (user_name != null && !(user_name.equals("")))
          {
            RowModel rowName = new RowModel();
            String mem_display_name = MemberHelper.getMemberDisplayName(con, user_name, out);
            rowName.setId(user_name);
            rowName.add(mem_display_name);

            ActionModel actions = new ActionModel();
            String removeUrl = "javascript:removeNameFromList('" + versionId + "servlet/Edit_distributionlist', '" + ActionHelper.REMOVE_FROM_LIST + "', '" + user_name + "')";
            Action removeAction = new Action(ActionHelper.REMOVE, Labels.REMOVE, "Remove this member from the list.", removeUrl);
            actions.add(removeAction);
            rowName.add(actions);
            names.addRow(rowName);
          }
        }

        list_row.add(names);

        ActionModel toActions = new ActionModel();

        String searchMembersUrl = "javascript:openNewWindow('" + versionId + Member.SEARCH_WINDOW_URL + "', '" + Member.SEARCH_WINDOW_NAME + "', '" + Member.SEARCH_WINDOW_PARAMS + "')";
        Action searchMembersAction = new Action(ActionHelper.SEARCH_MEMBERS , Labels.SEARCH_MEMBERS, "Search for members to add", searchMembersUrl);

        if (names.size()<DistributionList.getMaxListSize(session))
        {
          searchMembersAction.setSelected(false);
        }
        else
        {
          searchMembersAction.setSelected(true);
        }

        toActions.add(searchMembersAction);

        //add the search action to the table
        names.setContextActions(toActions);

        form.addRow(list_row);
      }
      pstmt.close();

    }
    catch (Exception exc)
    {
    }

    return form;
  }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that comes after the add list
  * form
  *
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private void drawEndOfPageAfterForm(PrintWriter out)
  {
    LayoutHelper.drawEndMainBodyContentWrapper(out);
    LayoutHelper.drawEndPageContentWrapper(out);
    out.flush();
  }

}
