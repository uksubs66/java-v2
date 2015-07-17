/***************************************************************************************
 *   Proshop_announce: This servlet will process the 'View Club Announcements' request
 *                     from the Proshop's main page.  It will also (doPost) process the
 *                     request to save the edited announcement page from Active Edit (ae.jsp).
 *
 *
 *   called by:  Proshop_*** (most servlets as a return to Home)
 *               Proshop Tools Menu (doGet menu=yes)
 *               self (doPost from doGet)
 *               v_/ae.jsp (do Post)
 *
 *
 *   created: 2/03/2003   Bob P.
 *
 *   last updated:            ******* keep this accurate *******
 *
 *       11/26/13   Add club switch drop-down to top of announce page when user is proshop4tea.
 *        7/16/13   Explain why users should use full screen mode in the Helpful Hint.
 *        3/26/13   Changed the name of the parm in doPost from probio to proid when looking for this value to determine if call is from lesson pro.
 *        1/26/13   Fixed new lesson pro bio page issue when the file was not found.
 *        1/22/13   Fixed issue with save button on announcement page online editor screen.
 *        1/08/13   Updated the Lesson Bio editor to use tinymce and switched preview to match what members see
 *       10/23/12   Updated file_filter regex to skip our SAVED BY html comments that are encountered in the middle of a page (or mixed with other commented out html)
 *       10/03/12   Add ability to delete staged announcement pages
 *        7/13/12   Dining - changed references to Proshop Announcement page to Dining Announcement page
 *        4/19/12   Change the Upload process to allow any name for the file and save it in the Staging folder until it is published.
 *        4/17/12   Add Hint message to editor page to inform users to use the Full Screen button for easier editing, and to use the Save button to save the changes while
 *                  in full screen mode.
 *        4/11/12   Updated announcement page publish message to indicate where clubs should go to activate the new skin.
 *        4/10/12   Updated announcement page publish/restore messages so they no longer say the action cannot be undone, but instead point users to the Manage Backup Copies page to revert to previous versions.
 *        4/05/12   Added management of annoucement pages to allow clubs to stage pages in advance.
 *        3/15/12   Bracketts Crossing (bracketts) - Added custom to display the old online editor link, since their current announcement page will not work without it.
 *        3/14/12   Removed the old online editor from the available options (commented out).
 *        2/26/12   Added support for proshopfb users to view/update the dining announcement page
 *        2/25/12   Added code for using tinymce 3.4.9 and Jquery with new skin in Proshop_announce
 *        2/15/12   Added a note to warn clubs that the old editor will be removed from ForeTees on 3/15/2012. A prompt will also appear with details if the link is clicked.
 *        1/24/12   Add support for viewing new skin announcement pages on the proshop side
 *        1/04/12   Backup Preview now includes CSS and wrapper for new skin. Dining mode links now return to menu instead of closing window.
 *       11/16/11   Added center alignment to div surrounding entire announcement page to force pages to center align, even if the announcement page is left aligned.
 *       11/06/11   Change paths for announcement and bio pages - now using NFS and discrete club folders
 *       10/31/11   Palm Valley CC (palmvalley-cc) - Added custom processing to process of loading backups, since the "-" in their clubname was crashing the parsing of the backup file name.
 *        8/30/11   Allow for Dining proshop user (Admin) to access these pages in non-frame mode.
 *        3/20/11   Add @SuppressWarnings annotations to applicable methods
 *        8/31/10   Added 'fast-club-switching' ability for testing on the demo sites
 *        8/25/10   Remove the media icon from the tinymce menu - it requires an add-on that we don't have.
 *        8/24/10   Listing of backup files is now sorted by date (actually filename which includes a timestamp)
 *        7/17/10   Fix club name matching in backupListings method
 *        6/09/10   Add a div tag with align=center to force all announcement pages to center alignment.
 *        4/10/10   Add support for the new tinyMCE editor.
 *        9/25/09   Add support for Activity announcement pages
 *        7/18/08   Added limited access proshop users checks
 *        4/09/08   Add Announcement/Bio page management area for deleting / restoring automated backups
 *       11/15/07   Add trim to textfield before we see if it's empty
 *        1/20/07   Changed the multiple static paths to use the getRealPath method or resolving local path at runtime
 *        4/14/06   Changed to read in the announcement page from disk then output
 *                  it to the user instead of loading it in an iframe and having
 *                  the client request the page.
 *       11/11/04   Ver 5 - add support for Lesson Pro's Bio page - uploading.
 *        1/09/04   Add processing to display intermediate page after control from Proshop_system.
 *        1/05/04   Add the doPost processing
 *        7/18/03   Enhancements for Version 3 of the software.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.regex.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;

public class Proshop_announce extends HttpServlet {


 final String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 final String diningPrefix = "<div id=\"wrapper\"><br />\n<div id=\"main\" align=\"center\">"; //  (55)  <!--4T-SNIP-->
 final String diningSuffix = "</div><br />&nbsp;\n</div>";
 final int diningPrefix_length = diningPrefix.length() + 1; // remove one for each escaped character
 final int diningSuffix_length = diningSuffix.length() + 1;
 
 static String DINING_USER = ProcessConstants.DINING_USER;               // Dining username for Admin user from Dining System

 final String folder = "/";
 final static String staging_ext = ".staging";

 //*****************************************************
 // Process the initial request from Proshop_main
 //*****************************************************
 //
 @SuppressWarnings("deprecation")
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   
   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");   // get club name
   String user = (String)session.getAttribute("user");
   int activity_id = (Integer)session.getAttribute("activity_id");
   
   if (req.getParameter("backups") != null) {
       

       Connection con = SystemUtils.getCon(session);                     // get DB connection

       if (con == null) {

           out.println(SystemUtils.HeadTitle("DB Connection Error"));
           out.println("<BODY><CENTER><BR>");
           out.println("<BR><BR><H3>Database Connection Error</H3>");
           out.println("<BR><BR>Unable to connect to the Database.");
           out.println("<BR>Please try again later.");
           out.println("<BR><BR>If problem persists, contact customer support.");
           out.println("<BR><BR>");
           out.println("<a href=\"javascript:history.back(1)\">Return</a>");
           out.println("</CENTER></BODY></HTML>");
           out.close();
           return;
       }

       if (!user.equals(DINING_USER)) {
         
          // Check Feature Access Rights for current proshop user
          if (!SystemUtils.verifyProAccess(req, "TOOLS_ANNOUNCE", con, out)) {
              SystemUtils.restrictProshop("TOOLS_ANNOUNCE", out);
              return;
          }
       }
       
       manageBackups(req, resp, club, user, activity_id, con, out);

       return;

   } else if (req.getParameter("manage") != null) {

       Connection con = SystemUtils.getCon(session);
       manageVersions(req, resp, club, user, activity_id, con, out);

       return;

   } else if (req.getParameter("clubswitch") != null && req.getParameter("clubswitch").equals("1") && req.getParameter("club") != null) {

       String newClub = req.getParameter("club");

       Connection con = null;

       //
       //  release the old connection
       //
       ConnHolder holder = (ConnHolder) session.getAttribute("connect");

       if (holder != null) {

           con = holder.getConn();      // get the connection for previous club
       }

       if (con != null) {

           try { con.close(); }            // close/release the previous connection
           catch (Exception ignore) {}
       }
       
       //
       //  Connect to the new club
       //
       try {
          con = dbConn.Connect(newClub);           // get connection to this club's db
       }
       catch (Exception ignore) {}

       holder = new ConnHolder(con);

       session.setAttribute("club", newClub);
       session.setAttribute("connect", holder);
       
       out.println("<p align=center><br><br>Switching club from " + club + " to " + newClub + "</p>");
       
       out.println("<p align=center><a href=\"/" + rev + "/proshop_welcome.htm\" target=\"_top\">  Continue  </a></p>");
       
       //resp.setHeader("Location", "http://216.243.184.86:8080/" + rev + "/proshop_welcome.htm");
       //resp.setHeader("Connection", "close");

       return;
   }

   
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   
   //
   //   File objects to get the current announcement page
   //
   File f;
   FileReader fr = null;
   BufferedReader br = null;
   String tmp = "";
   String path = "";
   
   
   //
   //  if call from menu below to use the tinyMCE online editor, then open the tinyMCE editor
   //
   if (req.getParameter("tinymce") != null) {
      
      
      //temp until new skin is always on - then won't need a con object
      Connection con = null;
      boolean new_skin = false;

      try {

          con = SystemUtils.getCon(session);
          new_skin = Utilities.isNewSkinActive(club, con);

      } catch (Exception ignore) {

      } finally {

          try { con.close(); }
          catch (Exception ignore) {}

      }

      String filename = "";

      // see if we're here to do something with a pro's lesson bio page
      int bio_id = 0;
      try {
          if (req.getParameter("bio") != null) bio_id = Integer.parseInt(req.getParameter("id"));
      } catch (Exception ignore) {}

      
      if (req.getParameter("file") != null && !req.getParameter("file").equals("")) {

          // user is here to edit a page that's in staging
          filename = req.getParameter("file");
          path = req.getRealPath("");

          f = new File(path + "/announce/" +club+ "/" + filename);

          if (!f.isFile()) {

              out.println("<p>File not found.</p><br>" + path + filename);
              return;

          }

      } else /* if (req.getParameter("ns") != null) {

          // club is in transition - user is here to edit the temp new skin file
          filename = club + "_announce_ns" + ((activity_id == 0) ? "" : (activity_id == ProcessConstants.DINING_ACTIVITY_ID) ? "_dining" : "_" + activity_id) + ".htm";
          new_skin = true;

      } else */ {

          // get current announcement page for this club / activity
          if (bio_id > 0) {
              
              filename = Utilities.getLessonBioPageFileName(bio_id, club, "", false);

          } else {
              
              filename = getFileName(activity_id, user, club, "", false);

          }

      }

       if (filename.endsWith(staging_ext)) new_skin = true; // force new skin if viewing staging
      
       String css_includes = "";
       Gson gson_obj = new Gson();
       Map<String, Object> editor_options = new LinkedHashMap<String, Object>();

       /*
       if (new_skin || user.equals(DINING_USER) || user.equals("proshopfb")) {
           css_includes += "/" + rev + "/assets/stylesheets/sitewide.css";
       }
       if (user.equals(DINING_USER) || user.equals("proshopfb")) {
           css_includes += ((!css_includes.equals("")) ? "," : "") + "/" + rev + "/assets/stylesheets/sitewide_dining.css";
       }
        * 
        */
       // Get CSS list for editor iframe from Common Skin
       if (new_skin || user.equals(DINING_USER) || user.equals("proshopfb")) {
        List<String> cssList = Common_skin.getProshopCSS(ProcessConstants.SCRIPT_MODE_PROSHOP_EDITOR_IFRAME, 0);
        css_includes = StringUtils.join(cssList.toArray(new String[cssList.size()]), ",");
       }
       
       if (new_skin || user.equals(DINING_USER) || user.equals("proshopfb")) { // New skin, transitional
           out.println("<!doctype html>");
           out.println("<html lang=\"en-US\">\n<head>");
           out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
           out.println("<title> \"ForeTees " + ((user.equals(DINING_USER) || user.equals("proshopfb")) ? "Dining" : "Proshop") + " Announcement Page\"</title>");

           out.println( Common_skin.getScripts(club, activity_id, session, req, true));

           if (css_includes.length() > 0) {
               editor_options.put("content_css", css_includes);
           }

       } else { // old skin

           Utilities.logError("*** CLUB IS USING OLD SKIN TO EDIT ANNOUNCEMENT PAGE *** club=" + club);

           out.println("<html lang=\"en-US\">\n<head>");
           out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
           out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
           out.println("<title> \"ForeTees Proshop Announcement Page\"</title>");
           out.println("<script type=\"text/javascript\" src=\"/" + rev + "/web%20utilities/foretees.js\"></script>");
           out.println("<script type=\"text/javascript\" src=\"/" + rev + "/web%20utilities/tiny_mce/tiny_mce.js\"></script>");
           out.println("<script type=\"text/javascript\">");
           out.println("tinyMCE.init({");
           // General options
           //out.println("browsers : \"msie,gecko,opera\",");
      /*
           out.println("relative_urls : false,");
           out.println("verify_html : false,");
            */
           //out.println("valid_elements : \"*[*]\",");
           out.println("element_format : \"html\","); // xhtml is the default
           out.println("mode : \"textareas\",");
           out.println("height : \"400\",");
           //out.println("force_p_newlines : false,");
           out.println("theme : \"advanced\",");
           out.println("plugins : \"safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager,autoresize\",");

           //
           // Theme options - these are the default buttons that came with the product (refer to our custom button rows directly below)
           /*
           out.println("theme_advanced_buttons1 : \"save,newdocument,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,styleselect,formatselect,fontselect,fontsizeselect\",");
           out.println("theme_advanced_buttons2 : \"cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,|,insertdate,inserttime,preview,|,forecolor,backcolor\",");
           out.println("theme_advanced_buttons3 : \"tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,iespell,media,advhr,|,print,|,ltr,rtl,|,fullscreen\",");
           out.println("theme_advanced_buttons4 : \"insertlayer,moveforward,movebackward,absolute,|,styleprops,spellchecker,|,cite,abbr,acronym,del,ins,attribs,|,visualchars,nonbreaking,template,blockquote,pagebreak,|,insertfile,insertimage\",");
           */

           // Theme options
           out.println("theme_advanced_buttons1 : \"save,|,cut,copy,paste,pastetext,pasteword,|,search,replace,|,undo,redo,|,tablecontrols,|,removeformat,visualaid,|,charmap,insertdate,inserttime,hr,advhr,|,print,|,ltr,rtl,|,insertlayer,moveforward,movebackward,absolute,|,iespell,spellchecker,|,fullscreen\",");
           out.println("theme_advanced_buttons2 : \"formatselect,fontselect,fontsizeselect,styleprops,|,bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote,|,sub,sup,|,link,unlink,anchor,image,insertimage,|,cleanup,code,preview\",");
           out.println("theme_advanced_buttons3 : \"\",");
           out.println("theme_advanced_buttons4 : \"\",");   // we squeezed all desired buttons onto 2 rows - leave 3 & 4 blank (must be defined here to avoid the default)  

           out.println("theme_advanced_toolbar_location : \"top\",");
           out.println("theme_advanced_toolbar_align : \"left\",");
           out.println("theme_advanced_resizing : true,");
           //out.println("theme_advanced_statusbar_location : \"bottom\",");      // we don't need to show the file location info

           if (!css_includes.equals("")) {
               out.println("content_css: \"" + css_includes + "\",");
           }


           // Drop lists for link/image/media/template dialogs
           out.println("template_external_list_url : \"js/template_list.js\",");
           out.println("external_link_list_url : \"js/link_list.js\",");
           out.println("external_image_list_url : \"js/image_list.js\",");
           out.println("media_external_list_url : \"js/media_list.js\",");

           // Replace values for the template plugin
           out.println("template_replace_values : {");
           out.println("username : \"Some User\",");
           out.println("staffid : \"991234\"");
           out.println("}");

           out.println("});");

           out.println("</script>");
           
       }
       
      out.println("<style type=\"text/css\"> body {text-align:center} </style>");      // so body will align on center
      
      out.println("</head>");
      out.println("<body class=\"white_center\" bgcolor=\"#FFFFFF\" text=\"#000000\">");

      if (!user.equals(DINING_USER)) {
         
         SystemUtils.getProshopSubMenu(req, out, lottery);
         
      }
      
      StringBuilder file_contents = new StringBuilder();

      //
      //   Get the announcement page
      try {
          path = req.getRealPath("");
          f = new File(path + "/announce/" +club+ "/" + filename);
          fr = new FileReader(f);
          br = new BufferedReader(fr);
          if (!f.isFile()) {
              // do nothing
          }
      }
      catch (FileNotFoundException e) {
          if (bio_id > 0) {
              out.println("Creating New Bio Page");
          } else {
              out.println("<br><br><p align=center>Missing Announcement Page.</p>");
              out.println("</body></html>");
              return;
          }
      }
      catch (SecurityException se) {
          out.println("<br><br><p align=center>Access Denied.</p>");
          out.println("</body></html>");
          return;
      }

      try {

          while( (tmp = br.readLine()) != null )
              file_contents.append(tmp);
          
      } catch (Exception ignore) {}

      try { br.close(); }
      catch(Exception ignore) { }
      
      try { fr.close(); }
      catch(Exception ignore) { }

      br = null;
      fr = null;
      f = null;
      
      //out.println("<div style=\"width:" + ((new_skin || user.equals(DINING_USER)) ? "1000px" : "100%") + ";height:80%;margin:0px auto;\">");

      if (new_skin) {
          
          out.println("<center><br><table width=\"810\" align=\"center\" border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"8\"><tr><td align=\"center\" border=\"0\">");
          out.println("<p align=\"left\">&nbsp;&nbsp;Use the Editor to make changes, then click on 'Save Changes' below.</p><BR><HR align=\"center\" width=\"90%\"></HR><BR>");
          out.println("<p align=\"left\"><strong>&nbsp;&nbsp;Helpful Hint: </strong>&nbsp;<font color=\"red\">To keep the editing tools/toolbar in place as you scroll down the page, we recommend you use the " +
                      "<BR>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;full screen mode when editing this page.&nbsp;&nbsp;</font>" +
                      "<br>&nbsp;&nbsp;To enter Full Screen Mode, click on the <img src=\"/" + rev + "/assets/images/editor_fullscreen_button.png\" width=\"24\" height=\"22\" border=\"0\" alt=\"Full Screen\"> image located at the " +
                      "far right end of the top row of icons in the toolbar below. &nbsp;<BR>&nbsp;&nbsp;Once in Full Screen Mode, click on the " +
                      "<img src=\"/" + rev + "/assets/images/editor_save_button.png\" width=\"24\" height=\"25\" border=\"0\" alt=\"Save\"> image located at the " +
                      "far left of the top row of icons to Save your changes.</p>");
          out.println("</td></tr></table></center>");
      
      } else {
          
          out.println("<p align=center><BR>Use the Editor to make changes, then click on 'Save Changes' below.</p>");
      }
      out.println("<p align=center>Click here to view a <strong><a href=\"/" +rev+ "/web%20utilities/tiny_mce/TinyMCE-User-Guide.pdf\" target=\"_blank\">User Guide</a></strong></p>");
      
      
      
      // Filter file contents, removing linebreaks, excess comments, etc.
      Pattern file_filter = Pattern.compile("[\\n\\r]*<!-- SAVED BY [^/]*-->[\\n\\r]*$",Pattern.DOTALL | Pattern.MULTILINE);
      Matcher comment_match = file_filter.matcher(file_contents);
      String comments = "";
      StringBuilder new_comments = new StringBuilder();
      if (comment_match.find()) {
        comments = comment_match.group();
        String[] comment_a = comments.split("( -->[\\n\\r]*<!-- |[\\n\\r]*<!-- | -->[\\n\\r]*)");
        List<String> comment_list = new ArrayList<String>();
        for(int i = 0; i < comment_a.length; i++){
            String temp_comment = comment_a[i].trim();
            if(temp_comment != null && !temp_comment.equals("")){
                comment_list.add(temp_comment);
            }
        }
        int max_comments = 3;
        int comment_start = comment_list.size() - max_comments;
        if(comment_start<0) comment_start = 0;
        for(String comment:comment_list.subList(comment_start, comment_list.size())){
            new_comments.append("<!-- ");
              new_comments.append(comment);
              new_comments.append(" -->");
        } 
      }
      
      out.println("<form id=\"frmEditor\" name=\"frmEditor\" action=\"Proshop_announce\" method=\"post\">");
      
      if (bio_id > 0) {
          out.println("<input type=\"hidden\" name=\"probio\" value=\"\">");
          out.println("<input type=\"hidden\" name=\"proid\" value=\"" + bio_id + "\">");
      }
      
      // if editing a staing file then pass the name of it forward
      if (req.getParameter("file") != null) {

          out.println("<input type=\"hidden\" name=\"file\" value=\"" + filename + "\">");

      }

      out.println("<div class=\"editor_container\" style=\"width:1000px;margin:auto\">"); // height:1024px;

      //if (new_skin || user.equals(DINING_USER)) {

          // new skin
      

      if (new_skin || user.equals(DINING_USER) || user.equals("proshopfb")) {

          // new skin
          out.println("<textarea class=\"text_editor advanced\" name=\"content\" data-ftjson=\""+StringEscapeUtils.escapeHtml(gson_obj.toJson(editor_options))+"\">"+comment_match.replaceFirst(new_comments.toString())+"</textarea>"); //  align=\"center\"  height:100%;
          //out.println("<textarea name=\"content\" style=\"width:100%;height:0px;margin:auto\">"); //  align=\"center\"  height:100%;

      } else {

          // old skin
          out.println("<textarea name=\"content\" style=\"width:100%;height:80%\">"+comment_match.replaceFirst(new_comments.toString())+"</textarea>");

      }

      out.println("</div><!-- closing editor_container -->");

      out.println("<br><br>");
      
      out.println("</form>");

      out.println("<div style=\"margin:auto; text-align:center\">");

          out.println("<span><button value=\" Save Changes \" style=\"background-color: #8B8970; padding:4px; width:200px;\" " +
                  "onclick=\"document.forms['frmEditor'].submit();\"> Save Changes </button></span>");

          if (!user.equals(DINING_USER)) {

             // EVERYTHING BUT DINING
             out.println("<span><form method=get action=\"Proshop_announce\" style=\"display:inline\">");
             out.println("<input type=submit value=\" Cancel - Return w/o Changes \" style=\"background-color: #8B8970; padding:4px\">");
             out.println("</form></span>");

          } else {

             // DINING
             out.println("<span>");
             out.println("<form method=\"post\" action=\"Proshop_announce?menu=yes\" style=\"margin: 0; padding: 0\"><td align=\"right\">" +
                             "<input type=\"submit\" style=\"background:#8B8970; padding:4px; display:inline\" value=\" Return w/o Changes \" " +
                              "onclick=\"return confirm('Are you sure you want to return without saving your changes?')\">" +
                         "</form>>");
             out.println("</span>");
          }

      out.println("</div>");
      
