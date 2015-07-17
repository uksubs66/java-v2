/***************************************************************************************
 *   Member_partner - Displays the configuration page for members to edit their partner lists.  A separate list for each
 *                    activity is displayed and the member can add and remove members, as well as alter the ordering for each.
 *
 *
 *   created: 11/17/2009 BSK
 *
 *   last updated:
 *
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

      con = SystemUtils.getCon(session);            // get DB connection

      if (con == null) {

          out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
          out.println("<BODY><CENTER>");
          out.println("<BR><BR><H3>Database Connection Error</H3>");
          out.println("<BR><BR>Unable to connect to the Database.");
          out.println("<BR>Please try again later.");
          out.println("<BR><BR>If problem persists, contact customer support.");
          out.println("<BR><BR><a href=\"" +rev+ "/servlet/Admin_announce\">Return</a>");
          out.println("</CENTER></BODY></HTML>");
          return;
      }

      int sess_activity_id = (Integer)session.getAttribute("activity_id");;                    // ID # of current activity

      // Declare Variables
      String result_msg = "";

      boolean golfEnabled = getActivity.isGolfEnabled(con);
      boolean activitiesEnabled = getActivity.isConfigured(con);

      if (req.getParameter("addPartner") != null || req.getParameter("submitPartner") != null) {
          result_msg = addPartner(req, session, out, con);
      }

      if (req.getParameter("removePartner") != null) {
          result_msg = removePartner(req, con);
      }

      if (req.getParameter("changeOrder") != null) {
          result_msg = changeOrder(req, session, out, con);
          return;
      }

      if (req.getParameter("submitOrder") != null) {
          result_msg = changeOrder(req, session, out, con);
      }

      if (req.getParameter("resetOrder") != null) {
          result_msg = resetOrder(req, con);
      }
      
      if (req.getParameter("list") != null) {
          displayPartnerLists(req, session, result_msg, out, con);
          return;
      }

      // Gather activity names and ids
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

      //
      // Display main form
      //
      out.println(SystemUtils.HeadTitle("Partner List Management"));

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
      out.println("document.getElementById(iframeName).style.height = divHeight;");
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
      out.println("   fr.src = '/" + rev + "/servlet/Member_partner?list&addPartner' + params;");
      out.println(" }");
      out.println("}");
      out.println("// -->");
      out.println("</script>");

      out.println("<body bgcolor=\"#F5F5DC\">");

      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page

      out.println("<table align=\"center\" border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"550\">"); // instructions table
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<b>Partner List Management</b><br>");
      if (activity_ids.size() == 1) {
          out.println("<br>To <b>add a new partner</b>, click their name in the list to the right.");
      } else {
          out.println("<br>To <b>add a new partner</b>, first use the checkboxes to select " +
                  "<br>which activities this member will be added to the partner list for.");
          out.println("<br>Next, click the member's name in the list to the right.");
      }
      out.println("<br>To <b>alter the ordering of a partner list</b> click on the \"Change Order\" button.");
      out.println("<br>To <b>return list to alphabetical order</b> click on the \"Alphabetize List\" button.");
      out.println("<br>To <b>remove a member from a partner list</b>, click the 'trash can' icon next to<br> " +
              "their name in the parnter list you'd like them removed from.");
      out.println("<br><br><b>*NOTE* You are no longer limited to 25 partners, and may add as many as desired!!</b>");

      out.println("</font>");
      out.println("</td></tr>");
      out.println("</table><br>");      // close instructions table

      out.println("<table align=\"center\" border=\"0\" bgcolor=\"#F5F5DC\" width=\"80%\">");      // layout table

      out.println("<tr><td align=\"center\" colspan=\"4\">");
      out.println("<button class=\"btnNorm\" onclick=\"location.href='/" + rev + "/servlet/Member_announce'\">Home</button>");
      
      //  If the guest tracking system is in use, display a link to the guest list page
      if (Utilities.isGuestTrackingConfigured(sess_activity_id, con)) out.println("<button class=\"btnNorm\" onclick=\"location.href='/" + rev + "/servlet/Common_guestdb'\">Guest List</button>");

      if (activity_ids.size() > 1) {
          out.println("<button class=\"btnNorm\" onclick=\"location.href='http://www.foretees.com/messages/partners_multi.pdf'\">Help</button>&nbsp;&nbsp;");
      } else {
          out.println("<button class=\"btnNorm\" onclick=\"location.href='http://www.foretees.com/messages/partners.pdf'\">Help</button>");
      }
      out.println("</td></tr>");
      
      out.println("<tr><td align=\"center\" colspan=\"4\"><hr width=\"90%\" size=\"1\" color=\"#dfeda3\"></td></tr>");

      out.println("<tr>");

      // Print iframe to display partner lists
      out.println("<td align=\"center\" valign=\"top\" colspan=\"2\" width=\"60%\">");
      out.println("<iframe id=\"partnerlistiframe\" src=\"/" + rev + "/servlet/Member_partner?list\" width=\"90%\" scrolling=no frameborder=no></iframe>");
      out.println("</td>");  // close left column of layout table


      // Set up form for use with adding a new partner by clicking the name-list on the main config page
      out.println("<form action=\"/" + rev + "/servlet/Member_partner\" method=\"POST\" name=\"playerform\" onsubmit=\"false\">");
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
              out.println("<input type=\"checkbox\" name=\"activity_" + activity_ids.get(i) + "\" checked>&nbsp;&nbsp;" + activity_names.get(i));

              out.println("</td></tr>");
          }
          out.println("</table>");      // close activity select table
          out.println("</td>");
      }

      // Print namelist selection box
      out.println("<td align=\"center\" valign=\"top\"><br>");
      alphaTable.nameList_simple("", 20, false, out, con);
      out.println("</td>");

      out.println("</form>");   // close playerform form
      out.println("</tr>");

      out.println("<tr><td align=\"center\" colspan=\"4\"><hr width=\"90%\" size=\"1\" color=\"#dfeda3\"></td></tr>");
      
      out.println("<tr><td align=\"center\" colspan=\"4\">");
      out.println("<button class=\"btnNorm\" onclick=\"location.href='/" + rev + "/servlet/Member_announce'\">Home</button>");

      //  If the guest tracking system is in use, display a link to the guest list page
      if (Utilities.isGuestTrackingConfigured(sess_activity_id, con)) out.println("<button class=\"btnNorm\" onclick=\"location.href='/" + rev + "/servlet/Common_guestdb'\">Guest List</button>");

      if (activity_ids.size() > 1) {
          out.println("<button class=\"btnNorm\" onclick=\"location.href='http://www.foretees.com/messages/partners_multi.pdf'\">Help</button>&nbsp;&nbsp;");
      } else {
          out.println("<button class=\"btnNorm\" onclick=\"location.href='http://www.foretees.com/messages/partners.pdf'\">Help</button>");
      }
      out.println("</td></tr>");

      out.println("</table>");  // close layout table
      out.println("</body></html>");

  }   // end of doPost


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

      // Gather activity names and ids
      if (golfEnabled) {
          activity_names.add("Golf");
          activity_ids.add(0);
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

      ArrayList<String> activity_names = new ArrayList<String>();
      ArrayList<Integer> activity_ids = new ArrayList<Integer>();

      String activity_name = "";
      String partner_id = "";
      String partner_name = "";
      String success_msg = "";

      int activity_id = 0;
      int count = 0;

      boolean golfEnabled = getActivity.isGolfEnabled(con);
      boolean activitiesEnabled = getActivity.isConfigured(con);

      // See if there's a success message to grab
      if (req.getParameter("success_msg") != null && !req.getParameter("success_msg").equals("")) {
          success_msg = req.getParameter("success_msg");
      }

      try {
          // Gather activity names and ids
          if (golfEnabled) {
              activity_names.add("Golf");
              activity_ids.add(0);
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

      out.println("<body onload=\"parent.window.resizeIFrame(document.getElementById('partnerlistiframediv').offsetHeight, 'partnerlistiframe');\" bgcolor=\"#F5F5DC\" text=\"#000000\">");
      out.println("<div id=\"partnerlistiframediv\">");

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

      out.println(".btnNorm {");
      out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
      out.println("  background: #99CC66;");
      out.println("  width: 80px;");
      out.println("}");

      out.println("</style>");

      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      out.println("function removePartner(user_id, activity_id, partner_id) {");
      out.println(" f = document.forms['removePartnerForm'];");
      out.println(" f.user_id.value = user_id;");
      out.println(" f.activity_id.value = activity_id;");
      out.println(" f.partner_id.value = partner_id;");
      out.println(" f.submit();");
      out.println("}");
      out.println("function alphaList(user_id, activity_id) {");
      out.println(" if (confirm('This will reset this partner list to alphabetical order.  Are you sure you want to do this?')) {");
      out.println("   window.location.href = '/" + rev + "/servlet/Member_partner?list&resetOrder&user_id=' + user_id + '&activity_id=' + activity_id;");
      out.println(" }");
      out.println("}");
      out.println("// -->");
      out.println("</script>");

      // Set up form for use with the remove partner buttons
      out.println("<form action=\"/" + rev + "/servlet/Member_partner\" method=\"POST\" name=\"removePartnerForm\">");
      out.println("<input type=\"hidden\" name=\"user_id\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"activity_id\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"partner_id\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"removePartner\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"list\" value=\"\">");
      out.println("</form>");
    
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
          out.println("<tr><td align=\"center\" colspan=\"2\" bgcolor=\"#336633\">");
          out.println("<font size=\"4\" color=\"white\"><b>" + activity_name + "</b></font>");
          out.println("</td></tr>");

          // Print ordering buttons
          out.println("<form action=\"/" + rev + "/servlet/Member_partner\" method=\"POST\" target=\"bot\" name=\"orderPartnerForm\">");
          out.println("<tr><td align=\"center\" colspan=\"2\" nobreak>");
          out.println("<input type=\"hidden\" name=\"user_id\" value=\"" + user_id + "\">");
          out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");
          out.println("<nobr>");
          out.println("<input class=\"btnOrder\" type=\"submit\" name=\"changeOrder\" value=\"Change Order\"" + (count == 0 ? "disabled ": "") + ">");
          out.println("<input class=\"btnOrder\" type=\"button\" name=\"resetOrder\" value=\"Alphabetize List\" " + (count == 0 ? "disabled ": "") + " onclick=\"alphaList('" + user_id + "'," + activity_id + ")\">");
          out.println("</nobr>");
          out.println("</td></tr>");
          out.println("</form>");

          // Print divider line before start of list of names
          out.println("<tr><td align=\"center\" colspan=\"2\">");
          out.println("<hr size=\"1\" width=\"90%\" color=\"#dfeda3\">");
          out.println("</td></tr>");

          // Don't need to look up parnter info for this activity if none were found earlier
          if (count > 0) {

              try {
                  // Gather all partners for this member from database for the current activity
                  pstmt = con.prepareStatement(
                          "SELECT p.*, CONCAT(m.name_last, ', ', m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi), '')) as partner_name FROM partner p " +
                          "LEFT OUTER JOIN member2b m ON p.partner_id = m.username " +
                          "WHERE p.user_id = ? and p.activity_id = ? " +
                          "ORDER BY priority, m.name_last, m.name_first");
                  pstmt.clearParameters();
                  pstmt.setString(1, user_id);
                  pstmt.setInt(2, activity_id);
                  rs = pstmt.executeQuery();

                  // Loop through the results and print out the partner lists
                  while (rs.next()) {

                      count++;
                      partner_id = rs.getString("partner_id");
                      partner_name = rs.getString("partner_name").trim();

                      // Print parnter removal button (trash can icon)
                      out.println("<tr><td align=\"right\" width=\"35%\">");
                      out.println("<a alt=\"Remove partner\" title=\"Remove partner\" onclick=\"removePartner('" + user_id + "', '" + activity_id + "', '" + partner_id + "')\"><img width=\"13\" height=\"13\" border=\"0\" src=\"/v5/images/dts_trash.gif\"></a>");
                      out.println("</td>");

                      // Print partner name
                      out.println("<td align=\"left\">");
                      out.println(partner_name);
                      out.println("</td></tr>");
                  }

                  pstmt.close();

              } catch (Exception exc) {

                  out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
                  out.println("<BODY><CENTER>");
                  out.println("<BR><BR><H3>Database Connection Error</H3>");
                  out.println("<BR><BR>Unable to connect to the Database.");
                  out.println("<BR>Please try again later.");
                  out.println("<BR><BR>If problem persists, contact customer support.");
                  out.println("<BR><BR><a href=\"" +rev+ "servlet/Member_announce\">Return</a>");
                  out.println("</CENTER></BODY></HTML>");
                  return;
              }

          } else {
              out.println("<tr><td align=\"center\" colspan=\"2\">");
              out.println("No partners found");
              out.println("</td></tr>");
          }

          // Print divider line after list of names
          out.println("<tr><td align=\"center\" colspan=\"2\">");
          out.println("<hr size=\"1\" width=\"90%\" color=\"#dfeda3\">");
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

      int activity_id = Integer.parseInt(req.getParameter("activity_id"));
      int count = 0;

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
          }

          pstmt.close();

          pstmt = con.prepareStatement("DELETE FROM partner WHERE user_id = ? AND activity_id = ? AND partner_id = ?");
          pstmt.clearParameters();
          pstmt.setString(1, user_id);
          pstmt.setInt(2, activity_id);
          pstmt.setString(3, partner_id);

          count = pstmt.executeUpdate();

          pstmt.close();

          if (count > 0) {
              result_msg = partner_name + " removed from list.";
          } else {
              result_msg = "Problem encountered, " + partner_name + " not removed.";
          }

      } catch (Exception exc) {
          result_msg = "Error: " + exc.getMessage();
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

      String user_id = "";
      String activity_name = "";
      String partner_name = "";
      String result_msg = "";

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

                              pstmt2.close();
                          }
                      } catch (Exception exc) {
                          // Print out comment error message and continue to next id without making changes
                          out.println("<!-- Error - invalid input: " + req.getParameter("priority_" + id_curr) + " - skipped -->");
                          continue;
                      }
                  }
              }

              pstmt.close();

              result_msg = "Changes applied!  Make additional changes or click 'Return' to go back to the main partner list display.<br><br>";

          } catch (Exception exc) {
              result_msg = "Unable to update order.  Please try again.";
          }

          return result_msg;
      }

      //
      // Display main form
      //
      out.println(SystemUtils.HeadTitle("Partner List Management"));

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
      out.println("<form action=\"/" + rev + "/servlet/Member_partner\" method=\"POST\" name=\"orderForm\">");
      out.println("<input type=\"hidden\" name=\"user_id\" value=\"" + user_id + "\">");
      out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");

      // Print activity name
      out.println("<tr><td align=\"center\" colspan=\"2\" bgcolor=\"#336633\">");
      out.println("<font size=\"4\" color=\"white\"><b>" + activity_name + "</b></font>");
      out.println("</td></tr>");

      // Print submit and return buttons
      out.println("<tr><td align=\"center\" colspan=\"2\">");
      out.println("<input class=\"btnNorm\" type=\"submit\" name=\"submitOrder\" value=\"Apply\">&nbsp;");
      out.println("<input class=\"btnNorm\" type=\"button\" value=\"Cancel\" onclick=\"location.href='/" + rev + "/servlet/Member_partner'\">");
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
      out.println("<input class=\"btnNorm\" type=\"button\" value=\"Cancel\" onclick=\"location.href='/" + rev + "/servlet/Member_partner'\">");
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

}
