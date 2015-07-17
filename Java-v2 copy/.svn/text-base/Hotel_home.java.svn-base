/***************************************************************************************
 *   Hotel_home: This servlet will display the Home page for TPC Hotel Users.
 *
 *
 *
 *   created: 9/10/2010   Bob P.
 *
 *   last updated:            ******* keep this accurate *******
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Hotel_home extends HttpServlet {


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

   HttpSession session = SystemUtils.verifyHotel(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");   // get club name
   String user = (String)session.getAttribute("user");
   

   if (req.getParameter("clubswitch") != null && req.getParameter("clubswitch").equals("1") && req.getParameter("club") != null) {

       //
       //  Request is to switch clubs - switch the db (TPC or Demo sites)
       //
       String newClub = req.getParameter("club");

       Connection con = null;
       
       //
       //  release the old connection
       //
       ConnHolder holder = (ConnHolder) session.getAttribute("connect");

       if (holder != null) {

           con = holder.getConn();      // get the connection for previous club
       }

       if (con != null) {
/*
           // abandon any unfinished transactions
           try { con.rollback(); }
           catch (Exception ignore) {}
*/
           // close/release the connection
           try { con.close(); }
           catch (Exception ignore) {}
       }

       
       //
       //  Connect to the new club
       //
       try {
          con = dbConn.Connect(newClub);           // get connection to this club's db
       }
       catch (Exception ignore) {}

       holder = new ConnHolder(con);

       session.setAttribute("club", newClub);
       session.setAttribute("connect", holder);
       
       out.println("<HTML><HEAD><Title>Switching Sites</Title>");
       out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/hotel_home.htm\">");
       out.println("</HEAD>");
       out.println("<BODY><CENTER><BR>");
       out.println("<BR><H2>Switching Sites</H2><BR>");
       out.println("<a href=\"/" +rev+ "/hotel_home.htm\" target=_top>Continue</a><br>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
   }


       
   //
   //  Call is to display the Home page.
   //
   out.println("<html><head>");
   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
   out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
   out.println("<title> \"ForeTees Hotel Home Page\"</title>");
   out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");

   out.println("<style type=\"text/css\"> body {text-align: center} </style>");      // so body will align on center
   
   out.println("</head>");

   out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");

   out.println("<div style=\"align:center; margin:0px auto;\">");

   if (club.startsWith("tpc") && user.startsWith("passport")) {    // if TPC Passport user
   
       out.println("<br><H3>Welcome to ForeTees</H3><br>");
      
       String clubname = "";
       String fullname = "";
      
       Connection con = null;

       try {
          con = dbConn.Connect(rev);           // get connection to the Vx db
          
          //
          //  Get the club names for each TPC club
          //
          PreparedStatement pstmt = con.prepareStatement("SELECT fullname FROM clubs WHERE clubname=?");
          
          pstmt.clearParameters();
          pstmt.setString(1, club);
          ResultSet rs = pstmt.executeQuery();

          if (rs.next()) {

             fullname = rs.getString("fullname");             // get the club's full name
          }
             
          out.println("<p>You are currently connected to: <b>" + fullname + "</b><br><br>");
          out.println("To continue with this site, simply use the navigation menus above.<br><br>");
          out.println("To switch sites, click on the desired club name below.</p><br>");
      
          //
          //  Get the club names for each TPC club
          //
          pstmt = con.prepareStatement("SELECT clubname, fullname FROM clubs WHERE inactive=0 AND clubname LIKE 'tpc%' ORDER BY fullname");

          pstmt.clearParameters();
          rs = pstmt.executeQuery();

          while (rs.next()) {

             clubname = rs.getString("clubname");                // get a club name
             
             if (clubname.startsWith("tpc")) {
                
                fullname = rs.getString("fullname");             // get the club's full name
             
                out.println("<a href=\"Hotel_home?clubswitch=1&club=" +clubname+ "\" target=_top>" +fullname+ "</a><br>");
             }
          }  
          pstmt.close();
          
       }
       catch (Exception e) {
       
           // Error connecting to db....
           out.println("<BR><BR>Sorry, we encountered an error while trying to connect to the database.");
             // out.println("<br><br>Error: " + e.toString() + "<br>");
           out.println("<BR><BR> <A HREF=\"Hotel_home\">Return</A>.");
           out.println("</BODY></HTML>");
           return;       
       }      
      
   } else {

     out.println("<BR><BR> You have entered here by mistake. Please contact ForeTees Support at 651-765-6006.");
     out.println("</BODY></HTML>");
   }
   out.println("</div></BODY></HTML>");
   
 }  // end of doGet

}