/*
      out.println("<table width=\"300\" border=\"0\" align=\"center\"><tr><td align=\"left\">");     
      out.println("<input type=submit value=\" Save Changes \" style=\"background-color: #8B8970; padding:4px\">");
      out.println("");
      out.println("</td></form>");     
      
      if (!user.equals(DINING_USER)) {
      
         out.println("<td align=\"right\">");     
         out.println("<form method=get action=\"Proshop_announce\">");
         out.println("<input type=submit value=\" Cancel - Return w/o Changes \" style=\"background-color: #8B8970; padding:4px\">");
      
      } else {
         out.println("<form method=\"post\" action=\"Proshop_announce?menu=yes\"><td align=\"right\">");     
         //out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Return w/o Changes \" onClick=\"window.location.href='Proshop_announce?menu=yes'\" alt=\"Close\">");
         out.println("<input type=\"submit\" style=\"background:#8B8970; padding:4px\" value=\" Return w/o Changes \" onclick=\"return confirm('Are you sure you want to return without saving your changes?')\">"); // onClick='self.close()'
      }
      
      out.println("</td></form></tr></table>");
*/
      //out.println("</div>");

      out.println("<br>");

      out.println("</body></html>");

      out.close();

      return;

    } // end of IF tinyMCE




    boolean new_skin = (user.equals(DINING_USER)) || Utilities.isNewSkinActive(club, SystemUtils.getCon(session));
    
    //
    //  Call is to display the announcement page.
    //
    if (new_skin) out.println("<!doctype html>");
    out.println("<html lang=\"en-US\">\n<head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    if (!new_skin) out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<title> \"ForeTees " + ((user.equals(DINING_USER)) ? "Dining" : "Proshop") + " Announcement Page\"</title>");
    

    if (new_skin || user.equals(DINING_USER)) {
        
        out.println( Common_skin.getScripts(club, activity_id, session, req, false));
        /*
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/assets/stylesheets/sitewide.css\" type=\"text/css\">"); // use base stylesheet

        if (user.equals(DINING_USER) || user.equals("proshopfb")) {

            out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/assets/stylesheets/sitewide_dining.css\" type=\"text/css\">"); // use dining stylesheet

        } else if (activity_id > 0) {

            out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/assets/stylesheets/sitewide_" +activity_id+ ".css\" type=\"text/css\">"); // use the activity specific stylesheet
        }
         * 
         */
    } else {
        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web%20utilities/foretees.js\"></script>");
    }


   out.println("<style type=\"text/css\"> body {text-align: center} </style>");      // so body will align on center
   
   out.println("</head>");

   if (club.equals("claremontcc")) {
       out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" alink=\"#FFFF99\" vlink=\"#FFFF99\" link=\"#FFFF99\">");
   } else {
       out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   }
   
   if (!user.equals(DINING_USER)) {

      SystemUtils.getProshopSubMenu(req, out, lottery);
   }
  
   out.println("<div style=\"margin:0px auto;\" align=\"center\">");
   
   if (user.equals("proshop4tea")) {    // add club selection drop-down
      
      out.println("<p><br>");    //  add drop down list of all clubs - for switch

      out.println("<form action=\"/" +rev+ "/servlet/Proshop_announce\" method=\"get\" name=\"cform\">");
      out.println("<input type=\"hidden\" name=\"clubswitch\" value=\"1\">");

      out.println("<br><b>Club:</b>&nbsp;&nbsp;</span>");

      out.println("<select size=\"1\" name=\"club\" onChange=\"document.cform.submit()\">");

      String clubname = "";
      String fullname = "";
      int inactive = 0;

      Connection con2 = null;

      try {
         con2 = dbConn.Connect(rev);           // get connection to the Vx db

         //
         //  Get the club names for each club
         //
         //
         //  Get the club names for each TPC club
         //
         PreparedStatement pstmt = con2.prepareStatement("SELECT clubname, fullname, inactive FROM clubs ORDER BY fullname");

         pstmt.clearParameters();
         ResultSet rs = pstmt.executeQuery();

         while (rs.next()) {

               clubname = rs.getString("clubname");             // get the club's site name             
               fullname = rs.getString("fullname");             // get the club's full name
               inactive = rs.getInt("inactive");                // get inactive flag

               if (clubname.equals(club)) {
                  if (inactive > 0) {                    
                     out.println("<option selected value=\"" + clubname + "\">" + fullname + " - " + clubname + " (INACT)</option>"); 
                  } else {
                     out.println("<option selected value=\"" + clubname + "\">" + fullname + " - " + clubname + "</option>"); 
                  }
               } else {
                  if (inactive > 0) {                    
                     out.println("<option value=\"" + clubname + "\">" + fullname + " - " + clubname + " (INACT)</option>"); 
                  } else {
                     out.println("<option value=\"" + clubname + "\">" + fullname + " - " + clubname + "</option>"); 
                  }
               }
         }  
         pstmt.close();

      }
      catch (Exception e) {

      } finally {

         try { con2.close(); }
         catch (SQLException ignored) {}
      }          

      out.println("</select><br /></p></form>");
   }
      
   if (club.startsWith("demo") && user.equals("proshop1")) {

       out.println("<p>CURRENT CLUB: " + club + "</p>");

       out.println("<a href=\"Proshop_announce?clubswitch=1&club=demobrad\" target=_top>demobrad</a><br>");
       out.println("<a href=\"Proshop_announce?clubswitch=1&club=demobrock\" target=_top>demobrock</a><br>");
       out.println("<a href=\"Proshop_announce?clubswitch=1&club=demoedu1\" target=_top>demoedu1</a><br>");
       out.println("<a href=\"Proshop_announce?clubswitch=1&club=demolarry\" target=_top>demolarry</a><br>");
       out.println("<a href=\"Proshop_announce?clubswitch=1&club=demopaul\" target=_top>demopaul</a><br>");
       out.println("<a href=\"Proshop_announce?clubswitch=1&club=demoracquet\" target=_top>demoracquet</a><br>");
       out.println("<a href=\"Proshop_announce?clubswitch=1&club=demotemp\" target=_top>demotemp</a><br>");
       out.println("<a href=\"Proshop_announce?clubswitch=1&club=demotom\" target=_top>demotom</a><br>");
       out.println("<a href=\"Proshop_announce?clubswitch=1&club=demov4\" target=_top>demov4</a>");

   } else {

       if (user.equals(DINING_USER) || new_skin || user.equals("proshopfb")) out.println(diningPrefix);

       try {
           path = req.getRealPath("");

           f = new File(path + "/announce/" +club+ "/" + getFileName(activity_id, user, club, "", false));
           fr = new FileReader(f);
           br = new BufferedReader(fr);
           if (!f.isFile()) {
               // do nothing
           }
       }
       catch (FileNotFoundException e) {
           out.println("<br><br><p align=center>Missing Announcement Page.</p>");
           out.println("</div></BODY></HTML>");
           return;
       }
       catch (SecurityException se) {
           out.println("<br><br><p align=center>Access Denied.</p>");
           out.println("</div></BODY></HTML>");
           return;
       }

       out.println("<div class=\"announcement_container\">");
       out.println("<!-- BEGIN INSERT -->");
       
       while( (tmp = br.readLine()) != null )
           out.println(tmp);

       out.println("<!-- END INSERT -->");
       out.println("<div class=\"clearfloat\"></div>"); // force floats to clear in case annoucement page doesn't
       out.println("</div>");


       try {
           br.close();
           fr.close();
       } catch(Exception ignore) {
           // do nothing
       } finally {
           br = null;
           fr = null;
           f = null;
       }

       if (user.equals(DINING_USER) || new_skin || user.equals("proshopfb")) out.println(diningSuffix);

       /*
       out.println("<iframe frameborder=\"0\" class=\"announce\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"auto\" height=\"100%\" width=\"100%\" src=\"/" +rev+ "/announce/" +club+ "_announce.htm\">");
       out.println("<!-- Alternate content for non-supporting browsers -->");
       out.println("<H2>The browser you are using does not support frames</H2>");
       out.println("</iframe>");
       */
   }
   out.println("</div></body></html>");
   
 }  // end of doGet

 
 
