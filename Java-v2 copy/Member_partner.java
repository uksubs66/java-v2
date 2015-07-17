/***************************************************************************************
 *   Member_partner - Displays the configuration page for members to edit their partner lists.  A separate list for each
 *                    activity is displayed and the member can add and remove members, as well as alter the ordering for each.
 *
 *
 *   created: 11/17/2009 BSK
 *
 *   last updated:
 *
 *    9/03/13   Add drag-n-drop to sort partner lists
 *    8/15/13   Add a checkbox for Golf partners to show the handicap index next to the partner name when showing
 *              the partner list on the tee time page (_slot).  Requested by Ballenisles for group play.
 *    6/21/13   Denver CC (denvercc) - Added custom to display Dependent mtype names in green in the partner lists.
 *    5/30/13   Made several tweaks to the page to make the layout a bit cleaner and more user-friendly, adjust some verbiage.
 *    1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *   11/27/12   Tweak iframe resize code
 *   09/06/12   Updated outputTopNav calls to also pass the HttpServletRequest object.
 *   02/23/12   Check for Dining in displayPartnerLists
 *   02/06/12   Fixed a few typos where "partner" was used instead of "partner".
 *   01/15/12   New Skin changes.
 *   08/25/11   Change the Help onclick to open in a new window.
 *   05/26/11   Add support for dining partner lists as well as partial support for new skin (if called from dining)
 *   12/22/09   Configuration overhauled to handle addition of new partners all from the main page.
 *   12/10/09   Assorted tweaks to streamline and simplify some components
 *   12/09/09   Changes to allow for submenus at top of all pages associated with partner list configuration
 *   11/17/09   Original functionality added
 *
 ***************************************************************************************
 */

//third party imports
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json
import com.google.gson.reflect.*; // for json

//foretees imports
import com.foretees.common.FeedBack;
import com.foretees.common.help.Help;
import com.foretees.common.Labels;
import com.foretees.common.alphaTable;
import com.foretees.common.ProcessConstants;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;

/**
***************************************************************************************
*
* This servlet will build the form for editing an existing member
*
***************************************************************************************
**/

public class Member_partner extends HttpServlet {

