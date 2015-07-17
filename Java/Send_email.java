/***************************************************************************************
 *   Send_email:  This servlet allows users to create an email and pick from
 *                distribution lists or members to send it to
 *
 *
 *   created: 1/14/2004   JAG
 *
 *   last updated: 
 *
 *       6/08/10  Brooklawn CC (brooklawn) - Re-added case 1737.  Must have gotten deleted accidentally at some point.
 *       6/07/10  Interlachen - remove a pro's email address from the CC list.
 *       5/27/10  Add a notice for attachements regarding excel files (requet they send pdf files).
 *       5/19/10  Rolling Hills GC (rollinghillsgc) - Do not print header/footer on proshop emails
 *       5/12/10  FlxRez - make sure we are using the correct 'from' address for the proshop
 *       3/16/10  Notify the user if the attachment(s) were lost due to the page reloading
 *       3/02/10  Emails are now queued after displaying message to the user.
 *       2/19/10  Updated to allow attachments and is now using common/sendEmail.doSending for delivery
 *       2/10/09  Use custom message header for Royal Oaks Dallas (Same as hillcrest and pinehills)
 *      12/04/09  Updated getUserNamesFromPartnerList() method to grab partners from the new partner table
 *      11/03/09  Add check for Activities user when sending backup time sheets to pro.
 *      10/20/09  Brooklawn - CC admin person on every email sent by a proshop user (case 1737).
 *      10/01/09  Add processing for sending emails from pro Activity Sheet.
 *       9/03/09  Cleaned up the database commands - everything now closes when done
 *       9/03/09  Merrill Hills - disable access for member '9527' and add no spam message to member side - Case #1721
 *       8/28/09  TPC at Potomac - CC all pros on every email sent by a proshop user (case 1719).
 *       8/26/09  Interlachen - CC all pros on every email sent by a proshop user (case 1715).
 *       7/30/09  Minneapolis restore the eamil feature.
 *       6/25/09  Misc typo fix
 *       5/13/09  Portage CC - Changed standard message to use their custom verbiage
 *       4/29/09  Use different sender address and message content for emails sent by any username starting with "proshopfb" (case 1641).
 *       4/24/09  Treesdale Golf - Don't display Ben Roethlisberger's email in member selection window, adding to dist. lists,
 *                and don't import from partner list for members only (case 1660).
 *       2/10/09  Fountain Grove Golf - Disable email tool from member side
 *       9/25/08  Hillcrest WI - custom message header for proshop2 user (case 1497).
 *       9/02/08  Adjusted Limited Access checks so only TOOLS_EMAIL restriction is checked
 *       9/02/08  Add check for no course name in getUserNamesFromTeeSheet.
 *       8/01/08  Fixed returning to correct _announce after sending email
 *       7/18/08  Added limited access proshop users checks
 *       7/14/08  Do not include excluded members (billable = 0) in email lists.
 *       6/12/08  Fix the order of body tag, table tag and menus in drawBeginningOfPageBeforeForm.
 *       6/03/08  Updated instructions on member side and added SessionSaver feature
 *       5/13/08  Shadow Ridge CC - set max_bcc to 50
 *       5/08/08  Forest Hills GC - custom message header for proshop4
 *       4/24/08  Fix reply-to issue
 *       3/10/08  Added support for external mail servers
 *      10/31/07  Add new call to SystemUtils.tsheetTimer(club) for send backup tee sheet to the pro
 *      10/11/07  Pine Hills - add custom trailer message in emails from Pro.
 *      10/03/07  Troon CC - Disable email tool from member side (Case #1279)
 *       9/07/07  Add password protection to email feature on pro side. (Case #1233)
 *       8/22/07  New Canaan - Block proshop users from using the email feature. (TEMP until password protection implemented)
 *       5/31/07  Added fortcollins checks to outgoing emails to remove club name from subject & message body
 *       5/10/07  Added sender information to top of message.
 *       4/06/07  Do not include members that are inactive (new inact flag in member2b).
 *      10/07/06  Changes for bounced email address flagging
 *       7/19/06  Hillcrest WI - add custom trailer message in emails from Pro.
 *       5/23/06  Unfilter special chars from the clubname for the subject line.
 *       5/18/06  Move AOL message to end (trailer) of main message.
 *       5/10/06  Minneapolis restore the eamil feature.
 *       9/29/05  For Minneapolis members, disable the eamil feature for the rest of 2005.
 *       8/29/05  For members, use canned 'From' address to prevent send failures (forging).
 *       5/20/05  For proshop, use canned 'From' address to prevent send failures (forging).
 *      10/06/04  Ver 5 - add submenu support for Members.
 *       3/08/04  RDP Remove restriction of 100 recipients for proshop user.
 *                Send email to 100 recipients at a time when more than 100 requested.
 *                This is a mail server restriction.
 *       1/25/04  RDP Add logic to extract usernames from buddy table.
 *       1/24/04  RDP Add logic to query the distribution lists (dist4 table).
 *       1/08/05  JAG Add method to process members on a particular tee sheet
 *
 ***************************************************************************************
 */

//third party imports
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
//import org.apache.commons.io.*;


//foretees imports
import com.foretees.client.ScriptHelper;
import com.foretees.client.action.Action;
import com.foretees.client.action.ActionModel;
import com.foretees.client.action.ActionHelper;
import com.foretees.client.attribute.Attribute;
import com.foretees.client.attribute.TextBox;
import com.foretees.client.attribute.FileInput;
import com.foretees.client.form.FormModel;
import com.foretees.client.form.FormRenderer;
import com.foretees.client.layout.Separator;
import com.foretees.client.layout.LayoutHelper;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.client.table.Cell;
import com.foretees.client.table.Column;
import com.foretees.client.table.RowModel;
import com.foretees.client.table.TableModel;
import com.foretees.common.FeedBack;
import com.foretees.common.Labels;
import com.foretees.common.ProcessConstants;
import com.foretees.common.parmSMTP;
import com.foretees.common.getSMTP;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.communication.DistributionList;
import com.foretees.communication.Email;
import com.foretees.event.Event;

//import com.foretees.client.attribute.SelectionList;
//import com.foretees.client.misc.LetterChooser;
//import com.foretees.client.misc.NameSelector;
//import com.foretees.common.help.Help;
//import com.foretees.communication.CommunicationHelper;

/**
***************************************************************************************
*
* This servlet will display and process a page to send an email to a selected set of members
*
***************************************************************************************
**/

public class Send_email extends HttpServlet {

    @SuppressWarnings("unchecked")

   //********************************************************
   // Some constants for emails sent within this class
   //********************************************************
   //
   static String host = ProcessConstants.HOST;
   static String port = ProcessConstants.PORT;

   //initialize the attributes
   private static String versionId = ProcessConstants.CODEBASE;

