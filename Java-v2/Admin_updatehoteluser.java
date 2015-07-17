/***************************************************************************************
*   Admin_updatehoteluser:  This servlet will process the the request to update a hotel
*                           users information
*
*   created: 11/22/2003  JAG
*
*   last updated:
*
*       4/21/10  Changes to add support for unlimited guest types
*       4/24/08  Update Connection object to use SystemUtils.getCon()
*       3/10/05  Changed to validate the days in advance.
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
import com.foretees.client.action.ActionHelper;
import com.foretees.client.form.FormModel;
import com.foretees.common.FeedBack;
import com.foretees.common.ProcessConstants;
import com.foretees.common.parmClub;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.common.Connect;

/**
***************************************************************************************
*
* This servlet will process the form data for editing information about an
* existing hotel user
*
***************************************************************************************
**/

public class Admin_updatehoteluser extends HttpServlet {

  //initialize the attributes
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
  * This method will process the request to update a hotel users information
  *
  ***************************************************************************************
  **/

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

   //check for intruder
    HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

    if (session == null) {

      return;
    }

    // Process request according to the next action

    String nextAction = (String)(req.getAttribute(ActionHelper.NEXT_ACTION));
    String redirectURL = "";

    if (nextAction == null || nextAction.equals("")){
      nextAction = req.getParameter(ActionHelper.NEXT_ACTION);
    }

    if (nextAction.equals(ActionHelper.CLEANUP) || nextAction.equals(ActionHelper.CANCEL))
    {
      //user left the page with out hitting a button or hit the cancel button
      redirectURL = cleanup(req, resp, session, out);
    }
    else if (nextAction.equals(ActionHelper.DELETE)) {
      //remove the record from the database and return them to the page they came from
      removeMem(req, out, session);
      redirectURL = cleanup(req, resp, session, out);
    } else {

      boolean updateSuccessful = updateMem(req, resp, out, session);

      if (updateSuccessful){


        if (nextAction.equals(ActionHelper.UPDATE_AND_RETURN))
        {
          String username = req.getParameter(Member.REQ_USER_NAME);
          session.removeAttribute(Member.EDIT_HOTEL_USER_FRM); // added 4-21-10 because form was still displaying previously selected guest types that are no longer selected...
          session.removeAttribute(Member.HOTEL_USER_FEEDBACK);
          redirectURL = versionId + "servlet/Admin_edithoteluser?username=" + username;
        }
        else
        {
          redirectURL = cleanup(req, resp, session, out);
        }
      }
      else
      {
        String username = req.getParameter(Member.REQ_USER_NAME);
        redirectURL = versionId + "servlet/Admin_edithoteluser?username=" + username;
      }

    }

