/***************************************************************************************
 *  SessionSaver:  This servlet simply access the users session block 
 *                 so their session does not expire
 *
 *
 *  Called by:     session_saver.htm (popup window contents)
 *
 *
 *  Created:       05/07/2008 - Paul S.
 *
 *
 *  Revisions: 
 *
 *
 *
 ***************************************************************************************
 */


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class SessionSaver extends HttpServlet {


   String rev = SystemUtils.REVLEVEL;           
   
 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
     
    resp.setHeader("Pragma","no-cache");
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");
    resp.setDateHeader("Expires",0);
    resp.setContentType("text/html");
    
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyMem(req, out);
    
    out.println("<script type='text/javascript'>");
    out.println(" self.close();");
    out.println("</script>");
    
 }
 
}