/*
 private String getFileName(int activity_id, String user, String club, boolean bak) {
    
    //String filename = getFileName(activity_id, user, club, "", bak);
    return Utilities.getAnnouncementPageFileName(activity_id, user, club, "", bak);
 }
*/

 private String getFileName(int activity_id, String user, String club, String timestamp, boolean bak) {


    return Utilities.getAnnouncementPageFileName(activity_id, user, club, timestamp, bak);

 }


 private void manageVersions(HttpServletRequest req, HttpServletResponse resp, String club, String user, int activity_id, Connection con, PrintWriter out) {


    if (req.getParameter("new") != null) {

        manageListNewTemplates(req, resp, club, user, activity_id, con, out);

    } else if (req.getParameter("view") != null) {

        backupsPreview(req, resp, club, user, activity_id, con, out);

    } else if (req.getParameter("publish") != null && !req.getParameter("publish").equals("")) {

        managePublishFile(req, resp, club, user, activity_id, con, out);

    } else if (req.getParameter("edit") != null) {


        //backupsDelete(req, resp, club, out);
        //manageListStagingPages(req, resp, out);

    } else if (req.getParameter("list") != null) {

        manageListStagingPages(req, resp, club, user, activity_id, con, out);

    } else if (req.getParameter("select") != null) {

        manageCreateNewStagingPage(req, resp, club, user, activity_id, con, out);

    } else if (req.getParameter("delete") != null) {

        manageDeleteStagingPage(req, resp, club, user, activity_id, con, out);

    } else {

        out.println("<p>NO ACTION PASSED</p>");
/*
        // display manage announcement menu
        out.println("<font color=\"#000000\" size=\"3\">");
        out.println("<center><b>Announcement Page Management</b></center>");
        out.println("</font>");

        out.println("<br><br>");
        out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\"><tr><td align=center><br>");

        out.println("<a href=\"Proshop_announce?manage&new\" target=\"bot\">Stage New Announcement</a><br><br>");

        out.println("<a href=\"Proshop_announce?manage&list\" target=\"bot\">List Annoucement Pages in Staging</a><br>");

        out.println("</td></tr>");
        out.println("</table>");
 */
    }

 }



 private void managePublishFile(HttpServletRequest req, HttpServletResponse resp, String club, String user, int activity_id, Connection con, PrintWriter out) {


    startPage(req, resp, "Publishing New Announcement Page", out);

    String publish_filename = scrubFileName(req.getParameter("publish"));

    GregorianCalendar cal = new GregorianCalendar();
    int day = cal.get(cal.DAY_OF_MONTH);
    int month = cal.get(cal.MONTH) + 1;
    int year = cal.get(cal.YEAR);
    int hr = cal.get(cal.HOUR_OF_DAY);
    int min = cal.get(cal.MINUTE);
    int date = (year * 10000) + (month * 100) + day;

    String timestamp = date + "T" + SystemUtils.ensureDoubleDigit(hr) + SystemUtils.ensureDoubleDigit(min);
    
    String path = req.getRealPath("");
    String tmp = "/announce/";

    int fail = 0;
    int backup_fail = 0;
    boolean copy_ok = false;

    String announce_file   = path + tmp + club + folder + getFileName(activity_id, user, club, "", false);
    String announce_backup = path + tmp + club + folder + getFileName(activity_id, user, club, timestamp, true);

    // overwrite the full path to announcement file and skip the backup
    String publish_filepath = path + tmp + club + folder + publish_filename;

    // make back up copy
    try {

        File f = new File(announce_file);

        if (f.exists()) {
            f.renameTo(new File(announce_backup));
        }

    } catch (Exception ignore) {

        backup_fail = 1;

    }

    if (backup_fail == 0) {

        copy_ok = Utilities.copyFile(publish_filepath, announce_file);

        /*
        // make staging file the live file
        try {

            File f = new File(publish_filepath);

            if (f.exists()) {
                f.renameTo(new File(announce_file));
            }

        } catch (Exception ignore) {

            fail = 1;

        }
    */
    }

    out.println("<div style=\"width:60%;margin:auto;text-align:center\">");

    if (backup_fail == 1) {

        // backup failed and did not publish
        out.println("Sorry but we encountered an error trying to backup your existing Announcement page so the system aborted the publish command.  Please try again and let prosupport@foretees.com know if this problem continues.");

    } else if (!copy_ok) {

        // backed up but publish failed
        out.println("Sorry but we encountered an error trying to copy your staging file to your live Announcement page.  Please try again and let prosupport@foretees.com know if this problem continues.");

    } else {

        // OK
        out.println("Success! We've backed up your previous Announcement page and copied the staging file as your new live Annoumcement page.");

    }

    out.println("<br><br>");
    
    out.println("<form method=\"post\" action=\"Proshop_announce?menu=yes\"><input type=\"submit\" style=\"background:#8B8970\" value=\" Return \" alt=\"Return\">");

    out.println("</div>");

 }


 private void manageDeleteStagingPage(HttpServletRequest req, HttpServletResponse resp, String club, String user, int activity_id, Connection con, PrintWriter out) {


    String file = (req.getParameter("file") != null) ? req.getParameter("file") : "";

    File f;
    String tmp = "";
    String path = "";

    try {

       path = req.getRealPath("");
       tmp = "/announce/" + club + "/" + file;
       f = new File(path + tmp);
       if (f.isFile()) f.delete();

    } catch (Exception e) {
       out.println("<br><br><p align=center>Staging file \"" + file + "\" is in use by another user or has already been deleted.</p>");
       out.println("<p align=center>Error: " + e.toString() + "</p>");
       out.println("<p align=center>Please contact support if problem persists.</p>");
       out.println("</BODY></HTML>");
       return;
    }

    out.println("<br><center><h2><span style=\"background-color:yellow\">Your staging file \"" + file + "\" has been successfully removed.</font></h2></center><br>");


    manageListStagingPages(req, resp, club, user, activity_id, con, out);

}


 private void manageCreateNewStagingPage(HttpServletRequest req, HttpServletResponse resp, String club, String user, int activity_id, Connection con, PrintWriter out) {


    //
    // first check the zip code in club5
    //
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;

    String zipcode = (req.getParameter("zipcode") != null) ? req.getParameter("zipcode") : "";

    if (!zipcode.equals("")) {

        try {

            pstmt = con.prepareStatement( "UPDATE club5 SET zipcode = ?" );

            pstmt.clearParameters();
            pstmt.setString(1, zipcode);
            pstmt.executeUpdate();

        } catch (Exception exc) {

            Utilities.logError("manageCreateNewStagingPage: Error updating zipcode. club=" + club + ", err=" + exc.toString());

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

    } else {

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery( "SELECT zipcode FROM club5 WHERE clubName <> '';" );

            if (rs.next()) zipcode = rs.getString(1);

        } catch (Exception exc) {

            Utilities.logError("manageCreateNewStagingPage: Error looking up zipcode. club=" + club + ", err=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

    }

    startPage(req, resp, "Creating New Announcement Page", out);

    String filename = scrubFileName(req.getParameter("select"));
    String path = req.getRealPath("") + "/announce/";
    //String staging_name = filename.replaceAll(".htm", staging_ext);


    String staging_name = (req.getParameter("staging_name") != null) ? req.getParameter("staging_name") : "";


    if (!staging_name.equals("") && !zipcode.equals("")) {
        
        staging_name = scrubFileName(staging_name);
        staging_name = staging_name.replace(" ", "_");
        staging_name = staging_name.replace(".", "_");

        if (!user.equals(DINING_USER)) {
            if (activity_id > 0) staging_name += "_" + activity_id;
        } else {
            staging_name += "_dining";
        }
        // name is here - now check to see if name already exists
        File f = new File(path + club + folder + staging_name + staging_ext);

        out.println("<div style=\"width:65%;margin:auto;text-align:center\">");

        if (f.isFile()) {
            
            out.println("<p><font color=red>File with the name \"" + staging_name + "\" already exists.</font></p>");
            
        } else {
            
            // name is unique - permform copy
            boolean copy_ok = Utilities.copyFile(path + "announcement_templates/" + filename, path + club + folder + staging_name + staging_ext);
            
            if (copy_ok) {
                
                out.println("<p>New announcement page has been staged for you to work on.</p>");

                out.println("<a href=\"Proshop_announce?tinymce&file=" + staging_name + staging_ext + "\" class=\"fileLink\" style=\"font-size:11pt\">Edit New Page Now</a><br><br>");

            } else {
                
                out.println("<p>We encountered an error trying to create the new announcement page for you.</p>");
            }

            out.println("<form method=\"post\" action=\"Proshop_announce?menu=yes\"><input type=\"submit\" style=\"background:#8B8970\" value=\" Return \" alt=\"Return\">");

            out.println("</div>");
            return;
        }

        out.println("</div>");
        
    }

    // if we're still here then we need a unique filename or their zip code

    out.println("<div style=\"width:65%;margin:auto;text-align:center\">");

    out.println("Please provide a name for your new announcement page.  Please provide only a name and not an extention. ");
    if (zipcode.equals("")) {
        out.print("It also appears you do not have a zip code specified within your club's configuration. Please provide one now so the weather feature functions properly.");
    }
    out.println("<br><br>");

    out.println("<form method=\"get\" action=\"Proshop_announce\" id=\"frmStagingName\">");
    
    out.println("<input type=\"hidden\" name=\"manage\" value=\"\">");
    out.println("<input type=\"hidden\" name=\"select\" value=\"" + filename + "\">");

    out.println("<table align=\"center\"><tr><td>");
    out.println("Filename:&nbsp; <input type=\"text\" name=\"staging_name\" value=\""+ staging_name + "\" size=\"25\">&nbsp; &nbsp;<font size=2>(do not include an extention such as .htm or .html)</font><br><br>");
    out.println("</td></tr>");
    
    if (zipcode.equals("")) {
        out.println("<tr><td>");
        out.println("Zip Code:&nbsp; <input type=\"text\" name=\"zipcode\" value=\"" + zipcode + "\" size=\"15\"><br><br>");
        out.println("</td></tr>");
    }

    out.println("<tr><td align=\"center\">");
    out.println("<input type=\"submit\" name=\"submit\" value=\"Create\">");
    out.println("</td></tr></table>");

    out.println("</form>");

    out.println("</div>");
     
 }


 private void startPage(HttpServletRequest req, HttpServletResponse resp, String title, PrintWriter out) {


    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder

    if (session == null) return;

    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);

    out.println(SystemUtils.HeadTitle(""));
    out.println("<style type=\"text/css\">");
    out.println(".fileLink {color:darkBlue; font-size: 10pt}");
    out.println("</style>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<br>");
    out.println("<font color=\"#000000\" size=\"3\">");
    out.println("<div style=\"margin:auto;width:75%;text-align:center\"><b>" + title + "</b></div>");
    out.println("</font><br>");

 }

 
@SuppressWarnings({"unchecked","deprecation"})
 private void manageListNewTemplates(HttpServletRequest req, HttpServletResponse resp, String club, String user, int activity_id, Connection con, PrintWriter out) {
     
     
     // list available templates from announce_template dir
     //
     // provide preview and select links
     

    startPage(req, resp, "Select New Announcement Page To Begin", out);

    /*
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder

    if (session == null) return;

    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    out.println(SystemUtils.HeadTitle(""));
    out.println("<style type=\"text/css\">");
    out.println(".fileLink {color:darkBlue; font-size: 10pt}");
    out.println("</style>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

    out.println("<br>");
    //out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");

    //out.println("<tr><td align=\"center\" valign=\"top\" bgcolor=\"#336633\">");
    out.println("<font color=\"#000000\" size=\"3\">");
    out.println("<center><b>Announcement Page Management</b></center><br>");
    
    out.println("</font>");
    //out.println("</font></td></tr>");
    */

    out.println("<div style=\"margin:auto;width:70%;text-align:center\">Listed below are the different templates you can use to start a new Announcement page.  Use the links to the right of the names " +
                "to preview and select the template you wish to use.  When you click 'Select' you will be asked to provide a name for the new Announcement page.</div>");

    //out.println("</table>");
    
    String announce_tmpl_path = req.getRealPath("") + "/announce/announcement_templates";

    boolean z = false;

    File dir = new File( announce_tmpl_path );
    File[] files = dir.listFiles();

    FilenameFilter ff = null;

    ff = new FilenameFilter() {
        public boolean accept (File b, String name) {         
            return (name.endsWith (".htm") == true);
        }
    };
    
    files = dir.listFiles(ff);
/*
    // Sort files by name
    Arrays.sort(files, new Comparator()
    {
        @Override
        public int compare(Object o1, Object o2)
        {
        return ((File) o2).getName().compareTo(((File) o1).getName());
        }
    });
*/

    out.println("<br><br>");
    out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\">");

    for (File f : files) {
        
        out.println("<tr bgcolor=\"" + ((z=z==false) ? "#FFFFFF" : "#F5F5DC") + "\">");
        out.print("<td class=\"fileLink\">" + scrubNameForDisplay(f.getName()) + "</td>");
        out.print("<td><a href=\"Proshop_announce?manage&preview&tmpl&view=" + f.getName() + "\" class=\"fileLink\" style=\"font-size:10pt\" target=\"_preview\">Preview</a></td>");
        out.print("<td><a href=\"Proshop_announce?manage&select=" + f.getName() + "\" class=\"fileLink\" style=\"font-size:10pt\">Select</a></td>");
        out.print("</tr>");
        
    }

    out.println("</table>");

    out.println("<br><br><center>");
     if (!user.equals(DINING_USER)) {
        //out.println("<form method=get action=\"Proshop_announce\">");
        //out.println("<input type=submit value=\" Home \" style=\"background-color:#8B8970\">");
        out.println("<form method=\"post\" action=\"Proshop_announce?menu=yes\"><input type=\"submit\" style=\"background:#8B8970\" value=\" Return \" alt=\"Return\">");
     } else {
        out.println("<form method=\"post\" action=\"Proshop_announce?menu=yes\"><input type=\"submit\" style=\"background:#8B8970\" value=\" Return \" alt=\"Return\">");
     }        
    out.println("</form>");
    out.println("</center>");
    
     
 }