    resp.sendRedirect(redirectURL);

  }   // end of doPost

  /**
  ***************************************************************************************
  *
  * This method will remove a member record from the member table in the database
  *
  ***************************************************************************************
  **/

  private void removeMem(HttpServletRequest req, PrintWriter out, HttpSession sess) {


    String h_user = "";

    String club = (String)sess.getAttribute("club");

    Connection con = Connect.getCon(req);            // get DB connection

    if (con == null) {

      out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"" +versionId+ "servlet/Admin_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
    }

    // Get the parameters entered, original username (from hidden field)
    h_user = req.getParameter(Member.REQ_USER_NAME);

    // Delete the member from the member table
    int count = 0;
    try {

      PreparedStatement stmt = con.prepareStatement (
               "DELETE FROM hotel3 WHERE username = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, h_user);            // put the parm in stmt
      count = stmt.executeUpdate();     // execute the prepared stmt

      stmt.close();

    }
    catch (Exception exc) {

      dbError(out);
      return;
    }

    if (count == 0) {

      noMem(out);    // member does not exist - inform the user and return
      return;
    }
  }  // done with delete function

  /**
  ***************************************************************************************
  *
  * This method will update a member record in the member table in the database
  *
  ***************************************************************************************
  **/

  private boolean updateMem(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) {

    String club = (String)session.getAttribute("club");

    //int sess_activity_id = (Integer)session.getAttribute("activity_id");

    Connection con = Connect.getCon(req);            // get DB connection

    if (con == null) {

      out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"" +versionId+ "servlet/Admin_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return false;
    }

    FormModel editHotelUserForm = null;
    Member member = new Member();

    Object theForm = session.getAttribute(Member.EDIT_HOTEL_USER_FRM);

    if (theForm != null && theForm instanceof FormModel)
    {
      editHotelUserForm = (FormModel)theForm;
    }
    else
    {
      try
      {
        //editHotelUserForm = new FormModel();
        //session.setAttribute(Member.EDIT_HOTEL_USER_FRM, editHotelUserForm);
        //editHotelUserForm.update(req, resp, out);
      }
      catch (Exception e)
      {
        return false;
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
     updateFormAndReturnUser(editHotelUserForm, feedback, req, resp, session, out);
     return false;
    }

    //  last name
    String lname = req.getParameter(member.LAST_NAME);
    feedback = (member.isLastNameValid(member.LAST_NAME, req));
    if (!feedback.isPositive())
    {
     //the last name has invalid data, update the form and return with error message
     updateFormAndReturnUser(editHotelUserForm, feedback, req, resp, session, out);
     return false;
    }

    //  mid initial
    String mname = req.getParameter(member.MIDDLE_INITIAL);

    //  username
    String user = req.getParameter(member.USER_NAME);
    feedback = (member.isUserNameValid(member.USER_NAME, req));
    if (!feedback.isPositive())
    {
     //the user name has invalid data, update the form and return with error message
     updateFormAndReturnUser(editHotelUserForm, feedback, req, resp, session, out);
     return false;
    }

    String h_user = req.getParameter(Member.REQ_USER_NAME);

    String changedUserName = req.getParameter(member.USER_NAME);
    if (!(changedUserName.equals(h_user)))
    {
      //check if this userid is already being used
      feedback = (member. memberExists(changedUserName, member.USER_NAME, con));
      if (!feedback.isPositive())
      {
        feedback.setPositive(false);
       updateFormAndReturnUser(editHotelUserForm, feedback, req, resp, session, out);
       return false;
      }

    }

    //  password
    String password = req.getParameter(member.PASSWORD);
    feedback = (member.isPasswordValid(member.PASSWORD, req));
    if (!feedback.isPositive())
    {
     //the password has invalid data, update the form and return with error message
     updateFormAndReturnUser(editHotelUserForm, feedback, req, resp, session, out);
     return false;
    }

    //  guest types
     String[] guestTypes = req.getParameterValues(member.GUEST_TYPE);
     feedback = (member.isGuestTypesValid(member.GUEST_TYPE + 1, req));
     if (!feedback.isPositive())
     {
       //the guestTypes have invalid data, update the form and return with error message
       updateFormAndReturnUser(editHotelUserForm, feedback, req, resp, session, out);
       return false;
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
       updateFormAndReturnUser(editHotelUserForm, feedback, req, resp, session, out);
       return false;
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

    //  Update the member in the member table
    //
    int count = 0;
    try {

      PreparedStatement stmt = con.prepareStatement (
      "UPDATE hotel3 SET username = ?, password = ?, name_last = ?, name_first = ?, name_mi = ?, " +
      "days1 = ?, days2 = ?, days3 = ?, days4 = ?, days5 = ?, days6 = ?, days7 = ?, " + /*
      "guest1 = ?, guest2 = ?, guest3 = ?, guest4 = ?, guest5 = ?, guest6 = ?, guest7 = ?, guest8 = ?, " +
      "guest9 = ?, guest10 = ?, guest11 = ?, guest12 = ?, guest13 = ?, guest14 = ?, guest15 = ?, guest16 = ?, " +
      "guest17 = ?, guest18 = ?, guest19 = ?, guest20 = ?, guest21 = ?, guest22 = ?, guest23 = ?, guest24 = ?, " +
      "guest25 = ?, guest26 = ?, guest27 = ?, guest28 = ?, guest29 = ?, guest30 = ?, guest31 = ?, guest32 = ?, " +
      "guest33 = ?, guest34 = ?, guest35 = ?, guest36 = ?, " + */
      "message = '' " +
      "WHERE username = ?");

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

      stmt.setString(13, h_user);
      count = stmt.executeUpdate();

      stmt.close();


      // first delete any exising ones to ensure we remove any the user unselected
      stmt = con.prepareStatement (
          "DELETE FROM hotel3_gtypes WHERE username = ?");
      stmt.clearParameters();
      stmt.setString(1, user);
      stmt.executeUpdate();

      // now add the allowed hotel guest types to their table
      stmt = con.prepareStatement (
          "INSERT INTO hotel3_gtypes (id, username, guest_type) VALUES (NULL, ?, ?)");

      for (int i = 0; i < hguest.size(); i++) {

          stmt.clearParameters();
          stmt.setString(1, user);
          stmt.setString(2, hguest.get(i));
          stmt.executeUpdate();

      }

    }
    catch (Exception exc) {
      exc.printStackTrace();
      dbError(out);
      return false;
    }

    if (editHotelUserForm == null) {
        out.println("<p>editHotelUserForm is null</p>");
    } else {

    editHotelUserForm.update(req, resp, out);
    session.setAttribute(Member.EDIT_HOTEL_USER_FRM, editHotelUserForm);
    }
    return true;
  }

        
  /**
  ***************************************************************************************
  *
  * This method will print an error message when there is a database error
  *
  ***************************************************************************************
  **/

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
  * This method will print an error message when the user does not exist in the database
  *
  ***************************************************************************************
  **/

  private void noMem(PrintWriter out)
  {

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

  /**
  ***************************************************************************************
  *
  * This method update the form model with the data from the request and adds the form and
  * the feedback into the session
  *
  ***************************************************************************************
  **/

  private void updateFormAndReturnUser(FormModel editHotelUserForm, FeedBack feedback, HttpServletRequest req,HttpServletResponse resp, HttpSession session, PrintWriter out)
  {
    if (editHotelUserForm != null)
    {
      editHotelUserForm.update(req, resp, out);
      session.setAttribute(Member.EDIT_HOTEL_USER_FRM, editHotelUserForm);
      session.setAttribute(Member.HOTEL_USER_FEEDBACK, feedback);

     }
  }

  /**
  ***************************************************************************************
  *
  * This method remove the form model and the feedback from the session and return
  * the redirect url
  *
  ***************************************************************************************
  **/

  private String cleanup(HttpServletRequest req,HttpServletResponse resp, HttpSession session, PrintWriter out)
  {
    session.removeAttribute(Member.EDIT_HOTEL_USER_FRM);
    session.removeAttribute(Member.HOTEL_USER_FEEDBACK);
    return (versionId + "servlet/Admin_hotelusers");
  }


}
