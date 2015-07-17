/***************************************************************************************
 *   Admin_memlist:  This servlet will display a list of members from the member table.
 *
 *   called by:  admin_memlist.htm (via Edit All button)
 *
 *
 *   created: 2/08/2002   Bob P.
 *
 *   last updated:
 *
 *        7/17/13   Underscores and subsequent values in last names will now be ignored when determining the sort order of the members.
 *        9/03/09   Changed processing to grab mships from mship5 instead of club5
 *        1/16/09   Add Walk/Cart option to Edit All Optional.
 *        7/19/07   Change non-golf (billing) flag labels from NON_GOLF to EXCLUDE.
 *        7/18/07   Add 'non-golf' to form for Roster Sync clubs for our billing purposes.
 *        5/17/07   Add 'inact' flag processing.
 *       12/13/06   Increase bag slot max length from 6 to 12.
 *        9/21/06   Add 2nd 'Edit All' processing so we can add more items to the edit all display.
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
import com.foretees.client.attribute.SelectionList;
import com.foretees.client.attribute.Checkbox;

import com.foretees.client.form.FormModel;
import com.foretees.client.form.FormRenderer;

import com.foretees.client.layout.LayoutHelper;

import com.foretees.client.table.Column;
import com.foretees.client.table.TableModel;
import com.foretees.client.table.TableRenderer;
import com.foretees.client.table.RowModel;

import com.foretees.common.FeedBack;
import com.foretees.common.Labels;
import com.foretees.common.ProcessConstants;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;

import com.foretees.common.Connect;

/**
***************************************************************************************
*
* This servlet will draw the table for editing multiple members
*
***************************************************************************************
**/

public class Admin_memlistedit extends HttpServlet {


  private static String versionId = ProcessConstants.CODEBASE;