private static String scrubFileName(String f) {

    // quick sanity check for now - just remove any directory movement or folder indicators
    String result = f.replace("/", "");
    result = result.replace("..", "");
    result = result.replace("\"", " ");

    return result;

}


private static String scrubNameForDisplay(String f) {

    // quick sanity check for now - just remove any directory movement or folder indicators
    String result = f.replace("_dining", "");
    result = result.replace(staging_ext, "");
    result = result.replace(".htm", "");
    result = result.replace("_", " ");

    return result;

}


@SuppressWarnings({"unchecked","deprecation"})
 private void manageListStagingPages(HttpServletRequest req, HttpServletResponse resp, final String club, String user, final int activity_id, Connection con, PrintWriter out) {


    boolean new_skin = Utilities.isNewSkinActive(club, con);

    startPage(req, resp, "Select An Announcement Page To Manage", out);

/*
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder

    if (session == null) return;

    String user = (String)session.getAttribute("user");
    final String club = (String)session.getAttribute("club");             // get club name
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    final int activity_id = (Integer)session.getAttribute("activity_id");

    out.println(SystemUtils.HeadTitle(""));
    out.println("<style type=\"text/css\">");
    out.println(".fileLink {color:darkBlue; font-size: 10pt}");
    out.println("</style>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

    out.println("<br>");
    //out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");

    //out.println("<tr><td align=\"center\" valign=\"top\" bgcolor=\"#336633\">");
    out.println("<font color=\"#000000\" size=\"3\">");
    out.println("<center><b>Announcement Page Management</b></center>");

    out.println("</font>");
    //out.println("</font></td></tr>");
*/

    out.println("<div style=\"width:70%;margin:auto;text-align:center\">Below you will find a listing of all your existing Announcement pages that are being staged. " +
                "You can edit, view or publish them by clicking the links to the right of each page name. Select <b>Edit</b> to make changes to the desired page. " +
                "Select <b>Preview</b> to view the page in a new window or tab. Selecting <b>Publish</b> will cause the current Announcement page to be replaced by this staging page.</div>");
    //out.println("</table>");

    String path = req.getRealPath("");
    String tmp = "/announce/" + club + "/";
    String announce_path = path + tmp;


    boolean z = false;

    /*
    StringTokenizer tok = null;
    String sdate = "";
    String stime = "";
    String part = "";
    int time = 0;
    int date = 0;
    int yy = 0;
    int mm = 0;
    int dd = 0;
    */

    File dir = new File( announce_path );
    File[] files = dir.listFiles();

    FilenameFilter ff = null;

    if (!user.equals(DINING_USER)) {

        ff = new FilenameFilter() {
            public boolean accept (File b, String name) {
                return (name.endsWith (((activity_id == 0) ? "" : "_" + activity_id) + staging_ext));
            }
        };

    } else {      // ForeTees Dining Admin user

        ff = new FilenameFilter() {
            public boolean accept (File b, String name) {
                return (name.endsWith (staging_ext) && name.startsWith(club + "_announce_dining") == true);
            }
        };
    }

    files = dir.listFiles(ff);

    // Sort files by name
    Arrays.sort(files, new Comparator()
    {
        @Override
        public int compare(Object o1, Object o2)
        {
        return ((File) o2).getName().compareTo(((File) o1).getName());
        }
    });


    out.println("<br><br>");
    out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\">");

    for (File f : files) {

        tmp = f.getName();
/*
        if (club.equals("palmvalley-cc")) {  // needs custom handling due to the "-" in their clubname

            tok = new StringTokenizer(tmp, "_");
            tok.nextToken(); // eat clubname portion
            tmp = tok.nextToken();
        }

        tok = new StringTokenizer( tmp, "-" );
        part = tok.nextToken();
        part = tok.nextToken();
        tok = new StringTokenizer( part, "T" );
        sdate = tok.nextToken();
        part = tok.nextToken();
        tok = new StringTokenizer( part, "." );
        stime = tok.nextToken();

        try {
            time = Integer.parseInt(stime);
            date = Integer.parseInt(sdate);
        } catch (Exception ignore) {
        } finally {
            stime = SystemUtils.getSimpleTime(time);
        }

        yy = date / 10000;
        mm = date - (yy * 10000);
        dd = mm - ((mm / 100) * 100);
        mm = mm / 100;
*/

        out.println("<tr bgcolor=\"" + ((z=z==false) ? "#FFFFFF" : "#F5F5DC") + "\">");

        out.println("<td class=\"fileLink\">" + scrubNameForDisplay(tmp) + "</td>");
        out.println("<td><a href=\"Proshop_announce?tinymce&file=" + tmp + "\" class=\"fileLink\" style=\"font-size:10pt\">Edit</a></td>");
        out.println("<td><a href=\"Proshop_announce?manage&view=" + tmp + "\" class=\"fileLink\" style=\"font-size:10pt\" target=\"_preview\">Preview</a></td>");
        
        if (new_skin) {
            
            out.println("<td><a href=\"Proshop_announce?manage&publish=" + tmp + "\" class=\"fileLink\" style=\"font-size:10pt\" onclick=\"return confirm('Your current Announcement page will be backed up and replaced by this page.\\n\\n" + scrubNameForDisplay(tmp) + "\\n\\nShould you need to revert back to an older version, you can do so from the Manage Backup Copies page.\\n\\nAre you sure you want to publish this file?')\">Publish</a></td>");
        
        } else {
            
            out.println("<td><a href=\"#\" class=\"fileLink\" style=\"font-size:10pt\" onclick=\"alert('You can only publish Announcement pages once your club is using the new skin.\\n\\nTo switch to the new skin, select a desired date on the System Config &gt; Club Setup &gt; Club Options page\\nand your club will switch over to the new skin on the selected date.');return false\">Publish</a></td>");
         
        }
        out.println("<td><a href=\"Proshop_announce?manage&delete&file=" + tmp + "\" class=\"fileLink\" style=\"font-size:10pt\" onclick=\"return confirm('You are about to PERMENATLY delete a file! This action cannot be undone!\\n\\nAre you sure you want to delete this file?')\">Delete</a></td>");

        out.println("</tr>");

        //out.println("<tr><td class=\"fileLink\">"+ f.getName() +"</td></tr>");

    }

    out.println("</table>");

    out.println("<br><br><center>");
     if (!user.equals(DINING_USER)) {
        out.println("<form method=post action=\"Proshop_announce?menu=yes\">");
        out.println("<input type=submit value=\" Return \" style=\"background-color:#8B8970\">");
     } else {
        out.println("<form method=\"post\" action=\"Proshop_announce?menu=yes\"><input type=\"submit\" style=\"background:#8B8970\" value=\" Return \" alt=\"Close\">");
     }
    out.println("</form>");
    out.println("</center>");

 } // end manageListStagingPages


 private void manageBackups(HttpServletRequest req, HttpServletResponse resp, String club, String user, int activity_id, Connection con, PrintWriter out) {

    if (req.getParameter("view") != null) {
         
        backupsPreview(req, resp, club, user, activity_id, con, out);
         
    } else if (req.getParameter("restore") != null) {
         
        backupsRestore(req, resp, club, user, activity_id, out);

    } else if (req.getParameter("rm") != null) {

        backupsDelete(req, resp, club, out);
        backupsListing(req, resp, out);

    } else {

        backupsListing(req, resp, out);

    }

 }
  

 @SuppressWarnings("deprecation")
 private void backupsRestore(HttpServletRequest req, HttpServletResponse resp, String club, String user, int activity_id, PrintWriter out) {

/*

    int activity_id = (Integer)session.getAttribute("activity_id");
    String club = (String)session.getAttribute("club");
    String user = (String)session.getAttribute("user");
*/
    String file = (req.getParameter("restore") != null) ? req.getParameter("restore") : "";
    
    //String qry_bio = "";
     
    int id = 0;
    boolean bio = req.getParameter("probio") != null;
    if (bio) {

        HttpSession session = SystemUtils.verifyPro(req, out);           // check for intruder

        if (session == null) return;
        
        String proid = (String)session.getAttribute("proid");       // get lesson pro id
        id = Integer.parseInt(proid);                               // convert the proid
        //qry_bio = "&probio=" + id;
    }
    
    //final int bio_id = id;
    
    out.println("<p align=center>Restoring " + file + "</p>");
    
    String announce_file = ""; 
    String restore_file = "";
    
    File f;
    String tmp = "";
    String path = "";
    boolean restore_fail = false;
    String err = "";
    
    try {

        path = req.getRealPath("");
        tmp = "/announce/" + club + "/";
        
        if (bio) {
        
            announce_file = path + tmp + "_bio" + id + ".htm";
            
        } else {

            announce_file = path + tmp + "/" + getFileName(activity_id, user, club, "", false);

        }
        
        restore_file = path + tmp + file;

        //Utilities.logError("Proshop_announce: (Restoring) announce_file=" + announce_file + ", restore_file=" + restore_file);

        f = new File(announce_file);
        if (f.isFile()) f.delete();
       
    } catch (Exception e) {

        restore_fail = true;
        err = e.toString();
        Utilities.logError("Proshop_announce: announce_file=" + announce_file + ", restore_file=" + restore_file + ", err=" + err);
        /*out.println("<br><br><p align=center>Backup file " + file + " is missing or in use.</p>");
        out.println("<p align=center>Error: " + e.toString() + "</p>");
        out.println("<p align=center>Please contact support.</p>");
        out.println("</BODY></HTML>");
        return;*/
    }

    if (!restore_fail) {
        
        try {

            f = new File(restore_file);

            if (f.exists()) {
                f.renameTo(new File(announce_file));
            }

        } catch (Exception e2) {
            err = e2.toString();
            restore_fail = true;
        }
        
    }
    
    if (restore_fail) {
        
        out.println("<br><br><p align=center>Error restoring backup file.</p>");
        out.println("<p align=center>Please contact support.</p>");
        out.println("</BODY></HTML>");
        
    } else {
        
        out.println("<HTML><BODY>");
        if (bio) {
            out.println("<br><br><p align=center>Lesson Book Bio page restored.</p>");
        } else {
            out.println("<br><br><p align=center>Announcement page restored.</p>");
        }
        out.println("<br><br><center>");
        if (!user.equals(DINING_USER)) {
           out.println("<form method=get action=\"Proshop_announce\">");
           out.println("<input type=submit value=\" Home \" style=\"background-color: #8B8970\">");        
        } else {
           //out.println("<form><input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" value=\"Return\" onClick='self.close()' alt=\"Close\">");
           out.println("<form method=\"post\" action=\"Proshop_announce?menu=yes\"><input type=\"submit\" style=\"background:#8B8970\" value=\" Return \">"); // onClick='self.close()'

        }        
        out.println("</form>");
        out.println("</center>");
        out.println("</BODY></HTML>");
        
    }
    
    f = null;
    
 } // end backupsRestore
 

 @SuppressWarnings("deprecation")
 private void backupsDelete(HttpServletRequest req, HttpServletResponse resp, String club, PrintWriter out) {

/*
    HttpSession session = SystemUtils.verifyPro(req, out);           // check for intruder

    if (session == null) return;

    final String club = (String)session.getAttribute("club");             // get club name
*/
    String file = (req.getParameter("rm") != null) ? req.getParameter("rm") : "";

    File f;
    String tmp = "";
    String path = "";

    try {
        
       path = req.getRealPath("");
       tmp = "/announce/" + club + "/" + file;
       f = new File(path + tmp);
       if (f.isFile()) f.delete();
       
    } catch (Exception e) {
       out.println("<br><br><p align=center>Backup file " + file + " is missing or in use.</p>");
       out.println("<p align=center>Error: " + e.toString() + "</p>");
       out.println("<p align=center>Please contact support.</p>");
       out.println("</BODY></HTML>");
       return;
    }
     
     
    /*catch (FileNotFoundException e) {
       out.println("<br><br><p align=center>Backup file " + file + " is missing.</p>");
       out.println("</BODY></HTML>");
       return;
    }
    catch (SecurityException se) {
       out.println("<br><br><p align=center>Access Denied.  Please contact support.</p>");
       out.println("</BODY></HTML>");
       return;
    }*/
    
 } // end backupsDelete
 

 @SuppressWarnings("deprecation")
 private void backupsPreview(HttpServletRequest req, HttpServletResponse resp, String club, String user, int activity_id, Connection con, PrintWriter out) {
     

    boolean new_skin = Utilities.isNewSkinActive(club, con);

    if (req.getParameter("tmpl") != null) new_skin = true; // force new skin if viewing templates
    if (req.getParameter("manage") != null) new_skin = true; // force new skin if viewing staging

    String file = (req.getParameter("view") != null) ? req.getParameter("view") : "";

    HttpSession session = SystemUtils.verifyPro(req, out);
    
    if (new_skin || user.equals("proshopfb") || user.equals(DINING_USER)) out.println("<!doctype html>");
    out.println("<html lang=\"en-US\">");
    out.println("<head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
  //out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">"); // removed - now obsolute and lang is now declared on the root html element
    out.println("<title>ForeTees " + ((user.equals(DINING_USER)) ? "Dining" : "Proshop") + " Announcement Page</title>");
    //   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\">");


    if (new_skin || user.equals("proshopfb") || user.equals(DINING_USER)) {

        out.println(Common_skin.getScripts(club, activity_id, session, req, false));

    } else {

        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web%20utilities/foretees.js\"></script>");

    }
/*
    if (new_skin) {

        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/assets/stylesheets/sitewide.css\" type=\"text/css\">"); // use standard stylesheet

        if (user.equals(DINING_USER) || user.equals("proshopfb")) {

            out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/assets/stylesheets/sitewide_dining.css\" type=\"text/css\">"); // use standard dining stylesheet

        } else if (activity_id > 0) {

            out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/assets/stylesheets/sitewide_" +activity_id+ ".css\" type=\"text/css\">"); // use the activity specific stylesheet
        }

    }
*/
    out.println("<style type=\"text/css\"> body {text-align: center} </style>");      // so body will align on center

    out.println("</head>");

    if (club.equals("claremontcc")) {
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" alink=\"#FFFF99\" vlink=\"#FFFF99\" link=\"#FFFF99\">");
    } else {
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    }

    if (new_skin || user.equals(DINING_USER) || user.equals("proshopfb")) out.println("\n\n" + diningPrefix + "\n<!-- BEGIN INSERT -->\n");

    File f;
    FileReader fr;
    BufferedReader br;
    String tmp = "";
    String path = "";

    try {
        
       path = req.getRealPath("");
       if (req.getParameter("tmpl") != null) {
           tmp = "/announce/announcement_templates/" + file;
       } else {
           tmp = "/announce/" + club + "/" + file;
       }
       f = new File(path + tmp);
       fr = new FileReader(f);
       br = new BufferedReader(fr);
       if (!f.isFile()) {
           // throw error
       }

    }
    catch (FileNotFoundException e) {
       out.println("<br><br><p align=center>File " + path + tmp + " is missing.</p>");
       out.println("</BODY></HTML>");
       return;
    }
    catch (SecurityException se) {
       out.println("<br><br><p align=center>Access to file denied.  Please contact support.</p>");
       out.println("</BODY></HTML>");
       return;
    }

    out.println("<div class=\"announcement_container\">");
    out.println("<!-- BEGIN INSERT -->");

    try {
        
        while( (tmp = br.readLine()) != null )
           out.println(tmp);

        br.close();
        
    } catch (Exception exp) {
       out.println("<br><br><p align=center>File " + tmp + " could not be fully read.</p>");
       out.println("<p align=center>Error: " + exp.toString() + "</p>");
       out.println("<p align=center>Please contact support.</p>");
       out.println("</BODY></HTML>");
    }

    out.println("<!-- END INSERT -->");
    out.println("<div class=\"clearfloat\"></div>"); // force floats to clear in case annoucement page doesn't
    out.println("</div>");

    try {
        br.close();
        fr.close();
    } catch(Exception ignore) {
       // do nothing
    } finally {
        br = null;
        fr = null;
        f = null;
    }

    if (new_skin || user.equals(DINING_USER) || user.equals("proshopfb")) out.println("\n<!-- END INSERT -->\n" + diningSuffix + "\n\n");

    out.println("</div></body></html>");
   
} // end backupsPreview


