/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;

/**
 *
 * @author sindep
 */
public class Common_lesson extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


        if (req.getParameter("bioview") != null && req.getParameter("club") != null && req.getParameter("proid") != null) {

            outputLessonBioPage(req, resp);
            return;
        }

    } 


    private void outputLessonBioPage(HttpServletRequest req, HttpServletResponse resp) {


        PrintWriter out = null;

        String filename = "";
        String club = req.getParameter("club");
        
        boolean modalMode = Utilities.getParameterString(req, "modal", null) != null;
        
        int bio_id = 0;
        try {
            if (req.getParameter("proid") != null) bio_id = Integer.parseInt(req.getParameter("proid"));
        } catch (Exception ignore) {}

        // sanity - kill any requests with a club name that has a dot or forward slash in it - what else??
        if (bio_id == 0 || club.indexOf(".") > 0 || club.indexOf("/") > 0) return;

        try {

            resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
            resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
            resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
            resp.setContentType("text/html;charset=UTF-8");
            
            out = resp.getWriter();

            if(!modalMode){
                out.println("<!DOCTYPE html>");
                out.println("<html lang=\"en-US\">");
                out.println("<head>");
                out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />");
                out.println("<link rel=\"stylesheet\" href=\"../assets/stylesheets/sitewide.css\" type=\"text/css\" />");
                out.println("<style type=\"text/css\"> body {background-color:#FFF;padding:5px;margin:5px} </style>");      // override some of the sitewide settings
                out.println("</head>");
                out.println("<body>");
            }

            //
            //   File objects to get the current announcement page
            //
            File f;
            FileReader fr;
            BufferedReader br;
            String tmp = "";
            String path = "";

            if (req.getParameter("backup") != null && req.getParameter("file") != null) {

                // use the backup file's filename being passed in
                filename = req.getParameter("file");
                
                if (!filename.startsWith(club) || !filename.endsWith((".bak")) || filename.indexOf("/") > 0) {

                    // bad filename - should never happen unless someone is messing around
                    if(!modalMode){
                        out.println("</body></html>");
                    }
                    return;
                }

            } else {

                // get the current filename for the live bio page
                filename = Utilities.getLessonBioPageFileName(bio_id, club, "", false);

            }

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
               if(!modalMode){
                out.println("<br><br>");
               }
               out.print("<p align=center>Lesson Pro Bio Page Not Found.</p>");
               if(!modalMode){
                out.println("</div></BODY></HTML>");
               }
               return;
            }
            catch (SecurityException se) {
               if(!modalMode){
                out.println("<br><br>");
               }
               out.print("<p align=center>Access Denied.</p>");
               if(!modalMode){
                out.println("</div></BODY></HTML>");
               }
               return;
            }

            if(!modalMode){
                out.println("<div class=\"announcement_container\">");
                out.println("<!-- BEGIN INSERT -->");
            }
            while( (tmp = br.readLine()) != null ){
               out.println(tmp);
            }

            if(!modalMode){
                out.println("<!-- END INSERT -->");
                out.println("<div class=\"clearfloat\"></div>"); // force floats to clear in case the bio page doesn't
                out.println("</div>");
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
            if(!modalMode){
                out.println("</body></html>");
            }
        } catch (Exception exc) {

            Utilities.logError("Common_lesson.outputLessonBioPage() club=" + club + ", proid=" + bio_id + ", err=" + exc.getMessage());

        } finally {

            out.close();
            
        }

    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
