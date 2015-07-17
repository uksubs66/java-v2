/***************************************************************************************
 *   Purpose: This servlet is used to test sending emails from within ForeTees
 *
 *
 *   Notes:
 *                  
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.io.*;


public class ett extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


    if (req.getParameter("todo") != null && req.getParameter("todo").equals("cmd")) {

        doCmd(req, resp);
    }

/*
    if (req.getParameter("todo") != null && req.getParameter("todo").equals("getImage")) {

        Connection con = null;
          Statement stmt = null;
          ResultSet rs = null;
          Blob len1 = null;

          try {

             con = dbConn.Connect("demov4");
             stmt = con.createStatement();
             rs = stmt.executeQuery("SELECT * FROM member_photos WHERE id = 1");

             if (rs.next()) {

                len1 = rs.getBlob("photo");

                int len = (int)len1.length();
                byte [] b = new byte[len];
                InputStream readImg = rs.getBinaryStream(1);

                int index=readImg.read(b, 0, len);
                //System.out.println("index"+index);
                resp.reset();
                resp.setContentType("image/jpg");
                resp.getOutputStream().write(b,0,len);
                resp.getOutputStream().flush();

             }

             stmt.close();

          } catch (Exception e1) {

              //Utilities.logError("DinReq Error in sendEmail.sendIt() for " + club + ": " + e1.getMessage() + ", " + e1.toString());

          } finally {

              try { rs.close(); }
              catch (SQLException ignored) {}

              try { stmt.close(); }
              catch (SQLException ignored) {}
          }


        return;

    }
*/
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
 
    out.println("<html>");
    out.println("<head>");
    out.println("<title>4T-ETT</title>");
    out.println("</head>");
    out.println("");
    out.println("");
    
    out.println("<script>");
    out.println("var upload_number = 2;");
    out.println("function addFileInput() {");
    out.println("if(upload_number > 3) { alert('Sorry you can only upload 3 files.'); exit(0); }");
    out.println(" var d = document.createElement(\"div\");");
    out.println(" var l = document.createElement(\"a\");");
    out.println(" var file = document.createElement(\"input\");");
    out.println(" file.setAttribute(\"type\", \"file\");");
    out.println(" file.setAttribute(\"id\", \"attachment\"+upload_number);");
    out.println(" file.setAttribute(\"name\", \"attachment\"+upload_number);");
    out.println(" l.setAttribute(\"href\", \"javascript:removeFileInput('f\"+upload_number+\"')\");");
    out.println(" l.appendChild(document.createTextNode(\"Remove\"))");
    out.println(" d.setAttribute(\"id\", \"f\"+upload_number);");
    out.println(" d.appendChild(file);");
    out.println(" d.appendChild(l);");
    out.println(" document.getElementById(\"moreUploads\").appendChild(d);");
    out.println(" upload_number++;");
    out.println("}");
    out.println("function removeFileInput(i) {");
    out.println(" var elm = document.getElementById(i);");
    out.println(" document.getElementById(\"moreUploads\").removeChild(elm);");
    out.println(" upload_number--;");
    out.println("}");
    out.println("</script>");

    out.println("<body onload=\"document.myForm.enctype='application/x-www-form-urlencoded';\">");
    out.println("");

    out.println("<FORM ENCTYPE=\"multipart/form-data\" method=post action=\"/v5/servlet/ett\" name=myForm>"); // multipart/form-data application/x-www-form-urlencoded
    //out.println("<INPUT TYPE=\"file\" NAME=\"attachment\">");
    //out.println("<INPUT TYPE=\"submit\" VALUE=\"Upload\">");
    out.println("<INPUT TYPE=\"text\" name=var1 VALUE=\"Blah Blah\">");
    out.println("<INPUT TYPE=\"hidden\" name=var2 VALUE=\"Foo Bar\">");
    out.println("<input type=\"file\" name=\"attachment1\" id=\"attachment1\" onchange=\"document.getElementById('moreUploadsLink').style.display = 'block';\">");
    out.println("<div id=\"moreUploads\"></div>");
    out.println("<div id=\"moreUploadsLink\" style=\"display:none;\"><a href=\"javascript:addFileInput();\">Attach another File</a></div>");

    out.println("<br><input type=button value=' Send ' onclick=\"sendEmail()\">");

    out.println("</FORM>");
    out.println("");
    out.println("<script>");
    out.println("function sendEmail() {");
    out.println(" document.myForm.enctype='multipart/form-data';");
    out.println(" document.myForm.submit();");
    out.println(" ");
    out.println(" ");
    out.println("}");
    out.println("</script>");
    out.println("");
    out.println("");


    out.println("<form>");

    out.println("<>");


    out.println("</FORM>");