@SuppressWarnings({"unchecked","deprecation"})
 private void backupsListing(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
     
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder

    if (session == null) return;

    String user = (String)session.getAttribute("user");
    final String club = (String)session.getAttribute("club");             // get club name
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    final int activity_id = (Integer)session.getAttribute("activity_id");
    String qry_bio = "";
     
    int id = 0;
    boolean bio = req.getParameter("probio") != null;
    if (bio) {

        String proid = (String)session.getAttribute("proid");       // get lesson pro id
        id = Integer.parseInt(proid);                               // convert the proid
        qry_bio = "&probio=" + id;
    }
    
    final int bio_id = id;
    
    out.println(SystemUtils.HeadTitle2(""));

    if (bio) {

       out.println("<script type=\"text/javascript\" src=\"/" + rev + "/assets/jquery/fancybox/jquery.fancybox-1.3.4.pack.js\"></script>");
       out.println("<script type=\"text/javascript\" src=\"/" + rev + "/assets/jquery/fancybox/jquery.easing-1.3.pack.js\"></script>");
       out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/assets/jquery/fancybox/jquery.fancybox-1.3.4.css\" type=\"text/css\" />");

       // Simulate TopUp's modal behavior
       out.println("<script>");
       out.println("$(document).ready(function() {");
       out.println(" $(\"[class^=tu_iframe_]\").click(function(event){");

       out.println(" var tu_obj = $(this);");
       out.println(" var tu_class_arr = tu_obj.attr(\"class\").split(\" \");");
       out.println(" var tu_height = 768;");
       out.println(" var tu_width = 1024;");
       out.println(" tu_obj.fancybox({");
       out.println("     'transitionIn':'elastic',");
       out.println("     'transitionOut':'elastic',");
       out.println("     'height':tu_height,");
       out.println("     'width':tu_width,");
       out.println("     'type':'iframe',");
       out.println("     'centerOnScroll':true,");
       out.println("     'changeSpeed':50,");
       out.println("     'margin':13,");
       out.println("     'padding':0");
       out.println(" });");

       out.println(" event.preventDefault();");
       out.println(" // For some reason, the first click fails. If this is the first try, send another click. ");
       out.println(" if(tu_obj.data('tu_click_track')!=true){");
       out.println("     tu_obj.data('tu_click_track',true)");
       out.println("     tu_obj.click();");
       out.println(" }");

       out.println("});");

       out.println("});");

       out.println("</script>");
    }

    out.println("<style type=\"text/css\">");
    out.println(".fileLink {color:darkBlue; font-size: 10pt}");
    out.println("</style>");
   out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

    out.println("<br>");
    //out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");

    //out.println("<tr><td align=\"center\" valign=\"top\" bgcolor=\"#336633\">");
    out.println("<font color=\"#000000\" size=\"3\">");
    if (bio) {
        out.println("<center><b>Lesson Book Bio Backup Management</b></center>");
    } else {
        out.println("<center><b>Announcement Page Backup Management</b></center>");
    }
    out.println("</font>");
    //out.println("</font></td></tr>");
    
    out.println("");
    out.println("</table>");
    
    String path = req.getRealPath("");
    String tmp = "/announce/" + club + "/";
    String announce_path = path + tmp;


    boolean z = false;
    StringTokenizer tok = null;
    String sdate = "";
    String stime = "";
    String part = "";
    int time = 0;
    int date = 0;
    int yy = 0;
    int mm = 0;
    int dd = 0;


    File dir = new File( announce_path );
    File[] files = dir.listFiles();

    FilenameFilter ff = null;

    if (bio) {

        ff = new FilenameFilter() {
            public boolean accept (File b, String name) {
                return (name.endsWith (".bak") && name.startsWith(club + "_bio" + bio_id + "-") == true);
            }
        };

    } else if (!user.equals(DINING_USER)) {

        ff = new FilenameFilter() {
            public boolean accept (File b, String name) {         
                return (name.endsWith (".bak") && name.startsWith(club + "_announce" + ((activity_id == 0) ? "" : "_" + activity_id) + "-") == true);
            }
        };

    } else {      // ForeTees Dining Admin user
       
        ff = new FilenameFilter() {
            public boolean accept (File b, String name) {         
                return (name.endsWith (".bak") && name.startsWith(club + "_announce_dining") == true);
            }
        };
    }
       
