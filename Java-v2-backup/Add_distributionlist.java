/***************************************************************************************
 *   Add_distributionlist:  This servlet will display a page that allows the user to create a new
 *                          distribution list
 *
 *
 *   created: 1/14/2004   JAG
 *
 *   last updated:  TEST
 *
 *      8/30/13  Fixed distribution list name field so it allows a max length of 60 characters.
 *      5/08/12  Allow access from Dining Admin user.
 *      6/28/11  Burlington CC (burlington) - Display a custom message under Instructions to correctly state that there is a 10 member maximum instead of the default of 30 (case 1998).
 *      6/22/11  Burlington CC (burlington) - Only allow members to have 1 dist list (case 1998).
 *      6/12/11  Olympic Club - do not allow members to build distribution lists.
 *     12/10/09  Druid Hills - only allow members to have 1 dist list (case 1754).
 *      7/18/08  Added limited access proshop users checks
 *      4/24/08  Update ArrayList to use String instead of raw types
 *      4/24/08  Update Connection object to use SystemUtils.getCon()
 *      1/25/04  RDP Change user verification to check here - allow for both members and proshop.
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
import com.foretees.common.getClub;
import com.foretees.common.help.Help;
import com.foretees.communication.CommunicationHelper;
import com.foretees.communication.DistributionList;
import com.foretees.communication.Email;
import com.foretees.event.Event;
import com.foretees.common.Connect;

/**
***************************************************************************************
*
* This servlet will display and process a page to send an email to a selected set of members
*
***************************************************************************************
**/

public class Add_distributionlist extends HttpServlet {

  //initialize the attributes
  private static String versionId = ProcessConstants.CODEBASE;

 static String DINING_USER = ProcessConstants.DINING_USER;               // Dining username for Admin user from Dining System
 

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


    Connection con = Connect.getCon(req);            // get DB connection
    
