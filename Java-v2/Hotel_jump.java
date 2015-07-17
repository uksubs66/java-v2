/***************************************************************************************
 *   Hotel_jump:  This servlet will process a request from Hotel_slot (verify) to
 *                 process the transition from the servlet w/o frames back to Hotel_sheet
 *                 which uses frames.
 *
 *
 *   called by:  Hotel_slot verify processing when res is complete or cancelled
 *
 *
 *   parms passed by Hotel_slot:
 *
 *               index = index (index value for which day's tee sheet to display
 *               course = name of course
 *
 *
 *   created: 11/19/2003   Bob P.
 *
 *   last updated:      ******* keep this accurate *******
 *
 *          01/07/2004   JAG  Modified to match new color scheme
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

public class Hotel_jump extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process a GoBack (return w/o changes) from Hotel_slot
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }


 //*****************************************************************************************************
 //  Gets control from Hotel_slot (verify) when reservation complete or cancelled
 //*****************************************************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   HttpSession sess = SystemUtils.verifyMem(req, out);       // check for intruder

   if (sess == null) {

      return;
   }

   String jumps = "0";                            // default to zero (jump index for _sheet)

   //
   //  Control came from Hotel_slot
   //
   String index = req.getParameter("index");         //  get the index value passed
   String course = req.getParameter("course");       //  get the course name passed

   if (req.getParameter("jump") != null) {

      jumps = req.getParameter("jump");         //  get the jump index
   }

   //
   //  Build the HTML page that will automatically jump to Hotel_sheet
   //
   out.println("<HTML><HEAD><Title>ForeTees Hotel Tee Times</Title>");
   out.println("</HEAD>");

   out.println("<frameset rows=\"90,*\" frameborder=\"0\" framespacing=\"5\" border=\"0\">");
   out.println("<frame name=\"top\" src=\"Hotel_maintop\" marginwidth=\"3\" marginheight=\"0\" scrolling=\"no\">");
   out.println("<frame name=\"bot\" src=\"Hotel_sheet?" + index + "=a&course=" + course + "&jump=" + jumps + "\" marginwidth=\"3\" marginheight=\"5\">");
   out.println("</frameset>");

   out.println("</html>");


 }  // end of doPost

}
