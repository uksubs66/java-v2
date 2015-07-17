/*
 **********************************************************************************************                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          /***************************************************************************************     
 *   Proshop_event_categories:  This servlet will display a list of event categories and allow the club
 *                              to add/update/remove categories that can be applied to different events.
 *                              These categories can then be used to filter what events are displayed on
 *                              the Event Sign Up page.
 *
 *
 *   created: 12/20/2011   Brad K.
 *
 *   last updated:
 *
 *     12/20/11  Servlet created
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.congressionalCustom;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;
import com.foretees.common.Utilities;


public class Proshop_event_categories extends HttpServlet {
                               

 String rev = SystemUtils.REVLEVEL;                          // Software Revision Level (Version)
 
 static String DINING_USER = ProcessConstants.DINING_USER;   // Dining username for Admin user from Dining System
 static int dining_activity_id = ProcessConstants.DINING_ACTIVITY_ID;    // Global activity_id for Dining System  
 


 //********************************************************************************
 //
 //  doGet - gets control from Proshop_content to display a list of events
 //
 //********************************************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

     doPost(req, resp);                          // call doPost processing

 }   // end of doGet
 
 
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
     
     HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder
     
     if (session == null) {
         
         return;
     }
     
     Connection con = SystemUtils.getCon(session);            // get DB connection
     
     if (con == null) {
         
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact support.");
         out.println("<BR><BR>");
         out.println("<form><p align=center>");
         out.println("<input type=\"button\" value=\"Cancel - Close\" onClick='self.close();'>");
         out.println("</p></form>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
     String club = (String)session.getAttribute("club");      // get club name
     String user = (String)session.getAttribute("user");      // get pro's username
     String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
     int lottery = Integer.parseInt(templott);
     int sess_activity_id = (Integer)session.getAttribute("activity_id");
     
     PreparedStatement pstmt = null;
     PreparedStatement pstmt2 = null;
     ResultSet rs = null;
     
     int selected_id = 0;
     int curr_category_id = 0;
     int count = 0;
     
     String selected_category_name = "";
     String result_message = "";
     String category_name = "";
     
     boolean error = false;
     
     if (req.getParameter("selected_id") != null && req.getParameter("reset") == null) selected_id = Integer.parseInt(req.getParameter("selected_id"));
     
     if (selected_id != 0) {
         selected_category_name = Utilities.getEventCategoryNameFromId(selected_id, con);
     }
     
     if (req.getParameter("remove") != null) {
         
         try {
             
             count = 0;
             
             // Remove any event category bindings that exist for this category
             pstmt = con.prepareStatement("DELETE FROM event_category_bindings WHERE category_id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, selected_id);
             
             pstmt.executeUpdate();
             
             pstmt.close();
             
             // Remove the event category from the category list
             pstmt = con.prepareStatement("DELETE FROM event_categories WHERE category_id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, selected_id);
             
             count = pstmt.executeUpdate();
             
             if (count > 0) {
                 result_message = "Category removed successfully";
             }
             
         } catch (Exception exc) { 
             Utilities.logError("Proshop_event_categories.doPost - " + club + " - Error removing event category - ERR: " + exc.toString());
         } finally {
             
             try { rs.close(); }
             catch (Exception ignore) { }
             
             try { pstmt.close(); }
             catch (Exception ignore) { }
         }
         
         selected_id = 0;
         selected_category_name = "";
         
     } else if (req.getParameter("update") != null) {
         
         // Only bother trying to update a category if a name was entered.
         if (req.getParameter("category_name") != null && !req.getParameter("category_name").equals("")) {
             
             category_name = req.getParameter("category_name");
             
             // Scan the name for special characters and reject if it contains any.
             error = SystemUtils.scanName(category_name);
             
             if (error) {
                 
                 result_message = "Category names cannot contain special characters.";
                 
             } else {
             
                 try {

                     pstmt = con.prepareStatement("SELECT * FROM event_categories WHERE activity_id = ? AND category_name = ? AND category_id <> ?");
                     pstmt.clearParameters();
                     pstmt.setInt(1, sess_activity_id);
                     pstmt.setString(2, category_name);
                     pstmt.setInt(3, selected_id);

                     rs = pstmt.executeQuery();

                     if (rs.next()) {
                         result_message = "A category already exists with that name";
                     } else {

                         try {

                             count = 0;

                             // No duplicate found, add the new category
                             pstmt2 = con.prepareStatement("UPDATE event_categories SET category_name = ? WHERE category_id = ?");
                             pstmt2.clearParameters();
                             pstmt2.setString(1, category_name);
                             pstmt2.setInt(2, selected_id);

                             count = pstmt2.executeUpdate();

                             if (count > 0) {
                                 result_message = "Category updated successfully!";
                             }

                         } catch (Exception exc) { 
                             Utilities.logError("Proshop_event_categories.doPost - " + club + " - Error updating category - ERR: " + exc.toString());
                         } finally {

                             try { pstmt2.close(); }
                             catch (Exception ignore) { }
                         }
                     }

                 } catch (Exception exc) { 
                     Utilities.logError("Proshop_event_categories.doPost - " + club + " - Error looking up duplicate categories (update) - ERR: " + exc.toString());
                 } finally {

                     try { rs.close(); }
                     catch (Exception ignore) { }

                     try { pstmt.close(); }
                     catch (Exception ignore) { }
                 } 
             }
         }
         
         selected_id = 0;
         selected_category_name = "";
         
     } else if (req.getParameter("add") != null) {
         
         // Only bother trying to add a category if a name was entered.
         if (req.getParameter("category_name") != null && !req.getParameter("category_name").equals("")) {
             
             category_name = req.getParameter("category_name");
             
             // Scan the name for special characters and reject if it contains any.
             error = SystemUtils.scanName(category_name);
             
             if (error) {
                 
                 result_message = "Category names cannot contain special characters.";
                 
             } else {
             
                 try {

                     pstmt = con.prepareStatement("SELECT * FROM event_categories WHERE activity_id = ? AND category_name = ?");
                     pstmt.clearParameters();
                     pstmt.setInt(1, sess_activity_id);
                     pstmt.setString(2, category_name);

                     rs = pstmt.executeQuery();

                     if (rs.next()) {
                         result_message = "A category already exists with that name";
                     } else {

                         try {

                             count = 0;

                             // No duplicate found, add the new category
                             pstmt2 = con.prepareStatement("INSERT INTO event_categories (activity_id, category_name) VALUES (?,?)");
                             pstmt2.clearParameters();
                             pstmt2.setInt(1, sess_activity_id);
                             pstmt2.setString(2, category_name);

                             count = pstmt2.executeUpdate();

                             if (count > 0) {
                                 result_message = "Category added successfully!";
                             }

                         } catch (Exception exc) { 
                             Utilities.logError("Proshop_event_categories.doPost - " + club + " - Error adding new category - ERR: " + exc.toString());
                         } finally {

                             try { pstmt2.close(); }
                             catch (Exception ignore) { }
                         }
                     }

                 } catch (Exception exc) { 
                     Utilities.logError("Proshop_event_categories.doPost - " + club + " - Error looking up duplicate categories (update) - ERR: " + exc.toString());
                 } finally {

                     try { rs.close(); }
                     catch (Exception ignore) { }

                     try { pstmt.close(); }
                     catch (Exception ignore) { }
                 } 
             }
         }
         
         selected_id = 0;
         selected_category_name = "";
     }
     
     // START PAGE OUTPUT
     out.println(SystemUtils.HeadTitle("Proshop Event Category Management Page"));

     out.println("<script type=\"text/javascript\">");
     out.println("function selectCategory(selected_id) {");
     out.println(" f = document.forms[\"frmEventCategories\"];");
     out.println(" f.selected_id.value = selected_id;");
     out.println(" f.submit();");
     out.println("}");
     out.println("</script>");
        
     out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
     SystemUtils.getProshopSubMenu(req, out, lottery);
     out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
         
     out.println("<br><h2 align=center>Event Category Management</h2>");
     
     out.println("<table width=660 align=center cellpadding=5 bgcolor=\"#336633\">");
     out.println("<tr><td><font color=white size=2>");
     out.println("<b>Instructions:</b>&nbsp;&nbsp;");
     out.println("Use the form below to manage your event categories.&nbsp; Once your event categories are defined in the ");
     out.println("system, your staff will be able to assign them to events<br><br>");
     out.println("To <b>ADD</b> a category, type the name in the text box and click 'Add'. (Use A-Z, a-z, 0-9 and spaces <b>only</b>)<br>");
     out.println("To <b>UPDATE</b> a category, click on the category in the list, make the desired changes, and then click 'Update'.<br>");
     out.println("To <b>REMOVE</b> a category, select it in the list and click 'Remove'.<br>");
     out.println("To <b>RESET</b> the form and clear any selections and entered text, click 'Reset'.");
     out.println("</font></td></tr>");
     out.println("</table><br>");

     // OUTPUT ERROR MESSAGE
     if (!result_message.equals("")) {
         
         out.println("<span align=center style=\"align:center; background-color:#F5F5DC; border:1px solid #336633; padding:7px\">");
         out.println(result_message);
         out.println("</span><br><br>");
     } else {
         out.println("<br><br>");
     }
     
     out.println("<table border=0 width=450 align=center bgcolor=#F5F5DC>");
     
     // DEFINED CLUBS
     out.println("<form method=\"POST\" action=\"Proshop_event_categories\" name=\"frmEventCategories\">");
     out.println("<tr bgcolor=\"#336633\"><td align=center><b><font color=white>&nbsp;Event Categories</font></b></td></tr>");
     out.println("<tr><td align=\"center\">");
     out.println("<br><select name=\"selected_id\" size=15 onchange=\"selectCategory(this.options[this.selectedIndex].value)\" style=\"width:180px\">");
     
     try {
         
         pstmt = con.prepareStatement("SELECT category_id, category_name FROM event_categories WHERE activity_id = ? ORDER BY category_name");
         pstmt.clearParameters();
         pstmt.setInt(1, sess_activity_id);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
             
             curr_category_id = rs.getInt(1);
             
             out.println("<option value=\"" + curr_category_id + "\"" + (selected_id == curr_category_id ? " selected" : "") + ">" + rs.getString("category_name") + "</option>");
         }
         
     } catch (Exception exc) { 
         Utilities.logError("Proshop_event_categories.doPost - " + club + " - Error printing event category options - ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     out.println("</select><br><br>");
     out.println("</td></tr>");
     out.println("<tr><td align=\"center\">");
     out.println("Category Name: <input type=\"text\" name=\"category_name\" size=\"20\" maxlength=\"30\" value=\"" + selected_category_name + "\"><br><br>");
     out.println("</td></tr>");
     
     
     if (selected_id != 0 && req.getParameter("add") == null) {
         
         out.println("<tr><td align=center>");

         out.println("<tr><td align=center colspan=2>");
         out.println("<input type=submit name=update value=\"Update\" style=\"width:80px\"> &nbsp;&nbsp; ");
         out.println("<input type=submit name=remove value=\"Remove\" style=\"width:80px\" onclick=\"return confirm('This event category will be removed from the system and no longer associated with any events.\\n\\nAre you sure you want to do this?');\"> &nbsp;&nbsp; ");
         out.println("<input type=submit name=reset value=\"Reset\" style=\"width:80px\"><br><br>");
         out.println("</td></tr>");
         
     } else {
         
         out.println("<tr><td align=center>");

         out.println("<tr><td align=center colspan=2>");
         out.println("<input type=submit name=add value=\"Add\" style=\"width:80px\"> &nbsp;&nbsp; ");
         out.println("<input type=submit disabled name=remove value=\"Remove\" style=\"width:80px\"> &nbsp;&nbsp; ");
         out.println("<input type=submit name=reset value=\"Reset\" style=\"width:80px\"><br><br>");
         out.println("</td></tr>");
     }
     
     out.println("</table>");
     out.println("</form>");
     
     
 }
 
}