/*
    out.println("<form method=get>");

    out.println("<input type=hidden name=todo value=getImage>");
    out.println("<input type=text name=imgId value='1'>");
    out.println("<input type=submit value=Display>");

    out.println("</form>");

    out.println("<form method=post enctype=\"multipart/form-data\">");

    //out.println("<input type=hidden name=todo value=getImage>");
    out.println("<input type=file name=attachement1>");
    out.println("<input type=submit value=Upload>");

    out.println("</form>");

*/

/*
    if (req.getParameter("thefile") != null) {

        out.println("<pre>" + req.getParameter("thefile") + "</pre>");
        

    }
*/



    out.println("</body>");
    out.println("</html>");
    out.close();

 }


 public void doPost_photos(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    out.println("<html>");
    out.println("<head>");
    out.println("<title>4T-ETT</title>");
    out.println("</head>");
    out.println("<body>");

    out.println("");
    out.println("");

    if (!ServletFileUpload.isMultipartContent(req)) {
        
        out.println("No File Uploaded!"); 
        return;
    }

    ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
    servletFileUpload.setSizeMax(1024 * 512); // 512KB
    List fileItemsList = null;
    Dictionary fields = new Hashtable();

    try {

        fileItemsList = servletFileUpload.parseRequest(req);

    } catch (org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException exc) {

        out.println("ERROR: Too Big! " + exc.getMessage());
        return;

    } catch (Exception exc) {

        out.println("ERROR: " + toString());
        return;
    }

    Iterator it = fileItemsList.iterator();

    while ( it.hasNext() ) {

        FileItem fileItem = (FileItem)it.next();
        if (fileItem.isFormField()) {

            /* The file item contains a simple name-value pair of a form field */
            out.println("<br>Adding name=" + fileItem.getFieldName() + ", value=" + fileItem.getString());

            if (fileItem.getString() != null && fileItem.getFieldName() != null) {
                fields.put(fileItem.getFieldName(), (String)fileItem.getString());
            }

        } else {

            /* The file item contains an uploaded file */
            out.println("<br>filename=" + fileItem.getName() + ", fieldName=" + fileItem.getFieldName() + ", size=" + (fileItem.getSize() / 1024) + "KB");

            Connection con = null;
            PreparedStatement pstmt = null;

            try {

                con = dbConn.Connect("demov4");
                pstmt = con.prepareStatement (
                        "INSERT INTO member_photos (username, photo) VALUES ('6700', ?)");

                pstmt.clearParameters();
                pstmt.setString(1, (String)fileItem.getString());
                pstmt.executeUpdate();

            } catch (Exception e1) {

                out.println("Error: " + e1.getMessage());

            } finally {

                try { pstmt.close(); }
                catch (SQLException ignored) {}
            }

        }

    } // end while


 }


 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    


    out.println("<html>");
    out.println("<head>");
    out.println("<title>4T-ETT</title>");
    out.println("</head>");
    out.println("<body>");
    //out.println("<br>isMultipart=" + isMultipart);
    //out.println("<br>var1=" + req.getParameter("var1"));
    //out.println("<br>var2=" + req.getParameter("var2"));
    out.println("");
    out.println("");
    out.println("");

    boolean isMultipart = ServletFileUpload.isMultipartContent(req);
    out.println("<br>isMultipart=" + isMultipart);
    
    ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
    servletFileUpload.setSizeMax(1024 * 512); // 512KB
    List fileItemsList = null;
    Dictionary fields = new Hashtable();

    try {

        fileItemsList = servletFileUpload.parseRequest(req);
    
    } catch (org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException exc) {

        out.println("ERROR: Too Big! " + exc.getMessage());
        return;

    } catch (Exception exc) {

        out.println("ERROR: " + toString());
        return;
    }

    Iterator it = fileItemsList.iterator();

    while ( it.hasNext() ) {

        FileItem fileItem = (FileItem)it.next();
        if (fileItem.isFormField()) {

            /* The file item contains a simple name-value pair of a form field */
            out.println("<br>Adding name=" + fileItem.getFieldName() + ", value=" + fileItem.getString());

            if (fileItem.getString() != null && fileItem.getFieldName() != null) {
                fields.put(fileItem.getFieldName(), (String)fileItem.getString());
            } else {
                out.println(" was null...");
            }

        } else {
            
            /* The file item contains an uploaded file */
            out.println("<br>filename=" + fileItem.getName() + ", fieldName=" + fileItem.getFieldName() + ", size=" + (fileItem.getSize() / 1024) + "KB");

            try {

                //InternetHeaders headers = new InternetHeaders();
                MimeBodyPart bodyPart = new MimeBodyPart();
                DataSource ds = new ByteArrayDataSource(
                        fileItem.get(),
                        fileItem.getContentType(),fileItem.getName());
                bodyPart.setDataHandler(new DataHandler(ds));
                bodyPart.setDisposition("attachment; filename=\"" + fileItem.getName() + "\"");
                bodyPart.setFileName(fileItem.getName());

                fields.put(fileItem.getFieldName(), bodyPart);

            } catch (Exception exc) {

                out.println("ERROR2: " + toString());
            }

        }

    } // end while


    if(fields.get("var1") != null) {
        
         out.println("<br>var1=" + (String)fields.get("var1") + "");

    }


    StringBuffer vCalMsg = new StringBuffer();
    vCalMsg.append("" +
        "BEGIN:VCALENDAR\n" +
        "PRODID:-//ForeTees//NONSGML v1.0//EN\n" +
        "METHOD:PUBLISH\n" +
        "BEGIN:VEVENT\n" +
        "DTSTAMP:20100217T162000\n" +
        "DTSTART:20100301T083000\n" +
        "SUMMARY:Reservation\n" +
        "LOCATION:Demo Club\n" +
        "DESCRIPTION:Round of golf.\n" +
        "URL:http://www1.foretees.com/demov4\n" +
        "END:VEVENT\n" +
        "END:VCALENDAR");

    
    String txtBody = "Reservaton Info\n\n1: Paul Sindelar\n2: John Sindelar\n3: X\n4: TBD\n\n";

    
    String htmlBody = "<html><body>";
    htmlBody += "<h3>ForeTees</h3>";
    htmlBody += txtBody.replace("\n", "<br>");
    htmlBody += "<p><a href=\"/v5/servlet/Login?verify\">Link</a></p>";
    htmlBody += "</body></html>";
        