/*
    // This filter only returns files.
    FileFilter fileFilter = new FileFilter()
    {

        @Override
        public boolean accept(File file)
        {
        return !file.isDirectory();
        }
    };
    files = dir.listFiles(fileFilter);
*/
    files = dir.listFiles(ff);

    // Sort files by name.
    Arrays.sort(files, new Comparator()
    {
        @Override
        public int compare(Object o1, Object o2)
        {
        return ((File) o2).getName().compareTo(((File) o1).getName());
        }
    });


    out.println("<br><br>");
    out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\">");

    for (File f : files) {

        tmp = f.getName();

        if (club.equals("palmvalley-cc")) {  // needs custom handling due to the "-" in their clubname
            
            tok = new StringTokenizer(tmp, "_");
            tok.nextToken(); // eat clubname portion
            tmp = tok.nextToken(); 
        }
        
        tok = new StringTokenizer( tmp, "-" );
        part = tok.nextToken();
        part = tok.nextToken();
        tok = new StringTokenizer( part, "T" );
        sdate = tok.nextToken();
        part = tok.nextToken();
        tok = new StringTokenizer( part, "." );
        stime = tok.nextToken();

        try {
            time = Integer.parseInt(stime);
            date = Integer.parseInt(sdate);
        } catch (Exception ignore) {
        } finally {
            stime = SystemUtils.getSimpleTime(time);
        }

        yy = date / 10000;
        mm = date - (yy * 10000);
        dd = mm - ((mm / 100) * 100);
        mm = mm / 100;

        out.println("<tr bgcolor=\"" + ((z=z==false) ? "#FFFFFF" : "#F5F5DC") + "\">");
        out.print("<td class=\"fileLink\">Back up from "+ mm +"/" + dd + "/" + yy + " at " + stime + " CST</td>");
        if (bio) {

          //out.print("<td><a href=\"Proshop_announce?backups&view=" + tmp + qry_bio + "\" class=\"fileLink\" style=\"font-size:10pt\" target=\"_preview\">Preview</a></td>");
          //out.println("<td><a href=\"/" +rev+ "/announce/" +club+ "/" +club+ "_bio" +id+ ".htm\" class=\"tu_iframe_1024x768 fileLink\">Preview</a></td>");
            out.println("<td><a href=\"Common_lesson?bioview&backup&file=" + tmp + qry_bio + "&club=" +club+ "&proid=" +id+ "\" class=\"tu_iframe_1024x768 fileLink\">Preview</a></td>");

        } else {

            out.print("<td><a href=\"Proshop_announce?backups&view=" + tmp + qry_bio + "\" class=\"fileLink\" style=\"font-size:10pt\" target=\"_preview\">Preview</a></td>");

        }
        out.print("<td><a href=\"Proshop_announce?backups&restore=" + tmp + qry_bio + "\" class=\"fileLink\" style=\"font-size:10pt\" onclick=\"return confirm('Your current Announcement page will be deleted and replaced by this backup file.\\n\\nShould you need to revert back to an older version, you can do so from the Manage Backup Copies page.\\n\\nAre you sure you want to restore this file?')\">Restore</a></td>");
        //out.print("<td><a href=\"Proshop_announce?backups&rm=" + tmp + qry_bio + "\" class=\"fileLink\" style=\"font-size:10pt\"><img src=\"/" + rev + "/images/dts_trash.gif\" border=0 onclick=\"return confirm('The file can NOT be undeleted.\\n\\nAre you sure you want to delete it?')\"></a></td>");
        out.print("</tr>");

        //out.println("<tr><td class=\"fileLink\">"+ f.getName() +"</td></tr>");
        
    }

    out.println("</table>");

