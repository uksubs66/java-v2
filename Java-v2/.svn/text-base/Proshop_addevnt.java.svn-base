/***************************************************************************************     
 *   Proshop_addevnt:  This servlet will process the 'add event' request from Proshop's
 *                     addevnt page.
 *
 *
 *   called by:  Proshop_events
 *
 *   created: 12/19/2001   Bob P.
 *
 *   last updated:
 * 
 *        9/26/14   Add POS charge data for Abacus21 (at Waialae).
 *        4/01/14   Activity locations will now use the sort_by field to order things.
 *        2/18/14   Interlachen - add a question for the tees that member will play from (custom).
 *        2/18/14   Remove ext_Q custom - event questions have been increased to 64 chars (from 32).
 *        1/08/14   Add Golf Genius export type.
 *        6/05/13   Add Event POS charge fields for TAI (Bald Peak to start with).
 *        2/13/13   Tweak TinyMCE settings
 *        1/06/13   Add TinyMCE to the new event form, cleaned up some html for more consistant rendering
 *       11/08/12   Add option (memedit) to allow members to edit event times after moved to tee sheet. 
 *        9/21/12   Add Palatino Linotype font to the tinyMCE editor
 *       12/20/11   Added support for selecting event categories when adding/editing events (case 2076).
 *        9/28/11   Added sanity checking to the fb value when adding an event via copy to prevent it from being set to NULL for FlxRez events.
 *       10/12/10   Fixed a spot still using golf terminology
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *       12/16/09   Added ext_Q boolean to allow clubs to ask custom questions over 32 chars in length
 *                  note: need to update otherQ1-3 in the clubs db table before adding them to this custom
 *       10/12/09   Changes to prevent doubles spaces in event names
 *        9/28/09   Added support for Activities
 *        6/25/09   Add Tournament Expert (TourEx) option to export types
 *        5/01/09   Allow sseason to arrive null and trim event name & copy name
 *        3/02/09   Add asterisk to guest only questions
 *        2/05/09   Added 72 holes as an option during event setup
 *       12/03/08   Add GolfNet TMS option to export types
 *       11/10/08   Added changes for additional signup information
 *        9/02/08   Javascript compatability updates
 *        8/11/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        7/01/08   Do not verify the actual event time so pro can define an event without blocking times.
 *        5/19/08   Subtract one day from event date for season long events (to prevent appearing on tee sheet)
 *        5/05/08   Remove Event man option until ready for implementation.
 *        4/07/08   Fixes for season long events
 *        3/24/08   Add gender field to event config
 *        2/27/08   Removed message of 254 char restriction on Itinerary - removed actual limitation last year
 *       10/14/07   Do not update 'holes' on 2nd page - it was getting overwritten.
 *        9/25/07   Add minimum sign-up size to configurable options
 *       10/11/06   Call Common_Config to output the colors.
 *        6/22/06   Remove POS charge codes until the POS interfaces are added for events.
 *        5/10/05   Add mtype and mship restrictions to the events.
 *        2/16/05   Ver 5 - start with today's date and use common function to set it.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        1/07/05   If copying an event, copy the 2nd page also - get name from Proshop_events.
 *        7/08/04   Add 'Make Copy' option to allow pro to copy an existing event.
 *        5/05/04   Add 2nd set of block times for events.
 *        5/05/04   Move Itinery from 2nd page to first page so it is always included.
 *                  Add 2nd set of blocker times so pro can block off another group of
 *                  times for cross-overs during event.
 *        2/16/04   Enhancements for Version 4.  Add dynamic Modes of Trans options.
 *                  Add POS Charge fields for cost of event.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        2/10/03   Enhancement for V2 - add event sign-up.
 *        1/14/03   Add SignUp option for members to sign up online.
 *        1/06/03   Enhancements for Version 2 of the software.
 *                  Add support for multiple courses.
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;

// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.dateOpts;
import com.foretees.common.Labels;
import com.foretees.common.getActivity;
import com.foretees.common.parmActivity;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;

public class Proshop_addevnt extends HttpServlet {
 

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************
 // Process the request from Proshop_events
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
   String omit = "";

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H1>Database Connection Error</H1>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_EVENT", out);
   }

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();

   String courseName = "";        // course names
   String posType = "";
   
   int multi = 0;               // multiple course support
   int index = 0;
   int tmp = 0;

   String club = (String)session.getAttribute("club");
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int sess_activity_id = (Integer)session.getAttribute("activity_id");
/*
   boolean USE_NEW_SIGNUP = false;          //  flag to control which clubs can access new feature

   if (club.startsWith("demo") ||
       club.equals("ridgeclub")
      ) USE_NEW_SIGNUP = true;
*/

   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);        // get the year
   int thisMonth = cal.get(Calendar.MONTH) +1;
   int thisDay = cal.get(Calendar.DAY_OF_MONTH);

   //
   // Get the 'Multiple Course' option from the club db
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi, posType " +
                             "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
         posType = rs.getString(2);
      }
      stmt.close();

      if (multi != 0) {           // if multiple courses supported for this club

         course = Utilities.getCourseNames(con);     // get all the course names
      }

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Build the HTML page to prompt user for the info
   //
   out.println(SystemUtils.HeadTitleEditor("Proshop - Add Event Main"));

      //out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/tiny_mce/tiny_mce.js\"></script>");
      //out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/assets/jquery/tiny_mce/tiny_mce.js\"></script>");
      out.println("<script type=\"text/javascript\">");
      //out.println("<!--");


      out.println("tinyMCE.init({");

        // General options
        out.println("relative_urls : false,");      // convert all URLs to absolute URLs
      //out.println("remove_script_host : false,"); // don't strip the protocol and host part of the URLs
        out.println("document_base_url : \"http://www1.foretees.com/\",");

        out.println("mode : \"textareas\",");
        out.println("theme : \"advanced\","); // simple
        out.println("plugins : \"safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager\",");

        // Theme options
        //out.println("theme_advanced_buttons1 : \"save,|,cut,copy,paste,pastetext,pasteword,|,search,replace,|,undo,redo,|,tablecontrols,|,removeformat,visualaid,|,charmap,insertdate,inserttime,emotions,hr,advhr,|,print,|,ltr,rtl,|,fullscreen,|,insertlayer,moveforward,movebackward,absolute,|,iespell,spellchecker\",");
        //out.println("theme_advanced_buttons2 : \"formatselect,fontselect,fontsizeselect,styleprops,|,bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote,|,sub,sup,|,link,unlink,anchor,image,insertimage,|,cleanup,code,preview\",");

        out.println("theme_advanced_buttons1 : \"cut,copy,paste,pastetext,pasteword,|,undo,redo,|,hr,advhr,|,link,unlink,anchor,image,insertimage,|,code\",");
      //out.println("theme_advanced_buttons2 : \"tablecontrols\",");
        out.println("theme_advanced_buttons2 : \"bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote\",");
        out.println("theme_advanced_buttons3 : \"formatselect,fontselect,fontsizeselect,styleprops\",");
      //out.println("theme_advanced_buttons1 : \"cut,copy,paste,pastetext,pasteword,|,undo,redo,|,tablecontrols,|,hr,advhr,|,link,unlink,anchor,image,insertimage,|,code\",");
      //out.println("theme_advanced_buttons2 : \"formatselect,fontselect,fontsizeselect,styleprops,|,bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote\",");
      //out.println("theme_advanced_buttons3 : \"\",");
        out.println("theme_advanced_buttons4 : \"\",");

        out.println("theme_advanced_fonts : \"Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Palatino Linotype=palatino linotype,palatino,book antiqua;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings,zapf dingbats\",");

        out.println("theme_advanced_toolbar_location : \"top\",");
        out.println("theme_advanced_toolbar_align : \"left\",");
        out.println("theme_advanced_resizing : true,");
        // out.println("theme_advanced_statusbar_location : \"bottom\",");      // we don't need to show the file location info

        // Example content CSS (should be your site CSS)
        // out.println("content_css : \"css/example.css\",");

        // Drop lists for link/image/media/template dialogs
        out.println("template_external_list_url : \"js/template_list.js\",");
        out.println("external_link_list_url : \"js/link_list.js\",");
        out.println("external_image_list_url : \"js/image_list.js\",");
        out.println("media_external_list_url : \"js/media_list.js\",");

        //out.println("}");

       out.println("});");

      out.println("function cursor(){document.forms['f'].event_name.focus();}");
