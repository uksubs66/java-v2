/***************************************************************************************
 *   Admin_addmem:  This servlet will process the 'add club members' request from Admin's
 *                  addmem page.
 *
 *
 *   called by:  admin_addmem.htm
 *
 *   created: 12/13/2001   Bob P.
 *
 *   last updated:
 * 
 *       12/04/09   Make sub-type field available to all clubs so we don't have to 
 *                  update this for future customs (case 1750).
 *        9/03/09   Changed processing to grab mships from mship5 instead of club5
 *        6/05/09   Interlachen - add mem_subtype of "Member Guest Pass" (case 1686).
 *       12/31/08   Add Member Sub Type for Green Hills CC custom processing (case 1574).
 *        8/15/08   Add Member Sub Type for Sharon Heights custom processing.
 *        6/02/08   Add Member Sub Type for Brooklawn custom processing (case 1493).
 *       01/05/08   Add 'gender' to form
 *        7/19/07   Change non-golf (billing) flag labels from NON_GOLF to EXCLUDE.
 *       07/18/07   Add 'non-golf' to form for Roster Sync clubs for our billing purposes.
 *       05/17/07   Add 'inact' to form
 *       05/16/07   Do not allow duplicate names.
 *       03/15/07   Add hdcp club & association numbers to form
 *       12/13/06   Increase bag slot max length from 6 to 12.
 *       10/13/06   Scrub all incoming form values
 *        6/27/06   RDP - add webid field to form for web site id mapping.
 *        2/07/06   RDP - add webid field to member2b for web site id mapping.
 *        9/02/05   RDP - change call to isEmailValid - only pass the email address.
 *        4/22/04   Add Member Sub Type for Hazeltine custom processing.
 *        2/24/04   Add POS_ID field to member db table.
 *       11/04/03   Enhancements for Version 4 of the software.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        1/14/03   Enhancements for Version 2 of the software.
 *        1/08/05   Added fields for 2nd email address, 2 phone numbers, and birth date.
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
import com.foretees.common.Labels;
import com.foretees.common.ProcessConstants;

import com.foretees.member.Member;
import com.foretees.member.MemberHelper;

/**
***************************************************************************************
*
* This servlet will draw the form for adding a new member as well as process the form to
* add the member to the database
*
***************************************************************************************
**/

public class Admin_addmem extends HttpServlet {

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