/*
    Dictionary fields = new Hashtable();


    if(req.getContentType() != null && req.getContentType().startsWith("multipart/form-data")) {

        out.println("<br>req.getContentType=" + req.getContentType());

        try {
            fields = getUpload(req, resp, out);
        } catch (Exception exc) {
            out.println("<br><br>ERROR: " + exc.toString());
        }

    }
*/

    try {

        MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap)MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes("text/calendar ics ICS");

        MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        
        Properties properties = new Properties();
        Session mailSess;

        properties.put("mail.smtp.host", "216.243.184.88");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", "true");

        mailSess = Session.getInstance(properties, getAuthenticator("support@foretees.com", "fikd18"));

        MimeMessage message = new MimeMessage(mailSess);

        message.setFrom(new InternetAddress("auto-send@foretees.com"));
        message.setSubject( "ETT Test Message" );
        message.setSentDate(new java.util.Date());
        message.addHeader("X-Mail-Calendar-Part:", "Yes");
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("paul@foretees.com"));



        //
        // ADD PLAIN TEXT BODY
        //
        
            // If a Content-Transfer-Encoding header field appears as part of a message header,
            // it applies to the entire body of that message. If a Content-Transfer-Encoding header
            // field appears as part of a body part's headers, it applies only to the body
            // of that body part. If an entity is of type "multipart" or "message", the
            // Content-Transfer- Encoding is not permitted to have any value other than
            // a bit width (e.g., "7bit", "8bit", etc.) or "binary".


        //msgBodyPart.addHeader("Content-Transfer-Encoding", "quoted-printable");

        BodyPart txtBodyPart = new MimeBodyPart();
        txtBodyPart.setContent(txtBody, "text/plain");
        //message.setText(txtBody);


        //
        // ADD HTML BODY
        //
        BodyPart htmlBodyPart = new MimeBodyPart();
        //htmlBodyPart.addHeader("Content-Transfer-Encoding", "quoted-printable");
        htmlBodyPart.setContent(htmlBody, "text/html");

