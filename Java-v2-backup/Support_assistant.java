/***************************************************************************************
 *   Support_assistant: This servlet will provide assistance to members when they
 *                      trouble logging in to the ForeTees site
 *
 *
 *   called by: 
 *
 *   created: 1/06/2008   Paul S..
 *
 *   last updated:      ******* keep this accurate *******
 *
 *              
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Multipart.*;
import javax.activation.*;
//import javax.mail.util.*;



public class Support_assistant extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 
 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
   
   if (req.getParameter("cookie") != null) {
       doCookieTest(req, out);
       return;
   } else if (req.getParameter("email") != null) {
       buildEmailForm(req, out);
       return;
   } else if (req.getParameter("vcal") != null) {
       vCalTest(req, out);
       return;
   }
   
   out.println("<h3>Troubleshoot Menu</h3>");
   out.println("<a href='?cookie'>Cookie Test</a><br>");
   out.println("<a href='?vcal'>vCalendar Test</a><br>");
   out.println("<a href='?email'>Contact our Support Team</a><br>");
   out.println("<a href='?exit'>Exit & Return to Login Page</a><br>");
   
   //String test = "Test this";
   //out.println("startsWith('test') = " + ((test.startsWith("test")) ? "Yes" : "No"));
   //out.println("startsWith('Test') = " + ((test.startsWith("Test")) ? "Yes" : "No"));
   
   String test = "Robert Mulé";
   out.println("<br>name=" + test);
   test = toTitleCase(test);
   out.println("<br>name=" + test);
   
 }
 
   private final static String toTitleCase( String s ) {

      char[] ca = s.toCharArray();

      boolean changed = false;
      boolean capitalise = true;

      for ( int i=0; i<ca.length; i++ ) {
         char oldLetter = ca[i];
         if ( oldLetter <= '/'
              || ':' <= oldLetter && oldLetter <= '?'
              || ']' <= oldLetter && oldLetter <= '`' ) {
            /* whitespace, control chars or punctuation */
            /* Next normal char should be capitalized */
            capitalise = true;
         } else {
            char newLetter  = capitalise
                              ? Character.toUpperCase(oldLetter)
                              : Character.toLowerCase(oldLetter);
            ca[i] = newLetter;
            changed |= (newLetter != oldLetter);
            capitalise = false;
         }
      } // end for

      return new String (ca);

 } // end toTitleCase

   
 private static void vCalTest(HttpServletRequest req, PrintWriter out) {
     
/*
      //
      //   Prepare fields for vCalendar attachment (vCal file for member to add notification to Outlook)
      //
      String vCalDate = String.valueOf(20080701) + "T";

      vCalDate += String.valueOf(1500);             // yyyymmddThhmm    
      

      String vCalId = vCalDate + "-foretees";
      String vCalFileName = "Notification.ics";

      String enewMsg = "Message Body";
      String subject = "vCal Test";
      
      StringBuffer vCalMsg = new StringBuffer();       // use string buffer to build file

      vCalMsg.append("BEGIN:vCalendar\n" +
               "VERSION:5.0\n" +
               "BEGIN:vEvent\n" +
               "DTSTART:" + vCalDate + "\n" +
               "DTEND:" + vCalDate + "\n");            // Start of vCal file contents
    
      out.println("<p>vCalMsg = " +vCalMsg.toString());
      
     
      Properties properties = new Properties();
      properties.put("mail.smtp.host", "216.243.184.88");          // set outbound host address
      properties.put("mail.smtp.port", "20025");                   // set port address
      properties.put("mail.smtp.auth", "true");                    // set 'use authentication'

      Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

      MimeMessage message = new MimeMessage(mailSess);
        
      try {
        
         message.addRecipient(Message.RecipientType.TO, new InternetAddress("paul@foretees.com"));
         message.setFrom(new InternetAddress("auto-send@foretees.com"));           // set from addr
         message.setSubject( subject );                                        // set subject line
         message.setSentDate(new java.util.Date());                                // set date/time sent
         
      }
      catch (Exception e1) {

         out.println("Error: " + e1);
      }

      
      
      //************************************************************************
      //  Build the Message Parts so we can add the vCal file as an attachment
      //************************************************************************
      //
      Multipart mp = new MimeMultipart();           // create a new Multi Part to hold both the text and file
      
      MimeBodyPart mbp1 = new MimeBodyPart();       // body part to hold the notification message built below
      
      MimeBodyPart mbp2 = new MimeBodyPart();       // body part to hold the vCal file
      
      try {
         
         mbp2.setFileName(vCalFileName);            // set the name of our file
         
      }
      catch (Exception e1) {
          
         out.println("Error: " + e1);
      }
      

        try {
           vCalMsg.append("SUMMARY:" + subject + "\n" +
                "DESCRIPTION:" + enewMsg + "\n" +
                "UID:" + vCalId + "\n" +
                "END:vEvent\n" +
                "END:vCalendar");

           //mbp2.setContent(vCalMsg.toString(), "text/calendar");           // set the file content

           mbp2.setDataHandler(new DataHandler(new ByteArrayDataSource(vCalMsg, "text/calendar")));           // set the file content

           mbp1.setText(enewMsg);       // put email message in first body part

           mp.addBodyPart(mbp1);        // add it to the msg part

           mp.addBodyPart(mbp2);        // add vCal file to the msg part

           message.setContent(mp);      // put entire mp in the message


           Transport.send(message);     // send it!!
      }
      catch (Exception e1) {
          
         out.println("Error: " + e1);
      }
 */
     
    try {
    
      Properties properties = new Properties();
      properties.put("mail.smtp.host", "216.243.184.88");          // set outbound host address
      properties.put("mail.smtp.port", "20025");                   // set port address
      properties.put("mail.smtp.auth", "true");                    // set 'use authentication'

      Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

      MimeMessage message = new MimeMessage(mailSess);

      String vCalDate = String.valueOf(20080701) + "T";

      vCalDate += String.valueOf(1500);             // yyyymmddThhmm    
      
      String vCalId = vCalDate + "-foretees";
      String vCalFileName = "Notification.ics";

      String enewMsg = "Message Body";
      String subject = "vCal Test";
      
      try {
        
         message.addRecipient(Message.RecipientType.TO, new InternetAddress("paul@foretees.com"));
         message.setFrom(new InternetAddress("auto-send@foretees.com"));           // set from addr
         message.setSubject( "Test #2" );                                        // set subject line
         message.setSentDate(new java.util.Date());                                // set date/time sent
         
      }
      catch (Exception e1) {

         out.println("Error: " + e1);
      }
      
      StringBuffer vCalMsg = new StringBuffer();       // use string buffer to build file

      vCalMsg.append("" +
                "BEGIN:vCalendar\n" +
                "VERSION:5.0\n" +
                "BEGIN:vEvent\n" +
                "DTSTART:20080701\n" +
                "DTEND:20080701\n" + 
                "SUMMARY:" + subject + "\n" +
                "DESCRIPTION:" + enewMsg + "\n" +
                "UID:" + vCalId + "\n" +
                "END:vEvent\n" +
                "END:vCalendar");
                
      out.println("<p>vCalMsg = " +vCalMsg.toString() + "</p>" +
              "<p>" + new java.util.Date() + "</p>");      
     
      
      
      
      BodyPart msgBodyPart = new MimeBodyPart();
      
      msgBodyPart.setText("Attached is your iCalendar file.");
      
      Multipart multipart = new MimeMultipart();
      
      multipart.addBodyPart(msgBodyPart);
      
      
      msgBodyPart = new MimeBodyPart();
      msgBodyPart.setFileName("teetime.vcs");
      msgBodyPart.setContent(vCalMsg.toString(), "text/plain");
      
      multipart.addBodyPart(msgBodyPart);
      
      message.setContent(multipart);
      
      Transport.send(message);
           
    }
    catch (Exception e1) {

        out.println("Error: " + e1);
    }
 }
 
 
 private static void doCookieTest(HttpServletRequest req, PrintWriter out) {

     if (req.getParameter("step2") == null) {
     
         // do step 1
         HttpSession session = req.getSession(true);   // Create a new session object
         session.setAttribute("count", 1);             // testing variable

         out.println("We've placed a cookie on your computer.<br>");
         out.println("<a href='?cookie&step2'>Click here to continue.</a>");
         
     } else {
         
         // do step2
         HttpSession session = null;
         session = req.getSession(false);  // Get user's session object (no new one)

         if (session == null) {
             
             out.println("Your computer is not excepting the cookie.");
             
         } else {
             
             out.println("Your computer has excepted the cookie.  You should have no problems logging into ForeTees.");
             out.println("<a href=''>Return</a>;");
         }
         
     }
 }
 
 
 private static void buildEmailForm(HttpServletRequest req, PrintWriter out) {
     
    boolean ie = false;
    boolean ns6 = false;
    boolean ns4 = false;
    boolean safari = false;
    boolean gecko = false;
    boolean opera = false;
    boolean win = false;
    boolean mac = false;
    
    String ua = req.getHeader("User-Agent");
    ua = ua.toLowerCase();
    
    if(ua.indexOf("msie") != -1) {
        ie = true;
    } else if(ua.indexOf("netscape6") != -1) {
        ns6 = true;
    } else if(ua.indexOf("mozilla") != -1) {
        ns4 = true;
    } else if(ua.indexOf("safari") != -1) {
        safari = true;
    } else if(ua.indexOf("gecko") != -1) {
        gecko = true;
    } else if(ua.indexOf("opera") != -1) {
        opera = true;
    }
    
    if(ua.indexOf("win") != -1) {
        win = true;
    } else if(ua.indexOf("mac") != -1) {
        mac = true;
    }


    out.println("<p>");
    
    out.println("<br>ie=" + ie);
    out.println("<br>ns6=" + ns6);
    out.println("<br>ns4=" + ns4);
    out.println("<br>safari=" + safari);
    out.println("<br>gecko=" + gecko);
    out.println("<br>opera=" + opera);
    out.println("<br>win=" + win);
    out.println("<br>mac=" + mac);
    out.println("<br>");
    out.println("<br>UA=" + ua);
    
    out.println("</p>");
    
    out.println("<table align=center cellpadding=15 border=1 bgcolor=\"#F5F5DC\"><tr><td>");

    out.println("<table align=center>");

    out.println("<tr><td colspan=3 align=center><font size=3><b>Contact Support</b></font></td></tr>");

    out.println("<tr>");
    out.println("<td align=right>Your Name:</td>");
    out.println("<td>&nbsp;</td>");
    out.println("<td><input type=text name=member size=32></td>");
    out.println("</tr>");

    out.println("<tr>");
    out.println("<td align=right>Your Golf Club:</td>");
    out.println("<td>&nbsp;</td>");
    out.println("<td><input type=text name=club size=32></td>");
    out.println("</tr>");

    out.println("<tr>");
    out.println("<td align=right>Your Email:</td>");
    out.println("<td>&nbsp;</td>");
    out.println("<td><input type=text name=email size=32></td>");
    out.println("</tr>");

    out.println("<tr>");
    out.println("<td align=right>Operating System:</td>");
    out.println("<td>&nbsp;</td>");
    out.println("<td>");
    out.println("<select name=os size=1>");
    out.println("<option value=\"\">Choose...");
    out.println("<option>Windows 95/98/ME");
    out.println("<option>Windows 2000");
    out.println("<option>Windows XP");
    out.println("<option>Windows Vista");
    out.println("<option>Windows Mobile/CE");
    out.println("<option>Mac OSX");
    out.println("<option>Linux");
    out.println("<option>Other");
    out.println("</select>");
    out.println("</td>");
    out.println("</tr>");

    out.println("<tr>");
    out.println("<td align=right>Web Browser:</td>");
    out.println("<td>&nbsp;</td>");
    out.println("<td>");
    out.println("<select name=browser size=1>");
    out.println("<option value=\"\">Choose...");
    out.println("<option>Internet Explorer");
    out.println("<option>Safari");
    out.println("<option>Firefox");
    out.println("<option>Other");
    out.println("</select>");
    out.println("</td>");
    out.println("</tr>");

    out.println("</table>");

    out.println("</td></tr></table>");
 }
 
 
 private static Authenticator getAuthenticator() {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication("support@foretees.com", "fikd18");
       }
    };
    
    return auth;
 
 }
 
} // end of servlet class