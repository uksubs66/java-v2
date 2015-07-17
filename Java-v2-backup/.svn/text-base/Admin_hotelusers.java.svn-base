/***************************************************************************************
 *   Admin_hotelusers:  This servlet will display a list of hotelusers
 *
 *   created: 11/22/2003   JAG
 *
 *   last updated:
 *
 *      4/24/08  Update Connection object to use SystemUtils.getCon()
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

import com.foretees.common.help.Help;

import com.foretees.member.Member;
import com.foretees.common.Connect;

/**
***************************************************************************************
*
* This servlet will draw the table for listing the hotel users
*
***************************************************************************************
**/

public class Admin_hotelusers extends HttpServlet {

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
  * This method will query the database for all hotel users and will display them
  * in a table.
  *
  ***************************************************************************************
  **/

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    Connection con = null;                 // init DB objects
    ResultSet rs = null;

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

    String letter = "%";

    try {

      PreparedStatement stmt = con.prepareStatement (
               "SELECT * FROM hotel3  WHERE name_last LIKE ? ORDER BY name_last, name_first, name_mi");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, letter);            // put the parm in stmt
      rs = stmt.executeQuery();            // execute the prepared stmt


      TableModel users = new TableModel(Member.MEMBER_LIST_LABEL);
      users.addColumn(new Column("last_name", Member.LAST_NAME_LABEL));
      users.addColumn(new Column("first_name", Member.FIRST_NAME_LABEL));
      users.addColumn(new Column("mi", Member.MIDDLE_INITIAL_LABEL));
      users.addColumn(new Column("actions", ActionHelper.ACTIONS_LABEL));
      users.addColumn(new Column("user_name", Member.USER_NAME_LABEL_SHORT));


      while(rs.next()) {

         RowModel row = new RowModel();

         String lastname = rs.getString("name_last");
         row.add(lastname);
         String firstname = rs.getString("name_first");
         row.add(firstname);
         String middlename = rs.getString("name_mi");
         row.add(middlename);

         //create the action model for this event and add it to the row
         ActionModel rowActions = new ActionModel();
         //escape special characters in the username
         String username = rs.getString("username");
         String escName = ScriptHelper.escapeSpecialCharacters(username);

         String editUrl = "javascript:viewUser('" + versionId + "servlet/Admin_edithoteluser', '" + escName + "')";
         Action editAction = new Action("edit", Labels.EDIT, "Edit this users information.", editUrl);
         rowActions.add(editAction);

         String displayName = firstname + " " + lastname + " (" + username + ")";
         String escDisplayName = ScriptHelper.escapeSpecialCharacters(displayName);
         String deleteUrl = "javascript:deleteUser('" + versionId + "servlet/Admin_updatehoteluser" + "', '" + escName + "','" + escDisplayName + "')";
         Action deleteAction = new Action("delete", Labels.DELETE, "Delete this user from the database.", deleteUrl);
         rowActions.add(deleteAction);
         row.add(rowActions);

         row.add(username);

         users.addRow(row);
      }


      stmt.close();

      ActionModel pageActions = new ActionModel();
      Action addMember = new Action(ActionHelper.ADD_MEMBER, Member.ADD_HOTEL_USER_LABEL);
      addMember.setUrl("javascript:goTo('" + versionId + "servlet/Admin_addhoteluser')");
      pageActions.add(addMember);

      Action userHelp = new Action(ActionHelper.HELP, Help.LABEL);
      userHelp.setUrl("javascript:openNewWindow('" + versionId + Help.VIEW_HOTEL_USERS + "', 'HotelUserOnlineHelp', '" + Help.WINDOW_SIZE + "')");

      pageActions.add(userHelp);

      out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Hotel Users Page"));
      LayoutHelper.drawBeginPageContentWrapper(null, null, out);
      ActionHelper.drawAdminHotelUserNavBar(ActionHelper.HOTEL_USER_LIST, out);

      LayoutHelper.drawBeginMainBodyContentWrapper(Member.VIEW_HOTEL_USERS_LABEL, pageActions, out);

      //we need a form to submit the actions in the table
      FormModel form = new FormModel("pgFrm", FormModel.POST, "bot");
      form.addHiddenInput(Member.REQ_USER_NAME, "");
      form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
      form.setStyleSheetClass("");
      RowModel tableRow = new RowModel();
      tableRow.add(users);
      form.addRow(tableRow);

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
