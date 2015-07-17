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
 *        9/12/13   Added support for updating the flexid value when submitting a member record.
 *       11/26/12   Added updDemoClubs to update username when a member's username changes.
 *        8/30/12   Reworked updDiningDB, usernames can be updated in ForeTees and will update in the Dining system as well
 *        4/12/12   Moved updDiningDB() call in updateMem, since it was getting called before the member's record had been updated, resulting in information lag between the two systems.
 *        2/09/12   Updated updDiningDB so it will not overwrite peope.suffix or people.prefix when updating member records (new records will add those still)
 *        9/01/11   Updated updDiningDB to close the dining db connection at the end of processing.
 *        8/30/11   Updated updDiningDB so that it also creates the associated "user" record for the member in the dining db
 *        6/22/11   Updated updTeecurr() method to build the member's tflag value and update the tflag value for their tee times as well.
 *        1/20/11   Added processing to information to the dining system database when a member is added in ForeTees as long as the club has an organization_id defined.
 *        1/20/11   Added remDiningMem() method to remove a member from the dining system database when that member is removed from ForeTees.
 *        1/20/11   Added updDiningDB() method to add/update the dining system database with any changes/additions to member information within ForeTees.
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
import java.util.*;
//import javax.sql.*;
//import javax.naming.*;