  String rev = ProcessConstants.REV;       // Software Revision Level (Version)


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

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    doPost2(req, resp, false);

  }

  public void doPost2(HttpServletRequest req, HttpServletResponse resp, boolean dining)
         throws ServletException, IOException {

      resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
      resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
      resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
      resp.setContentType("text/html");

      PrintWriter out = resp.getWriter();

      Connection con = null;                 // init DB objects

      Statement stmt = null;

      ResultSet rs = null;

      ArrayList<String> activity_names = new ArrayList<String>();
      ArrayList<Integer> activity_ids = new ArrayList<Integer>();

      HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

      if (session == null) {

          return;
      }

      String user_id = (String)session.getAttribute("user");
      String caller = (String)session.getAttribute("caller");
      String club = (String)session.getAttribute("club");      // get club name
      boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
      boolean json_mode = (req.getParameter("json") != null);

      con = SystemUtils.getCon(session);            // get DB connection

      if (con == null) {

          out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
          out.println("<BODY><CENTER>");
          out.println("<BR><BR><H3>Database Connection Error</H3>");
          out.println("<BR><BR>Unable to connect to the Database.");
          out.println("<BR>Please try again later.");
          out.println("<BR><BR>If problem persists, contact customer support.");
          out.println("<BR><BR><a href=\"Admin_announce\">Return</a>");
          out.println("</CENTER></BODY></HTML>");
          return;
      }

      int sess_activity_id = (Integer)session.getAttribute("activity_id");         // ID # of current activity

      String clubName = Utilities.getClubName(con, true);        // get the full name of this club
      String tmp_file_url = ((activity_ids.size() > 1) ? "partners_multi.pdf" : "partners.pdf" );
   
      //out.println("<!-- sess_activity_id=" + sess_activity_id + " -->");

      // Declare Variables
      String result_msg = "";

      boolean golfEnabled = getActivity.isGolfEnabled(con);
      boolean activitiesEnabled = getActivity.isConfigured(con);
      // boolean new_skin = (dining == true); // for now use new skin changes just for dining

      if (req.getParameter("addPartner") != null || req.getParameter("submitPartner") != null) {
          result_msg = addPartner(req, session, out, con);
      }

      if (req.getParameter("removePartner") != null) {
          result_msg = removePartner(req, con);
          if(json_mode){
              resp.setContentType("application/json");
              out.print(result_msg);
              return;
          }
      }

      if (req.getParameter("changeOrder") != null) {
          result_msg = changeOrder(req, session, out, con);
          return;
      }

      if (req.getParameter("submitOrder") != null) {
          result_msg = changeOrder(req, session, out, con);
          if(json_mode){
              resp.setContentType("application/json");
              out.print(result_msg);
              return;
          }
      }

      if (req.getParameter("resetOrder") != null) {
          result_msg = resetOrder(req, con);
      }
      
      if (req.getParameter("list") != null) {
          displayPartnerLists(req, session, result_msg, out, con);
          return;
      }

      if (req.getParameter("handicap") != null) {      // if call is to populate the handicap option iframe under Golf
          displayHandicapOption(req, session, user_id, out, con);
          return;
      }

      // if dining is enabled then add it to the list
      if (Utilities.getOrganizationId(con) != 0) {
          activity_names.add("Dining");
          activity_ids.add(ProcessConstants.DINING_ACTIVITY_ID);
      }

      // if golf is enabled then add it to the list
      if (golfEnabled) {
         
          activity_names.add("Golf");
          activity_ids.add(0);
      }
      
      

      try {
          if (activitiesEnabled) {
              stmt = con.createStatement();
              rs = stmt.executeQuery("SELECT activity_id, activity_name FROM activities WHERE parent_id = '0' ORDER BY activity_name");

              while (rs.next()) {
                  activity_names.add(rs.getString("activity_name"));
                  activity_ids.add(rs.getInt("activity_id"));
              }

              stmt.close();
          }
      } catch (Exception exc) {

      }
      
      if (new_skin) {

          if (!dining) {      // these components are built in Dining_home for the dining system

             //
             //  Build the top of the page
             //
             Common_skin.outputHeader(club, sess_activity_id, "Manage Partners", true, out, req);
             Common_skin.outputBody(club, sess_activity_id, out, req);
             Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
             Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);  
             Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
             Common_skin.outputPageStart(club, sess_activity_id, out, req);
             Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Manage Partners", req);
             Common_skin.outputLogo(club, sess_activity_id, out, req);

             out.println("<div class=\"preContentFix\"></div>"); // clear the float
          }
       
      } else {

         if (!new_skin) out.println(SystemUtils.HeadTitle("Partner List Management"));
      }

      out.println("<style>");

      out.println(".activityTable {");
      out.println("  padding: 0;");
      out.println("  margin: 0;");
      out.println("  background: #F5F5DC;");
      out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
      out.println("  border: 0px solid #dfeda3;");
      out.println("  align: center;");
      out.println("}");

      out.println(".btnOrder {");
      out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
      out.println("  background: #99CC66;");
      out.println("  width: 100px;");
      out.println("}");

      out.println(".btnAdd {");
      out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
      out.println("  background: #99CC66;");
      out.println("  width: 120px;");
      out.println("}");

      out.println(".btnNorm {");
      out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
      out.println("  background: #99CC66;");
      out.println("  width: 80px;");
      out.println("}");

      out.println("</style>");

      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      out.println("function resizeIFrame(divHeight, iframeName) {");
      out.println(" document.getElementById(iframeName).height = divHeight+'px';");
      out.println("}");
      out.println("function movename(nameinfo) {");
      out.println(" var f = document.forms['playerform'];");
      out.println(" var fr = document.getElementById('partnerlistiframe');");
      out.println(" array = nameinfo.split(':');"); // split string (partner_name, partner_id)
      out.println(" var partner_id = array[1];");
      out.println(" var actChecked = false;");
      out.println(" var params = '&partner_id=' + partner_id;");
      if (activity_ids.size() == 1) {
          out.println(" params += '&activity_" + activity_ids.get(0) + "';");
          out.println(" actChecked = true;");
      } else {
          for (int i=0; i<activity_ids.size(); i++) {
              out.println(" if (f.activity_" + activity_ids.get(i) + ".checked == true) {");
              out.println("   params += '&activity_" + activity_ids.get(i) + "';");
              out.println("   actChecked = true;");
              out.println(" }");
          }
      }
      out.println(" if (actChecked == true) {");
      out.println("   fr.src = ftSetJsid('Member_partner?list&addPartner' + params);");
      out.println(" }");
      out.println("}");
      out.println("// -->");
      out.println("</script>");

      out.println("<div class=\"main_instructions\"><strong>Instructions</strong>: ");
      if (activity_ids.size() == 1) {
          out.println("To <b>add a new partner</b>, click their name in the list to the right. ");
      } else {
          out.println("To <b>add a new partner</b>, first use the checkboxes to select "
                  + "the activities for the partner list which you would like to update. ");
          out.println("Next, click the member's name in the list to the right. ");
      }
      out.println(""
              + "To <b>alter the ordering of a partner list</b> grab an item by the <span class=\"ui-icon ui-icon-arrowthick-2-n-s\" style=\"display:inline-block\"></span> icon, and drag to the desired location. "
              + "To <b>return the list to alphabetical order</b> click on the \"Alphabetize List\" button. "
              + "To <b>remove a member from a partner list</b>, click the <span class=\"deleteButtonSmall\" title=\"Remove partner\"><b>Remove</b></span> icon next to "
              + "their name in the partner list you'd like them removed from.");
      
      if (club.equals("denvercc")) {
          out.println("Names <b>displayed in green</b> indicate that they are Dependents.");
      }
      out.println("</div>");

      out.println("<table align=\"center\" border=\"0\" bgcolor=\"#F5F5DC\" width=\"100%\">");      // layout table
      out.println("<tr><td align=\"center\" colspan=\"4\"><br>");

      if (dining) {

          //out.println("<button class=\"btnNorm\" onclick=\"location.href='Dining_home'\">Home</button>");
          out.println("<div id=\"tt2_left\" align=\"center\">"
                  + "<input type=\"button\" value=\"Home\" id=\"back\" onclick=\"window.location.href=ftSetJsid('Dining_home')\">"
                  + "<input type=\"button\" value=\"Help\" id=\"back\" onclick=\"window.open('http://www.foretees.com/messages/" + tmp_file_url + "')\">"
                  + "</div>");

      } else {

          out.println("<button class=\"btnNorm\" onclick=\"location.href=ftSetJsid('Member_announce')\">Home</button>");

          //  If the guest tracking system is in use, display a link to the guest list page
          if (Utilities.isGuestTrackingConfigured(sess_activity_id, con)) {
              out.println("<button class=\"btnNorm\" onclick=\"location.href=ftSetJsid('Common_guestdb')\">Guest List</button>");
          }

          out.println("<button class=\"btnNorm\" onclick=\"location.href='http://www.foretees.com/messages/" + tmp_file_url + "'\">Help</button>");
      }

      out.println("</td></tr>");

      out.println("<tr>");

      // Print iframe to display partner lists
      out.println("<td align=\"center\" valign=\"top\" colspan=\"2\" width=\"60%\">");
      out.println("<iframe id=\"partnerlistiframe\" src=\""+Common_skin.setUrlJsid("Member_partner?list", session)+"\" width=\"90%\" scrolling=no frameborder=no></iframe>");
      out.println("</td>");  // close left column of layout table


      // Set up form for use with adding a new partner by clicking the name-list on the main config page
      out.println("<form action=\"Member_partner\" method=\"POST\" name=\"playerform\" onsubmit=\"false\">");
      out.println("<input type=\"hidden\" name=\"addPartner\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"nameList\" value=\"\">");      // Signifies this submission came from clicking the name list
      out.println("<input type=\"hidden\" name=\"list\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"user_id\" value=\"" + user_id + "\">");
      out.println("<input type=\"hidden\" name=\"partner_id\" value=\"\">");

      // If only one activity id is present, use a hidden input
      if (activity_ids.size() == 1) {
          out.println("<input type=\"hidden\" name=\"activity_" + activity_ids.get(0) + "\" value=\"yes\">");
      } else {      // If more than one activity, print checkboxes for all activities
          out.println("<td align=\"center\" valign=\"top\"><br>");
          out.println("<table class=\"activityTable\" align=\"center\" border=\"0\" bgcolor=\"#F5F5DC\" width=\"100%\">");      // activity select table
          out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
          out.println("<font size=\"4\" color=\"white\"><b>Activities</b></font>");
          out.println("</td></tr>");

          for (int i=0; i<activity_ids.size(); i++) {
              out.println("<tr><td align=\"left\">");

              // Print checkbox and activity_name
              out.println("<input type=\"checkbox\" id=\"chk_p_activity_" + activity_ids.get(i) + "\" name=\"activity_" + activity_ids.get(i) + "\"" + ((activity_ids.get(i) == sess_activity_id) ? " checked" : "") + "><label for=\"chk_p_activity_" + activity_ids.get(i) + "\">" + activity_names.get(i) + "</label>");

              out.println("</td></tr>");
          }
          out.println("</table>");      // close activity select table
          out.println("</td>");
      }

      // Print namelist selection box
      out.println("<td align=\"center\" valign=\"top\"><br>");
      alphaTable.nameList_simple(club, 16, false, out, con);
      out.println("</td>");

      out.println("</form>");   // close playerform form
      out.println("</tr>");

      if (!new_skin) out.println("<tr><td align=\"center\" colspan=\"4\"><hr width=\"90%\" size=\"1\" color=\"#dfeda3\"></td></tr>");
      
      out.println("<tr><td align=\"center\" colspan=\"4\">");
      
      if (dining) {
        
          //out.println("<button class=\"btnNorm\" onclick=\"location.href='Dining_home'\">Home</button>");
          out.println("<div id=\"tt2_left\" align=\"center\">" +
                  "<input type=\"button\" value=\"Home\" id=\"back\" onclick=\"window.location.href=ftSetJsid('Dining_home')\">" +
                  "<input type=\"button\" value=\"Help\" id=\"back\" onclick=\"window.open('http://www.foretees.com/messages/" + tmp_file_url + "')\">" +
                  "</div>");
      
      } else {

          out.println("<br>");
          out.println("<button class=\"btnNorm\" onclick=\"location.href=ftSetJsid('Member_announce')\">Home</button>");

          //  If the guest tracking system is in use, display a link to the guest list page
          if (Utilities.isGuestTrackingConfigured(sess_activity_id, con)) out.println("<button class=\"btnNorm\" onclick=\"location.href=ftSetJsid('Common_guestdb')\">Guest List</button>");

          out.println("<button class=\"btnNorm\" onclick=\"location.href='http://www.foretees.com/messages/" + tmp_file_url + "'\">Help</button>");
          out.println("<br><br>");
      }
      
      out.println("</td></tr>");
      out.println("</table>");  // close layout table

      if (new_skin) {
          
          Common_skin.outputPageEnd(club, sess_activity_id, out, req);
          
      } else {
          
          out.println("</body></html>");
      }

  }   // end of doPost2


  /**
   * addPartner - Display page to allow members to add new partners to partner lists for multiple activities at once.
   *
   * @param req Request object
   * @param out Output writer
   * @param con Connection to club database
   */
  private String addPartner(HttpServletRequest req, HttpSession session, PrintWriter out, Connection con) {

      Statement stmt = null;
      PreparedStatement pstmt = null;

      ResultSet rs = null;

      String user_id = (String)session.getAttribute("user");

      //int sess_activity_id = (Integer)session.getAttribute("activity_id");

      ArrayList<String> activity_names = new ArrayList<String>();
      ArrayList<Integer> activity_ids = new ArrayList<Integer>();

      int activity_id = -1;
      int count = 0;
      int priority = 0;

      String partner_name = "";
      String partner_id = "";
      String result_msg = "";

      boolean golfEnabled = getActivity.isGolfEnabled(con);
      boolean activitiesEnabled = getActivity.isConfigured(con);

      // add golf if enabled
      if (golfEnabled) {
          activity_names.add("Golf");
          activity_ids.add(0);
      }

      // add dining if configured
      if (Utilities.getOrganizationId(con) != 0) {
          activity_names.add("Dining");
          activity_ids.add(ProcessConstants.DINING_ACTIVITY_ID);
      }

      try {
          if (activitiesEnabled) {
              stmt = con.createStatement();
              rs = stmt.executeQuery("SELECT activity_id, activity_name FROM activities WHERE parent_id = '0' ORDER BY activity_id");

              while (rs.next()) {
                  activity_names.add(rs.getString("activity_name"));
                  activity_ids.add(rs.getInt("activity_id"));
              }

              stmt.close();
          }
      } catch (Exception exc) {

      }
      // Gather the passed parameters
      partner_id = req.getParameter("partner_id");

      // Loop through activitiy ids and see if this partner needs to be added to any of them
      if (!partner_id.equals("") && !partner_id.equals("undefined")) {
          for (int i=0; i<activity_ids.size(); i++) {

              if (req.getParameter("activity_" + activity_ids.get(i)) != null) {
                  priority = 1;
                  partner_name = "Member";
                  activity_id = activity_ids.get(i);

                  try {

                      // First, get the current highest priority for this partner list
                      pstmt = con.prepareStatement("SELECT priority FROM partner WHERE user_id = ? AND activity_id = ? ORDER BY priority DESC LIMIT 1");
                      pstmt.clearParameters();
                      pstmt.setString(1, user_id);
                      pstmt.setInt(2, activity_id);

                      rs = pstmt.executeQuery();

                      if (rs.next()) {
                          priority = rs.getInt("priority") + 1;
                      }

                      pstmt.close();

                      // Next, gather the user's name for result_msg
                      pstmt = con.prepareStatement(
                              "SELECT CONCAT(name_first, IF(name_mi <> '', CONCAT(' ', name_mi), ''), ' ', name_last) as partner_name " +
                              "FROM member2b WHERE username = ?");
                      pstmt.clearParameters();
                      pstmt.setString(1, partner_id);

                      rs = pstmt.executeQuery();

                      if (rs.next()) {
                          partner_name = rs.getString("partner_name");
                      }

                      pstmt.close();

                      // Finally, insert the partner into this activity's partner list
                      pstmt = con.prepareStatement("INSERT INTO partner (user_id, activity_id, partner_id, priority) VALUES(?,?,?,?)");
                      pstmt.clearParameters();
                      pstmt.setString(1, user_id);
                      pstmt.setInt(2, activity_id);
                      pstmt.setString(3, partner_id);
                      pstmt.setInt(4, priority);

                      count = pstmt.executeUpdate();

                      pstmt.close();

                  } catch (Exception exc) {
                      result_msg = partner_name + " is already present in the partner list.  No changes made.";
                  }

                  if (count > 0) {
                      result_msg = partner_name + " added successfully!";
                  }
              }
          }
      }
        
      return result_msg;
  }


  private void displayPartnerLists(HttpServletRequest req, HttpSession session, String result_msg, PrintWriter out, Connection con) {

      PreparedStatement pstmt = null;
      Statement stmt = null;
      ResultSet rs = null;

      String user_id = (String)session.getAttribute("user");
      String club = (String)session.getAttribute("club");

      int sess_activity_id = (Integer)session.getAttribute("activity_id");; 
      
      ArrayList<String> activity_names = new ArrayList<String>();
      ArrayList<Integer> activity_ids = new ArrayList<Integer>();

      String activity_name = "";
      String partner_id = "";
      String partner_name = "";
      String partner_mtype = "";
      String success_msg = "";
      String style = "";
      Gson gson_obj = new Gson();

      int activity_id = 0;
      int count = 0;
      
      boolean handicapsTracked = false;
      boolean golfEnabled = getActivity.isGolfEnabled(con);
      boolean activitiesEnabled = getActivity.isConfigured(con);
      boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
     // boolean new_skin = (sess_activity_id == ProcessConstants.DINING_ACTIVITY_ID); // enable the new skin if here for dining

      // See if there's a success message to grab
      if (req.getParameter("success_msg") != null && !req.getParameter("success_msg").equals("")) {
          success_msg = req.getParameter("success_msg");
      }

      try {
          // Gather activity names and ids
          if (golfEnabled) {
             
              activity_names.add("Golf");
              activity_ids.add(0);
              
              handicapsTracked = Utilities.isHandicapSysConfigured(0, con);  // is handicap system configured?
          }

          if (activitiesEnabled) {
              stmt = con.createStatement();
              rs = stmt.executeQuery("SELECT activity_id, activity_name FROM activities WHERE parent_id = '0' ORDER BY activity_name");

              while (rs.next()) {
                  activity_names.add(rs.getString("activity_name"));
                  activity_ids.add(rs.getInt("activity_id"));
              }

              stmt.close();
          }
      } catch (Exception exc) {
          out.println(exc.getMessage());
      }

          
      //
      // for now just force only dining if it's enabled
      //if (sess_activity_id == ProcessConstants.DINING_ACTIVITY_ID || club.equals("demov4")) {
      
      // if dining is enabled then add it to the list
      if (Utilities.getOrganizationId(con) != 0) {

          activity_names.add("Dining");
          activity_ids.add(ProcessConstants.DINING_ACTIVITY_ID);
      }
      
      Common_skin.outputHeader(club, sess_activity_id, "Manage Partners", true, out, req);
      
      out.println("<body onload=\"parent.window.resizeIFrame(document.getElementById('partnerlistiframediv').offsetHeight, 'partnerlistiframe');\">");
      out.println("<div id=\"partnerlistiframediv\">");

      out.println("<style>");
      
      out.println("body {");
      out.println("  background: transparent;");
      out.println("}");
      
      out.println("html {");
      out.println("  background: transparent;");
      out.println("}");

      out.println(".activityTable {");
      out.println("  padding: 0;");
      out.println("  margin: 0;");
      //out.println("  background: #F5F5DC;");
      //out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
      out.println("  border: 0px solid #dfeda3;");
      out.println("  align: center;");
      out.println("}");

      out.println(".btnOrder {");
      out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
      out.println("  background: #99CC66;");
      out.println("  width: 100px;");
      out.println("}");

      out.println(".btnNorm {");
      out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
      out.println("  background: #99CC66;");
      out.println("  width: 80px;");
      out.println("}");

      out.println("</style>");

      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      /*
      out.println("function removePartner(user_id, activity_id, partner_id) {");
      out.println(" f = document.forms['removePartnerForm'];");
      out.println(" f.user_id.value = user_id;");
      out.println(" f.activity_id.value = activity_id;");
      out.println(" f.partner_id.value = partner_id;");
      out.println(" f.submit();");
      out.println("}");
       * 
       */
      out.println("function alphaList(user_id, activity_id) {");
      out.println(" if (confirm('This will reset this partner list to alphabetical order.  Are you sure you want to do this?')) {");
      out.println("   window.location.href = ftSetJsid('Member_partner?list&resetOrder&user_id=' + user_id + '&activity_id=' + activity_id);");
      out.println(" }");
      out.println("}");
      out.println("// -->");
      out.println("</script>");
      /*
      // Set up form for use with the remove partner buttons
      out.println("<form action=\"Member_partner\" method=\"POST\" name=\"removePartnerForm\">");
      out.println("<input type=\"hidden\" name=\"user_id\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"activity_id\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"partner_id\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"removePartner\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"list\" value=\"\">");
      out.println("</form>");
    */
      out.println("<table align=\"center\" border=\"0\" bgcolor=\"#F5F5DC\" width=\"100%\">");      // left-half layout table

      // If a result message is present to be displayed, print it before continuing
      if (!result_msg.equals("")) {
          out.println("<tr><td align=\"center\" colspan=\"3\">" + result_msg + "<br><br></td></tr>");
      }

      out.println("<tr>");

      for (int i=0; i<activity_ids.size(); i++) {

          count = 0;
          activity_id = activity_ids.get(i);
          activity_name = activity_names.get(i);

          // Get a count of the current number of partners for this activity
          try {
              pstmt = con.prepareStatement("SELECT count(*) FROM partner WHERE user_id = ? AND activity_id = ?");
              pstmt.clearParameters();
              pstmt.setString(1, user_id);
              pstmt.setInt(2, activity_id);

              rs = pstmt.executeQuery();

              if (rs.next()) {
                  count = rs.getInt(1);
              }

              pstmt.close();

          } catch (Exception exc) {
              count = 0;
          }

          // Start a new row every 3rd activity
          if(i != 0 && i%2 == 0) {

              // End current row
              out.println("</tr>");

              // Add divider
              out.println("<tr><td align=\"center\" colspan=\"3\">");
              out.println("<hr width=\"90%\" size=\"1\" color=\"#dfeda3\">");
              out.println("</td></tr>");

              // Start new row
              out.println("<tr>");
          }

          // Start new column of layout table for this activity
          out.println("<td align=\"center\" valign=\"top\">");

          // Print the start of the column for this activity
          out.println("<table class=\"activityTable\">");   // activity table

          // Print activity name
          out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
          out.println("<font size=\"4\" color=\"white\"><b>" + activity_name + "</b></font>");
          out.println("</td></tr>");

          // Print ordering buttons
          out.println("<form action=\"Member_partner\" method=\"POST\"" + ((new_skin) ? " target=\"_parent\"" : " target=\"bot\"") + " name=\"orderPartnerForm\">");
          out.println("<tr><td align=\"center\" nowrap>");
          out.println(" <input type=\"hidden\" name=\"user_id\" value=\"" + user_id + "\">");
          out.println(" <input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");
          //out.println(" <input class=\"btnOrder\" type=\"submit\" name=\"changeOrder\" value=\"Change Order\"" + (count == 0 ? "disabled ": "") + ">");
          out.println(" <input class=\"btnOrder\" type=\"button\" name=\"resetOrder\" value=\"Alphabetize List\" " + (count == 0 ? "disabled ": "") + " onclick=\"alphaList('" + user_id + "'," + activity_id + ")\">");
          out.println("</td></tr>");
          out.println("</form>");

          // Print divider line before start of list of names
          out.println("<tr><td align=\"center\">");
          out.println("<hr size=\"1\" width=\"90%\" color=\"#dfeda3\">");
          out.println("</td></tr>");

          // Don't need to look up partner info for this activity if none were found earlier
          if (count > 0) {

              try {
                  // Gather all partners for this member from database for the current activity
                  pstmt = con.prepareStatement(
                          "SELECT p.*, CONCAT(m.name_last, ', ', m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi), '')) as partner_name, m_type as partner_mtype FROM partner p " +
                          "LEFT OUTER JOIN member2b m ON p.partner_id = m.username " +
                          "WHERE p.user_id = ? and p.activity_id = ? " +
                          "ORDER BY priority, m.name_last, m.name_first");
                  pstmt.clearParameters();
                  pstmt.setString(1, user_id);
                  pstmt.setInt(2, activity_id);
                  rs = pstmt.executeQuery();

                  out.print("<tr class=\"partnerList\"><td><ul class=\"uiSortable partnerList\" data-ftActivityId=\"" + activity_id + "\" data-ftUserId=\"" + StringEscapeUtils.escapeHtml(user_id) + "\">");
                  // Loop through the results and print out the partner lists
                  while (rs.next()) {
                      
                      //Map<String, Object> partnerMap = new LinkedHashMap<String, Object>();

                      count++;
                      partner_id = rs.getString("partner_id");
                      partner_name = rs.getString("partner_name").trim();
                      partner_mtype = rs.getString("partner_mtype");
                      style = "";
                      
                      //partnerMap.put("partner_name", partner_name);
                      //partnerMap.put("partner_mtype", partner_mtype);
                      //partnerMap.put("partner_id", partner_id);
                      //partnerMap.put("activity_id", activity_id);
                      //partnerMap.put("user_id", user_id);
                      

                      // Print partner removal button (trash can icon)
                      //out.println("<tr class=\"plRow\"><td align=\"right\" width=\"35%\">");
                      //out.println("<tr class=\"plRow\"><td>");
                      //out.println("<td>");
                      out.print("<li class=\"ui-state-default\" data-ftId=\""+StringEscapeUtils.escapeHtml(rs.getString("id"))+"\" data-ftPartnerMtype=\"" + StringEscapeUtils.escapeHtml(partner_mtype) + "\" data-ftPartnerName=\"" + StringEscapeUtils.escapeHtml(partner_name) + "\" data-ftPartnerId=\"" + StringEscapeUtils.escapeHtml(partner_id) + "\"><div class=\"sortItem\">");
                      //out.println("<a alt=\"Remove partner\" title=\"Remove partner\" onclick=\"removePartner('" + user_id + "', '" + activity_id + "', '" + partner_id + "')\"><img width=\"13\" height=\"13\" border=\"0\" src=\"/v5/images/dts_trash.gif\"></a>");
                      //out.print("<a class=\"deleteButtonSmall\" alt=\"Remove partner\" title=\"Remove partner\" onclick=\"removePartner('" + user_id + "', '" + activity_id + "', '" + partner_id + "')\"><b>Remove</b></a>");
                      out.print("<a class=\"deleteButtonSmall\" href=\"#\" alt=\"Remove partner\" title=\"Remove partner\"><b>Remove</b></a>");
                      //out.println("</td>");
                      
                      if (club.equals("denvercc") && partner_mtype.equalsIgnoreCase("Dependent")) {
                          style = "color:green;";
                      }

                      // Print partner name
                      //out.println("<td align=\"left\">");
                      //out.println("<td>");
                      if (!style.equals("")) {
                          out.print("<span style=\"" + style + "\">" + partner_name + "</span>");
                      } else {
                          //out.println(partner_name);
                          out.print("<span>" + partner_name + "</span>");
                      }
                      //out.println("</td></tr>");
                      out.print("<div class=\"sortHandle\"><div class=\"ui-icon ui-icon-arrowthick-2-n-s\"></div></div>");
                      out.print("</div></li>");
                  }
                  out.println("</ul></td></tr>");
                  pstmt.close();

              } catch (Exception exc) {

                  out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
                  out.println("<BODY><CENTER>");
                  out.println("<BR><BR><H3>Database Connection Error</H3>");
                  out.println("<BR><BR>Unable to connect to the Database.");
                  out.println("<BR>Please try again later.");
                  out.println("<BR><BR>If problem persists, contact customer support.");
                  out.println("<BR><BR><a href=\"Member_announce\">Return</a>");
                  out.println("</CENTER></BODY></HTML>");
                  return;
              }

          } else {
              out.println("<tr><td align=\"center\">");
              out.println("No partners found");
              out.println("</td></tr>");
          }

          // Print divider line after list of names
          out.println("<tr><td align=\"center\">");
          out.println("<hr size=\"1\" width=\"90%\" color=\"#dfeda3\">");
          
          //
          //  If Golf included and Handicaps are tracked, allow member to specify option to show handicaps next to partner names
          //
          if (activity_id == 0 && handicapsTracked) {     // if Golf and handicaps are tracked - add iframe for checkbox to specify if handicap index to appear with partners

             out.println("<iframe name=\"partnerhndcpiframe\" id=\"partnerhndcpiframe\" src=\""+Common_skin.setUrlJsid("Member_partner?handicap", session) +"\" width=\"100%\" height=\"70\" scrolling=no frameborder=no></iframe>");
          }
       
          out.println("</td></tr>");          

          out.println("</table>");  // close activity table

          out.println("</td>");   // close iframe layout table column
      }

      out.println("</tr></table>");        // close left-half layout table
      out.println("</div>");
      out.println("</body>");
  }


  /**
   * removePartner - Removes the passed partner from the current member's partner list for the specified activity id
   *
   * @param req Request object
   * @param con Connection to club database
   *
   * @return result_msg - Message containing the result of the method
   */
  private String removePartner(HttpServletRequest req, Connection con) {

      PreparedStatement pstmt = null;
      ResultSet rs = null;

      String user_id = req.getParameter("user_id");
      String partner_id = req.getParameter("partner_id");
      String result_msg = "";
      String partner_name = "";
      
      boolean json_mode = (req.getParameter("json") != null);

      int activity_id = Integer.parseInt(req.getParameter("activity_id"));
      int count = 0;
      int ucount = 0;

      try {
          // Next, gather the user's name for result_msg
          pstmt = con.prepareStatement(
                  "SELECT CONCAT(name_first, IF(name_mi <> '', CONCAT(' ', name_mi), ''), ' ', name_last) as partner_name " +
                  "FROM member2b WHERE username = ?");
          pstmt.clearParameters();
          pstmt.setString(1, partner_id);

          rs = pstmt.executeQuery();

          if (rs.next()) {
              partner_name = rs.getString("partner_name");
              ucount ++;
          }

          pstmt.close();

          pstmt = con.prepareStatement("DELETE FROM partner WHERE user_id = ? AND activity_id = ? AND partner_id = ?");
          pstmt.clearParameters();
          pstmt.setString(1, user_id);
          pstmt.setInt(2, activity_id);
          pstmt.setString(3, partner_id);

          count = pstmt.executeUpdate();

          pstmt.close();

          if(!json_mode){
              if (count > 0) {
                  result_msg = partner_name + " removed from list.";
              } else {
                  result_msg = "Problem encountered, " + partner_name + " not removed.";
              }
          }else{
              if(ucount > 0){
                  result_msg = "{\"result\":true}";
              } else {
                result_msg = "{\"result\":false}";
              }
          }
      } catch (Exception exc) {
          if(!json_mode){
            result_msg = "Error: " + exc.getMessage();
          } else {
              result_msg = "{\"result\":false}";
          }
      }

      return result_msg;
  }

  /**
   * changeOrder - Display page and allow user to change the order of the selected activity's partner list
   *
   * @param req Request object
   * @param out Output writer
   * @param con Connection to club database
   */
  private String changeOrder(HttpServletRequest req, HttpSession session, PrintWriter out, Connection con) {

      PreparedStatement pstmt = null;
      PreparedStatement pstmt2 = null;
      ResultSet rs = null;

      String caller = (String)session.getAttribute("caller");

      // Declare Variables
      int activity_id = -1;
      int priority = 1;
      int id = 0;
      int count = 0;
      int count_total = 0;
      int count_perCol = 0;
      int count_cols = 0;
      int maxCols = 0;
      
      boolean json_mode = (req.getParameter("json") != null);

      String user_id = "";
      String activity_name = "";
      String partner_name = "";
      String result_msg = "";
      
      String club = (String)session.getAttribute("club");
      //int sess_activity_id = (Integer)session.getAttribute("activity_id"); 

      // Grab parameters from request object
      user_id = req.getParameter("user_id");
      activity_id = Integer.parseInt(req.getParameter("activity_id"));

      // Get the name of this activity
      if (activity_id == 0) {
          activity_name = "Golf";
      } else {
          activity_name = getActivity.getActivityName(activity_id, con);
      }

      // If we're coming from a form submission, process submission before continuing
      if (req.getParameter("submitOrder") != null) {

          int id_curr = 0;
          int priority_curr = 0;
          int priority_old = 0;
          
          

          // Grab all ids from the database for this activity's partner list and loop through them
          try {
              pstmt = con.prepareStatement("SELECT id, priority FROM partner WHERE user_id = ? AND activity_id = ? ORDER BY id");
              pstmt.clearParameters();
              pstmt.setString(1, user_id);
              pstmt.setInt(2, activity_id);

              rs = pstmt.executeQuery();
              
              int rscount = 0;

              while (rs.next()) {

                  id_curr = rs.getInt("id");
                  priority_old = rs.getInt("priority");

                  // For each id pulled back, try to get a priority from the request object for it.
                  if (req.getParameter("priority_" + id_curr) != null) {
                      try {
                          priority_curr =  Integer.parseInt(req.getParameter("priority_" + id_curr));

                          // If priority successfully grabbed, make sure it's greater than 0 and has changed from the old value, and then apply it to the database
                          if (priority_curr > 0 && priority_curr != priority_old) {
                              pstmt2 = con.prepareStatement("UPDATE partner SET priority = ? WHERE id = ?");
                              pstmt2.clearParameters();
                              pstmt2.setInt(1, priority_curr);
                              pstmt2.setInt(2, id_curr);

                              count = pstmt2.executeUpdate();
                              if(count > 0){
                                  rscount ++;
                              }

                              pstmt2.close();
                          }
                      } catch (Exception exc) {
                          // Print out comment error message and continue to next id without making changes
                          //out.println("<!-- Error - invalid input: " + req.getParameter("priority_" + id_curr) + " - skipped -->");
                          continue;
                      }
                  }
                  
              }

              pstmt.close();
              if(!json_mode){
                result_msg = "Changes applied!  Make additional changes or click 'Return' to go back to the main partner list display.<br><br>";
              }else{
                  result_msg = "{\"result\":true}";
              }

          } catch (Exception exc) {
              if(!json_mode){
                result_msg = "Unable to update order.  Please try again.";
              }else{
                  result_msg = "{\"result\":false}";
              }
          }

          return result_msg;
      }

      //
      // Display main form
      //
      Common_skin.outputHeader(club, activity_id, "Manage Partners", true, out, req);
      //out.println(SystemUtils.HeadTitle("Partner List Management"));

      out.println("<style>");
      out.println(".activityTable {");
      out.println("  padding: 0;");
      out.println("  margin: 0;");
      out.println("  background: #F5F5DC;");
      out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
      out.println("  border: 0px solid #dfeda3;");
      out.println("  align: center;");
      out.println("}");
      out.println(".btnNorm {");
      out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
      out.println("  background: #99CC66;");
      out.println("  width: 80px;");
      out.println("}");
      out.println("</style>");

      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      out.println("function cursor() { document.forms['f'].name.focus(); }");
      out.println("function movename(name) {");
      out.println(" document.forms['f'].name.value = name;");
      out.println("}");
      out.println("function movename(nameinfo) {");
      out.println(" array = nameinfo.split(':');"); // split string (partner_name, partner_id)
      out.println(" var partner_name = array[0];");
      out.println(" var partner_id = array[1];");
      out.println(" f = document.forms['playerform'];");
      out.println(" f.partner_name.value = partner_name;");
      out.println(" f.partner_id.value = partner_id;");
      out.println("}");
      out.println("// -->");
      out.println("</script>");

      out.println("<body bgcolor=\"#F5F5DC\">");

      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page

      out.println("<table align=\"center\" border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"650\">"); // instructions table
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<b>Change Partner Order</b><br>");
      out.println("<br>To alter the ordering, <b>type positive, whole numbers</b> into the spaces to the left of each name.");
      out.println("<br>The lower the number, the higher they will appear in the partner list for this activity (1 being the highest).");
      out.println("<br><br><b>NOTE:</b> If multiple partners are given the same priority value, <br>" +
              "they will appear together in the list, sorted alphabetically amongst themselves.");
      out.println("<br>When a new partner is added, they will be placed at the end of the list.");
      out.println("<br><br>Click on <b>'Apply'</b> to apply any changes.");
      out.println("<br>Click on <b>'Cancel'</b> to exit without making changes.");

      out.println("</font>");
      out.println("</td></tr>");
      out.println("</table><br><br>");      // close instructions table

      // Start layout table
      out.println("<table align=\"center\" border=\"0\" bgcolor=\"#F5F5DC\" width=\"80%\">");
      
      out.println("<tr><td align=\"center\">");

      // Start activity table
      out.println("<table class=\"activityTable\" align=\"center\">");
      
      // Start form for order change submission
      out.println("<form action=\"Member_partner\" method=\"POST\" name=\"orderForm\">");
      out.println("<input type=\"hidden\" name=\"user_id\" value=\"" + user_id + "\">");
      out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");

      // Print activity name
      out.println("<tr><td align=\"center\" colspan=\"2\" bgcolor=\"#336633\">");
      out.println("<font size=\"4\" color=\"white\"><b>" + activity_name + "</b></font>");
      out.println("</td></tr>");

      // Print submit and return buttons
      out.println("<tr><td align=\"center\" colspan=\"2\">");
      out.println("<input class=\"btnNorm\" type=\"submit\" name=\"submitOrder\" value=\"Apply\">&nbsp;");
      out.println("<input class=\"btnNorm\" type=\"button\" value=\"Cancel\" onclick=\"location.href=ftSetJsid('Member_partner')\">");
      out.println("</td></tr>");

      // Print divider line before partner list
      out.println("<tr><td align=\"center\" colspan=\"2\">");
      out.println("<hr size=\"1\" width=\"90%\" color=\"#dfeda3\">");
      out.println("</td></tr>");

      out.println("<tr><td align=\"center\">");
      out.println("<table class=\"activityTable\" align=\"center\" style=\"padding:5;\">");      // table to hold columns for listing partners
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<table class=\"activityTable\" align=\"center\">");  // table to hold the partners for this column
      count_cols = 1;

      // Get this partner list and associated priority values from the club database
      try {

          // First get a count of the number of partners for this user and activity
          pstmt = con.prepareStatement("SELECT count(*) FROM partner WHERE user_id = ? AND activity_id = ?");
          pstmt.clearParameters();
          pstmt.setString(1, user_id);
          pstmt.setInt(2, activity_id);

          rs = pstmt.executeQuery();

          if (rs.next()) {
              count_total = rs.getInt(1);
          }

          pstmt.close();

          // Determine how many columns we need
          maxCols = (count_total / 15) + 1;
          if (maxCols > 5) maxCols = 5;     // limit at 5 columns

          // Determine how many members per column
          count_perCol = (count_total / maxCols) + 1;

          pstmt = con.prepareStatement(
                  "SELECT p.id, p.priority, CONCAT(m.name_last, ', ', m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi), '')) as partner_name FROM partner p " +
                  "LEFT OUTER JOIN member2b m ON p.partner_id = m.username " +
                  "WHERE p.user_id = ? and p.activity_id = ? " +
                  "ORDER BY priority, m.name_last, m.name_first");
          pstmt.clearParameters();
          pstmt.setString(1, user_id);
          pstmt.setInt(2, activity_id);

          rs = pstmt.executeQuery();

          // Loop through results and print out partners
          while (rs.next()) {

              // See if we need to start a new column
              if (count_cols < maxCols && count >= count_perCol) {

                  // Close table for this column, start a new column, and start a new table for the new column
                  out.println("</table>");
                  out.println("</td><td align=\"center\" valign=\"top\">");
                  out.println("<table class=\"activityTable\" align=\"center\">");

                  count_cols++;
                  count = 0;
              }

              // Populate variables
              id = rs.getInt("p.id");
              priority = rs.getInt("p.priority");
              partner_name = rs.getString("partner_name");

              if (priority == 0) priority = 1;

              // Print text box for priority
              out.println("<tr><td align=\"right\">");
              out.println("<input type=\"text\" size=\"2\" name=\"priority_" + id + "\" value=\"" + priority + "\">");
              out.println("</td><td align=\"left\">");

              // Print partner name
              out.println(partner_name);
              out.println("</td></tr>");

              count++;
          }

          pstmt.close();


      } catch (Exception exc) {

      }

      out.println("</table>");  // end of table for individual column
      out.println("</td></tr>");
      out.println("</table>");  // end of table for partner list columns
      out.println("</td></tr>");

      // Print divider line after partner list
      out.println("<tr><td align=\"center\" colspan=\"2\">");
      out.println("<hr size=\"1\" width=\"90%\" color=\"#dfeda3\">");
      out.println("</td></tr>");

      // Print submit and return buttons
      out.println("<tr><td align=\"center\" colspan=\"2\">");
      out.println("<input class=\"btnNorm\" type=\"submit\" name=\"submitOrder\" value=\"Apply\">&nbsp;");
      out.println("<input class=\"btnNorm\" type=\"button\" value=\"Cancel\" onclick=\"location.href=ftSetJsid('Member_partner')\">");
      out.println("<br><br><br></td></tr>");

      // Close activity table
      out.println("</form>");
      out.println("</table>");

      // Close layout table and page
      out.println("</td></tr></table>");
      out.println("</body></html>");

      return result_msg;
  }

  /**
   * resetOrder - Resets all priority values for a specific activity id partner list to 1, returning them to default alphabetical order.
   *
   * @param req Request object
   * @param con Connection to club database
   *
   * @return result_msg - message detailing the results of the method
   */
  private String resetOrder(HttpServletRequest req, Connection con) {

      PreparedStatement pstmt = null;
      PreparedStatement pstmt2 = null;
      ResultSet rs = null;

      String user_id = req.getParameter("user_id");
      String partner_id = "";
      String result_msg = "";
      String activity_name = "";

      int activity_id = Integer.parseInt(req.getParameter("activity_id"));
      int priority = 1;

      try {
          if (activity_id == 0) {
              activity_name = "Golf";
          } else if (activity_id > 0) {
              
              pstmt = con.prepareStatement("SELECT activity_name FROM activities WHERE activity_id = ?");
              pstmt.clearParameters();
              pstmt.setInt(1, activity_id);

              rs = pstmt.executeQuery();

              if (rs.next()) {
                  activity_name = rs.getString("activity_name");
              }

              pstmt.close();
          }

          pstmt = con.prepareStatement(
                  "SELECT partner_id FROM partner p " +
                  "LEFT OUTER JOIN member2b m ON m.username = p.partner_id " +
                  "WHERE user_id = ? AND activity_id = ? " +
                  "ORDER BY m.name_last, m.name_first");
          pstmt.clearParameters();
          pstmt.setString(1, user_id);
          pstmt.setInt(2, activity_id);

          rs = pstmt.executeQuery();

          while (rs.next()) {

              partner_id = rs.getString("partner_id");

              try {
                  pstmt2 = con.prepareStatement("UPDATE partner SET priority = ? WHERE user_id = ? AND activity_id = ? AND partner_id = ?");
                  pstmt2.clearParameters();
                  pstmt2.setInt(1, priority);
                  pstmt2.setString(2, user_id);
                  pstmt2.setInt(3, activity_id);
                  pstmt2.setString(4, partner_id);

                  pstmt2.executeUpdate();

                  pstmt2.close();
                  
                  priority++;

              } catch (Exception exc) {
                  result_msg += "Error: " + exc.getMessage();
              }
          }

          pstmt.close();

      } catch (Exception exc) {
          result_msg += "Error: " + exc.getMessage();
      }

      if (result_msg.equals("")) {
          result_msg = "Partner list" + (!activity_name.equals("") ? " for " + activity_name : "") + " alphabetized!";
      }

      return result_msg;
  }
  
  
  private void displayHandicapOption(HttpServletRequest req, HttpSession session, String user_id, PrintWriter out, Connection con) {

      PreparedStatement pstmt = null;
      ResultSet rs = null;

      String club = (String)session.getAttribute("club");

      int activity_id = 0;
      int displayHndcpIndex = 0;
      
      boolean golfEnabled = getActivity.isGolfEnabled(con);

      //
      //  get this user's current partner handicap display option setting
      //
      try {

         pstmt = con.prepareStatement("SELECT display_partner_hndcp FROM member2b WHERE username = ?");
         pstmt.clearParameters();
         pstmt.setString(1, user_id);
         rs = pstmt.executeQuery();

         if (rs.next()) {

            displayHndcpIndex = rs.getInt("display_partner_hndcp");
         }

         pstmt.close();

      } catch (Exception exc) {

      } finally {

         try {pstmt.close();} catch (Exception ignore){}
      }
      
      //
      // check if user changed this option
      //
      if (req.getParameter("updatehndcp") != null) {
         
         if (displayHndcpIndex == 0) {
            
            displayHndcpIndex = 1;     // toggle it
            
         } else {
            
            displayHndcpIndex = 0;     // toggle it
         }
         
         try {

            pstmt = con.prepareStatement("UPDATE member2b SET display_partner_hndcp = ? WHERE username = ?");
            pstmt.clearParameters();

            pstmt.setInt(1, displayHndcpIndex);
            pstmt.setString(2, user_id);
            pstmt.executeUpdate();

            pstmt.close();

         } catch (Exception exc) {

         } finally {

            try {pstmt.close();} catch (Exception ignore){}
         }
    
      }
      
      out.println("<form method=post name=frmHndcpOption id=frmHndcpOption>");        
      out.println("<input type=hidden name=handicap value=\"yes\">");       // re-trigger this method
      out.println("<input type=hidden name=updatehndcp value=\"yes\">");  
      out.println("<span style=\"font: 11px 'Trebuchet MS',Verdana,Arial,Helvetica,sans-serif;\"><BR><input type=\"checkbox\" " + ((displayHndcpIndex == 1) ? "checked " : "") + "name=\"displayHndcpIndex\" id=\"displayHndcpIndex\" value=\"1\" onChange=\"document.frmHndcpOption.submit()\"><label for=\"displayHndcpIndex\">Show Handicap Indexes in Partner List when booking tee times</label></span>");
      out.println("</form>");
      
  }  // end of displayHandicapOption

}