/*
      out.println("var tinyMCEmode = true;");
      out.println("function toogleEditorMode(sEditorID) {");
      out.println(" try {");
      out.println("  if(tinyMCEmode) {");
      out.println("   tinyMCE.removeMCEControl(tinyMCE.getEditorId('itin'));");
      out.println("   tinyMCEmode = false;");
      out.println("  } else {");
      out.println("   tinyMCE.addMCEControl(document.getElementById('itin'), 'itin');");
      out.println("   tinyMCEmode = true;");
      out.println("  }");
      out.println(" } catch(err) {");
      out.println("  alert('Error: ' + err.description);");
      out.println(" }");
      out.println("}");
*/
      //out.println("// -->");
      out.println("</script>");
   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, sans-serif\">");
   //out.println("<center>");
   out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Special Events</b><br>");
   out.println("</font><font color=\"#FFFFFF\" size=\"2\">");
      out.println("Complete the following information for each event to be added.<br>");
      out.println("Click on <b>Continue</b> to add the event.");
   out.println("</font></td></tr></table><br>");
   out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" align=\"center\">");
      out.println("<form action=\"Proshop_addevnt\" method=\"post\" target=\"bot\" name=\"f\" id=\"f\">");
      out.println("<tr>");
      out.println("<td width=\"530\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\"><nobr>Event Name (Use A-Z, a-z, 0-9 and spaces <b>only</b>):&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"event_name\" size=\"30\" maxlength=\"30\"></nobr>");
            out.println("<br>&nbsp;&nbsp;&nbsp;* Must be unique");
         
         if (sess_activity_id == 0) {

           out.println("<br><br>");
           out.println("Season Long Event?&nbsp;&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"season\" onchange=\"toggleSeason(this.selectedIndex)\">");
             out.println("<option value=\"0\">No</option>");
             out.println("<option value=\"1\">Yes</option>");
           out.println("</select>");

         } else {

            out.println("<input type=hidden name=season value='0'>");
            out.println("<input type=hidden name=sess_activity_id value='" + sess_activity_id + "'>");
/*
            out.println("&nbsp;&nbsp;Select an Activity:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"activity_id\">");

            // display the currently selected activity
            //if (default_activity_id != activity_id) {
            //    Common_Config.buildOption(default_activity_id, getActivity.getActivityName(default_activity_id, con), activity_id, out);
            //}

            // also display all the child activities of the currently selected activity
            ArrayList<Integer> result = new ArrayList<Integer>();
            try { result = getActivity.getAllChildrenForActivity(sess_activity_id, con); }
            catch (Exception exc) { out.println("ERR=" + exc.toString()); }

            for (int i = 0; i < result.size(); i++) {
               try { Common_Config.buildOption(result.get(i), getActivity.getActivityName(result.get(i), con), 0, out); }
               catch (Exception exc) {}
            }

            out.println("</select>");
*/
         }
         
         out.println("<br><br>");
         
         //
         //  If multiple courses, then add a drop-down box for course names
         //
         if (sess_activity_id == 0 && multi != 0) {           // if multiple courses supported for this club

            out.println("&nbsp;&nbsp;Course:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\">");
            out.println("<option selected value=\"-ALL-\">ALL</option>");

            for (index=0; index<course.size(); index++) {

               courseName = course.get(index);                    // get course name from array
               out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
            }
            out.println("</select>");
            out.println("<br><br>");

         } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
         }
         
        out.println("<div id=\"season_data\" style=\"height: auto; width: auto\">");
        if (sess_activity_id == 0) {
         out.println("&nbsp;&nbsp;Tees:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"fb\">");
             out.println("<option value=\"Both\">Both</option>");
             out.println("<option value=\"Front\">Front</option>");
             out.println("<option value=\"Back\">Back</option>");
           out.println("</select>");
         out.println("<br><br>"); 
        } else {
            out.println("<input type=hidden name=fb value=''>");
        }
         out.println("Date of Event:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("Month:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"month\">");
              dateOpts.opMonth(out, thisMonth);         // output the month options
           out.println("</select>");
             
           out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"day\">");
              dateOpts.opDay(out, thisDay);         // output the day options
           out.println("</select>");
             
           out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"year\">");
              dateOpts.opYear(out, thisYear);         // output the year options
           out.println("</select>");
             
         out.println("<br><br>");
         
         
         if (sess_activity_id != 0) {
             
            // display a series of checkboxes for each court (sub-activity w/ time sheets) that this event will take place on

            out.println("Choose the locations for this event:&nbsp;&nbsp;");
            
            Common_Config.displayActivitySheetSelect("", sess_activity_id, false, con, out);
         }
         

         out.println("<br><br>");
         out.println("Time to Start Blocking " + ((sess_activity_id == 0) ? "Tees" : "Times") + ":&nbsp;&nbsp;&nbsp; hr &nbsp;");
           out.println("<select size=\"1\" name=\"start_hr\">");
           for (tmp=1; tmp <= 12; tmp++) {
               Common_Config.buildOption(tmp, Utilities.ensureDoubleDigit(tmp), 0, out);
           }
           out.println("</select>");
           out.println("&nbsp; min &nbsp;");
           out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"start_min\">");
           out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"start_ampm\">");
             out.println("<option value=\"00\">AM</option>");
             out.println("<option value=\"12\">PM</option>");
           out.println("</select>");
         out.println("<br><br>");
         
         out.println("Time to Stop Blocking " + ((sess_activity_id == 0) ? "Tees" : "Times") + ":&nbsp;&nbsp;&nbsp; hr &nbsp;");
           out.println("<select size=\"1\" name=\"end_hr\">");
           for (tmp=1; tmp <= 12; tmp++) {
               Common_Config.buildOption(tmp, Utilities.ensureDoubleDigit(tmp), 0, out);
           }
           out.println("</select>");                         
           out.println("&nbsp; min &nbsp;");
           out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"end_min\">");
           out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"end_ampm\">");
             out.println("<option value=\"00\">AM</option>");
             out.println("<option value=\"12\">PM</option>");
           out.println("</select>");
         out.println("<br><br>");
         
         out.println("Actual Start Time of Event:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;");
           out.println("<select size=\"1\" name=\"act_hr\">");
           for (tmp=1; tmp <= 12; tmp++) {
               Common_Config.buildOption(tmp, Utilities.ensureDoubleDigit(tmp), 0, out);
           }
           out.println("</select>");
           out.println("&nbsp; min &nbsp;");
           out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"act_min\">");
           out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"act_ampm\">");
             out.println("<option value=\"00\">AM</option>");
             out.println("<option value=\"12\">PM</option>");
           out.println("</select>");
         out.println("<br><br>");

         out.println("<hr width=\"400\"><br>");             // separate with bar

         if (sess_activity_id == 0) {

               out.println("Optional 2nd Set of Tee Times to Block (e.g. for cross-overs)<br><br>");
             out.println("&nbsp;&nbsp;Tees:&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"fb2\">");
                 out.println("<option value=\"Both\">Both</option>");
                 out.println("<option value=\"Front\">Front</option>");
                 out.println("<option value=\"Back\">Back</option>");
               out.println("</select>");
             out.println("<br><br>");
             out.println("Time to Start Blocking Tees:&nbsp;&nbsp;&nbsp; hr &nbsp;");
               out.println("<select size=\"1\" name=\"start_hr2\">");
               for (tmp=1; tmp <= 12; tmp++) {
                   Common_Config.buildOption(tmp, Utilities.ensureDoubleDigit(tmp), 0, out);
               }
               out.println("</select>");
               out.println("&nbsp; min &nbsp;");
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"start_min2\">");
               out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"start_ampm2\">");
                 out.println("<option value=\"00\">AM</option>");
                 out.println("<option value=\"12\">PM</option>");
               out.println("</select>");
             out.println("<br><br>");
             out.println("Time to Stop Blocking Tees:&nbsp;&nbsp;&nbsp; hr &nbsp;");
               out.println("<select size=\"1\" name=\"end_hr2\">");
               for (tmp=1; tmp <= 12; tmp++) {
                   Common_Config.buildOption(tmp, Utilities.ensureDoubleDigit(tmp), 0, out);
               }
               out.println("</select>");
               out.println("&nbsp; min &nbsp;");
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"end_min2\">");
               out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"end_ampm2\">");
                 out.println("<option value=\"00\">AM</option>");
                 out.println("<option value=\"12\">PM</option>");
               out.println("</select>");
             out.println("<br><br><hr width=\"400\"><br>");

               out.println("Shotgun Event?&nbsp;&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"type\">");
                 out.println("<option value=\"00\">No</option>");
                 out.println("<option value=\"01\">Yes</option>");
               out.println("</select>");
             out.println("<br><br>");

         } else {
            // pass these to prevent null errors
            out.println("<input type=hidden name=start_hr2 value='0'>");
            out.println("<input type=hidden name=start_min2 value='0'>");
            out.println("<input type=hidden name=start_ampm2 value='00'>");
            out.println("<input type=hidden name=end_hr2 value='0'>");
            out.println("<input type=hidden name=end_min2 value='0'>");
            out.println("<input type=hidden name=end_ampm2 value='00'>");
            out.println("<input type=hidden name=fb2 value=''>");
            out.println("<input type=hidden name=type value='00'>");
         }
         out.println("</div>");
         
         if (sess_activity_id == 0) {
           out.println("Number of holes to be played:&nbsp;&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"holes\">");
             out.println("<option value=\"09\">9</option>");
             out.println("<option value=\"18\" selected>18</option>");
             out.println("<option value=\"27\">27</option>");
             out.println("<option value=\"36\">36</option>");
             out.println("<option value=\"45\">45</option>");
             out.println("<option value=\"54\">54</option>");
             out.println("<option value=\"72\">72</option>");
           out.println("</select>");
           out.println("<br><br>");
         } else {
            out.println("<input type=hidden name=holes value=0>");
         }
         
         out.println("Guest Only Event (Outside Event)?&nbsp;&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"gstOnly\">");
             out.println("<option value=\"No\">No</option>");
             out.println("<option value=\"Yes\">Yes</option>");
           out.println("</select>");
         out.println("<br><br>");
                      
         if (sess_activity_id == 0) {
             out.println("If not, can members access the event times after <BR>they have been moved to the tee sheet?&nbsp;&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"memedit\">");
             out.println("<option value=\"No\">No</option>");
             out.println("<option value=\"Yes\">Yes</option>");
             out.println("</select>");
             out.println("<br><br>");
         } else {
            out.println("<input type=hidden name=memedit value=\"No\">");
         }
           
           
         out.println("Gender Based Event?&nbsp;&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"gender\">");
             //out.println("<option value=-1>Choose...</option>");
             out.println("<option value=\"1\">" + Labels.gender_opts[1] + "</option>");
             out.println("<option value=\"2\">" + Labels.gender_opts[2] + "</option>");
             out.println("<option value=\"3\">" + Labels.gender_opts[3] + "</option>");
         out.println("</select>");
         out.println("<br><br>");
           
         out.println("Event Categories? (Used to filter list on Event Sign Up page)<br><br>");
           
         Common_Config.buildEventCategoryOptions(sess_activity_id, 0, out, con);
           
         out.println("<br><br>");
           
         if (sess_activity_id == 0) {     // if Golf
             
             if (posType.equals( "Abacus21 Direct" )) {      // allow POS codes for events?
              
                 out.println("If this is a member event and you wish to send charges to the POS when players are<br>"
                           + "checked in on the tee sheet, specify the POS info here:<br>");
                 out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"8\">");
                 out.println("<tr><td width=\"20\">&nbsp;</td><td align=\"right\">");
                 out.println("Item Code for Members: </td><td align=\"left\"> <input type=text name=member_item_code size=20 maxlength=30></td></tr>");
                 out.println("<tr><td width=\"20\">&nbsp;</td><td align=\"right\">");
                 out.println("Event Fee for Members (No $): </td><td align=\"left\"><input type=text name=member_fee size=8 maxlength=8></td></tr>");
                 out.println("<tr><td width=\"20\">&nbsp;</td><td align=\"right\">");                
                 out.println("Item Code for Guests: </td><td align=\"left\"><input type=text name=guest_item_code size=20 maxlength=30></td></tr>");
                 out.println("<tr><td width=\"20\">&nbsp;</td><td align=\"right\">");
                 out.println("Event Fee for Guests (No $): </td><td align=\"left\"><input type=text name=guest_fee size=8 maxlength=8></td></tr>");                             
                 out.println("</table>");
                 out.println("<br><br>");
             }
         }
                  
           
         out.println("Color to make this event on the " + (sess_activity_id == 0 ? "tee" : "time") + " sheet:&nbsp;&nbsp;");
           
         Common_Config.displayColors(out);       // output the color options
           
           out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
           out.println("<br><br>");
           out.println("Itinerary: (Optional explaination of event - double quotes not allowed)<br>");

         //out.println("<a href=\"javascript:void(0)\" id=\"toggle\" onclick=\"toogleEditorMode('itin');document.getElementById('toggle').innerHTML=(document.getElementById('toggle').innerHTML == 'Hide TinyMCE') ? 'Show TinyMCE' : 'Hide TinyMCE';\">Hide TinyMCE</a><br>");

           out.println("<textarea name=\"itin\" id=\"itin\" cols=\"60\" rows=\"10\"></textarea>");
           //out.println("<br>Optional. Double quotes not allowed.");
           out.println("<br><br>");
           out.println("Allow Online Sign-ups?&nbsp;&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"signUp\" onchange=\"toggleExtraQ(this.selectedIndex)\">");
             out.println("<option value=\"No\">No</option>");
             out.println("<option value=\"Yes\">Yes</option>");
           out.println("</select>");
           out.println("<br>(Multiple day events - select 'No' if not 1st day. Guest only event - select 'No'.)");
           out.println("<br><br>");
           
           //
           // GUESTS ONLY - home club, phone, address, email
           // ALL PLAYERS - hdcp#, gender, shirt size, shoe size, other1, other2, other3
           //
           
         out.println("<div id=\"extra_info\" style=\"height: auto; width: auto\">");
        
           out.println("<table>");
            out.println("<tr align=center><td></td><td><font size=2><b>A</b></td><td><font size=2><b>R</b></td><td align=left><font size=2>&nbsp;&nbsp;(A = Ask Only,&nbsp; R = Require, * = Guest Questions)</font></td></tr>");
            out.println("<tr><td>&nbsp;</td><td><!-- <input type=checkbox name=ask_names value=1 onchange=\"updateCheckbox(this.name, 'req_guestname')\"> --></td><td><input type=checkbox name=req_guestname value=1></td><td><font size=2>Force Guest Names to be included? (If guests allowed)</td></tr>");
            out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_hdcp value=1 onchange=\"updateCheckbox(this.name, 'req_hdcp')\"></td><td><input type=checkbox name=req_hdcp value=1></td><td><font size=2>Include HDCP Numbers? *</td></tr>");
            out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_gender value=1 onchange=\"updateCheckbox(this.name, 'req_gender')\"></td><td><input type=checkbox name=req_gender value=1></td><td><font size=2>Include Gender?</td></tr>");
            out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_homeclub value=1 onchange=\"updateCheckbox(this.name, 'req_homeclub')\"></td><td><input type=checkbox name=req_homeclub value=1></td><td><font size=2>Include Home Club, ST? *</td></tr>");
            out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_phone value=1 onchange=\"updateCheckbox(this.name, 'req_phone')\"></td><td><input type=checkbox name=req_phone value=1></td><td><font size=2>Include Phone Number? *</td></tr>");
            out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_email value=1 onchange=\"updateCheckbox(this.name, 'req_email')\"></td><td><input type=checkbox name=req_email value=1></td><td><font size=2>Include Email Address? *</td></tr>");
            out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_address value=1 onchange=\"updateCheckbox(this.name, 'req_address')\"></td><td><input type=checkbox name=req_address value=1></td><td><font size=2>Include Mailing Address? *</td></tr>");
            out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_shirtsize value=1 onchange=\"updateCheckbox(this.name, 'req_shirtsize')\"></td><td><input type=checkbox name=req_shirtsize value=1></td><td><font size=2>Include Shirt Size?</td></tr>");
            out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_shoesize value=1 onchange=\"updateCheckbox(this.name, 'req_shoesize')\"></td><td><input type=checkbox name=req_shoesize value=1></td><td><font size=2>Include Shoe Size?</td></tr>");
            if (club.equals("interlachen")) {
               out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_custom1 value=1 onchange=\"updateCheckbox(this.name, 'req_custom1')\"></td><td><input type=checkbox name=req_custom1 value=1></td><td><font size=2>Include Tee Selection?</td></tr>");
            }
            out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_otherQ1 value=1 onchange=\"updateCheckbox(this.name, 'req_otherQ1')\"></td><td><input type=checkbox name=req_otherQ1 value=1></td><td><font size=2>Include Custom Question #1?&nbsp; &nbsp;<input type=text name=otherQ1 size=40 maxlength=64></td></tr>");
            out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_otherQ2 value=1 onchange=\"updateCheckbox(this.name, 'req_otherQ2')\"></td><td><input type=checkbox name=req_otherQ2 value=1></td><td><font size=2>Include Custom Question #2?&nbsp; &nbsp;<input type=text name=otherQ2 size=40 maxlength=64></td></tr>");
            out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_otherQ3 value=1 onchange=\"updateCheckbox(this.name, 'req_otherQ3')\"></td><td><input type=checkbox name=req_otherQ3 value=1></td><td><font size=2>Include Custom Question #3?&nbsp; &nbsp;<input type=text name=otherQ3 size=40 maxlength=64></td></tr>");
           out.println("</table>");
           
           out.println("<br><br>");
           
         out.println("</div>");
        
         
          out.println("If you wish to receive an email notification of all signups/modifications/cancelations<br>");
          out.println("provide up to two email address in the boxes below.<br><br>");
           out.println("&nbsp;&nbsp;Email #1: <input type=text name=email1 size=40 maxlength=50><br><br>");
           out.println("&nbsp;&nbsp;Email #2: <input type=text name=email2 size=40 maxlength=50>");
           
          out.println("<br><br>");

        if (sess_activity_id == 0) {
           //out.println("If you wish to export this event to an external program, select it from the list below:");
           out.println("Export Type:&nbsp;&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"export_type\">");
             out.println("<option value=\"0\">None</option>");
             out.println("<option value=\"1\">TPP</option>");
             out.println("<option value=\"2\">Event-Man</option>");
             out.println("<option value=\"3\">GolfNet TMS</option>");
             out.println("<option value=\"4\">TourEx</option>");
             out.println("<option value=\"5\">Golf Genius</option>");
           out.println("</select>");
        } else {
            out.println("<input type=hidden name=export_type value=0>");
        }
           out.println("<br><br>");
        
         out.println("<p align=\"center\">");
           out.println("<input type=\"submit\" value=\"Continue\" name=\"btnSubmit\">");
         out.println("</p>");
         out.println("</font>");
      out.println("</td>");
   out.println("</tr>");
   out.println("</form>");
   out.println("</table>");
   out.println("<font size=\"2\"><br>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\"><b>Note:</b>&nbsp;&nbsp;Adding an event will block all members from the " + ((sess_activity_id == 0) ? "tee times" : "time sheets") + " within the timeframe specified here.<br>");
       out.println("Only proshop personnel will be allowed to update these " + ((sess_activity_id == 0) ? "tee times" : "times") + ".<br>");
       out.println("If the event spans more than 1 day, add 1 event per day and use unique names <br>");
       if (sess_activity_id == 0) {
           out.println("(i.e. 'Couples Best Ball Day 1' and 'Couples Best Ball Day 2').");
       } else {
           out.println("(i.e. 'Couples Tournament Day 1' and 'Couples Tournament Day 2').");
       }
   out.println("</font></td></tr></table><br>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_events\">");
   out.println("<div style=\"margin:auto; text-align:center\"><input type=\"submit\" value=\"Done\" style=\"text-decoration:underline; background:#8B8970; width:75px;\"></div>");
   out.println("</form><br><br></font>");
   out.println("</font>");
   
    out.println("<script type=\"text/javascript\">");
    out.println("<!--");
    out.println("function toggleSeason(id) {");
    out.println(" var b = (id == 0);");
    out.println(" document.getElementById('season_data').style.display = ((b) ? 'block' : 'none');");
    out.println(" document.getElementById('season_data').style.visibility = ((b) ? 'visible' : 'hidden');");
    out.println(" document.getElementById('season_data').style.height = ((b) ? 'auto' : '0px');");
    out.println("}");
    
    out.println("function updateCheckbox(ask, req) {");
    out.println(" var a = eval(\"document.forms['f'].\" + ask + \".checked;\");");
    out.println(" if (a==false) {");
    out.println("  eval(\"document.forms['f'].\" + req + \".checked = false;\");");
    out.println("  eval(\"document.forms['f'].\" + req + \".disabled = true;\");");
    out.println(" } else {");
    out.println("  eval(\"document.forms['f'].\" + req + \".disabled = false;\");");
    out.println(" }");
    out.println("}");
    
    out.println("function toggleExtraQ(id) {");
    out.println(" var showExtra = (id == 1);");
    out.println(" document.getElementById('extra_info').style.display = ((showExtra) ? 'block' : 'none');");
    out.println(" document.getElementById('extra_info').style.visibility = ((showExtra) ? 'visible' : 'hidden');");
    out.println(" document.getElementById('extra_info').style.height = ((showExtra) ? 'auto' : '0px');");
    out.println(" document.forms['f'].btnSubmit.value=\"Finish\";");
    out.println("}");

    if (sess_activity_id == 0) {
        out.println("toggleSeason(document.forms['f'].season.selectedIndex);");
    } else {
        out.println("toggleSeason(0);"); // force to the hidden div
    }

    out.println("toggleExtraQ(document.forms['f'].signUp.selectedIndex);");
    
        out.println("updateCheckbox('ask_hdcp', 'req_hdcp');");
        out.println("updateCheckbox('ask_homeclub', 'req_homeclub');");
        out.println("updateCheckbox('ask_gender', 'req_gender');");
        out.println("updateCheckbox('ask_phone', 'req_phone');");
        out.println("updateCheckbox('ask_email', 'req_email');");
        out.println("updateCheckbox('ask_address', 'req_address');");
        out.println("updateCheckbox('ask_shirtsize', 'req_shirtsize');");
        out.println("updateCheckbox('ask_shoesize', 'req_shoesize');");
        if (club.equals("interlachen")) {
           out.println("updateCheckbox('ask_custom1', 'req_custom1');");
        }
        out.println("updateCheckbox('ask_otherQ1', 'req_otherQ1');");
        out.println("updateCheckbox('ask_otherQ2', 'req_otherQ2');");
        out.println("updateCheckbox('ask_otherQ3', 'req_otherQ3');");
    
    out.println(" // -->");
    out.println("</script>");
   
   out.println("</body>");
   out.println("</html>");
   out.close();
   
 }     // end of doGet processing


 //****************************************************
 // Process the form request from doGet above
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   PreparedStatement pstmt = null; 
   Statement stmtc = null;
   ResultSet rs = null;
   String omit = "";

   HttpSession session = SystemUtils.verifyPro(req, out);   // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_EVENT", out);
   }

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String format = "";
   String pairings = "";
   String ssize = "";
   String sminsize = "";
   String smax = "";
   String sguests = "";
   String memcost = "";
   String gstcost = "";
   String ssu_month = "";
   String ssu_day = "";
   String ssu_year = "";
   String ssu_hr = "";
   String ssu_min = "";
   String su_ampm = "";
   String sc_month = "";
   String sc_day = "";
   String sc_year = "";
   String sc_hr = "";
   String sc_min = "";
   String c_ampm = "";
   String posType = "";
   String sx = "";
   String sxhrs = "";
   String mempos = "";
   String gstpos = "";
   String name = "";
   String copyname = "";
   String member_item_code = "";
   String guest_item_code = "";
   String temp = "";

   String [] tmode = new String [16];                       // Modes of trans 

   int [] tmodei = new int [16];                            // Modes of trans indicators

   int i = 0;
   int i2 = 0;
   int month = 0;
   int day = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;
   int s_hr2 = 0;
   int s_min2 = 0;
   int s_ampm2 = 0;
   int e_hr2 = 0;
   int e_min2 = 0;
   int e_ampm2 = 0;
   int a_hr = 0;
   int a_min = 0;
   int a_ampm = 0;
   int year = 0;
   int type = 0;
   int signUp = 0;
   int size = 0;
   int minsize = 0;
   int max = 0;
   int guests = 0;
   int holes = 0;
   int x = 0;
   int xhrs = 0;
   int gstOnly = 0;
   int gender = 0;
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   int c_hr = 0;
   int c_min = 0;
   int c_time = 0;
   int su_month = 0;
   int su_day = 0;
   int su_year = 0;
   int su_hr = 0;
   int su_min = 0;
   int su_time = 0;
   //int itinL = 0;
   int export_type = 0;
   int season = 0;
   
   long c_date = 0;
   long su_date = 0;
   int location_count = 0;
   //int activity_id = 0;
   
   double member_fee = 0;
   double member_tax = 0;
   double guest_fee = 0;
   double guest_tax = 0;

   boolean copy = false;
   
   ArrayList<Integer> category_ids = new ArrayList<Integer>();  // ArrayList to hold all category_ids for this event

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   String [] mship = new String [parm.MAX_Mems+1];            // Mem Types
   String [] mtype = new String [parm.MAX_Mships+1];          // Mship Types

   if (req.getParameter("copy") != null) {

      copy = true;            // this is a copy request (from doCopy in _events)
   }
   if (req.getParameter("copyname") != null) {

      copyname = req.getParameter("copyname").trim();      //  name of event being copied
   }

   //
   // Get all the parameters entered
   //
   if (req.getParameter("event_name") != null) {

      name = req.getParameter("event_name").trim();      //  name of event being copied

      name = Utilities.trimDoubleSpaces(name);
   }
   //String name = req.getParameter("event_name");           //  new event name


   String course = req.getParameter("course");             //  course name
   String fb = req.getParameter("fb");                     //  Front/Back/Both tees
   String s_month = req.getParameter("month");             //  month (00 - 12)
   String s_day = req.getParameter("day");                 //  day (01 - 31)
   String s_year = req.getParameter("year");               //  year (2004 - 20xx)
   String start_hr = req.getParameter("start_hr");         //  start hour (01 - 12)
   String start_min = req.getParameter("start_min");       //  start min (00 - 59)
   String start_ampm = req.getParameter("start_ampm");     //  AM/PM (00 or 12)
   String end_hr = req.getParameter("end_hr");             //   .
   String end_min = req.getParameter("end_min");           //   .
   String end_ampm = req.getParameter("end_ampm");         //   .
   String act_hr = req.getParameter("act_hr");             //   .
   String act_min = req.getParameter("act_min");           //   .
   String act_ampm = req.getParameter("act_ampm");         //   .
   String fb2 = req.getParameter("fb2");                   //  Front/Back/Both tees #2
   String start_hr2 = req.getParameter("start_hr2");       //  start hour (01 - 12)
   String start_min2 = req.getParameter("start_min2");     //  start min (00 - 59)
   String start_ampm2 = req.getParameter("start_ampm2");   //  AM/PM (00 or 12)
   String end_hr2 = req.getParameter("end_hr2");           //   .
   String end_min2 = req.getParameter("end_min2");         //   .
   String end_ampm2 = req.getParameter("end_ampm2");       //   .
   String stype = req.getParameter("type");                //  type of event (shotgun?)
   String sholes = req.getParameter("holes");              //  number of holes to play
   String color = req.getParameter("color");               //  color for the event display
   String ssignUp = req.getParameter("signUp");            //  allow members to sign up online
   String guestOnly = req.getParameter("gstOnly");
   String sgender = req.getParameter("gender");
   String itin = req.getParameter("itin").trim();
   String sseason = req.getParameter("season");
   String email1 = req.getParameter("email1").trim();
   String email2 = req.getParameter("email2").trim();
   String sexport_type = req.getParameter("export_type");
   
   //
   //  Get POS parms, if provided
   //
   if (req.getParameter("member_item_code") != null) member_item_code = req.getParameter("member_item_code");
   if (req.getParameter("guest_item_code") != null) guest_item_code = req.getParameter("guest_item_code");
   
   int ask_hdcp = (req.getParameter("ask_hdcp") == null) ? 0 : 1;
   int ask_homeclub = (req.getParameter("ask_homeclub") == null) ? 0 : 1;
   int ask_gender = (req.getParameter("ask_gender") == null) ? 0 : 1;
   int ask_phone = (req.getParameter("ask_phone") == null) ? 0 : 1;
   int ask_email = (req.getParameter("ask_email") == null) ? 0 : 1;
   int ask_address = (req.getParameter("ask_address") == null) ? 0 : 1;
   int ask_shirtsize = (req.getParameter("ask_shirtsize") == null) ? 0 : 1;
   int ask_shoesize = (req.getParameter("ask_shoesize") == null) ? 0 : 1;
   int ask_custom1 = (req.getParameter("ask_custom1") == null) ? 0 : 1;
   int ask_otherQ1 = (req.getParameter("ask_otherQ1") == null) ? 0 : 1;
   int ask_otherQ2 = (req.getParameter("ask_otherQ2") == null) ? 0 : 1;
   int ask_otherQ3 = (req.getParameter("ask_otherQ3") == null) ? 0 : 1;
   
   int req_guestname = (req.getParameter("req_guestname") == null) ? 0 : 1;
   int req_hdcp = (req.getParameter("req_hdcp") == null) ? 0 : 1;
   int req_homeclub = (req.getParameter("req_homeclub") == null) ? 0 : 1;
   int req_gender = (req.getParameter("req_gender") == null) ? 0 : 1;
   int req_phone = (req.getParameter("req_phone") == null) ? 0 : 1;
   int req_email = (req.getParameter("req_email") == null) ? 0 : 1;
   int req_address = (req.getParameter("req_address") == null) ? 0 : 1;
   int req_shirtsize = (req.getParameter("req_shirtsize") == null) ? 0 : 1;
   int req_shoesize = (req.getParameter("req_shoesize") == null) ? 0 : 1;
   int req_custom1 = (req.getParameter("req_custom1") == null) ? 0 : 1;
   int req_otherQ1 = (req.getParameter("req_otherQ1") == null) ? 0 : 1;
   int req_otherQ2 = (req.getParameter("req_otherQ2") == null) ? 0 : 1;
   int req_otherQ3 = (req.getParameter("req_otherQ3") == null) ? 0 : 1;
   
   String otherQ1 = (ask_otherQ1 == 0) ? "" : req.getParameter("otherQ1");
   String otherQ2 = (ask_otherQ2 == 0) ? "" : req.getParameter("otherQ2");
   String otherQ3 = (ask_otherQ3 == 0) ? "" : req.getParameter("otherQ3");
   
   String slocation_count = (req.getParameter("location_count") == null) ? "0" : req.getParameter("location_count");
   
   int memedit = (req.getParameter("memedit") != null && req.getParameter("memedit").equals("Yes")) ? 1 : 0;    // can member edit event times once on tee sheet ?
   
   //
   //  Get other POS parms, if provided
   //
   if (req.getParameter("member_fee") != null) {
       
       temp = req.getParameter("member_fee");
       
       if (temp != null && !temp.equals("")) {
       
            if (temp.startsWith( "$" )) {

                temp = temp.substring(1);      // strip it
            }

            member_fee = Double.parseDouble(temp);
       }
   }

   if (req.getParameter("guest_fee") != null) {
       
       temp = req.getParameter("guest_fee");
       
       if (temp != null && !temp.equals("")) {
       
            if (temp.startsWith( "$" )) {

                temp = temp.substring(1);      // strip it
            }

            guest_fee = Double.parseDouble(temp);
       }
   }
   
   
   // sanity check some of the options
   if (otherQ1 == null) otherQ1 = "";
   if (otherQ2 == null) otherQ2 = "";
   if (otherQ3 == null) otherQ3 = "";
   if (fb == null) fb = "";
   
   category_ids = Utilities.buildEventCategoryListFromReq(req, sess_activity_id, con);
   
   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);        // get the year
   int thisMonth = cal.get(Calendar.MONTH) +1;
   int thisDay = cal.get(Calendar.DAY_OF_MONTH);

   if (req.getParameter("page2") != null) {                // if member signup selected and 2nd page of options

      format = req.getParameter("format");
      pairings = req.getParameter("pairings");
      ssize = req.getParameter("size");
      sminsize = req.getParameter("minsize");
      smax = req.getParameter("max");
      sguests = req.getParameter("guests");
      sx = req.getParameter("x");
      sxhrs = req.getParameter("xhrs");
      memcost = req.getParameter("memcost");
      gstcost = req.getParameter("gstcost");
      ssu_month = req.getParameter("su_month");
      ssu_day = req.getParameter("su_day");
      ssu_year = req.getParameter("su_year");
      ssu_hr = req.getParameter("su_hr");
      ssu_min = req.getParameter("su_min");
      su_ampm = req.getParameter("su_ampm");
      sc_month = req.getParameter("c_month");
      sc_day = req.getParameter("c_day");
      sc_year = req.getParameter("c_year");
      sc_hr = req.getParameter("c_hr");
      sc_min = req.getParameter("c_min");
      c_ampm = req.getParameter("c_ampm");

      if (req.getParameter("mempos") != null) {   // if member POS charge code specified

         mempos = req.getParameter("mempos");
      }
      if (req.getParameter("gstpos") != null) {   // if guest POS charge code specified

         gstpos = req.getParameter("gstpos");
      }
      if (req.getParameter("tmode1") != null) {   

         tmodei[0] = 1;
      } else {
         tmodei[0] = 0;
      }
      if (req.getParameter("tmode2") != null) {

         tmodei[1] = 1;
      } else {
         tmodei[1] = 0;
      }
      if (req.getParameter("tmode3") != null) {

         tmodei[2] = 1;
      } else {
         tmodei[2] = 0;
      }
      if (req.getParameter("tmode4") != null) {

         tmodei[3] = 1;
      } else {
         tmodei[3] = 0;
      }
      if (req.getParameter("tmode5") != null) {

         tmodei[4] = 1;
      } else {
         tmodei[4] = 0;
      }
      if (req.getParameter("tmode6") != null) {

         tmodei[5] = 1;
      } else {
         tmodei[5] = 0;
      }
      if (req.getParameter("tmode7") != null) {

         tmodei[6] = 1;
      } else {
         tmodei[6] = 0;
      }
      if (req.getParameter("tmode8") != null) {

         tmodei[7] = 1;
      } else {
         tmodei[7] = 0;
      }
      if (req.getParameter("tmode9") != null) {

         tmodei[8] = 1;
      } else {
         tmodei[8] = 0;
      }
      if (req.getParameter("tmode10") != null) {

         tmodei[9] = 1;
      } else {
         tmodei[9] = 0;
      }
      if (req.getParameter("tmode11") != null) {

         tmodei[10] = 1;
      } else {
         tmodei[10] = 0;
      }
      if (req.getParameter("tmode12") != null) {

         tmodei[11] = 1;
      } else {
         tmodei[11] = 0;
      }
      if (req.getParameter("tmode13") != null) {

         tmodei[12] = 1;
      } else {
         tmodei[12] = 0;
      }
      if (req.getParameter("tmode14") != null) {

         tmodei[13] = 1;
      } else {
         tmodei[13] = 0;
      }
      if (req.getParameter("tmode15") != null) {

         tmodei[14] = 1;
      } else {
         tmodei[14] = 0;
      }
      if (req.getParameter("tmode16") != null) {

         tmodei[15] = 1;
      } else {
         tmodei[15] = 0;
      }
        
      for (i=1; i<parm.MAX_Mems+1; i++) {
         mtype[i] = "";
         if (req.getParameter("mem" +i) != null) {

            mtype[i] = req.getParameter("mem" +i);
         }
      }

      for (i=1; i<parm.MAX_Mships+1; i++) {
         mship[i] = "";
         if (req.getParameter("mship" +i) != null) {

            mship[i] = req.getParameter("mship" +i);
         }
      }

      i = 0;
   }

   //
   // Convert the numeric string parameters to Int's
   //
   try {
      month = Integer.parseInt(s_month);
      day = Integer.parseInt(s_day);
      year = Integer.parseInt(s_year);
      s_hr = Integer.parseInt(start_hr);
      s_min = Integer.parseInt(start_min);
      s_ampm = Integer.parseInt(start_ampm);
      e_hr = Integer.parseInt(end_hr);
      e_min = Integer.parseInt(end_min);
      e_ampm = Integer.parseInt(end_ampm);
      s_hr2 = Integer.parseInt(start_hr2);
      s_min2 = Integer.parseInt(start_min2);
      s_ampm2 = Integer.parseInt(start_ampm2);
      e_hr2 = Integer.parseInt(end_hr2);
      e_min2 = Integer.parseInt(end_min2);
      e_ampm2 = Integer.parseInt(end_ampm2);
      a_hr = Integer.parseInt(act_hr);
      a_min = Integer.parseInt(act_min);
      a_ampm = Integer.parseInt(act_ampm);
      type = Integer.parseInt(stype);
      holes = Integer.parseInt(sholes);
      export_type = Integer.parseInt(sexport_type);
      location_count = Integer.parseInt(slocation_count);

      if (ssignUp.equals( "Yes" )) {

         signUp = 1;
      }
      if (!ssize.equals( "" )) {

         size = Integer.parseInt(ssize);
      }
      if (!sminsize.equals( "" )) {

         minsize = Integer.parseInt(sminsize);
      }
      if (!smax.equals( "" )) {

         max = Integer.parseInt(smax);
      }
      if (!sguests.equals( "" )) {

         guests = Integer.parseInt(sguests);
      }
      if (!sx.equals( "" )) {

         x = Integer.parseInt(sx);
      }
      if (!sxhrs.equals( "" )) {

         xhrs = Integer.parseInt(sxhrs);
      }
      if (!ssu_month.equals( "" )) {

         su_month = Integer.parseInt(ssu_month);
      }
      if (!ssu_day.equals( "" )) {

         su_day = Integer.parseInt(ssu_day);
      }
      if (!ssu_year.equals( "" )) {

         su_year = Integer.parseInt(ssu_year);
      }
      if (!ssu_hr.equals( "" )) {

         su_hr = Integer.parseInt(ssu_hr);
      }
      if (!ssu_min.equals( "" )) {

         su_min = Integer.parseInt(ssu_min);
      }
      if (!sc_month.equals( "" )) {

         c_month = Integer.parseInt(sc_month);
      }
      if (!sc_day.equals( "" )) {

         c_day = Integer.parseInt(sc_day);
      }
      if (!sc_year.equals( "" )) {

         c_year = Integer.parseInt(sc_year);
      }
      if (!sc_hr.equals( "" )) {

         c_hr = Integer.parseInt(sc_hr);
      }
      if (!sc_min.equals( "" )) {

         c_min = Integer.parseInt(sc_min);
      }
      if (!sgender.equals( "" )) {

         gender = Integer.parseInt(sgender);
      }
      
      if (sseason != null && sseason.equals( "1" )) { season = 1; }
      
   }
   catch (NumberFormatException e) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Data Input Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to process one or more of the parameters entered.");
      out.println("<BR>Please go back and make sure all values are correct.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR>Exception:   " + e.getMessage());
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }
     
   if (guestOnly.equals( "Yes" )) {

      gstOnly = 1;
      memedit = 0;        // members cannot edit event times since there won't be any
   }
   
   // skip if here for page two!

   String locations_csv = "";
   if (req.getParameter("page2") == null && sess_activity_id != 0) {
       
       //
       // Get all the checked activities (courts or locations) from the form
       // this is where the event will take place
       //
       ArrayList<Integer> locations = new ArrayList<Integer>();
       try {

           for (i = 0; i <= location_count; i++) {

               if (req.getParameter("actChkBox_" + i) != null) {

                   try {

                       //out.println("<!-- i=" + req.getParameter("actChkBox_" + i) + " -->");
                       locations.add(Integer.parseInt(req.getParameter("actChkBox_" + i)));

                   } catch (Exception ignore) { }

               }

           }

           for (i = 0; i < locations.size(); i++) {

               locations_csv += locations.get(i) + ",";
           }

           if (!locations_csv.equals("")) {
               
               locations_csv = locations_csv.substring(0, locations_csv.length() - 1);

           } else {

               out.println(SystemUtils.HeadTitle("Data Entry Error"));
               out.println("<BODY><CENTER>");
               out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
               out.println("<BR><BR>You must specify at least one location for this event.");
               out.println("<BR>Please try again.");
               out.println("<BR><BR>");
               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               return;

           }

       } catch (Exception exc) {

           out.println("<br>locations.size()=" + locations.size());
           out.println("<br>location_count=" + location_count);
           out.println("<br>locations_csv=" + locations_csv);
           out.println("<br>Err=" + exc.toString());
           return;

       }
   }


   //
   // Populate a parm block for this activity
   //
   parmActivity parmAct = new parmActivity();              // allocate a parm block
   parmAct.activity_id = sess_activity_id;                      // pass in the activity_id so we can determin which activity to load parms for

   // get the activity config
   try { getActivity.getParms(con, parmAct); }
   catch (Exception e1) { out.println("<BR><BR>" + e1.getMessage()); }

   
