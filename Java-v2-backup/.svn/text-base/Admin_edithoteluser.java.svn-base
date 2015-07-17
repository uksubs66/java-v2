/***************************************************************************************
 *   Admin_edithoteluser:  This servlet will process the 'edit hotel user' request
 *
 *
 *   created: 11/22/2003   JAG
 *
 *   last updated:
 *
 *      4/21/10  Changes to add support for unlimited guest types
 *      4/24/08  Update Connection object to use SystemUtils.getCon()
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

public class Admin_edithoteluser extends HttpServlet {

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

     FormModel form = null;

     Object theForm = req.getSession().getAttribute(Member.EDIT_HOTEL_USER_FRM);

     if (theForm != null && theForm instanceof FormModel)
     {
       form = (FormModel)theForm;
     }


     if (form == null){
       try {

          PreparedStatement stmt = con.prepareStatement (
                   "SELECT * FROM hotel3 WHERE username = ?");

          stmt.clearParameters();        // clear the parms
          stmt.setString(1, user);       // put the parm in stmt
          rs = stmt.executeQuery();      // execute the prepared stmt

          if (!rs.next()) {

             noMem(out);    // member does not exist - inform the user and return
             return;
          }

          form = buildNewForm(req, resp, session, con, rs, out);

          //add the form to the session to retrieve later
          req.getSession().setAttribute(Member.EDIT_HOTEL_USER_FRM, form);

          stmt.close();              // close the stmt

       }
       catch (Exception exc) {

          dbError(out, exc);
          return;
       }
     }

    FeedBack feedback = null;

    Object theFeedback = session.getAttribute(Member.HOTEL_USER_FEEDBACK);

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
  * This method will draw the the portion of the page that comes before the edit hotel user
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
    addHtUserHelp.setUrl("javascript:openNewWindow('" + versionId + Help.EDIT_HOTEL_USER + "', 'EditHotelUserOnlineHelp', '" + Help.WINDOW_SIZE + "')");

    pageActions.add(addHtUserHelp);

    out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Edit Hotel User Main Page"));
    String onLoad = "";

    if (feedback != null)
    {
      onLoad = "javascript:displayFeedbackMessage('" + feedback.get(0) + "');document.pgFrm." + feedback.getAffectedField() + ".focus()";
    }
    else
    {
      onLoad = "document.pgFrm." + Member.FIRST_NAME + ".focus()";
    }

    String onUnLoad = "javascript:cleanup('" + versionId + "servlet/Admin_updatehoteluser', 'cleanup', 'Any changes you have made since you last saved will be lost.')";

    LayoutHelper.drawBeginPageContentWrapper(onLoad, onUnLoad, out);
    ActionHelper.drawAdminHotelUserNavBar(null, out);
    LayoutHelper.drawBeginMainBodyContentWrapper("Edit hotel user", pageActions, out);

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that comes after the edit hotel user
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
  * This method will build a new form model for editing an existing hotel user
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
    form.addHiddenInput("formId", member.EDIT_HOTEL_USER_FRM);
    form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
    String username = rs.getString("username");             // get the hotel user name
    form.addHiddenInput(Member.REQ_USER_NAME, username);

    //create the action model for this form and add it to the form model
    ActionModel formActions = new ActionModel();


    String applyUrl = "javascript:update('" + versionId + "servlet/Admin_updatehoteluser', 'updateAndReturn')";
    Action applyAction = new Action("updateAndReturn", Labels.APPLY, "Save the changes and return to this page to continue editing.", applyUrl);
    formActions.add(applyAction);

    String okUrl = "javascript:update('" + versionId + "servlet/Admin_updatehoteluser', 'updateAndClose')";
    Action okAction = new Action("updateAndClose", Labels.OK, "Save the changes and return to hotel users list.", okUrl);
    formActions.add(okAction);

    String cancelUrl = "javascript:cancel('" + versionId + "servlet/Admin_updatehoteluser', 'cancel', '" + Labels.WARNING_CHANGES_MAY_BE_LOST + "')";
    Action cancelAction = new Action("cancel", Labels.CANCEL, Labels.RETURN_NO_CHANGES_SAVED, cancelUrl);
    formActions.add(cancelAction);

    form.setActions(formActions);

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

    RowModel loginRow = new RowModel();
    nameRow.setId("loginRow");

    Attribute userName = new Attribute(member.USER_NAME, member.USER_NAME_LABEL, username, Attribute.EDIT);
    userName.setSize("15");
    userName.setMaxLength("15");
    Attribute password = new Attribute(member.PASSWORD, member.PASSWORD_LABEL, rs.getString("password"), Attribute.EDIT);
    password.setSize("10");
    password.setMaxLength("10");

    loginRow.add(userName, "frm");
    loginRow.add(password, "frm");

    form.addRow(loginRow);

    //Add a separator
    form.addSeparator(new Separator());

    //need to add the guest types row to the form

    RowModel guestTypeLabelRow = new RowModel();
    guestTypeLabelRow.setId("guestTypeLabelRow");
    guestTypeLabelRow.add("Select Guest Types for this user", "frm", 4);
    form.addRow(guestTypeLabelRow);


    try {

       String user = req.getParameter(Member.REQ_USER_NAME);

       PreparedStatement stmt = con.prepareStatement (
               "SELECT * FROM hotel3_gtypes WHERE username = ?");

       stmt.clearParameters();        // clear the parms
       stmt.setString(1, user);       // put the parm in stmt
       ResultSet rs2 = stmt.executeQuery();      // execute the prepared stmt

       //Get the guest types for this club
       String[] types = getSelectedGuestTypes(rs2);

       //guestTypes = MemberHelper.getGuestTypes(con, types);
       MemberHelper.addGuestTypesToForm(con, types, form);

    }
    catch (Exception e1) {

       throw new Exception(e1);
    }


    //Add a separator
    form.addSeparator(new Separator());

    RowModel daysInAdvance = new RowModel();
    daysInAdvance.setId("guestTypeLabelRow");
    daysInAdvance.add("Select days in advance this user can reserve tee times", "frm", 4);
    form.addRow(daysInAdvance);



    String[] days = new String[90];
    for (int j=0;j<90;j++)
    {
    days[j] = (new Integer(j+1)).toString();
    }

    LayoutModel daysInAdLayout = new LayoutModel();
    daysInAdLayout.setId("daysInAdLayout");
    daysInAdLayout.setNumColumns(form.getNumColumns());

    RowModel daysRow1 = new RowModel();
    String d1 = (new Integer(rs.getInt("days1"))).toString();
    Attribute day1 = new Attribute("day1", Labels.SUNDAY, d1, Attribute.EDIT);
    day1.setMaxLength("3");
    day1.setSize("3");
    daysRow1.add(day1,"frm");
    String d2 = (new Integer(rs.getInt("days2"))).toString();
    Attribute day2 = new Attribute("day2", Labels.MONDAY, d2, Attribute.EDIT);
    day2.setMaxLength("3");
    day2.setSize("3");
    daysRow1.add(day2, "frm");
    String d3 = (new Integer(rs.getInt("days3"))).toString();
    Attribute day3 = new Attribute("day3", Labels.TUESDAY, d3, Attribute.EDIT);
    day3.setMaxLength("3");
    day3.setSize("3");
    daysRow1.add(day3, "frm");
    String d4 = (new Integer(rs.getInt("days4"))).toString();
    Attribute day4 = new Attribute("day4", Labels.WEDNESDAY, d4, Attribute.EDIT);
    day4.setMaxLength("3");
    day4.setSize("3");
    daysRow1.add(day4, "frm");
    RowModel daysRow2 = new RowModel();
    String d5 = (new Integer(rs.getInt("days5"))).toString();
    Attribute day5 = new Attribute("day5", Labels.THURSDAY, d5, Attribute.EDIT);
    day5.setMaxLength("3");
    day5.setSize("3");
    daysRow2.add(day5, "frm");
    String d6 = (new Integer(rs.getInt("days6"))).toString();
    Attribute day6 = new Attribute("day6", Labels.FRIDAY, d6, Attribute.EDIT);
    day6.setMaxLength("3");
    day6.setSize("3");
    daysRow2.add(day6, "frm");
    String d7 = (new Integer(rs.getInt("days7"))).toString();
    Attribute day7 = new Attribute("day7", Labels.SATURDAY, d7, Attribute.EDIT);
    day7.setMaxLength("3");
    day7.setSize("3");
    daysRow2.add(day7, "frm");


    daysInAdLayout.addRow(daysRow1);
    daysInAdLayout.addRow(daysRow2);

    form.addRow(daysInAdLayout);

    return form;
  }

  /**
  ***************************************************************************************
  *
  * This method will get all the selected guest types for this user and put them into
  * a string array
  *
  * @param rs the results set that contains the user to edit
  * @return the string array containing the guest types selected for this hotel user
  *
  ***************************************************************************************
  **/

  private String[] getSelectedGuestTypes(ResultSet rs) throws SQLException
  {
/*
    String selectedGuestTypes[] = new String[MemberHelper.NUM_HOTEL_GUEST_TYPES];

    int colEntryStart = 12;            // start in rs + 12 (13th column - first guest type)

    for (int i=0; i<selectedGuestTypes.length; i++)
    {

        selectedGuestTypes[i] = rs.getString(i+colEntryStart);

    }

    return selectedGuestTypes;
*/

    rs.last();
    int i = 0;
    String selectedGuestTypes[] = new String[rs.getRow()];

    rs.beforeFirst();

    while ( rs.next() ) {

        selectedGuestTypes[i] = rs.getString("guest_type");
        i++;

    }

    return selectedGuestTypes;

  }

}