/*
    // sort files by last modified date
    Arrays.sort(files, new Comparator()
    {
        @Override
        public int compare(Object o1, Object o2)
        {
        if(((File) o1).lastModified() <((File) o1).lastModified())
        {
                return -1;
        }
        else if(((File) o1).lastModified() >((File) o1).lastModified())
        {
                return 1;
        }
        else
        {
                return 0;
        }
        }
    });
*/

/*
    File b = new File ( announce_path );
    FilenameFilter ff = null;
    
    if (bio) {
        
        ff = new FilenameFilter() {
            public boolean accept (File b, String name) {
                return (name.endsWith (".bak") && name.startsWith(club + "_bio" + bio_id + "-") == true);
            }
        };
        
    } else {
        
        ff = new FilenameFilter() {
            public boolean accept (File b, String name) {
                return (name.endsWith (".bak") && name.startsWith(club + "_announce" + ((activity_id == 0) ? "" : "_" + activity_id) + "-") == true);
            }
        };
        
    }
    
    String [] files = b.list(ff);
/*
    Arrays.sort( files, new Comparator()
    {
      public int compare(final Object o1, final Object o2) {
        return new Long(((File)o1).lastModified()).compareTo
             (new Long(((File) o2).lastModified()));
      }
    });
*/
/*
    out.println("<br><br>");
    out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\">");
    boolean z = false;

    StringTokenizer tok = null;
    String sdate = "";
    String stime = "";
    String part = "";
    int time = 0;
    int date = 0;
    int yy = 0;
    int mm = 0;
    int dd = 0;
    int temp = 0;
    
    for ( int i = 0; i < files.length; i++ ) {

        tok = new StringTokenizer( files[i], "-" );
        part = tok.nextToken();
        part = tok.nextToken();
        tok = new StringTokenizer( part, "T" );
        sdate = tok.nextToken();
        part = tok.nextToken();
        tok = new StringTokenizer( part, "." );
        stime = tok.nextToken();
        
        try {
            time = Integer.parseInt(stime);
            date = Integer.parseInt(sdate);
        } catch (Exception ignore) {            
        } finally {
            stime = SystemUtils.getSimpleTime(time);
        }
        
        yy = date / 10000;
        mm = date - (yy * 10000);
        dd = mm - ((mm / 100) * 100);
        mm = mm / 100;
        
        out.println("<tr bgcolor=\"" + ((z=z==false) ? "#FFFFFF" : "#F5F5DC") + "\">");
        out.print("<td class=\"fileLink\">Back up from "+ mm +"/" + dd + "/" + yy + " at " + stime + " CST</td>");
        out.print("<td><a href=\"Proshop_announce?backups&view=" + files[i] + qry_bio + "\" class=\"fileLink\" style=\"font-size:10pt\" target=\"_preview\">Preview</a></td>");
        out.print("<td><a href=\"Proshop_announce?backups&restore=" + files[i] + qry_bio + "\" class=\"fileLink\" style=\"font-size:10pt\" onclick=\"return confirm('Your current Announcement page will be deleted and\\nreplaced by this backup file.\\n\\nThis action can NOT be undone.\\n\\nAre you sure you want to restore this file?')\">Restore</a></td>");
        //out.print("<td><a href=\"Proshop_announce?backups&rm=" + files[i] + qry_bio + "\" class=\"fileLink\" style=\"font-size:10pt\"><img src=\"/" + rev + "/images/dts_trash.gif\" border=0 onclick=\"return confirm('The file can NOT be undeleted.\\n\\nAre you sure you want to delete it?')\"></a></td>");
        out.print("</tr>");
    }
    
    out.println("</table>");
    
    if (files.length == 0) {
     
        out.print("<center>No backups have been created yet.<br>Backups are created automatically each time you save the " + ((bio) ? "Bio" : "Announcement") + " page.</center>");
        
    }
*/
    out.println("<br><br><center>");
     if (!user.equals(DINING_USER)) {
         if (bio) {
            out.println("<form method=post action=\"Proshop_lesson\">");
            out.println("<input type=\"hidden\" name=\"bio\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");
         } else {
            out.println("<form method=post action=\"Proshop_announce\">");
            out.println("<input type=\"hidden\" name=\"menu\" value=\"yes\">");
         }
        out.println("<input type=submit value=\"Back\" style=\"background-color:#8B8970; width:75px\">");
     } else {
        out.println("<form method=\"post\" action=\"Proshop_announce?menu=yes\"><input type=\"submit\" style=\"background:#8B8970\" value=\" Return \" alt=\"Close\">");
     }        
    out.println("</form>");
    out.println("</center>");
    out.println("<br>");
    
 } // end backupsListing
 
 
 //*****************************************************
 // Process the request from ae.jsp or ae2.jsp (save the file)
 //*****************************************************
 //
 @SuppressWarnings("deprecation")
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);           // check for intruder

   if (session == null) {

      return;
   }

   String user = (String)session.getAttribute("user");
   String club = (String)session.getAttribute("club");              // get club name
   String templott = (String)session.getAttribute("lottery");       // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int activity_id = (Integer)session.getAttribute("activity_id");

   int id = 0;

   Connection con = SystemUtils.getCon(session);

   if (con == null) {

       out.println(SystemUtils.HeadTitle("DB Connection Error"));
       out.println("<BODY><CENTER><BR>");
       out.println("<BR><BR><H3>Database Connection Error</H3>");
       out.println("<BR><BR>Unable to connect to the Database.");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact customer support.");
       out.println("<BR><BR>");
       out.println("<a href=\"javascript:history.back(1)\">Return</a>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
   }

   boolean isDining = user.equals(DINING_USER) || user.equals("proshopfb");
   boolean new_skin = Utilities.isNewSkinActive(club, con);

   //
   //  If the pro id was passed, then this is a call for a lesson pro's bio page
   //
   //if (req.getParameter("probio") != null) {
   if (req.getParameter("proid") != null) {

      String proid = (String)session.getAttribute("proid");   // get lesson pro id
        
      id = Integer.parseInt(proid);                           // convert the proid
   }

   //
   //  if menu= was provided, then call came from Proshop's menu to display a menu
   //
   if (req.getParameter("menu") != null) {

       if (isDining == false) {       // if not Dining Admin
          
          // Check Feature Access Rights for current proshop user
          if (!SystemUtils.verifyProAccess(req, "TOOLS_ANNOUNCE", con, out)) {
              SystemUtils.restrictProshop("TOOLS_ANNOUNCE", out);
              return;
          }
       }
       
      //
      //  Build the HTML page (secondary menu)
      //
      out.println(SystemUtils.HeadTitle("" + ((isDining) ? "Dining" : "Proshop") + " - Announcement Menu"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"darkBlue\" alink=\"darkBlue\" vlink=\"darkBlue\">");
      if (isDining == false) {       // if not Dining Admin
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      }
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<br>");
      out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\" bgcolor=\"F5F5DC\">");

         out.println("<tr><td align=\"center\" valign=\"top\" bgcolor=\"336633\">");
         out.println("<font color=\"#FFFFFF\" size=\"3\">");
         if (isDining == true) {       // if Dining Admin
            out.println("<b>Dining Home Page Management Menu</b><br>");
         } else {
            out.println("<b>Announcement Page Management Menu</b><br>");
         }
         out.println("</font>");
         out.println("</td>");
         out.println("</tr>");

         out.println("<tr>");
         out.println("<td align=\"left\"><font size=\"2\"><br>");
         if (isDining == true) {       // if Dining Admin
            out.println("Use this to manage your home page and keep your members up to date on club activities.");
         } else {
            out.println("<b>Note:</b> To create or change an announcement page, do one of the following.");
            out.println("<br><br>");
            out.println("&nbsp;&nbsp;&nbsp;1. Use our online editor (allows you to insert your own images).");
            out.println("<br>");
            out.println("&nbsp;&nbsp;&nbsp;2. Create the page using your computer, save it as");
            out.println("<br>");
            out.println("&nbsp;&nbsp;&nbsp; &nbsp; &nbsp;an HTML document (<b>the name MUST end with .htm</b>), then upload it.");
            out.println("<br>");
            out.println("&nbsp;&nbsp;&nbsp;3. Create the page using your computer, save it as");
            out.println("<br>");
            out.println("&nbsp;&nbsp;&nbsp; &nbsp; &nbsp;an HTML document, upload it and use our editor to add images, etc.");
         }
         out.println("<br>");
         out.println("</font></td></tr>");

         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<p>");

         if (isDining == true) {       // if Dining Admin

            out.println("<a href=\"Proshop_announce?tinymce\">Edit Dining Home Page</a>");
            out.println("</p>");
            out.println("</font></td></tr>");

            out.println("<tr>");
            out.println("<td align=\"center\"><font size=\"2\">");
            out.println("<p>");
            out.println("<a href=\"Proshop_announce\" target=\"_preview\">Preview Dining Home Page</a>");

         } else {

             if (new_skin) {

                // club if on the new skin - only one link
                //out.println("<a href=\"Proshop_announce?tinymce\" target=\"bot\">Use NEW Online Editor (Recommended)</a>");

                out.println("<a href=\"Proshop_announce?tinymce\" target=\"bot\">Use Online Editor</a>");
                /*out.println("<br><br><b>*Note*</b> The old editor option has been removed."
                        + "<br>The above option was formerly known as the \"NEW Online Editor\".");*/

             } else {
/*

                // club is NOT yet on the new skin - provide links to both old & new skin announcement pages

                String path = req.getRealPath("") + "/announce/" + club + "/";
                String filename = club + "_announce_ns" + ((activity_id == 0) ? "" : (activity_id == ProcessConstants.DINING_ACTIVITY_ID || user.equals("proshopfb")) ? "_dining" : "_" + activity_id) + ".htm";

                File f = new File(path + filename);

                if (!f.isFile()) {

                    // temp new skin file does not exist - copy it
                    String ns_filename = req.getRealPath("") + "/announce/announcement_templates/new_skin1.htm";
                    boolean copy_ok = Utilities.copyFile(ns_filename, path + filename);

                }

                f = new File(path + filename);

                if (f.isFile()) {

                    out.println("<a href=\"Proshop_announce?tinymce&amp;ns\" target=\"bot\">Use Online Editor To Prepare New Skin Announcement Page</a><br><br>");
                
                }
*/
                out.println("<a href=\"Proshop_announce?tinymce\" target=\"bot\">Use Online Editor</a>");

             }
         }

         out.println("</p>");
         out.println("</font></td>");
         out.println("</tr>");
         
         if (isDining == false) {       // do not offer these to Dining Admin user

            if (club.equals("bracketts")) {
                out.println("<tr>");
                out.println("<td align=\"center\"><font size=\"2\">");
                out.println("<p>");
                out.println("<a href=\"/" +rev+ "/ae.jsp\" target=\"bot\">Use Old Online Editor</a>");
                out.println("</p>");
                out.println("</font></td>");
                out.println("</tr>");
            }

            out.println("<tr>");
            out.println("<td align=\"center\"><font size=\"2\">");
            out.println("<p>");
            out.println("<a href=\"Proshop_upload\" target=\"bot\">Upload From Your Computer</a>");
            out.println("</p>");
            out.println("</font></td>");
            out.println("</tr>");
         }
         
         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<p>");
         if (isDining == true) {       // if Dining Admin
            out.println("<a href=\"Proshop_announce?backups\">Manage Backup Copies</a>");
         } else {
            out.println("<a href=\"Proshop_announce?manage&new\" target=\"bot\">Create New Announcement Page</a>");
            out.println("<br><br>");
            out.println("<a href=\"Proshop_announce?manage&list\" target=\"bot\">Manage Non-Active Annoucement Pages</a>");
            out.println("</p>");
            out.println("</font></td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td align=\"center\"><font size=\"2\">");
            out.println("<p>");
            out.println("<a href=\"Proshop_announce?backups\" target=\"bot\">Manage Backup Copies</a>");
         }
         out.println("</p>");
         out.println("</font></td>");
         out.println("</tr>");
         
      out.println("</table>");
      
        out.println("<br><br><center>");
        if (isDining == false) {     
           out.println("<form method=get action=\"Proshop_announce\">");
           out.println("<input type=submit value=\" Home \" style=\"background-color: #8B8970; width:75px\">");
        } else {
           out.println("<form><input type=\"button\" style=\"background:#8B8970\" Value=\" Return To Dining System \" onClick='self.close()' alt=\"Close\">");
        }
        out.println("</form>");
        out.println("</center>");
        
      out.println("</body></html>");

   } else {

      //
      // Get the parameter entered
      //
      String textfield = "";         

      String staging_filename = (req.getParameter("file") != null && !req.getParameter("file").equals("")) ? req.getParameter("file") : "";
      boolean staging_mode = !staging_filename.equals("");

      if (req.getParameter("textfield") != null) {           

         textfield = req.getParameter("textfield");          // from 'name=textfield' in ae.jsp
         
      } else if (req.getParameter("content") != null) {           

         textfield = req.getParameter("content");            // from tinyMCE Editor (doGet above)
      }

      if (textfield == null) textfield = "";
      textfield = textfield.trim();
      
      if (textfield.equals( "" )) {         // if nothing there

         out.println("<HTML><HEAD><Title>Proshop Save Announcements/Bio Page</Title>");
         out.println("</HEAD>");
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Error Save File Failed</H3>");
         out.println("<BR><BR>");
         out.println("Sorry, we were unable to save the file due to a system error.");
         out.println("<BR>Please try again. If problem continues please contact Customer Support.");
         out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }


      //out.println("\n\nBEFORE: (" + (diningPrefix.length()) + ")(" + (diningPrefix_length) + ")<pre>\n" + textfield + "\n</pre>\n\n\n");
/*
      // process new skin announcement
      if (isDining) {

          //if (textfield.startsWith(diningPrefix)) {

              textfield = textfield.substring(diningPrefix_length);

          //}

          //if (textfield.endsWith(diningSuffix)) {

              textfield = textfield.substring(0, (textfield.length() - diningSuffix_length));

          //}

      }
*/
      //out.println("\n\nAFTER: (" + diningSuffix.length() + ")(" + (diningSuffix_length) + ")<pre>\n" + textfield + "\n</pre>\n\n\n");

      GregorianCalendar cal = new GregorianCalendar(); 
      int day = cal.get(cal.DAY_OF_MONTH);
      int month = cal.get(cal.MONTH) + 1;
      int year = cal.get(cal.YEAR);
      int hr = cal.get(cal.HOUR_OF_DAY);
      int min = cal.get(cal.MINUTE);
      int date = (year * 10000) + (month * 100) + day;
      
      String timestamp = date + "T" + SystemUtils.ensureDoubleDigit(hr) + SystemUtils.ensureDoubleDigit(min);
      String tagline = "<!-- SAVED BY " + user + " on node " + Common_Server.SERVER_ID + " from " + req.getHeader("X-Forwarded-For") + " at " + timestamp + " -->";
      
      String path = req.getRealPath("");
      String tmp = "/announce/";

      int fail = 0;
      int backup_fail = 0;
      
      //
      //  Save the announcement page that was just created/edited
      //
      if (id == 0) {     // if an announcment page

         String announce_file   = path + tmp + club + "/" + getFileName(activity_id, user, club, "", false);
         String announce_backup = path + tmp + club + "/" + getFileName(activity_id, user, club, timestamp, true);

       //String announce_file = path + tmp + club + "_announce" + ((activity_id == 0) ? "" : "_" + activity_id) + ".htm";
       //String announce_backup = path + tmp + club + "_announce" + ((activity_id == 0) ? "" : "_" + activity_id) + "-" + timestamp + ".bak";
         
         if (staging_mode) {
             
             // overwrite the full path to announcement file and skip the backup
             announce_file = path + tmp + club + "/" + staging_filename;
             
         } else {
         
             // make back up copy
             try {

                 File f = new File(announce_file);

                 if (f.exists()) {
                     f.renameTo(new File(announce_backup));
                 }

             }
             catch (Exception e2) {

                backup_fail = 1;
             }
             
         }
         
         // save new page
         try {
             
            PrintWriter fout1 = new PrintWriter(new FileWriter(announce_file, false));

            fout1.print(textfield);
            //fout1.println();            // output the line
            fout1.print(tagline);     // output the tag line
            fout1.close();

         }
         catch (Exception e2) {

            Utilities.logError("Proshop_announce: Error saving announcement page. club=" + club + ", err=" + e2.toString());

            fail = 1;
         }
         
         //
         //  Display a page to confirm the save and provide a link to the club's announcement page
         //
         out.println(SystemUtils.HeadTitle("Proshop Display Announcement Page"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

         SystemUtils.getProshopSubMenu(req, out, lottery);
         out.println("<CENTER>");
         out.println("<BR><BR><H3>Announcement Page Saved</H3>");
         out.println("<BR><BR>");

       //out.println("<br>announce_file=" + announce_file);
       //out.println("<br>announce_backup=" + announce_backup);


         if (staging_mode) {
            out.println("<BR><BR>Your staging file \"" + staging_filename + "\" has been successfully saved.");
         } else if (fail == 0) {
            out.println("<BR><BR>The announcement page has been saved ");
            if (backup_fail == 1) {
                out.print("but we were NOT able to create a back for you.");
            } else {
                out.print("and we made a backup of the original file.");
            }
         } else {
            out.println("<BR><BR><b>Warning:</b> An error has been encountered.  The announcement page was NOT replaced.  Please try again.");
            out.println("<BR><BR>If problem continues, please contact support.");
         }
         
         out.println("<br><br>");
         if (isDining == false) {     
            out.println("<form method=get action=\"Proshop_announce\">");
            out.println("<input type=submit value=\" Home \" style=\"background-color: #8B8970\">");
         } else {
            out.println("<form method=\"post\" action=\"Proshop_announce?menu=yes\"><input type=\"submit\" style=\"background:#8B8970\" Value=\" Return \" alt=\"Close\">"); // onClick='self.close()' 
         }
         out.println("</form>");
         out.println("</CENTER></BODY></HTML>");
            
      }
      
      else 
          
      {    // this is for a Lesson Pro Bio Page
        
          
         String announce_file = path + "/announce/" + club + "/" + club + "_bio" + id + ".htm";
         String announce_backup = path + "/announce/" + club + "/" + club + "_bio" + id + "-" + timestamp + ".bak";
         
         // make back up copy
         try {

             File f = new File(announce_file);
             
             if (f.exists()) {
                 f.renameTo(new File(announce_backup));
             }
             
         }
         catch (Exception e2) {

            backup_fail = 1;
         }
          
         // save the new page
         try {
             
            PrintWriter fout1 = new PrintWriter(new FileWriter(announce_file, false));

            fout1.print(textfield);
            fout1.println();            // output the line
            fout1.println(tagline);     // output the tag line
            fout1.close();

         }
         catch (Exception e2) {

            Utilities.logError("Proshop_announce: Error saving bio page. club=" + club + ", id=" + id + ", err=" + e2.toString());

            fail = 1;
         }
         
         //
         //  Display a page to confirm the save 
         //
         out.println(SystemUtils.HeadTitle("Proshop Lesson Bio"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\"><center>");

         SystemUtils.getProshopSubMenu(req, out, lottery);
         if (fail == 0) {
             out.println("<BR><BR><H3>Lesson Pro Bio Saved</H3>");
             out.println("<BR><BR>");
             out.println("<BR><BR>The Lesson Pro Bio has been replaced ");
             if (backup_fail == 1) {
                 out.print("but we were NOT able to create a back for you.");
             } else {
                 out.print("and we made a backup of the original file.");
             }
         } else {
             out.println("<BR><BR><H3>Lesson Pro Bio Not Saved</H3>");
             out.println("<BR><BR>");
             out.println("<BR><BR><b>Warning:</b> An error occurred while saving.  The Lesson Pro Bio has NOT been replaced.");
         }
         out.println("<BR><BR>");
         //out.println("<a href=\"/" +rev+ "/announce/" +club+ "/" +club+ "_bio" +id+ ".htm\" target=\"_blank\">View the Bio Page</a>");
         out.println("<br><br>");

         out.println("<form method=post action=\"Proshop_lesson\">");
         out.println("<input type=\"hidden\" name=\"bio\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");

         //out.println("<form method=get action=\"Proshop_announce\">");
         out.println("<input type=submit value=\"Return\" style=\"background-color: #8B8970; width:75px\">");
         out.println("</form>");
         out.println("</CENTER></BODY></HTML>");
      }
   }

 }  // end of doPost

}
