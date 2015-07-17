/***************************************************************************************
 *   Admin_editmem:  This servlet will process the 'edit club member' request from Admin's
 *                   editmain page.
 *
 *
 *   called by:  Admin_editmain
 *
 *   created: 12/16/2001   Bob P.
 *
 *   last updated:
 *
 *       03/29/10   Add support for USTA Number & NTRP Rating values
 *       12/07/09   Change removeMem to also delete all partner list entries for/with the user being removed
 *       12/01/09   Add updPartner method to apply any member username updates to the new partner db table (removed updBuddy)
 *        5/14/09   Add anchor link to cleanup link so user is returned to same location they left from on member list
 *        2/15/09   Add tflag field for Tee Sheet Flag (case 1612).
 *       09/05/08   Commented out player/username fields #6-10 in updEvents - Fields no longer in db
 *       01/05/08   Add 'gender' to form
 *        7/19/07   Change non-golf (billing) flag labels from NON_GOLF to EXCLUDE.
 *       07/18/07   Add 'non-golf' to form for Roster Sync clubs for our billing purposes.
 *       06/25/07   Add updTeepast method to update tee past table when name or username changes.
 *                  Common_sync now calls these methods as well (upd....).
 *       05/17/07   Add 'inact' to form
 *       05/16/07   Do not allow duplicate names.
 *       03/15/07   Add hdcp club & association numbers
 *       10/13/06   Scrub all incoming form values
 *        6/27/06   RDP - add webid field for web site id mapping.
 *       10/14/05   RDP - If member's name or username changes, update all other tables.
 *        9/02/05   RDP - change call to isEmailValid - only pass the email address.
 *        4/22/04   Add Member Sub-type processing for Hazeltine Natl - custom
 *        2/24/04   Add POS_ID field to member db table.
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
import java.sql.*;

//foretees imports
import com.foretees.client.action.ActionHelper;
import com.foretees.client.form.FormModel;
import com.foretees.common.FeedBack;
import com.foretees.common.ProcessConstants;
import com.foretees.member.Member;

/**
 ***************************************************************************************
 *
 * This servlet will process the form data for editing information about an
 * existing member
 *
 ***************************************************************************************
**/

