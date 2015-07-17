/***************************************************************************************
 *   Support_styles:  This class will allow the user to change the member theme by changing the custom style sheet.
 *
 *
 *   called by:  support_main2.htm and sales_main.htm
 *
 *   created: 9/11/2012   Bob P.
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
import java.sql.*;


public class Support_styles extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                 // init DB objects

   HttpSession session = null;

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username
   String club = (String)session.getAttribute("club");

   if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }
   
   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
         out.println("<BR><BR>");
         if (user.startsWith( "sales" )) {
            out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
         } else {
            out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
         }
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   
   //
   //   Get the current cusotm style sheet, if any
   //
   String customStyle = getCustomStyle(con);
           
   //
   //   Display Custom Style Sheet Options - Prompt for New Style Sheet
   //
   out.println("<HTML><HEAD><TITLE>Support Toggle Custom Style Sheet</TITLE></HEAD>");
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
     
   out.println("<table border=\"1\" bgcolor=\"#8B8970\" width=\"700\">");
   out.println("<tr><td align=\"center\">");
   out.println("<H2>Select or Change The Custom Style Sheet</H2>");
   out.println("<p align=\"center\">Use this utility to select or change the custom color scheme (template) for this club.</p>");
   out.println("</td></tr></table>");

   out.println("<p align=\"center\">&nbsp;</p>");
   
   out.println("<table border=\"2\" bgcolor=\"yellow\" width=\"500\">");
   out.println("<tr><td align=\"center\">");
   out.println("<p align=center><H2>**** WARNING **** </H2>THIS WILL CHANGE THE COLOR SCHEME FOR THIS CLUB IMMEDIATELY ON ALL SERVERS!!!!!</p>");
   out.println("</td></tr></table><BR>");
   
   if (user.startsWith( "sales" )) {
      out.println("<A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
   } else {
      out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   }
   
   out.println("<BR><BR><table border=\"1\" bgcolor=\"#F5F5DC\" width=\"700\">");
   out.println("<tr><td align=\"center\">");
   out.println("<BR>");
      
      out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" width=\"300\" align=\"center\">");
      out.println("<form method=\"post\" action=\"Support_styles\">");
      out.println("<tr><td align=\"left\">");
      
      out.println("<p align=\"center\">Club: <b>" +club+ "</b></p>");
   
      out.println("<p>Select from the following list:</p>");
      
      out.println("<p>");
      out.println("<input type=\"hidden\" name=\"toggle\" value=\"yes\">");       // in case we need to know they clicked this form
      
      if (customStyle.equals("")) {
        out.println("<input type=radio checked value=\"classic\" name=\"filename\"> ForeTees Classic (default)");
      } else {
        out.println("<input type=radio value=\"classic\" name=\"filename\"> ForeTees Classic (default)");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#3a5248\" style=\"color:white\"><p>");
      
      if (customStyle.equals("original.css")) {
        out.println("<BR><input type=radio checked value=\"original.css\" name=\"filename\"> Dan's Original");
      } else {
        out.println("<BR><input type=radio value=\"original.css\" name=\"filename\"> Dan's Original");
      }
      if (customStyle.equals("original-serif.css")) {
        out.println("<BR><input type=radio checked value=\"original-serif.css\" name=\"filename\"> Dan's Original w/Serif Font");
      } else {
        out.println("<BR><input type=radio value=\"original-serif.css\" name=\"filename\"> Dan's Original w/Serif Font");
      }
      if (customStyle.equals("original-serif-cust.css")) {
        out.println("<BR><input type=radio checked value=\"original-serif-cust.css\" name=\"filename\"> Dan's Original w/Custom Serif");
      } else {
        out.println("<BR><input type=radio value=\"original-serif-cust.css\" name=\"filename\"> Dan's Original w/Custom Serif");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#002861\" style=\"color:white\"><p>");
      
      if (customStyle.equals("blue.css")) {
        out.println("<BR><input type=radio checked value=\"blue.css\" name=\"filename\"> Blue");
      } else {
        out.println("<BR><input type=radio value=\"blue.css\" name=\"filename\"> Blue");
      }
      if (customStyle.equals("blue-serif.css")) {
        out.println("<BR><input type=radio checked value=\"blue-serif.css\" name=\"filename\"> Blue w/Serif Font");
      } else {
        out.println("<BR><input type=radio value=\"blue-serif.css\" name=\"filename\"> Blue w/Serif Font");
      }
      if (customStyle.equals("blue-serif-cust.css")) {
        out.println("<BR><input type=radio checked value=\"blue-serif-cust.css\" name=\"filename\"> Blue w/Custom Serif");
      } else {
        out.println("<BR><input type=radio value=\"blue-serif-cust.css\" name=\"filename\"> Blue w/Custom Serif");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#8c6448\"><p>");
      
      if (customStyle.equals("brown.css")) {
        out.println("<BR><input type=radio checked value=\"brown.css\" name=\"filename\"> Brown");
      } else {
        out.println("<BR><input type=radio value=\"brown.css\" name=\"filename\"> Brown");
      }
      if (customStyle.equals("brown-serif.css")) {
        out.println("<BR><input type=radio checked value=\"brown-serif.css\" name=\"filename\"> Brown w/Serif Font");
      } else {
        out.println("<BR><input type=radio value=\"brown-serif.css\" name=\"filename\"> Brown w/Serif Font");
      }
      if (customStyle.equals("brown-serif-cust.css")) {
        out.println("<BR><input type=radio checked value=\"brown-serif-cust.css\" name=\"filename\"> Brown w/Custom Serif");
      } else {
        out.println("<BR><input type=radio value=\"brown-serif-cust.css\" name=\"filename\"> Brown w/Custom Serif");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#8c7c62\"><p>");
      
      if (customStyle.equals("light-brown.css")) {
        out.println("<BR><input type=radio checked value=\"light-brown.css\" name=\"filename\"> Light Brown");
      } else {
        out.println("<BR><input type=radio value=\"light-brown.css\" name=\"filename\"> Light Brown");
      }
      if (customStyle.equals("light-brown-serif.css")) {
        out.println("<BR><input type=radio checked value=\"light-brown-serif.css\" name=\"filename\"> Light Brown w/Serif Font");
      } else {
        out.println("<BR><input type=radio value=\"light-brown-serif.css\" name=\"filename\"> Light Brown w/Serif Font");
      }
      if (customStyle.equals("light-brown-serif-cust.css")) {
        out.println("<BR><input type=radio checked value=\"light-brown-serif-cust.css\" name=\"filename\"> Light Brown w/Custom Serif");
      } else {
        out.println("<BR><input type=radio value=\"light-brown-serif-cust.css\" name=\"filename\"> Light Brown w/Custom Serif");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#676e4c\"><p>");
      
      if (customStyle.equals("green.css")) {
        out.println("<BR><input type=radio checked value=\"green.css\" name=\"filename\"> Green");
      } else {
        out.println("<BR><input type=radio value=\"green.css\" name=\"filename\"> Green");
      }
      if (customStyle.equals("green-serif.css")) {
        out.println("<BR><input type=radio checked value=\"green-serif.css\" name=\"filename\"> Green w/Serif Font");
      } else {
        out.println("<BR><input type=radio value=\"green-serif.css\" name=\"filename\"> Green w/Serif Font");
      }
      if (customStyle.equals("green-serif-cust.css")) {
        out.println("<BR><input type=radio checked value=\"green-serif-cust.css\" name=\"filename\"> Green w/Custom Serif");
      } else {
        out.println("<BR><input type=radio value=\"green-serif-cust.css\" name=\"filename\"> Green w/Custom Serif");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#4C646B\"><p>");
      
      if (customStyle.equals("contemporary.css")) {
        out.println("<BR><input type=radio checked value=\"contemporary.css\" name=\"filename\"> Contemporary");
      } else {
        out.println("<BR><input type=radio value=\"contemporary.css\" name=\"filename\"> Contemporary");
      }
      if (customStyle.equals("contemporary-serif.css")) {
        out.println("<BR><input type=radio checked value=\"contemporary-serif.css\" name=\"filename\"> Contemporary w/Serif Font");
      } else {
        out.println("<BR><input type=radio value=\"contemporary-serif.css\" name=\"filename\"> Contemporary w/Serif Font");
      }
      if (customStyle.equals("contemporary-serif-cust.css")) {
        out.println("<BR><input type=radio checked value=\"contemporary-serif-cust.css\" name=\"filename\"> Contemporary w/Custom Serif");
      } else {
        out.println("<BR><input type=radio value=\"contemporary-serif-cust.css\" name=\"filename\"> Contemporary w/Custom Serif");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#4c4c4c\"><p>");
      
      if (customStyle.equals("dark.css")) {
        out.println("<BR><input type=radio checked value=\"dark.css\" name=\"filename\"> Dark");
      } else {
        out.println("<BR><input type=radio value=\"dark.css\" name=\"filename\"> Dark");
      }
      if (customStyle.equals("dark-serif.css")) {
        out.println("<BR><input type=radio checked value=\"dark-serif.css\" name=\"filename\"> Dark w/Serif Font");
      } else {
        out.println("<BR><input type=radio value=\"dark-serif.css\" name=\"filename\"> Dark w/Serif Font");
      }
      if (customStyle.equals("dark-serif-cust.css")) {
        out.println("<BR><input type=radio checked value=\"dark-serif-cust.css\" name=\"filename\"> Dark w/Custom Serif");
      } else {
        out.println("<BR><input type=radio value=\"dark-serif-cust.css\" name=\"filename\"> Dark w/Custom Serif");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#a3a691\"><p>");
      
      if (customStyle.equals("faded.css")) {
        out.println("<BR><input type=radio checked value=\"faded.css\" name=\"filename\"> Faded");
      } else {
        out.println("<BR><input type=radio value=\"faded.css\" name=\"filename\"> Faded");
      }
      if (customStyle.equals("faded-serif.css")) {
        out.println("<BR><input type=radio checked value=\"faded-serif.css\" name=\"filename\"> Faded w/Serif Font");
      } else {
        out.println("<BR><input type=radio value=\"faded-serif.css\" name=\"filename\"> Faded w/Serif Font");
      }
      if (customStyle.equals("faded-serif-cust.css")) {
        out.println("<BR><input type=radio checked value=\"faded-serif-cust.css\" name=\"filename\"> Faded w/Custom Serif");
      } else {
        out.println("<BR><input type=radio value=\"faded-serif-cust.css\" name=\"filename\"> Faded w/Custom Serif");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#75c46b\"><p>");
      
      if (customStyle.equals("neon.css")) {
        out.println("<BR><input type=radio checked value=\"neon.css\" name=\"filename\"> Neon");
      } else {
        out.println("<BR><input type=radio value=\"neon.css\" name=\"filename\"> Neon");
      }
      if (customStyle.equals("neon-serif.css")) {
        out.println("<BR><input type=radio checked value=\"neon-serif.css\" name=\"filename\"> Neon w/Serif Font");
      } else {
        out.println("<BR><input type=radio value=\"neon-serif.css\" name=\"filename\"> Neon w/Serif Font");
      }
      if (customStyle.equals("neon-serif-cust.css")) {
        out.println("<BR><input type=radio checked value=\"neon-serif-cust.css\" name=\"filename\"> Neon w/Custom Serif");
      } else {
        out.println("<BR><input type=radio value=\"neon-serif-cust.css\" name=\"filename\"> Neon w/Custom Serif");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#6a241c\" style=\"color:white\"><p>");
      
      if (customStyle.equals("niners.css")) {
        out.println("<BR><input type=radio checked value=\"niners.css\" name=\"filename\"> Forty Niners");
      } else {
        out.println("<BR><input type=radio value=\"niners.css\" name=\"filename\"> Forty Niners");
      }
      if (customStyle.equals("niners-serif.css")) {
        out.println("<BR><input type=radio checked value=\"niners-serif.css\" name=\"filename\"> Forty Niners w/Serif Font");
      } else {
        out.println("<BR><input type=radio value=\"niners-serif.css\" name=\"filename\"> Forty Niners w/Serif Font");
      }
      if (customStyle.equals("niners-serif-cust.css")) {
        out.println("<BR><input type=radio checked value=\"niners-serif-cust.css\" name=\"filename\"> Forty Niners w/Custom Serif");
      } else {
        out.println("<BR><input type=radio value=\"niners-serif-cust.css\" name=\"filename\"> Forty Niners w/Custom Serif");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#C60202\" style=\"color:white\"><p>");
      
      if (customStyle.equals("huskers.css")) {
        out.println("<BR><input type=radio checked value=\"huskers.css\" name=\"filename\"> Corn Huskers");
      } else {
        out.println("<BR><input type=radio value=\"huskers.css\" name=\"filename\"> Corn Huskers");
      }
      if (customStyle.equals("huskers-serif.css")) {
        out.println("<BR><input type=radio checked value=\"huskers-serif.css\" name=\"filename\"> Corn Huskers w/Serif Font");
      } else {
        out.println("<BR><input type=radio value=\"huskers-serif.css\" name=\"filename\"> Corn Huskers w/Serif Font");
      }
      if (customStyle.equals("huskers-serif-cust.css")) {
        out.println("<BR><input type=radio checked value=\"huskers-serif-cust.css\" name=\"filename\"> Corn Huskers w/Custom Serif");
      } else {
        out.println("<BR><input type=radio value=\"huskers-serif-cust.css\" name=\"filename\"> Corn Huskers w/Custom Serif");
      }

      out.println("</p></td></tr><tr><td align=\"left\" bgcolor=\"#F5F5DC\">");
      
      out.println("<p><BR><input type=\"submit\" value=\"Change to This Selection\" style=\"text-decoration:underline;\">");
      
      out.println("</p><br></td></tr></form></table>");
      
      
   if (user.equals( "support" )) {    // option for support only

      out.println("<p align=\"center\"><BR>---- OR ----<BR><BR></p>");
   
            
      out.println("<p align=\"center\"><form method=\"post\" action=\"Support_styles\">");
    
      out.println("Specify the file name (i.e.  dark-brown.css):<BR><input type=\"text\" name=\"filename\" value=\"" +customStyle+ "\" size=\"30\" maxlength=\"30\">");
      
      out.println("<br><br><input type=\"submit\" value=\"Change to This File\" style=\"text-decoration:underline;\">");
      out.println("</form><br></p>");
   }
   out.println("</td></tr></table>");

      

   out.println("<BR>");
   if (user.startsWith( "sales" )) {
      out.println("<A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
   } else {
      out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   }
   out.println("<BR><BR></CENTER></BODY></HTML>");
   out.close();
   
   try {
      con.close();
   }
   catch (Exception exc) {
   }
   
 }


 // *********************************************************
 //  doPost - perform the enable or disable 
 // *********************************************************
   
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                 // init DB objects
   PreparedStatement stmt = null;
   ResultSet rs = null;


   HttpSession session = null;

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username
   String club = (String)session.getAttribute("club");

   if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
         out.println("<BR><BR>");
         if (user.startsWith( "sales" )) {
            out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
         } else {
            out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
         }
      out.println("</CENTER></BODY></HTML>");
      return;
   }



   //
   //   Get the css file name that was selected and set it in club5
   //
   String filename = "";

   if (req.getParameter("filename") != null) {

      filename = req.getParameter("filename");
   }
   
   if (filename.equalsIgnoreCase("classic")) filename = "";   // empty defaults to classic style

   try {

       stmt = con.prepareStatement (
               "UPDATE club5 SET custom_styles = ?");

       stmt.clearParameters();          
       stmt.setString(1, filename);
       stmt.executeUpdate(); 

       stmt.close();

         out.println("<HTML><HEAD><TITLE>Support Set Styles</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>CSS Style Sheet File Changed</H3>");
         out.println("<BR>The style sheet has been changed to " +filename+ " for club: <b>" +club+ "</b>");
         out.println("<BR><BR>");

         if (user.startsWith( "sales" )) {
            out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
         } else {
            out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
         }
         out.println("</CENTER></BODY></HTML>");
         out.close();

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H2>Database Error</H2><BR>");
      out.println("<BR><BR>Error trying to change the CSS File Name in club5. Error = " + e1.getMessage());
      out.println("<BR><BR><BR>");
      if (user.startsWith( "sales" )) {
         out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
      } else {
         out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
      }
      out.println("</CENTER></BODY></HTML>");
   }

   try {
      con.close();
   }
   catch (Exception exc) {
   }
   
 }


 // *********************************************************
 // Get the current custom style sheet for this club
 // *********************************************************

 private String getCustomStyle(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;

    String style = "";
     
    try {

        stmt = con.createStatement();              // create a statement

        rs = stmt.executeQuery("SELECT custom_styles FROM club5");

        if (rs.next()) {

            style = rs.getString(1);     // get the custom style sheet name, if any
        }
     
        stmt.close();

    }
    catch (Exception ignore) {
    }
         
    return(style);
 }
 
 
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR> <FORM>");
   out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'>");
   out.println("</FORM></CENTER></BODY></HTML>");

 }
 
 
}