//foretees imports
import com.foretees.client.action.ActionHelper;
import com.foretees.client.form.FormModel;
import com.foretees.common.FeedBack;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;
import com.foretees.common.verifySlot;
import com.foretees.member.Member;
import com.foretees.common.BasicSHA256;
import com.foretees.common.parmDining;
import com.foretees.common.Connect;

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

    Connection con = Connect.getCon(req);            // get DB connection

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
    int member_id = 0;
    
    String lname = "";
    String fname = "";
    String mname = "";
    String newName = "";
    String webid = "";
    String flexid = "";
    
    try {

      if (Utilities.getOrganizationId(con) > 0) {
          remDiningMem(h_user, con);
      }

      PreparedStatement stmt = null;
      ResultSet rs = null;
      
      //  Get the member_id for this member so we can create a new username
      
      stmt = con.prepareStatement (
                "SELECT id, name_last, name_first, name_mi, webid, flexid " +
                "FROM member2b WHERE username = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, h_user);
      rs = stmt.executeQuery();           

      if (rs.next()) {

          member_id = rs.getInt("id");            // get the member_id value
          lname = rs.getString("name_last");      // get member's name
          fname = rs.getString("name_first");
          mname = rs.getString("name_mi");
          webid = rs.getString("webid");
          flexid = rs.getString("flexid");
      }
      stmt.close();
       
      String newUser = "XYZ" +member_id;    // create new username
      
      if (!webid.equals("")) {
          
          webid = "XYZ" +webid;             //    and web ids to prevent dups and rsync failures
      }

      if (!flexid.equals("")) {
          
          flexid = "XYZ" +flexid;
      }
       
      //
      //  Change the username and set this member as deleted (do not actually delete the record so we don't lose login counts and other report data)!!!!
      //
      stmt = con.prepareStatement (
                "UPDATE member2b SET username = ?, inact = 1, billable = 0, deleted = 1, webid = ?, flexid = ? " +
                "WHERE username = ?");

      stmt.clearParameters();             
      stmt.setString(1, newUser);            // set new username
      stmt.setString(2, webid);
      stmt.setString(3, flexid);
      stmt.setString(4, h_user);
       
      stmt.executeUpdate();

      stmt.close();
       
      //
      //  Change the username in other tables for this member
      //
      StringBuffer mem_name = new StringBuffer( fname );       // get the new first name

      if (!mname.equals( "" )) {
           mem_name.append(" " +mname);                       // new mi
      }
      mem_name.append(" " +lname);                            // new last name

      newName = mem_name.toString();                          // convert to one string

      updTeecurr(newName, newUser, h_user, con);      // update teecurr with new values

      updTeepast(newName, newUser, h_user, con);      // update teepast with new values

      updLreqs(newName, newUser, h_user, con);        // update lreqs with new values

      updEvents(newName, newUser, h_user, con);       // update evntSignUp with new values

      updLessons(newName, newUser, h_user, con);      // update the lesson books with new values

      updDemoClubs(newUser, h_user, con);             // update demo_clubs_usage with new username

      
      /*
      stmt = con.prepareStatement (
            "DELETE FROM member2b WHERE username = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, h_user);            // put the parm in stmt
      stmt.executeUpdate();                 // execute the prepared stmt

      stmt.close();
      * 
      */

      //
      //   Remove this member from some tables where it is no longer needed
      //
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

      // Delete this member's partner list, as well as from all other partner lists
      stmt = con.prepareStatement(
            "DELETE FROM guestdb_hosts WHERE username = ?");
      stmt.clearParameters();
      stmt.setString(1, h_user);
      stmt.executeUpdate();

      stmt.close();
      
      updDistLists("", h_user, true, con);

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
    int organization_id = 0;
    float course = -99;     // default hndcp's
    float usga = -99;

    String club = (String)session.getAttribute("club");
    int rsync = (Integer)session.getAttribute("rsync");          // get Roster Sync indicator for this club

    Connection con = Connect.getCon(req);            // get DB connection

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
     
     String flexId = "";
     
     if (req.getParameter(member.FLEXID) != null) {
         flexId = SystemUtils.scrubString(req.getParameter(member.FLEXID));
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
                   "SELECT inact FROM member2b WHERE username != ? AND name_last = ? AND name_first = ? AND name_mi = ? AND deleted = 0");

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

          updDemoClubs(user, old_user, con);               // update demo_clubs_usage with new username
          
          updDistLists(user, old_user, false, con);       // update distribution lists with the new username
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
               "phone2 = ?, name_pre = ?, name_suf = ?, webid = ?, flexid = ?, hdcp_club_num_id = ?, hdcp_assoc_num_id = ?, inact = ?, gender = ?, tflag = ?, " +
               "ntrp_rating = ?, usta_num = ? " +
               "WHERE username = ?");

      } else {     // Roster Sync - set billing indicator (excluded)

         stmt = con.prepareStatement (
               "UPDATE member2b SET username = ?, password = ?, name_last = ?, name_first = ?, " +
               "name_mi = ?, m_ship = ?, m_type = ?, email = ?, c_hancap = ?, g_hancap = ?, wc = ?, " +
               "memNum = ?, ghin = ?, locker = ?, bag = ?, birth = ?, posid = ?, msub_type = ?, email2 = ?, phone1 = ?, " +
               "phone2 = ?, name_pre = ?, name_suf = ?, webid = ?, flexid = ?, hdcp_club_num_id = ?, hdcp_assoc_num_id = ?, inact = ?, gender = ?, tflag = ?, " + 
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
      stmt.setString(25, flexId);
      stmt.setString(26, hdcpClubNum);
      stmt.setString(27, hdcpAssocNum);
      stmt.setInt(28, inact);
      stmt.setString(29, gender);
      stmt.setString(30, tFlag);
      stmt.setFloat(31, ntrp_rating);
      stmt.setString(32, usta_num);

      if (rsync > 0) {            // if club does not use Roster Sync (DO NOT alter the billable flag if no RS - see Support_billing)
         billable = 1;             // default = billable
         if (exclude == 1) {       // if member is to be excluded (not billable)
            billable = 0;          // NOT billable
         }
         stmt.setInt(33, billable);
         stmt.setString(34, old_user);
      } else {
         stmt.setString(33, old_user);
      }
       
      stmt.executeUpdate();

      stmt.close();
       
      // If organization_id is greater than 0, Dining system is in use.  Push updates to this member's record over to the dining system database
      if (Utilities.getOrganizationId(con) > 0) {
          
          updDiningDB(user, con);
      }

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
  * This method will change the name, username, and tflag in the teecurr2 db table
  *
  ***************************************************************************************
  **/
    
  public static void updTeecurr(String newName, String newUser, String oldUser, Connection con) {

   PreparedStatement pstmt = null;

   String tflag = "";

   // Get the tflag value for this member to be updated in their current tee times.
   tflag = verifySlot.getTflag(newUser, con);

   try {

      pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET player1 = ?, username1 = ?, tflag1 = ? WHERE username1 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, tflag);
      pstmt.setString(4, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET player2 = ?, username2 = ?, tflag2 = ? WHERE username2 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, tflag);
      pstmt.setString(4, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET player3 = ?, username3 = ?, tflag3 = ? WHERE username3 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, tflag);
      pstmt.setString(4, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET player4 = ?, username4 = ?, tflag4 = ? WHERE username4 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, tflag);
      pstmt.setString(4, oldUser);
      pstmt.executeUpdate();

      pstmt.close();

      pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET player5 = ?, username5 = ?, tflag5 = ? WHERE username5 = ?");

      pstmt.clearParameters();
      pstmt.setString(1, newName);
      pstmt.setString(2, newUser);
      pstmt.setString(3, tflag);
      pstmt.setString(4, oldUser);
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
   * updDemoClubs - Updates the demo_clubs_usage db table with any username changes
   *
   * @param user_id_new User's new username
   * @param user_id_old User's old username
   * @param con Connection to club database
   */
  public static void updDemoClubs(String user_id_new, String user_id_old, Connection con) {

      PreparedStatement pstmt = null;

      try {

          // Update user_id for all of this user's demo clubs 
          pstmt = con.prepareStatement("UPDATE demo_clubs_usage SET username = ? WHERE username = ?");
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
  
  public static void updDistLists(String newUser, String oldUser, boolean remove, Connection con) {
      
      PreparedStatement pstmt = null;
      
      if (remove) {
          
          if (!oldUser.equals("")) {
           
              try {
                  
                  pstmt = con.prepareStatement("DELETE FROM distribution_lists_entries WHERE username = ?");
                  pstmt.clearParameters();
                  pstmt.setString(1, oldUser);
                  
                  pstmt.executeUpdate();
                  
              } catch (Exception e) {
                  Utilities.logError("Admin_editmem.updDistList - Error removing distribution list entries for user: " + oldUser + " - Error: " + e.toString());
              } finally {
                  Connect.close(pstmt);
              }
          }
          
      } else {
          
          if (!newUser.equals("") && !oldUser.equals("")) {
              
              try {
                  
                  pstmt = con.prepareStatement("UPDATE distribution_lists_entries SET username = ? WHERE username = ?");
                  pstmt.clearParameters();
                  pstmt.setString(1, newUser);
                  pstmt.setString(2, oldUser);
                  
                  pstmt.executeUpdate();
                  
              } catch (Exception e) {
                  Utilities.logError("Admin_editmem.updDistList - Error updating distribution list entries for user. newUser: " + newUser + ", oldUser: " + oldUser + " - Error: " + e.toString());
              } finally {
                  Connect.close(pstmt);
              }
          }
      }
  }

  /**
   * updDiningDB - Pushes changes from ForeTees database over to the Dining database after member updated on admin side or via roster sync
   *
   * @param username username to push changes over for
   */
  public static void updDiningDB(String username, Connection con) {

      PreparedStatement pstmt = null;
      PreparedStatement pstmt2 = null;
      //Statement stmt = null;
      ResultSet rs = null;
      Connection con_d = null;

      int organization_id = Utilities.getOrganizationId(con);

      if (organization_id > 0) {

          String club = Utilities.getClubDbName(con);

          con_d = Connect.getDiningCon();

          if (con_d != null) {
              
              String prefix = "";
              String last_name = "";
              String first_name = "";
              String middle_name = "";
              String suffix = "";
              String email_address = "";
              String home_phone = "";
              String work_phone = "";
              String mobile_phone = "";
              String custom_string = "";
              String custom_string2 = "";
              String mobilePhoneQuery = " ";

              int birth = 0;
              int yy = 0;
              int mm = 0;
              int dd = 0;
              int temp = 0;
              int emailOpt = 0;
              int dining_id = 0;
              int count = 0;
              int inact = 0;

              long tempdate = 0;

              boolean emailOptBool = false;
              boolean syncMobilePhone = false;

              java.util.Date bday = null;
              java.sql.Date birth_date = null;

              Calendar cal = new GregorianCalendar();

              try {

                  // Gather member data from member2b (this is to be performed AFTER changes have already been applied to member2b table
                  pstmt = con.prepareStatement(
                          "SELECT name_pre, name_last, name_first, name_mi, name_suf, email, emailOpt, phone1, phone2, birth, custom_string, custom_string2, inact, "
                          + "IF(dining_id = NULL, 0, dining_id) as dining_id_val "
                          + "FROM member2b "
                          + "WHERE username = ?");
                  pstmt.clearParameters();
                  pstmt.setString(1, username);

                  rs = pstmt.executeQuery();

                  if (rs.next()) {

                      prefix = rs.getString("name_pre");
                      last_name = rs.getString("name_last");
                      first_name = rs.getString("name_first");
                      middle_name = rs.getString("name_mi");
                      suffix = rs.getString("name_suf");
                      email_address = rs.getString("email");
                      emailOpt = rs.getInt("emailOpt");
                      home_phone = rs.getString("phone1");
                      work_phone = rs.getString("phone2");
                      birth = rs.getInt("birth");
                      custom_string = rs.getString("custom_string");
                      custom_string2 = rs.getString("custom_string2");
                      inact = rs.getInt("inact");
                      dining_id = rs.getInt("dining_id_val");

                      if (emailOpt == 1) {
                          emailOptBool = true;
                      } else {
                          emailOptBool = false;
                      }
                      
                      if (club.equals("mediterra")) {    // Mediterra wants to reroute where their phone numbers are ending up in dining
                          syncMobilePhone = true;
                          mobile_phone = rs.getString("phone1");
                          home_phone = rs.getString("phone2");
                          work_phone = rs.getString("custom_string");
                      }

                      if (birth > 0) {

                          yy = birth / 10000;
                          temp = yy * 10000;
                          mm = birth - temp;
                          temp = mm / 100;
                          temp = temp * 100;
                          dd = mm - temp;
                          mm = mm / 100;

                          cal.set(yy, mm - 1, dd);

                          bday = cal.getTime();

                          tempdate = bday.getTime();

                          birth_date = new java.sql.Date(tempdate);

                      } else {
                          bday = null;
                          birth_date = null;
                      }
                  }

              } catch (Exception exc) {

              } finally {

                  try { rs.close(); }
                  catch (Exception ignore) {}

                  try { pstmt.close(); }
                  catch (Exception ignore) {}

              }

              if (syncMobilePhone) {
                  mobilePhoneQuery = ", mobile_phone = ? ";
              }

              if (dining_id > 0) {      // Member already has a record in the Dining db, update it with the info from ForeTees member2b

                  try {

                      pstmt = con_d.prepareStatement(
                              "UPDATE people "
                              //"SET prefix = ?, last_name = ?, first_name = ?, middle_name = ?, suffix = ?, " +
                              + "SET last_name = ?, first_name = ?, middle_name = ?, "
                              + "home_phone = ?, work_phone = ?, email_address = ?, birth_date = ?, auto_email_for_dining_reservations = ?, "
                              + "auto_email_for_event_reservations = ?, membership_status_id = ?"
                              + mobilePhoneQuery 
                              + "WHERE id = ? "
                              + "RETURNING *");
                      pstmt.clearParameters();
                      /*
                      pstmt.setString(1, prefix);
                      pstmt.setString(2, last_name);
                      pstmt.setString(3, first_name);
                      pstmt.setString(4, middle_name);
                      pstmt.setString(5, suffix);
                      pstmt.setString(6, home_phone);
                      pstmt.setString(7, work_phone);
                      pstmt.setString(8, email_address);
                      pstmt.setDate(9, birth_date);
                      pstmt.setBoolean(10, emailOptBool);
                      pstmt.setBoolean(11, emailOptBool);
                      pstmt.setInt(12, dining_id);
                      */
                      int i = 1;
                      pstmt.setString(i++, last_name);
                      pstmt.setString(i++, first_name);
                      pstmt.setString(i++, middle_name);
                      pstmt.setString(i++, home_phone);
                      pstmt.setString(i++, work_phone);
                      pstmt.setString(i++, email_address);
                      pstmt.setDate(i++, birth_date);
                      pstmt.setBoolean(i++, emailOptBool);
                      pstmt.setBoolean(i++, emailOptBool);
                      pstmt.setInt(i++, ((inact == 1) ? 1 : 2));
                      if (syncMobilePhone) {
                          pstmt.setString(i++, mobile_phone);
                      }
                      pstmt.setInt(i++, dining_id);


                      //count = pstmt.executeUpdate();
                      rs = pstmt.executeQuery();

                      if (rs.next()) {

                          prefix = rs.getString("prefix");
                          last_name = rs.getString("last_name");
                          first_name = rs.getString("first_name");
                          middle_name = rs.getString("middle_name");
                          suffix = rs.getString("suffix");

                      }

                      pstmt.close();

                      // UPDATE THE user_identity FIELD IN THE people TABLE
                      pstmt = con_d.prepareStatement(
                              "UPDATE people " +
                              "SET user_identity = ? " +
                              "WHERE id = ?");
                      pstmt.clearParameters();
                      pstmt.setString(1, parmDining.buildUserIdentity(username, prefix, last_name, first_name, middle_name, suffix));
                      pstmt.setInt(2, dining_id);
                      pstmt.executeUpdate();

                      pstmt.close();

                      // UPDATE THE username FIELD IN THE users TABLE
                      pstmt = con_d.prepareStatement(
                              "UPDATE users " +
                              "SET username = ? " +
                              "WHERE person_id = ?");
                      pstmt.clearParameters();
                      pstmt.setString(1, username);
                      pstmt.setInt(2, dining_id);
                      pstmt.executeUpdate();


                  } catch (Exception exc) {

                      Utilities.logError("Error updating dining record for " + last_name + ", " + first_name + " (" + dining_id + "): " + exc.getMessage());

                  } finally {

                      try { rs.close(); }
                      catch (Exception ignore) {}

                      try { pstmt.close(); }
                      catch (Exception ignore) {}

                  }

              } else if (inact == 0) {   // No record exists for this member in the Dining db, insert a new record and populate the dining_id field in ForeTees member2b

                  int address_id = 0;

                  try {

                      // add an address table entry for this person and get its id back
                      pstmt = con_d.prepareStatement("" +
                               "INSERT INTO addresses (" +
                                   "created_at, updated_at, lock_version, street_address, city, state_id, zip_or_postal_code, country, imported" +
                               ") VALUES (" +
                                   "now(), now(), 0, '', '', NULL, '', 'United States', FALSE" +
                               ") RETURNING id");

                      pstmt.clearParameters();
                      rs = pstmt.executeQuery();

                      if (rs.next()) {

                          address_id = rs.getInt(1);

                      }

                      pstmt.close();

                      String user_identity = parmDining.buildUserIdentity(username, prefix, last_name, first_name, middle_name, suffix);

                      /*
                      String user_identity = username + ":" + " " + last_name + ", " + first_name;

                      int pos = user_identity.indexOf(":");

                      for (int i=pos; i < 10; i++) {

                          user_identity = " " + user_identity;
                      }
                      */
                      pstmt = con_d.prepareStatement("INSERT INTO people (" +
                              "created_at, updated_at, lock_version, prefix, last_name, first_name, middle_name, suffix, occupation, home_phone, " +
                              "work_phone, work_extension, mobile_phone, email_address, birth_date, other_is_spouse, other_last_name, other_first_name, other_middle_name, " +
                              "organization_id, food_preference, cocktail_preference, special_requests, auto_email_for_dining_reservations, user_identity, member, " +
                              "auto_email_for_event_reservations, lottery_multiplyer, address_id" +
                              ") VALUES (" +
                              "now(),now(),1,?,?,?,?,?,'',?," +
                              "?,'',?,?,?,FALSE,'','',''," +
                              "?,'','','',?,?,TRUE," +
                              "?,0,?" +
                              ") RETURNING id");

                      int i = 1;
                      pstmt.clearParameters();
                      pstmt.setString(i++, prefix);
                      pstmt.setString(i++, last_name);
                      pstmt.setString(i++, first_name);
                      pstmt.setString(i++, middle_name);
                      pstmt.setString(i++, suffix);
                      pstmt.setString(i++, home_phone);
                      pstmt.setString(i++, work_phone);
                      pstmt.setString(i++, mobile_phone);
                      pstmt.setString(i++, email_address);
                      pstmt.setDate(i++, birth_date);
                      pstmt.setInt(i++, organization_id);
                      pstmt.setBoolean(i++, emailOptBool);
                      pstmt.setString(i++, user_identity);
                      pstmt.setBoolean(i++, emailOptBool);
                      pstmt.setInt(i++, address_id);

                      //count = pstmt.executeUpdate();

                      rs = pstmt.executeQuery();

                      if (rs.next()) {

                          dining_id = rs.getInt(1);

                      }

                  } catch (Exception e2) {

                      Utilities.logError("Error inserting dining record for " + last_name + ", " + first_name + ": " + e2.getMessage());

                  } finally {

                      try { rs.close(); }
                      catch (Exception ignore) {}

                      try { pstmt.close(); }
                      catch (Exception ignore) {}

                  }

                  // If insert was successful, get dining_id from record and populate it into member2b record
                  if (dining_id > 0) { // was using count var

                      try {
                          /*
                          pstmt = con_d.prepareStatement("SELECT id FROM people WHERE organization_id = ? AND last_name = ? AND first_name = ? AND middle_name = ? AND email_address = ? AND home_phone = ? AND work_phone = ?");
                          pstmt.clearParameters();
                          pstmt.setInt(1, organization_id);
                          pstmt.setString(2, last_name);
                          pstmt.setString(3, first_name);
                          pstmt.setString(4, middle_name);
                          pstmt.setString(5, email_address);
                          pstmt.setString(6, home_phone);
                          pstmt.setString(7, work_phone);

                          rs = pstmt.executeQuery();

                          if (rs.next()) {

                              dining_id = rs.getInt("id");

                              try {*/
                           String password_salt = BasicSHA256.getSalt(6);
                           String password_hash = BasicSHA256.SHA256(last_name + password_salt);

                           // Now add this member to the "users" table
                           pstmt = con_d.prepareStatement("" +
                                   "INSERT INTO users (" +
                                       "created_at, updated_at, lock_version, username, password_salt, password_hash, role_id, person_id, login_allowed" +
                                   ") VALUES (" +
                                       "now(), now(), 1, ?, ?, ?, 6, ?, TRUE" +
                                   ")");

                           pstmt.clearParameters();
                           pstmt.setString(1, username);
                           pstmt.setString(2, password_salt);
                           pstmt.setString(3, password_hash);
                           pstmt.setInt(4, dining_id);

                           pstmt.executeUpdate();

                      } catch (Exception e3) {

                          Utilities.logError("Error inserting user record in dining db for dining_id=" + dining_id + ", err=" + e3.toString());
                      
                      } finally {

                          try { pstmt.close(); }
                          catch (Exception ignore) {}

                      }

                      try {
                          pstmt = con.prepareStatement("UPDATE member2b SET dining_id = ? WHERE username = ?");
                          pstmt.clearParameters();
                          pstmt.setInt(1, dining_id);
                          pstmt.setString(2, username);

                          pstmt.executeUpdate();

                      } catch (Exception e3) {

                          Utilities.logError("Error updating member2b dining_id: " + e3.toString());
                      
                      } finally {

                          try { pstmt.close(); }
                          catch (Exception ignore) {}

                      }/*
                          }

                      } catch (Exception e2) {
                          Utilities.logError("Error getting dining_id: " + e2.toString());
                      }*/
                  
                  } // end if user added to people table

              } // end if updating or adding a dining user

              try { con_d.close(); }
              catch (Exception exc) {
                  Utilities.logError("Admin_editmem - updDiningDb - Error closing Dining DB connection!");
              }

          } // end if con_d not null

      } // end if club is using dining

  }
  
  /**
   * Pushes changes from ForeTees side to Dining for all members with a dining_id configured
   * 
   * @param con Connection to club database
   */
  public static void updAllDiningDB(Connection con) {
      
      Statement stmt = null;
      ResultSet rs = null;
      
      String club = "";
      
      try {
          
          club = Utilities.getClubDbName(con);      // get club's site name
     
          stmt = con.createStatement();
          
          rs = stmt.executeQuery("SELECT username FROM member2b WHERE dining_id IS NOT NULL");
          
          while (rs.next()) {
              updDiningDB(rs.getString("username"), con);
          }
          
      } catch (Exception e) {
          Utilities.logError("Admin_editmem.updAllDiningDB - " + club + " - Failed to push updates for all FT members to Dining - Error=" + e.toString());
      } finally {
          Connect.close(rs, stmt);
      }
  }

  /**
   * remDiningMem - Attempts to remove a member from Dining database
   *
   * @param username Username of member to remove
   * @param con Connection to club database
   */
  public static void remDiningMem(String username, Connection con) {

      PreparedStatement pstmt = null;
      ResultSet rs = null;
      Connection con_d = null;

      int dining_id = 0;
      int count = 0;

      int organization_id = Utilities.getOrganizationId(con);

      // See if this member has a dining_id
      try {
          pstmt = con.prepareStatement("SELECT dining_id FROM member2b WHERE username = ?");
          pstmt.clearParameters();
          pstmt.setString(1, username);

          rs = pstmt.executeQuery();

          if (rs.next()) {
              dining_id = rs.getInt("dining_id");
          }

          pstmt.close();

      } catch (Exception exc) {
          dining_id = 0;
      }

      // If dining_id is greater than 0, that means this member has a corresponding member entry in the Dining DB, remove this entry as well
      if (dining_id > 0) {

          // Get connection to dining database
          con_d = Connect.getDiningCon();

          if (con_d != null) {

              try {
                  pstmt = con_d.prepareStatement("DELETE FROM people WHERE id = ?");
                  pstmt.clearParameters();
                  pstmt.setInt(1, dining_id);

                  count = pstmt.executeUpdate();

                  pstmt.close();
                  
              } catch (Exception exc) {
                  Utilities.logError("Error deleting member from Dining DB. dining_id: " + dining_id);
              }
          }

          try { con_d.close(); }
          catch (Exception exc) {
              Utilities.logError("Admin_editmem - remDiningMem - Error closing Dining DB connection!");
          }
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
