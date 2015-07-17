/***************************************************************************************
 *   Communication:  This servlet will display the main page that allows user set up communication
 *                   channels between other members and the propshop
 *
 *
 *   created: 1/14/2004   JAG
 *
 *   last updated:
 *
 *          5/08/12  Allow access from Dining Admin user.
 *          7/18/08  Added limited access proshop users checks
 *          4/24/08  Update ArrayList to use String instead of raw types
 *          1/25/04  Add processing to query the distribution lists (dist4 table).
 *                   Also, get the username from the session (proshop vs. member).
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

import com.foretees.client.ScriptHelper;
import com.foretees.client.action.Action;
import com.foretees.client.action.ActionModel;
import com.foretees.client.action.ActionHelper;
import com.foretees.client.attribute.Attribute;
import com.foretees.client.attribute.SelectionList;
import com.foretees.client.form.FormModel;
import com.foretees.client.form.FormRenderer;
import com.foretees.client.layout.Separator;
import com.foretees.client.layout.LayoutHelper;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.client.misc.LetterChooser;
import com.foretees.client.misc.NameSelector;
import com.foretees.client.table.Cell;
import com.foretees.client.table.Column;
import com.foretees.client.table.RowModel;
import com.foretees.client.table.TableModel;
import com.foretees.common.FeedBack;
import com.foretees.common.Labels;
import com.foretees.common.ProcessConstants;
import com.foretees.common.help.Help;
import com.foretees.communication.CommunicationHelper;
import com.foretees.communication.DistributionList;
import com.foretees.communication.Email;
import com.foretees.common.Connect;
import com.foretees.common.Utilities;

/**
***************************************************************************************
*
* This servlet will process the display the main portal page for allowing members to communicate
* with other members and the proshop
*
***************************************************************************************
**/

public class Communication extends HttpServlet {

  //initialize the attributes
  private static String versionId = ProcessConstants.CODEBASE;

  static String DINING_USER = ProcessConstants.DINING_USER;               // Dining username for Admin user from Dining System
 

