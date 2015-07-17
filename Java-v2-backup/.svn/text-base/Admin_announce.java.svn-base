/***************************************************************************************     
 *   Admin_announce: This servlet will process the 'View Club Announcements' request
 *                     from the Admin's main page.  It will also (doPost) process the
 *                     request to save the edited announcement page from Active Edit (ae.jsp).
 *
 *
 *   called by:  Admin_main (doGet)
 *
 *
 *   created: 1/16/2004   Bob P.
 *
 *   last updated:            ******* keep this accurate *******
 *
 *        2/06/12   Updates for new announcement pages and new skin
 *       11/16/11   Added center alignment to div surrounding entire announcement page to force pages to center align, even if the announcement page is left aligned.
 *       11/09/11   Update path to announcement pages
 *        3/20/11   Add @SuppressWarnings annotations to applicable methods
 *
 ***************************************************************************************
 */
    
import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.foretees.common.Utilities;
import com.foretees.common.Connect;
//import com.foretees.common.ProcessConstants;

public class Admin_announce extends HttpServlet {

 
 final String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 final String diningPrefix = "<div id=\"wrapper\"><br />\n<div id=\"main\" align=\"center\">"; //  (55)  <!--4T-SNIP-->
 final String diningSuffix = "</div><br />&nbsp;\n</div>";


 //*****************************************************
 // Process the initial request from Admin_main
 //*****************************************************
 //
 @SuppressWarnings("deprecation")
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           

   //
   //  Prevent cacheing so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   HttpSession session = SystemUtils.verifyAdmin(req, out);             // check for intruder

   if (session == null) {
     
      return;
   }

   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");
   
   int activity_id = 0;
   
   boolean new_skin = true; // Utilities.isNewSkinActive(club, Connect.getCon(req));

   out.println("<!doctype html>");
   out.println("<html lang=\"en-US\">");
   out.println("<head>");
   out.println("<meta name=\"application-name\" content=\"ForeTees\">");
   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
   out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
   out.println("<title>ForeTees Admin</title>");

    if (new_skin) {

       // out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/assets/stylesheets/sitewide.css\" type=\"text/css\">");
        out.println( Common_skin.getScripts(club, 0, session, req, false));
        
    } else {
       
       out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web%20utilities/foretees.js\"></script>");
    }

   out.println("<style type=\"text/css\"> body {text-align: center} </style>");      // so body will align on center
   
   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   
   out.println("<div style=\"margin:0px auto;\" align=\"center\">");

   out.println("<div class=\"announcement_container\">");

   if (new_skin) out.println(diningPrefix);
   
   if (club.equals("venicegolfandcc")) {
       activity_id = 9999;
   }

   File f;
   FileReader fr = null;
   BufferedReader br = null;
   String tmp = "";
   String path = "";
   
   try {
       
       path = req.getRealPath("") + "/announce/" + club + "/";

       //tmp = "/announce/" +club+ "/" +club+ "_announce.htm";
       tmp = Utilities.getAnnouncementPageFileName(activity_id, user, club, "", false);
       f = new File(path + tmp);
       fr = new FileReader(f);
       br = new BufferedReader(fr);
       
       if ( f.isFile() ) {

           while( (tmp = br.readLine()) != null )
               out.println(tmp);

       }

       out.println("<!-- END INSERT -->");
       out.println("<div class=\"clearfloat\"></div>"); // force floats to clear in case annoucement page doesn't
       out.println("</div><!-- closing announcement_container -->");
       out.println("</div>");

   } catch (FileNotFoundException ignore) {

       out.println("<p>Missing Announcement Page.</p>");

   } catch (SecurityException ignore) {

       out.println("<p>Access Denied.</p>");

   } finally {

       br.close();
       fr.close();

   }

   if (new_skin) out.println(diningSuffix);
   
   out.println("</div></body></html>");
   
   /*
   //
   //  Call is to display the announcement page.
   //
   //  Display a page to provide a link to the club's announcement page
   //
   out.println("<HTML><HEAD><Title>Admin Display Announcements Page</Title>");
   out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/announce/" +club+ "_announce.htm\">");
   out.println("</HEAD>");
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Display Club Announcements</H3>");
   out.println("<BR><BR>");
   out.println("<BR><BR>Click on the link below to display the club announcments.");
   out.println("<BR><BR>");
   out.println("<a href=\"/" +rev+ "/announce/" +club+ "_announce.htm\">Club Announcements</a>");
   out.println("</CENTER></BODY></HTML>");
   */
   
 }  // end of doGet

}