  /**
  ***************************************************************************************
  *
  * This method will process the request to send email messages from both members and pros
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
       return;
    }

    //
    //  ***** get user id so we know if proshop or member
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
    String caller = (String)session.getAttribute("caller");     // get caller (web site?)
    String club = (String)session.getAttribute("club");

    //
    // See what activity mode we are in
    //
    int sess_activity_id = 0;

    try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
    catch (Exception ignore) { }


    // see if we are here to send out a backup tee sheet
    if (req.getParameter("backup") != null && ProcessConstants.isProshopUser(user)) {

        SystemUtils.tsheetTimer(club, out);
        out.println(SystemUtils.HeadTitle("Send ForeTees Backup Sheets"));
        out.println("<BODY><CENTER>");
        
        if (sess_activity_id == 0) {    // if Golf
           
           out.println("<BR><H2>Send Backup Tee Sheet</H2><BR>");
           out.println("<BR><BR>We have sent todays tee sheet to you in an email.<BR>");
           out.println("<BR>Normally you should recieve the email within a few minutes, however if you");
           out.println("<BR>do not receive it within 15 minutes there may be a problem.");
           out.println("<BR><BR>Make sure you have an email address specified and have choosen to receive ");
           out.println("<BR>backup tee sheets under System Config | Club Setup | Club Options.");
           out.println("<BR>If it ends up in your junk or spam folder you can try adding ");
           out.println("<BR>teesheets@foretees.com to your Contacts / Address Book in your email program.");
           
        } else {       // if Activity
        
           out.println("<BR><H2>Send Backup Reservation Sheets</H2><BR>");
           out.println("<BR><BR>We have sent todays time sheet(s) to you in an email.<BR>");
           out.println("<BR>Normally you should recieve the email within a few minutes, however if you");
           out.println("<BR>do not receive it within 15 minutes there may be a problem.");
           out.println("<BR><BR>Make sure you have an email address specified and have choosen to receive ");
           out.println("<BR>backup time sheets under System Config | Define Activities.");
           out.println("<BR>If it ends up in your junk or spam folder you can try adding ");
           out.println("<BR>timesheets@foretees.com to your Contacts / Address Book in your email program.");
        }
        out.println("<BR><BR>");
        out.println("<a href=\""  + versionId +  "servlet/Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }

    //get the database connection
    Connection con = SystemUtils.getCon(session);            // get DB connection

    //if this is a member, check to see if the user has registered an email address in the system, if not
    //they cannot use the email feature.  Let them know and give them a link the Settings
    //page to add one.

    if (!ProcessConstants.isProshopUser(user) && !ProcessConstants.isAdminUser(user))
    {

      //
      // USER IS A MEMBER
      //
      if (!MemberHelper.hasEmailAddress(user, con, out))
      {

         String settingsUrl = "servlet/Member_services";

         out.println(SystemUtils.HeadTitleAdmin(Email.SEND_EMAIL_LIST_HEADER));
         out.println("<BODY>");
         SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
         out.println("<CENTER><BR><BR>To use the email feature you must first register an email address with the system.<BR>");
         out.println("<BR>Use the ");
         out.print("<a href=\""  + versionId +  settingsUrl + "\" target=\"bot\">Settings</a>");
         out.print(" page to enter your email address.</CENTER><BR><BR></BODY>");

         drawEndOfPageAfterForm(out);
         return;


      } else {         // member ok - check if MPLS GC and if so do not allow them to use email (temp)

         /*
         if (club.equals( "minneapolis" )) {

            out.println(SystemUtils.HeadTitleAdmin("Service Suspended Notification"));
            out.println("<BODY>");
            SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
            out.println("<CENTER><BR><BR>Sorry, but the email feature has been temporarily disabled.<BR>");
            out.print("</CENTER><BR><BR></BODY>");

            drawEndOfPageAfterForm(out);
            return;

         } else if (club.equals( "trooncc" ) || club.equals( "fountaingrovegolf" )) {
          */
            