  /**
  ***************************************************************************************
  *
  * This method will process the data from the multi edit member screen
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
       out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
       out.println("</CENTER></BODY></HTML>");
    }

    //
    //  ***** get user id so we know if proshop or member
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)

    Connection con = Connect.getCon(req);            // get DB connection

    boolean isDining = user.equals(DINING_USER);
   
    
    if (isDining == false) {
         
        // If proshop, check Feature Access Rights for current proshop user
        if (ProcessConstants.isProshopUser(user)) {
            if (!SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
                SystemUtils.restrictProshop("TOOLS_EMAIL", out);
                return;
            }
        }
    }
    
    String excel = (req.getParameter("excel") == null) ? "" : req.getParameter("excel");
    
    if (excel.equals("yes")) {
        excelDistribution(session, req, resp, con, out);
        return;
    }
    
    showDistLists(session, req, con, out);

  }

  private void showDistLists(HttpSession session, HttpServletRequest req, Connection con, PrintWriter out)
  {


    TableModel lists = new TableModel("Email Distribution Lists");
    lists.addColumn(new Column("list_name", "Distribution List Name"));
    lists.addColumn(new Column("actions", ActionHelper.ACTIONS_LABEL));

    String table_name = DistributionList.getTableName(session);

    //
    //  Get this user's distribution lists, if any, and list them by name
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)

    boolean isDining = user.equals(DINING_USER);
   
    try {

       PreparedStatement pstmt = con.prepareStatement (
                "SELECT name FROM " + table_name + " WHERE owner = ? ORDER BY name");

       pstmt.clearParameters();            // clear the parms
       pstmt.setString(1, user);           // put username in statement
       ResultSet rs = pstmt.executeQuery();          // execute the prepared stmt

       while (rs.next()) {

          RowModel row = new RowModel();

          String list_name = rs.getString("name");
          row.add(list_name);


          //create the action model for this event and add it to the row
          ActionModel rowActions = new ActionModel();

          //escape special characters in the distribution list name
          String escName = ScriptHelper.escapeSpecialCharacters(list_name);

//          String editUrl = "javascript:viewList('" + versionId + "servlet/Edit_distributionlist', '" + escName + "', 'edit')";
          String editUrl = "javascript:viewList('Edit_distributionlist', '" + escName + "', '')";
          Action editAction = new Action("edit", Labels.EDIT, "Edit this distribution list.", editUrl);
          rowActions.add(editAction);

          String deleteUrl = "javascript:deleteList('Edit_distributionlist', '" + escName + "','delete')";
          Action deleteAction = new Action("delete", Labels.DELETE, "Delete this distribution list from the database.", deleteUrl);
          rowActions.add(deleteAction);
          //row.add(rowActions);
          
          String excelUrl = "Communication?excel=yes&listname="+list_name;
          Action excelAction = new Action("excel", Labels.EXCEL, "Excel", excelUrl);
          rowActions.add(excelAction);
          row.add(rowActions);

          lists.addRow(row);
       }

       pstmt.close();

    }
    catch (Exception exc) {             // SQL Error

       out.println(SystemUtils.HeadTitle("DB Access Error"));
       out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
       out.println("<BR><BR><H2>Database Access Error 1</H2>");
       out.println("<BR><BR>Unable to process database change at this time.");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact your club manager or ForeTees support.");
       out.println("<BR><BR>");
       if (user.startsWith( "proshop" )) {
           if (isDining) {
               out.println("<form><input type=\"button\" style=\"background:#8B8970\" Value=\" Return To Dining System \" onClick='self.close()' alt=\"Close\"></form>");
           } else {
              out.println("<a href=\"Proshop_announce\">Return</a>");
           }
       } else {
          out.println("<a href=\"Member_announce\">Return</a>");
       }
       out.println("</CENTER></BODY></HTML>");
       return;
    }


    ActionModel pageActions = new ActionModel();
    //Action addList = new Action("addList", DistributionList.ADD_LIST_LABEL);
    //addList.setUrl("javascript:addList('" + versionId + "servlet/Add_distributionlist', '')");
    //pageActions.add(addList);

    //Action listHelp = new Action(ActionHelper.HELP, Help.LABEL);
    //listHelp.setUrl("javascript:openNewWindow('" + versionId + Help.VIEW_DIST_LIST + "', 'DistListOnlineHelp', '" + Help.WINDOW_SIZE + "')");

    //pageActions.add(listHelp);

    out.println(SystemUtils.HeadTitleAdmin(DistributionList.COMMUNICATION_HEADER));
    LayoutHelper.drawBeginPageContentWrapper(null, null, out);
    //ActionHelper.drawDistListNavBar(ActionHelper.VIEW_DIST_LIST, out);

    String caller = (String)session.getAttribute("caller");     // get caller (web site?)

    if (isDining == false) {
        
        if (ProcessConstants.isProshopUser(user)) {
       
          String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
          int lottery = Integer.parseInt(templott);
          SystemUtils.getProshopSubMenu(req, out, lottery);
          
        } else if (ProcessConstants.isAdminUser(user)) {
          //fix later
        } else {
          SystemUtils.getMemberSubMenu(req, out, caller);
        }
    }


    //we need a form to submit the actions in the table
    FormModel form = null;
    if (isDining) {
        form = new FormModel("pgFrm", FormModel.POST, "_top");
    } else {
        form = new FormModel("pgFrm", FormModel.POST, "bot");
    }
    form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
    form.addHiddenInput(ActionHelper.ACTION, "");
    form.addHiddenInput(DistributionList.LIST_NAME, "");

    //Add the help steps for the form
    ArrayList <String> helpSteps = new ArrayList<String>(2);
    helpSteps.add(DistributionList.EDIT_LIST_HELP);
    helpSteps.add(DistributionList.DELETE_LIST_HELP);

    form.setHelpSteps(helpSteps);

    form.setStyleSheetClass("frmWrp");
    RowModel tableRow = new RowModel();
    tableRow.add(lists, "frm", 1);
    form.addRow(tableRow);

    FormRenderer.render(form, out);
    LayoutHelper.drawEndPageContentWrapper(out);
    
    if (isDining) {
        
       //out.println("<BR><BR><p align=\"center\"><a href=\"Proshop_dining_sendEmail\">Done - Return</a></p>");
       out.println("<BR><BR><p align=\"center\"><button class=\"btnNorm\" onclick=\"location.href='Proshop_dining_sendEmail'\">Done - Return</button></p>");
    }

    out.flush();

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that comes before the add list
  * form
  *
  * @param feedback the feedback model that contains any messages to present to the user
  *                 upon loading the page
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private void drawBeginningOfPageBeforeAddForm(FeedBack feedback, PrintWriter out, HttpServletRequest request, HttpSession session)
  {
    ActionModel pageActions = new ActionModel();
    Action addListHelp = new Action(ActionHelper.HELP, Labels.HELP);
    addListHelp.setUrl("javascript:openNewWindow('" + versionId + Help.ADD_DIST_LIST + "', 'AddDistListOnlineHelp', 'width=250, height=300, directories=no, location=no, menubar=no, scrollbars=yes, status=no, toolbar=no, resizable=yes')");

    pageActions.add(addListHelp);

    out.println(SystemUtils.HeadTitleAdmin(DistributionList.ADD_DIST_LIST_HEADER));
    String onLoad = "";

    if (feedback != null)
    {
      onLoad = "javascript:displayFeedbackMessage('" + feedback.get(0) + "');document.pgFrm." + feedback.getAffectedField() + ".focus()";
    }
    else
    {
      onLoad = "document.pgFrm." + DistributionList.LIST_NAME + ".focus()";

    }

    String onUnLoad = "javascript:cleanup('Communication', 'cleanup', 'Any changes you have made since you last saved will be lost.')";


    LayoutHelper.drawBeginPageContentWrapper(onLoad, onUnLoad, out);

    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
    String caller = (String)session.getAttribute("caller");     // get caller (web site?)

    boolean isDining = user.equals(DINING_USER);
   
    if (isDining == false) {
        
        if (ProcessConstants.isProshopUser(user)) {
       
          String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
          int lottery = Integer.parseInt(templott);
          SystemUtils.getProshopSubMenu(request, out, lottery);
          
        } else if (ProcessConstants.isAdminUser(user)) {
          //fix later
        } else {
          SystemUtils.getMemberSubMenu(request, out, caller);
        }
    }


    LayoutHelper.drawBeginMainBodyContentWrapper(DistributionList.ADD_LIST_LABEL, pageActions, out);

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
    LayoutHelper.drawEndMainBodyContentWrapper(out);
    //LayoutHelper.drawFooter(out);
    LayoutHelper.drawEndPageContentWrapper(out);
    out.flush();
  }
    //******************************************************************************************************
    //   excelDistribution
    //******************************************************************************************************
    //

    private void excelDistribution(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
            throws IOException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList<String> listName = new ArrayList<String>();
        ArrayList<String> usernames = new ArrayList<String>();

        String listname = (req.getParameter("listname") == null) ? "" : req.getParameter("listname");
        listName.add(listname);

        String bgrndcolor = "#FFFFFF";      // default
        String fontcolor = "#000000";      // default

        //for each distribution list selected, need to extract the user names
        usernames = getUserNamesFromDistributionLists(listName, session, req, res, con, out);

        if (usernames != null && usernames.size() > 0) {

            try {              // if user requested Excel Spreadsheet Format
                res.setContentType("application/vnd.ms-excel");    // response in Excel Format
                res.setHeader("Content-Disposition", "attachment;filename=\"distribution.xls\"");
            } catch (Exception ignore) {
            }
            out.println("<table align=\"center\" border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"2\" cols=\"3\">");
            out.println("<tr bgcolor=\"" + bgrndcolor + "\">");
            out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Name</td>");
            out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">email1</td>");
            out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">email2</td>");
            out.println("</tr>");
            for (String dist_names : usernames) {

                try {

                    pstmt = con.prepareStatement("SELECT name_first, name_last, name_mi, email, email2 from member2b WHERE username = ? ORDER BY name_last");

                    pstmt.clearParameters();
                    pstmt.setString(1, dist_names);

                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        String name = "";
                        String email1 = rs.getString("email");
                        String email2 = rs.getString("email2");
                        String mi = rs.getString("name_mi");
                        if (mi == null || mi.equals("")) {
                            name = rs.getString("name_last") + ", " + rs.getString("name_first");
                        } else {
                            name = rs.getString("name_last") + ", " + rs.getString("name_first") + " " + mi;
                        }

                        if (email1 == null || email1.equals("") || email1.equals("")) {
                            email1 = "";
                        }
                        if (email2 == null || email2.equals("") || email2.equals(" ")) {
                            email2 = "";
                        }

                        out.println("<tr>");
                        out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + name + "</td>");
                        out.println("<td style=\"margin-left:auto; margin-right:auto;\"><center>" + email1 + "</center></td>");
                        out.println("<td style=\"margin-left:auto; margin-right:auto;\"><center>" + email2 + "</center></td>");
                        out.println("</tr>");
                    }

                } catch (Exception exc) {
                    //Utilities.logError("Proshop_reports.noShowRounds -  - Error looking up No Show rounds data for club - ERR: " + exc.toString());
                } finally {
                    Connect.close(rs, pstmt);
                }
            }

            out.println("</table>");

        }

    }
    
    
    
      //******************************************************************************************************
    //   getUserNamesFromDistributionLists
    //******************************************************************************************************
    //
    private ArrayList<String> getUserNamesFromDistributionLists(ArrayList selectedItems, HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
            throws IOException {

        ArrayList<String> names = new ArrayList<String>();

        //get the database table name based on the user
        String table_name = DistributionList.getTableName(session);    // 'dist4' (members) or 'distribution_lists' (proshop) 

    //
        //  Get this user's distribution lists, if any, and list them by name
        //
        String user = (String) session.getAttribute("user");     // get username ('proshop' or member's username)
        String club = (String) session.getAttribute("club");     // get club
        int list_size = DistributionList.getMaxListSize(session) + 1;

        if (user.startsWith("proshop")) {
            list_size = 1;     // unlimited 
        }
        ResultSet rs = null;
        PreparedStatement stmt = null;

        if (selectedItems != null) {
            for (int i = 0; i < selectedItems.size(); i++) {
                String list_name = (String) (selectedItems.get(i));

                try {
                    if (!user.startsWith("proshop")) {     // if member

                        String[] users = new String[list_size];                     // max of 30 users per dist list (start with 1)
                        String uname = "";

                        stmt = con.prepareStatement(
                                "SELECT * FROM " + table_name + " WHERE name = ? AND owner = ?");

                        stmt.clearParameters();               // clear the parms
                        stmt.setString(1, list_name);
                        stmt.setString(2, user);
                        rs = stmt.executeQuery();            // execute the prepared stmt

                        if (rs.next()) {

                            for (int i2 = 1; i2 < list_size; i2++) {               // check all 30 (start with 1)

                                uname = "user" + i2;
                                users[i2] = rs.getString(uname);

                                // add each user from the distribution list
                                if (!users[i2].equals("")) {

                                    names.add(users[i2]);
                                }
                            }
                        }

                    } else {        // proshop user

                        int list_id = 0;

                        //  Establish the distribution list
                        stmt = con.prepareStatement("SELECT id FROM distribution_lists WHERE name = ? AND owner = ?");

                        stmt.clearParameters();
                        stmt.setString(1, list_name);
                        stmt.setString(2, user);

                        rs = stmt.executeQuery();

                        if (rs.next()) {
                            list_id = rs.getInt(1);         // get the id of the list
                        }

                        if (list_id > 0) {

                            stmt = con.prepareStatement("SELECT username FROM distribution_lists_entries WHERE distribution_list_id = ?");

                            stmt.clearParameters();
                            stmt.setInt(1, list_id);

                            rs = stmt.executeQuery();

                            while (rs.next()) {

                                String user_name = rs.getString(1);

                                if (!user_name.equals("")) {

                                    names.add(user_name);
                                }
                            }
                        }
                    }

                } catch (Exception exc) {

          // Utilities.logError("Send_email.getUserNamesFromDistributionLists: Error gathering the users for club " + club + ", err=" + exc.toString());
                } finally {

                    try {
                        rs.close();
                    } catch (Exception ignore) {
                    }

                    try {
                        stmt.close();
                    } catch (Exception ignore) {
                    }

                }
            }
        }

    // ***********TEMP **********************
        // Utilities.logError("Send_email.getUserNamesFromDistributionLists: Number of members added: " + names.size());
        return names;
    }
}
