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
import java.sql.*;


public class Proshop_announce extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process the initial request from Proshop_main
 //*****************************************************
 //
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

       // Check Feature Access Rights for current proshop user
       if (!SystemUtils.verifyProAccess(req, "TOOLS_ANNOUNCE", con, out)) {
           SystemUtils.restrictProshop("TOOLS_ANNOUNCE", out);
           return;
       }
       
       manageBackups(req, resp, out);
       return;
   }

   String club = (String)session.getAttribute("club");   // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int activity_id = (Integer)session.getAttribute("activity_id");

   
   //
   //   File objects to get the current announcement page
   //
   File f;
   FileReader fr;
   BufferedReader br;
   String tmp = "";
   String path = "";
   
   
   //
   //  if call from menu below to use the tinyMCE online editor, then open the tinyMCE editor
   //
   if (req.getParameter("tinymce") != null) {
      
      out.println("<html><head>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<title> \"ForeTees Proshop Announcement Page\"</title>");
      out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
      out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/tiny_mce/tiny_mce.js\"></script>");

      out.println("<script type=\"text/javascript\">");
      out.println("tinyMCE.init({");
      // General options
      out.println("mode : \"textareas\",");
      out.println("theme : \"advanced\",");
     // out.println("plugins : \"safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager,filemanager\",");
      out.println("plugins : \"safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager\",");

      //
      // Theme options - these are the default buttons that came with the product (refer to our custom button rows directly below)
      /*
      out.println("theme_advanced_buttons1 : \"save,newdocument,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,styleselect,formatselect,fontselect,fontsizeselect\",");
      out.println("theme_advanced_buttons2 : \"cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,|,insertdate,inserttime,preview,|,forecolor,backcolor\",");
      out.println("theme_advanced_buttons3 : \"tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,iespell,media,advhr,|,print,|,ltr,rtl,|,fullscreen\",");
      out.println("theme_advanced_buttons4 : \"insertlayer,moveforward,movebackward,absolute,|,styleprops,spellchecker,|,cite,abbr,acronym,del,ins,attribs,|,visualchars,nonbreaking,template,blockquote,pagebreak,|,insertfile,insertimage\",");
       */
      
      // Theme options
      out.println("theme_advanced_buttons1 : \"save,|,cut,copy,paste,pastetext,pasteword,|,search,replace,|,undo,redo,|,tablecontrols,|,removeformat,visualaid,|,charmap,insertdate,inserttime,emotions,hr,advhr,|,media,|,print,|,ltr,rtl,|,fullscreen,|,insertlayer,moveforward,movebackward,absolute,|,iespell,spellchecker\",");
      out.println("theme_advanced_buttons2 : \"formatselect,fontselect,fontsizeselect,styleprops,|,bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote,|,sub,sup,|,link,unlink,anchor,image,insertimage,|,cleanup,code,preview\",");
      out.println("theme_advanced_buttons3 : \"\",");     
      out.println("theme_advanced_buttons4 : \"\",");   // we squeezed all desired buttons onto 2 rows - leave 3 & 4 blank (must be defined here to avoid the default)  
      
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

      // Replace values for the template plugin
      out.println("template_replace_values : {");
      out.println("username : \"Some User\",");
      out.println("staffid : \"991234\"");
      out.println("}");

      out.println("});");

      out.println("</script>");
      
      out.println("<style type=\"text/css\"> body {text-align:center} </style>");      // so body will align on center
      
      out.println("</head>");
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

      SystemUtils.getProshopSubMenu(req, out, lottery);

      
      out.println("<div style=\"align:center; margin:0px auto;\">");

      out.println("<p align=center><BR>Use the Editor to make changes, then click on 'Save Changes' below.</p>");
      out.println("<p align=center>Click here to view a <a href=\"/" +rev+ "/web utilities/tiny_mce/TinyMCE-User-Guide.pdf\" target=\"_blank\">User Guide</a></p>");
      
      out.println("<form method=post action=\"/" + rev + "/servlet/Proshop_announce\">");
      
      out.println("<textarea name=\"content\" style=\"width:100%;height:80%\">");
      
      //
      //   Get the announcement page
      try {
          path = req.getRealPath("");
          if (activity_id == 0) {
              tmp = "/announce/" +club+ "_announce.htm"; // "/" +rev+ 
          } else {
              tmp = "/announce/" +club+ "_announce_" + activity_id + ".htm";
          }
          f = new File(path + tmp);
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

      while( (tmp = br.readLine()) != null )
          out.println(tmp);


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
   
      out.println("</textarea><BR>");     
      out.println("<table width=\"300\" border=\"0\" align=\"center\"><tr><td align=\"left\">");     
      out.println("<input type=submit value=\" Save Changes \" style=\"background-color: #8B8970\">");
      out.println("");
      out.println("</td></form>");     
      
      out.println("<form method=get action=\"/" + rev + "/servlet/Proshop_announce\">");
      out.println("<td align=\"right\">");     
      out.println("<input type=submit value=\" Cancel - Return w/o Changes \" style=\"background-color: #8B8970\">");
      out.println("</td></form></tr></table>");     

      out.println("</div></BODY></HTML>");     
      return;   
   }           // end of IF tinyMCE

       
   //
   //  Call is to display the announcement page.
   //
   //  Display a page to provide a link to the club's announcement page
   //
   out.println("<html><head>");
   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
   out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
   out.println("<title> \"ForeTees Proshop Announcement Page\"</title>");
