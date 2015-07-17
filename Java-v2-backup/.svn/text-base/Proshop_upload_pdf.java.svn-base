/***************************************************************************************     
 *   Proshop_upload_pdf:  This servlet will process the request to upload a PDF file.
 *
 *   called by:  Proshop_announce
 *
 *   created: 12/10/2014   Bob P.
 *
 *   last updated:
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.regex.*;
import java.sql.*;

import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;
import com.foretees.common.getActivity;

import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.Part;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.ParamPart;


public class Proshop_upload_pdf extends HttpServlet {
 
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 static String DINING_USER = ProcessConstants.DINING_USER;    // Dining username for Admin user from Dining System


 //
 //  doGet from menu
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) return;
   
   Connection con = Connect.getCon(req);                     // get DB connection
   
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
   
   String user = (String)session.getAttribute("user");
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "TOOLS_ANNOUNCE", con, out) && !user.equals("proshopautofb")) {
       SystemUtils.restrictProshop("TOOLS_ANNOUNCE", out);
       return;
   }
   
   String club = (String)session.getAttribute("club");   // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int activity_id = (Integer)session.getAttribute("activity_id");
   
   String endParm = "";
     
   if (req.getParameter("sendemail") != null) {        // if caller is Send_email

       endParm = "&sendemail";
   }   
     
   //
   //   output the html page to start the upload process
   //
   out.println(SystemUtils.HeadTitle("Upload File"));
     
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

   
   if (req.getParameter("delete") != null) {        // if call to delete a file

       deleteFile(req, resp, club, user, activity_id, con, out);
       return;
   } 
   
   
   String act_folder = "Golf";
   
   if (activity_id > 0) {
       
       act_folder = getActivity.getActivityName(activity_id, con);        
   }
   

   String path = req.getRealPath("") + "/AEimages/" + club + "/PDFs/" +act_folder+ "/";   // PDF folder

   boolean z = false;

   File dir = new File( path );
   File[] files = dir.listFiles();

   out.println("<br><table align=\"center\" cellpadding=\"7\" border=\"1\" bgcolor=\"#336633\">");
   out.println("<tr><td>");
    out.println("<font color=\"#FFFFFF\" size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<p align=\"center\"><b>Upload a PDF File</b></p>");          
    out.println("</font>");
    out.println("<font color=\"#FFFFFF\" size=\"2\">");
    out.println("<p align=\"center\">Must be a single PDF file with any name ending in <strong>.pdf</strong>.<br><br>" +
                    "The file cannot be larger than 5 MB.<br>If there is " +
                    "already a file with this name it will be replaced by this new file.</p>");

      out.println("</font></td></tr></table>");
      out.println("<font size=\"2\"><br>");
   out.println("<br><table align=\"center\" cellpadding=\"5\" border=\"2\" bgcolor=\"#F5F5DC\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("Use the button below to locate the PDF file on your computer or network.<br><br>");
   //
   //  Note:  hidden parms will not work here - use the parms on the end of the URL (filler is not used)
   //
   out.println("<form action=\"Proshop_upload_pdf?filler" +endParm+ "\" method=\"post\" enctype=\"multipart/form-data\">");
   out.println("<input type=\"file\" name=\"filename\">");
   out.println("</font></td></tr></table>");
   out.println("<br>");
   out.println("<input type=\"submit\" value=\"Upload Selected File\" name=\"send\" style=\"text-decoration:underline\" style=\"background:#8B8970\">");
   out.println("</form>");
   out.println("<br><br>");
   out.println("</font>");
   
   //  Add some notes about using these files for links
   out.println("<br><table align=\"center\" width=\"50%\" cellpadding=\"5\" border=\"2\" bgcolor=\"#F5F5DC\">");
   out.println("<tr><td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println("<strong>NOTES:</strong><br>");
   out.println("To create a link to a PDF file within an email message or on an announcement page, do one of the following:<br>");
   out.println("<ul>");
   out.println("<li>Copy the Full URL from the table below and paste it into the 'Link URL' field in "
           + "the 'Insert/Edit Link' box.</li>");
   out.println("<li>Use the Browse button in the 'Insert/Edit Link' box to locate the PDF file. &nbsp;Be sure to navigate to "
           + "the 'PDFs' folder found under the Folders list.</li>");
   out.println("</ul>");
   out.println("</font></td></tr></table>");
   
   
   
   //  List all the existing PDF files and provide URL link info and the ability to delete the file
   
   out.println("<font size=\"2\"><br><br>");
   out.println("Your Current PDF Files<br>(Please delete if no longer needed)<br><br></font>");
   
    out.println("<table border=\"1\" align=\"center\" cellpadding=\"5\">");
    out.println("<tr bgcolor=\"#336633\">");
    out.println("<td><font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">File Name (click to view)</font></td>");
    out.println("<td><font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">Full URL - Copy and Paste For Use in Links</font></td>");
    out.println("<td>&nbsp;</td>");
    out.println("</tr>");
        
    if (files != null) {
        
        for (File f : files) {

            String tmp = f.getName();

            out.println("<tr bgcolor=\"" + ((z=z==false) ? "#FFFFFF" : "#F5F5DC") + "\">");

            out.println("<td class=\"fileLink\"><a href=\"http://web.foretees.com/v5/AEimages/" +club+ "/PDFs/" +act_folder+ "/" + tmp + "\" target=\"_blank\">" + tmp + "</a></td>");

            out.println("<td class=\"fileLink\">http://web.foretees.com/v5/AEimages/" +club+ "/PDFs/"  +act_folder+ "/"+ tmp + "</td>");

            out.println("<td><a href=\"Proshop_upload_pdf?delete" +endParm+ "&file=" + tmp + "\" class=\"fileLink\" style=\"font-size:10pt\" onclick=\"return confirm('You are about to PERMENATLY delete a file! This action cannot be undone!\\n\\nAre you sure you want to delete this file?')\">Delete</a></td>");

            out.println("</tr>");
        }
    }
    
    out.println("</table>");
    

   out.println("<font size=\"2\"><br><br>");
   if (endParm.equals("") && !user.equals("proshopautofb")) {
        out.println("<form method=\"get\" action=\"Proshop_announce\">");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline\" style=\"background:#8B8970\">");
        out.println("</form>");
   } else {
       out.println("<form action=\"#\" onsubmit=\"self.close()\"><input type=\"submit\" value=\"RETURN\" style=\"text-decoration:underline; background:#8B8970\" onClick='self.close();'></form>");             
   }
   out.println("</font></CENTER></BODY></HTML>");
   out.close();
 }


 //
 // Process the form request from above
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = Connect.getCon(req);                     // get DB connection
   
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
   

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   String user = (String)session.getAttribute("user");
   String club = (String)session.getAttribute("club");     // get club name
   int activity_id = (Integer)session.getAttribute("activity_id");
   
   String fileName = "";
      
   boolean endParm = false;
   
   if (req.getParameter("sendemail") != null) {        // if caller is Send_email

       endParm = true;
   }   
         

   String act_folder = "Golf";
   
   if (activity_id > 0) {
       
       act_folder = getActivity.getActivityName(activity_id, con); 
   }
   

   //
   //  Use a copy of Oreilly's MultipartRequest class to to process the file received.
   //  Pass it the request, a directory to save the file to (our root), and the
   //  max file size to receive (5 MB).
   //
   //   refer to page 123 of OReilly's Java Servlet Programming book by Jason Hunter!!
   //
   int fsize = 5120;           // max file size = 5 MB
   
   String serverFile = req.getRealPath("") + "/AEimages/" + club + "/PDFs/" +act_folder;   // determine the server folder for storing PDF files      
      
   int fail = 0;

   try {
      
      ProshopMultipartRequest multi = new ProshopMultipartRequest(req, serverFile, fsize * 1024, club);  // change for testing
      
      fileName = multi.FileName;       // get the name of the file the user uploaded
   
   }   
   catch (Exception e1) {
      
      SystemUtils.logError("Proshop_upload_pdf: ERR=" + e1.getMessage());
      fail = 1;
   }

   if (fail != 0) {
       
         out.println(SystemUtils.HeadTitle("File Error - Redirect"));
         out.println("<BODY><CENTER>");
         out.println("<BR><H2>File Transfer Error</H2><BR>");
         out.println("<BR><BR>Sorry, there was a problem transferring your file.");
         out.println("<BR><BR>Please check the file name and try again.");
         out.println("<BR><BR>The file name you selected could be invalid or the file was too large.");
         out.println("<BR>It must be a PDF file less than 5 MB in size and end with '.pdf'.<BR>");
         out.println("<BR>If problem persists, please contact pro support.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         if (!endParm && !user.equals("proshopautofb")) {
            out.println("<form method=\"get\" action=\"Proshop_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");
         } else {
              out.println("<form action=\"#\" onsubmit=\"self.close()\"><input type=\"submit\" value=\"RETURN\" style=\"text-decoration:underline; background:#8B8970\" onClick='self.close();'></form>");             
         }
         out.println("</font></CENTER></BODY></HTML>");
         return;
        
   } else if (fileName == null || fileName.equals("")) {     // no file selected
       
         out.println(SystemUtils.HeadTitle("File Error - Redirect"));
         out.println("<BODY><CENTER>");
         out.println("<BR><H2>File Transfer Error</H2><BR>");
         out.println("<BR><BR>No file selected.");
         out.println("<BR><BR>Please use the 'Choose File' button to select a PDF file.");
         out.println("<BR>If problem persists, please contact pro support.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"Proshop_upload_pdf\">");
         if (endParm) out.println("<input type=\"hidden\" name=\"sendemail\">");       
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
       
   } else {   // ok
       
        out.println(SystemUtils.HeadTitle("File Transfer Confirmation"));
        out.println("<BODY><CENTER>");
        out.println("<BR><H2>File Transfer Complete</H2><BR>");
        out.println("<BR><BR>Thank you, your file has been transferred.<BR>");
        out.println("<BR><BR>You can link to the file by using the following URL:"
                  + "<BR><BR>&nbsp;&nbsp;&nbsp;&nbsp;<strong>http://web.foretees.com/v5/AEimages/" +club+ "/PDFs/" +act_folder+ "/" +fileName+ "</strong>");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"Proshop_upload_pdf\">");
        if (endParm) out.println("<input type=\"hidden\" name=\"sendemail\">");       
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");       
        out.println("</CENTER></BODY></HTML>");
   }
 }
 
 
 private void deleteFile(HttpServletRequest req, HttpServletResponse resp, String club, String user, int activity_id, Connection con, PrintWriter out) {

     
    boolean endParm = false;
   
    if (req.getParameter("sendemail") != null) {        // if caller is Send_email

       endParm = true;
    }   
         
    String act_folder = "Golf";
   
    if (activity_id > 0) {
              
       act_folder = getActivity.getActivityName(activity_id, con); 
    }
   

    String file = (req.getParameter("file") != null) ? req.getParameter("file") : "";

    File f;
    String tmp = "";
    String path = "";

    try {

       path = req.getRealPath("");
       tmp = "/AEimages/" + club + "/PDFs/" +act_folder+ "/" + file;
       f = new File(path + tmp);
       if (f.isFile()) f.delete();

    } catch (Exception e) {
       out.println("<br><br><p align=center>PDF file \"" + file + "\" is in use by another user or has already been deleted.</p>");
       out.println("<p align=center>Error: " + e.toString() + "</p>");
       out.println("<p align=center>Please contact support if problem persists.</p>");
       out.println("</BODY></HTML>");
       return;
    }

    out.println("<br><center><h2><span style=\"background-color:yellow\">Your PDF file \"" + file + "\" has been successfully removed.</font></h2></center><br>");

    out.println("<BR><BR>");
    out.println("<font size=\"2\">");
    out.println("<form method=\"get\" action=\"Proshop_upload_pdf\">");
    if (endParm) out.println("<input type=\"hidden\" name=\"sendemail\">");       
    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form></font>");       
    out.println("</CENTER></BODY></HTML>");
 }
  
}