/*     
   //
   //  Get the length of Itin (max length of 255 chars)
   //
   if (!itin.equals( "" )) {

      itinL = itin.length();       // get length of itin
   }
*/
   //
   //  verify the required fields (name reqr'ed)
   //
   if (name.equals( omit )) {

      invData(out);    // inform the user and return
      return;
   }

   //
   //  Event names cannot include special chars 
   //
   boolean error = SystemUtils.scanName(name);           // check for special characters

   if (error == true) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Special characters cannot be part of the Event Name.");
      out.println("<BR>You can only use the characters A-Z, a-z, 0-9 and space.");
      out.println("<BR>You entered:" + name);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }
   
   //
   //  Validate minute values
   //
   if ((s_min < 0) || (s_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Start Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + s_min);
      out.println("<BR>Please try again.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   if ((e_min < 0) || (e_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>End Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + e_min);
      out.println("<BR>Please try again.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   if ((a_min < 0) || (a_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Actual Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + e_min);
      out.println("<BR>Please try again.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   if (sess_activity_id == 0 && holes < 9) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>The Number of Holes for the event must be at least 9.");
      out.println("<BR>You entered:" + holes);
      out.println("<BR>Please try again.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  adjust some values for the table
   //
   long date = year * 10000;      // create event date
   date = date + (month * 100);
   date = date + day;             // date = yyyymmdd (for comparisons)

   if (s_hr != 12) {              // _hr specified as 01 - 12 (_ampm = 00 or 12)
      s_hr = s_hr + s_ampm;       // convert to military time (12 is always Noon or PM)
   }
   if (e_hr != 12) {              // ditto
      e_hr = e_hr + e_ampm;
   }
   if (a_hr != 12) {              // ditto
      a_hr = a_hr + a_ampm;
   }

   int stime = s_hr * 100;
   stime = stime + s_min;
   int etime = e_hr * 100;
   etime = etime + e_min;
   int atime = a_hr * 100;
   atime = atime + a_min;

   if (s_hr2 != 12) {              // _hr specified as 01 - 12 (_ampm = 00 or 12)
      s_hr2 = s_hr2 + s_ampm2;       // convert to military time (12 is always Noon or PM)
   }
   if (e_hr2 != 12) {              // ditto
      e_hr2 = e_hr2 + e_ampm2;
   }

   int stime2 = s_hr2 * 100;
   stime2 = stime2 + s_min2;
   int etime2 = e_hr2 * 100;
   etime2 = etime2 + e_min2;

   // if not a season long event, then verify the dates & times passed
   if (season == 0) {

       //
       //  verify that the start time is less than the end time
       //
       if (stime >= etime) {

          invData(out);    // inform the user and return
          return;
       }

       /*     Not wanted - allow pro to define an event that does not actually block any times!
       //
       //  verify that the actual start time is greater than start time and  less than the end time
       //
       if ((atime < stime) || (atime >= etime)) {

          invData(out);    // inform the user and return
          return;
       }
        */
       

       //
       //  verify that the 2nd start time is less than the 2nd end time (if specified - this is optional)
       //
       if (stime2 != etime2) {

          if (stime2 > etime2) {

             out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
             out.println("<BODY><CENTER><BR>");
             out.println("<p>&nbsp;</p>");
             out.println("<BR><H3>Input Error</H3><BR>");
             out.println("<BR><BR>Sorry, the times specified for the 2nd Set of Blockers are invalid.<BR>");
             out.println("The start time cannot be greater than the end time.<BR>");
             out.println("<BR>Please try again.<BR>");
             out.println("<BR><BR>");
             out.println("<font size=\"2\">");
             out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
             out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
             out.println("</form></font>");
             out.println("</CENTER></BODY></HTML>");
             out.close();
             return;
          }
       }
       
   } else {    
   
       // if this is a season long event, then subtract 1 day from the date which defaults to today (the date event was created)
       // so that the event does not appear on the tee sheet and block any tee times
       try {
         
         PreparedStatement stmt = con.prepareStatement (
                 "SELECT DATE_FORMAT(DATE_ADD(?, INTERVAL -1 DAY), '%Y%m%d') AS d");

         stmt.clearParameters();
         stmt.setLong(1, date);
         rs = stmt.executeQuery();
         
         if (rs.next()) {
             
            date = rs.getInt(1);
         }
         stmt.close();

         year = (int)date / 10000;
         month = ((int)date - (year * 10000)) / 100;
         day = (int)date - (year * 10000) - (month * 100);
         
       } catch (Exception ignore) { }
       
   } // end if not season long event
   
   //
   //  If Members can sign up, then check if guest-only was specified
   //
   if (signUp != 0) {

      if (gstOnly != 0) {
        
         out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
         out.println("<BODY><CENTER><BR>");
         out.println("<p>&nbsp;</p>");
         out.println("<BR><H3>Input Error</H3><BR>");
         out.println("<BR><BR>You have specified 'Guest Only' and 'Allow Members to Sign Up'.<BR>");
         out.println("You cannot specify both of these options for the same event.<BR>");
         out.println("<BR>Please try again.<BR>");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }

   //
   //  If Members can sign up and this is page2 being submitted, then check some fields
   //
   if (signUp != 0 && req.getParameter("page2") != null) {

      //
      // if golf event then check for at least one mode of trans
      //
      if (sess_activity_id == 0) {
          
          boolean some = false;

          for (i=0; i<16; i++) {

             if (tmodei[i] == 1) {

                some = true;
             }
          }

          if (some == false) {    // if no modes specified

             out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
             out.println("<BODY><CENTER><BR>");
             out.println("<p>&nbsp;</p>");
             out.println("<BR><H3>Input Error</H3><BR>");
             out.println("<BR><BR>You have specified 'Allow Members to Sign Up' but did not specify<BR>");
             out.println("any transportation modes.  You must specify at least one transportation mode.<BR>");
             out.println("<BR>Please try again.<BR>");
             out.println("<BR><BR>");
             out.println("<font size=\"2\">");
             out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
             out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
             out.println("</form></font>");
             out.println("</CENTER></BODY></HTML>");
             out.close();
             return;
          }
      }
      
      if (max == 0 || max > 999) {

         out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
         out.println("<BODY><CENTER><BR>");
         out.println("<p>&nbsp;</p>");
         out.println("<BR><H3>Input Error</H3><BR>");
         out.println("<BR><BR>Sorry, the Max Number of Teams value is invalid.<BR>");
         out.println("It must be in the range of 1 - 999.<BR>");
         out.println("<BR>Please try again.<BR>");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
        
      if (su_min > 59) {

         out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
         out.println("<BODY><CENTER><BR>");
         out.println("<p>&nbsp;</p>");
         out.println("<BR><H3>Input Error</H3><BR>");
         out.println("<BR><BR>Sorry, the Minute value for the Sign-up Time is invalid.<BR>");
         out.println("It cannot be greater than 59.<BR>");
         out.println("<BR>Please try again.<BR>");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
        
      if (c_min > 59) {

         out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
         out.println("<BODY><CENTER><BR>");
         out.println("<p>&nbsp;</p>");
         out.println("<BR><H3>Input Error</H3><BR>");
         out.println("<BR><BR>Sorry, the Minute value for the cut-off Time is invalid.<BR>");
         out.println("It cannot be greater than 59.<BR>");
         out.println("<BR>Please try again.<BR>");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  adjust some values for the table
      //
      su_date = su_year * 10000;             // create a date field from input values
      su_date = su_date + (su_month * 100);
      su_date = su_date + su_day;             // date = yyyymmdd (for comparisons)

      if ((su_hr != 12) && (su_ampm.equals( "PM" ))) {   // _hr specified as 01 - 12

         su_hr = su_hr + 12;                             // convert to military time (12 is always Noon or PM)
      }

      if ((su_hr == 12) && (su_ampm.equals( "AM" ))) {   // _hr specified as 01 - 12

         su_hr = 0;                                     // midnight is 00:00
      }

      su_time = su_hr * 100;
      su_time = su_time + su_min;

      c_date = c_year * 10000;             // create a date field from input values
      c_date = c_date + (c_month * 100);
      c_date = c_date + c_day;             // date = yyyymmdd (for comparisons)

      if ((c_hr != 12) && (c_ampm.equals( "PM" ))) {   // _hr specified as 01 - 12

         c_hr = c_hr + 12;                             // convert to military time (12 is always Noon or PM)
      }

      if ((c_hr == 12) && (c_ampm.equals( "AM" ))) {   // _hr specified as 01 - 12

         c_hr = 0;                                     // midnight is 00:00
      }

      c_time = c_hr * 100;
      c_time = c_time + c_min;

      //
      //  Trim the event name
      //
      name = Utilities.trimDoubleSpaces(name).trim();

      //
      //  verify that the signup cut-off date is earlier than the events date (skip for season long events)
      //
      if (c_date > date && season == 0) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>The Sign Up Cut-off date cannot be later than the Event date.");
         out.println("<BR>Please try again.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  verify that the signup cut-off date is later than the start sign-up date
      //
      if (c_date < su_date) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>The Cut-Off date cannot be earlier than the Sign-Up date.");
         out.println("<BR>Please try again.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
      
      //
      //  Validate minimum team size
      //
      if (minsize > size) {

          out.println(SystemUtils.HeadTitle("Data Entry Error"));
          out.println("<BODY><CENTER>");
          out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
          out.println("<BR><BR>The minimum team size is larger then the team size.");
          out.println("<BR>You entered a team size of " + size + " and a minimum sign-up size of " + minsize + ".");
          out.println("<BR>Please try again.");
          out.println("<br><br><font size=\"2\">");
          out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
          out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form></font>");
          out.println("</CENTER></BODY></HTML>");
          return;
      }
   }
        
   try {

      if (req.getParameter("page2") == null) {   // if first time here (page 1)

         //
         //  Check if event already exists in database
         //
         PreparedStatement stmt = con.prepareStatement (
                 "SELECT date FROM events2b WHERE name = ?");

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);       // put the parm in stmt
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            dupMem(out);    // event exists - inform the user and return
            stmt.close();
            return;
         }
         stmt.close();

         //
         //  add event data to the database
         //
         pstmt = con.prepareStatement (
           "INSERT INTO events2b (name, date, year, month, day, start_hr, start_min, " +
           "stime, end_hr, end_min, etime, color, type, act_hr, act_min, courseName, signUp, " +
           "format, pairings, size, max, guests, memcost, gstcost, c_month, c_day, c_year, c_hr, c_min, " +
           "c_date, c_time, itin, mc, pc, wa, ca, gstOnly, x, xhrs, holes, su_month, su_day, su_year, " +
           "su_hr, su_min, su_date, su_time, fb, mempos, gstpos, tmode1, tmode2, tmode3, tmode4, tmode5, tmode6, " +
           "tmode7, tmode8, tmode9, tmode10, tmode11, tmode12, tmode13, tmode14, tmode15, tmode16, " +
           "stime2, etime2, fb2, gender, season, export_type, email1, email2, " +
           "mem1, mem2, mem3, mem4, mem5, mem6, mem7, mem8, " +
           "mem9, mem10, mem11, mem12, mem13, mem14, mem15, mem16, " +
           "mem17, mem18, mem19, mem20, mem21, mem22, mem23, mem24, " +
           "mship1, mship2, mship3, mship4, mship5, mship6, mship7, mship8, " +
           "mship9, mship10, mship11, mship12, mship13, mship14, mship15, mship16, " +
           "mship17, mship18, mship19, mship20, mship21, mship22, mship23, mship24, " +
           "ask_homeclub, ask_phone, ask_address, ask_hdcp, ask_email, ask_gender, ask_shirtsize, ask_shoesize, ask_otherA1, ask_otherA2, ask_otherA3, req_guestname, " +
           "req_homeclub, req_phone, req_address, req_hdcp, req_email, req_gender, req_shirtsize, req_shoesize, req_otherA1, req_otherA2, req_otherA3, otherQ1, otherQ2, otherQ3, " +
           "activity_id, locations, memedit, ask_custom1, req_custom1, " +
           "member_item_code, guest_item_code, member_fee, guest_fee, member_tax, guest_tax) " +
           "VALUES (?,?,?,?,?,?,?," +
           "?,?,?,?,?,?,?,?,?,?," +
           "'','',0,0,0,'','',0,0,0,0,0," +
           "0,0,?,0,0,0,0,?,0,0,?,0,0,0," +
           "0,0,0,0,?,'','',0,0,0,0,0,0," +
           "0,0,0,0,0,0,0,0,0,0," +
           "?,?,?,?,?,?,?,?," +
           "'','','','','','','',''," +
           "'','','','','','','',''," +
           "'','','','','','','',''," +
           "'','','','','','','',''," +
           "'','','','','','','',''," +
           "'','','','','','','',''," +
           "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
           "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
           "?,?,?,?,?,?,?,?," +
           "?,?,?,?,?,?)");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, name);       // put the parm in pstmt
         pstmt.setLong(2, date);
         pstmt.setInt(3, year);
         pstmt.setInt(4, month);
         pstmt.setInt(5, day);
         pstmt.setInt(6, s_hr);
         pstmt.setInt(7, s_min);
         pstmt.setInt(8, stime);
         pstmt.setInt(9, e_hr);
         pstmt.setInt(10, e_min);
         pstmt.setInt(11, etime);
         pstmt.setString(12, color);
         pstmt.setInt(13, type);
         pstmt.setInt(14, a_hr);
         pstmt.setInt(15, a_min);
         pstmt.setString(16, course);
         pstmt.setInt(17, signUp);
         pstmt.setString(18, itin);
         pstmt.setInt(19, gstOnly);
         pstmt.setInt(20, holes);
         pstmt.setString(21, fb);
         pstmt.setInt(22, stime2);
         pstmt.setInt(23, etime2);
         pstmt.setString(24, fb2);
         pstmt.setInt(25, gender);
         pstmt.setInt(26, season);
         pstmt.setInt(27, export_type);
         pstmt.setString(28, email1);
         pstmt.setString(29, email2);
         pstmt.setInt(30, ask_homeclub);
         pstmt.setInt(31, ask_phone);
         pstmt.setInt(32, ask_address);
         pstmt.setInt(33, ask_hdcp);
         pstmt.setInt(34, ask_email);
         pstmt.setInt(35, ask_gender);
         pstmt.setInt(36, ask_shirtsize);
         pstmt.setInt(37, ask_shoesize);
         pstmt.setInt(38, ask_otherQ1);
         pstmt.setInt(39, ask_otherQ2);
         pstmt.setInt(40, ask_otherQ3);
         pstmt.setInt(41, req_guestname);
         pstmt.setInt(42, req_homeclub);
         pstmt.setInt(43, req_phone);
         pstmt.setInt(44, req_address);
         pstmt.setInt(45, req_hdcp);
         pstmt.setInt(46, req_email);
         pstmt.setInt(47, req_gender);
         pstmt.setInt(48, req_shirtsize);
         pstmt.setInt(49, req_shoesize);
         pstmt.setInt(50, req_otherQ1);
         pstmt.setInt(51, req_otherQ2);
         pstmt.setInt(52, req_otherQ3);
         pstmt.setString(53, otherQ1);
         pstmt.setString(54, otherQ2);
         pstmt.setString(55, otherQ3);
         pstmt.setInt(56, sess_activity_id);
         pstmt.setString(57, locations_csv);
         pstmt.setInt(58, memedit);
         pstmt.setInt(59, ask_custom1);
         pstmt.setInt(60, req_custom1);
         pstmt.setString(61, member_item_code);
         pstmt.setString(62, guest_item_code);
         pstmt.setDouble(63, member_fee);
         pstmt.setDouble(64, guest_fee);
         pstmt.setDouble(65, member_tax);
         pstmt.setDouble(66, guest_tax);
         
         pstmt.executeUpdate();          // execute the prepared stmt

         pstmt.close();   // close the stmt
         
         // Go out and apply the selected event categories for this event
         Utilities.updateEventCategoryBindings(name, sess_activity_id, category_ids, con);
      
         //
         //  If member signup was specified, then prompt for page2 of options
         //
         if (signUp != 0) {

            //
            //  If we got here from a Copy request, go to Proshop_editevnt to do the 2nd page
            //
            if (!copyname.equals( "" )) {

               out.println("<script type=\"text/javascript\">");
               out.println("document.location.href='Proshop_editevnt?signUp=yes&Continue=yes&name=" +name+ "&course=" +course+ "&copyname=" +copyname+ "'");
               out.println("</script>");
               return;
            }

            //
            // Get the 'POS Type' option from the club db, if specified
            //
            try {

               stmtc = con.createStatement();        // create a statement

               rs = stmtc.executeQuery("SELECT posType " +
                                      "FROM club5 WHERE clubName != ''");

               if (rs.next()) {

                  posType = rs.getString(1);

               }
               stmtc.close();

               if (sess_activity_id == 0) {

                   //
                   //  Get the Modes of Trans for the course specified
                   //
                   PreparedStatement pstmtc = null;

                   if (course.equals( "-ALL-" )) {

                      pstmtc = con.prepareStatement (
                         "SELECT * FROM clubparm2");        // get the first course's parms

                      pstmtc.clearParameters();

                   } else {

                      pstmtc = con.prepareStatement (
                         "SELECT * " +
                         "FROM clubparm2 WHERE courseName = ?");

                      pstmtc.clearParameters();        // clear the parms
                      pstmtc.setString(1, course);
                   }

                   rs = pstmtc.executeQuery();      // execute the prepared stmt

                   if (rs.next()) {

                      tmode[0] = rs.getString("tmode1");
                      tmode[1] = rs.getString("tmode2");
                      tmode[2] = rs.getString("tmode3");
                      tmode[3] = rs.getString("tmode4");
                      tmode[4] = rs.getString("tmode5");
                      tmode[5] = rs.getString("tmode6");
                      tmode[6] = rs.getString("tmode7");
                      tmode[7] = rs.getString("tmode8");
                      tmode[8] = rs.getString("tmode9");
                      tmode[9] = rs.getString("tmode10");
                      tmode[10] = rs.getString("tmode11");
                      tmode[11] = rs.getString("tmode12");
                      tmode[12] = rs.getString("tmode13");
                      tmode[13] = rs.getString("tmode14");
                      tmode[14] = rs.getString("tmode15");
                      tmode[15] = rs.getString("tmode16");
                   }
                   pstmtc.close();
               }
               
               //
               // Get the Mem and Mship Types from the club db
               //
               getClub.getParms(con, parm, sess_activity_id);        // get the club parms

            } catch (Exception ignore) { }

            
            //
            //  Build the HTML page to prompt user for the info
            //
            out.println(SystemUtils.HeadTitle2("ForeTees Proshop Add Event Page 2"));
            /*
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">");
            out.println("<html>");
            out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
               out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
               out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
               out.println("<title>ForeTees Proshop Add Special Event Main</title>");
            */
               out.println("<script type=\"text/javascript\">");
               out.println("<!--");
               out.println("function cursor() { document.forms['f'].format.focus(); }");
               out.println("// -->");
               out.println("</script>");
            out.println("</head>");

            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
            SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
            out.println("<font face=\"Arial, Helvetica, sans-serif\">");
            //out.println("<center>");
            out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
            out.println("<tr><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
               out.println("<b>Special Events</b><br>");
            out.println("</font><font color=\"#FFFFFF\" size=\"2\">");
               out.println("Complete the following information for the Member Sign-up.<br>");
               out.println("Click on <b>Continue</b> to add this information to the event.");
            out.println("</font></td></tr></table><br>");
            out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" align=\"center\">");
               out.println("<form action=\"Proshop_addevnt\" method=\"post\" target=\"bot\" name=\"f\">");
               out.println("<tr>");
               out.println("<td width=\"530\">");
                  out.println("<font size=\"2\">");
                    out.println("<b>Complete the following ONLY if you selected Yes for members to sign up online:</b>");
                    out.println("<br> (This information will be used to define the event for member sign up.)");
                    out.println("<br><br>");
                  out.println("Event Format:&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"format\" size=\"40\" maxlength=\"60\">");
                     out.println("");
                  out.println("<br><br>");
                    out.println("Who Makes the Teams:&nbsp;&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"pairings\">");
                      out.println("<option value=\"ProShop\">" + ((sess_activity_id == 0) ? "Golf Shop" : "Staff") + "</option>");
                      out.println("<option value=\"Member\">Member</option>");
                    out.println("</select>");
                    out.println("<br><br>");
                    out.println("Team Size (if Members Make Their Own Teams):&nbsp;&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"size\">");
                    if (sess_activity_id == 0) {
                      out.println("<option>1</option>");
                      out.println("<option>2</option>");
                      out.println("<option>3</option>");
                      out.println("<option>4</option>");
                      out.println("<option>5</option>");
                    } else {
                      for (i = 1; i <= parmAct.max_players; i++) {
                        out.println("<option>" + i + "</option>");
                      }
                    }
                    out.println("</select>");
                    out.println("&nbsp;&nbsp;(1 if " + ((sess_activity_id == 0) ? "Golf Shop" : "Staff") + " Makes Teams)");
                    out.println("<br><br>");
                    out.println("Min. Sign-up Size (if Members Make Their Own Teams):&nbsp;&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"minsize\">");
                    if (sess_activity_id == 0) {
                      out.println("<option>1</option>");
                      out.println("<option>2</option>");
                      out.println("<option>3</option>");
                      out.println("<option>4</option>");
                      out.println("<option>5</option>");
                    } else {
                      for (i = 1; i <= parmAct.max_players; i++) {
                        out.println("<option>" + i + "</option>");
                      }
                    }
                    out.println("</select>");
                    out.println("&nbsp;&nbsp;(must not be greater than team size)");
                    out.println("<br><br>");
                  out.println("Max # of Teams:&nbsp;&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"max\" size=\"3\" maxlength=\"3\">");
                     out.println("<br> (If " + ((sess_activity_id == 0) ? "Golf Shop" : "Staff") + " Makes Teams, this will be # of members allowed to sign up.)");
                     out.println("");
                    out.println("<br><br>");
                    out.println("Guests Allowed per Member (if Member Makes Teams):&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"guests\">");
                  if (sess_activity_id == 0) {
                      out.println("<option>0</option>");
                      out.println("<option>1</option>");
                      out.println("<option>2</option>");
                      out.println("<option>3</option>");
                      out.println("<option>4</option>");
                  } else {
                      for (i = 1; i <= parmAct.max_players; i++) {
                          out.println("<option>" + (i - 1) + "</option>");
                      }
                  }
                    out.println("</select>");
/*
                    out.println("<br><br>");
                    out.println("If allowing Guests do you want to force the inclusion of Guest Names?:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"req_guestname\">");
                      out.println("<option value=\"No\">No</option>");
                      out.println("<option value=\"Yes\">Yes</option>");
                    out.println("</select>");
*/
                    out.println("<br><br>");
                      out.println("Allow members to reserve player positions using an <b>'X'</b>?<br>");
                        out.println("If yes, how many X's can members specify per team? (0 [zero] = NO): &nbsp;&nbsp;");
                        out.println("<select size=\"1\" name=\"x\">");
                      if (sess_activity_id == 0) {
                          out.println("<option>0</option>");
                          out.println("<option>1</option>");
                          out.println("<option>2</option>");
                          out.println("<option>3</option>");
                          out.println("<option>4</option>");
                      } else {
                          for (i = 1; i <= parmAct.max_players; i++) {
                              out.println("<option>" + (i - 1) + "</option>");
                          }
                      }
                        out.println("</select>");
                    out.println("<br>");
                      out.println("How many hours in advance of cut-off date/time should we remove X's?: &nbsp;&nbsp;");
                        out.println("<input type=\"text\" name=\"xhrs\" size=\"2\" maxlength=\"2\">&nbsp;&nbsp;(0 - 48)");
                    out.println("<br><br>");
                    if (sess_activity_id == 0) {
                      out.println("Player Transportation Options allowed (select all that apply):");
                      i2 = 1;
                      for (i=0; i<16; i++) {
                         if (!tmode[i].equals( "" ) && !tmode[i].equals( null )) {
                            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                            out.println("<label><input type=\"checkbox\" name=\"tmode" +i2+ "\" value=\"yes\">&nbsp;&nbsp;" +tmode[i]+ "</label>");
                         }
                         i2++;
                      }
                    }
                    out.println("<br><br>");
                  out.println("Cost:&nbsp;&nbsp;per Member&nbsp;");
                     out.println("<input type=\"text\" name=\"memcost\" size=\"10\" maxlength=\"10\">");

                     if (posType.equals("TAI Club Management" ) && sess_activity_id == 0) {   // if POS charges needed
                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;POS Charge Code&nbsp;");
                        out.println("<input type=\"text\" name=\"mempos\" size=\"15\" maxlength=\"30\">");
                        
                     } else {
                         
                         out.println("&nbsp;&nbsp;For Informational Purposes Only&nbsp;");
                     }

                  out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("&nbsp;&nbsp;per Guest&nbsp;");
                     out.println("<input type=\"text\" name=\"gstcost\" size=\"10\" maxlength=\"10\">");

                     if (posType.equals( "TAI Club Management" ) && sess_activity_id == 0) {   // if POS charges needed
                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;POS Charge Code&nbsp;");
                        out.println("<input type=\"text\" name=\"gstpos\" size=\"15\" maxlength=\"30\">");
                     }

                    out.println("<br><br>");
                  out.println("Sign-up Date and Time (when members can begin to sign-up):<br><br>");
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Month:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"su_month\">");
                       dateOpts.opMonth(out, thisMonth);         // output the month options
                    out.println("</select>");
                    out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"su_day\">");
                       dateOpts.opDay(out, thisDay);         // output the day options
                    out.println("</select>");
                    out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"su_year\">");
                       dateOpts.opYear(out, thisYear);         // output the year options
                    out.println("</select>");
                  out.println("<br><br>");
                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Hour &nbsp;");
                    out.println("<select size=\"1\" name=\"su_hr\">");
                      out.println("<option value=\"01\">01</option>");
                      out.println("<option value=\"02\">02</option>");
                      out.println("<option value=\"03\">03</option>");
                      out.println("<option value=\"04\">04</option>");
                      out.println("<option value=\"05\">05</option>");
                      out.println("<option value=\"06\">06</option>");
                      out.println("<option value=\"07\">07</option>");
                      out.println("<option value=\"08\">08</option>");
                      out.println("<option value=\"09\">09</option>");
                      out.println("<option value=\"10\">10</option>");
                      out.println("<option value=\"11\">11</option>");
                      out.println("<option value=\"12\">12</option>");
                    out.println("</select>");
                    out.println("&nbsp; Min &nbsp;");
                    out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"su_min\">");
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"su_ampm\">");
                      out.println("<option value=\"AM\">AM</option>");
                      out.println("<option value=\"PM\">PM</option>");
                    out.println("</select>");
                    out.println("<br><br>");
                  out.println("Cut-off Date and Time (when members can no longer sign up):<br><br>");
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Month:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"c_month\">");
                       dateOpts.opMonth(out, thisMonth);         // output the month options
                    out.println("</select>");
                    out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"c_day\">");
                       dateOpts.opDay(out, thisDay);         // output the day options
                    out.println("</select>");
                    out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"c_year\">");
                       dateOpts.opYear(out, thisYear);         // output the year options
                    out.println("</select>");
                  out.println("<br><br>");
                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Hour &nbsp;");
                    out.println("<select size=\"1\" name=\"c_hr\">");
                      out.println("<option value=\"01\">01</option>");
                      out.println("<option value=\"02\">02</option>");
                      out.println("<option value=\"03\">03</option>");
                      out.println("<option value=\"04\">04</option>");
                      out.println("<option value=\"05\">05</option>");
                      out.println("<option value=\"06\">06</option>");
                      out.println("<option value=\"07\">07</option>");
                      out.println("<option value=\"08\">08</option>");
                      out.println("<option value=\"09\">09</option>");
                      out.println("<option value=\"10\">10</option>");
                      out.println("<option value=\"11\">11</option>");
                      out.println("<option value=\"12\">12</option>");
                    out.println("</select>");
                    out.println("&nbsp; Min &nbsp;");
                    out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"c_min\">");
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"c_ampm\">");
                      out.println("<option value=\"AM\">AM</option>");
                      out.println("<option value=\"PM\">PM</option>");
                    out.println("</select>");
                  out.println("</p>");
                      
                  out.println("<p align=\"left\">&nbsp;&nbsp;<b>Groups to be Restricted</b><br>");
                  out.println("&nbsp;&nbsp;Specify Members to restrict and/or Membership Types to restrict.<br>");
                  out.println("&nbsp;&nbsp;(Any members with the selected Member Type or Membership Type will NOT be allowed.)</p>");

                  out.println("<p align=\"left\">&nbsp;&nbsp;Members to be Restricted (select all that apply, or none):");
                  for (i = 0; i < parm.MAX_Mems; i++) {
                     if (!parm.mem[i].equals( "" )) {
                        i2 = i + 1;
                        out.println("<br>");
                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        out.println("<label><input type=\"checkbox\" name=\"mem" +i2+ "\" value=\"" + parm.mem[i] + "\">&nbsp;&nbsp;" + parm.mem[i] + "</label>");
                     }
                  }
                  out.println("<br><br>&nbsp;&nbsp;Membership Types to be Restricted (select all that apply, or none):");
                  for (i = 0; i < parm.MAX_Mships; i++) {
                     if (!parm.mship[i].equals( "" )) {
                        i2 = i + 1;
                        out.println("<br>");
                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        out.println("<label><input type=\"checkbox\" name=\"mship" +i2+ "\" value=\"" + parm.mship[i] + "\">&nbsp;&nbsp;" + parm.mship[i] + "</label>");
                     }
                  }
                  out.println("</p>");
  
                  out.println("<input type=\"hidden\" name=\"page2\" value=\"yes\">");
                  out.println("<input type=\"hidden\" name=\"event_name\" value=\"" +name+ "\">");
                  out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" +sess_activity_id+ "\">");
                  //out.println("<input type=\"hidden\" name=\"locations_csv\" value=\"" +locations_csv+ "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" +fb+ "\">");
                  out.println("<input type=\"hidden\" name=\"month\" value=\"" +s_month+ "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" +s_day+ "\">");
                  out.println("<input type=\"hidden\" name=\"year\" value=\"" +s_year+ "\">");
                  out.println("<input type=\"hidden\" name=\"start_hr\" value=\"" +start_hr+ "\">");
                  out.println("<input type=\"hidden\" name=\"start_min\" value=\"" +start_min+ "\">");
                  out.println("<input type=\"hidden\" name=\"start_ampm\" value=\"" +start_ampm+ "\">");
                  out.println("<input type=\"hidden\" name=\"end_hr\" value=\"" +end_hr+ "\">");
                  out.println("<input type=\"hidden\" name=\"end_min\" value=\"" +end_min+ "\">");
                  out.println("<input type=\"hidden\" name=\"end_ampm\" value=\"" +end_ampm+ "\">");
                  out.println("<input type=\"hidden\" name=\"act_hr\" value=\"" +act_hr+ "\">");
                  out.println("<input type=\"hidden\" name=\"act_min\" value=\"" +act_min+ "\">");
                  out.println("<input type=\"hidden\" name=\"act_ampm\" value=\"" +act_ampm+ "\">");
                  out.println("<input type=\"hidden\" name=\"fb2\" value=\"" +fb2+ "\">");
                  out.println("<input type=\"hidden\" name=\"start_hr2\" value=\"" +start_hr2+ "\">");
                  out.println("<input type=\"hidden\" name=\"start_min2\" value=\"" +start_min2+ "\">");
                  out.println("<input type=\"hidden\" name=\"start_ampm2\" value=\"" +start_ampm2+ "\">");
                  out.println("<input type=\"hidden\" name=\"end_hr2\" value=\"" +end_hr2+ "\">");
                  out.println("<input type=\"hidden\" name=\"end_min2\" value=\"" +end_min2+ "\">");
                  out.println("<input type=\"hidden\" name=\"end_ampm2\" value=\"" +end_ampm2+ "\">");
                  out.println("<input type=\"hidden\" name=\"type\" value=\"" +stype+ "\">");
                  out.println("<input type=\"hidden\" name=\"holes\" value=\"" +holes+ "\">");
                  out.println("<input type=\"hidden\" name=\"color\" value=\"" +color+ "\">");
                  out.println("<input type=\"hidden\" name=\"signUp\" value=\"" +ssignUp+ "\">");
                  out.println("<input type=\"hidden\" name=\"gstOnly\" value=\"" +guestOnly+ "\">");
                  out.println("<input type=\"hidden\" name=\"gender\" value=\"" +gender+ "\">");
                  out.println("<input type=\"hidden\" name=\"season\" value=\"" +season+ "\">");
                  out.println("<input type=\"hidden\" name=\"export_type\" value=\"" +export_type+ "\">");
                  out.println("<input type=\"hidden\" name=\"email1\" value=\"" +email1+ "\">");
                  out.println("<input type=\"hidden\" name=\"email2\" value=\"" +email2+ "\">");
                  out.println("<input type=\"hidden\" name=\"itin\" value=\"" +StringEscapeUtils.escapeHtml(itin)+ "\">");
                  
                  Common_Config.printEventCategoryHiddenInputs(category_ids, out);      // print out all the hidden inputs for selected category_ids
                  
                  if (copy == true) {
                     out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
                  }
                  out.println("<p align=\"center\">");
                    out.println("<input type=\"submit\" value=\"Continue\">");
                    out.println("</p>");
                  out.println("</font>");
               out.println("</td>");
            out.println("</tr>");
            out.println("</form>");
            out.println("</table>");
            out.println("<font size=\"2\"><br>");
            out.println("<form method=\"get\" action=\"Proshop_events\">");
            out.println("<div style=\"margin:auto; text-align:center\"><input type=\"submit\" value=\"Done\" style=\"text-decoration:underline; background:#8B8970; width:75px\"></div>");
            out.println("</form></font>");
            //out.println("</center>");
            out.println("</font><br>");
            out.println("</body>");
            out.println("</html>");
            out.close();
            return;               // exit and wiat for reply
            
         } // end if member signup was specified

      } else {   // this is page2 - update the event
        
         pstmt = con.prepareStatement (
           "UPDATE events2b SET " +
           "format = ?, pairings = ?, size = ?, max = ?, guests = ?, memcost = ?, " +
           "gstcost = ?, c_month = ?, c_day = ?, c_year = ?, c_hr = ?, c_min = ?, c_date = ?, " +
           "c_time = ?, gstOnly = ?, x = ?, xhrs = ?, " +
           "su_month = ?, su_day = ?, su_year = ?, su_hr = ?, su_min = ?, su_date = ?, su_time = ?, " +
           "mempos = ?, gstpos = ?, tmode1 = ?, tmode2 = ?, tmode3 = ?, tmode4 = ?, tmode5 = ?, tmode6 = ?, " + 
           "tmode7 = ?, tmode8 = ?, tmode9 = ?, tmode10 = ?, tmode11 = ?, tmode12 = ?, tmode13 = ?, " +
           "tmode14 = ?, tmode15 = ?, tmode16 = ?, " +
           "mem1 = ?, mem2 = ?, mem3 = ?, mem4 = ?, mem5 = ?, mem6 = ?, mem7 = ?, mem8 = ?, " +
           "mem9 = ?, mem10 = ?, mem11 = ?, mem12 = ?, mem13 = ?, mem14 = ?, mem15 = ?, mem16 = ?, " +
           "mem17 = ?, mem18 = ?, mem19 = ?, mem20 = ?, mem21 = ?, mem22 = ?, mem23 = ?, mem24 = ?, " +
           "mship1 = ?, mship2 = ?, mship3 = ?, mship4 = ?, mship5 = ?, mship6 = ?, mship7 = ?, mship8 = ?, " +
           "mship9 = ?, mship10 = ?, mship11 = ?, mship12 = ?, mship13 = ?, mship14 = ?, mship15 = ?, mship16 = ?, " +
           "mship17 = ?, mship18 = ?, mship19 = ?, mship20 = ?, mship21 = ?, mship22 = ?, mship23 = ?, mship24 = ?, " +
           "minsize = ?, activity_id = ? " +
           "WHERE name = ? AND courseName = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, format);
         pstmt.setString(2, pairings);
         pstmt.setInt(3, size);
         pstmt.setInt(4, max);
         pstmt.setInt(5, guests);
         pstmt.setString(6, memcost);
         pstmt.setString(7, gstcost);
         pstmt.setInt(8, c_month);
         pstmt.setInt(9, c_day);
         pstmt.setInt(10, c_year);
         pstmt.setInt(11, c_hr);
         pstmt.setInt(12, c_min);
         pstmt.setLong(13, c_date);
         pstmt.setInt(14, c_time);
         pstmt.setInt(15, gstOnly);
         pstmt.setInt(16, x);
         pstmt.setInt(17, xhrs);
         pstmt.setInt(18, su_month);
         pstmt.setInt(19, su_day);
         pstmt.setInt(20, su_year);
         pstmt.setInt(21, su_hr);
         pstmt.setInt(22, su_min);
         pstmt.setLong(23, su_date);
         pstmt.setInt(24, su_time);
         pstmt.setString(25, mempos);
         pstmt.setString(26, gstpos);
         pstmt.setInt(27, tmodei[0]);
         pstmt.setInt(28, tmodei[1]);
         pstmt.setInt(29, tmodei[2]);
         pstmt.setInt(30, tmodei[3]);
         pstmt.setInt(31, tmodei[4]);
         pstmt.setInt(32, tmodei[5]);
         pstmt.setInt(33, tmodei[6]);
         pstmt.setInt(34, tmodei[7]);
         pstmt.setInt(35, tmodei[8]);
         pstmt.setInt(36, tmodei[9]);
         pstmt.setInt(37, tmodei[10]);
         pstmt.setInt(38, tmodei[11]);
         pstmt.setInt(39, tmodei[12]);
         pstmt.setInt(40, tmodei[13]);
         pstmt.setInt(41, tmodei[14]);
         pstmt.setInt(42, tmodei[15]);
         for (i=1; i<parm.MAX_Mems+1; i++) {
            pstmt.setString(42+i, mtype[i]);
         }
         for (i=1; i<parm.MAX_Mships+1; i++) {
            pstmt.setString(66+i, mship[i]);
         }
        
         pstmt.setInt(91, minsize);
         pstmt.setInt(92, sess_activity_id);
         pstmt.setString(93, name);
         pstmt.setString(94, course);

         pstmt.executeUpdate();     // execute the prepared stmt

         pstmt.close();
      }
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR>Exception:   " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Event"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>The Event Has Been Added</H3>");
   out.println("<BR><BR>Thank you, the event has been added to the system database.");
   out.println("<BR><BR>If you would like to recur this event, please verify that the event was added as desired<BR>and then go to System Config - Event Setup - Recur an Existing Event.");
/*   
   if (itinL > 255) {

      out.println("<br><br><b>Notice:</b>&nbsp;&nbsp;The Itinerary you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
   }
*/
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   if (copy == false) {
      out.println("<form method=\"get\" action=\"Proshop_addevnt\">");
   } else {
      out.println("<form method=\"get\" action=\"Proshop_events\">");
      out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
   }
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   try {
      resp.flushBuffer();      // force the repsonse to complete
   }
   catch (Exception ignore) {
   }

   //
   //  Now, call utility to scan the event table and update tee slots in teecurr accordingly
   //
   if (sess_activity_id == 0) {

       SystemUtils.do1Event(con, name);

   } else {

       SystemUtils.do1Event(con, name);

   }
   
 }   // end of doPost   


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("Check the Name, Start Time and End Time fields.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 //  Event already exists
 // *********************************************************

 private void dupMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the <b>event name</b> you specified already exists in the database.<BR>");
   out.println("<BR>Please use the edit feature to change an existing event record.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

}
