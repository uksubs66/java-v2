/***************************************************************************************
 *   Admin_memlist:  This servlet will display a list of members from the member table.
 *
 *   called by:  admin_memlist.htm
 *
 *
 *   created: 2/08/2002   Bob P.
 *
 *   last updated:
 *
 *        5/14/09   Add anchor tags to reload member list at same place as left off (after you return from editing a member)
 *        7/19/07   Change non-golf (billing) flag labels from NON_GOLF to EXCLUDE.
 *        7/18/07   Add 'non-golf' column to list for Roster Sync clubs for our billing purposes.
 *        5/23/07   Add 'inact' (status) column.
 *        5/17/07   Mark inactive members by making their name red.
 *        9/21/06   Add 2nd 'Edit All' button so we can add more items to the edit all display.
 *       11/01/03   Enhancements for Version 3 of the software.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        1/14/03   Enhancements for Version 2 of the software.
 *
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

/**
***************************************************************************************
*
* This servlet will draw the table for listing the members based on the letter selection
* picked by the user
*
***************************************************************************************
**/

public class Admin_memlist extends HttpServlet {

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
  * This method will query the database based on the input from the user to find all the
  * members based on the letter provided.  If not letter is provided all members will be
  * returned.  The members will be displayed in a table.
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

    Connection con = null;                 // init DB objects
    ResultSet rs = null;

    String letter = "";
    String orig_letter = "";
    String min = "";
    String view_all = "view all";

    HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

    if (session == null) {

      return;
    }

    String club = (String)session.getAttribute("club");
    int rsync = (Integer)session.getAttribute("rsync");          // get Roster Sync indicator for this club

    con = SystemUtils.getCon(session);            // get DB connection

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

    String action = req.getParameter(ActionHelper.NEXT_ACTION);