/*

        //
        // ADD CALENDAR
        //
        BodyPart icsBodyPart = new MimeBodyPart();
        //icsBodyPart.setFileName("foretees.ics"); // OUTLOOK recommends not setting filename
        //icsBodyPart.addHeader("Content-Transfer-Encoding", "7bit");
        
            // Bodyparts can be designated `attachment' to indicate that they are
            // separate from the main body of the mail message, and that their
            // display should not be automatic, but contingent upon some further
            // action of the user.

        icsBodyPart.addHeader("Content-Disposition", "attachment"); // ;filename=foretees.ics
        icsBodyPart.addHeader("Content-Class", "urn:content-classes:calendarmessage");
        
        icsBodyPart.setContent(vCalMsg.toString(), "text/calendar;method=REQUEST"); // this way the email renders properly on desktops but ical is not attachment on iPhone
        //icsBodyPart.setContent(vCalMsg.toString(), "application/octet-stream;name=foretees.ics"); // this way the email does not render properly on desktops but ical is an attachment on iPhone
        
*/
        
        Multipart mpRoot = new MimeMultipart("mixed");
        Multipart mpContent = new MimeMultipart("alternative");

        // Create a body part to house the multipart/alternative Part
        MimeBodyPart contentPartRoot = new MimeBodyPart();
        contentPartRoot.setContent(mpContent);

        // Add the root body part to the root multipart
        mpRoot.addBodyPart(contentPartRoot);

        mpContent.addBodyPart(txtBodyPart);
        mpContent.addBodyPart(htmlBodyPart);
//        mpRoot.addBodyPart(icsBodyPart);

        if(fields.get("attachment1") == null) {
             out.println("<p>No Attachment Found.</p>");
        } else {
      
            BodyPart body = new MimeBodyPart(), attachment = (BodyPart)fields.get("attachment1");
            mpRoot.addBodyPart(attachment);
        }

        if(fields.get("attachment2") != null) {

            BodyPart body = new MimeBodyPart(), attachment = (BodyPart)fields.get("attachment2");
            mpRoot.addBodyPart(attachment);

        }

        if(fields.get("attachment3") != null) {

            BodyPart body = new MimeBodyPart(), attachment = (BodyPart)fields.get("attachment3");
            mpRoot.addBodyPart(attachment);
            
        }

        message.setContent(mpRoot);
        message.saveChanges();
        
        Transport.send(message);
        
    } catch (Exception exc) {

        out.println("<br><br>Error sending. Err = " + exc.getMessage());

    } finally {

        out.println("<p>Done...</p>");

    }

    out.println("</body>");
    out.println("</html>");
    out.close();

 }



/*
 private Dictionary getUpload(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
    throws IOException, MessagingException {


      String boundary = request.getHeader("Content-Type");
      int pos = boundary.indexOf('=');
      boundary = boundary.substring(pos + 1);
      boundary = "--" + boundary;
      ServletInputStream in =
         request.getInputStream();
      byte[] bytes = new byte[512];
      int state = 0;
      ByteArrayOutputStream buffer =
         new ByteArrayOutputStream();
      String name = null,
             value = null,
             filename = null,
             contentType = null;
      Dictionary fields = new Hashtable();

      int i = in.readLine(bytes,0,512);
      while(-1 != i)
      {
         String st = new String(bytes,0,i);
         if(st.startsWith(boundary))
         {
            state = 0;
            if(null != name)
            {
               if(value != null)
                  fields.put(name,
                     value.substring(0,
                           // -2 to remove CR/LF
                           value.length() - 2));
               else if(buffer.size() > 2)
               {
                  InternetHeaders headers =
                     new InternetHeaders();
                  MimeBodyPart bodyPart =
                     new MimeBodyPart();
                  DataSource ds =
                     new ByteArrayDataSource(
                        buffer.toByteArray(),
                        contentType,filename);
                  bodyPart.setDataHandler(
                     new DataHandler(ds));
                  bodyPart.setDisposition(
                     "attachment; filename=\"" +
                     filename + "\"");
                  bodyPart.setFileName(filename);
                  fields.put(name,bodyPart);
               }
               name = null;
               value = null;
               filename = null;
               contentType = null;
               buffer = new ByteArrayOutputStream();
            }
         }
         else if(st.startsWith(
            "Content-Disposition: form-data") &&
            state == 0)
         {
            StringTokenizer tokenizer =
               new StringTokenizer(st,";=\"");
            while(tokenizer.hasMoreTokens())
            {
               String token = tokenizer.nextToken();
               if(token.startsWith(" name"))
               {
                  name = tokenizer.nextToken();
                  state = 2;
               }
               else if(token.startsWith(" filename"))
               {
                  filename = tokenizer.nextToken();
                  StringTokenizer ftokenizer =
                     new StringTokenizer(filename,"\\/:");
                  filename = ftokenizer.nextToken();
                  while(ftokenizer.hasMoreTokens())
                     filename = ftokenizer.nextToken();
                  state = 1;
                  break;
               }
            }
         }
         else if(st.startsWith("Content-Type") &&
                 state == 1)
         {
            pos = st.indexOf(":");
            // + 2 to remove the space
            // - 2 to remove CR/LF
            contentType =
               st.substring(pos + 2,st.length() - 2);
         }
         else if(st.equals("\r\n") && state == 1)
            state = 3;
         else if(st.equals("\r\n") && state == 2)
            state = 4;
         else if(state == 4)
            value = value == null ? st : value + st;
         else if(state == 3)
            buffer.write(bytes,0,i);
         i = in.readLine(bytes,0,512);

      } // end while loop

      return fields;

  }
*/