         if (club.equals( "trooncc" ) || club.equals( "fountaingrovegolf" )) {

            out.println(SystemUtils.HeadTitleAdmin("Service Suspended Notification"));
            out.println("<BODY>");
            SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
            out.println("<CENTER><BR><BR>Sorry, but the email feature is disabled for your club.<BR>");
            out.print("</CENTER><BR><BR></BODY>");

            drawEndOfPageAfterForm(out);
            return;

         }/* else if (club.equals( "merrillhills" ) && user.equals( "9527" )) {

            out.println(SystemUtils.HeadTitleAdmin("Service Suspended Notification"));
            out.println("<BODY>");
            SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
            out.println("<CENTER><BR><BR>Sorry, but the email feature has been disabled for your account.<BR>");
            out.print("</CENTER><BR><BR></BODY>");

            drawEndOfPageAfterForm(out);
            return;
         }*/

      }

    } else {

        //
        // USER IS A PROSHOP USER
        //

        /*
        if (club.equals( "newcanaan" ) && user.startsWith("proshop")) {

            out.println(SystemUtils.HeadTitleAdmin("Service Suspended Notification"));
            out.println("<BODY>");
            SystemUtils.getProshopSubMenu(req, out, 0);
            out.println("<CENTER><BR><BR>Sorry, but the email feature has been temporarily disabled.<BR>");
            out.print("</CENTER><BR><BR></BODY>");

            drawEndOfPageAfterForm(out);
            return;

         } else { */

            // Check Feature Access Rights for current proshop user
        if (ProcessConstants.isProshopUser(user)) {

            if (!SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
                SystemUtils.restrictProshop("TOOLS_EMAIL", out);
                return;
            }
        }


         //}
    }


    //
    // If this req is a multipart then we know user is here trying to send
    // an email.  Let's handle that seperately.
    //
    if (ServletFileUpload.isMultipartContent(req)) {

        sendEmail(session, req, resp, con, out);

    } else {

        if (ProcessConstants.isProshopUser(user)) {
            
            //
            // Check to see if the 'flags' are populated which indicate there were attachments added
            //
            String attach_added = ((req.getParameter("attach_added") != null) ? req.getParameter("attach_added") : "");

            if (!attach_added.equals("")) {

                out.println("<!-- DISPLAY ATTACHMENT WARNING MESSAGE -->");
                out.println("<script>alert('Your attachments have been cleared and you will need to reselect them if you wish to include them.  To avoid this, finalize your recipients before attaching your files.  This is a new security feature of your web browser.');</script>");

            }
            
            String email = "";
            String emailPass = "";
            String userPass = req.getParameter("emailPass");
            if (userPass == null) userPass = "";

            Statement stmt = null;
            ResultSet rs = null;

            try {

                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT email, emailPass FROM club5;");

                if (rs.next()) {

                    email = rs.getString(1);
                    emailPass = rs.getString(2);
                }

            } catch (Exception exp) {

                SystemUtils.buildDatabaseErrMsg("Unable to retrieve required password.", exp.toString(), out, false);
                return;

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { stmt.close(); }
                catch (Exception ignore) {}

            }

            if ( email.equals("") ) {

                out.println(SystemUtils.HeadTitle("Email Address Required"));
                out.println("<BODY>");
                SystemUtils.getProshopSubMenu(req, out, 0);
                out.println("<CENTER><BR><BR>");
                out.println("<h3>Email Address Required</h3>");
                out.println("<p>In order to access the email communication feature you will need to specify an email address under Club Setup | Club Options.</p>");
                out.println("<p>You can <a href=\"/v5/servlet/Proshop_club\">click here</a> to access the setup page and add an email address.</p>");
                out.println("");
                out.print("</CENTER><BR><BR></BODY>");

                drawEndOfPageAfterForm(out);
                return;

            } else if ( !emailPass.equals("") && !userPass.equals(emailPass) ) {

                out.println(SystemUtils.HeadTitle("Password Required"));
                out.println("<BODY>");
                SystemUtils.getProshopSubMenu(req, out, 0);
                out.println("<CENTER><BR><BR>");
                out.println("<h3>Authentication Required</h3>");
                out.println("<p>Your club requires that you provide a password in order to access the email communication feature.</p>");
                out.println("<form method=post>");
                out.println("Password:&nbsp; <input type=password name=emailPass value=''> ");
                out.println("&nbsp; &nbsp; <input type=submit value=' Submit '>");
                out.println("</form>");
                out.print("</CENTER><BR><BR></BODY>");

                drawEndOfPageAfterForm(out);
                return;

            }
        }

        /* standard unencoded request - determin action */

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
        }/***** NOW CALLING sendEmail ABOVE ONLY IF A MULTIPART REQUEST IS RECEIVED
        else if (action.equals(ActionHelper.SEND))
        {
          sendEmail(session, req, resp, con, out);
        }*/
        else if (action.equals(ActionHelper.CANCEL))
        {
          showPage(null, session, req, resp, con, out, true);
        }
        else
        {
          /* default action */

          showPage(null, session, req, resp, con, out, refresh);
        }

    }

  }



  //******************************************************************************************************
  //   showPage
  //******************************************************************************************************
  //
  private void showPage(FeedBack feedback, HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out, boolean refresh)
    throws IOException
  {

     
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
    
    boolean disp_attach_notice = false;                    // Display Attachment Notice option (proshop user only)
    
    
    if (ProcessConstants.isProshopUser(user)) {            // if Proshop user
       
       disp_attach_notice = true;
    }
      
    if (refresh)
    {
      session.removeAttribute(Email.EMAIL_FRM);
    }

    FormModel form = retrieveFormFromSession(session, req, res, con, out);

    if (!refresh)
    {
      form.update(req, res, out);
    }

    drawBeginningOfPageBeforeForm(feedback, out, req, session);
    FormRenderer.render(form, out);
    drawEndOfPageAfterForm(out, disp_attach_notice);

  }


  //******************************************************************************************************
  //   addToList
  //******************************************************************************************************
  //
  private void addToList(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

    FormModel form = retrieveFormFromSession(session, req, res, con, out);
    FeedBack feedback = null;

    ArrayList selected_items = null;
    ArrayList selected_names = null;

    String user = (String)session.getAttribute("user");   // get username

    String search_type = (String)(req.getParameter(ActionHelper.SEARCH_TYPE));

    if (search_type.equals(ActionHelper.ADD_PARTNERS))
    {
      selected_names = getUserNamesFromPartnerList(session, con);

      if (selected_names == null || selected_names.size()<=0)
      {
        feedback = new FeedBack();
        feedback.setPositive(false);
        String message = ScriptHelper.escapeSpecialCharacters("You don't have any members on your partner list.");
        feedback.addMessage(message);

      }

    }
    else
    {
      String items = req.getParameter(ActionHelper.SELECTED_ITEMS_STRING);

      if (items != null && !(items.equals("")))
      {

        selected_items = ActionHelper.getSelectedNames(items);

      }

      if (search_type.equals(ActionHelper.SEARCH_LISTS))
      {
        //for each distribution list selected, need to extract the user names
        selected_names = getUserNamesFromDistributionLists(selected_items, session, req, res, con, out);

      }
      else if (search_type.equals(ActionHelper.SEARCH_EVENTS))
      {
        //for each event selected, need to extract the user names
        selected_names = getUserNamesFromEvents(selected_items, session, req, res, con, out);

      }
      else if (search_type.equals(ActionHelper.SEARCH_TEESHEET))
      {
        //for the indicated tee sheet, need to extract the user names
        selected_names = getUserNamesFromTeeSheet(session, req, res, con, out);

      }
      else if (search_type.equals(ActionHelper.SEARCH_TIMESHEET))
      {                                     
        //for the indicated ACTIVITY Time sheet, need to extract the user names
        selected_names = getUserNamesFromActivitySheet(session, req, res, con, out);

      }
      else //the search type was for members, user names are already determined
      {
        selected_names = selected_items;
      }

    }

    if (selected_names != null && selected_names.size()>0)
    {

      // indicate that there are recipients
      form.addHiddenInput("recipients", "Yes");

      //get the table from the form and add the name in the list
      RowModel row = form.getRow(DistributionList.LIST_OF_NAMES);
      TableModel names = (TableModel)(((Cell)row.get(0)).getContent());

      for (int i=0; i<selected_names.size(); i++)
      {

        //check to see if this name is already in the list
        String name_to_add = (String)(selected_names.get(i));
        RowModel nameRow = names.getRow(name_to_add);

        boolean sizeOK = true;

        // make sure there aren't too many names (pro is unlimited)
       if (!ProcessConstants.isProshopUser(user) && !ProcessConstants.isAdminUser(user))
       {
          if (names.size() >= Email.MAX_RECIPIENTS)
          {
             sizeOK = false;
          }
        }

        if (sizeOK == true)
        {
          if (nameRow == null)
          {
            try
            {
              nameRow = new RowModel();
              nameRow.setId(name_to_add);
              String displayName = "";

              displayName = MemberHelper.getMemberDisplayName(con, name_to_add, out);   // pass username

              nameRow.add(displayName);
              ActionModel actions = new ActionModel();
              String removeUrl = "javascript:removeNameFromList('" + versionId + "servlet/Send_email', '" + ActionHelper.REMOVE_FROM_LIST + "', '" + name_to_add + "')";
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
        else
        {
          feedback = new FeedBack();
          feedback.setPositive(false);
          feedback.addMessage("The maximum number of recipients for an email is 100.  Some of the selected members may not have been added.");
        }
      }

      ActionModel model = names.getContextActions();
      boolean maxSizeReached = false;
      if (names.size()>=Email.MAX_RECIPIENTS)
      {
        if (!ProcessConstants.isProshopUser(user) && !ProcessConstants.isAdminUser(user))
        {
           maxSizeReached = true;
        }
      }

      for (int i=0; i<model.size(); i++)
      {
        ((Action)(model.get(i))).setSelected(maxSizeReached);

      }

    } else {

      // indicate that there are NO recipients
      form.addHiddenInput("recipients", "");

    }

    showPage(feedback, session, req, res, con, out, false);
  }


  //******************************************************************************************************
  //  addTeeSheetMembersToList
  //******************************************************************************************************
  //
  private void addTeeSheetMembersToList(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
  throws IOException
  {

     buildForm(req, res, session, con, out);
     showPage(null, session, req, res, con, out, true);

  }


  //******************************************************************************************************
  //  getUserNamesFromPartnerList
  //******************************************************************************************************
  //
  private ArrayList getUserNamesFromPartnerList(HttpSession session, Connection con)
    throws IOException
  {

    String user = (String)session.getAttribute("user");   // get username
    String club = (String)session.getAttribute("club");   // get club
    ArrayList<String> usernames = new ArrayList<String>();

    int sess_activity_id = 0;

    try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
    catch (Exception ignore) { }

    boolean isPro = ProcessConstants.isProshopUser(user);
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null; 
    ResultSet rs2 = null;

    String partner_id = "";
    String email = "";

    try {
        pstmt = con.prepareStatement (
                "SELECT partner_id FROM partner WHERE user_id = ? AND activity_id = ?");

        pstmt.clearParameters();            // clear the parms
        pstmt.setString(1, user);           // put username in statement
        pstmt.setInt(2, sess_activity_id);  // current activity id
        rs = pstmt.executeQuery();          // execute the prepared stmt

        while (rs.next()) {
            
            partner_id = rs.getString("partner_id");

            if (isPro || !club.equals("treesdalegolf") || !partner_id.equals("R0084")) {

                //check if the buddy has registered an email address
                try {

                    pstmt2 = con.prepareStatement (
                            "SELECT email FROM member2b WHERE username = ? AND email_bounced = 0 AND inact = 0 AND billable = 1");

                    pstmt2.clearParameters();                   // clear the parms
                    pstmt2.setString(1, partner_id);             // put the parm in stmt
                    rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                    if (rs2.next()) {

                        email = rs2.getString("email");

                        if (email != null && !email.equals("")) {
                            usernames.add(partner_id);
                        }
                    }

                } catch (Exception ignore) {
                } finally {

                    try { rs2.close(); }
                    catch (Exception ignore) {}

                    try { pstmt2.close(); }
                    catch (Exception ignore) {}

                }
             }
          }        // end of FOR loop

    } catch (Exception ignore) {
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return(usernames);
  }



  //******************************************************************************************************
  //   getUserNamesFromDistributionLists
  //******************************************************************************************************
  //
  private ArrayList<String> getUserNamesFromDistributionLists(ArrayList selectedItems, HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

    ArrayList<String> names = new ArrayList<String>();

    //get the database table name based on the user
    String table_name = DistributionList.getTableName(session);    // 'dist4' or 'dist4p'

    //
    //  Get this user's distribution lists, if any, and list them by name
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
    int list_size = DistributionList.getMaxListSize(session) + 1;

    String [] users = new String [list_size];                     // max of 30 users per dist list (start with 1)
    String uname = "";
    ResultSet rs = null;
    PreparedStatement stmt = null;

    if (selectedItems != null)
    {
      for (int i=0; i<selectedItems.size(); i++)
      {
        String list_name = (String)(selectedItems.get(i));

        try
        {

          stmt = con.prepareStatement (
                 "SELECT * FROM " + table_name + " WHERE name = ? AND owner = ?");

          stmt.clearParameters();               // clear the parms
          stmt.setString(1, list_name);
          stmt.setString(2, user);
          rs = stmt.executeQuery();            // execute the prepared stmt

          if (rs.next())
          {

             for (int i2 = 1; i2 < list_size; i2++) {               // check all 30 (start with 1)

                uname = "user" +i2;
                users[i2] = rs.getString(uname);

                // add each user from the distribution list
                if (!users[i2].equals( "" )) {

                   names.add(users[i2]);
                }
             }
          }

        } catch(Exception exc) {
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }
      }
    }

    return names;
  }


  //******************************************************************************************************
  //  getUserNamesFromTeeSheet
  //******************************************************************************************************
  //
  private ArrayList getUserNamesFromTeeSheet(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
  throws IOException
  {


      ArrayList<String> selectedItems = new ArrayList<String>();
      //get the teesheet date
      String indexStr = req.getParameter("index");
      if (indexStr == null || indexStr.equals(""))
        indexStr = "0";

      int index = (new Integer(indexStr)).intValue();

      // get the course
      String course = (String)req.getParameter("course");


      PreparedStatement pstmt2p = null;
      ResultSet rs = null;
      String email = "";

      String [] username = new String [6];

      //
      //  Get today's date and then use 'index' to locate the requested date
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      cal.add(Calendar.DATE,index);                  // roll ahead 'index' days

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd

   //  ************ TEMP *************
   //   SystemUtils.logError("SendEmail for tee sheet - course=" +course+ ", index=" +index);


      try{
        //
        //  Get the username for each member in this date's tee sheet (username will only be present for members)
        //
        if (course.equals( "-ALL-" ) || course == null || course.equals("")) {

            pstmt2p = con.prepareStatement (
              "SELECT username1, username2, username3, username4, username5 " +
              "FROM teecurr2 WHERE date = ?");

            pstmt2p.clearParameters();        // clear the parms
            pstmt2p.setLong(1, date);

        } else {

            pstmt2p = con.prepareStatement (
              "SELECT username1, username2, username3, username4, username5 " +
              "FROM teecurr2 WHERE date = ? AND courseName = ?");

            pstmt2p.clearParameters();        // clear the parms
            pstmt2p.setLong(1, date);
            pstmt2p.setString(2, course);
        }

        rs = pstmt2p.executeQuery();      // execute the prepared stmt

        while (rs.next()) {

            for (int i = 1; i < 6; i++) {

              username[i] = rs.getString( i );
              if (!username[i].equals( "" )) {

                  if (MemberHelper.hasEmailAddress(username[i], con, out))
                    selectedItems.add(username[i]);

              }

            }

        }

        pstmt2p.close();
    }
    catch (SQLException sqle)
    {
         String club = (String)session.getAttribute("club");   // get club name
         String errorMsg = "Error adding teesheet members to the email (Send_email) from " +club+ ", Error: " +sqle.getMessage(); // build error msg
         SystemUtils.logError(errorMsg);                           // log it

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt2p.close(); }
        catch (Exception ignore) {}

    }

    return selectedItems;
  }


  //******************************************************************************************************
  //  getUserNamesFromEvents
  //******************************************************************************************************
  //
  private ArrayList getUserNamesFromEvents(ArrayList selectedItems, HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

    ArrayList<String> names = new ArrayList<String>();

    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";

    PreparedStatement stmt = null;
    ResultSet rs = null;

    if (selectedItems != null)
    {
      for (int i=0; i<selectedItems.size();i++)
      {
        String event_name = (String)(selectedItems.get(i));

        try
        {

          stmt = con.prepareStatement (
                 "SELECT username1, username2, username3, username4, username5 FROM evntsup2b WHERE name = ? AND inactive = 0");

          stmt.clearParameters();               // clear the parms
          stmt.setString(1, event_name);
          rs = stmt.executeQuery();            // execute the prepared stmt

          while ( rs.next() )
          {

             user1 = rs.getString(1);
             user2 = rs.getString(2);
             user3 = rs.getString(3);
             user4 = rs.getString(4);
             user5 = rs.getString(5);

             // add each member currently registered for the selected event
             if (!user1.equals( "" )) {
                if (MemberHelper.hasEmailAddress(user1, con, out))
                  names.add(user1);
             }
             if (!user2.equals( "" )) {
                if (MemberHelper.hasEmailAddress(user2, con, out))
                  names.add(user2);
             }
             if (!user3.equals( "" )) {
               if (MemberHelper.hasEmailAddress(user3, con, out))
                  names.add(user3);
             }
             if (!user4.equals( "" )) {
               if (MemberHelper.hasEmailAddress(user4, con, out))
                names.add(user4);
             }
             if (!user5.equals( "" )) {
                if (MemberHelper.hasEmailAddress(user5, con, out))
                names.add(user5);
             }

          }
        }
        catch(Exception ignore)
        {
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }
      }
    }
    return names;
  }


  //******************************************************************************************************
  //  getUserNamesFromTeeSheet
  //******************************************************************************************************
  //
  private ArrayList getUserNamesFromActivitySheet(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
  throws IOException
  {


      ArrayList<String> selectedItems = new ArrayList<String>();   // array of usernames to return
      
      PreparedStatement pstmt = null;
      PreparedStatement pstmt2 = null;
      ResultSet rs = null;
      ResultSet rs2 = null;
      
      String username = "";

      int date = 0;
      int count = 0;
      int count2 = 0;
      int activity_id = 0;
      int sheet_id = 0;
      int i = 0;
      
      //get the time sheet date
      String dateStr = req.getParameter("date");
      
      if (dateStr == null || dateStr.equals("")) {
         
        date = 0;
        
      } else {

         date = Integer.parseInt(dateStr);
      }

      //get the max number of items selected (# of time sheets)
      dateStr = req.getParameter("count");
      
      if (dateStr == null || dateStr.equals("")) {
         
        count = 0;
        
      } else {

         count = Integer.parseInt(dateStr);
      }
      
      if (date > 0 && count > 0) {     // only continue if something to process
         
         int [] activityA = new int [count];     // create an array to hold the max number of items
      
         //
         //   get the activity ids
         //
         for (i=1; i<=count; i++) { 
         
            if (req.getParameter("activity" +i) != null) {    // did user select this check box?

               dateStr = req.getParameter("activity" +i);     // get the activity id

               activityA[count2] = Integer.parseInt(dateStr);  // save it
               
               count2++;
            }
         }
         
         // SystemUtils.logError("Send_email - Activities: Date=" +date+ ", Count=" +count+ ", Count2=" +count2+ ", Ids=" +activityA[0]+ ", " +activityA[1]);    

         
         if (count2 > 0) {           // only continue if user selected one or more items

            for (i=0; i<count2; i++) {          // get each activity id that was selected
         
               activity_id = activityA[i];      // get this item's id
               
               if (activity_id > 0) {

                  try{
                    //
                    //  Get the username for each member in this date's sheet (username will only be present for members)
                    //
                    pstmt = con.prepareStatement (
                       "SELECT sheet_id " +
                       "FROM activity_sheets WHERE DATE_FORMAT(date_time, \"%Y%m%d\") = ? AND activity_id = ?");

                    pstmt.clearParameters();      
                    pstmt.setInt(1, date);
                    pstmt.setInt(2, activity_id);

                    rs = pstmt.executeQuery();     

                    while (rs.next()) {

                       sheet_id = rs.getInt(1);            // get the id for the selected sheet (one time slot at a time)
               
                       //   now get the players on this sheet       
                       pstmt2 = con.prepareStatement (
                          "SELECT username " +
                          "FROM activity_sheets_players WHERE activity_sheet_id = ? AND username != ''");

                       pstmt2.clearParameters();     
                       pstmt2.setInt(1, sheet_id);

                       rs2 = pstmt2.executeQuery();    

                       while (rs2.next()) {

                          username = rs2.getString(1);

                          if (!username.equals( "" )) {

                              if (MemberHelper.hasEmailAddress(username, con, out))
                                selectedItems.add(username);
                          }
                       }
                       pstmt2.close();
                                          
                    }
                    pstmt.close();
                    
                   }
                   catch (SQLException sqle)
                   {
                        String club = (String)session.getAttribute("club");   // get club name
                        String errorMsg = "Error adding activity sheet members to the email (Send_email) from " +club+ ", Error: " +sqle.getMessage(); // build error msg
                        SystemUtils.logError(errorMsg);                           // log it

                   } finally {

                       try { rs.close(); }
                       catch (Exception ignore) {}

                       try { rs2.close(); }
                       catch (Exception ignore) {}

                       try { pstmt.close(); }
                       catch (Exception ignore) {}

                       try { pstmt2.close(); }
                       catch (Exception ignore) {}

                   }
               }
            }         // end of FOR loop for activity ids
         }
    }

    return selectedItems;
  }


  //******************************************************************************************************
  //  removeFromList
  //******************************************************************************************************
  //
  private void removeFromList(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

    FormModel form = retrieveFormFromSession(session, req, res, con, out);

    //the user has submitted a name to add to the list
    String name_to_remove = req.getParameter(Member.REQ_USER_NAME);

    if (name_to_remove != null && !(name_to_remove.equals("")))
    {

      //get the table from the form and add the name in the list
      RowModel row = form.getRow(DistributionList.LIST_OF_NAMES);
      TableModel names = (TableModel)(((Cell)row.get(0)).getContent());

      names.remove(name_to_remove);

      ActionModel model = names.getContextActions();
      boolean maxSizeReached = false;
      if (names.size()>=Email.MAX_RECIPIENTS)
      {
        maxSizeReached = true;
      }

      for (int i=0; i<model.size(); i++)
      {
        ((Action)(model.get(i))).setSelected(maxSizeReached);

      }
    }



    showPage(null, session, req, res, con, out, false);

  }


  //******************************************************************************************************
  //   sendEmail
  //******************************************************************************************************
  //
  private void sendEmail(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

   Statement stmtN = null;
   ResultSet rs = null;


   String fb_message = "";
   String clubName = "";
   String activity_name = "";
   String activity_efrom = "";

   //boolean aol = false;

   //  ***** get user id so we know if proshop or member

   String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
   String club = (String)session.getAttribute("club");

   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   //
   //  Get the name of the club
   //
   try {

      stmtN = con.createStatement();

      rs = stmtN.executeQuery("SELECT clubName FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         clubName = rs.getString(1);
      }

   } catch (Exception ignore) {
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmtN.close(); }
        catch (Exception ignore) {}

    }

   if (!clubName.equals( "" )) {

      clubName = SystemUtils.unfilter(clubName);   //  Filter out special characters - change from html format to real chars
   }
   
   //
   //  Get name of activity if user is logged into one
   //
   if (sess_activity_id > 0) {
   
       try {

           stmtN = con.createStatement();
           rs = stmtN.executeQuery("SELECT activity_name, email FROM activities WHERE activity_id = " + sess_activity_id);

           if (rs.next()) {
               activity_name = rs.getString("activity_name");
               activity_efrom = rs.getString("email");
           }

           // if there was no email address specified then try and get it from the parent activity (should need if configured correctly)
           if (activity_efrom.equals("")) {


           }

         } catch (Exception ignore) {
         } finally {

              try { rs.close(); }
              catch (Exception ignore) {}

              try { stmtN.close(); }
              catch (Exception ignore) {}
         }
   }


    //
    // Get the SMTP parmaters this club is using
    //
    parmSMTP parm = new parmSMTP();

    try {

        getSMTP.getParms(con, parm);        // get the SMTP parms

    } catch (Exception ignore) {}


    //
    // Find any attachments and plain/text form elements
    // and put them in message BodyParts for later
    //

    ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
    servletFileUpload.setSizeMax(1024 * 512); // 512KB
    List fileItemsList = null;
    Dictionary fields = new Hashtable(); // this probably needs to remain a generic and not get typed because it stores both strings and mimebodyparts

    try {

        fileItemsList = servletFileUpload.parseRequest(req);

    } catch (org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException exc) {

        out.println("<br><p>Your attachment was too large to send.  The maximum size for an attachment is 512KB (half a megabyte).</p>" +
                "<p>Details: " + exc.getMessage() + "</p>" +
                "<p>Please <a href='javascript: location.history(-1)'>go back</a> and change your attachment(s) and try again.</p>");
        return;

    } catch (Exception exc) {

        out.println("<br><p>There was a problem detected with one or more of your attachments.</p>" +
                "<p>Details: " + exc.getMessage() + "</p>" +
                "<p>Please <a href='javascript: location.history(-1)'>go back</a> and change your attachment(s) and try again.</p>");
        return;
    }

    Iterator it = fileItemsList.iterator();

    while ( it.hasNext() ) {

        FileItem fileItem = (FileItem)it.next();
        if (fileItem.isFormField()) {

            /* The file item contains a simple name-value pair of a form field */

            if (fileItem.getString() != null && fileItem.getFieldName() != null && (!(fileItem.getString().equals("")))) {

                //out.println("<br>Adding name=" + fileItem.getFieldName() + ", value=" + fileItem.getString());

                fields.put(fileItem.getFieldName(), (String)fileItem.getString());
            }

        } else {

            /* The file item contains an uploaded file */
            
            // only include attachment is there is one
            if (!fileItem.getName().trim().equals("")) {
                
                //out.println("<br>filename=" + fileItem.getName() + ", fieldName=" + fileItem.getFieldName() + ", size=" + (fileItem.getSize() / 1024) + "KB");

                try {

                    MimeBodyPart bodyPart = new MimeBodyPart();
                    DataSource ds = new ByteArrayDataSource(
                            fileItem.get(),
                            fileItem.getContentType(),fileItem.getName());
                    bodyPart.setDataHandler(new DataHandler(ds));
                    bodyPart.setDisposition("attachment; filename=\"" + fileItem.getName() + "\"");
                    bodyPart.setFileName(fileItem.getName());

                    fields.put(fileItem.getFieldName(), bodyPart);

                } catch (Exception exc) {

                    out.println("ERROR2: " + toString());
                }

            }
        }

    } // end while




   //get the subject, message and the to list

   String subject = req.getParameter(Email.SUBJECT);
   if (subject == null) subject = (String)fields.get(Email.SUBJECT);
   String trailer = "";
   String message = "";
   String memberName = SystemUtils.getFullNameFromUsername(user, con);
   String senderTitle = "golf professional";
   
   if (!activity_name.equals("")) {
      
      senderTitle = activity_name+ " professional";
   }

   if (user.startsWith("proshopfb")) {
       senderTitle = "food and beverage staff";
   }

   if (user.equals("proshop4") && club.equals("foresthillsgc")) {

           message = "A message from the Forest Hills GC Office:\n\n";

   } else if (user.startsWith("proshop")) {      

       if (!club.equals("rollinghillsgc")) {      // Do not print header for Rolling Hills GC!

           if (club.equals("portage")) {

               message = "Your Team of Golf Professionals has sent you the following message:\n\n";

           } else if (club.equals("fortcollins")) {

               message = "Your " + senderTitle + " has sent you the following message:\n\n";

           } else {

              if (user.equals("proshop2") && club.equals("hillcrestwi")) {

                 message = "Your Superintendant/General Manager at " + clubName + " has sent you the following message:\n\n";

              } else {
                 message = "Your " + senderTitle + " at " + clubName + " has sent you the following message:\n\n";
              }
           }
       }

   } else {

       message = memberName + " has sent you the following message.\n\n";
   }

   String user_message = req.getParameter(Email.MESSAGE);
   if (user_message == null) user_message = (String)fields.get("messageField");
   message += user_message;

   //
   //  Add the name of the club to the subject to ensure it is included (for members that belong to multiple clubs)
   //
   if (!club.equals("fortcollins")) {
       if (!clubName.equals( "" )) subject += " (" +clubName+ ")";
   }

   // determin the address we are going to use as the 'from' address
   String efrom = "";

   if (user.startsWith( "proshop" ) && sess_activity_id != 0) {

       // this is an activity proshop user so use the addy we pulled earlier from the activities table
       efrom = activity_efrom;

   } else {

       // this is a golf user (pro or mem)
       efrom = MemberHelper.getEmailAddress(user, con, out);  // get caller's email address (pro or member)

   }

   String replyTo = efrom;                                       // copy for ReplyTo field

   //
   //  for proshop use YourGolfShop@foretees.com as the From address to prevent 'forging' problem
   //
   if (user.startsWith( "proshop" )) {

      if (!club.equals("rollinghillsgc")) {     // Do not print a footer for Rolling Hills GC

          if (user.startsWith("proshopfb")) {

              efrom = parm.EMAIL_FROM_FB;

          } else if (!activity_name.equals("")) {

              efrom = ProcessConstants.EFROMPROA;        // change this later ???? - Activities

          } else {

              efrom = parm.EMAIL_FROM_PRO;

          }

          if (club.equals( "hillcrestwi" ) || club.equals( "pinehills" ) || club.equals("roccdallas")) {        // if Hillcrest WI or Pine Hills

             trailer = "\n\n\n*****************************************************************************************" +
                       "\nNOTICE: This message has been sent by a " +senderTitle+ " at your club via the ForeTees system. " +
                       "In order to comply with new anti-spam rules, we cannot use the sender's email address in the FROM " +
                       "field. However, you may reply to the sender as their address is saved in the REPLY-TO field. " +
                       "You may also contact him/her at ";

          } else if (club.equals( "portage" )) {

             trailer = "\n\n\n*****************************************************************************************" +
                       "\nNOTICE: Please DO NOT reply to this email. " +
                       "\nThis message has been sent by your " +senderTitle+ " via the ForeTees system which " +
                       "cannot process a reply. Should you prefer not to receive these email messages, " +
                       "please contact your " +senderTitle+ " at ";

          } else if (user.startsWith("proshopfb")) {

             trailer = ProcessConstants.TRAILERFB;

          } else {

             trailer = ProcessConstants.TRAILERPRO;
          }
      }

   } else {

      efrom = parm.EMAIL_FROM_MEM;
      trailer = ProcessConstants.TRAILERMEM;
   }

/*      SHOULDN'T BE NEEDED SINCE WE ARE ALREADY CHECKING IF MEMBER HAS AN EMAIL ADDRESS
 *      SO THEY SHOULDN'T EVEN BE ABLE TO GET THIS FAR IF THEY DID NOT
   //
   //  make sure there is a 'from' address for members
   //
   if (replyTo.equals( "" ) && !user.startsWith( "proshop" )) {

     FeedBack feedback = new FeedBack();
     feedback.setPositive(false);

     fb_message = "You don't have an email address.  Please go to Settings and add your email address.";

     feedback.addMessage(ScriptHelper.escapeSpecialCharacters(fb_message));
     showPage(feedback, session, req, res, con, out, false);
   }
*/

   FormModel form = retrieveFormFromSession(session, req, res, con, out);

   RowModel row = form.getRow(DistributionList.LIST_OF_NAMES);
   TableModel names = (TableModel)(((Cell)row.get(0)).getContent());
   //there should be at least one person on the to list
   if (names.size() <= 0)
   {
     FeedBack feedback = new FeedBack();
     feedback.setPositive(false);
     fb_message = "You haven't selected any recipients.  Please add at least one member to the list.";
     feedback.addMessage(ScriptHelper.escapeSpecialCharacters(fb_message));

     showPage(feedback, session, req, res, con, out, false);
     return;
   }

   //we need to get the email address for each user from the database

   int i = 0;

   ArrayList<ArrayList<String>> eaddrTo  = new ArrayList<ArrayList<String>>();
   ArrayList<String> eaddrProCopy = new ArrayList<String>();
   ArrayList<String> member_addresses = new ArrayList<String>();

   for (i=0; i<names.size(); i++) {

       // get one or both email address for this member
       member_addresses = MemberHelper.getEmailAddresses((names.getRow(i)).getId(), con, out);
       if (member_addresses.size() > 0 && (member_addresses.get(0) != null && !member_addresses.get(0).equals(""))) {

           // found at least one email address - add it
           eaddrTo.add(new ArrayList<String>());
           eaddrTo.get(eaddrTo.size() - 1).add( member_addresses.get(0) ); // add their first email address
           eaddrTo.get(eaddrTo.size() - 1).add( names.getRow(i).getId() ); // add the username

           // now check to see if they had two emails address specified
           if (member_addresses.size() > 1 && (member_addresses.get(1) != null && !member_addresses.get(1).equals(""))) {

               eaddrTo.add(new ArrayList<String>());
               eaddrTo.get(eaddrTo.size() - 1).add( member_addresses.get(1) ); // add their second email address
               eaddrTo.get(eaddrTo.size() - 1).add( names.getRow(i).getId() ); // add the username again

           }

       }

   } // end loop of names arraylist


   if (user.startsWith( "proshop" )) {      // if email from proshop

        //eaddrProCopy.add( replyTo );             // we now add the pros email address to the normal send to list

        eaddrTo.add(new ArrayList<String>());
        eaddrTo.get(eaddrTo.size() - 1).add(replyTo);
        eaddrTo.get(eaddrTo.size() - 1).add("");

        //  Custom for Interlachen - add all pros to the recipient list for all emails sent by proshop users
        if ( club.equals("interlachen") ) {
/*
            eaddrProCopy.add( "rory.luck@pga.com" );
            eaddrProCopy.add( "rcarroll@pga.com" );        // removed
            eaddrProCopy.add( "schuette.mike@gmail.com" );
            eaddrProCopy.add( "mikeherzog@pga.com" );
            eaddrProCopy.add( "cody54022@hotmail.com" );
*/
            eaddrTo.add(new ArrayList<String>());
            eaddrTo.get(eaddrTo.size() - 1).add("rory.luck@pga.com");
            eaddrTo.get(eaddrTo.size() - 1).add("");

            eaddrTo.add(new ArrayList<String>());
            eaddrTo.get(eaddrTo.size() - 1).add("schuette.mike@gmail.com");
            eaddrTo.get(eaddrTo.size() - 1).add("");

            eaddrTo.add(new ArrayList<String>());
            eaddrTo.get(eaddrTo.size() - 1).add("mikeherzog@pga.com");
            eaddrTo.get(eaddrTo.size() - 1).add("");

            eaddrTo.add(new ArrayList<String>());
            eaddrTo.get(eaddrTo.size() - 1).add("cody54022@hotmail.com");
            eaddrTo.get(eaddrTo.size() - 1).add("");

        }

        if (club.equals("brooklawn")) {

            eaddrTo.add(new ArrayList<String>());
            eaddrTo.get(eaddrTo.size() - 1).add("ctgolfgirl@gmail.com");
            eaddrTo.get(eaddrTo.size() - 1).add("");

            eaddrTo.add(new ArrayList<String>());
            eaddrTo.get(eaddrTo.size() - 1).add("jimf.bcc@gmail.com");
            eaddrTo.get(eaddrTo.size() - 1).add("");
        }

       //
       // !!!NOTE!!!
       // THIS IS WHERE WE CAN LOOKUP THE ADDITIONAL PRO EMAILS AND SEE IF THEY WANT TO BE COPIED ON ALL EMAILS SENT VIA THE TOOL
       //
       //eaddrProCopy.add( proEmail );             // add the pro's email address

   }


   //
   //  Add trailer to message
   //
   message = message + trailer;            // add trailer message (Notice.....)

   if (user.startsWith( "proshop" ) && !club.equals("rollinghillsgc")) {      // Do not print a footer for Rolling Hills GC

      message = message + replyTo;    // add pro's email addr to the 'unsubscribe' message
   }


   parmEmail emailParm = new parmEmail();
   emailParm.type = "EmailTool" + ((user.startsWith( "proshop" )) ? "Pro" : "Mem");
   emailParm.subject = subject;
   emailParm.txtBody = message;
   emailParm.from = efrom;
   emailParm.replyTo = replyTo;

   StringBuffer vCalMsg = new StringBuffer();  // no vCal for email tool (at least not yet!)


   //
   //  Build the HTML page
   //
   out.println(SystemUtils.HeadTitle("Email Confirmation Page"));
   out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<br><center><img src=\"" +versionId+ "images/foretees.gif\" border=\"1\" alt=\"ForeTees\"><hr width=\"40%\">");
   out.println("<p>&nbsp;</p>");
   out.println("<table width=\"70%\" border=\"0\" align=\"center\"><tr><td align=\"center\"><font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

   if (user.startsWith( "proshop" )) {

       out.println("<p><b>Thank you!</b>&nbsp;&nbsp;The email message is now being queued for sending.</p>" +
                   "<p>Upon completion you will receive an email confirmation indicating your message has been received by our mail server.&nbsp; " +
                   "Please allow up to 20 minutes for us to send you the confirmation email.</p>");

   } else {

       out.println("<p><b>Thank you!</b>&nbsp;&nbsp;Your email message is now being queued for sending.&nbsp; " +
                   "Your recipients should receive their email message soon.</p>");
   }
   
   out.println("</font></td></tr></table><br>");

/*
   if (success) {
       out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your email message has been sent.</p></font>");
   } else {
       out.println("<p>&nbsp;</p><p><b>Error sending email!</b><br><br>Your email message was <u>not</u> sent.</p></font>");
   }
*/
   out.println("<table border=0><tr><td>");
   out.println("<form method=\"get\" action=\"" +versionId+ "servlet/Send_email\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;width:90px\">");
   out.println("</form>");
   out.println("</td><td>&nbsp;</td><td>");
   if (!ProcessConstants.isProshopUser(user)) {
       out.println("<form method=\"get\" action=\"" +versionId+ "servlet/Member_announce\">");
   } else {
       out.println("<form method=\"get\" action=\"" +versionId+ "servlet/Proshop_announce\">");
   }
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;width:90px\">");
   out.println("</form></td></tr></table>");
   out.println("</center></body></html>");
   out.close();
   
   sendEmail.doSending(eaddrTo, eaddrProCopy, replyTo, subject, message, vCalMsg, emailParm, con, "", fields);
   
 }


 // ************************************************************************
 //  Process getAuthenticator for email authentication
 // ************************************************************************

 private static Authenticator getAuthenticator(final String user, final String pass) {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication(user, pass);
         //return new PasswordAuthentication("support@foretees.com", "fikd18"); // credentials
       }
    };

    return auth;
 }


  /**
  ***************************************************************************************
  *
  * This method will retrieve the email form from the session if one exists or will build
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
    Object theForm = session.getAttribute(Email.EMAIL_FRM);

    if ( theForm != null && theForm instanceof FormModel)
    {
      return (FormModel)theForm;
    }
    else
    {
      FormModel form = buildForm(req, res, session, con, out);
      session.setAttribute(Email.EMAIL_FRM, form);
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
    //Action sendEmailHelp = new Action(ActionHelper.HELP, Labels.HELP);
    //sendEmailHelp.setUrl("javascript:openNewWindow('" + versionId + Help.SEND_EMAIL + "', 'SendEmailOnlineHelp', 'width=500, height=450, directories=no, location=no, menubar=no, scrollbars=yes, status=no, toolbar=no, resizable=yes')");

    //pageActions.add(sendEmailHelp);

    out.println(SystemUtils.HeadTitleAdmin(Email.SEND_EMAIL_LIST_HEADER));
    String onLoad = "";

    if (feedback != null)
    {
      out.print("<body onLoad=\"displayFeedbackMessage('" + feedback.get(0) + "')\">");
      //   onLoad = "javascript:displayFeedbackMessage('" + feedback.get(0) + "')"; //document.pgFrm." + feedback.getAffectedField() + ".focus()";
    }
    else
    {
      out.print("<body onLoad=\"document.pgFrm." + Email.SUBJECT + ".focus()\">");
      //   onLoad = "document.pgFrm." + Email.SUBJECT + ".focus()";
      // document.pgFrm.enctype='application/x-www-form-urlencoded';
    }


   // LayoutHelper.drawBeginPageContentWrapper(onLoad, null, out);

    String club = (String)session.getAttribute("club");
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
    {
      SystemUtils.getMemberSubMenu(request, out, caller);
      out.println("<script type=\"text/javascript\" src=\"" + versionId + "sessionSaver.js\"></script>");
    }

    //ActionHelper.drawDistListNavBar(ActionHelper.SEND_EMAIL_TO_DIST_LIST, out);
    if (club.equals( "merrillhills" )) {
        LayoutHelper.drawBeginMainBodyContentWrapper(Email.SEND_EMAIL_LIST_HEADER + " - This feature is intended for golf related communications between members and should not be used for solicitations of any type.", pageActions, out);
    } else {
        LayoutHelper.drawBeginMainBodyContentWrapper(Email.SEND_EMAIL_LIST_HEADER, pageActions, out);
    }
    out.println("<table align=\"center\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>");

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

    //
    //  ***** get user id so we know if proshop or member
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)

    //String club = (String)session.getAttribute("club");

    String userPass = req.getParameter("emailPass");
    if (userPass == null) userPass = "";

    String recipients = req.getParameter(ActionHelper.SELECTED_ITEMS_STRING);
    if (recipients == null) recipients = "";

    FormModel form = new FormModel("pgFrm", FormModel.POST, "bot");
    if (ProcessConstants.isProshopUser(user))
    {
      //form.setEncType("multipart/form-data");
      //form.setEncType("application/x-www-form-urlencoded"); // default
    }
    form.setNumColumns(3);
    form.addHiddenInput("formId", Email.EMAIL_FRM);
    form.addHiddenInput(ActionHelper.ACTION, ActionHelper.SEND_EMAIL_TO_DIST_LIST);
    form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
    form.addHiddenInput(ActionHelper.SELECTED_ITEMS_STRING, ""); // contains a list of names - gets set by child window that holds list of recipients
    form.addHiddenInput(Member.REQ_USER_NAME, "");
    form.addHiddenInput(ActionHelper.SEARCH_TYPE, "");
    form.addHiddenInput("emailPass", userPass);
    form.addHiddenInput("attach_added", "");  // flag which get set if any of the attachment file boxes gets set
    
    //create the action model for this form and add it to the form model
    ActionModel formActions = new ActionModel();

    String sendUrl = "javascript:sendEmail('" + versionId + "servlet/Send_email', '" + ActionHelper.SEND + "')";
    Action sendAction = new Action(ActionHelper.SEND, Labels.SEND, "Send the email.", sendUrl);

    formActions.add(sendAction);

    String cancelUrl = "javascript:cancel('" + versionId + "servlet/Send_email', '" +  ActionHelper.CANCEL + "', '" + Labels.CANCEL_WITHOUT_SENDING_EMAIL + "')";
    Action cancelAction = new Action("cancel", Labels.CANCEL, Labels.CANCEL_WITHOUT_SENDING_EMAIL, cancelUrl);
    formActions.add(cancelAction);

    form.setActions(formActions);

    //Add the help steps for the form
    ArrayList<String> helpSteps = new ArrayList<String>(4);
    if (!ProcessConstants.isProshopUser(user))
    {
      // MEMBER SPECIFIC HELP ITEMS
      helpSteps.add("<b><i>You must complete your email within " + (SystemUtils.MEMBER_TIMEOUT / 60) + " minutes or your session will expire and your email will be lost.</i></b>");
      //if (club.equals( "merrillhills" )) helpSteps.add("<b><i>This feature is intended for golf related communications between members and should not be used for solicitations of any type.</i></b>");
    } 
    else 
    {
      // PROSHOP SPECIFIC HELP ITEMS
      helpSteps.add("<b>Choose your attachments last</b>.  New security features in your browser will cause it to forget your attachments if you change your recipient list.");
    }

    helpSteps.add(Email.SUBJECT_HELP);
    helpSteps.add(Email.MESSAGE_HELP);

    if (ProcessConstants.isProshopUser(user))
    {
      helpSteps.add(Email.TO_HELP_PROSHOP);
    }
    else
    {
      helpSteps.add(Email.TO_HELP);
    }

    helpSteps.add(Email.SEND_HELP);

    if (ProcessConstants.isProshopUser(user))
    {
      helpSteps.add(Email.NOTE_PROSHOP);
    }
    else
    {
      helpSteps.add(Email.NOTE);
    }

    form.setHelpSteps(helpSteps);

    Attribute subject = new Attribute(Email.SUBJECT, Email.SUBJECT_LABEL, "", Attribute.EDIT);
    subject.setMaxLength("200");
    subject.setSize("70");
    //subject.setHelpText(Email.SUBJECT_HELP);

    RowModel subject_row = new RowModel();
    subject_row.setId(Email.SUBJECT);
    subject_row.add(subject, "frm", 3);

    form.addRow(subject_row);

    TextBox message = new TextBox(Email.MESSAGE, Email.MESSAGE_LABEL, "", Attribute.EDIT);
    message.setMaxLength("50");
    message.setSize("7"); // was 5
    //message.setHelpText(Email.MESSAGE_HELP);

    RowModel message_row = new RowModel();
    message_row.setId(Email.MESSAGE);
    message_row.add(message, "frm", 3);

    form.addRow(message_row);
    form.addSeparator(new Separator());

    RowModel list_row = new RowModel();
    list_row.setId(DistributionList.LIST_OF_NAMES);

    TableModel names = new TableModel(Email.RECIPIENTS_LABEL);
    names.setEditLabel(Email.TO_LABEL);
    names.setMode(Attribute.EDIT);
    //names.setHelpText(Email.TO_HELP);

    names.addColumn(new Column("name", "Member"));
    names.addColumn(new Column("actions", ActionHelper.ACTIONS_LABEL));

    list_row.add(names);

    ActionModel toActions = new ActionModel();

    String searchMembersUrl = "javascript:openNewWindow('" + versionId + Member.SEARCH_WINDOW_URL + "', '" + Member.SEARCH_WINDOW_NAME + "', '" + Member.SEARCH_WINDOW_PARAMS + "')";
    Action searchMembersAction = new Action(ActionHelper.SEARCH_MEMBERS , Labels.SEARCH_MEMBERS, "Search for members to add", searchMembersUrl);
    toActions.add(searchMembersAction);

    String searchListsUrl = "javascript:openNewWindow('" + versionId + DistributionList.SEARCH_WINDOW_URL + "', '" + DistributionList.SEARCH_WINDOW_NAME + "', '" + DistributionList.SEARCH_WINDOW_PARAMS + "')";
    Action searchListsAction = new Action(ActionHelper.SEARCH_LISTS, Labels.SEARCH_DIST_LISTS, "Search for distribution lists to add", searchListsUrl);
    toActions.add(searchListsAction);

    //
    //  for Proshop only
    //
    if (user.startsWith( "proshop" )) {

       //
       //  for Proshop only
       //
       String searchEventsUrl = "javascript:openNewWindow('" + versionId + Event.SEARCH_WINDOW_URL + "', '" + Event.SEARCH_WINDOW_NAME + "', '" + Event.SEARCH_WINDOW_PARAMS + "')";
       Action searchEventsAction = new Action(ActionHelper.SEARCH_EVENTS , Labels.SEARCH_EVENTS, "Search for events to add participants to this email", searchEventsUrl);
       toActions.add(searchEventsAction);

    } else {
       //
       //  for Members only
       //
       String addPartnersUrl = "javascript:execute('" + versionId + "servlet/Send_email', '" + ActionHelper.ADD_TO_LIST + "', '" + ActionHelper.ADD_PARTNERS + "')";
       Action addPartnersAction = new Action(ActionHelper.ADD_PARTNERS , Labels.ADD_PARTNERS, "Add partners to the email", addPartnersUrl);
       toActions.add(addPartnersAction);
    }

    names.setContextActions(toActions);

    form.addRow(list_row);

    //
    // if proshop user then allow them to add attachments
    //
    if (user.startsWith( "proshop" )) {

        form.addSeparator(new Separator());

        FileInput attach1 = new FileInput("attachment1", "Attachment #1"); // name, value
        attach1.setOnChange("document.pgFrm.attach_added.value='Y'");
        RowModel attach1_row = new RowModel();
        attach1_row.setId("attach1");
        attach1_row.add(attach1, "frm", 3);
        form.addRow(attach1_row);

        FileInput attach2 = new FileInput("attachment2", "Attachment #2");
        attach2.setOnChange("document.pgFrm.attach_added.value='Y'");
        RowModel attach2_row = new RowModel();
        attach2_row.setId("attach2");
        attach2_row.add(attach2, "frm", 3);
        form.addRow(attach2_row);

        FileInput attach3 = new FileInput("attachment3", "Attachment #3");
        attach3.setOnChange("document.pgFrm.attach_added.value='Y'");
        RowModel attach3_row = new RowModel();
        attach3_row.setId("attach3");
        attach3_row.add(attach3, "frm", 3);
        form.addRow(attach3_row);

    }

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
     
    drawEndOfPageAfterForm(out, false);
  }

  private void drawEndOfPageAfterForm(PrintWriter out, boolean disp_attach_notice)
  {
     
    if (disp_attach_notice == true) {
       
       //
       //  Proshop user and normal send email page - display notice about Attachments
       //
       out.println("<table align=\"center\" width=\"100%\" bgcolor=\"#F5F5DC\" border=\"0\" cellspacing=\"0\" cellpadding=\"10\">");
       out.println("<tr valign=\"top\"><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>");
       out.println("<b>NOTICE REGARDING ATTACHMENTS:</b><font size=2>");
       out.println("<BR><BR>It is highly recommended that you send PDF type documents whenever possible.");
       out.println("<BR>If you attach a Word or Excel document, there is no guarantee that all recipients will be able to read it.");
       out.println("<BR>To convert a Word or Excel document to PDF, open the document in the appropriate application (Word or Excel)");
       out.println("<BR>and select File - Save As. When the save panel opens, select PDF from the Format options.");
       out.println("<BR><BR>If you do not have a PDF option under Format, or you cannot create a PDF for some other reason,");
       out.println("<BR>you can download a free program for this (PrimoPDF from www.primopdf.com).");
       out.println("<BR><BR>Also note that the file cannot exceed <b>512 KB</b> in size.");
       out.println("<BR><BR>Please contact Pro Support if you have any questions or need assistance.");
       out.println("</font></td></tr></table>");
    }
    
    LayoutHelper.drawEndMainBodyContentWrapper(out); // </td></tr></table>
    // LayoutHelper.drawFooter(out);
    
    LayoutHelper.drawEndPageContentWrapper(out); // </body></html>
    out.flush();
  }

}


class ByteArrayDataSource
   implements DataSource
{
   byte[] bytes;
   String contentType,
          name;

   ByteArrayDataSource(byte[] bytes,
                       String contentType,
                       String name)
   {
      this.bytes = bytes;
      if(contentType == null)
         this.contentType = "application/octet-stream";
      else
         this.contentType = contentType;
      this.name = name;
   }

   public String getContentType()
   {
      return contentType;
   }

   public InputStream getInputStream()
   {
      // remove the final CR/LF
      return new ByteArrayInputStream(
         bytes,0,bytes.length - 2);
   }

   public String getName()
   {
      return name;
   }

   public OutputStream getOutputStream()
      throws IOException
   {
      throw new FileNotFoundException();
   }
}