    if (action != null && action.equals("report"))
      printReport(out, con);
    else
      printMemberList(out, con, req, resp, rsync);

 }

 /**
  ***************************************************************************************
  *
  * Prints a table of members based on the letter passed in.
  *
  ***************************************************************************************
  **/
 protected void printMemberList(PrintWriter out, Connection con, HttpServletRequest req, HttpServletResponse res, int rsync)
 {

    ResultSet rs = null;
    String letter = "";
    String orig_letter = "";
    String min = "";
    String view_all = "view all";

    int inact = 0;
    int billable = 0;
    int exclude = 0;

    //
    // Get the letter of first name requested (or 'all')
    //
    letter = req.getParameter(Member.LETTER);
    orig_letter = letter;

    if (letter.equalsIgnoreCase( view_all )) {

      letter = "%";        // all names
    } else {

       letter = letter + "%";
    }


    try {                            // Get all columns from member table for names requested

      String order_by = "name_last, name_first, name_mi";

      String sort_by = req.getParameter("sortby");

      if (sort_by != null && !(sort_by.equals("")))
        order_by = sort_by + "," + order_by;

      PreparedStatement stmt = con.prepareStatement (
               "SELECT * FROM member2b WHERE name_last LIKE ? ORDER BY " + order_by);

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, letter);            // put the parm in stmt
      rs = stmt.executeQuery();            // execute the prepared stmt


      TableModel users = new TableModel(Member.MEMBER_LIST_LABEL);
      users.addColumn(new Column("last_name", "<A class=\"btnHref\" href=\"" + versionId + "servlet/Admin_memlist?letter=" + orig_letter + "&sortby=name_last\">" + Member.LAST_NAME_LABEL+ "</A>"));
      users.addColumn(new Column("first_name", "<A class=\"btnHref\" href=\"" + versionId + "servlet/Admin_memlist?letter=" + orig_letter + "&sortby=name_first\">" + Member.FIRST_NAME_LABEL+ "</A>"));
      users.addColumn(new Column("mi", Member.MIDDLE_INITIAL_LABEL));
      users.addColumn(new Column("actions", ActionHelper.ACTIONS_LABEL));
      users.addColumn(new Column("user_name", Member.USER_NAME_LABEL_SHORT));
      users.addColumn(new Column("membership", "<A class=\"btnHref\" href=\"" + versionId + "servlet/Admin_memlist?letter=" + orig_letter + "&sortby=m_ship\">" + Member.MEMSHIP_TYPE_LABEL+ "</A>"));
      users.addColumn(new Column("memberType", "<A class=\"btnHref\" href=\"" + versionId + "servlet/Admin_memlist?letter=" + orig_letter + "&sortby=m_type\">" + Member.MEM_TYPE_LABEL+ "</A>"));
      users.addColumn(new Column("mem#", Member.MEM_NUM_LABEL_SHORT));
      users.addColumn(new Column("member_pos", Member.POS_ID_LABEL_SHORT));
      users.addColumn(new Column("bagSlot", Member.BAG_SLOT_LABEL_SHORT));
      users.addColumn(new Column("email", Member.EMAIL_LABEL_SHORT));
      users.addColumn(new Column("inactive", "Status"));
      if (rsync > 0) {                                          // if Roster Sync club
         users.addColumn(new Column("exclude", "Exclude"));
      }


      while(rs.next()) {


         //escape special characters in the username
         String username = rs.getString("username");
         String escName = ScriptHelper.escapeSpecialCharacters(username);
         
         RowModel row = new RowModel();

         String lastname = rs.getString("name_last");
         row.add("<a name=\"" + username + "\">" + lastname);       // include anchor tag for redirection purposes
         String firstname = rs.getString("name_first");
         row.add(firstname);
         String middlename = rs.getString("name_mi");
         row.add(middlename);

         //create the action model for this event and add it to the row
         ActionModel rowActions = new ActionModel();

         String editUrl = "javascript:viewUser('" + versionId + "servlet/Admin_editmain', '" + escName + "')";
         Action editAction = new Action("edit", Labels.EDIT, "Edit this users information.", editUrl);
         rowActions.add(editAction);

         String displayName = firstname + " " + lastname + " (" + username + ")";
         String escDisplayName = ScriptHelper.escapeSpecialCharacters(displayName);
         String deleteUrl = "javascript:deleteUser('" + versionId + "servlet/Admin_editmem?letter=" + orig_letter + "', '" + escName + "','" + escDisplayName + "')";
         Action deleteAction = new Action("delete", Labels.DELETE, "Delete this user from the database.", deleteUrl);
         rowActions.add(deleteAction);
         row.add(rowActions);

         row.add(username);
         row.add(rs.getString("m_ship"));
         row.add(rs.getString("m_type"));
         row.add(rs.getString("memNum"));
         row.add(rs.getString("posid"));
         row.add(rs.getString("bag"));
         row.add(rs.getString("email"));
           
         // add inact status
         inact = rs.getInt("inact");                               // inact flag (1 = inact)
         if (inact == 1) {                                         // inactive?
            row.add(" Inact");
         } else {
            row.add(" A");
         }           

         if (rsync > 0) {                                          // if Roster Sync club
           
            // add billable (excluded) status
            billable = rs.getInt("billable");                         // billable flag (1 = billable)
            if (billable == 1) {                                      // billable ?
               row.add(" In");                                          // Yes
            } else {
               row.add(" Ex");                                         // excluded
            }
         }

         users.addRow(row);
      }

      stmt.close();

      ActionModel pageActions = new ActionModel();
      Action addMember = new Action(ActionHelper.ADD_MEMBER, Member.ADD_MEMBER_LABEL);
      addMember.setUrl("javascript:goTo('" + versionId + "servlet/Admin_addmem')");
      pageActions.add(addMember);

      if (users.size() > 0 && !(orig_letter.equalsIgnoreCase(view_all) || orig_letter.equals(""))){   // if a letter was selected

        Action editAll = new Action(ActionHelper.EDIT_ALL, Labels.EDIT_ALL);
        editAll.setUrl("javascript:goTo('" + versionId + "servlet/Admin_memlistedit?letter=" + orig_letter + "&editall=1')");
        pageActions.add(editAll);

        Action editAll2 = new Action(ActionHelper.EDIT_ALL2, Labels.EDIT_ALL2);
        editAll2.setUrl("javascript:goTo('" + versionId + "servlet/Admin_memlistedit?letter=" + orig_letter + "&editall=2')");
        pageActions.add(editAll2);
      }

      Action userHelp = new Action(ActionHelper.HELP, Help.LABEL);
      userHelp.setUrl("javascript:openNewWindow('" + versionId + Help.VIEW_MEMBERS + "', 'EvntSetupOnlineHelp', '" + Help.WINDOW_SIZE + "')");

      pageActions.add(userHelp);

      out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Members Page"));
      LayoutHelper.drawBeginPageContentWrapper(null, null, out);

      //we need a form to submit the actions in the table
      FormModel form = new FormModel("pgFrm", FormModel.POST, "bot");
      form.addHiddenInput(Member.REQ_USER_NAME, "");
      form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
      form.addHiddenInput(Member.LETTER, orig_letter);
      form.setStyleSheetClass("");
      RowModel tableRow = new RowModel();
      tableRow.add(users);
      form.addRow(tableRow);
      users.setContextActions(pageActions);

      FormRenderer.render(form, out);

      LayoutHelper.drawFooter(out);
      LayoutHelper.drawEndPageContentWrapper(out);

      out.flush();
   }
   catch (Exception exc) {
      dbError(out, exc);
      return;
   }
 }

 /**
  ***************************************************************************************
  *
  * Prints the report for the member type/membership statistics.
  *
  *
  ***************************************************************************************
  **/

  protected void printReport(PrintWriter out, Connection con)
  {


      ResultSet rs = null;
      ResultSet rs2 = null;
      PreparedStatement stmt1 = null;
      PreparedStatement stmt2 = null;

      int count1 = 0;
      int count2 = 0;


      out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Members Page"));
      LayoutHelper.drawBeginPageContentWrapper(null, null, out);

      try {
      
        //
        //  Get total number of members in db table
        //
        stmt1 = con.prepareStatement (
                 "SELECT COUNT(*) FROM member2b");

        stmt1.clearParameters();               // put the parm in stmt
        rs = stmt1.executeQuery();            // execute the prepared stmt

        if(rs.next()) {

           count1 = rs.getInt("COUNT(*)");

           out.println("<br><br><font size=\"3\">");
           out.println("<b>Total number of members:&nbsp;&nbsp; " + count1 + "</b><br><br>");
           out.println("</font><font size=\"2\"><br>");
        }
        stmt1.close();

        //
        //  Get each mship type
        //
        stmt1 = con.prepareStatement (
           "SELECT m_ship, COUNT(*) FROM member2b GROUP BY m_ship");

        stmt1.clearParameters();               // put the parm in stmt
        rs = stmt1.executeQuery();            // execute the prepared stmt

        TableModel stats = new TableModel("Member Database Report");
        stats.setPrintNumItems(false);
        stats.addColumn(new Column("membership", Member.MEMSHIP_TYPE_LABEL));
        stats.addColumn(new Column("memberType", Member.MEM_TYPE_LABEL));
        stats.addColumn(new Column("count", "Count"));


        while(rs.next()) {

           String mship1 = rs.getString("m_ship");
           count1 = rs.getInt("COUNT(*)");
             
           String counts1 = String.valueOf(count1);

           RowModel row = new RowModel();

           row.add(mship1, "rwDtaB", 2);
           row.add(counts1, "rwDtaB");

           stats.addRow(row);

           //
           //  Get the mtype and count for each mship type
           //
           stmt2 = con.prepareStatement (
                 "SELECT m_type, COUNT(*) FROM member2b WHERE m_ship = ? GROUP BY m_type");

           stmt2.clearParameters();                    // clear the parms
           stmt2.setString(1, rs.getString("m_ship")); // put the parm in stmt
           rs2 = stmt2.executeQuery();                 // execute the prepared stmt

           while(rs2.next()) {

             String mtype2 = rs2.getString("m_type");
             count2 = rs2.getInt("COUNT(*)");

             String counts2 = String.valueOf(count2);
           
             RowModel row2 = new RowModel();
             row2.add("&nbsp;&nbsp;");
             row2.add(mtype2);
             row2.add(counts2);

             stats.addRow(row2);
           }
           stmt2.close();
        }

        stmt1.close();
        TableRenderer.render(stats, out);

        out.println("<br><center><font size=\"2\">");
        out.println("<form method=\"get\" action=\"/" + SystemUtils.REVLEVEL + "/servlet/Admin_members\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</input></form></font></center><br>");

        LayoutHelper.drawEndMainBodyContentWrapper(out);
        LayoutHelper.drawEndPageContentWrapper(out);

      }
      catch (Exception exc) {
        dbError(out, exc);
      }

  }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception exc) {

   out.println(SystemUtils.HeadTitleAdmin("Database Error"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR><BR>Exception: "+ exc.getMessage());
   out.println("<BR><BR>Please try again later.");
   out.println("<BR>If problem persists, contact customer support.");
   out.println("<BR><BR>");
   out.println("<a href=\"javascript:history.back(1)\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }

}
