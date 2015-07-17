/***************************************************************************************
 *   Admin_proshopusers:  This servlet will display a list of proshopusers
 *
 *   created: 11/22/2003   JAG (Reworked for Proshop Users - 7/01/2008 BSK)
 *
 *   last updated:
 *
 *   9/03/09  Added processing for displaying/handling activities
 *   7/14/08  Updated/Modified to display proshop users and corresponding info
 *   4/24/08  Update Connection object to use SystemUtils.getCon()
 *
 *
 ***************************************************************************************
 */

//thrird party imports
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

//foretees imports
import com.foretees.client.ScriptHelper;

import com.foretees.client.action.Action;
import com.foretees.client.action.ActionModel;
import com.foretees.client.action.ActionHelper;

import com.foretees.client.form.FormModel;
import com.foretees.client.form.FormRenderer;

import com.foretees.client.layout.LayoutHelper;

import com.foretees.client.table.Cell;
import com.foretees.client.table.Column;
import com.foretees.client.table.TableModel;
import com.foretees.client.table.TableRenderer;
import com.foretees.client.table.RowModel;

import com.foretees.common.Labels;
import com.foretees.common.ProcessConstants;
import com.foretees.common.getActivity;

import com.foretees.common.help.Help;

import com.foretees.member.Member;

import com.foretees.common.Connect;

/**
***************************************************************************************
*
* This servlet will draw the table for listing the proshop users
*
***************************************************************************************
**/

public class Admin_proshopusers extends HttpServlet {

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
  * This method will query the database for all proshop users and will display them
  * in a table.
  *
  ***************************************************************************************
  **/

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    Connection con = null;                 // init DB objects

    PreparedStatement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;


    HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

    if (session == null) {

      return;
    }

    String club = (String)session.getAttribute("club");

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

    int activity_id = 0;                    // ID # of activity

    String activity_name = "";              // Name of activity
    String username_prev = "";              // Previous username
    String default_entry = "";              // Symbol to denote default entry;

    boolean first = true;                   // First time processing a given username?
    boolean first2 = true;

    try {

      stmt = con.prepareStatement (
               "SELECT * FROM login2 WHERE username NOT LIKE ? AND username LIKE ? ORDER BY username, activity_id");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, "proshop4tea%");     // exclude our logins!
      stmt.setString(2, "proshop%");            // put the parm in stmt
      rs = stmt.executeQuery();            // execute the prepared stmt


      TableModel users = new TableModel(Member.MEMBER_LIST_LABEL);
      users.addColumn(new Column("user_name", Member.USER_NAME_LABEL_SHORT));
      users.addColumn(new Column("activity", Member.ACTIVITY_NAME_LABEL_SHORT));
      users.addColumn(new Column("actions", ActionHelper.ACTIONS_LABEL));
      users.addColumn(new Column("last_name", Member.LAST_NAME_LABEL));
      users.addColumn(new Column("first_name", Member.FIRST_NAME_LABEL));
      users.addColumn(new Column("mi", Member.MIDDLE_INITIAL_LABEL));
      users.addColumn(new Column("inactive", Member.INACT_LABEL_SHORT));


      while(rs.next()) {

         RowModel row = new RowModel();

         //create the action model for this event and add it to the row
         ActionModel rowActions = new ActionModel();
         //escape special characters in the username
         String username = rs.getString("username");

         // Check if we're still on the same username
         if (!username.equals(username_prev)) {
             username_prev = username;
             first = true;
             first2 = true;
         }

         if (rs.getInt("default_entry") == 1) {
             default_entry = Member.DEFAULT_ACTIVITY_SYMBOL;
         } else {
             default_entry = "";
         }

         if (first) {
             row.add(username);
             first = false;
         } else {
             row.add("---" + username);
         }

         activity_id = rs.getInt("activity_id");

         if (activity_id == 0) {

             // activity is Golf
             activity_name = "Golf" + default_entry;

         } else {

             // activity is not Golf, look up activity name from activities table for this activity_id
             try {

                 pstmt = con.prepareStatement(
                         "SELECT activity_name FROM activities WHERE activity_id = ?");
                 pstmt.clearParameters();
                 pstmt.setInt(1, activity_id);
                 rs2 = pstmt.executeQuery();

                 if (rs2.next()) {
                     activity_name = rs2.getString("activity_name") + default_entry;
                 }

             } catch (Exception exc) {
                 dbError(out);
                 return;
             }

             pstmt.close();
         }

         row.add(activity_name);
         
         String escName = ScriptHelper.escapeSpecialCharacters(username);

         String editUrl = "javascript:viewProshopUser('" + versionId + "servlet/Admin_editproshopuser', '" + escName + "', " + activity_id + ")";
         Action editAction = new Action("edit", Labels.EDIT, "Edit this users information.", editUrl);
         rowActions.add(editAction);

         if (getActivity.isConfigured(con) && first2) {
             first2 = false;
             String addUrl = "javascript:goTo('" + versionId + "servlet/Admin_addproshopuser?username=" + escName + "&existing_user=1')";
             Action addAction = new Action("add", Labels.ADD_ACTIVITY, "Add an activity for this user.", addUrl);
             rowActions.add(addAction);
         }

         row.add(rowActions);


         String lastname = rs.getString("name_last");
         row.add(lastname);
         String firstname = rs.getString("name_first");
         row.add(firstname);
         String middlename = rs.getString("name_mi");
         row.add(middlename);
         
         String inactive = rs.getString("inact");
         row.add(inactive);
         
         users.addRow(row);
      }


      stmt.close();

      ActionModel pageActions = new ActionModel();
      Action addMember = new Action(ActionHelper.ADD_MEMBER, Member.ADD_PROSHOP_USER_LABEL);
      addMember.setUrl("javascript:goTo('" + versionId + "servlet/Admin_addproshopuser?username=&existing_user=0')");
      pageActions.add(addMember);

      Action userHelp = new Action(ActionHelper.HELP, Help.LABEL);
      userHelp.setUrl("javascript:openNewWindow('" + versionId + Help.VIEW_PROSHOP_USERS + "', 'ProshopUserOnlineHelp', '" + Help.WINDOW_SIZE + "')");

      pageActions.add(userHelp);

      out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Proshop Users Page"));
      LayoutHelper.drawBeginPageContentWrapper(null, null, out);

      LayoutHelper.drawBeginMainBodyContentWrapper(Member.VIEW_PROSHOP_USERS_LABEL, pageActions, out);

      //we need a form to submit the actions in the table
      FormModel form = new FormModel("pgFrm", FormModel.POST, "bot");
      form.addHiddenInput(Member.REQ_USER_NAME, "");
      form.addHiddenInput(Member.REQ_ACTIVITY_ID, "");
      form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
      form.addHiddenInput(Member.EXISTING_USER, "");
      form.setStyleSheetClass("");
      RowModel tableRow = new RowModel();
      tableRow.add(users);
      form.addRow(tableRow);

      if (getActivity.isConfigured(con)) {
          RowModel noteRow = new RowModel();
          noteRow.add("<br>Note: '*' denotes default activity");
          form.addRow(noteRow);
      }
      
      FormRenderer.render(form, out);
      LayoutHelper.drawEndMainBodyContentWrapper(out);

      LayoutHelper.drawFooter(out);
      LayoutHelper.drawEndPageContentWrapper(out);

      out.flush();
   }
   catch (Exception exc) {

      dbError(out);
      return;
   }

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
   out.println("<BR><br><A HREF=\"javascript:history.back(1)\">Return</A>");
   out.println("</CENTER></BODY></HTML>");

 }

}
