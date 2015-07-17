/***************************************************************************************
 *   Event_searchsel:  This servlet will display a page that allows the user to select one or more
 *                      events lists to send an email
 *
 *
 *   created: 1/20/2004   JAG
 *
 *   last updated:
 *
 *      5/08/12  Allow access from Dining Admin user.
 *      3/23/10  Updated the events2b query to only pull back events for the current activity id from the session block
 *     12/09/09  When looking for events only check those that are active.
 *      2/25/09  Modifiy event sql to include season long events even if past date (need better fix!)
 *      4/24/08  Update ArrayList to use String instead of raw types
 *      4/24/08  Update Connection object to use SystemUtils.getCon()
 *      1/26/04  RDP  Change the Cancel event to go to Send_email vs. Communication.
 *                    Search for events one week before to today's date.
 *      1/25/04  RDP  Change the event table query to only extract current (today or later) events.
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
import com.foretees.event.Event;
import com.foretees.common.Connect;
import com.foretees.common.Utilities;


/**
***************************************************************************************
*
* This servlet will display and process a page to allow a user to search and select a set of
* events for a specific purpose.
*
* To use this you will need to include an action like the following on your page
*
*   String searchEventsUrl = "javascript:openNewWindow('" + versionId + Event.SEARCH_WINDOW_URL + "', '" + Event.SEARCH_WINDOW_NAME + "', '" + Event.SEARCH_WINDOW_PARAMS + "')";
*   Action searchEventsAction = new Action(ActionHelper.SEARCH_EVENTS , Labels.SEARCH_EVENTS, "Search for events to add participants to this email", searchEventsUrl);
*
* and also have a hidden input field in your form named using ActionHelper.SELECTED_ITEMS_STRING
* and a hidden input field named using ActionHelper.SEARCH_TYPE
*
***************************************************************************************
**/

public class Event_searchsel extends HttpServlet {

  //initialize the attributes
  private static String versionId = ProcessConstants.CODEBASE;

  static String DINING_USER = ProcessConstants.DINING_USER;               // Dining username for Admin user from Dining System
 
   
  /**
  ***************************************************************************************
  *
  * This method will process the data from the request
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
    }

    //
    //  ***** get user id so we know if proshop or member
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)


    Connection con = Connect.getCon(req);           // get DB connection

    showPage(session, req, resp, con, out, true);

  }

  private void showPage(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out, boolean refresh)
    throws IOException
  {


    FormModel form = buildForm(session, req, res, con,out);
    out.println(SystemUtils.HeadTitleAdmin(Event.SEARCH_EVENTS_LABEL));
    FormRenderer.render(form, out);

  }


  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that comes before the show events form
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

    String onUnLoad = "javascript:cleanup('" + versionId + "servlet/Communication', 'cleanup', 'Any changes you have made since you last saved will be lost.')";


    LayoutHelper.drawBeginPageContentWrapper(onLoad, onUnLoad, out);
    ActionHelper.drawDistListNavBar(null, out);
    LayoutHelper.drawBeginMainBodyContentWrapper(DistributionList.ADD_LIST_LABEL, pageActions, out);

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the the portion of the page that contains the form
  *
  * @param req the request that contains information submitted by the user
  * @param resp the response object
  * @param session the session object
  * @param con the database connection
  * @param out the printwriter to write out the html
  *
  ***************************************************************************************
  **/

