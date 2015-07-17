/***************************************************************************************
 *   Communication:  This servlet will display the main page that allows user set up communication
 *                   channels between other members and the propshop
 *
 *
 *   created: 1/14/2004   JAG
 *
 *   last updated:
 *
 *          7/18/08  Added limited access proshop users checks
 *          4/24/08  Update ArrayList to use String instead of raw types
 *          1/25/04  Add processing to query the distribution lists (dist4 table).
 *                   Also, get the username from the session (proshop vs. member).
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

import com.foretees.client.ScriptHelper;
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
* This servlet will process the display the main portal page for allowing members to communicate
* with other members and the proshop
*
***************************************************************************************
**/

public class Communication extends HttpServlet {

  //initialize the attributes
  private static String versionId = ProcessConstants.CODEBASE;

  /**
  ***************************************************************************************
  *
  * This method will process the data from the multi edit member screen
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

    // If proshop, check Feature Access Rights for current proshop user
    if (ProcessConstants.isProshopUser(user)) {
        if (!SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
            SystemUtils.restrictProshop("TOOLS_EMAIL", out);
            return;
        }
    }
    
    showDistLists(session, req, con, out);

  }

  private void showDistLists(HttpSession session, HttpServletRequest req, Connection con, PrintWriter out)
  {


    TableModel lists = new TableModel("Email Distribution Lists");
    lists.addColumn(new Column("list_name", "Distribution List Name"));
    lists.addColumn(new Column("actions", ActionHelper.ACTIONS_LABEL));

    String table_name = DistributionList.getTableName(session);

    //
    //  Get this user's distribution lists, if any, and list them by name
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)

    try {

       PreparedStatement pstmt = con.prepareStatement (
                "SELECT name FROM " + table_name + " WHERE owner = ? ORDER BY name");

       pstmt.clearParameters();            // clear the parms
       pstmt.setString(1, user);           // put username in statement
       ResultSet rs = pstmt.executeQuery();          // execute the prepared stmt

       while (rs.next()) {

          RowModel row = new RowModel();

          String list_name = rs.getString("name");
          row.add(list_name);


          //create the action model for this event and add it to the row
          ActionModel rowActions = new ActionModel();

          //escape special characters in the distribution list name
          String escName = ScriptHelper.escapeSpecialCharacters(list_name);

//          String editUrl = "javascript:viewList('" + versionId + "servlet/Edit_distributionlist', '" + escName + "', 'edit')";
          String editUrl = "javascript:viewList('" + versionId + "servlet/Edit_distributionlist', '" + escName + "', '')";
          Action editAction = new Action("edit", Labels.EDIT, "Edit this distribution list.", editUrl);
          rowActions.add(editAction);

          String deleteUrl = "javascript:deleteList('" + versionId + "servlet/Edit_distributionlist', '" + escName + "','delete')";
          Action deleteAction = new Action("delete", Labels.DELETE, "Delete this distribution list from the database.", deleteUrl);
          rowActions.add(deleteAction);
          row.add(rowActions);

          lists.addRow(row);
       }

       pstmt.close();

    }
    catch (Exception exc) {             // SQL Error

       out.println(SystemUtils.HeadTitle("DB Access Error"));
       out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
       out.println("<BR><BR><H2>Database Access Error 1</H2>");
       out.println("<BR><BR>Unable to process database change at this time.");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact your club manager or ForeTees support.");
       out.println("<BR><BR>");
       if (user.startsWith( "proshop" )) {
          out.println("<a href=\"" +versionId+ "servlet/Proshop_announce\">Return</a>");
       } else {
          out.println("<a href=\"" +versionId+ "servlet/Member_announce\">Return</a>");
       }
       out.println("</CENTER></BODY></HTML>");
       return;
    }


    ActionModel pageActions = new ActionModel();
    //Action addList = new Action("addList", DistributionList.ADD_LIST_LABEL);
    //addList.setUrl("javascript:addList('" + versionId + "servlet/Add_distributionlist', '')");
    //pageActions.add(addList);

    //Action listHelp = new Action(ActionHelper.HELP, Help.LABEL);
    //listHelp.setUrl("javascript:openNewWindow('" + versionId + Help.VIEW_DIST_LIST + "', 'DistListOnlineHelp', '" + Help.WINDOW_SIZE + "')");

    //pageActions.add(listHelp);

    out.println(SystemUtils.HeadTitleAdmin(DistributionList.COMMUNICATION_HEADER));
    LayoutHelper.drawBeginPageContentWrapper(null, null, out);
    //ActionHelper.drawDistListNavBar(ActionHelper.VIEW_DIST_LIST, out);

    String caller = (String)session.getAttribute("caller");     // get caller (web site?)

    if (ProcessConstants.isProshopUser(user))
    {
      String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
      int lottery = Integer.parseInt(templott);
      SystemUtils.getProshopSubMenu(req, out, lottery);
    }
    else if (ProcessConstants.isAdminUser(user))
    {
      //fix later
    }
    else
      SystemUtils.getMemberSubMenu(req, out, caller);


    //we need a form to submit the actions in the table
    FormModel form = new FormModel("pgFrm", FormModel.POST, "bot");
    form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
    form.addHiddenInput(ActionHelper.ACTION, "");
    form.addHiddenInput(DistributionList.LIST_NAME, "");

    //Add the help steps for the form
    ArrayList <String> helpSteps = new ArrayList<String>(2);
    helpSteps.add(DistributionList.EDIT_LIST_HELP);
    helpSteps.add(DistributionList.DELETE_LIST_HELP);

    form.setHelpSteps(helpSteps);

    form.setStyleSheetClass("frmWrp");
    RowModel tableRow = new RowModel();
    tableRow.add(lists, "frm", 1);
    form.addRow(tableRow);

    FormRenderer.render(form, out);
    LayoutHelper.drawEndPageContentWrapper(out);

    out.flush();

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

  private void drawBeginningOfPageBeforeAddForm(FeedBack feedback, PrintWriter out, HttpServletRequest request, HttpSession session)
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


    LayoutHelper.drawBeginMainBodyContentWrapper(DistributionList.ADD_LIST_LABEL, pageActions, out);

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
    //LayoutHelper.drawFooter(out);
    LayoutHelper.drawEndPageContentWrapper(out);
    out.flush();
  }

}
