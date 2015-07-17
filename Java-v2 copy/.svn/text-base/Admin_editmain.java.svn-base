/***************************************************************************************
 *   Admin_editmain:  This servlet will process the 'edit club member' request from Admin's
 *                    editmain page.
 *
 *
 *   called by:  admin_main.htm (calls doGet)
 *               self (calls doPost)
 *
 *   created: 12/13/2001   Bob P.
 *
 *   last updated:
 *
 *        9/12/13   The member2b.flexid field will now be displayed and editable when logged in as a ForeTees superadmin account (e.g. admin4tea) if the club is a Premier client.
 *        4/12/13   Commented out the "Save" button since it is unnecessary, and can cause complications due to the setup of the page. Only the "Save and Close" button will be available going forward.
 *       12/05/11   Updated memNum field to have a maxlength of 15 instead of 10.
 *       03/29/10   Add support for USTA Number & NTRP Rating values
 *       12/04/09   Make sub-type field available to all clubs so we don't have to 
 *                  update this for future customs (case 1750).
 *        9/03/09   Changed processing to grab mships from mship5 instead of club5
 *        6/05/09   Interlachen - add mem_subtype of "Member Guest Pass" (case 1686).
 *        2/15/09   Add tflag field for Tee Sheet Flag (case 1612).
 *       12/31/08   Add Member Sub Type for Green Hills CC custom processing (case 1574).
 *        8/15/08   Add Member Sub Type for Sharon Heights custom processing.
 *        6/02/08   Add Member Sub Type for Brooklawn custom processing.
 *       01/05/08   Add 'gender' to form
 *        7/19/07   Change non-golf (billing) flag labels from NON_GOLF to EXCLUDE.
 *       07/18/07   Add 'non-golf' to form for Roster Sync clubs for our billing purposes.
 *       05/17/07   Add 'inact' to form
 *       03/15/07   Add hdcp club & association numbers to form
 *       12/13/06   Increase bag slot max length from 6 to 12.
 *       10/13/06   Scrub all incoming form values
 *        6/27/06   RDP - add webid field to form for web site id mapping.
 *        4/22/04   Add Member Sub Type for Hazeltine custom processing.
 *        2/24/04   Add POS_ID field to member db table.
 *       11/01/03   Enhancements for Version 3 of the software.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        1/14/03   Enhancements for Version 2 of the software.
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
import com.foretees.client.attribute.DatePicker;
import com.foretees.client.attribute.SelectionList;
import com.foretees.client.attribute.Checkbox;

import com.foretees.client.form.FormModel;
import com.foretees.client.form.FormRenderer;

import com.foretees.client.layout.LayoutHelper;
import com.foretees.client.layout.Separator;

import com.foretees.client.table.Cell;
import com.foretees.client.table.RowModel;

import com.foretees.common.FeedBack;
import com.foretees.common.help.Help;
import com.foretees.common.Labels;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;

import com.foretees.member.Member;
import com.foretees.member.MemberHelper;


/**
***************************************************************************************
*
* This servlet will build the form for editing an existing member
*
***************************************************************************************
**/

public class Admin_editmain extends HttpServlet {

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
  * This method will process the form request for an Edit Member
  *
  ***************************************************************************************
  **/

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    //
    //  Prevent caching so sessions are not mangled
    //
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    ResultSet rs = null;
    String omit = "";

    HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

     if (session == null) {

        return;
     }

     String club = (String)session.getAttribute("club");

     Connection con = SystemUtils.getCon(session);            // get DB connection

     if (con == null) {

        out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"" +versionId+ "servlet/Admin_announce\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
     }

     //
     //  Make sure the system has been configured - reject if not
     //
     boolean okToProceed = checkConfig(con);

