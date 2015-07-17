/***************************************************************************************
 *   Admin_addhoteluser:  This servlet will process the 'add hotel user' request  *
 *
 *   created: 11/22/2003   JAG
 *
 *   last updated:
 *
 *      4/21/10  Changes to add support for unlimited guest types
 *      4/24/08  Update Connection object to use SystemUtils.getCon()
 *      3/10/04  Changed the "Days in Advance" to be text input fields instead
 *               of dropdowns to allow for 365 days.
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
import com.foretees.client.attribute.SelectionList;

import com.foretees.client.form.FormModel;
import com.foretees.client.form.FormRenderer;

import com.foretees.client.layout.LayoutHelper;
import com.foretees.client.layout.LayoutModel;
import com.foretees.client.layout.Separator;

import com.foretees.client.table.RowModel;

import com.foretees.common.FeedBack;
import com.foretees.common.Labels;
import com.foretees.common.parmClub;
import com.foretees.common.ProcessConstants;

import com.foretees.common.help.Help;

import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.common.Connect;

/**
***************************************************************************************
*
* This servlet will draw the form for adding a new hotel user as well as process the form to
* add the hotel user to the database
*
***************************************************************************************
**/

public class Admin_addhoteluser extends HttpServlet {


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

    Connection con = Connect.getCon(req);            // get DB connection

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
    req.getSession().setAttribute(Member.ADD_HOTEL_USER_FRM, form);

    FormRenderer.render(form, out);