    //
    //  Prevent caching so sessions are not mangled
    //
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

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
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
    }

    //
    //  Make sure the system has been configured - reject if not
    //
    boolean okToProceed = checkConfig(con);
      
    if (okToProceed == true) {
      
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
       req.getSession().setAttribute(Member.ADD_MEM_FRM, form);

       FormRenderer.render(form, out);

       drawEndOfPageAfterForm(out);

    } else {

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
       out.println("</input></form></font>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
    }

 }   // end of doGet


  /**
  ***************************************************************************************
  *
  * This method will process the form data for a new member
  *
  ***************************************************************************************
  **/

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    ResultSet rs = null;
    PreparedStatement stmt = null;
    Member member = new Member();

    HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

     if (session == null) {

        return;
     }

     String club = (String)session.getAttribute("club");
     int rsync = (Integer)session.getAttribute("rsync");          // get Roster Sync indicator for this club

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
        out.println("</input></form></font>");
        out.println("</CENTER></BODY></HTML>");
        return;
     }

     float course = -99;     // default hndcp's
     float usga = -99;

     int bmm = 0;          // birthday
     int bdd = 0;
     int bday = 0;
     int inact = 0;
     int exclude = 0;
     int billable = 0;

     String locker = "";
     String smm = "";
     String sdd = "";

     FormModel addMemForm = null;
     Object theForm = session.getAttribute(Member.ADD_MEM_FRM);

     if (theForm != null && theForm instanceof FormModel)
     {
       addMemForm = (FormModel)theForm;
     }
     else
     {
      try
      {
        //It is possible that the user hit the back button and got to an old form and then
        //clicked save, in this case there isn't one in session, so create one
        addMemForm = buildNewForm(req, resp, session, con, out);
        session.setAttribute(Member.ADD_MEM_FRM, addMemForm);
        addMemForm.update(req, resp, out);
      }
      catch (Exception e)
      {
        return;
      }
     }

     FeedBack feedback = new FeedBack();

     // Get all the parameters entered and validate them

     //  first name
     String fname = SystemUtils.scrubString(req.getParameter(member.FIRST_NAME));
     feedback = (member.isFirstNameValid(member.FIRST_NAME, req));
     if (!feedback.isPositive())
     {
       //the first name has invalid data, update the form and return with error message
       updateFormAndReturnUser(addMemForm, feedback, req, resp, session, out);
       return;
     }

     //  last name
     String lname = SystemUtils.scrubString(req.getParameter(member.LAST_NAME));
     feedback = (member.isLastNameValid(member.LAST_NAME, req));
     if (!feedback.isPositive())
     {
       //the last name has invalid data, update the form and return with error message
       updateFormAndReturnUser(addMemForm, feedback, req, resp, session, out);
       return;
     }

     //  mid initial
     String mname = SystemUtils.scrubString(req.getParameter(member.MIDDLE_INITIAL));

     //  username
     String user = SystemUtils.scrubString(req.getParameter(member.USER_NAME));
     feedback = (member.isUserNameValid(member.USER_NAME, req));
     if (!feedback.isPositive())
     {
       //the user name has invalid data, update the form and return with error message
       updateFormAndReturnUser(addMemForm, feedback, req, resp, session, out);
       return;
     }

     feedback = member.memberExists(user, member.USER_NAME, con);
     if (!feedback.isPositive() )
     {
       //the member number already exists, update the form and return with error message
       updateFormAndReturnUser(addMemForm, feedback, req, resp, session, out);
       return;
     }

     //  member number
     String memNum = SystemUtils.scrubString(req.getParameter(member.MEM_NUM));

     //  webid
     String memwebid = "";

     if (req.getParameter(member.WEBID) != null) {
        memwebid = SystemUtils.scrubString(req.getParameter(member.WEBID));
     }

     //  password
     String password = SystemUtils.scrubString(req.getParameter(member.PASSWORD));
     feedback = (member.isPasswordValid(member.PASSWORD, req));
     if (!feedback.isPositive())
     {
       //the password has invalid data, update the form and return with error message
       updateFormAndReturnUser(addMemForm, feedback, req, resp, session, out);
       return;
     }

     //  inact flag
     inact = 0;
     if (req.getParameter(member.MEM_INACT) != null) {
        String inacts = req.getParameter(member.MEM_INACT);
        inact = Integer.parseInt(inacts);
     }


     //  billing (non-golf) flag
     exclude = 0;
     if (req.getParameter(member.EXCLUDE) != null) {
        String excludes = req.getParameter(member.EXCLUDE);
        exclude = Integer.parseInt(excludes);
     }


     //  membership type
     String mshiptype = SystemUtils.scrubString(req.getParameter(member.MEMSHIP_TYPE));

     //  member type
     String memtype = SystemUtils.scrubString(req.getParameter(member.MEM_TYPE));

     //  member sub-type
     String memsubtype = "";

     if (req.getParameter(member.MEM_SUB_TYPE) != null) {
        memsubtype = SystemUtils.scrubString(req.getParameter(member.MEM_SUB_TYPE));
     }

     //  email addr
     String email = SystemUtils.scrubString(req.getParameter(member.EMAIL));
     feedback = (member.isEmailValid(email));
     if (!feedback.isPositive())
     {
       //the email address has invalid data, update the form and return with error message
       updateFormAndReturnUser(addMemForm, feedback, req, resp, session, out);
       return;
     }

     //  email addr
     String email2 = SystemUtils.scrubString(req.getParameter(member.EMAIL2));
     feedback = (member.isEmailValid(email2));
     if (!feedback.isPositive())
     {
       //the email address has invalid data, update the form and return with error message
       updateFormAndReturnUser(addMemForm, feedback, req, resp, session, out);
       return;
     }


      //course handicap
      String ch = SystemUtils.scrubString(req.getParameter(member.COURSE_HANDICAP));
      if (ch != null && !(ch.equals(""))){
        feedback = member.isCourseHandicapValid(member.COURSE_HANDICAP, req);
        if (feedback.isPositive())
        {
          course = member.convertCourseHandicap(member.COURSE_HANDICAP, req);
        }
        else
        {
          //the course handicap has invalid data, update the form and return with error message
          updateFormAndReturnUser(addMemForm, feedback, req, resp, session, out);
          return;
        }
      }

      //  usga handicap
      String uh = SystemUtils.scrubString(req.getParameter(member.USGA_HANDICAP));
      if (uh != null && !(uh.equals(""))){
        feedback = member.isUSGAHandicapValid(member.USGA_HANDICAP, req);
        if (feedback.isPositive())
        {
          usga = member.convertUSGAHandicap(member.USGA_HANDICAP,req);
        }
        else
        {
          //the usga handicap has invalid data, update the form and return with error message
          updateFormAndReturnUser(addMemForm, feedback, req, resp, session, out);
          return;
        }
      }

     //  phone number 1
     String phone_num = SystemUtils.scrubString(req.getParameter(member.PHONE_NUM));

     //  phone number 2
     String phone_num2 = SystemUtils.scrubString(req.getParameter(member.PHONE_NUM2));

     //  birth date
     String birth_day = SystemUtils.scrubString(req.getParameter(member.BIRTH_DATE + "_" + "day"));
     String birth_month = SystemUtils.scrubString(req.getParameter(member.BIRTH_DATE + "_" + "month"));
     String birth_year = SystemUtils.scrubString(req.getParameter(member.BIRTH_DATE + "_" + "year"));

     int daySet = 0;
     int monthSet = 0;
     int yearSet = 0;

     if (birth_day != null && !(birth_day.equals("")))
       daySet = 1;
     if (birth_month != null && !(birth_month.equals("")))
       monthSet = 1;
     if (birth_year != null && !(birth_year.equals("")))
       yearSet = 1;

     int total = daySet + monthSet + yearSet;

     if (total != 0 && total != 3)
     {
       feedback.setPositive(false);
       feedback.addMessage("Please fill in month, day and year to set the birth date");
       feedback.setAffectedField(member.BIRTH_DATE + "_" + "month");

       updateFormAndReturnUser(addMemForm, feedback, req, resp, session, out);
       return;
     }

     long birth_date = 0;
     if (total != 0)
     {
       int day = (new Integer(birth_day)).intValue();
       int month = (new Integer(birth_month)).intValue();
       int year = (new Integer(birth_year)).intValue();

       birth_date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd
    }

     //  walk/cart preference
     String walk_cart = SystemUtils.scrubString(req.getParameter(member.WALK_CART));

     if (walk_cart == null)
       walk_cart = "";

     //  gender
     String gender = req.getParameter(member.GENDER);
    
     //  ghin number
     String ghin = SystemUtils.scrubString(req.getParameter(member.GHIN));

     //  hdcp club number
     String hdcpClubNum = req.getParameter(member.HDCP_CLUB_NUM);

     //  hdcp assoc number
     String hdcpAssocNum = req.getParameter(member.HDCP_ASSOC_NUM);
    
     int hdcp_club_num_id = 0;
     int hdcp_assoc_num_id = 0;
     
     try {
        hdcp_club_num_id = Integer.parseInt(hdcpClubNum);
        hdcp_assoc_num_id = Integer.parseInt(hdcpAssocNum);
     }
     catch (NumberFormatException e) {
     }
     
     //  locker number
     //   locker = req.getParameter("locker");

     //  bag storage number
     String bag = SystemUtils.scrubString(req.getParameter(member.BAG_SLOT));

     
     //  bag storage number
     String tflag = SystemUtils.scrubString(req.getParameter(member.T_FLAG));       // get the tee sheet flag

     
     //   smm = req.getParameter("bmm");                  //  birthday month
     //   sdd = req.getParameter("bdd");                  //  birthday day

     //  POS Id
     String posid = SystemUtils.scrubString(req.getParameter(member.POS_ID));


     //
     //  convert numeric fields
     //
     if (!smm.equals( "" )) {
        try {
           bmm = Integer.parseInt(smm);
        }
        catch (NumberFormatException e) {
           // ignore error - let verify catch it
        }
     }

     if (!sdd.equals( "" )) {
        try {
           bdd = Integer.parseInt(sdd);
        }
        catch (NumberFormatException e) {
           // ignore error - let verify catch it
        }
     }

     //
     //  convert birthday month and day to mmdd
     //
     bday = bmm * 100;
     bday = bday + bdd;

     //
     //  check if the name already exists
     //
     try {

        boolean dupName = false;

        stmt = con.prepareStatement (
                 "SELECT inact FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

        stmt.clearParameters();
        stmt.setString(1, lname);
        stmt.setString(2, fname);
        stmt.setString(3, mname);
        rs = stmt.executeQuery();            // execute the prepared stmt

        if (rs.next()) {

           dupName = true;
        }
        stmt.close();              // close the stmt


        if (dupName == false) {

           //
           //  now add member to the database
           //
           if (rsync == 0) {            // if club does not use Roster Sync
             
              stmt = con.prepareStatement (
                "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                 "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, " +
                 "memNum, ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, " +
                 "name_pre, name_suf, webid, hdcp_club_num_id, hdcp_assoc_num_id, inact, gender, tflag) " +
                 "VALUES (?,?,?,?,?,?,?,?,0,?,?,?,'',1,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

           } else {     // Roster Sync - set billing indicator (exclude)

              stmt = con.prepareStatement (
                "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                 "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, " +
                 "memNum, ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, " +
                 "name_pre, name_suf, webid, hdcp_club_num_id, hdcp_assoc_num_id, inact, gender, tflag, billable) " +
                 "VALUES (?,?,?,?,?,?,?,?,0,?,?,?,'',1,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
           }

           stmt.clearParameters();        // clear the parms
           stmt.setString(1, user);       // put the parm in stmt
           stmt.setString(2, password);
           stmt.setString(3, lname);
           stmt.setString(4, fname);
           stmt.setString(5, mname);
           stmt.setString(6, mshiptype);
           stmt.setString(7, memtype);
           stmt.setString(8, email);
           stmt.setFloat(9, course);
           stmt.setFloat(10, usga);
           stmt.setString(11, walk_cart);
           stmt.setString(12, memNum);
           stmt.setString(13, ghin);
           stmt.setString(14, locker);
           stmt.setString(15, bag);
           stmt.setLong(16, birth_date);
           stmt.setString(17, posid);
           stmt.setString(18, memsubtype);
           stmt.setString(19, email2);
           stmt.setString(20, phone_num);
           stmt.setString(21, phone_num2);
           stmt.setString(22, "");
           stmt.setString(23, "");
           stmt.setString(24, memwebid);
           stmt.setInt(25, hdcp_club_num_id);
           stmt.setInt(26, hdcp_assoc_num_id);
           stmt.setInt(27, inact);
           stmt.setString(28, gender);
           stmt.setString(29, tflag);
           if (rsync > 0) {            // if club does not use Roster Sync (DO NOT alter the billable flag if no RS - see Support_billing)
              billable = 1;             // default = billable
              if (exclude == 1) {       // if member is to be excluded (not billable)
                 billable = 0;          // NOT billable
              }
              stmt.setInt(30, billable);
           }
           stmt.executeUpdate();          // execute the prepared stmt

           stmt.close();   // close the stmt

           //User was successfully added to the database, remove the form from session so that
           //the data is not reused.
           session.removeAttribute(Member.ADD_MEM_FRM);

           String nextAction = req.getParameter(ActionHelper.NEXT_ACTION);
           if (nextAction.equals(ActionHelper.UPDATE_AND_RETURN))
           {
            resp.sendRedirect(versionId + "servlet/Admin_addmem");
           }
           else
           {
             resp.sendRedirect(versionId + "servlet/Admin_members");

           }
             
        } else {      // dup name - reject

           feedback.setPositive(false);
           feedback.addMessage("The name (first, mi, last) already exists.  It must be unique. Please change one of the name fields.");
           feedback.setAffectedField("name");

           updateFormAndReturnUser(addMemForm, feedback, req, resp, session, out);
           return;
        }

     }
     catch (Exception exc) {

        out.println(SystemUtils.HeadTitleAdmin("Database Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<br><br><font size=\"2\">");
        out.println("<form method=\"get\" action=\"" +versionId+ "servlet/Admin_announce\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
        out.println("</input></form></font>");
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
    addMemHelp.setUrl("javascript:openNewWindow('" + versionId + "help/AddMemberOnlineHelp.html', 'AddMemberOnlineHelp', 'width=250, height=300, directories=no, location=no, menubar=no, scrollbars=yes, status=no, toolbar=no, resizable=yes')");

    pageActions.add(addMemHelp);

    out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Add Member to Database Main Page"));
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
    ActionHelper.drawAdminMemberNavBar(null, out);
    LayoutHelper.drawBeginMainBodyContentWrapper("Add new member&nbsp&nbsp" + Labels.REQUIRED_FIELDS, pageActions, out);

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
  * the add member form fails.  It will update the form with the data from the request.
  *
  * @pararm addMemForm the form model that contains the components to render in the page
  * @param feedback the feedback model that contains any messages to present to the user
  *                 upon loading the page
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private void updateFormAndReturnUser(FormModel addMemForm, FeedBack feedback, HttpServletRequest req,HttpServletResponse resp, HttpSession session, PrintWriter out)
  {
    if (addMemForm != null)
    {
      addMemForm.update(req, resp, out);
      session.setAttribute(Member.ADD_MEM_FRM, addMemForm);
    }

    drawBeginningOfPageBeforeForm(feedback, out);
    FormRenderer.render(addMemForm, out);
    drawEndOfPageAfterForm(out);


  }

  /**
  ***************************************************************************************
  *
  * This method will build a new form model for creating a new member
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

     Member member = new Member();
     SelectionList memTypes = null;
     SelectionList memSubTypes = null;
     SelectionList memShips = null;
     SelectionList walkCart = null;
     SelectionList hdcpClubNums = null;
     SelectionList hdcpAssocNums = null;
     SelectionList genders = null;

     String club = (String)session.getAttribute("club");
     int rsync = (Integer)session.getAttribute("rsync");          // get Roster Sync indicator for this club

     try {

        //Get the walk/cart options available for this club
        walkCart = MemberHelper.getWalkCartOptions(con, null);

        //Get the membership types and member types for this club
        memTypes = MemberHelper.getMemberTypes(con, null, null);
        memShips = MemberHelper.getMemberShips(con, null, null);
        
        hdcpClubNums = MemberHelper.getHdcpClubNums(con, null, null);
        hdcpAssocNums = MemberHelper.getHdcpAssocNums(con, null, null);

        genders = MemberHelper.getGenderTypes("", null);
        
        /*
        if (club.equals( "hazeltine" ) || club.equals( "brooklawn" ) || club.equals( "sharonheights" ) || club.equals( "greenhills" ) || 
            club.equals( "interlachen" ) ) {      
           memSubTypes = MemberHelper.getMemberSubTypes(con, null, null);   // add member sub_type
        }
         */   // now available to all clubs (optional)
        memSubTypes = MemberHelper.getMemberSubTypes(con, null, null);   // add member sub_type

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
        out.println("</input></form></font>");
        out.println("</CENTER></BODY></HTML>");

        throw new Exception(e1);
     }

      //create the form
      FormModel form = new FormModel("pgFrm", FormModel.POST, "bot");
      form.setNumColumns(3);
      form.addHiddenInput("formId", Member.ADD_MEM_FRM);
      form.addHiddenInput(ActionHelper.NEXT_ACTION, "");

      //create the action model for this form and add it to the form model
      ActionModel formActions = new ActionModel();

      String applyUrl = "javascript:update('" + versionId + "servlet/Admin_addmem', 'updateAndReturn')";
      Action applyAction = new Action("updateAndReturn", Labels.APPLY, "Save the member and return to this page to add another.", applyUrl);
      formActions.add(applyAction);

      String okUrl = "javascript:update('" + versionId + "servlet/Admin_addmem', 'updateAndClose')";
      Action okAction = new Action("updateAndClose", Labels.OK, "Save the changes and return to Members menu.", okUrl);
      formActions.add(okAction);

      String cancelUrl = "javascript:cancel('" + versionId + "servlet/Admin_members', 'cancel', '" + Labels.WARNING_CHANGES_MAY_BE_LOST + "')";
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


      nameRow.add(firstName, "frmRqd");
      nameRow.add(middleInitial, "frm");
      nameRow.add(lastName, "frmRqd");

      form.addRow(nameRow);

      RowModel loginRow = new RowModel();
      nameRow.setId("loginRow");

      Attribute userName = new Attribute(member.USER_NAME, member.USER_NAME_LABEL, "", Attribute.EDIT);
      userName.setSize("15");
      userName.setMaxLength("15");
      Attribute password = new Attribute(member.PASSWORD, member.PASSWORD_LABEL, "", Attribute.EDIT);
      password.setSize("15");
      password.setMaxLength("15");
      Checkbox cb = new Checkbox(Member.MEM_INACT, Member.INACT_LABEL_SHORT, "1", false);
      Checkbox cb1 = null;
      if (rsync > 0) {       // if roster sync supported for this club
         cb1 = new Checkbox(Member.EXCLUDE, Member.EXCLUDE_LABEL_SHORT, "1", false);
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
      Attribute email = new Attribute(member.EMAIL, member.EMAIL_LABEL, "", Attribute.EDIT);
      email.setSize("30");
      email.setMaxLength("50");

      Attribute email2 = new Attribute(member.EMAIL2, member.EMAIL_LABEL_2, "", Attribute.EDIT);
      email2.setSize("30");
      email2.setMaxLength("50");

      emailRow.add(email, "frm");
      emailRow.add(email2, "frm");

      form.addRow(emailRow);

      //Construct a row for the phone numbers
      RowModel phoneRow = new RowModel();
      phoneRow.setId("phoneRow");
      Attribute phone = new Attribute(member.PHONE_NUM, member.PHONE_NUMBER_LABEL, "", Attribute.EDIT);
      phone.setSize("24");
      phone.setMaxLength("24");

      Attribute phone2 = new Attribute(member.PHONE_NUM2, member.PHONE_NUMBER_LABEL_2, "", Attribute.EDIT);
      phone2.setSize("24");
      phone2.setMaxLength("24");

      phoneRow.add(phone, "frm");
      phoneRow.add(phone2, "frm");

      form.addRow(phoneRow);

      //Construct a row for the Birth date and POS member id
      RowModel posRow = new RowModel();
      posRow.setId("posRow");

      DatePicker bday = new DatePicker(member.BIRTH_DATE, member.BIRTH_DATE_LABEL, 0, 0, 0);

      Calendar cal = new GregorianCalendar();       // get todays date

      int year = cal.get(Calendar.YEAR);

      bday.setStartYear(year - 110);
      bday.setEndYear(year);
      bday.setRequired(false);
      Cell bdayCell = new Cell(bday);
      bdayCell.setStyleSheetClass("frm");
      posRow.add(bdayCell);

      Attribute posId = new Attribute(member.POS_ID, member.POS_ID_LABEL, "", Attribute.EDIT);
      posId.setSize("15");
      posId.setMaxLength("15");

      posRow.add(posId, "frm");

      form.addRow(posRow);

      //Construct a row for specifying the gender
      RowModel genderRow = new RowModel();
      genderRow.setId("genderRow");
      genderRow.add(genders, "frm");
      form.addRow(genderRow);

      //Add a separator
      form.addSeparator(new Separator());

      //Construct a row for the membertypes and memberships
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

      Attribute memNumber = new Attribute(member.MEM_NUM, member.MEM_NUM_LABEL, "", Attribute.EDIT);
      memNumber.setSize("10");
      memNumber.setMaxLength("10");

      Attribute webId = new Attribute(member.WEBID, member.WEB_ID_LABEL, "", Attribute.EDIT);
      webId.setSize("10");
      webId.setMaxLength("15");

      /*
      if (club.equals( "hazeltine" ) || club.equals( "brooklawn" ) || club.equals( "sharonheights" ) || club.equals( "greenhills" ) ||
          club.equals( "interlachen" ) ) {
         
         //need to construct a list of all possible member numbers and sub-types
         memNumRow.add(memNumber, "frmRqd");
         memNumRow.add(webId, "frm");
         memNumRow.add(memSubTypes, "frmRqd");       // add member sub_type for customs
      } else {
         memNumRow.add(memNumber, "frmRqd");        // just member numbers
         memNumRow.add(webId, "frm", 2);
      }
       */    // now available to all clubs
      memNumRow.add(memNumber, "frmRqd");
      memNumRow.add(webId, "frm");
      memNumRow.add(memSubTypes, "frmRqd");       // add member sub_type for customs

      form.addRow(memNumRow);

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

      Attribute cHandicap = new Attribute(member.COURSE_HANDICAP, member.COURSE_HANDICAP_LABEL, "", Attribute.EDIT);
      cHandicap.setSize("6");
      cHandicap.setMaxLength("6");
      Attribute gHandicap = new Attribute(member.USGA_HANDICAP, member.USGA_HANDICAP_LABEL, "", Attribute.EDIT);
      gHandicap.setSize("6");
      gHandicap.setMaxLength("6");

      handicapRow.add(cHandicap, "frm");
      handicapRow.add(gHandicap, "frm");

      form.addRow(handicapRow);

      //Construct a row for the handicap system number
      RowModel ghinRow = new RowModel();
      ghinRow.setId("ghinRow");
      Attribute ghin = new Attribute(member.GHIN, member.GHIN_LABEL, "", Attribute.EDIT);
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

      //need to construct a list of all possible forms of transportation
      RowModel walkCartRow = new RowModel();
      walkCartRow.setId("walkCartRow");

      walkCartRow.add(walkCart, "frm");

      Attribute bagSlot = new Attribute(member.BAG_SLOT, member.BAG_SLOT_LABEL, "", Attribute.EDIT);
      bagSlot.setSize("6");
      bagSlot.setMaxLength("12");

      walkCartRow.add(bagSlot, "frm");

      Attribute tFlag = new Attribute(member.T_FLAG, member.T_FLAG_LABEL, "", Attribute.EDIT);       // Add the T-Flag field
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