public class Admin_editmem extends HttpServlet {

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
  * This method will process the form request
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
          String username = SystemUtils.scrubString(req.getParameter(Member.REQ_USER_NAME));
          session.removeAttribute(Member.MEM_FEEDBACK);
          redirectURL = versionId + "servlet/Admin_editmain?username=" + username;
        }
        else
        {
          redirectURL = cleanup(req, resp, session, out);
        }
      }
      else
      {
        String username = SystemUtils.scrubString(req.getParameter(Member.REQ_USER_NAME));
        redirectURL = versionId + "servlet/Admin_editmain?username=" + username;
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

    Connection con = SystemUtils.getCon(sess);            // get DB connection

    if (con == null) {

      out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"" +versionId+ "servlet/Admin_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
    }

    // Get the parameters entered, original username (from hidden field)
    h_user = SystemUtils.scrubString(req.getParameter(Member.REQ_USER_NAME));

    // Delete the member from the member table
    int count = 0;
    try {

      PreparedStatement stmt = con.prepareStatement (
            "DELETE FROM member2b WHERE username = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, h_user);            // put the parm in stmt
      stmt.executeUpdate();                 // execute the prepared stmt

      stmt.close();

      stmt = con.prepareStatement (
            "DELETE FROM dist4 WHERE owner = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, h_user);            // put the parm in stmt
      stmt.executeUpdate();                 // execute the prepared stmt

      stmt.close();

      // Delete this member's partner list, as well as from all other partner lists
      stmt = con.prepareStatement(
            "DELETE FROM partner WHERE user_id = ? OR partner_id = ?");
      stmt.clearParameters();
      stmt.setString(1, h_user);
      stmt.setString(2, h_user);
      stmt.executeUpdate();

      stmt.close();

    }
    catch (Exception exc) {

      dbError(out);
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


    PreparedStatement stmt = null;
    ResultSet rs = null;

    String old_user = "";
    String locker = "";
    String smm = "";
    String sdd = "";

    String newName = "";
    String oldFname = "";
    String oldLname = "";
    String oldMname = "";

    int birth = 0;
    int bmm = 0;
    int bdd = 0;
    int billable = 0;
    float course = -99;     // default hndcp's
    float usga = -99;

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
      out.println("<a href=\"" +versionId+ "servlet/Admin_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return false;
    }

    FormModel editMemberForm = null;
    Member member = new Member();

    Object theForm = session.getAttribute(Member.EDIT_MEM_FRM);

    if (theForm != null && theForm instanceof FormModel)
    {
      editMemberForm = (FormModel)theForm;
    }
    else
    {
      try
      {
        //editMemberForm = new FormModel();
        //session.setAttribute(Member.EDIT_MEM_FRM, editMemberForm);
        //editMemberForm.update(req, resp, out);
      }
      catch (Exception e)
      {
        return false;
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
     updateFormAndReturnUser(editMemberForm, feedback, req, resp, session, out);
     return false;
    }

    //  last name
    String lname = SystemUtils.scrubString(req.getParameter(member.LAST_NAME));
    feedback = (member.isLastNameValid(member.LAST_NAME, req));
    if (!feedback.isPositive())
    {
     //the last name has invalid data, update the form and return with error message
     updateFormAndReturnUser(editMemberForm, feedback, req, resp, session, out);
     return false;
    }

    //  mid initial
    String mname = SystemUtils.scrubString(req.getParameter(member.MIDDLE_INITIAL));

    //  new username
    String user = SystemUtils.scrubString(req.getParameter(member.USER_NAME));
    feedback = (member.isUserNameValid(member.USER_NAME, req));
    if (!feedback.isPositive())
    {
     //the user name has invalid data, update the form and return with error message
     updateFormAndReturnUser(editMemberForm, feedback, req, resp, session, out);
     return false;
    }

    old_user = SystemUtils.scrubString(req.getParameter(Member.REQ_USER_NAME));

    if (!(user.equals(old_user)))     
    {
      //check if this userid is already being used
      feedback = (member.memberExists(user, member.USER_NAME, con));
      if (!feedback.isPositive())
      {
        feedback.setPositive(false);
        updateFormAndReturnUser(editMemberForm, feedback, req, resp, session, out);
        return false;
      }

    }


    //  membership type
    String mship = SystemUtils.scrubString(req.getParameter(member.MEMSHIP_TYPE));

    //  member type
    String mtype = SystemUtils.scrubString(req.getParameter(member.MEM_TYPE));

    //  member sub-type (if provided - Hazeltine only)
    String msubtype = "";

    if (req.getParameter(member.MEM_SUB_TYPE) != null) {
       msubtype = SystemUtils.scrubString(req.getParameter(member.MEM_SUB_TYPE));
    }

    //  email addr
    String email = SystemUtils.scrubString(req.getParameter(member.EMAIL));
    feedback = (member.isEmailValid(email));
    if (!feedback.isPositive())
    {
     //the email address has invalid data, update the form and return with error message
     updateFormAndReturnUser(editMemberForm, feedback, req, resp, session, out);
     return false;
    }

    //  email 2 addr
    String email2 = SystemUtils.scrubString(req.getParameter(member.EMAIL2));
    feedback = (member.isEmailValid(email2));
    if (!feedback.isPositive())
    {
      //the email address has invalid data, update the form and return with error message
      updateFormAndReturnUser(editMemberForm, feedback, req, resp, session, out);
      return false;
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
       updateFormAndReturnUser(editMemberForm, feedback, req, resp, session, out);
       return false;
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
       updateFormAndReturnUser(editMemberForm, feedback, req, resp, session, out);
       return false;
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

       updateFormAndReturnUser(editMemberForm, feedback, req, resp, session, out);
       return false;
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
    String wc = SystemUtils.scrubString(req.getParameter(member.WALK_CART));

    if (wc == null)
       wc = "";



    //  ghin number
    String ghin = SystemUtils.scrubString(req.getParameter(member.GHIN));

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
     updateFormAndReturnUser(editMemberForm, feedback, req, resp, session, out);
     return false;
    }

     //  inact flag
     int inact = 0;               // if checkbox is not checked, then parm is not passed
     if (req.getParameter(member.MEM_INACT) != null) {
        inact = 1;
     }


     //  billable (non-golf) flag
     int exclude = 0;               // if checkbox is not checked, then parm is not passed
     if (req.getParameter(member.EXCLUDE) != null) {
        exclude = 1;
     }

    //  gender
    String gender = req.getParameter(member.GENDER);

    //  POS Id
    String posId = SystemUtils.scrubString(req.getParameter(member.POS_ID));

    //  hdcp club number
    String hdcpClubNum = req.getParameter(member.HDCP_CLUB_NUM);

    //  hdcp assoc number
    String hdcpAssocNum = req.getParameter(member.HDCP_ASSOC_NUM);

    //   locker = req.getParameter("locker");            //  locker number
    String bag = SystemUtils.scrubString(req.getParameter(member.BAG_SLOT));
    //   smm = req.getParameter("bmm");                  //  birthday month
    //   sdd = req.getParameter("bdd");                  //  birthday day

    String tFlag = SystemUtils.scrubString(req.getParameter(member.T_FLAG));

    //  USTA Number
    String usta_num = SystemUtils.scrubString(req.getParameter(member.USTA_NUM));

    //  NTRP Level
    String ntrpRating = SystemUtils.scrubString(req.getParameter(member.NTRP_RATING));
    float ntrp_rating = Float.parseFloat(ntrpRating);


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
    //  Combine mm and dd into birth value (mmdd)
    //
    birth = bmm * 100;
    birth = birth + bdd;


    //
    //  Now get the old name and see if it or the username has changed
    //
    try {

       stmt = con.prepareStatement (
                "SELECT name_last, name_first, name_mi " +
                "FROM member2b WHERE username = ?");

       stmt.clearParameters();               // clear the parms
       stmt.setString(1, old_user);
       rs = stmt.executeQuery();           

       while(rs.next()) {

          oldLname = rs.getString("name_last");
          oldFname = rs.getString("name_first");
          oldMname = rs.getString("name_mi");
       }
       stmt.close();

       //
       //  check if username or name changed
       //
       if (!user.equals(old_user) || !oldFname.equals(fname) || !oldLname.equals(lname) || !oldMname.equals(mname)) {

          //
          //  First check if the name already exists
          //
          boolean dupName = false;

          stmt = con.prepareStatement (
                   "SELECT inact FROM member2b WHERE username != ? AND name_last = ? AND name_first = ? AND name_mi = ?");

          stmt.clearParameters();
          stmt.setString(1, old_user);
          stmt.setString(2, lname);
          stmt.setString(3, fname);
          stmt.setString(4, mname);
          rs = stmt.executeQuery();            // execute the prepared stmt

          if (rs.next()) {

             dupName = true;
          }
          stmt.close();              // close the stmt

          if (dupName == true) {

             feedback.setPositive(false);
             feedback.addMessage("The name (first, mi, last) already exists.  It must be unique. Please change one of the name fields.");
             feedback.setAffectedField("name");

             updateFormAndReturnUser(editMemberForm, feedback, req, resp, session, out);
             return false;
          }

          //
          //  username or name changed - we must update other tables now
          //
          StringBuffer mem_name = new StringBuffer( fname );       // get the new first name

          if (!mname.equals( "" )) {
             mem_name.append(" " +mname);                         // new mi
          }
          mem_name.append(" " +lname);                            // new last name

          newName = mem_name.toString();                          // convert to one string

          updTeecurr(newName, user, old_user, con);      // update teecurr with new values

          updTeepast(newName, user, old_user, con);      // update teepast with new values

          updLreqs(newName, user, old_user, con);        // update lreqs with new values

          updPartner(user, old_user, con);               // update partner with new values

          updEvents(newName, user, old_user, con);        // update evntSignUp with new values

          updLessons(newName, user, old_user, con);       // update the lesson books with new values
       }
    }
    catch (Exception exc) {
    }

    //
    //  Update the member in the member table
    //
    try {

      if (rsync == 0) {            // if club does not use Roster Sync

         stmt = con.prepareStatement (
               "UPDATE member2b SET username = ?, password = ?, name_last = ?, name_first = ?, " +
               "name_mi = ?, m_ship = ?, m_type = ?, email = ?, c_hancap = ?, g_hancap = ?, wc = ?, " +
               "memNum = ?, ghin = ?, locker = ?, bag = ?, birth = ?, posid = ?, msub_type = ?, email2 = ?, phone1 = ?, " +
               "phone2 = ?, name_pre = ?, name_suf = ?, webid = ?, hdcp_club_num_id = ?, hdcp_assoc_num_id = ?, inact = ?, gender = ?, tflag = ?, " +
               "ntrp_rating = ?, usta_num = ? " +
               "WHERE username = ?");

      } else {     // Roster Sync - set billing indicator (excluded)

         stmt = con.prepareStatement (
               "UPDATE member2b SET username = ?, password = ?, name_last = ?, name_first = ?, " +
               "name_mi = ?, m_ship = ?, m_type = ?, email = ?, c_hancap = ?, g_hancap = ?, wc = ?, " +
               "memNum = ?, ghin = ?, locker = ?, bag = ?, birth = ?, posid = ?, msub_type = ?, email2 = ?, phone1 = ?, " +
               "phone2 = ?, name_pre = ?, name_suf = ?, webid = ?, hdcp_club_num_id = ?, hdcp_assoc_num_id = ?, inact = ?, gender = ?, tflag = ?, " + 
               "ntrp_rating = ?, usta_num = ?, billable = ? " +
               "WHERE username = ?");
      }

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, user);              // put the parm in stmt
      stmt.setString(2, password);
      stmt.setString(3, lname);
      stmt.setString(4, fname);
      stmt.setString(5, mname);
      stmt.setString(6, mship);
      stmt.setString(7, mtype);
      stmt.setString(8, email);
      stmt.setFloat(9, course);
      stmt.setFloat(10, usga);
      stmt.setString(11, wc);
      stmt.setString(12, memNum);
      stmt.setString(13, ghin);
      stmt.setString(14, locker);
      stmt.setString(15, bag);
      stmt.setLong(16, birth_date);
      stmt.setString(17, posId);
      stmt.setString(18, msubtype);
      stmt.setString(19, email2);
      stmt.setString(20, phone_num);
      stmt.setString(21, phone_num2);
      stmt.setString(22, "");
      stmt.setString(23, "");
      stmt.setString(24, memwebid);
      stmt.setString(25, hdcpClubNum);
      stmt.setString(26, hdcpAssocNum);
      stmt.setInt(27, inact);
      stmt.setString(28, gender);
      stmt.setString(29, tFlag);
      stmt.setFloat(30, ntrp_rating);
      stmt.setString(31, usta_num);

      if (rsync > 0) {            // if club does not use Roster Sync (DO NOT alter the billable flag if no RS - see Support_billing)
         billable = 1;             // default = billable
         if (exclude == 1) {       // if member is to be excluded (not billable)
            billable = 0;          // NOT billable
         }
         stmt.setInt(32, billable);
         stmt.setString(33, old_user);
      } else {
         stmt.setString(32, old_user);
      }
      stmt.executeUpdate();

      stmt.close();

    }
    catch (Exception exc) {

      dbError(out);
      return false;
    }

    editMemberForm.update(req, resp, out);
    session.setAttribute(Member.EDIT_MEM_FRM, editMemberForm);

    return true;
  }


  /**
  ***************************************************************************************
  *
  * This method will change the name and/or username in the teecurr db table
  *
  ***************************************************************************************
  **/
    
  public static void updTeecurr(String newName, String newUser, String oldUser, Connection con) {

   PreparedStatement pstmt = null;

   try {

      pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET player1 = ?, username1 = ? WHERE username1 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET player2 = ?, username2 = ? WHERE username2 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET player3 = ?, username3 = ? WHERE username3 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET player4 = ?, username4 = ? WHERE username4 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET player5 = ?, username5 = ? WHERE username5 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

   }
   catch (Exception ignore) {
   }

  }


  /**
  ***************************************************************************************
  *
  * This method will change the name and/or username in the teepast db table
  *
  ***************************************************************************************
  **/

  public static void updTeepast(String newName, String newUser, String oldUser, Connection con) {

   PreparedStatement pstmt = null;

   try {

      pstmt = con.prepareStatement (
               "UPDATE teepast2 SET player1 = ?, username1 = ? WHERE username1 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teepast2 SET player2 = ?, username2 = ? WHERE username2 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teepast2 SET player3 = ?, username3 = ? WHERE username3 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teepast2 SET player4 = ?, username4 = ? WHERE username4 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teepast2 SET player5 = ?, username5 = ? WHERE username5 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

   }
   catch (Exception ignore) {
   }

  }


  /**
  ***************************************************************************************
  *
  * This method will change the name and/or username in the lreqs db table
  *
  ***************************************************************************************
  **/

  public static void updLreqs(String newName, String newUser, String oldUser, Connection con) {

   PreparedStatement pstmt = null;

   try {

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player1 = ?, user1 = ? WHERE user1 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player2 = ?, user2 = ? WHERE user2 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player3 = ?, user3 = ? WHERE user3 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player4 = ?, user4 = ? WHERE user4 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player5 = ?, user5 = ? WHERE user5 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player6 = ?, user6 = ? WHERE user6 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player7 = ?, user7 = ? WHERE user7 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player8 = ?, user8 = ? WHERE user8 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player9 = ?, user9 = ? WHERE user9 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player10 = ?, user10 = ? WHERE user10 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player11 = ?, user11 = ? WHERE user11 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player12 = ?, user12 = ? WHERE user12 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player13 = ?, user13 = ? WHERE user13 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player14 = ?, user14 = ? WHERE user14 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player15 = ?, user15 = ? WHERE user15 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player16 = ?, user16 = ? WHERE user16 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player17 = ?, user17 = ? WHERE user17 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player18 = ?, user18 = ? WHERE user18 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player19 = ?, user19 = ? WHERE user19 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player20 = ?, user20 = ? WHERE user20 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player21 = ?, user21 = ? WHERE user21 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player22 = ?, user22 = ? WHERE user22 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player23 = ?, user23 = ? WHERE user23 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player24 = ?, user24 = ? WHERE user24 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lreqs3 SET player25 = ?, user25 = ? WHERE user25 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

   }
   catch (Exception ignore) {
   }

  }

  /**
   * updPartner - Updates the partner db table with any username changes
   *
   * @param user_id_new User's new username
   * @param user_id_old User's old username
   * @param con Connection to club database
   */
  public static void updPartner(String user_id_new, String user_id_old, Connection con) {

      PreparedStatement pstmt = null;

      try {

          // Update user_id for all of this user's partner entries
          pstmt = con.prepareStatement("UPDATE partner SET user_id = ? WHERE user_id = ?");
          pstmt.clearParameters();
          pstmt.setString(1, user_id_new);
          pstmt.setString(2, user_id_old);

          pstmt.executeUpdate();

          pstmt.close();

          // Update partner_id for all entries in which this user is a partner
          pstmt = con.prepareStatement("UPDATE partner SET partner_id = ? WHERE partner_id = ?");
          pstmt.clearParameters();
          pstmt.setString(1, user_id_new);
          pstmt.setString(2, user_id_old);

          pstmt.executeUpdate();

          pstmt.close();

      } catch (Exception ignore) { }
  }


  /**
  ***************************************************************************************
  *
  * This method will change the name and/or username in the Event Sign Up db table
  *
  ***************************************************************************************
  **/

  public static void updEvents(String newName, String newUser, String oldUser, Connection con) {

   PreparedStatement pstmt = null;

   try {

      pstmt = con.prepareStatement (
               "UPDATE evntsup2b SET player1 = ?, username1 = ? WHERE username1 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE evntsup2b SET player2 = ?, username2 = ? WHERE username2 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE evntsup2b SET player3 = ?, username3 = ? WHERE username3 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE evntsup2b SET player4 = ?, username4 = ? WHERE username4 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE evntsup2b SET player5 = ?, username5 = ? WHERE username5 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();
/**     
 * These fields no longer exist in the table 9-4-08 PTS
 
      pstmt = con.prepareStatement (
               "UPDATE evntsup2b SET player6 = ?, username6 = ? WHERE username6 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE evntsup2b SET player7 = ?, username7 = ? WHERE username7 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE evntsup2b SET player8 = ?, username8 = ? WHERE username8 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE evntsup2b SET player9 = ?, username9 = ? WHERE username9 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE evntsup2b SET player10 = ?, username10 = ? WHERE username10 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();
*/
   }
   catch (Exception ignore) {
   }

  }


  /**
  ***************************************************************************************
  *
  * This method will change the name and/or username in the Lesson Book db tables
  *
  ***************************************************************************************
  **/

  public static void updLessons(String newName, String newUser, String oldUser, Connection con) {

   PreparedStatement pstmt = null;

   try {

      pstmt = con.prepareStatement (
               "UPDATE lessonbook5 SET memname = ?, memid = ? WHERE memid = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE lgrpsignup5 SET memname = ?, memid = ? WHERE memid = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

   }
   catch (Exception ignore) {
   }

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

  private void updateFormAndReturnUser(FormModel editMemberForm, FeedBack feedback, HttpServletRequest req,HttpServletResponse resp, HttpSession session, PrintWriter out)
  {
    if (editMemberForm != null)
    {
      editMemberForm.update(req, resp, out);
      session.setAttribute(Member.EDIT_MEM_FRM, editMemberForm);
      session.setAttribute(Member.MEM_FEEDBACK, feedback);

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
    session.removeAttribute(Member.EDIT_MEM_FRM);
    session.removeAttribute(Member.MEM_FEEDBACK);
    String letter = req.getParameter(Member.LETTER);
    String memlistBookmark = "";        // Used to pass a bookmark link back to the memlist so position on the list is retained
    if (req.getParameter(Member.REQ_USER_NAME) != null && !req.getParameter(Member.REQ_USER_NAME).equals("")) {
        memlistBookmark = "#" + req.getParameter(Member.REQ_USER_NAME);
    }
    return (versionId + "servlet/Admin_memlist?" + Member.LETTER + "=" + letter + memlistBookmark);
  }


}