    drawEndOfPageAfterForm(out);
 }   // end of doGet


  /**
  ***************************************************************************************
  *
  * This method will process the form data for a new hotel user
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

     Connection con = Connect.getCon(req);            // get DB connection

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

     FormModel addHotelUserForm = null;
     Object theForm = session.getAttribute(Member.ADD_HOTEL_USER_FRM);

     if (theForm != null && theForm instanceof FormModel)
     {
       addHotelUserForm = (FormModel)theForm;
     }
     else
     {
      try
      {
        //It is possible that the user hit the back button and got to an old form and then
        //clicked save, in this case there isn't one in session, so create one
        addHotelUserForm = buildNewForm(req, resp, session, con, out);
        session.setAttribute(Member.ADD_HOTEL_USER_FRM, addHotelUserForm);
        addHotelUserForm.update(req, resp, out);
      }
      catch (Exception e)
      {
        return;
      }
     }

     FeedBack feedback = new FeedBack();

     // Get all the parameters entered and validate them

     //  first name
     String fname = req.getParameter(member.FIRST_NAME);
     feedback = (member.isFirstNameValid(member.FIRST_NAME, req));
     if (!feedback.isPositive())
     {
       //the first name has invalid data, update the form and return with error message
       updateFormAndReturnUser(addHotelUserForm, feedback, req, resp, session, out);
       return;
     }

     //  last name
     String lname = req.getParameter(member.LAST_NAME);
     feedback = (member.isLastNameValid(member.LAST_NAME, req));
     if (!feedback.isPositive())
     {
       //the last name has invalid data, update the form and return with error message
       updateFormAndReturnUser(addHotelUserForm, feedback, req, resp, session, out);
       return;
     }

     //  mid initial
     String mname = req.getParameter(member.MIDDLE_INITIAL);

     //  username
     String user = req.getParameter(member.USER_NAME);
     feedback = (member.isUserNameValid(member.USER_NAME, req));
     if (!feedback.isPositive())
     {
       //the user name has invalid data, update the form and return with error message
       updateFormAndReturnUser(addHotelUserForm, feedback, req, resp, session, out);
       return;
     }

     feedback = member.memberExists(user, member.USER_NAME, con);
     if (!feedback.isPositive() )
     {
       //the member number already exists, update the form and return with error message
       feedback.setAffectedField(member.USER_NAME);
       updateFormAndReturnUser(addHotelUserForm, feedback, req, resp, session, out);
       return;
     }

     //  password
     String password = req.getParameter(member.PASSWORD);
     feedback = (member.isPasswordValid(member.PASSWORD, req));
     if (!feedback.isPositive())
     {
       //the password has invalid data, update the form and return with error message
       updateFormAndReturnUser(addHotelUserForm, feedback, req, resp, session, out);
       return;
     }

     //  guest types
     feedback = (member.isGuestTypesValid(member.GUEST_TYPE_REQ + 1, req));
     if (!feedback.isPositive())
     {
       //the password has invalid data, update the form and return with error message
       updateFormAndReturnUser(addHotelUserForm, feedback, req, resp, session, out);
       return;
     }


     // days in advance
     String[] daysParams = new String[7];
     daysParams[0] = "day1";
     daysParams[1] = "day2";
     daysParams[2] = "day3";
     daysParams[3] = "day4";
     daysParams[4] = "day5";
     daysParams[5] = "day6";
     daysParams[6] = "day7";

     feedback = (member.isDaysInAdvanceValid(daysParams, req));
     if (!feedback.isPositive())
     {
      //the days in advance has invalid data, update the form and return with error message
       updateFormAndReturnUser(addHotelUserForm, feedback, req, resp, session, out);
       return;
     }

     String day1Str = req.getParameter("day1");
     if (day1Str.equals("") || day1Str == null) day1Str = Member.DAYS_IN_ADVANCE_DEFAULT;
     String day2Str = req.getParameter("day2");
     if (day2Str.equals("") || day2Str == null) day2Str = Member.DAYS_IN_ADVANCE_DEFAULT;
     String day3Str = req.getParameter("day3");
     if (day3Str.equals("") || day3Str == null) day3Str = Member.DAYS_IN_ADVANCE_DEFAULT;
     String day4Str = req.getParameter("day4");
     if (day4Str .equals("") || day4Str == null) day4Str = Member.DAYS_IN_ADVANCE_DEFAULT;
     String day5Str = req.getParameter("day5");
     if (day5Str .equals("") || day5Str == null) day5Str = Member.DAYS_IN_ADVANCE_DEFAULT;
     String day6Str = req.getParameter("day6");
     if (day6Str.equals("") || day6Str == null) day6Str = Member.DAYS_IN_ADVANCE_DEFAULT;
     String day7Str = req.getParameter("day7");
     if (day7Str.equals("") || day7Str == null) day7Str = Member.DAYS_IN_ADVANCE_DEFAULT;


     ArrayList<String> hguest = new ArrayList<String>();

     parmClub parm = new parmClub(0, con);

     for (int i = 1; i <= parm.MAX_Guests; i++) {

        if (req.getParameter("guest" + i) != null && !req.getParameter("guest" + i).equals(""))
            hguest.add(req.getParameter("guest" + i));

     }

     //  add hotel user to the database
     try {

          PreparedStatement stmt = con.prepareStatement (
         "INSERT INTO hotel3 (username, password, name_last, name_first, name_mi, " +
         "days1, days2, days3, days4, days5, days6, days7, " + /*
         "guest1, guest2, guest3, guest4, guest5, guest6, guest7, guest8, guest9, guest10, " +
         "guest11, guest12, guest13, guest14, guest15, guest16, guest17, guest18, guest19, guest20, " +
         "guest21, guest22, guest23, guest24, guest25, guest26, guest27, guest28, " +
         "guest29, guest30, guest31, guest32, guest33, guest34, guest35, guest36, " + */
         "message) " +
         "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,'')");

        stmt.clearParameters();
        stmt.setString(1, user);
        stmt.setString(2, password);
        stmt.setString(3, lname);
        stmt.setString(4, fname);
        stmt.setString(5, mname);
        stmt.setInt(6, new Integer(day1Str).intValue());
        stmt.setInt(7, new Integer(day2Str).intValue());
        stmt.setInt(8, new Integer(day3Str).intValue());
        stmt.setInt(9, new Integer(day4Str).intValue());
        stmt.setInt(10, new Integer(day5Str).intValue());
        stmt.setInt(11, new Integer(day6Str).intValue());
        stmt.setInt(12, new Integer(day7Str).intValue());

        //MemberHelper.setGuestTypesInStatement(req, stmt, out);

        stmt.executeUpdate();          // execute the prepared stmt

        stmt.close();   // close the stmt

        // now add the allowed hotel guest types to their table
        stmt = con.prepareStatement (
                "INSERT INTO hotel3_gtypes (id, username, guest_type) VALUES (NULL, ?, ?)");

        for (int i = 0; i < hguest.size(); i++) {

            stmt.clearParameters();
            stmt.setString(1, user);
            stmt.setString(2, hguest.get(i));
            stmt.executeUpdate();

        // first delete a
        }
      
        //User was successfully added to the database, remove the form from session so that
        //the data is not reused.
        session.removeAttribute(Member.ADD_HOTEL_USER_FRM);

        String nextAction = req.getParameter(ActionHelper.NEXT_ACTION);
        if (nextAction.equals(ActionHelper.UPDATE_AND_RETURN))
        {
          resp.sendRedirect(versionId + "servlet/Admin_addhoteluser");
        }
        else
        {
          resp.sendRedirect(versionId + "servlet/Admin_hotelusers");

        }

     }
     catch (Exception exc) {

      exc.printStackTrace();
        out.println(SystemUtils.HeadTitleAdmin("Database Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
        out.println("<BR>Please try again later.");
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
    addMemHelp.setUrl("javascript:openNewWindow('" + versionId + Help.ADD_HOTEL_USER + "', 'AddHotelUserOnlineHelp', '" + Help.WINDOW_SIZE + "')");

    pageActions.add(addMemHelp);

    out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Add Hotel User to Database Main Page"));
    String onLoad = "";

    if (feedback != null)
    {
      onLoad = "javascript:displayFeedbackMessage('" + feedback.get(0) + "');document.pgFrm." + feedback.getAffectedField() + ".focus()";
    }
    else
    {
      onLoad = "document.pgFrm." + Member.FIRST_NAME + ".focus()";

    }

    LayoutHelper.drawBeginPageContentWrapper(onLoad, null, out);
    ActionHelper.drawAdminHotelUserNavBar(null, out);
    LayoutHelper.drawBeginMainBodyContentWrapper("Add new hotel user", pageActions, out);

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
  * the add hotel user form fails.  It will update the form with the data from the request.
  *
  * @pararm addHotelUserForm the form model that contains the components to render in the page
  * @param feedback the feedback model that contains any messages to present to the user
  *                 upon loading the page
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private void updateFormAndReturnUser(FormModel addHotelUserForm, FeedBack feedback, HttpServletRequest req,HttpServletResponse resp, HttpSession session, PrintWriter out)
  {
    if (addHotelUserForm != null)
    {
      addHotelUserForm.update(req, resp, out);
      session.setAttribute(Member.ADD_HOTEL_USER_FRM, addHotelUserForm);
    }

    drawBeginningOfPageBeforeForm(feedback, out);
    FormRenderer.render(addHotelUserForm, out);
    drawEndOfPageAfterForm(out);


  }

  /**
  ***************************************************************************************
  *
  * This method will build a new form model for creating a new hotel user
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

     String club = (String)session.getAttribute("club");

      //create the form
      FormModel form = new FormModel("pgFrm", FormModel.POST, "bot");
      form.setNumColumns(4);
      form.addHiddenInput("formId", Member.ADD_HOTEL_USER_FRM);
      form.addHiddenInput(ActionHelper.NEXT_ACTION, "");

      //create the action model for this form and add it to the form model
      ActionModel formActions = new ActionModel();

      String applyUrl = "javascript:update('" + versionId + "servlet/Admin_addhoteluser', 'updateAndReturn')";
      Action applyAction = new Action("updateAndReturn", Labels.APPLY, "Save the hotel user and return to this page to add another.", applyUrl);
      formActions.add(applyAction);

      String okUrl = "javascript:update('" + versionId + "servlet/Admin_addhoteluser', 'updateAndClose')";
      Action okAction = new Action("updateAndClose", Labels.OK, "Save the changes and return to Hotel Users menu.", okUrl);
      formActions.add(okAction);

      String cancelUrl = "javascript:cancel('" + versionId + "servlet/Admin_hotelusers', 'cancel', '" + Labels.WARNING_CHANGES_MAY_BE_LOST + "')";
      Action cancelAction = new Action("cancel", Labels.CANCEL, Labels.RETURN_NO_CHANGES_SAVED, cancelUrl);
      formActions.add(cancelAction);

      form.setActions(formActions);

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
      lastName.setOnChange("setPasswordToLastName()");

      nameRow.add(firstName, "frm");
      nameRow.add(middleInitial, "frm");
      nameRow.add(lastName, "frm");
      form.addRow(nameRow);

      RowModel loginRow = new RowModel();
      nameRow.setId("loginRow");

      Attribute userName = new Attribute(member.USER_NAME, member.USER_NAME_LABEL, "", Attribute.EDIT);
      userName.setSize("15");
      userName.setMaxLength("15");
      Attribute password = new Attribute(member.PASSWORD, member.PASSWORD_LABEL, "", Attribute.EDIT);
      password.setSize("10");
      password.setMaxLength("10");

      loginRow.add(userName, "frm", 2);
      loginRow.add(password, "frm", 2);

      form.addRow(loginRow);

      //Add a separator
      form.addSeparator(new Separator());

      RowModel guestTypeLabelRow = new RowModel();
      guestTypeLabelRow.setId("guestTypeLabelRow");
      guestTypeLabelRow.add("Select Guest Types for this user", "frm", 4);
      form.addRow(guestTypeLabelRow);


      try {

        MemberHelper.addGuestTypesToForm(con, null, form);

      }
      catch (Exception e1) {

        out.println(SystemUtils.HeadTitleAdmin("DB Error"));
        out.println("<BR><BR><H2>Database Access Error</H2>");
        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact your club manager.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"" +versionId+ "servlet/Admin_announce\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");

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

      Attribute day1 = new Attribute("day1", Labels.SUNDAY, "", Attribute.EDIT);
      day1.setMaxLength("3");
      day1.setSize("3");
      daysRow1.add(day1,"frm");
      Attribute day2 = new Attribute("day2", Labels.MONDAY,"", Attribute.EDIT);
      day2.setMaxLength("3");
      day2.setSize("3");
      daysRow1.add(day2, "frm");
      Attribute day3 = new Attribute("day3", Labels.TUESDAY,"", Attribute.EDIT);
      day3.setMaxLength("3");
      day3.setSize("3");
      daysRow1.add(day3, "frm");
      Attribute day4 = new Attribute("day4", Labels.WEDNESDAY,"", Attribute.EDIT);
      day4.setMaxLength("3");
      day4.setSize("3");
      daysRow1.add(day4, "frm");

      RowModel daysRow2 = new RowModel();
      Attribute day5 = new Attribute("day5", Labels.THURSDAY,"", Attribute.EDIT);
      day5.setMaxLength("3");
      day5.setSize("3");
      daysRow2.add(day5, "frm");
      Attribute day6 = new Attribute("day6", Labels.FRIDAY,"", Attribute.EDIT);
      day6.setMaxLength("3");
      day6.setSize("3");
      daysRow2.add(day6, "frm");
      Attribute day7 = new Attribute("day7", Labels.SATURDAY,"", Attribute.EDIT);
      day7.setMaxLength("3");
      day7.setSize("3");
      daysRow2.add(day7, "frm");

      daysInAdLayout.addRow(daysRow1);
      daysInAdLayout.addRow(daysRow2);

      form.addRow(daysInAdLayout);

      return form;
  }

}
