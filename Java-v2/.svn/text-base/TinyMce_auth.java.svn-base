/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.foretees.common.VerifyUser;
import com.foretees.common.reqUtil;

import java.util.*;

/**
 *
 * @author Owner
 */
public class TinyMce_auth extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String key, secretKey, data = "";

            // Change this secret key so it matches the one in the imagemanager/filemanager config
            secretKey = "f0reteesXViii";

            // Check here if the user is logged in or not
	/*
            if (session.getAttribute("club") != "somevalue") {
            out.print("You are not logged in.");
            return;
            }
             */

            VerifyUser verify_user = VerifyUser.verifyPro(request);
            String club = reqUtil.getSessionString(request, "club", null);
            //String club = (String)session.getAttribute("club");
            if (verify_user.session == null || club == null) {
                out.print("You are not logged in.");
                return;
            }

            // Override any config values here
            Hashtable configuration = new Hashtable();
            //configuration.put("filesystem.path", "c:/Inetpub/wwwroot/somepath");
            configuration.put("filesystem.rootpath", "../../../../../AEimages/" + club);

            // Generates a unique key of the config values with the secret key
            for (Enumeration e = configuration.keys(); e.hasMoreElements();) {
                data += configuration.get(e.nextElement());
            }

            key = md5(data + secretKey);

            out.print("<html>");
            out.print("<body onload=\"document.forms[0].submit();\">");
            out.print("<form method=\"post\" action=\"" + htmlEncode(request.getParameter("return_url")) + "\">");
            out.print("<input type=\"hidden\" name=\"key\" value=\"" + htmlEncode(key) + "\" />");

            for (Enumeration e = configuration.keys(); e.hasMoreElements();) {
                key = (String) e.nextElement();

                out.print("	<input type=\"hidden\" name=\"" + htmlEncode(key.replaceAll("\\.", "__")) + "\" value=\"" + htmlEncode((String) configuration.get(key)) + "\" />");

            }

            out.print("</form>");
            out.print("</body>");
            out.print("</html>");

        } finally {
            out.close();
        }
    }

    private static String md5(String str) {
        try {
            java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");

            char[] charArray = str.toCharArray();
            byte[] byteArray = new byte[charArray.length];

            for (int i = 0; i < charArray.length; i++) {
                byteArray[i] = (byte) charArray[i];
            }

            byte[] md5Bytes = md5.digest(byteArray);
            StringBuilder hexValue = new StringBuilder();

            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;

                if (val < 16) {
                    hexValue.append("0");
                }

                hexValue.append(Integer.toHexString(val));
            }

            return hexValue.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            // Ignore
        }

        return "";
    }

    private static String htmlEncode(String str) {
        StringBuilder buff = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char chr = str.charAt(i);

            switch (chr) {
                case '<':
                    buff.append("&lt;");
                    break;

                case '>':
                    buff.append("&gt;");
                    break;

                case '"':
                    buff.append("&quot;");
                    break;

                case '&':
                    buff.append("&amp;");
                    break;

                default:
                    buff.append(chr);
            }
        }

        return buff.toString();
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
