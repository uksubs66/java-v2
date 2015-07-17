/***************************************************************************************
 *   Admin_mships:  This servlet will allow users to add/edit/remove, as well as modify permissions, of membership types.
 *                  Clubs with more than one configured activity MUST manage membership types here, but available for all clubs.
 *
 *
 *   created: 10/02/2009 BSK)
 *
 *   last updated:
 *
 *   10/14/11   Fixed issue where a blank mship, or one comprised of spaces, could be submitted when adding or editing mships.
 *   10/05/11   Updated processing so copies of the mship5 entry are automatically generated for Golf and all configured FlxRez parent activities when a new mship is added.
 *    3/01/10   Only admin4tea can see and use the activity checkboxes. 
 *   10/05/09   Cleaned up the page a bit
 *   10/02/09   Class created
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

public class Admin_mships extends HttpServlet {

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

  /**
  ***************************************************************************************
  *
  * This method will forward the request and response onto the the post method
  *
  ***************************************************************************************
  **/

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

      resp.setContentType("text/html");
      PrintWriter out = resp.getWriter();

      Connection con = null;                 // init DB objects

      Statement stmt = null;
      PreparedStatement pstmt = null;

      ResultSet rs = null;

      ArrayList<String> mships = new ArrayList<String>();
      ArrayList<String> activity_names = new ArrayList<String>();
      ArrayList<Integer> activity_ids = new ArrayList<Integer>();
      ArrayList<Integer> allowedActivities = new ArrayList<Integer>();

      HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

      if (session == null) {

          return;
      }

      String club = (String)session.getAttribute("club");
      String user = (String)session.getAttribute("user");               // get username

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

      int sess_activity_id = 0;                    // ID # of activity (0 since we're on admin)

      int addError = 0;
      String mship = "";
      boolean editError = false;


      // Check for other processing that needs to be done
      if (req.getParameter("addMship") != null && req.getParameter("mshipToAdd") != null) {
          addError = addMship(req, con);
          if (addError == 0) {
              mship = req.getParameter("mshipToAdd");
          }
      }

      if (req.getParameter("editMship") != null || req.getParameter("delMship") != null) {
          editError = editMship(req, con);
      }


      //
      // Display main form
      //
      if (req.getParameter("mship") != null && req.getParameter("editMship") == null && req.getParameter("delMship") == null) {
          mship = req.getParameter("mship");
      }

      try {

          boolean first = true;
          
          // Gather all membership types from database
          stmt = con.createStatement();
          rs = stmt.executeQuery("SELECT mship FROM mship5 GROUP BY mship ORDER BY mship");

          while (rs.next()) {
              // if first time and no mship specified, just populate with the first one in the list
              if (first && mship.equals("")) {
                  mship = rs.getString("mship");
                  first = false;
              }
              mships.add(rs.getString("mship"));
          }

          stmt.close();


          // Add golf to activity list if it's enabled for this club
          if (getActivity.isGolfEnabled(con)) {
              activity_names.add("Golf");
              activity_ids.add(0);
          }


          // Gather the rest of the activities for this club from database
          stmt = con.createStatement();
          rs = stmt.executeQuery("SELECT activity_name, activity_id FROM activities WHERE parent_id = 0 ORDER BY activity_name");

          while (rs.next()) {
              activity_names.add(rs.getString("activity_name"));
              activity_ids.add(rs.getInt("activity_id"));
          }

          stmt.close();


          // Gather list of activities that currently have access to this membership type
          pstmt = con.prepareStatement("SELECT activity_id FROM mship5 WHERE mship = ? ORDER BY activity_id");
          pstmt.clearParameters();
          pstmt.setString(1, mship);
          rs = pstmt.executeQuery();

          while (rs.next()) {
              allowedActivities.add(rs.getInt("activity_id"));
          }

          pstmt.close();

      } catch (Exception exc) {

          out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
          out.println("<BODY><CENTER>");
          out.println("<BR><BR><H3>Database Connection Error</H3>");
          out.println("<BR><BR>Unable to connect to the Database.");
          out.println("<BR>Please try again later.");
          out.println("<BR><BR>If problem persists, contact customer support.");
          out.println("<BR><BR><a href=\"" +rev+ "servlet/Admin_announce\">Return</a>");
          out.println("</CENTER></BODY></HTML>");
          return;
      }

      out.println("<body bgcolor=\"#F5F5DC\">");

      out.println("<table align=\"center\" border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"700\"><tr><td align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<b>Membership Type Management</b><br>");
      out.println("<br>To <b>add a new membership type</b>, type it in the \"Add New Membership Type\" box and click Add.");
      out.println("<br><br>To <b>alter the name and permissions for a membership type</b>, select it from the drop-down menu and " +
              "make changes below.  The spelling of the membership type can be altered, and checked boxes signify which " +
              "activities this membership type will be able to access.");
      out.println("<br><br>Click on <b>'Submit'</b> to apply any changes.");
      out.println("<br>Click on <b>'Delete'</b> to remove a membership type from the system.");
      out.println("<br>Click on <b>'Cancel'</b> to exit without making changes.");
      out.println("<br><br>To <b>alter days in advance and viewing privileges</b>, log in as Proshop, switch to the desired activity, " +
              "and navigate to System Config > Club Setup > Membership Options and apply the desired values.  Repeat this for any " +
              "additional activities this membership type has access to.  A single membership type can have different days in " +
              "advance settings for different activities.");

      out.println("</font>");
      out.println("</td></tr></table><br><br>");

      out.println("<table align=\"center\" border=\"0\" bgcolor=\"#F5F5DC\">");      // table 1 (outer)
      out.println("<tr><td align=\"center\">");

      out.println("<table border=\"0\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");

      // Print Add mship form
      out.println("<tr>");
      out.println("<form action=\"Admin_mships\" method=\"POST\" name=\"addMshipForm\">");
      out.println("<td align=\"right\">");
      out.println("Add New Membership Type: ");
      out.println("<input type=\"text\" name=\"mshipToAdd\" size=\"15\" maxlength=\"30\">");
      out.println("</td><td align=\"left\">");
      out.println("<input type=\"submit\" name=\"addMship\" value=\"Add\">");
      out.println("</td>");
      out.println("</form>");
      out.println("</tr>");

      // Print divider line
      out.println("<tr><td align=\"center\" colspan=\"2\">");
      out.println("<hr size=\"1\" width=\"60%\">");
      out.println("</td></tr>");

      // Print activity selection box
      out.println("<form action=\"Admin_mships\" method=\"POST\" name=\"editMshipForm\">");
      out.println("<tr>");
      out.println("<td align=\"center\" colspan=\"2\">");
      out.println("<br>Select Membership Type to Edit: ");
      out.println("<select name=\"mship\" size=\"1\" onChange=\"document.editMshipForm.submit()\">");

      for (int i=0; i<mships.size(); i++) {

          // First see if this mship should be selected
          String selected = "";

          if (mships.get(i).equals(mship)) selected = "selected ";

          out.println("<option " + selected + "value=\"" + mships.get(i) + "\">" + mships.get(i) + "</option>");
      }

      out.println("</select>");
      out.println("</td></tr>");

      // Print edit mship text box
      out.println("<tr><td align=\"center\">");
      out.println("<br>Membership Type: <input type=\"text\" name=\"mshipName\" size=\"15\" maxlength=\"30\" value=\"" + mship + "\">");
      out.println("</td></tr>");

      if (user.equals("admin4tea")) {

          // Print list of Activities
          out.println("<tr><td align=\"center\" colspan=\"2\">");
          out.println("Activities with access to this membership type:");
          out.println("<table border=\"0\" bgcolor=\"F5F5DC\">");

          for (int i=0; i<activity_ids.size(); i++) {

              // First see if this checkbox should be checked or not
              String checked = "";

              for (int j=0; j<allowedActivities.size(); j++) {
                  if (allowedActivities.get(j).equals(activity_ids.get(i))) {
                      checked = "checked ";
                      break;
                  }
              }

              out.println("<tr>");
              out.println("<td align=\"right\"><input type=\"checkbox\" " + checked + "name=\"act_" + activity_ids.get(i) + "\" value=\"" + activity_ids.get(i) + "\"></td>");
              out.println("<td align=\"left\">" + activity_names.get(i) + "</td>");
              out.println("</tr>");
          }
          out.println("</table>");
          out.println("</td></tr>");
      } else {

          // If not ForeTees staff user, print hidden inputs for all activities that would be checked otherwise.
          for (int i=0; i<activity_ids.size(); i++) {

              for (int j=0; j<allowedActivities.size(); j++) {
                  if (allowedActivities.get(j).equals(activity_ids.get(i))) {
                      out.println("<input type=\"hidden\" name=\"act_" + activity_ids.get(i) + "\" value=\"" + activity_ids.get(i) + "\">");
                      break;
                  }
              }
          }
      }

      out.println("<tr>");
      out.println("<td align=\"center\" colspan=\"2\"><br>" +
              "<input type=\"submit\" name=\"editMship\" value=\"Submit\">&nbsp;" +
              "<input type=\"submit\" name=\"delMship\" value=\"Delete\" onClick=\"return confirm('This will completely remove this membership type from the system.\\n\\n" +
              "Are you sure you want to continue?')\">&nbsp;" +
              "<button type=\"button\" onClick=\"location.href='Admin_announce'\">Cancel</button>" +
              "</td>");
      out.println("</tr>");
      out.println("</form>");
      out.println("</table>");

      out.println("</td></tr>");
      out.println("</table>");  // close table 1
      out.println("</body></html>");

  }   // end of doPost

  private int addMship(HttpServletRequest req, Connection con) {

      PreparedStatement pstmt = null;
      Statement stmt = null;
      ResultSet rs = null;

      int error = 0;
      int count = 0;
      int curr_activity_id = 0;
      int foretees_mode = 0;

      String mship = "";

      // Get the mship to add from the request object
      if (req.getParameter("mshipToAdd") != null) {
          mship = req.getParameter("mshipToAdd").trim();
      }

      if (mship.equals("")) {
          error = 1;
      }
      
      if (error == 0) {
          
          try {

              // See if this mship already exists
              pstmt = con.prepareStatement("SELECT mship FROM mship5 WHERE mship = ? LIMIT 1");
              pstmt.clearParameters();
              pstmt.setString(1, mship);

              rs = pstmt.executeQuery();

              if (rs.next()) {
                  error = 2;
              }

          } catch (Exception exc) {
              Utilities.logError("Admin_mships.addMship - Error checking for dup mship - Err: " + exc.toString());
              error = 3;
          } finally { 

              try { pstmt.close(); }
              catch (Exception ignore) { }
          }
      }

      if (error == 0) {

          // Check to see if Golf is active and add an instance of the mship for it
          try {

              stmt = con.createStatement();

              rs = stmt.executeQuery("SELECT foretees_mode FROM club5");

              if (rs.next()) {

                  foretees_mode = rs.getInt(1);

                  if (foretees_mode > 0) {
                      try {

                          // If no error, add mship to mship5 table with activity_id of -1 (entry will be deleted upon first config)
                          pstmt = con.prepareStatement(
                                  "INSERT INTO mship5 (mship, activity_id, mtimes, period, " +
                                  "days1, days2, days3, days4, days5, days6, days7, " +
                                  "advhrd1, advmind1, advamd1, advhrd2, advmind2, advamd2, advhrd3, advmind3, advamd3, " +
                                  "advhrd4, advmind4, advamd4, advhrd5, advmind5, advamd5, " +
                                  "advhrd6, advmind6, advamd6, advhrd7, advmind7, advamd7, " +
                                  "mpos, mposc, m9posc, mshipItem, mship9Item, viewdays, tflag) " +
                                  "VALUES (?,0,0,'', " +
                                  "0,0,0,0,0,0,0, " +
                                  "0,0,'',0,0,'',0,0,'', " +
                                  "0,0,'',0,0,'', " +
                                  "0,0,'',0,0,'', " +
                                  "'','','','','',30,'')");
                          pstmt.clearParameters();
                          pstmt.setString(1, mship);

                          count = pstmt.executeUpdate();

                          if (count != 1) {
                              error = 2;
                          }

                      } catch (Exception exc) {
                          Utilities.logError("Admin_mships.addMship - Error inserting mships - Err: " + exc.toString());
                          error = 3;
                      } finally {

                          try { pstmt.close(); }
                          catch (Exception ignore) { }
                      }
                  }
              }

          } catch (Exception exc) {
              Utilities.logError("Admin_mships.addMship - Error gathering activity_ids - Err: " + exc.toString());
              error = 3;
          } finally {

              try { rs.close(); }
              catch (Exception ignore) { }

              try { stmt.close(); }
              catch (Exception ignore) { }
          }

          // Now add an instance for every activity
          try {

              stmt = con.createStatement();

              rs = stmt.executeQuery("SELECT activity_id FROM activities WHERE parent_id = 0");

              while (rs.next()) {

                  curr_activity_id = rs.getInt(1);

                  try {

                      // If no error, add mship to mship5 table with activity_id of -1 (entry will be deleted upon first config)
                      pstmt = con.prepareStatement(
                              "INSERT INTO mship5 (mship, activity_id, mtimes, period, " +
                              "days1, days2, days3, days4, days5, days6, days7, " +
                              "advhrd1, advmind1, advamd1, advhrd2, advmind2, advamd2, advhrd3, advmind3, advamd3, " +
                              "advhrd4, advmind4, advamd4, advhrd5, advmind5, advamd5, " +
                              "advhrd6, advmind6, advamd6, advhrd7, advmind7, advamd7, " +
                              "mpos, mposc, m9posc, mshipItem, mship9Item, viewdays, tflag) " +
                              "VALUES (?,?,0,'', " +
                              "0,0,0,0,0,0,0, " +
                              "0,0,'',0,0,'',0,0,'', " +
                              "0,0,'',0,0,'', " +
                              "0,0,'',0,0,'', " +
                              "'','','','','',30,'')");
                      pstmt.clearParameters();
                      pstmt.setString(1, mship);
                      pstmt.setInt(2, curr_activity_id);

                      count = pstmt.executeUpdate();

                      if (count != 1) {
                          error = 2;
                      }

                  } catch (Exception exc) {
                      Utilities.logError("Admin_mships.addMship - Error inserting mships - Err: " + exc.toString());
                      error = 3;
                  } finally {

                      try { pstmt.close(); }
                      catch (Exception ignore) { }
                  }
              }

          } catch (Exception exc) {
              Utilities.logError("Admin_mships.addMship - Error gathering activity_ids - Err: " + exc.toString());
              error = 3;
          } finally {

              try { rs.close(); }
              catch (Exception ignore) { }

              try { stmt.close(); }
              catch (Exception ignore) { }
          }
      }

      return error;
  }

  private boolean editMship(HttpServletRequest req, Connection con) {

      PreparedStatement pstmt = null;
      PreparedStatement pstmt2 = null;
      Statement stmt = null;
      ResultSet rs = null;

      String mship = "";
      String mshipName = "";

      int count = 0;

      boolean error = false;

      if (req.getParameter("delMship") != null) { 
          
          // Delete the selected membership type from the database

          if (req.getParameter("mship") != null) mship = req.getParameter("mship").trim();

          if (!mship.equals("")) {
              try {
                  pstmt = con.prepareStatement("DELETE FROM mship5 WHERE mship = ?");
                  pstmt.clearParameters();
                  pstmt.setString(1, mship);

                  count = pstmt.executeUpdate();

                  pstmt.close();
                  
              } catch (Exception exc) {
                  error = true;
              }
          }

      } else if (req.getParameter("editMship") != null) {

          ArrayList<Integer> activity_ids = new ArrayList<Integer>();

          // Get selected mship from request object
          if (req.getParameter("mship") != null) mship = req.getParameter("mship").trim();
          if (req.getParameter("mshipName") != null) mshipName = req.getParameter("mshipName").trim();

          if (!mship.equals("")) {  // If no mship selected, don't bother continuing!!

              try {

                  // If mshipName not blank, see if spelling changes were made and apply if so
                  if (!mshipName.equals("")) {
                      if (!mshipName.equals(mship)) {
                          // Update all instances in mship 5
                          pstmt = con.prepareStatement("UPDATE mship5 SET mship = ? WHERE mship = ?");
                          pstmt.clearParameters();
                          pstmt.setString(1, mshipName);
                          pstmt.setString(2, mship);

                          count = pstmt.executeUpdate();

                          pstmt.close();

                          // Update all members with this mship
                          pstmt = con.prepareStatement("UPDATE member2b SET m_ship = ? WHERE m_ship = ?");
                          pstmt.clearParameters();
                          pstmt.setString(1, mshipName);
                          pstmt.setString(2, mship);

                          count = pstmt.executeUpdate();

                          pstmt.close();

                          mship = mshipName;  // Use the new value for the rest of the update procedure
                      }
                  }

                  // First check to see if golf is enabled for this club
                  if (getActivity.isGolfEnabled(con)) {
                      activity_ids.add(0);
                  }

                  // Gather a list of all other activity ids
                  stmt = con.createStatement();
                  rs = stmt.executeQuery("SELECT activity_id FROM activities WHERE parent_id = 0 ORDER BY activity_id");

                  while (rs.next()) {
                      activity_ids.add(rs.getInt("activity_id"));
                  }

                  stmt.close();

                  // Delete activity_id = -1 instance of this mship (will readd later if needed)
                  pstmt = con.prepareStatement("DELETE FROM mship5 WHERE mship = ? AND activity_id = -1");
                  pstmt.clearParameters();
                  pstmt.setString(1,mship);

                  count = pstmt.executeUpdate();

                  pstmt.close();

                  // Loop through all activity ids, if found in the request object, then add/update them in the database, if not, delete them
                  for (int i=0; i<activity_ids.size(); i++) {

                      if (req.getParameter("act_" + activity_ids.get(i)) != null) {

                          // See if there's already an instance of this mship for this activity_id
                          pstmt = con.prepareStatement("SELECT mship FROM mship5 WHERE mship = ? AND activity_id = ?");
                          pstmt.clearParameters();
                          pstmt.setString(1, mship);
                          pstmt.setInt(2, activity_ids.get(i));

                          rs = pstmt.executeQuery();

                          if (!rs.next()) {

                              // No record found for this activity_id, create one!
                              pstmt2 = con.prepareStatement(
                                      "INSERT INTO mship5 (mship, activity_id, mtimes, period, " +
                                      "days1, days2, days3, days4, days5, days6, days7, " +
                                      "advhrd1, advmind1, advamd1, advhrd2, advmind2, advamd2, advhrd3, advmind3, advamd3, " +
                                      "advhrd4, advmind4, advamd4, advhrd5, advmind5, advamd5, " +
                                      "advhrd6, advmind6, advamd6, advhrd7, advmind7, advamd7, " +
                                      "mpos, mposc, m9posc, mshipItem, mship9Item, viewdays, tflag) " +
                                      "VALUES (?,?,0,'', " +
                                      "0,0,0,0,0,0,0, " +
                                      "0,0,'',0,0,'',0,0,'', " +
                                      "0,0,'',0,0,'', " +
                                      "0,0,'',0,0,'', " +
                                      "'','','','','',30,'')");
                              pstmt2.clearParameters();
                              pstmt2.setString(1, mship);
                              pstmt2.setInt(2, activity_ids.get(i));

                              count = pstmt2.executeUpdate();

                              pstmt2.close();
                          }

                          pstmt.close();

                      } else {
                          
                          pstmt = con.prepareStatement("DELETE FROM mship5 WHERE mship = ? AND activity_id = ?");
                          pstmt.clearParameters();
                          pstmt.setString(1, mship);
                          pstmt.setInt(2, activity_ids.get(i));

                          count = pstmt.executeUpdate();

                          pstmt.close();
                      }

                  }

                  // Now check to see if this mship is present in mship5 anymore, if not, add an activity_id = -1 instance so it's not deleted completely!
                  pstmt = con.prepareStatement("SELECT mship FROM mship5 WHERE mship = ? LIMIT 1");
                  pstmt.clearParameters();
                  pstmt.setString(1, mship);
                  
                  rs = pstmt.executeQuery();

                  if (!rs.next()) {
                      pstmt2 = con.prepareStatement(
                                      "INSERT INTO mship5 (mship, activity_id, mtimes, period, " +
                                      "days1, days2, days3, days4, days5, days6, days7, " +
                                      "advhrd1, advmind1, advamd1, advhrd2, advmind2, advamd2, advhrd3, advmind3, advamd3, " +
                                      "advhrd4, advmind4, advamd4, advhrd5, advmind5, advamd5, " +
                                      "advhrd6, advmind6, advamd6, advhrd7, advmind7, advamd7, " +
                                      "mpos, mposc, m9posc, mshipItem, mship9Item, viewdays, tflag) " +
                                      "VALUES (?,-1,0,'', " +
                                      "0,0,0,0,0,0,0, " +
                                      "0,0,'',0,0,'',0,0,'', " +
                                      "0,0,'',0,0,'', " +
                                      "0,0,'',0,0,'', " +
                                      "'','','','','',30,'')");
                      pstmt2.clearParameters();
                      pstmt2.setString(1, mship);

                      count = pstmt2.executeUpdate();

                      pstmt2.close();
                  }

                  pstmt.close();

              } catch (Exception exc) {
                  error = true;
              }
          }
      }

      return error;
  }

}