//   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
   out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");

   out.println("<style type=\"text/css\"> body {text-align: center} </style>");      // so body will align on center
   
   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

   SystemUtils.getProshopSubMenu(req, out, lottery);
  
   out.println("<div style=\"align:center; margin:0px auto;\">");

   try {
       path = req.getRealPath("");
       if (activity_id == 0) {
           tmp = "/announce/" +club+ "_announce.htm"; // "/" +rev+ 
       } else {
           tmp = "/announce/" +club+ "_announce_" + activity_id + ".htm";
       }
       f = new File(path + tmp);
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
   
   while( (tmp = br.readLine()) != null )
       out.println(tmp);
   
   
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
   
   /*
   out.println("<iframe frameborder=\"0\" class=\"announce\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"auto\" height=\"100%\" width=\"100%\" src=\"/" +rev+ "/announce/" +club+ "_announce.htm\">");
   out.println("<!-- Alternate content for non-supporting browsers -->");
   out.println("<H2>The browser you are using does not support frames</H2>");
   out.println("</iframe>");
   */
   
   out.println("</div></BODY></HTML>");
   
 }  // end of doGet


 private void manageBackups(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {

    if (req.getParameter("view") != null) {
         
        backupsPreview(req, resp, out);
         
    } else if (req.getParameter("restore") != null) {
         
        backupsRestore(req, resp, out);

    } else if (req.getParameter("rm") != null) {

        backupsDelete(req, resp, out);
        backupsListing(req, resp, out);

    } else {

        backupsListing(req, resp, out);

    }

 }
  
 
 private void backupsRestore(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
     
    HttpSession session = SystemUtils.verifyPro(req, out);           // check for intruder

    if (session == null) return;

    int activity_id = (Integer)session.getAttribute("activity_id");
    String club = (String)session.getAttribute("club");
    String file = (req.getParameter("restore") != null) ? req.getParameter("restore") : "";
    
    //String qry_bio = "";
     
    int id = 0;
    boolean bio = req.getParameter("probio") != null;
    if (bio) {

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
        tmp = "/announce/";
        
        if (bio) {
        
            announce_file = path + tmp + club + "_bio" + id + ".htm";
            
        } else {
            
            announce_file = path + tmp + club; // + "_announce.htm";
            
            if (activity_id == 0) {
                announce_file += "_announce.htm";
            } else {
                announce_file += "_announce_" + activity_id + ".htm";
            }
            
        }
        
        restore_file = path + tmp + file;
        
        f = new File(announce_file);
        if (f.isFile()) f.delete();
       
    } catch (Exception e) {
        restore_fail = true;
        err = e.toString();
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

        }
        catch (Exception e2) {
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
        out.println("<form method=get action=\"/" + rev + "/servlet/Proshop_announce\">");
        out.println("<input type=submit value=\" Home \" style=\"background-color: #8B8970\">");
        out.println("</form>");
        out.println("</center>");
        out.println("</BODY></HTML>");
        
    }
    
    f = null;
    
 } // end backupsRestore
 
 
 private void backupsDelete(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {

/*
 * I AM DEACTIVATING THIS METHOD AND THE CALLS TO IT BECAUSE WE ACTUALLY
 * NEED TO DELETE THIS FILES FROM EACH NODE AND I AM THINKING IT WOULD
 * BE EASIER AND SAFER TO JUST DELETE ALL BACKUPS FILES EACH NIGHT THAT
 * ARE 30 DAYS OLD
 *   
  
    String file = (req.getParameter("rm") != null) ? req.getParameter("rm") : "";

    File f;
    String tmp = "";
    String path = "";

    try {
        
       path = req.getRealPath("");
       tmp = "/announce/" + file;
       f = new File(path + tmp);
       if (f.isFile()) f.delete();
       
    } catch (Exception e) {
       out.println("<br><br><p align=center>Backup file " + file + " is missing or in use.</p>");
       out.println("<p align=center>Error: " + e.toString() + "</p>");
       out.println("<p align=center>Please contact support.</p>");
       out.println("</BODY></HTML>");
       return;
    }
*/
     
     
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
 
 
 private void backupsPreview(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
    
    String file = (req.getParameter("view") != null) ? req.getParameter("view") : "";
       
    File f;
    FileReader fr;
    BufferedReader br;
    String tmp = "";
    String path = "";

    try {
        
       path = req.getRealPath("");
       tmp = "/announce/" + file;
       f = new File(path + tmp);
       fr = new FileReader(f);
       br = new BufferedReader(fr);
       if (!f.isFile()) {
           // throw error
       }
       
    }
    catch (FileNotFoundException e) {
       out.println("<br><br><p align=center>Backup file " + file + " is missing.</p>");
       out.println("</BODY></HTML>");
       return;
    }
    catch (SecurityException se) {
       out.println("<br><br><p align=center>Access Denied.  Please contact support.</p>");
       out.println("</BODY></HTML>");
       return;
    }

    try {
        
        while( (tmp = br.readLine()) != null )
           out.println(tmp);

        br.close();
        
    } catch (Exception exp) {
       out.println("<br><br><p align=center>Backup file " + file + " could not be fully read.</p>");
       out.println("<p align=center>Error: " + exp.toString() + "</p>");
       out.println("<p align=center>Please contact support.</p>");
       out.println("</BODY></HTML>");
    }
    
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
    
} // end backupsPreview


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
    String tmp = "/announce/";
    String announce_path = path + tmp;

    File b = new File ( announce_path );
    FilenameFilter ff = null;
    
    if (bio) {
        
        ff = new FilenameFilter() {
            public boolean accept (File b, String name) {
                return (name.endsWith (".bak") && name.indexOf(club + "_bio" + bio_id + "-") != -1);
            }
        };
        
    } else {
        
        ff = new FilenameFilter() {
            public boolean accept (File b, String name) {
                return (name.endsWith (".bak") && name.indexOf(club + "_announce" + ((activity_id == 0) ? "" : "_" + activity_id) + "-") != -1);
            }
        };
        
    }
    
    String [] files = b.list(ff);
    
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
        out.print("<td><a href=\"/" +rev+ "/servlet/Proshop_announce?backups&view=" + files[i] + qry_bio + "\" class=\"fileLink\" style=\"font-size:10pt\" target=\"_preview\">Preview</a></td>");
        out.print("<td><a href=\"/" +rev+ "/servlet/Proshop_announce?backups&restore=" + files[i] + qry_bio + "\" class=\"fileLink\" style=\"font-size:10pt\" onclick=\"return confirm('Your current Announcement page will be deleted and\\nreplaced by this backup file.\\n\\nThis action can NOT be undone.\\n\\nAre you sure you want to restore this file?')\">Restore</a></td>");
        //out.print("<td><a href=\"/" +rev+ "/servlet/Proshop_announce?backups&rm=" + files[i] + qry_bio + "\" class=\"fileLink\" style=\"font-size:10pt\"><img src=\"/" + rev + "/images/dts_trash.gif\" border=0 onclick=\"return confirm('The file can NOT be undeleted.\\n\\nAre you sure you want to delete it?')\"></a></td>");
        out.print("</tr>");
    }
    
    out.println("</table>");
    
    if (files.length == 0) {
     
        out.print("<center>No backups have been created yet.<br>Backups are created automatically each time you save the " + ((bio) ? "Bio" : "Announcement") + " page.</center>");
        
    }
    
    out.println("<br><br><center>");
    out.println("<form method=get action=\"/" + rev + "/servlet/Proshop_announce\">");
    out.println("<input type=submit value=\" Home \" style=\"background-color: #8B8970\">");
    out.println("</form>");
    out.println("</center>");
    
 } // end backupsListing
 
 
 //*****************************************************
 // Process the request from ae.jsp or ae2.jsp (save the file)
 //*****************************************************
 //
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

   //
   //  If the pro id was passed, then this is a call for a lesson pro's bio page
   //
   if (req.getParameter("probio") != null) {

      String proid = (String)session.getAttribute("proid");   // get lesson pro id
        
      id = Integer.parseInt(proid);                           // convert the proid
   }

   //
   //  if menu= was provided, then call came from Proshop's menu to display a menu
   //
   if (req.getParameter("menu") != null) {

       
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
       
       // Check Feature Access Rights for current proshop user
       if (!SystemUtils.verifyProAccess(req, "TOOLS_ANNOUNCE", con, out)) {
           SystemUtils.restrictProshop("TOOLS_ANNOUNCE", out);
           return;
       }
       
      //
      //  Build the HTML page (secondary menu)
      //
      out.println(SystemUtils.HeadTitle("Proshop - Announcement Menu"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<br>");
      out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\" bgcolor=\"F5F5DC\">");

         out.println("<tr><td align=\"center\" valign=\"top\" bgcolor=\"336633\">");
         out.println("<font color=\"#FFFFFF\" size=\"3\">");
         out.println("<b>Announcement Page Management Menu</b><br>");
         out.println("</font>");
         out.println("</td>");
         out.println("</tr>");

         out.println("<tr>");
         out.println("<td align=\"left\"><font size=\"2\"><br>");
         out.println("<b>Note:</b> To create or change an announcement page, do one of the following.");
         out.println("<br><br>");
         out.println("&nbsp;&nbsp;&nbsp;1. Use our online editor (allows you to insert your own images).");
         out.println("<br>");
         out.println("&nbsp;&nbsp;&nbsp;2. Create the page on a word processor using your computer, save it as");
         out.println("<br>");
         out.println("&nbsp;&nbsp;&nbsp; &nbsp; &nbsp;an HTML document (<b>MUST be named " +club+ "_announce" + ((activity_id == 0) ? "" : "_" + activity_id) + ".htm</b>), then upload it.");
         out.println("<br>");
         out.println("&nbsp;&nbsp;&nbsp;3. Create the page on a word processor using your computer, save it as");
         out.println("<br>");
         out.println("&nbsp;&nbsp;&nbsp; &nbsp; &nbsp;an HTML document, upload it and use our editor to add images, etc.");
         out.println("<br>");
         out.println("</font></td></tr>");

      if (club.startsWith("demo")) {
         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<br>");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce?tinymce\" target=\"bot\">Use NEW Online Editor (Recommended)</a>");
         out.println("<br>");
         out.println("</font></td>");
         out.println("</tr>");
      }

         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<br>");
         out.println("<a href=\"/" +rev+ "/ae.jsp\" target=\"bot\">Use Online Editor</a>");
         out.println("<br>");
         out.println("</font></td>");
         out.println("</tr>");

         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<br>");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_upload\" target=\"bot\">Upload From Your Computer</a>");
         out.println("<br>");
         out.println("</font></td>");
         out.println("</tr>");
         
         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<br>");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce?backups\" target=\"bot\">Manage Backup Copies</a>");
         out.println("<br>");
         out.println("</font></td>");
         out.println("</tr>");
         
      out.println("</table>");
      
        out.println("<br><br><center>");
        out.println("<form method=get action=\"/" + rev + "/servlet/Proshop_announce\">");
        out.println("<input type=submit value=\" Home \" style=\"background-color: #8B8970\">");
        out.println("</form>");
        out.println("</center>");
        
      out.println("</body></html>");

   } else {

      //
      // Get the parameter entered
      //
      String textfield = "";         

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
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      GregorianCalendar cal = new GregorianCalendar(); 
      int day = cal.get(cal.DAY_OF_MONTH);
      int month = cal.get(cal.MONTH) + 1;
      int year = cal.get(cal.YEAR);
      int hr = cal.get(cal.HOUR_OF_DAY);
      int min = cal.get(cal.MINUTE);
      int date = (year * 10000) + (month * 100) + day;
      
      String timestamp = date + "T" + SystemUtils.ensureDoubleDigit(hr) + SystemUtils.ensureDoubleDigit(min);
      String tagline = "<!-- SAVED BY " + user + " on node " + Common_Server.SERVER_ID + " at " + timestamp + "  -->";
      
      String path = req.getRealPath("");
      String tmp = "/announce/";
      
      int fail = 0;
      int backup_fail = 0;
      
      
      //
      //  Save the announcement page that was just created/edited
      //
      if (id == 0) {     // if an announcment page
        
         String announce_file = path + tmp + club + "_announce" + ((activity_id == 0) ? "" : "_" + activity_id) + ".htm";
         String announce_backup = path + tmp + club + "_announce" + ((activity_id == 0) ? "" : "_" + activity_id) + "-" + timestamp + ".bak";
         
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
         
         // save new page
         try {
             
            PrintWriter fout1 = new PrintWriter(new FileWriter(announce_file, false));

            fout1.print(textfield);
            fout1.println();            // output the line
            fout1.println(tagline);     // output the tag line
            fout1.close();

         }
         catch (Exception e2) {

            fail = 1;
         }
         
         //
         //  Display a page to confirm the save and provide a link to the club's announcement page
         //
         out.println(SystemUtils.HeadTitle("Proshop Display Announcements Page"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

         SystemUtils.getProshopSubMenu(req, out, lottery);
         out.println("<CENTER>");
         out.println("<BR><BR><H3>Announcement Page Saved</H3>");
         out.println("<BR><BR>");
         if (fail == 0) {
            out.println("<BR><BR>The announcement page has been saved ");
            if (backup_fail == 1) {
                out.print("but we were NOT able to create a back for you.");
            } else {
                out.print("and we made a backup of the original file.");
            }
         } else {
            out.println("<BR><BR>An error has been encountered.  The announcement page was NOT replaced.  Please try again.");
         }
         
         out.println("<br><br>");
         out.println("<form method=get action=\"/" + rev + "/servlet/Proshop_announce\">");
         out.println("<input type=submit value=\" Home \" style=\"background-color: #8B8970\">");
         out.println("</form>");
         out.println("</CENTER></BODY></HTML>");
            
      } 
      
      else 
          
      {    // this is for a Lesson Pro Bio Page
        
          
         String announce_file = path + tmp + club + "_bio" + id + ".htm";
         String announce_backup = path + tmp + club + "_bio" + id + "-" + timestamp + ".bak";
         
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
             out.println("<BR><BR>An error occurred while saving.  The Lesson Pro Bio has NOT been replaced.");
         }
         out.println("<BR><BR>");
         out.println("<a href=\"/" +rev+ "/announce/" +club+ "_bio" +id+ ".htm\" target=\"_blank\">View the Bio Page</a>");
         out.println("<br><br>");
         out.println("<form method=get action=\"/" + rev + "/servlet/Proshop_announce\">");
         out.println("<input type=submit value=\" Home \" style=\"background-color: #8B8970\">");
         out.println("</form>");
         out.println("</CENTER></BODY></HTML>");
      }
   }

 }  // end of doPost

}