  private FormModel buildForm(HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

      
    Connection con_d = null;     // Dining con - if needed

    //
    //  ***** get user id so we know if proshop or dining admin
    //
    String user = (String)session.getAttribute("user");   

    boolean isDining = user.equals(DINING_USER);            // Dining Admin User ?
   
    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    FormModel form = new FormModel("pgFrm", FormModel.POST, null);
    form.setNumColumns(3);
    form.addHiddenInput("formId", Event.SEARCH_EVENT_FRM);
    form.addHiddenInput(ActionHelper.NEXT_ACTION, "");
    form.addHiddenInput(Member.REQ_USER_NAME, "");

    //Add the help steps for the form
    ArrayList <String>helpSteps = new ArrayList<String>(3);
    helpSteps.add(Event.CHECKBOX_HELP);
    helpSteps.add(Event.OK_HELP);

    form.setHelpSteps(helpSteps);

    //create the action model for this form and add it to the form model
    ActionModel formActions = new ActionModel();

    String okUrl = "javascript:returnCheckedItems('"  + Event.LIST_TABLE + "', '" + ActionHelper.SEARCH_EVENTS + "')";
    Action okAction = new Action("updateAndClose", "OK", "Return selected items.", okUrl);
    formActions.add(okAction);

    String cancelUrl = "javascript:window.close()";
    Action cancelAction = new Action("cancel", Labels.CANCEL, Labels.RETURN_NO_CHANGES_SAVED, cancelUrl);
    formActions.add(cancelAction);

    form.setActions(formActions);

    RowModel list_row = new RowModel();
    list_row.setId(Event.LIST_OF_NAMES);

    TableModel names = new TableModel(Event.SEARCH_EVENTS_LABEL);
    names.setId(Event.LIST_TABLE);
    names.setSelectable(true);
    names.addColumn(new Column("name", Event.NAME_LABEL));

    //
    //  Get current date/time and setup parms to use when building the calendar
    //
    Calendar cal = new GregorianCalendar();             // get today's date
    cal.add(Calendar.DATE,-8);                          // back up a week and a day (so pro can select recent events)
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DAY_OF_MONTH);

    month++;

    long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        
        if (isDining == false) {      // if NOT dining user

            pstmt = con.prepareStatement (
                    "SELECT name, season, IF(date < ?, 1, 0) AS past_event, IF(DATE < ? AND season = 0, DATEDIFF(now(), DATE), 0) AS days "
                    + "FROM events2b "
                    + "WHERE inactive = 0 AND activity_id = ? AND (DATEDIFF(now(), DATE) < 365 OR season = 1) "
                    + "ORDER BY season DESC, past_event, days, date, act_hr");

            pstmt.clearParameters();
            pstmt.setLong(1, date);
            pstmt.setLong(2, date);
            pstmt.setInt(3, sess_activity_id);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                RowModel row = new RowModel();
                String event_name = rs.getString("name");
                String event_disp = event_name;
                
                if (rs.getInt("season") == 1) {
                    event_disp += " (Season Long)";
                } else if (rs.getInt("past_event") == 1) {
                    event_disp += " (Past)";
                }
                
                row.setId(event_name);
                row.add(event_disp);
                names.addRow(row);

            }
            
        } else {       // Dining Admin User - get dining events
            
             int organization_id = Utilities.getOrganizationId(con);      // get the Dining org id for this club (identifes the dining database)

             if (organization_id > 0) {

                con_d = Connect.getDiningCon();

                if (con_d != null) {

                    pstmt = con_d.prepareStatement ("" +
                                "SELECT e.id, e.name " +
                                "FROM events e " +
                                "LEFT OUTER JOIN locations AS loc ON e.location_id = loc.id " +
                                "WHERE e.organization_id = ? AND " +
                                    "to_char(e.date, 'YYYYMMDD')::int >= ? " +
                                "ORDER BY e.date, e.start_time");

                    pstmt.clearParameters();
                    pstmt.setInt(1, organization_id);
                    pstmt.setLong(2, date);

                    rs = pstmt.executeQuery();

                    while ( rs.next() ) {

                        RowModel row = new RowModel();
                        int event_id = rs.getInt("id");
                        String event_name = rs.getString("name");
                        
                        event_name = event_id + ":" +event_name;      // combine them so we have the event id (dining events can have dup names)
                        
                        row.setId(event_name);
                        row.add(event_name);
                        names.addRow(row);
                    }
                }
             }
        }

    } catch (Exception exc) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        try { con_d.close(); }
        catch (Exception ignore) {}
    }

    list_row.add(names);

    form.addRow(list_row);

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
    out.flush();

  }

}
