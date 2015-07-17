/***************************************************************************************
 *   Member_searchsel:  This servlet will a window that allows the user to search and collect a
 *                      a list of members that can then be sent back to the calling window
 *                      to populate another list.  Generally this should be called to populate
 *                      a popup window
 *
 *
 *   created: 1/14/2004   JAG
 *
 *   last updated:
 *
 *         11/22/13  Do not include members in member lists if they have opted to NOT receive emails from club staff.
 *          9/04/09  Minor code clean up - trying to track down errors thrown in getUsersByMemberTypeMembership
 *          4/24/09  Treesdale Golf - Don't display Ben Roethlisberger's email in member selection window, adding to dist. lists, 
 *                   and don't import from partner list for members only (case 1660).
 *          2/10/09  Updated getUsersByMemberTypeMemberShip to not pull back inactive members
 *          4/24/08  Update ArrayList to use String instead of raw types
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
import com.foretees.client.action.Action;
import com.foretees.client.action.ActionModel;
import com.foretees.client.action.ActionHelper;
import com.foretees.client.attribute.Attribute;
import com.foretees.client.attribute.SelectionList;
import com.foretees.client.form.FormModel;
import com.foretees.client.form.FormRenderer;
import com.foretees.client.layout.LayoutModel;
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
*   String searchMembersUrl = "javascript:openNewWindow('" + versionId + Member.SEARCH_WINDOW_URL + "', '" + Member.SEARCH_WINDOW_NAME + "', '" + Member.SEARCH_WINDOW_PARAMS + "')";
*   Action searchMembersAction = new Action(ActionHelper.SEARCH_MEMBERS , Labels.SEARCH_MEMBERS, "Search for members to add", searchMembersUrl);
*
* and also have a hidden input field in your form named using ActionHelper.SELECTED_ITEMS_STRING
* and a hidden input field named using ActionHelper.SEARCH_TYPE
*
***************************************************************************************
**/