class ByteArrayDataSource
   implements DataSource
{
   byte[] bytes;
   String contentType,
          name;

   ByteArrayDataSource(byte[] bytes,
                       String contentType,
                       String name)
   {
      this.bytes = bytes;
      if(contentType == null)
         this.contentType = "application/octet-stream";
      else
         this.contentType = contentType;
      this.name = name;
   }

   public String getContentType()
   {
      return contentType;
   }

   public InputStream getInputStream()
   {
      // remove the final CR/LF
      return new ByteArrayInputStream(
         bytes,0,bytes.length - 2);
   }

   public String getName()
   {
      return name;
   }

   public OutputStream getOutputStream()
      throws IOException
   {
      throw new FileNotFoundException();
   }
}


/*
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

     doPost(req, resp, null);
 }
*/

 private static Authenticator getAuthenticator(final String user, final String pass) {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication(user, pass); // credentials
         //return new PasswordAuthentication("support@foretees.com", "fikd18"); // credentials
       }
    };

    return auth;
 }

 private static void doCmd(HttpServletRequest req, HttpServletResponse resp) {

    PrintWriter out = null;
    //StringBuffer ta = new StringBuffer(512);
    String result = "";
    
    try {
        
        resp.setContentType("text/html");
        out = resp.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>CMDS</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("");
        out.println("");
        out.println("");
        out.println("");
        out.println("");


/*
        String cm = "ping javalessons.com -c 3";
        Process p = Runtime.getRuntime().exec(cm);
        InputStream in = p.getInputStream();
        int ch;
        StringBuffer sb = new StringBuffer(512);

        while ( ( ch = in.read() ) != -1 )
        {	sb.append((char) ch); }

        ta.append(sb.toString());
*/
        
        //
        // ADD CHECK TO MAKE SURE USER IS RUNINNG THIS ON NODE #1 !!!
        //
        
        String clubname = req.getParameter("clubname");
        
        if (clubname != null) {
        
            clubname = clubname.trim();
            
            if (clubname.indexOf(".") == -1) {

                String cm = "ln -s /usr/local/tomcat/webapps/ROOT/login.jsp /usr/local/tomcat/webapps/" + clubname + "/login.jsp";
                Process p = Runtime.getRuntime().exec(cm);
                out.println("<pre>" + cm + "</pre>");

                cm = "ln -s /usr/local/tomcat/webapps/ROOT/mlogin.jsp /usr/local/tomcat/webapps/" + clubname + "/mlogin.jsp";
                p = Runtime.getRuntime().exec(cm);
                out.println("<pre>" + cm + "</pre>");

                cm = "ls -lh /usr/local/tomcat/webapps/" + clubname;
                p = Runtime.getRuntime().exec(cm);
                out.println("<pre>" + cm + "</pre>");

                InputStream in = p.getInputStream();
                int ch;
                StringBuffer sb = new StringBuffer(512);

                while ( ( ch = in.read() ) != -1 )
                {	sb.append((char) ch); }

                //ta.append(sb.toString());

                result = sb.toString();
            
            } // no periods in clubname
        
        } // clubname not null
        
        
        
    } catch (Exception exc) {
    
        out.println("ERROR:" + exc.toString());

    }

    out.println("<pre>" + result + "</pre>");

    out.println("</body>");
    out.println("</html>");
    out.close();

 }
 
} // end servlet public class