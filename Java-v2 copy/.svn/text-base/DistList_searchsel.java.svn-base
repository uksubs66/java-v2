/***************************************************************************************
 *   DistList_searchsel:  This servlet will a window that allows the user to select one or more
 *                      distribution lists to send an email
 *
 *
 *   created: 1/14/2004   JAG
 *
 *   last updated:
 *
 *          4/24/08  Update ArrayList to use String instead of raw types
 *          4/24/08  Update Connection object to use SystemUtils.getCon()
 *          1/24/04  RDP  Add processing to query the distribution lists (dist4 table).
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

/**
***************************************************************************************
*
* This servlet will display and process a page to allow a user to search and select a set of
* members for a specific purpose.
*
* To use this you will need to include an action like the following on your page
*
*    String searchListsUrl = "javascript:openNewWindow('" + versionId + DistributionList.SEARCH_WINDOW_URL + "', '" + DistributionList.SEARCH_WINDOW_NAME + "', '" + DistributionList.SEARCH_WINDOW_PARAMS + "')";
*    Action searchListsAction = new Action(ActionHelper.SEARCH_LISTS, Labels.SEARCH_DIST_LISTS, "Search for distribution lists to add", searchListsUrl);
*
* and also have a hidden input field in your form named using ActionHelper.SELECTED_ITEMS_STRING
* and a hidden input field named using ActionHelper.SEARCH_TYPE
*
***************************************************************************************
**/

public class DistList_searchsel extends HttpServlet {

  //initialize the attributes
  private static String versionId = ProcessConstants.CODEBASE;

  /**
  ***************************************************************************************
  *
  * This method will process the data from the request
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


    Connection con = SystemUtils.getCon(session);           // get DB connection

    showPage(session, req, resp, con, out, true);

  }

  private void showPage(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out, boolean refresh)
    throws IOException
  {


    FormModel form = buildForm(session, req, res, con,out);
    out.println(SystemUtils.HeadTitleAdmin(DistributionList.SEARCH_LISTS_LABEL));
    FormRenderer.render(form, out);

  }


  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that comes before the add list
  * form
  *
  * @param feedback the feedback model that contains any messages to present to the user
  *                 upon loading the page
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private void drawBeginningOfPageBeforeAddForm(FeedBack feedback, PrintWriter out)
  {
    ActionModel pageActions = new ActionModel();
    Action addListHelp = new Action(ActionHelper.HELP, Labels.HELP);
    addListHelp.setUrl("javascript:openNewWindow('" + versionId + Help.ADD_DIST_LIST + "', 'AddDistListOnlineHelp', 'width=250, height=300, directories=no, location=no, menubar=no, scrollbars=yes, status=no, toolbar=no, resizable=yes')");

    pageActions.add(addListHelp);

    out.println(SystemUtils.HeadTitleAdmin(DistributionList.ADD_DIST_LIST_HEADER));
    String onLoad = "";

    if (feedback != null)
    {
      onLoad = "javascript:displayFeedbackMessage('" + feedback.get(0) + "');document.pgFrm." + feedback.getAffectedField() + ".focus()";
    }
    else
    {
      onLoad = "document.pgFrm." + DistributionList.LIST_NAME + ".focus()";

    }

    String onUnLoad = "javascript:cleanup('" + versionId + "servlet/Communication', 'cleanup', 'Any changes you have made since you last saved will be lost.')";


    LayoutHelper.drawBeginPageContentWrapper(onLoad, onUnLoad, out);
    ActionHelper.drawDistListNavBar(null, out);
    LayoutHelper.drawBeginMainBodyContentWrapper(DistributionList.ADD_LIST_LABEL, pageActions, out);

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that contains the form
  *
  * @param req the request that contains information submitted by the user
  * @param resp the response object
  * @param session the session object
  * @param con the database connection
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private FormModel buildForm(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

    FormModel form = new FormModel("pgFrm", FormModel.POST, null);
    form.setNumColumns(3);
    form.addHiddenInput("formId", Member.SEARCH_MEM_FRM);
    form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
    form.addHiddenInput(Member.REQ_USER_NAME, "");

    //Add the help steps for the form
    ArrayList <String>helpSteps = new ArrayList<String>(3);
    helpSteps.add(DistributionList.CHECKBOX_HELP);
    helpSteps.add(DistributionList.OK_HELP);

    form.setHelpSteps(helpSteps);


    //create the action model for this form and add it to the form model
    ActionModel formActions = new ActionModel();

    String okUrl = "javascript:returnCheckedItems('"  + DistributionList.LIST_TABLE + "', '" + ActionHelper.SEARCH_LISTS + "')";
    Action okAction = new Action("updateAndClose", "OK", "Return selected items.", okUrl);
    formActions.add(okAction);

    String cancelUrl = "javascript:window.close()";
    Action cancelAction = new Action("cancel", Labels.CANCEL, Labels.RETURN_NO_CHANGES_SAVED, cancelUrl);
    formActions.add(cancelAction);

    form.setActions(formActions);

    RowModel list_row = new RowModel();
    list_row.setId(Member.LIST_OF_NAMES);

    TableModel names = new TableModel(DistributionList.SEARCH_LISTS_LABEL);
    names.setId(DistributionList.LIST_TABLE);
    names.setSelectable(true);
    names.addColumn(new Column("name", "Name"));


    //
    //  Get this user's distribution lists, if any, and list them by name
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
    String table_name = DistributionList.getTableName(session);

    try {

       PreparedStatement pstmt = con.prepareStatement (
                "SELECT name FROM " + table_name + " WHERE owner = ? ORDER BY name");

       pstmt.clearParameters();            // clear the parms
       pstmt.setString(1, user);           // put username in statement
       ResultSet rs = pstmt.executeQuery();          // execute the prepared stmt

       while (rs.next()) {

          RowModel rowName = new RowModel();
          String dist_name = rs.getString("name");
          rowName.setId(dist_name);
          rowName.add(dist_name);
          names.addRow(rowName);
       }

    }
    catch (Exception exc) {             // SQL Error
    }

    list_row.add(names);

    form.addRow(list_row);

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
    out.flush();

  }

}