  /**
  ***************************************************************************************
  *
  * This method will build the editing multiple members for the selected letter
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
    FeedBack feedback = null;
    Connection con = null;                 // init DB objects
    ResultSet rs = null;

    String letter = "";
    String orig_letter = "";
    String min = "";
    String editall = "";
    String userName = "";
    String userNameName = "";

    int inact = 0;
    int billable = 0;
    int exclude = 0;

    HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

    if (session == null) {

      return;
    }

    String club = (String)session.getAttribute("club");
    int rsync = (Integer)session.getAttribute("rsync");          // get Roster Sync indicator for this club

    con = Connect.getCon(req);            // get DB connection

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

    //  Get the editall parm if provided (button 1 or button 2)
    if (req.getParameter("editall") != null) {
       editall = req.getParameter("editall");
    }

    // Get the letter of first name requested (or 'all')
    letter = req.getParameter(Member.LETTER);
    orig_letter = letter;

    if (letter == null){
      letter = Member.VIEW_ALL;
    }

    if (letter.equalsIgnoreCase( Member.VIEW_ALL )) {
      letter = "%";        // all names
    } else {
      letter = letter + "%";
    }

    try {                            // Get all columns from member table for names requested


      Object theForm = session.getAttribute(Member.MULTI_EDIT_MEM_FRM);
      FormModel form = null;

      if ( theForm == null){     // if first time here - get all the members for the specified letter and save 


        PreparedStatement stmt = con.prepareStatement (
                 "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, m.* "
               + "FROM member2b m "
               + "WHERE name_last LIKE ? AND deleted = 0 "
               + "ORDER BY last_only, name_last, name_first, name_mi");

        stmt.clearParameters();               // clear the parms
        stmt.setString(1, letter);            // put the parm in stmt
        rs = stmt.executeQuery();            // execute the prepared stmt


        TableModel members = new TableModel("Update Member Information");
        members.addColumn(new Column("last_name", Member.LAST_NAME_LABEL));
        members.addColumn(new Column("first_name", Member.FIRST_NAME_LABEL));
        members.addColumn(new Column("middle_initial", Member.MIDDLE_INITIAL_LABEL));
        members.addColumn(new Column("member_number", Member.MEM_NUM_LABEL_SHORT));
          
        if (editall.equals( "1" )) {       // if first Edit All button (required parms)
  
           members.addColumn(new Column("user_name", Member.USER_NAME_LABEL_SHORT));
           members.addColumn(new Column("membership", Member.MEMSHIP_TYPE_LABEL));
           members.addColumn(new Column("member_type", Member.MEM_TYPE_LABEL));
           members.addColumn(new Column("inactive", Member.INACT_LABEL_SHORT)); 
           if (rsync > 0) {                                          // if Roster Sync club
              members.addColumn(new Column("exclude", Member.EXCLUDE_LABEL_SHORT));   // add Exclude column
           }

        } else {                         // 2nd Edit All button (optional parms)

           members.addColumn(new Column("member_pos", Member.POS_ID_LABEL_SHORT));
           members.addColumn(new Column("bag_slot", Member.BAG_SLOT_LABEL_SHORT));
           members.addColumn(new Column("handicap", Member.GHIN_LABEL_SHORT));
           members.addColumn(new Column("webid", Member.WEB_ID_LABEL_SHORT));
           members.addColumn(new Column("walk_cart", Member.WALK_CART_LABEL_SHORT));
        }

        Member member = new Member();

        int numRecords = 0;

        //we need a form to submit the actions in the table  -  goes to Admin_editmultimem
        form = new FormModel("pgFrm", FormModel.POST, "bot");
        form.setStyleSheetClass("");

        while(rs.next()) {

          numRecords = numRecords + 1;

          RowModel row = new RowModel();

          userName = rs.getString("username");
          row.setId(userName);
          String uniqueCellId = ":" + userName;

          //last name
          String lastNameName = member.LAST_NAME + uniqueCellId;
          Attribute lastName = new Attribute(lastNameName,"", rs.getString("name_last"), Attribute.EDIT);
          lastName.setSize("20");
          lastName.setMaxLength("20");
          row.add(lastName);

          //first name
          String firstNameName = member.FIRST_NAME + uniqueCellId;
          Attribute firstName = new Attribute(firstNameName,"", rs.getString("name_first"), Attribute.EDIT);
          firstName.setSize("20");
          firstName.setMaxLength("20");
          row.add(firstName);

          //middle initial
          String midInitName = Member.MIDDLE_INITIAL +  uniqueCellId;
          Attribute midInit = new Attribute(midInitName,"", rs.getString("name_mi"), Attribute.EDIT);
          midInit.setSize("1");
          midInit.setMaxLength("1");
          row.add(midInit);

          //member number
          String memberNumberName = Member.MEM_NUM + uniqueCellId;
          Attribute memberNumber = new Attribute(memberNumberName,"", rs.getString("memNum"), Attribute.EDIT);
          memberNumber.setSize("10");
          memberNumber.setMaxLength("10");
          row.add(memberNumber);

          //get the user name
          userNameName = Member.USER_NAME + uniqueCellId;

          if (editall.equals( "1" )) {       // if first Edit All button (required parms)

             //get the user name
             Attribute userNameAtt = new Attribute(userNameName,"", rs.getString("username"), Attribute.EDIT);
             userNameAtt.setSize((new Integer(Member.USER_NAME_MAX_LENGTH)).toString());
             userNameAtt.setMaxLength((new Integer(Member.USER_NAME_MAX_LENGTH)).toString());
             row.add(userNameAtt);

             //need to construct a list of all possible memberships and select the one that
             //is currently set for this user
             String memShip = rs.getString("m_ship");  //member ship
             String msSelLstName = Member.MEMSHIP_TYPE + uniqueCellId;
             SelectionList memShipList = MemberHelper.getMemberShips(con, memShip, msSelLstName);
             memShipList.setLabel("");
             row.add(memShipList);

             //need to construct a list of all possible membertypes and select the one that
             //is currently set for this user
             String memType = rs.getString("m_type");  //member type
             String mtSelLstName = Member.MEM_TYPE + uniqueCellId;
             SelectionList memTypeList = MemberHelper.getMemberTypes(con, memType, mtSelLstName);
             memTypeList.setLabel("");
             row.add(memTypeList);
               
            
             //need to construct a check box for the inact flag
             String memberInact = Member.MEM_INACT + uniqueCellId;
             inact = rs.getInt("inact");                               // inact flag (1 = inact)
             Checkbox cb = null;
             if (inact == 1) {                                         // inactive?
                cb = new Checkbox(memberInact, "", "1", true);
             } else {
                cb = new Checkbox(memberInact, "", "1", false);
             }
             row.add(cb, "frm");


             if (rsync > 0) {                                          // if Roster Sync club
        
                //need to construct a check box for the billable (exclded) flag
                String memberBill = Member.EXCLUDE + uniqueCellId;
                billable = rs.getInt("billable");                               // billable flag (1 = billable)
                Checkbox cb1 = null;
                if (billable == 1) {                                         // billable?
                   cb1 = new Checkbox(memberBill, "", "1", false);
                } else {
                   cb1 = new Checkbox(memberBill, "", "1", true);            // if excluded, they are not billable
                }
                row.add(cb1, "frm");
             }


          } else {   // 2nd Edit All button (optional parms)

             form.addHiddenInput(userNameName, userName);    // must pass the username

             //pos id
             String memberPosId = Member.POS_ID + uniqueCellId;
             Attribute posid = new Attribute(memberPosId, "", rs.getString("posid"), Attribute.EDIT); // parms = (name, label, value, mode)
             posid.setSize("10");
             posid.setMaxLength("15");
             row.add(posid);

             //bag slot id
             String bagSlotName = Member.BAG_SLOT + uniqueCellId;
             Attribute bagSlot = new Attribute(bagSlotName,"", rs.getString("bag"), Attribute.EDIT);
             bagSlot.setSize("6");
             bagSlot.setMaxLength("12");
             row.add(bagSlot);  //bag slot

             //handicap number (GHIN #)
             String hndcpSlotName = Member.GHIN + uniqueCellId;
             Attribute hndcpSlot = new Attribute(hndcpSlotName,"", rs.getString("ghin"), Attribute.EDIT);
             hndcpSlot.setSize("10");
             hndcpSlot.setMaxLength("16");
             row.add(hndcpSlot);

             //Web Site Id
             String webidSlotName = Member.WEBID + uniqueCellId;
             Attribute webidSlot = new Attribute(webidSlotName,"", rs.getString("webid"), Attribute.EDIT);
             webidSlot.setSize("8");
             webidSlot.setMaxLength("15");
             row.add(webidSlot);
             
             //need to construct a list of all possible walk/cart options and select the one that
             //is currently set for this user
             String wcType = rs.getString("wc");  //walk/cart type
             String wcSelLstName = Member.WALK_CART + uniqueCellId;
             SelectionList wcTypeList = MemberHelper.getWalkCartOptionsAll(con, wcType, wcSelLstName);
             wcTypeList.setLabel("");
             row.add(wcTypeList);
          }

          members.addRow(row);

        }

        stmt.close();

        // complete the form
        form.addHiddenInput("numRecords", (new Integer(numRecords)).toString());
        form.addHiddenInput("formId", Member.MULTI_EDIT_MEM_FRM);
        form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
        form.addHiddenInput(Member.LETTER, orig_letter);


        //create the action model for this form and add it to the form model
        ActionModel formActions = new ActionModel();

        String applyUrl = "javascript:update('" + versionId + "servlet/Admin_editmultimem?letter=" + orig_letter + "&editall=" +editall+ "', 'updateAndReturn')";
        Action applyAction = new Action("updateAndReturn", Labels.APPLY, "Save the changes and return to this page.", applyUrl);
        formActions.add(applyAction);

        String okUrl = "javascript:update('" + versionId + "servlet/Admin_editmultimem?editall=" +editall+ "', 'updateAndClose')";
        Action okAction = new Action("updateAndClose", Labels.OK, "Save the changes and return to Members menu.", okUrl);
        formActions.add(okAction);

        //get the number of pages and determine if the paging buttons need to be displayed
        members.setPaging(true);
        members.setPageSize(Member.MULTI_EDIT_PAGE_SIZE);
        int num_pages = members.getNumberOfPages();


        if (num_pages > 1){
          String saveNextUrl = "javascript:update('" + versionId + "servlet/Admin_editmultimem?editall=" +editall+ "', 'saveAndNext')";
          Action saveNextAction = new Action(ActionHelper.SAVE_AND_NEXT, Labels.SAVE_AND_NEXT, "Save the changes and move to the next page of members.", saveNextUrl);

          if ( members.getCurrentPage() >= num_pages)
          {
            saveNextAction.setSelected(true);
          }
          formActions.add(saveNextAction);

          String savePrevUrl = "javascript:update('" + versionId + "servlet/Admin_editmultimem?editall=" +editall+ "', 'saveAndPrev')";
          Action savePrevAction = new Action(ActionHelper.SAVE_AND_PREV, Labels.SAVE_AND_PREVIOUS, "Save the changes and move to the previous page of members.", savePrevUrl);

          if ( members.getCurrentPage() <= 1)
          {
            savePrevAction.setSelected(true);
          }
          formActions.add(savePrevAction);
        }

        String cancelUrl = "javascript:cancel('" + versionId + "servlet/Admin_editmultimem', 'cancel', 'Any changes you have made since you last saved will be lost.  Are you sure you want to cancel?')";
        Action cancelAction = new Action("cancel", Labels.CANCEL, "Return without saving changes", cancelUrl);
        formActions.add(cancelAction);

        form.setActions(formActions);


        //add a row for the table to the form
        RowModel tableRow = new RowModel();
        tableRow.setId(Member.MEM_LIST_TABLE);
        tableRow.add(members);
        form.addRow(tableRow);
        req.getSession().setAttribute(Member.MULTI_EDIT_MEM_FRM, form);


      }
      else{                                     // Not first page - form already exists
        form = (FormModel)(theForm);
        feedback = (FeedBack)(session.getAttribute(Member.MULTI_MEM_FEEDBACK));
        session.removeAttribute(Member.MULTI_MEM_FEEDBACK);
      }

      out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Members Page"));

      String onLoad = null;

      if (feedback != null)
      {
        onLoad = "javascript:displayFeedbackMessage('" + feedback.get(0) + "');";
      }

      //String onUnLoad = "javascript:cleanup('" + versionId + "servlet/Admin_editmultimem', 'cleanup')";
      String onUnLoad = "javascript:cleanup('" + versionId + "servlet/Admin_editmultimem', 'cleanup', 'Any changes you have made since you last saved will be lost.')";


      LayoutHelper.drawBeginPageContentWrapper(onLoad, onUnLoad, out);
      out.print("<td width=\"25%\" bgcolor=\"#ccccaa\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");

      FormRenderer.render(form, out);

      LayoutHelper.drawEndPageContentWrapper(out);

      out.flush();

   }
   catch (Exception exc) {


      dbError(out);
      return;
   }

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

}