    boolean isDining = user.equals(DINING_USER);
   
    
    if (isDining == false) {
         
        // Check Feature Access Rights for current proshop user
        if (ProcessConstants.isProshopUser(user)) {
            if (!SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
                SystemUtils.restrictProshop("TOOLS_EMAIL", out);
                return;
            }
        }
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


    if (action.equals(ActionHelper.ADD_TO_LIST))
    {
      addToList(session, req, resp, con, out);
    }
    else if (action.equals(ActionHelper.REMOVE_FROM_LIST))
    {
      removeFromList(session, req, resp, con, out);
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

    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
    String club = (String)session.getAttribute("club");     // get club

    boolean isDining = user.equals(DINING_USER);
   
    if (refresh)
    {
       
      if ((club.equals("dhgc") || club.equals("burlington")) && !user.startsWith("proshop")) {   // if Druid Hills and a member
         
         //
         //  Druid Hills Members can only have 1 dist list
         //
         boolean dhReject = false;
         
         try {

            PreparedStatement pstmt = con.prepareStatement (
                    "SELECT name FROM dist4 WHERE owner = ?");

            pstmt.clearParameters();           
            pstmt.setString(1, user);        

            ResultSet rs = pstmt.executeQuery();          

            if (rs.next()) {         // if dist list already exist - reject
               
               dhReject = true; 
            }
            pstmt.close();

         }
         catch (Exception exc)
         {
         }
               
         if (dhReject == true) {
            
             out.println(SystemUtils.HeadTitle("Limit Reached"));
             out.println("<BODY><CENTER>");
             out.println("<BR><H2>Limit Reached</H2><BR>");
             out.println("<BR><BR>Sorry, your club only allows you to have one distribution list.<BR>");
             out.println("<BR><BR>");
             out.println("<a href=\""  + versionId +  "servlet/Member_announce\">Return</a>");
             out.println("</CENTER></BODY></HTML>");
             return;
         }
      }
       
      if (club.equals("olyclub") && !user.startsWith("proshop")) {   // if Olympic Club and a member
         
          out.println(SystemUtils.HeadTitle("Feature Not Allowed"));
          out.println("<BODY><CENTER>");
          out.println("<BR><H2>Feature Unavailable</H2><BR>");
          out.println("<BR><BR>Sorry, this feature is not available at your club.<BR>");
          out.println("<BR><BR>");
          out.println("<a href=\""  + versionId +  "servlet/Member_announce\">Return</a>");
          out.println("</CENTER></BODY></HTML>");
          return;
      }

      
      session.removeAttribute(DistributionList.ADD_DIST_LIST_FRM);
    }

    FormModel form = retrieveFormFromSession(session, req, res, con, out);

    if (!refresh)
    {
      form.update(req, res, out);
    }

    drawBeginningOfPageBeforeForm(feedback, out, req, session);
    FormRenderer.render(form, out);
    drawEndOfPageAfterForm(out);

    if (isDining) {
        
      // out.println("<BR><BR><p align=\"center\"><a href=\"Proshop_dining_sendEmail\">Done - Return</a></p>");
        
       out.println("<BR><p align=\"center\"><button class=\"btnNorm\" onclick=\"location.href='Proshop_dining_sendEmail'\">Done - Return</button></p>");
    }

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
    String user_name = "";
    FeedBack feedback = CommunicationHelper.validate(req, user_name, form, con, session, true);

    if (feedback.isPositive())
    {
      feedback = CommunicationHelper.persist(req, form, out, session, con);     // create/save the dist list

      req.getSession().removeAttribute(DistributionList.ADD_DIST_LIST_FRM);

      String next_action = req.getParameter(ActionHelper.NEXT_ACTION);
      if (next_action.equals(ActionHelper.UPDATE_AND_CLOSE))
      {
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
    Object theForm = session.getAttribute(DistributionList.ADD_DIST_LIST_FRM);

    if ( theForm != null && theForm instanceof FormModel)
    {
      return (FormModel)theForm;
    }
    else
    {
      FormModel form = buildForm(req, res, session, con, out);
      session.setAttribute(DistributionList.ADD_DIST_LIST_FRM, form);
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
    //addListHelp.setUrl("javascript:openNewWindow('" + versionId + Help.ADD_DIST_LIST + "', 'AddDistListOnlineHelp', 'width=250, height=300, directories=no, location=no, menubar=no, scrollbars=yes, status=no, toolbar=no, resizable=yes')");

    //pageActions.add(addListHelp);

    out.println(SystemUtils.HeadTitleAdmin(DistributionList.ADD_DIST_LIST_HEADER));
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


    LayoutHelper.drawBeginMainBodyContentWrapper(DistributionList.ADD_LIST_LABEL, pageActions, out);

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that contains the email form
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

    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
    String frame_loc = "bot"; 
    
    boolean isDining = user.equals(DINING_USER);
    
    if (isDining == true) frame_loc = "_top";             // no frames if dining user
         
    FormModel form = new FormModel("pgFrm", FormModel.POST, frame_loc);
    form.setNumColumns(3);
    form.setStyleSheetClass("frm");
    form.addHiddenInput("formId", DistributionList.ADD_DIST_LIST_FRM);
    form.addHiddenInput(ActionHelper.ACTION, "");
    form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
    form.addHiddenInput(Member.REQ_USER_NAME, "");
    form.addHiddenInput(ActionHelper.SELECTED_ITEMS_STRING, "");
    form.addHiddenInput(ActionHelper.SEARCH_TYPE, "");

    //Add the help steps for the form
    ArrayList <String> helpSteps = new ArrayList<String>(3);
    helpSteps.add(DistributionList.NAME_HELP);
    helpSteps.add(DistributionList.ADD_TO_LIST_HELP);
    helpSteps.add(DistributionList.ADD_OK_HELP);

    String club = "";

    try {
        club = getClub.getClubName(con);
    } catch (Exception exc) {
        club = "";
    }

    if (ProcessConstants.isProshopUser(user))
    {
      helpSteps.add(DistributionList.NOTE_PROSHOP);
    }
    else
    {
        if (club.equals("burlington")) {
            helpSteps.add(DistributionList.NOTE_MEMBER_BURLINGTON);
        } else {
            helpSteps.add(DistributionList.NOTE_MEMBER);
        }
    }

    form.setHelpSteps(helpSteps);


    //create the action model for this form and add it to the form model
    ActionModel formActions = new ActionModel();

    String applyUrl = "javascript:update('" + versionId + "servlet/Add_distributionlist', '" + ActionHelper.UPDATE_AND_RETURN + "')";
    Action applyAction = new Action(ActionHelper.UPDATE_AND_RETURN, Labels.APPLY, "Save the list and return to this page to add another.", applyUrl);
    formActions.add(applyAction);

    String okUrl = "javascript:update('" + versionId + "servlet/Add_distributionlist', '" + ActionHelper.UPDATE_AND_CLOSE + "')";
    Action okAction = new Action("updateAndClose", Labels.OK, "Save the changes and return to Distribution list menu.", okUrl);
    formActions.add(okAction);

    String cancelUrl = "javascript:cancel('" + versionId + "servlet/Communication', '" +  ActionHelper.CANCEL + "', '" + Labels.WARNING_CHANGES_MAY_BE_LOST + "')";
    Action cancelAction = new Action("cancel", Labels.CANCEL, Labels.RETURN_NO_CHANGES_SAVED, cancelUrl);
    formActions.add(cancelAction);

    form.setActions(formActions);

    Attribute list_name = new Attribute(DistributionList.LIST_NAME, DistributionList.LIST_NAME_LABEL, "", Attribute.EDIT);
    
    if (ProcessConstants.isProshopUser((String)session.getAttribute("user"))) {
       
       list_name.setSize((new Integer (DistributionList.LIST_NAME_MAX_LENGTH_PRO)).toString());
       list_name.setMaxLength((new Integer (DistributionList.LIST_NAME_MAX_LENGTH_PRO)).toString());
   
    } else {
       
       list_name.setSize((new Integer (DistributionList.LIST_NAME_MAX_LENGTH)).toString());
       list_name.setMaxLength((new Integer (DistributionList.LIST_NAME_MAX_LENGTH)).toString());
    }

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

    list_row.add(names);

    ActionModel toActions = new ActionModel();

    String searchMembersUrl = "javascript:openNewWindow('" + versionId + Member.SEARCH_WINDOW_URL + "', '" + Member.SEARCH_WINDOW_NAME + "', '" + Member.SEARCH_WINDOW_PARAMS + "')";
    Action searchMembersAction = new Action(ActionHelper.SEARCH_MEMBERS , Labels.SEARCH_MEMBERS, "Search for members to add", searchMembersUrl);

    toActions.add(searchMembersAction);

    //add the search action to the table
    names.setContextActions(toActions);

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
    LayoutHelper.drawEndMainBodyContentWrapper(out);
    //LayoutHelper.drawFooter(out);
    LayoutHelper.drawEndPageContentWrapper(out);
    out.flush();

  }

}