     if (okToProceed == false) {

        //
        //  System setup not completed - reject
        //
        out.println(SystemUtils.HeadTitleAdmin("Procedure Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Procedure Error</H3>");
        out.println("<BR><BR>Sorry, the system configuration has not yet been completed.");
        out.println("<BR>Please complete the system setup through the proshop System Config.");
        out.println("<BR><BR>If you still receive this error, contact customer support.");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"" +versionId+ "servlet/Admin_announce\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
     }

     //
     // Get the parameters entered
     //
     String user = SystemUtils.scrubString(req.getParameter(Member.REQ_USER_NAME));         //  username


     //
     //  verify the required fields
     //
     if (user.equals( omit )) {

        invData(out);    // inform the user and return
        return;
     }



     FormModel form = null;

     Object theForm = req.getSession().getAttribute(Member.EDIT_MEM_FRM);

     if (theForm != null && theForm instanceof FormModel)
     {
       form = (FormModel)theForm;
     }


     if (form == null){
       // username specified - use it to search table
       //
       try {

          PreparedStatement stmt = con.prepareStatement (
                "SELECT * FROM member2b WHERE username = ?");
                
          /*    "SELECT m.*, cn.club_num, ca.assoc_num " +
                "FROM member2b m " +
                "LEFT OUTER JOIN hdcp_club_num cn ON cn.hdcp_club_num_id = m.hdcp_club_num_id " + 
                "LEFT OUTER JOIN hdcp_assoc_num ca ON ca.hdcp_assoc_num_id = m.hdcp_assoc_num_id " +
                "WHERE m.username = ?"); */
          
          stmt.clearParameters();        // clear the parms
          stmt.setString(1, user);       // put the parm in stmt
          rs = stmt.executeQuery();      // execute the prepared stmt

          if (rs.next()) {

             form = buildNewForm(req, resp, session, con, rs, out);

             //add the form to the session to retrieve later
             req.getSession().setAttribute(Member.EDIT_MEM_FRM, form);

          } else {

             noMem(out);    // member does not exist - inform the user and return
             return;
          }

          stmt.close();              // close the stmt

       }
       catch (Exception exc) {

         out.println(SystemUtils.HeadTitleAdmin("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>User: " +user);
         out.println("<BR><BR>Error: " +exc.getMessage());
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"javascript:history.back(1)\">Return</a>");
         out.println("</CENTER></BODY></HTML>");

//          dbError(out);
          return;
       }
     }

     FeedBack feedback = null;

     Object theFeedback = session.getAttribute(Member.MEM_FEEDBACK);

     if (theFeedback != null && theFeedback instanceof FeedBack)
     {
       feedback = (FeedBack)theFeedback;
     }

     drawBeginningOfPageBeforeForm(feedback, out);

     FormRenderer.render(form, out);

     drawEndOfPageAfterForm(out);

  }   // end of doPost


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitleAdmin("Input Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>You must enter the username or First & Last names.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<a href=\"javascript:history.back(1)\">Return</a>");
   out.println("</CENTER></BODY></HTML>");
 }

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
  * This method will draw the the portion of the page that comes before the edit member
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
    Action addMemHelp = new Action(ActionHelper.HELP, Help.LABEL);
    addMemHelp.setUrl("javascript:openNewWindow('" + versionId + Help.EDIT_MEMBER + "', 'EditMemberOnlineHelp', '" + Help.WINDOW_SIZE + "')");

    pageActions.add(addMemHelp);

    out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Edit Member Main Page"));
    String onLoad = "";

    if (feedback != null)
    {
      onLoad = "javascript:displayFeedbackMessage('" + feedback.get(0) + "');document.pgFrm." + feedback.getAffectedField() + ".focus()";
    }
    else
    {
      onLoad = "document.pgFrm." + Member.FIRST_NAME + ".focus()";
    }

    String onUnLoad = "javascript:cleanup('" + versionId + "servlet/Admin_editmem', 'cleanup', 'Any changes you have made since you last saved will be lost.')";

    LayoutHelper.drawBeginPageContentWrapper(onLoad, onUnLoad, out);
    ActionHelper.drawAdminMemberNavBar(null, out);
    LayoutHelper.drawBeginMainBodyContentWrapper("Edit member&nbsp&nbsp" + Labels.REQUIRED_FIELDS, pageActions, out);

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that comes after the edit member
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
  * This method will build a new form model for editing an existing member
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
     SelectionList memTypes = null;
     SelectionList memSubTypes = null;
     SelectionList memShips = null;
     SelectionList walkCart = null;
     SelectionList hdcpClubNums = null;
     SelectionList hdcpAssocNums = null;
     SelectionList genders = null;

     String club_num = "" + rs.getInt("hdcp_club_num_id");
     String assoc_num = "" + rs.getInt("hdcp_assoc_num_id");
     String gender = rs.getString("gender");
     String mship = rs.getString("m_ship");
     String mtype = rs.getString("m_type");
     String msubtype = rs.getString("msub_type");
     String message = rs.getString("message");
     String locker = rs.getString("locker");
     
     int emailOpt = rs.getInt("emailOpt");
     int birth = rs.getInt("birth");
     int inact = rs.getInt("inact");
     int billable = rs.getInt("billable");

     String club = (String)session.getAttribute("club");   // get club name
     String user = (String)session.getAttribute("user");   // get username of current user
     int rsync = (Integer)session.getAttribute("rsync");   // get Roster Sync indicator for this club

     //
     //  Get month and day from birth value (mmdd)
     //

     int year = birth / 10000;
     int month = (birth%10000) / 100;
     int day = (birth%10000)%100;

     try {

        //Get the walk/cart options available for this club
        walkCart = MemberHelper.getWalkCartOptions(con, rs.getString("wc"));

        //Get the membership types and member types for this club
        memTypes = MemberHelper.getMemberTypes(con, mtype, null);
        memShips = MemberHelper.getMemberShips(con, mship, null);
        hdcpClubNums = MemberHelper.getHdcpClubNums(con, club_num, null);
        hdcpAssocNums = MemberHelper.getHdcpAssocNums(con, assoc_num, null);
        genders = MemberHelper.getGenderTypes(gender, null);
        
        /*
        if (club.equals( "hazeltine" ) || club.equals( "brooklawn" ) || club.equals( "sharonheights" ) || club.equals( "greenhills" ) ||
            club.equals( "interlachen" ) ) {        
           memSubTypes = MemberHelper.getMemberSubTypes(con, msubtype, null);    // add member sub_type
        }
         */   // now available to all clubs
        memSubTypes = MemberHelper.getMemberSubTypes(con, msubtype, null);    // add member sub_type
        
     }
     catch (Exception e1) {

        throw new Exception(e1);
     }

      //create the form
      FormModel form = new FormModel("pgFrm", FormModel.POST, "bot");
      form.setNumColumns(3);
      form.addHiddenInput("formId", member.EDIT_MEM_FRM);
      form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
      form.addHiddenInput(Member.LETTER, req.getParameter(Member.LETTER));
      String username = rs.getString("username");
      form.addHiddenInput(Member.REQ_USER_NAME, username);

      //create the action model for this form and add it to the form model
      ActionModel formActions = new ActionModel();

      /*
      String applyUrl = "javascript:update('" + versionId + "servlet/Admin_editmem', 'updateAndReturn')";
      Action applyAction = new Action("updateAndReturn", Labels.APPLY, "Save the changes and return to this page to continue editing.", applyUrl);
      formActions.add(applyAction);
       */
      
      String okUrl = "javascript:update('" + versionId + "servlet/Admin_editmem', 'updateAndClose')";
      Action okAction = new Action("updateAndClose", Labels.OK, "Save the changes and return to Members menu.", okUrl);
      formActions.add(okAction);

      String cancelUrl = "javascript:cancel('" + versionId + "servlet/Admin_editmem', 'cancel', '" + Labels.WARNING_CHANGES_MAY_BE_LOST + "')";
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


      nameRow.add(firstName, "frmRqd");
      nameRow.add(middleInitial, "frm");
      nameRow.add(lastName, "frmRqd");

      form.addRow(nameRow);

      RowModel loginRow = new RowModel();
      nameRow.setId("loginRow");

      Attribute userName = new Attribute(member.USER_NAME, member.USER_NAME_LABEL, username, Attribute.EDIT);
      userName.setSize("15");
      userName.setMaxLength("15");
      Attribute password = new Attribute(member.PASSWORD, member.PASSWORD_LABEL, rs.getString("password"), Attribute.EDIT);
      password.setSize("15");
      password.setMaxLength("15");
      Checkbox cb = null;
      Checkbox cb1 = null;
      if (inact == 0) {                                                               // if active
         cb = new Checkbox(member.MEM_INACT, member.INACT_LABEL_SHORT, "1", false);
      } else {
         cb = new Checkbox(member.MEM_INACT, member.INACT_LABEL_SHORT, "1", true);    // checked if inact
      }
      if (rsync > 0) {            // if Roster Sync club
         if (billable == 0) {                                                               // if NOT billable
            cb1 = new Checkbox(member.EXCLUDE, member.EXCLUDE_LABEL_SHORT, "1", true);     // checked if NOT billable (excluded member)
         } else {
            cb1 = new Checkbox(member.EXCLUDE, member.EXCLUDE_LABEL_SHORT, "1", false);    // NOT checked if billable
         }
      }

      loginRow.add(userName, "frmRqd");
      loginRow.add(password, "frmRqd");
      loginRow.add(cb, "frm");
      if (rsync > 0) {       // if roster sync supported for this club
         loginRow.add(cb1, "frm");
      }

      form.addRow(loginRow);

      //Add a separator
      form.addSeparator(new Separator());

       //Construct a row for the email input field
      RowModel emailRow = new RowModel();
      emailRow.setId("emailRow");
      Attribute email = new Attribute(member.EMAIL, member.EMAIL_LABEL, rs.getString("email"), Attribute.EDIT);
      email.setSize("30");
      email.setMaxLength("50");

      Attribute email2 = new Attribute(member.EMAIL2, member.EMAIL_LABEL_2, rs.getString("email2"), Attribute.EDIT);
      email2.setSize("30");
      email2.setMaxLength("50");

      emailRow.add(email, "frm");
      emailRow.add(email2, "frm");

      form.addRow(emailRow);

      //Construct a row for the phone numbers
      RowModel phoneRow = new RowModel();
      phoneRow.setId("phoneRow");
      Attribute phone = new Attribute(member.PHONE_NUM, member.PHONE_NUMBER_LABEL, rs.getString("phone1"), Attribute.EDIT);
      phone.setSize("24");
      phone.setMaxLength("24");

      Attribute phone2 = new Attribute(member.PHONE_NUM2, member.PHONE_NUMBER_LABEL_2, rs.getString("phone2"), Attribute.EDIT);
      phone2.setSize("24");
      phone2.setMaxLength("24");

      phoneRow.add(phone, "frm");
      phoneRow.add(phone2, "frm");

      form.addRow(phoneRow);

      //Construct a row for the Birth date and POS member id
      RowModel posRow = new RowModel();
      posRow.setId("posRow");

      DatePicker bday = new DatePicker(member.BIRTH_DATE, member.BIRTH_DATE_LABEL, month, day, year);

      Calendar cal = new GregorianCalendar();       // get todays date

      int calyear = cal.get(Calendar.YEAR);

      bday.setStartYear(calyear - 110);
      bday.setEndYear(calyear);
      bday.setRequired(false);
      Cell bdayCell = new Cell(bday);
      bdayCell.setStyleSheetClass("frm");
      posRow.add(bdayCell);

      Attribute posid = new Attribute(member.POS_ID, member.POS_ID_LABEL, rs.getString("posid"), Attribute.EDIT);
      posid.setSize("15");
      posid.setMaxLength("15");

      posRow.add(posid, "frm");

      form.addRow(posRow);

      //Construct a row for specifying the gender
      RowModel genderRow = new RowModel();
      genderRow.setId("genderRow");
      genderRow.add(genders, "frm");
      form.addRow(genderRow);
      
      //Add a separator
      form.addSeparator(new Separator());

      RowModel memRow = new RowModel();
      memRow.setId("memRow");

      //need to construct a list of all possible memberships
      memRow.add(memShips, "frmRqd");

      //need to construct a list of all possible membertypes
      memRow.add(memTypes, "frmRqd");

      form.addRow(memRow);

      //construct a row for the membership number
      RowModel memNumRow = new RowModel();
      memNumRow.setId("memNumRow");

      Attribute memNumber = new Attribute(member.MEM_NUM, member.MEM_NUM_LABEL, rs.getString("memNum"), Attribute.EDIT);
      memNumber.setSize("10");
      memNumber.setMaxLength("15");

      Attribute webId = new Attribute(member.WEBID, member.WEB_ID_LABEL, rs.getString("webid"), Attribute.EDIT);      
      webId.setSize("10");
      webId.setMaxLength("15");

      /*
      if (club.equals( "hazeltine" ) || club.equals( "brooklawn" ) || club.equals( "sharonheights" ) || club.equals( "greenhills" ) ||
          club.equals( "interlachen" ) ) {               
         //need to construct a list of all possible member numbers and member sub-types
         memNumRow.add(memNumber, "frmRqd");
         memNumRow.add(webId, "frm");
         memNumRow.add(memSubTypes, "frmRqd");      // add member sub_type for customs
      } else {
         memNumRow.add(memNumber, "frmRqd");   // just member numbers
         memNumRow.add(webId, "frm", 2);
      }
       */     // now available to all clubs
      //need to construct a list of all possible member numbers and member sub-types
      memNumRow.add(memNumber, "frmRqd");
      memNumRow.add(webId, "frm");
      memNumRow.add(memSubTypes, "frmRqd");      // add member sub_type for customs

      form.addRow(memNumRow);
      
      if (Utilities.isFTCPclub(con) && Utilities.isFTAdminUser(user)) {
          RowModel flexIdRow = new RowModel();
          flexIdRow.setId("flexIdRow");
          
          Attribute flexId = new Attribute(member.FLEXID, member.FLEX_ID_LABEL, rs.getString("flexid"), Attribute.EDIT);
          flexId.setSize("10");
          flexId.setMaxLength("15");
          
          flexIdRow.add(flexId, "frm");
          
          form.addRow(flexIdRow);
      } else {
          form.addHiddenInput(member.FLEXID, rs.getString("flexid"));
      }

      //Add a separator
      form.addSeparator(new Separator());

      //Construct a row for the handicap header
      RowModel handicapHeader = new RowModel();
      handicapHeader.setId("handicapHeader");

      handicapHeader.add(member.HANDICAP_HEADER, "frm", 3);

      form.addRow(handicapHeader);

      //Construct a row for the handicap
      RowModel handicapRow = new RowModel();
      handicapRow.setId("handicapRow");

      float c_hancap = rs.getFloat("c_hancap");
      String c_hancap_str = (new Float(c_hancap)).toString();
      Attribute cHandicap = new Attribute(member.COURSE_HANDICAP, member.COURSE_HANDICAP_LABEL, c_hancap_str, Attribute.EDIT);
      cHandicap.setSize("6");
      cHandicap.setMaxLength("6");
      float g_hancap = rs.getFloat("g_hancap");
      String g_hancap_str = (new Float(g_hancap)).toString();
      Attribute gHandicap = new Attribute(member.USGA_HANDICAP, member.USGA_HANDICAP_LABEL, g_hancap_str, Attribute.EDIT);
      gHandicap.setSize("6");
      gHandicap.setMaxLength("6");

      handicapRow.add(cHandicap, "frm");
      handicapRow.add(gHandicap, "frm");

      form.addRow(handicapRow);

      //Construct a row for the handicap system number
      RowModel ghinRow = new RowModel();
      ghinRow.setId("ghinRow");
      Attribute ghin = new Attribute(member.GHIN, member.GHIN_LABEL, rs.getString("ghin"), Attribute.EDIT);
      ghin.setSize("10");
      ghin.setMaxLength("16");

      ghinRow.add(ghin, "frm", 3);

      form.addRow(ghinRow);
      
      //Construct a row for the additional handicap information
      RowModel ghinRow2 = new RowModel();
      ghinRow2.setId("ghinRow2");
      
      ghinRow2.add(hdcpClubNums, "frm");
      ghinRow2.add(hdcpAssocNums, "frm");
      
      form.addRow(ghinRow2);

      //Add a separator
      form.addSeparator(new Separator());

      //Construct a row for the USTA Number
      RowModel ustaRow = new RowModel();
      ustaRow.setId("ustaRow");
      Attribute usta = new Attribute(member.USTA_NUM, member.USTA_NUM_LABEL, rs.getString(member.USTA_NUM), Attribute.EDIT);
      usta.setSize("10");
      usta.setMaxLength("16");

      ustaRow.add(usta, "frm");

      float ntrp_rating = rs.getFloat("ntrp_rating");
      String ntrp_rating_str = (new Float(ntrp_rating)).toString();
      Attribute ntrp = new Attribute(member.NTRP_RATING, member.NTRP_RATING_LABEL, ntrp_rating_str, Attribute.EDIT);

      ustaRow.add(ntrp, "frm");

      form.addRow(ustaRow);

      //Add a separator
      form.addSeparator(new Separator());

      //need to construct a list of all possible forms of transportation
      RowModel walkCartRow = new RowModel();
      walkCartRow.setId("walkCartRow");

      walkCartRow.add(walkCart, "frm");

      Attribute bagSlot = new Attribute(member.BAG_SLOT, member.BAG_SLOT_LABEL, rs.getString("bag"), Attribute.EDIT);
      bagSlot.setSize("6");
      bagSlot.setMaxLength("12");

      walkCartRow.add(bagSlot, "frm");

      Attribute tFlag = new Attribute(member.T_FLAG, member.T_FLAG_LABEL, rs.getString("tflag"), Attribute.EDIT);
      tFlag.setSize("4");
      tFlag.setMaxLength("4");

      walkCartRow.add(tFlag, "frm");

      form.addRow(walkCartRow);

      return form;
  }


 // *********************************************************
 // Check if this club has been configured by pro
 // *********************************************************

 private boolean checkConfig(Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   String mship = "";

   boolean status = true;            // ok

   try {
      stmt = con.createStatement();

      rs = stmt.executeQuery("SELECT mship FROM mship5 LIMIT 1");

      if (rs.next()) {

         mship = rs.getString("mship");

         if (mship == null || mship.equals( "" )) {

            status = false;
         }

      } else {

         status = false;
      }
      stmt.close();

   }
   catch (Exception ignore) {

         status = false;
   }

   return(status);
 }

}