public class Member_searchsel extends HttpServlet {

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
       out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
    }

    //
    //  ***** get user id so we know if proshop or member
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)


    Connection con = SystemUtils.getCon(session);            // get DB connection

    if (con == null) {

       SystemUtils.logError("Error in Member_searchsel.doGet: con was returned null from SystemUtils.getCon()");

       out.println(SystemUtils.HeadTitle("DB Connection Error"));
       out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +versionId+ "/images/foretees.gif\"><BR>");
       out.println("<hr width=\"40%\">");
       out.println("<BR><BR><H3>Database Connection Error</H3>");
       out.println("<BR><BR>Unable to connect to the Database.");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact your club manager.");
       out.println("<BR><BR>");
       out.println("<a href=\"javascript:history.back(1)\">Return</a>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
    }

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

    if (action.equals(ActionHelper.SEARCH_MEMBERS))
    {
      searchMembers(session, req, resp, con, out);
    }
    else if (action.equals(ActionHelper.ADD_TO_LIST))
    {
      addToList(session, req, resp, con, out);
    }
    else if (action.equals(ActionHelper.REMOVE_FROM_LIST))
    {
      removeFromList(session, req, resp, con, out);
    }
    else
    {
        showPage(session, req, resp, con, out, refresh);
    }

  }

  private void showPage(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out, boolean refresh)
    throws IOException
  {

    if (refresh)
    {
      session.removeAttribute(Member.SEARCH_MEM_FRM);
    }

    FormModel form = retrieveFormFromSession(session, req, res, con, out);

    if (!refresh)
    {
      form.update(req, res, out);
    }

    out.println(SystemUtils.HeadTitleAdmin(Member.SEARCH_MEMBERS_LABEL));
    FormRenderer.render(form, out);

  }

  private void removeFromList(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

    FormModel form = retrieveFormFromSession(session, req, res, con, out);

    //the user has submitted a name to add to the list
    String name_to_remove = req.getParameter(Member.REQ_USER_NAME);

    if (name_to_remove != null && !(name_to_remove.equals("")))
    {

      //get the table from the form and add the name in the list
      RowModel row = form.getRow(Member.LIST_OF_NAMES);
      TableModel names = (TableModel)(((Cell)row.get(0)).getContent());

      names.remove(name_to_remove);
    }

    showPage(session, req, res, con, out, false);

  }

  private void searchMembers(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {
      
      // Get the club and user name from session first
      String user = (String)session.getAttribute("user");
      String club = (String)session.getAttribute("club");          
      
      boolean isPro = ProcessConstants.isProshopUser(user);

      FormModel form = retrieveFormFromSession(session, req, res, con, out);
      
      //need to get the letter that the user clicked and refresh the form with
      //member names from the database
      String letter = req.getParameter(Member.LETTER);
      
      RowModel row = form.getRow(Member.LETTER_CHOOSER_ROW);
      Cell cell = row.getCell(Member.NAME_SELECTOR_CELL_ID);
      
      try
      {
          SelectionList theNames = MemberHelper.queryMembersWithEmailAddresses(con, club, letter, out, isPro);
          Object content = cell.getContent();
          
          if (content instanceof NameSelector)
          {
              ((NameSelector)content).setSelectionList(theNames);
          }
      }
      catch (SQLException sqle)
      {
          //what to do
      }
      
      showPage(session, req, res, con, out, false);

  }

  private void addToList(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
  throws IOException
  {

    FormModel form = retrieveFormFromSession(session, req, res, con, out);
    //get the table from the form and add the name in the list
    RowModel row = form.getRow(Member.LIST_OF_NAMES);
    TableModel names = (TableModel)(((Cell)row.get(0)).getContent());

    String searchType = req.getParameter(ActionHelper.SEARCH_TYPE);

    if (searchType != null && !(searchType.equals("")))
    {
      if (searchType.equals(ActionHelper.SEARCH_BY_MEMTYPE_MEMSHIP_TYPE))
      {
        ArrayList <String> user_names = getUsersByMemberTypeMemberShip(session, req, res, con, out);

        if (user_names != null)
        {
          for (int i=0; i<user_names.size(); i++)
          {
            addNewRow((String)user_names.get(i), names, form, con, out);
          }
        }
      }
    }
    else
    {
      //user must have selected a name from the selection list to add
      String name_to_add = req.getParameter(Member.SELECTION_LIST_OF_NAMES);

      if (name_to_add != null && !(name_to_add.equals("")))
      {
        addNewRow(name_to_add, names, form, con, out);
      }
    }

    showPage(session, req, res, con, out, false);

  }

  private void addNewRow(String name_to_add, TableModel names, FormModel form, Connection con, PrintWriter out)
    throws IOException
  {

     //check to see if this name is already in the list
    RowModel nameRow = names.getRow(name_to_add);

    if (nameRow == null)
    {
      try
      {
        nameRow = new RowModel();
        nameRow.setId(name_to_add);
        String displayName = MemberHelper.getMemberDisplayName(con, name_to_add, out);
        nameRow.add(displayName);
        ActionModel actions = new ActionModel();
        String removeUrl = "javascript:removeNameFromList('Member_searchsel', '" + ActionHelper.REMOVE_FROM_LIST + "', '" + name_to_add + "')";
        Action removeAction = new Action(ActionHelper.REMOVE, Labels.REMOVE, "Remove this member from the list.", removeUrl);

        actions.add(removeAction);
        nameRow.add(actions);
        names.addRow(nameRow);
      }
      catch (SQLException sqle)
      {
        //what to do
      }
    }

  }

  private ArrayList<String> getUsersByMemberTypeMemberShip(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {
    String mem_type = req.getParameter(Member.MEM_TYPE);
    String memship_type = req.getParameter(Member.MEMSHIP_TYPE);

    String select_stmt = "SELECT username FROM member2b WHERE ";
    boolean memShipTypeSelected = false;
    boolean memTypeSelected = false;

    if (mem_type != null && !(mem_type.equals("")))
    {
      memTypeSelected = true;
      select_stmt = select_stmt + "m_type = '" + mem_type + "'";
    }

    if (memship_type != null && !(memship_type.equals("")))
    {
      memShipTypeSelected = true;

      if (memTypeSelected)
      {
        select_stmt = select_stmt + " AND ";
      }

      select_stmt = select_stmt + "m_ship = '" + memship_type + "'";
    }

    if (!(memShipTypeSelected || memTypeSelected))
    {
      //return nothing
      return null;
    }
    else
    {
      select_stmt += " AND inact = 0 AND billable = 1 AND "
                  + "((email_bounced = 0 AND email <> '' && clubEmailOpt1 > 0) || (email2_bounced = 0 AND email2 <> '' && clubEmailOpt2 > 0))  "
                  + "ORDER BY name_last, name_first, name_mi";
    }

    ArrayList <String> names = new ArrayList<String>();

    Statement stmt = null;
    ResultSet rs = null;

    try {                            // Get all columns from member table for names requested

      stmt = con.createStatement();
      rs = stmt.executeQuery(select_stmt);

      while ( rs.next() ) {

        names.add(rs.getString("username"));

      }

    } catch (Exception exc) {

      exc.printStackTrace();

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

    return names;

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

    String onUnLoad = "javascript:cleanup('Communication', 'cleanup', 'Any changes you have made since you last saved will be lost.')";


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
    form.addHiddenInput(ActionHelper.SEARCH_TYPE, "");

    //Add the help steps for the form
    ArrayList <String> helpSteps = new ArrayList<String>(3);

    if (ProcessConstants.isProshopUser((String)session.getAttribute("user") ))
    {
      helpSteps.add(Member.SEARCH_MEMSHIP_MEM_TYPE_HELP);
    }
    helpSteps.add(Member.SEARCH_LETTER_HELP);
    helpSteps.add(Member.SEARCH_OK_HELP);

    form.setHelpSteps(helpSteps);


    //
    //  ***** get user id so we know if proshop or member
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)


    //create the action model for this form and add it to the form model
    ActionModel formActions = new ActionModel();

    String okUrl = "javascript:returnSelectedItems('"  + Member.SELECTED_MEMBERS + "', '" + ActionHelper.SEARCH_MEMBERS + "')";
    Action okAction = new Action("updateAndClose", "OK", "Return selected items.", okUrl);
    formActions.add(okAction);

    String cancelUrl = "javascript:window.close()";
    Action cancelAction = new Action("cancel", Labels.CANCEL, Labels.RETURN_NO_CHANGES_SAVED, cancelUrl);
    formActions.add(cancelAction);

    form.setActions(formActions);

    if (user.startsWith( "proshop" )) {

       try
       {

         LayoutModel layout = new LayoutModel();
         layout.setId("memTypeMemShipRow");
         layout.setNumColumns(form.getNumColumns());

         RowModel memTypeMemShipRow = new RowModel();

         //add the selector for the membership types
         SelectionList memberTypes = MemberHelper.getMemberTypes(con, null, null);
         memberTypes.setAlwaysShowAsList(true);
         memberTypes.setRequired(false);

         memTypeMemShipRow.add(memberTypes, "frm");

         //add the selector for the member types
         SelectionList memberShips = MemberHelper.getMemberShips(con, null, null);
         memberShips.setAlwaysShowAsList(true);
         memberShips.setRequired(false);

         memTypeMemShipRow.add(memberShips, "frm");

         ActionModel memShipTypeActions = new ActionModel();

         String searchMemShipTypesUrl = "javascript:execute('Member_searchsel" + "', '" + ActionHelper.ADD_TO_LIST + "', '" + ActionHelper.SEARCH_BY_MEMTYPE_MEMSHIP_TYPE + "')";
         Action searchMemShipTypesAction = new Action(ActionHelper.SEARCH_BY_MEMTYPE_MEMSHIP_TYPE, Labels.SEARCH, "Search for members by membership type and/or member type.", searchMemShipTypesUrl);

         memShipTypeActions.add(searchMemShipTypesAction);
         Cell actions = new Cell(memShipTypeActions);
         actions.setStyleSheetClass("frm");
         memTypeMemShipRow.add(actions);


         layout.addRow(memTypeMemShipRow);
         form.addRow(layout);
       }
       catch (Exception exc)
       {
         //problems getting membership types or member types.
       }
    }

    RowModel namechooser_row = new RowModel();
    namechooser_row.setId(Member.LETTER_CHOOSER_ROW);

    String searchUrl = "Member_searchsel";
    NameSelector name_sel = new NameSelector(Member.LIST_OF_NAMES_TO_SELECT_FROM, searchUrl, null);
    name_sel.setNextAction(ActionHelper.SEARCH_MEMBERS);
    name_sel.setCurrentAction(ActionHelper.SEARCH);
    name_sel.setOnClick("addNameToList('" + searchUrl + "', '" + ActionHelper.ADD_TO_LIST + "');");

    namechooser_row.add(Member.NAME_SELECTOR_CELL_ID, name_sel);

    form.addRow(namechooser_row);

    form.addSeparator(new Separator());

    RowModel list_row = new RowModel();
    list_row.setId(Member.LIST_OF_NAMES);

    TableModel names = new TableModel(Member.MEMBER_LIST_LABEL);
    names.setId(Member.SELECTED_MEMBERS);
    names.addColumn(new Column("name", "Member"));
    names.addColumn(new Column("actions", ActionHelper.ACTIONS_LABEL));

    list_row.add(names);

    form.addRow(list_row);

    return form;
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
    Object theForm = session.getAttribute(Member.SEARCH_MEM_FRM);

    if ( theForm != null && theForm instanceof FormModel)
    {
      return (FormModel)theForm;
    }
    else
    {
      FormModel form = buildForm(session, req, res, con, out);
      session.setAttribute(Member.SEARCH_MEM_FRM, form);
      return form;
    }
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
