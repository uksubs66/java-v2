/***************************************************************************************     
 *   Support_port_teetimes:  This servlet will port a text file containing past tee times
 *                           from a new client into teepast.
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


// foretees imports
import com.foretees.common.FeedBack;


public class Support_port_teetimes extends HttpServlet {
                           
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                 // init DB objects
   Statement stmt = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
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
   
   String support = "support";

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( support )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = (String)session.getAttribute("club");   // get club name

   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   String line = "";
   String dates = "";
   String times = "";
   String course = "";
   String username = "";
   String fname = "";
   String lname = "";
   String gtype = "";
   String temp = "";
   String last_course = "";
   
   String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String gtype1 = "";
   String gtype2 = "";
   String gtype3 = "";
   String gtype4 = "";
   String gtype5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String tflag1 = "";
   String tflag2 = "";
   String tflag3 = "";
   String tflag4 = "";
   String tflag5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String orig_by = "";
   String guestAccomp = "";
   String notes = "";
   String mship1 = "";
   String mship2 = "";
   String mship3 = "";
   String mship4 = "";
   String mship5 = "";
   String mtype1 = "";
   String mtype2 = "";
   String mtype3 = "";
   String mtype4 = "";
   String mtype5 = "";
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String holes = "";
   
   int count = 0;
   int tokcount = 0;
   int i = 0;
   int time = 0;
   int last_time = 0;
   int hr = 0;
   int min = 0;
   int teecurr_id = 1;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int grev1 = 0;
   int grev2 = 0;
   int grev3 = 0;
   int grev4 = 0;
   int grev5 = 0;
   int hideNotes = 0;
   int fb = 0;
   short show = 1;
   long mm = 0;
   long dd = 0;
   long yy = 0;
   int date = 0;
   long last_date = 0;


   //
   //  read in the text file - must be named 'teetimes.csv'
   //
   boolean failed = false;
   boolean playerFound = false;
   FileReader fr = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//teetimes.csv");

   }
   catch (Exception e1) {

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>File Read Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e1.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   
   try {
      
      //  Now read in the file

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

       player1 = "";
       player2 = "";
       player3 = "";
       player4 = "";
       player5 = "";
       user1 = "";
       user2 = "";
       user3 = "";
       user4 = "";
       user5 = "";
       gtype1 = "";
       gtype2 = "";
       gtype3 = "";
       gtype4 = "";
       gtype5 = "";
       p1cw = "";
       p2cw = "";
       p3cw = "";
       p4cw = "";
       p5cw = "";
       tflag1 = "";
       tflag2 = "";
       tflag3 = "";
       tflag4 = "";
       tflag5 = "";
       userg1 = "";
       userg2 = "";
       userg3 = "";
       userg4 = "";
       userg5 = "";
       guestAccomp = "";
       orig_by = "";
       notes = "";
       mship1 = "";
       mship2 = "";
       mship3 = "";
       mship4 = "";
       mship5 = "";
       mtype1 = "";
       mtype2 = "";
       mtype3 = "";
       mtype4 = "";
       mtype5 = "";
       mNum1 = "";
       mNum2 = "";
       mNum3 = "";
       mNum4 = "";
       mNum5 = "";
       holes = "18";

       p91 = 0;
       p92 = 0;
       p93 = 0;
       p94 = 0;
       p95 = 0;
       grev1 = 0;
       grev2 = 0;
       grev3 = 0;
       grev4 = 0;
       grev5 = 0;
       hideNotes = 0;
       fb = 0;

       teecurr_id++;

         //count++;                                            // keep track of line #

         //  parse the line to gather all the info

         StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

         tokcount = tok.countTokens();

         // make sure we have the essentials
         
         if (tokcount >= 11) {
    
            date = Integer.parseInt(tok.nextToken());
            times = tok.nextToken();
            //course = tok.nextToken();
            holes = tok.nextToken();
            //player1 = tok.nextToken();
            user1 = tok.nextToken();
            p1cw = tok.nextToken();
            //player2 = tok.nextToken();
            user2 = tok.nextToken();
            p2cw = tok.nextToken();
            //player3 = tok.nextToken();
            user3 = tok.nextToken();
            p3cw = tok.nextToken();
            //player4 = tok.nextToken();
            user4 = tok.nextToken();
            p4cw = tok.nextToken();
            //player5 = tok.nextToken();
            user5 = tok.nextToken();
            p5cw = tok.nextToken();

            if (!notes.equals("")) {
                hideNotes = 1;
            } else {
                hideNotes = 0;
            }
            

            tok = new StringTokenizer( times, ":" );        // time = "00:00"
            
            if ( tok.countTokens() > 1 ) {
               temp = tok.nextToken();              //  hr
               hr = Integer.parseInt(temp);
               temp = tok.nextToken();              //  min
               min = Integer.parseInt(temp);
               time = (hr * 100) + min;             // hhmm
            } else {
               time = 9999;
            }

            if (player1.equals("?")) player1 = "";
            if (user1.equals("?")) user1 = "";
            if (p1cw.equals("?")) p1cw = "";
            if (player2.equals("?")) player2 = "";
            if (user2.equals("?")) user2 = "";
            if (p2cw.equals("?")) p2cw = "";
            if (player3.equals("?")) player3 = "";
            if (user3.equals("?")) user3 = "";
            if (p3cw.equals("?")) p3cw = "";
            if (player4.equals("?")) player4 = "";
            if (user4.equals("?")) user4 = "";
            if (p4cw.equals("?")) p4cw = "";
            if (player5.equals("?")) player5 = "";
            if (user5.equals("?")) user5 = "";
            if (p5cw.equals("?")) p5cw = "";
            if (notes.equals("?")) notes = "";

            if (!user1.equals("")) {

                playerFound = false;

                String [] tempStr = user1.split(" ");
                
                user1 = "";
                
                for (i = 0; i < tempStr.length; i++) {
                    
                    if (tempStr[i].equalsIgnoreCase("GUEST")) {
                        
                        gtype1 = "Guest";
                        player1 = "Guest";
                        
                    } else if (tempStr[i].startsWith("(")) {
                        user1 = tempStr[i].substring(1, tempStr[i].length() - 1);
                        guestAccomp = user1;
                        orig_by = user1;
                    } else if (!player1.equals("Guest")) {
                        player1 += tempStr[i] + " ";
                    }
                }
                
                player1 = player1.trim();

                if (!gtype1.equals("")) {
                    user1 = "";
                    userg1 = guestAccomp;
                }
                
                if (holes.equals("9")) {
                    p91 = 1;
                } else {
                    p91 = 0;
                }

                if (!user1.equals("")) {
                    try {

                        pstmt = con.prepareStatement("SELECT tflag, CONCAT(name_first, ' ', IF(name_mi <> '', CONCAT(name_mi, ' '), ''), name_last) AS display_name, memNum FROM member2b WHERE username = ?");
                        pstmt.clearParameters();
                        pstmt.setString(1, user1);

                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            player1 = rs.getString("display_name");
                            tflag1 = rs.getString("tflag");
                            //mship1 = rs.getString("m_ship");
                            //mtype1 = rs.getString("m_type");
                            mNum1 = rs.getString("memNum");
                            playerFound = true;
                        } else {
                            user1 = "";
                        }

                    } catch (Exception exc) {
                        out.println("<br>Error1: " + player1 + " - " + exc.toString());
                    } finally {
                        try { rs.close(); }
                        catch (Exception ignore) { }

                        try { pstmt.close(); }
                        catch (Exception ignore) { }
                    }
                }
            }

            if (!user2.equals("")) {

                String [] tempStr = user2.split(" ");
                
                user2 = "";
                
                for (i = 0; i < tempStr.length; i++) {
                    
                    if (tempStr[i].equalsIgnoreCase("GUEST")) {
                        
                        gtype2 = "Guest";
                        player2 = "Guest";
                        
                    } else if (tempStr[i].startsWith("(")) {
                        user2 = tempStr[i].substring(1, tempStr[i].length() - 1);
                        guestAccomp = user2;
                    } else if (!player2.equals("Guest")) {
                        player2 += tempStr[i] + " ";
                    }
                }
                
                player2 = player2.trim();
                
                if (orig_by.equals("")) {
                    orig_by = user2;
                }

                if (!gtype2.equals("")) {
                 
                    user2 = "";
                    userg2 = guestAccomp;
                }
                
                if (holes.equals("9")) {
                    p92 = 1;
                } else {
                    p92 = 0;
                }

                playerFound = false;

                if (!user2.equals("")) {
                    try {

                        pstmt = con.prepareStatement("SELECT tflag, CONCAT(name_first, ' ', IF(name_mi <> '', CONCAT(name_mi, ' '), ''), name_last) AS display_name, memNum FROM member2b WHERE username = ?");
                        pstmt.clearParameters();
                        pstmt.setString(1, user2);

                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            player2 = rs.getString("display_name");
                            tflag2 = rs.getString("tflag");
                            //mship2 = rs.getString("m_ship");
                            //mtype2 = rs.getString("m_type");
                            mNum2 = rs.getString("memNum");
                            playerFound = true;
                        } else {
                            user2 = "";
                        }

                    } catch (Exception exc) {
                        out.println("<br>Error2: " + player2 + " - " + exc.toString());
                    } finally {
                        try { rs.close(); }
                        catch (Exception ignore) { }

                        try { pstmt.close(); }
                        catch (Exception ignore) { }
                    }
                }
            }

            if (!user3.equals("")) {

                String [] tempStr = user3.split(" ");
                
                user3 = "";
                
                for (i = 0; i < tempStr.length; i++) {
                    
                    if (tempStr[i].equalsIgnoreCase("GUEST")) {
                        
                        gtype3 = "Guest";
                        player3 = "Guest";
                        
                    } else if (tempStr[i].startsWith("(")) {
                        user3 = tempStr[i].substring(1, tempStr[i].length() - 1);
                        guestAccomp = user3;
                        orig_by = user3;
                    } else if (!player3.equals("Guest")) {
                        player3 += tempStr[i] + " ";
                    }
                }
                
                player3 = player3.trim();
                
                if (orig_by.equals("")) {
                    orig_by = user3;
                }

                if (!gtype3.equals("")) {
                    user3 = "";
                    userg3 = guestAccomp;
                }
                
                if (holes.equals("9")) {
                    p93 = 1;
                } else {
                    p93 = 0;
                }

                playerFound = false;

                if (!user3.equals("")) {
                    
                    try {

                        pstmt = con.prepareStatement("SELECT tflag, CONCAT(name_first, ' ', IF(name_mi <> '', CONCAT(name_mi, ' '), ''), name_last) AS display_name, memNum FROM member2b WHERE username = ?");
                        pstmt.clearParameters();
                        pstmt.setString(1, user3);

                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            player3 = rs.getString("display_name");
                            tflag3 = rs.getString("tflag");
                            //mship3 = rs.getString("m_ship");
                            //mtype3 = rs.getString("m_type");
                            mNum3 = rs.getString("memNum");
                            playerFound = true;
                        } else {
                            user3 = "";
                        }

                    } catch (Exception exc) {
                        out.println("<br>Error3: " + player1 + " - " + exc.toString());
                    } finally {
                        try { rs.close(); }
                        catch (Exception ignore) { }

                        try { pstmt.close(); }
                        catch (Exception ignore) { }
                    }
                }
            }

            if (!user4.equals("")) {

                String [] tempStr = user4.split(" ");
                
                user4 = "";
                
                for (i = 0; i < tempStr.length; i++) {
                    
                    if (tempStr[i].equalsIgnoreCase("GUEST")) {
                        
                        gtype4 = "Guest";
                        player4 = "Guest";
                        
                    } else if (tempStr[i].startsWith("(")) {
                        user4 = tempStr[i].substring(1, tempStr[i].length() - 1);
                        guestAccomp = user4;
                        orig_by = user4;
                    } else if (!player4.equals("Guest")) {
                        player4 += tempStr[i] + " ";
                    }
                }
                
                player4 = player4.trim();
                
                if (holes.equals("9")) {
                    p94 = 1;
                } else {
                    p94 = 0;
                }

                playerFound = false;
                
                if (orig_by.equals("")) {
                    orig_by = user4;
                }

                if (!gtype4.equals("")) {
                    user4 = "";
                    userg4 = guestAccomp;
                }

                if (!user4.equals("")) {
                    
                    try {

                        pstmt = con.prepareStatement("SELECT tflag, CONCAT(name_first, ' ', IF(name_mi <> '', CONCAT(name_mi, ' '), ''), name_last) AS display_name, memNum FROM member2b WHERE username = ?");
                        pstmt.clearParameters();
                        pstmt.setString(1, user4);

                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            player4 = rs.getString("display_name");
                            tflag4 = rs.getString("tflag");
                            //mship4 = rs.getString("m_ship");
                            //mtype4 = rs.getString("m_type");
                            mNum4 = rs.getString("memNum");
                            playerFound = true;
                        } else {
                            user4 = "";
                        }

                    } catch (Exception exc) {
                        out.println("<br>Error4: " + player1 + " - " + exc.toString());
                    } finally {
                        try { rs.close(); }
                        catch (Exception ignore) { }

                        try { pstmt.close(); }
                        catch (Exception ignore) { }
                    }
                }
            }

            if (!user5.equals("")) {

                String [] tempStr = user5.split(" ");
                
                user5 = "";
                
                for (i = 0; i < tempStr.length; i++) {
                    
                    if (tempStr[i].equalsIgnoreCase("GUEST")) {
                        
                        gtype5 = "Guest";
                        player5 = "Guest";
                        
                    } else if (tempStr[i].startsWith("(")) {
                        user5 = tempStr[i].substring(1, tempStr[i].length() - 1);
                        guestAccomp = user5;
                        orig_by = user5;
                    } else if (!player5.equals("Guest")) {
                        player5 += tempStr[i] + " ";
                    }
                }
                
                player5 = player5.trim();

                if (!gtype5.equals("")) {
                    user5 = "";
                    userg5 = guestAccomp;
                }
                
                if (holes.equals("9")) {
                    p95 = 1;
                } else {
                    p95 = 0;
                }

                playerFound = false;

                if (orig_by.equals("")) {
                    orig_by = user5;
                }

                if (!user5.equals("")) {
                    
                    try {

                        pstmt = con.prepareStatement("SELECT tflag, CONCAT(name_first, ' ', IF(name_mi <> '', CONCAT(name_mi, ' '), ''), name_last) AS display_name, memNum FROM member2b WHERE username = ?");
                        pstmt.clearParameters();
                        pstmt.setString(1, user5);

                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            player5 = rs.getString("display_name");
                            tflag5 = rs.getString("tflag");
                            //mship5 = rs.getString("m_ship");
                            //mtype5 = rs.getString("m_type");
                            mNum5 = rs.getString("memNum");
                            playerFound = true;
                        } else {
                            user5 = "";
                        }

                    } catch (Exception exc) {
                        out.println("<br>Error5: " + player1 + " - " + exc.toString());
                    } finally {
                        try { rs.close(); }
                        catch (Exception ignore) { }

                        try { pstmt.close(); }
                        catch (Exception ignore) { }
                    }
                }
            }
            pstmt = con.prepareStatement (
                    "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, "
                    + "username1 = ?, username2 = ?, username3 = ?, username4 = ?, username5 = ?, "
                    + "p1cw = ?, p2cw = ?, p3cw = ?, p4cw = ?, p5cw = ?, "
                    + "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, "
                    + "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, "
                    + "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, "
                    + "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, "
                    + "orig_by = ?, proNew = 1 "
                    + "WHERE date = ? AND time = ? AND fb = ?");
            
            pstmt.clearParameters();
            pstmt.setString(1, player1);
            pstmt.setString(2, player2);
            pstmt.setString(3, player3);
            pstmt.setString(4, player4);
            pstmt.setString(5, player5);
            pstmt.setString(6, user1);
            pstmt.setString(7, user2);
            pstmt.setString(8, user3);
            pstmt.setString(9, user4);
            pstmt.setString(10, user5);
            pstmt.setString(11, p1cw);
            pstmt.setString(12, p2cw);
            pstmt.setString(13, p3cw);
            pstmt.setString(14, p4cw);
            pstmt.setString(15, p5cw);
            pstmt.setInt(16, p91);
            pstmt.setInt(17, p92);
            pstmt.setInt(18, p93);
            pstmt.setInt(19, p94);
            pstmt.setInt(20, p95);
            pstmt.setString(21, mNum1);
            pstmt.setString(22, mNum2);
            pstmt.setString(23, mNum3);
            pstmt.setString(24, mNum4);
            pstmt.setString(25, mNum5);
            pstmt.setString(26, tflag1);
            pstmt.setString(27, tflag2);
            pstmt.setString(28, tflag3);
            pstmt.setString(29, tflag4);
            pstmt.setString(30, tflag5);
            pstmt.setString(31, userg1);
            pstmt.setString(32, userg2);
            pstmt.setString(33, userg3);
            pstmt.setString(34, userg4);
            pstmt.setString(35, userg5);
            pstmt.setString(36, orig_by);
            
            pstmt.setLong(37, date);
            pstmt.setInt(38, time);
            pstmt.setInt(39, fb);
            
                    
                  /*
            pstmt = con.prepareStatement (
             "INSERT INTO teepast2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
             "restriction, rest_color, player1, player2, player3, player4, username1, " +
             "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, fb, " +
             "player5, username5, p5cw, show5, courseName, proNew, proMod, memNew, memMod, " +
             "mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, userg3, userg4, userg5, hotelNew, " +
             "hotelMod, orig_by, conf, notes, p91, p92, p93, p94, p95, teecurr_id, pace_status_id, " +
             "custom_string, custom_int, pos1, pos2, pos3, pos4, pos5," +
             "mship1, mship2, mship3, mship4, mship5, mtype1, mtype2, mtype3, mtype4, mtype5, " +
             "gtype1, gtype2, gtype3, gtype4, gtype5, " +
             "grev1, grev2, grev3, grev4, grev5, guest_id1, guest_id2, guest_id3, guest_id4, guest_id5, " +
             "tflag1, tflag2, tflag3, tflag4, tflag5) " +
             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
             "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
             "?, ?, ?, ?, ?, ?, ?, " +
             "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
             "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                   * 

            pstmt.clearParameters();        // clear the parms
          pstmt.setLong(1, date);         // put the parms in pstmt for tee slot
          pstmt.setLong(2, mm);
          pstmt.setLong(3, dd);
          pstmt.setLong(4, yy);
          pstmt.setString(5, "");
          pstmt.setInt(6, hr);
          pstmt.setInt(7, min);
          pstmt.setInt(8, time);
          pstmt.setString(9, "");
          pstmt.setString(10, "");
          pstmt.setString(11, "");
          pstmt.setString(12, "");
          pstmt.setString(13, player1);
          pstmt.setString(14, player2);
          pstmt.setString(15, player3);
          pstmt.setString(16, player4);
          pstmt.setString(17, user1);
          pstmt.setString(18, user2);
          pstmt.setString(19, user3);
          pstmt.setString(20, user4);
          pstmt.setString(21, p1cw);
          pstmt.setString(22, p2cw);
          pstmt.setString(23, p3cw);
          pstmt.setString(24, p4cw);
          pstmt.setShort(25, show);
          pstmt.setShort(26, show);
          pstmt.setShort(27, show);
          pstmt.setShort(28, show);
          pstmt.setShort(29, show);
          pstmt.setString(30, player5);
          pstmt.setString(31, user5);
          pstmt.setString(32, p5cw);
          pstmt.setShort(33, show);
          pstmt.setString(34, course);
          pstmt.setInt(35, 0);
          pstmt.setInt(36, 0);
          pstmt.setInt(37, 0);
          pstmt.setInt(38, 0);
          pstmt.setString(39, user1);
          pstmt.setString(40, user2);
          pstmt.setString(41, user3);
          pstmt.setString(42, user4);
          pstmt.setString(43, user5);
          pstmt.setString(44, userg1);
          pstmt.setString(45, userg2);
          pstmt.setString(46, userg3);
          pstmt.setString(47, userg4);
          pstmt.setString(48, userg5);
          pstmt.setInt(49, 0);
          pstmt.setInt(50, 0);
          pstmt.setString(51, orig_by);
          pstmt.setString(52, "");
          pstmt.setString(53, "");
          pstmt.setInt(54, p91);
          pstmt.setInt(55, p92);
          pstmt.setInt(56, p93);
          pstmt.setInt(57, p94);
          pstmt.setInt(58, p95);
          pstmt.setInt(59, teecurr_id);
          pstmt.setInt(60, 0);
          pstmt.setString(61, "");
          pstmt.setInt(62, 0);
          pstmt.setInt(63, 0);
          pstmt.setInt(64, 0);
          pstmt.setInt(65, 0);
          pstmt.setInt(66, 0);
          pstmt.setInt(67, 0);
          pstmt.setString(68, mship1);
          pstmt.setString(69, mship2);
          pstmt.setString(70, mship3);
          pstmt.setString(71, mship4);
          pstmt.setString(72, mship5);
          pstmt.setString(73, mtype1);
          pstmt.setString(74, mtype2);
          pstmt.setString(75, mtype3);
          pstmt.setString(76, mtype4);
          pstmt.setString(77, mtype5);
          pstmt.setString(78, gtype1);
          pstmt.setString(79, gtype2);
          pstmt.setString(80, gtype3);
          pstmt.setString(81, gtype4);
          pstmt.setString(82, gtype5);
          pstmt.setInt(83, grev1);
          pstmt.setInt(84, grev2);
          pstmt.setInt(85, grev3);
          pstmt.setInt(86, grev4);
          pstmt.setInt(87, grev5);
          pstmt.setInt(88, 0);
          pstmt.setInt(89, 0);
          pstmt.setInt(90, 0);
          pstmt.setInt(91, 0);
          pstmt.setInt(92, 0);
          pstmt.setString(93, tflag1);
          pstmt.setString(94, tflag2);
          pstmt.setString(95, tflag3);
          pstmt.setString(96, tflag4);
          pstmt.setString(97, tflag5);
                   */

            int tempInt = 0;
            
            tempInt = pstmt.executeUpdate();        // move the tee slot to teepast
            
            if (tempInt > 0) {
                count++;
            }

            pstmt.close();
         }
         
      }   // end of while

   }
   catch (Exception e3) {

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>DB Add or Update Failed for  " + club);
      out.println("<BR><BR>Exception Received on Line Number " + count + ", Error = "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Import Complete</H3>");
   out.println("<BR><BR>" +count+ " Tee Times Added for  " + club);
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
      
 }   
                                 

 //************************************************************************
 //  logError - logs error messages to a text file in the club's folder
 //************************************************************************

 private void logError(String msg, String club) {

   String space = "  ";
   int fail = 0;

   try {
      //
      //  Dir path for the real server
      //
      PrintWriter fout1 = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//" +club+ "//error.txt", true));

      //
      //  Put header line in text file
      //
      fout1.print(new java.util.Date() + space + msg);
      fout1.println();      // output the line

      fout1.close();

   }
   catch (Exception e2) {

      fail = 1;
   }

   //
   //  if above failed, try local pc
   //
   if (fail != 0) {

      try {
         //
         //  dir path for test pc
         //
         PrintWriter fout = new PrintWriter(new FileWriter("c:\\java\\tomcat\\webapps\\" +club+ "\\error.txt", true));

         //
         //  Put header line in text file
         //
         fout.print(new java.util.Date() + space + msg);
         fout.println();      // output the line

         fout.close();
      }
      catch (Exception ignore) {
      }
   }
 }  // end of logError


 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");

 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR><BR>" + e.getMessage());
   out.println("<BR><BR><a href=\"/" +rev+ "/support_main.htm\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }
 
}
