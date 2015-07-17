/***************************************************************************************
 *   Common_mobile:  This servlet will process common functions for Mobile users.
 *
 *
 *   created: 7/29/2009   Bob P.
 *
 *   last updated:       ******* keep this accurate *******
 *
 *    5/04/14   Oak Hill CC (oakhillcc) - add custom_int so it gets passed to Member_slot.verify for processing (case 2361).
 *    8/22/13   Add custom to block access to existing notes if user not the originator (case 2293).
 *    2/04/13   Allow members to add guest names when guest tracking is enabled.
 *    3/09/12   Add a couple of blank lines at bottom of page to allow for the IOS Nav Bar on iPhones.
 *    2/24/12   Fixed issue with player 5 name/MoT not being included when a 5 some restriction was present, meaning any updates to the tee time would inadvertently remove them.
 *    3/29/11   Allow the use of X's in lottery requests if allowed in lottery config.
 *    2/09/11   Pro-only MoTs will now be dynamically added to the MoT selection drop-down menu if a partner is selected that has a pro-only default MoT.
 *    2/08/11   Adjusted how modes of transportation are loaded so that players with a pro-only MoT (default or booked by proshop) will have that MoT option available.
 *   10/08/10   Fixed an error that was occurring due to a guest array accessing out of bounds entries when no pro-only guest types were present
 *    9/23/10   Changes to accomodate guest tracking with mobile lottery
 *    9/08/10   Updated alphaTable.guestdbList() call to include new passed value
 *    4/18/10   Added guest tracking processing
 *    4/09/10   Only including the pipe seperator being name & tmode is there is a tmode to append also if
 *              there is no default tmode then clear the select box so user must select one.
 *    2/26/10   Change to how partner names are formatted.  Was causing problems on certain mobile devices
 *   12/04/09   Updated Parter optgroup processing to grab partners from the new partner table/setup
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.ProcessConstants;
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.alphaTable;
import com.foretees.common.Utilities;


public class Common_mobile {


 static String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


   static long Hdate1 = ProcessConstants.memDay;            // Memorial Day
   static long Hdate2 = ProcessConstants.july4;             // 4th of July - Monday
   static long Hdate2b = ProcessConstants.july4b;           // 4th of July - ACTUAL 7/04
   static long Hdate3 = ProcessConstants.laborDay;          // Labor Day
   static long Hdate7 = ProcessConstants.tgDay;             // Thanksgiving Day
   static long Hdate8 = ProcessConstants.colDay;            // Columbus Day
   static long Hdate9 = ProcessConstants.colDayObsrvd;      // Columbus Day Observed

   

 // *********************************************************
 //
 //  Generate the html page to solicit player names, etc for
 //  tee times and lottery requests.
 //
 //
 //     Called by:  Member_lott 
 //                 Member_slot
 //                 Member_slotm (future maybe)
 //
 //
 // *********************************************************

 public static void doSlot(parmClub parm, parmCourse parmc, parmMobile parmM, PrintWriter out, Connection con) {

    
   PreparedStatement pstmt = null;
   ResultSet rs = null;
    
    
   String[] nonProTmodes = new String[parmc.tmode_limit];
   String[] nonProGuests = new String[parm.MAX_Guests];

   String guest = "";
    
   int nonProCount = 0;
   int gstCount = 0;
   int i = 0;
   int i2 = 0;
   int max = parmM.players;         // max number of players allowed
   int cols = 3;
   int x = parm.x;                  // X indicator
   
   int activity_id = 0;             // only golf at this time

   ArrayList<String> partner_names = new ArrayList<String>();
   ArrayList<String> partner_wcs = new ArrayList<String>();
   
   
   //
   //  Adjust max number of players if necessary
   //
   if (!parmM.type.equals("lottery")) {           // if Tee Time request     
      
      if (parmM.twoSomeOnly == true) {            // if 2-some only time
         
         max = 2;
         
      } else if (parmM.threeSomeOnly == true) {   // if 3-some only time
         
         max = 3;
      }
   }
       
   
   // gather list of Non-Pro-Only tmodes locally
   for (i=0; i<parmc.tmode_limit; i++) {
       if (parmc.tOpt[i] == 0 && !parmc.tmodea[i].equals("")) {
           nonProTmodes[nonProCount] = parmc.tmodea[i];
           nonProCount++;
       }
   }
   
   
   // gather list of Non-Pro-Only Guests locally
   for (i=0; i<parm.MAX_Guests; i++) {
       if (parm.gOpt[i] == 0 && !parm.guest[i].equals("")) {
           nonProGuests[gstCount] = parm.guest[i];
           gstCount++;
       }
   }
   
   
   //
   //  Get the current partner list for this user
   //
   try {

       pstmt = con.prepareStatement (
               "SELECT CONCAT(m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi, ' '), ' '), m.name_last) as partner_name, " +
               "m.wc from partner p " +
               "LEFT OUTER JOIN member2b m ON p.partner_id = m.username " +
               "WHERE p.user_id = ? AND p.activity_id = ? " +
               "ORDER BY p.priority, m.name_last, m.name_first");

       pstmt.clearParameters();
       pstmt.setString(1, parmM.user);
       pstmt.setInt(2, 0);                  // Will need to change if we ever allow for more than golf on the mobile version!!
       rs = pstmt.executeQuery();

       while (rs.next()) {
           partner_names.add(rs.getString("partner_name"));
           partner_wcs.add(rs.getString("m.wc"));
       }
   }
   catch (Exception exc) {
   }
   
   //
   //  Dertermine if we can allow members to use X
   //
   if (parmM.type.equals("lottery")) {     // if lottery request      
      
      x = parmM.allowx;           // X's allowed if allowed in Lottery config
      
   } else {
            
      if (parmM.club.equals("greenwich")) {
          if ((parmM.day.equals( "Saturday" ) || parmM.day.equals( "Sunday" ) || 
              parmM.date == Hdate1 || parmM.date == Hdate2b || parmM.date == Hdate3 || parmM.date == Hdate9) &&
              ( parmM.time > 1131 && parmM.time < 1229 ) ) {

              x = 0;
          }
      }        
      if (parmM.club.equals( "foresthighlands" )) {       // Forest Highlands - no X
         x = 0;
      }
   }
   
   if (parmM.notes == null) {
      
      parmM.notes = "";
   }
   
   //
   //  Parse any players already selected in case they contain a "|" followed by the mode of trans (left over from scripts below when user clicks on an alpha letter).
   //
   for (i=0; i < max; i++) {   // check each player
      
      if (!parmM.playerA[i].equals("") && !parmM.playerA[i].equalsIgnoreCase("x")) {    // if player specified - parse it
         
         StringTokenizer tok = new StringTokenizer( parmM.playerA[i], "|" );        // check for horizontal bar

         if ( tok.countTokens() > 1 ) {             // if found
            parmM.playerA[i] = tok.nextToken();     // just get the player's name (strip bar and mode of trans)
         }
      }   
   }
   
   
   //  Get any guest types that are used for Guest Tracking
   
   ArrayList<String> gstTrackingGsts = new ArrayList<String>();
     
   gstTrackingGsts = Utilities.getGuestTrackingGsts(activity_id, false, con);
   
   

   //
   //  Build the page that solicits the player info for the tee time request
   //
   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
   out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\" />");
   out.println("<Title>Member Tee Time Request Page</Title>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/mobile/style.css\" type=\"text/css\" media=\"screen\">");

   
   //  ********* put scripts here ******************
   //
   //*******************************************************************
   //  Erase text area
   //  Place existing notes in notes box
   //*******************************************************************
   //
   //   erasetext script
   out.println("<script type=\"text/javascript\">");            // Erase text area script
   out.println("<!--");


   // Create global array of available tmodes for this course
   int tmodeCount = 0;
   out.println("var tmodes = Array()");
   for (int j=0; j<parmc.tmode_limit; j++) {
       if (!parmc.tmodea[j].equals("")) {
           tmodeCount++;
           out.println("tmodes[" + j + "] = \"" + parmc.tmodea[j] + "\";");
       }
   }
   out.println("var tmodeCount = " + tmodeCount + ";");

   out.println(" function erasetext(pos1) {");
   out.println(" document.playerform[pos1].value = '';");           // clear the text field
   out.println("}"); 
   
   
   // add option if not already in list (means it was member's default)
   out.println("function add(e, wc) {");
   out.println("  var i=0;");
   out.println("  for (i=0;i<e.length;i++) {");
   out.println("    if (e.options[i].value == wc) {");
   out.println("      return;");
   out.println("    }");        // end if
   out.println("  }");      // end for
   out.println("  for (i=0;i<tmodeCount;i++) {");
   out.println("    if (tmodes[i] == wc) {");
   out.println("      e.options[e.length] = new Option(wc, wc);");
   out.println("    }");
   out.println("  }");
   out.println("}");            // End of function add()
   
   //  movenotes script
   out.println("function movenotes() {");
   out.println(" var oldnotes = document.forms['playerform'].oldnotes.value;");
   if (parmM.protect_notes == false) {      // if ok for member to add/change notes (custom)
       out.println(" document.forms['playerform'].notes.value = oldnotes;");   // put notes in text area
   } else {      
       out.println(" document.forms['playerform'].protectednotes.value = oldnotes;");   // put notes in protected text area
   }
   out.println("}");                  // end of script function
   
   
   // doPrompt script
   out.println("function doPrompt(letter, player, ind, i) {");      // letter (player value), player parm name, index optgrp, player position number
   out.println("var sel=document.getElementById('player' +i);");    // get the select object for the player position being updated
   out.println("var optgrp = sel.options[ind].parentNode.label");   // get the optGroup that was selected
   
   out.println("if (optgrp == 'Partners') {");                      // if a Partner was selected
   out.println(" var el = eval(\"document.forms['playerform'].\" + player + \";\")");   // get name of player parm (player1, etc)
   out.println(" var tmp1 = el.options[el.selectedIndex].value;");                      // get partner name and mode of trans
   out.println(" var tmp3 = tmp1.split('|');");                                         // parse them
   out.println(" var cw = '';");                                                        // get partner's mode of trans option
   out.println(" if (tmp3[1]) cw = tmp3[1];");
   out.println(" if (cw == '') {");
   out.println("  eval(\"document.forms['playerform'].p\" +i+ \"cw.selectedIndex = -1;\")");    // clear this player's mode of trans option
   out.println(" } else {");
   out.println("  eval(\"add(document.forms['playerform'].p\" +i+ \"cw, cw);\")");
   out.println("  eval(\"document.forms['playerform'].p\" +i+ \"cw.value = cw;\")");    // set this player's mode of trans option
   out.println(" }");
   out.println("} else {");
   
   out.println("  if (optgrp == 'Name List') {");                                   // if a letter was selected
   out.println("   document.forms['playerform'].letterPrompt.value = letter;");     // put selected letter in parm
   out.println("   document.forms['playerform'].letterSlot.value = i;");            // set the player position being updated (index value 0 - 24)
   out.println("   document.forms['playerform'].submit();");                        // submit the form
   out.println("  }");   
   out.println("}");
   
   if (parm.forceg > 0) {                           // if club forces members to add names to guests
       
      out.println("if (optgrp == 'Guest') {");                                    // if a Guest was selected
      out.println("   document.forms['playerform'].gstPrompt.value = letter;");     // put selected Guest Type in parm
      out.println("   document.forms['playerform'].letterSlot.value = i;");         // set the player position being updated (index value 0 - 24)
      out.println("   document.forms['playerform'].submit();");                     // submit the form
      out.println("}");       
      
   } else if (Utilities.isGuestTrackingConfigured(activity_id, con)) {
       
      out.println("if (optgrp == 'Guest') {");                                    // if a Guest was selected
      out.println("  del = '|';");                                                    // deliminator is a colon
      out.println("  array = letter.split(del);");                                    // split string into 2 pieces (guest, yes/no)
      out.println("  var guest = array[0];");
      out.println("  var track = array[1];");
      out.println("  if (track == 'yes') {");                                          // if Guest is a tracking gsts
      out.println("      document.forms['playerform'].gstPrompt.value = guest;");      // put selected Guest Type in parm
      out.println("      document.forms['playerform'].letterSlot.value = i;");         // set the player position being updated (index value 0 - 24)
      out.println("      document.forms['playerform'].submit();");                     // submit the form
      out.println("  }");       
      out.println("}");       
   }
   out.println("}");

   // we'll do this before submitting the form so we can trim off the cw option from the name
   out.println("function scrubOptions() {");
   out.println(" for (i = 1; i <= " + max + "; i++) {"); // loop thru each player# select box

   out.println("  sel=document.getElementById('player'+i);");
   
   out.println("  for (i2 = 0; i2 < sel.options.length; i2++) {");                // loop thru each option for this select box
   out.println("   if (sel.options[i2].value.indexOf('|') != -1) sel.options[i2].value = sel.options[i2].text;"); // set the value to the text to get rid of the delim & cw
   out.println("  }");

   out.println(" }");
   out.println("}");
   
   out.println("// -->");
   out.println("</script>");          // End of scripts
   
   
   
   out.println("</HEAD>");
   
   out.println("<body onLoad=\"movenotes()\">");
   
   out.println(SystemUtils.BannerMobileSlot());                              // banner w/o Home and Logout links

   out.println("<div class=\"headertext\">Tee Time Request</div>");
   out.println("<div class=\"content\">");
   out.println("<div class=\"smheadertext\"><strong>For " +parmM.day+ " " +parmM.mm+ "/" + parmM.dd + "/" + parmM.yy + " at " + parmM.stime);
   if (!parmM.course.equals("")) {
      out.println("<br>On " +parmM.course);
   }
   out.println("<br>Note: 9 = 9 Hole Round");
    if (parmM.club.equals( "interlachen" ) || parmM.club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
       out.println("&nbsp;&nbsp;&nbsp;GP = Gift Pack");
    }
   out.println("</strong></div>");

   if (parmM.type.equals("lottery")) {     // if lottery request      
      out.println("<form action=\"Member_lott\" method=\"post\" name=\"playerform\" onsubmit=\"scrubOptions()\">");
   } else {            // tee time    
      out.println("<form action=\"Member_slot\" method=\"post\" name=\"playerform\" onsubmit=\"scrubOptions()\">");
   }

   //
   //   Notes - Script will put any existing notes in the textarea (value= doesn't work)
   //
   out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + parmM.notes + "\">"); // hold notes for script
   
   if (parmM.hide != 0) {      // if proshop wants to hide the notes, do not display the text box or notes

      out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parmM.notes + "\">"); // pass existing notes
   }
   

    out.println("<table cellpadding=\"0\" cellspacing=\"1\" border=\"1\" id=\"slotdata\">");
    out.println("<tr class=\"tableheader\">");
    out.println("<td><strong>Player</strong></td>");
    out.println("<td><strong>Mode</strong></td>");
    out.println("<td><strong>9</strong></td>");
    if (parmM.club.equals( "interlachen" ) || parmM.club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
       out.println("<td><strong>GP</strong></td>");
       cols = 4;            // use 4 columns in table
    }
    out.println("</tr>");

   //
   //  Output the individual player slots
   //
   i = 0;
   
   while (i < max) {

      out.println("<input type=\"hidden\" name=\"guest_id" + (i+1) + "\" value=\"" + parmM.guest_idA[i] + "\">");
      
      out.println("<tr class=\"tablerow\">");

      if (!parmM.playerA[i].equals("") && parmM.blockPA[i] == true) {    // IF option to not allow mems to change player
         
         out.println("<td>");
         out.println((i+1)+ ":&nbsp;<input disabled type=\"text\" id=\"player" +(i+1)+ "\" name=\"player" +(i+1)+ "\" value=\""+parmM.playerA[i]+"\" size=\"20\" maxlength=\"30\">");
         out.println("<input type=\"hidden\" name=\"player" +(i+1)+ "\" value=\"" +parmM.playerA[i]+ "\">");

         out.println("</td><td>");
         out.println("<select disabled size=\"1\" name=\"p" +(i+1)+ "cw\" id=\"p" +(i+1)+ "cw\">");
         if (parmM.pcwA[i].equals("")) {
             out.println("<option style=\"width:25px; height:15px\" value=\"\"></option>");
         } else {
             out.println("<option selected value=" + parmM.pcwA[i] + ">" + parmM.pcwA[i] + "</option>");

         }
         out.println("</select>");
         out.println("<input type=\"hidden\" name=\"p" +(i+1)+ "cw\" value=\"" + parmM.pcwA[i] + "\">");

         out.println("</td><td>");
         if (parmM.p9A[i] == 1 || (parmM.club.equals( "congressional" ) && parmM.twoSomeOnly == true)) {   // force 9-hole option
             out.println("<input disabled type=\"checkbox\" checked name=\"p9" +(i+1)+ "\" value=\"1\">");
             out.println("<input type=\"hidden\" name=\"p9" +(i+1)+ "\" value=\"1\">");
         } else {
             out.println("<input disabled type=\"checkbox\" name=\"p9" +(i+1)+ "\" value=\"1\">");
         }

         if (parmM.club.equals( "interlachen" ) || parmM.club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
             out.println("</td><td>");
             if (parmM.gpA[i] == 1) {
                 out.println("<input disabled type=\"checkbox\" checked name=\"gp" +(i+1)+ "\" value=\"1\">");
             out.println("<input type=\"hidden\" name=\"gp" +(i+1)+ "\" value=\"1\">");
             } else {
                 out.println("<input disabled type=\"checkbox\" name=\"gp" +(i+1)+ "\" value=\"1\">");
             }
         }

      } else {     // all others (ok to add or change)

         out.println("<td>");
         //
         //  Build list of player options - buddies, X, Guests, Alpha Table
         //
         out.println((i+1)+ ":&nbsp;<select size=\"1\" name=\"player" +(i+1)+ "\" id=\"player" +(i+1)+ "\" onChange=\"doPrompt(this.value, this.id, this.selectedIndex, " +(i+1)+ ");\">");

         //  add player name if it exists
         if (!parmM.playerA[i].equals("")) {                              // if player already exists
            out.println("<option selected value=\"" +parmM.playerA[i]+ "\">" +parmM.playerA[i]+ "</option>");
         }
         
         
         //  add empty value
         if (parmM.playerA[i].equals("")) {            // if empty
            out.println("<option selected value=\"\">[none]</option>");
         } else {
            out.println("<option value=\"\">[none]</option>");
         }
         
         
         //  add Partners
         if (partner_names.size() > 0) {
            
            out.println("<optgroup label=\"Partners\">");        // Partners Group

            String wc = "";

            for (int j=0; j<partner_names.size(); j++) {

                if (!partner_names.get(j).equals("")) {

                    out.print("<option ");
                    if (parmM.playerA[i].equals(partner_names.get(j))) out.print("selected ");
                    wc = (!partner_wcs.get(j).equals("")) ? "|" + partner_wcs.get(j) : "";
                    out.println("value=\"" + partner_names.get(j) + wc + "\">" + partner_names.get(j) + "</option>");
                    
                }
            }
            
            out.println("</optgroup>");
         }
         
         
         //  add X          
         if (x > 0) {          // if OK to use X
            
            out.println("<optgroup label=\"TBD\">"); 

            if (parmM.playerA[i].equalsIgnoreCase("x")) {    
               out.println("<option selected value=\"X\">X</option>");
            } else {
               out.println("<option value=\"X\">X</option>");
            }
            out.println("</optgroup>");
         }
         
         //  add Guests 
         if (gstCount > 0) {
            
            out.println("<optgroup label=\"Guest\">");       // GUESTS
            
            String guestD = "";
            i2 = 0;
            
            while (i2 < gstCount) {

               guest = nonProGuests[i2];        // get next Guest Type
               
               guestD = guest;
                       
               if (parm.forceg == 0 && Utilities.isGuestTrackingConfigured(activity_id, con)) {   // if guest tracking && NOT force guest names
       
                    guestD = guest + "|no";                                     // we need to identify the guest tracking guest types

                    for (int ig=0; ig<gstTrackingGsts.size(); ig++) {           // check if guest type requires guest tracking

                        if (guest.equals(gstTrackingGsts.get(ig))) {  

                            guestD = guest + "|yes";
                            break;
                        }
                    }
               }

               if (parmM.playerA[i].equals(guest)) {
                  out.println("<option selected value=\"" +guestD+ "\">" +guest+ "</option>");
               } else {
                  out.println("<option value=\"" +guestD+ "\">" +guest+ "</option>");
               }
               
               i2++;
            }
            out.println("</optgroup>");
         }
         
         
         //  add Alphabet Letters          
         out.println("<optgroup label=\"Name List\">"); 
         out.println("<option value=\"A\">A</option>");
         out.println("<option value=\"B\">B</option>");
         out.println("<option value=\"C\">C</option>");
         out.println("<option value=\"D\">D</option>");
         out.println("<option value=\"E\">E</option>");
         out.println("<option value=\"F\">F</option>");
         out.println("<option value=\"G\">G</option>");
         out.println("<option value=\"H\">H</option>");
         out.println("<option value=\"I\">I</option>");
         out.println("<option value=\"J\">J</option>");
         out.println("<option value=\"K\">K</option>");
         out.println("<option value=\"L\">L</option>");
         out.println("<option value=\"M\">M</option>");
         out.println("<option value=\"N\">N</option>");
         out.println("<option value=\"O\">O</option>");
         out.println("<option value=\"P\">P</option>");
         out.println("<option value=\"Q\">Q</option>");
         out.println("<option value=\"R\">R</option>");
         out.println("<option value=\"S\">S</option>");
         out.println("<option value=\"T\">T</option>");
         out.println("<option value=\"U\">U</option>");
         out.println("<option value=\"V\">V</option>");
         out.println("<option value=\"W\">W</option>");
         out.println("<option value=\"X\">X</option>");
         out.println("<option value=\"Y\">Y</option>");
         out.println("<option value=\"Z\">Z</option>");       
         out.println("</optgroup>");
         
         out.println("</select>");                  // end of player list

         
         // Mode of Trans Options
         out.println("</td><td>");
         out.println("<select size=\"1\" name=\"p" +(i+1)+ "cw\" id=\"p" +(i+1)+ "cw\">");

         for (int j=0; j<parmc.tmode_limit; j++) {

             if (!parmc.tmodea[j].equals("")){
                 
                 if (parmc.tmodea[j].equals(parmM.pcwA[i])) {
                     out.println("<option selected value=" + parmM.pcwA[i] + ">" + parmM.pcwA[i] + "</option>");
                 } else if (parmc.tOpt[j] == 0) {
                     out.println("<option value=" + parmc.tmodea[j] + ">" + parmc.tmodea[j] + "</option>");
                 }
             }
         }

         /*
         for (i2=0; i2<nonProCount; i2++) {        // get all c/w options

             if (nonProTmodes[i2].equals( parmM.pcwA[i] )) {
                 out.println("<option selected value=" + parmM.pcwA[i] + ">" + parmM.pcwA[i] + "</option>");
             } else {
                 out.println("<option value=\"" +nonProTmodes[i2]+ "\">" +nonProTmodes[i2]+ "</option>");
             }
         }
          */
         out.println("</select>");

         
         //  9-Hole Check Box
         out.println("</td><td>");
         if (parmM.p9A[i] == 1 || (parmM.club.equals( "congressional" ) && parmM.twoSomeOnly == true)) {   // force 9-hole option
             out.println("<input type=\"checkbox\" checked name=\"p9" +(i+1)+ "\" value=\"1\">");
         } else {
             out.println("<input type=\"checkbox\" name=\"p9" +(i+1)+ "\" value=\"1\">");
         }

         // Custom Gift Back 
         if (parmM.club.equals( "interlachen" ) || parmM.club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
             out.println("</td><td>");
             if (parmM.gpA[i] == 1) {
                 out.println("<input type=\"checkbox\" checked name=\"gp" +(i+1)+ "\" value=\"1\">");
             } else {
                 out.println("<input type=\"checkbox\" name=\"gp" +(i+1)+ "\" value=\"1\">");
             }
         }
      }
      
      out.println("</td></tr>");
      
      i++;     // next player
                     
   }         // end of player loop 
   
   // print player5 as hidden inputs if 5somes not allowed
   if (!parmM.p5.equals("Yes")) {
        out.println("<input type=\"hidden\" name=\"player5\" value=\"" + parmM.playerA[4] + "\">");
        out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + parmM.pcwA[4] + "\">");
   }
            
   //
   //   Notes
   //
   if (parmM.hide == 0) {      // if proshop does NOT want to hide the notes, display the text box or notes

      out.println("<tr class=\"tablerow\"><td colspan=\"" +cols+ "\">");
      
      if (parmM.protect_notes == false) {      // if ok for member to add/change notes (custom)
          out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
          out.println("Notes:&nbsp;<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"16\" rows=\"2\"></textarea>");
      } else {
          out.println("Notes:&nbsp;<textarea disabled name=\"protectednotes\" value=\"\" id=\"protectednotes\" cols=\"16\" rows=\"2\"></textarea>");
          out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parmM.notes + "\">"); // pass existing notes - iPhone will not pass the disabled textarea!!
      }
      out.println("</td></tr>");
   }
   
   out.println("</table>");

   out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + parmM.date + "\">");    // requires numeric date format (mmddyyyy)
   out.println("<input type=\"hidden\" name=\"date\" value=\"" + parmM.date + "\">");     // required for verify method
   out.println("<input type=\"hidden\" name=\"day\" value=\"" + parmM.day + "\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parmM.fb + "\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parmM.time + "\">");    // requires numeric time value (hhmm)
   out.println("<input type=\"hidden\" name=\"time\" value=\"" + parmM.time + "\">");     // required for verify method
   out.println("<input type=\"hidden\" name=\"mm\" value=\"" + parmM.mm + "\">");
   out.println("<input type=\"hidden\" name=\"yy\" value=\"" + parmM.yy + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + parmM.index + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + parmM.course + "\">");
   out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parmM.returnCourse + "\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parmM.p5 + "\">");
   out.println("<input type=\"hidden\" name=\"hide\" value=\"" + parmM.hide + "\">");
   out.println("<input type=\"hidden\" name=\"custom_int\" value=\"" + parmM.custom_int + "\">");
   if (parmM.type.equals("lottery")) {     // if lottery request      
      out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + parmM.mins_before + "\">");
      out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + parmM.mins_after + "\">");
      out.println("<input type=\"hidden\" name=\"lname\" value=\"" + parmM.lottName + "\">");
      out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + parmM.lstate + "\">");
      out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + parmM.lottid + "\">");
      out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parmM.slots + "\">");
      out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + parmM.checkothers + "\">");
   }
   out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + parmM.displayOpt + "\">");
   out.println("<input type=\"hidden\" name=\"return\" value=\"yes\">");           // we need this in case we return for letter of guest name prompt
   out.println("<input type=\"hidden\" name=\"letterPrompt\" value=\"\">");     // used to indicate letter prompt (set by doPrompt script)
   out.println("<input type=\"hidden\" name=\"gstPrompt\" value=\"\">");        // used to indicate a guest type prompt (set by doPrompt script)
   out.println("<input type=\"hidden\" name=\"letterSlot\" value=\"\">");     // used to indicate which player position is being updated

   if (!parmM.type.equals("lottery") && parmM.allowCancel == true && parmM.newreq == false) {   // if tee time request and Cancel ok    

      out.println("<BR><p align=\"center\"><input type=submit value=\"Cancel ENTIRE Tee Time\" name=\"remove\"></p>");
   }
                    
   out.println("<BR><p align=\"center\"><input type=submit value=\"Submit\" name=\"submitForm\"></p>");
   out.println("</form>");        
    
   //
   //  Add "Return W/O Changes" button
   //
   if (parmM.lottid > 0 || !parmM.type.equals("lottery")) {    // if lottery AND id has been assigned, OR NOT a lottery request

      if (parmM.type.equals("lottery")) {     // if lottery request
         out.println("<form action=\"Member_lott\" method=\"post\" name=\"can\">");
      } else {            // tee time
         out.println("<form action=\"Member_slot\" method=\"post\" name=\"can\">");
      }
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + parmM.index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + parmM.course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parmM.returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" + parmM.date + "\">");
      out.println("<input type=\"hidden\" name=\"time\" value=\"" + parmM.time + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parmM.fb + "\">");
      out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + parmM.lottid + "\">");

   } else {            // go directly back to caller

      if (parmM.index.equals( "995" )) {         // if came from Member_teelist_list

         out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain2.htm\">");

      } else {

         out.println("<form action=\"Member_sheet\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + parmM.index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + parmM.course + "\">");
      }
   }
   out.println("<div class=\"contentsm\">");
   out.println("<p align=\"center\">Return w/o Changes:");
   out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form></p></div>");

   out.println("</div>");
   out.println("<div><p>&nbsp;</p><p>&nbsp;</p></div>");    //  add a couple of blank lines at the bottom to allow for the IOS Nav Bar (iPhones)
   out.println("</body></html>");        
   out.close();

 }     // end of doSlot
 
 
 // *********************************************************
 //
 //  User selected a letter or guest type in a player slot.
 //  Prompt user for a name.
 //
 //
 //     Called by:  Member_lott 
 //                 Member_slot
 //                 Member_slotm (future maybe)
 //
 //
 // *********************************************************

 public static void namePrompt(String caller, String club, String user, HttpServletRequest req, PrintWriter out, Connection con) {

    
   PreparedStatement pstmt = null;
   ResultSet rs = null;
    
    
   //
   //  The user has selected an alphabet letter or a guest type from the player slot above.
   //  If it was a single letter, then prompt the user with a list of members with last names that start with that letter.
   //  If it was a guest type, then prompt the user for the name of their guest.
   //
   //  First, we must gather all the parms so we can go back to _slot or _lott.
   //
   String player1 = "";            // allow for 5 groups of five
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String player7 = "";
   String player8 = "";
   String player9 = "";
   String player10 = "";
   String player11 = "";
   String player12 = "";
   String player13 = "";
   String player14 = "";
   String player15 = "";
   String player16 = "";
   String player17 = "";
   String player18 = "";
   String player19 = "";
   String player20 = "";
   String player21 = "";
   String player22 = "";
   String player23 = "";
   String player24 = "";
   String player25 = "";
   String gstType = "";
   String letter = "";

   int sess_activity_id = 0;        // Temp value since only used on Golf side at the moment

   boolean use_guestdb = false;
   
   boolean guestdbTbaAllowed = Utilities.isGuestTrackingTbaAllowed(0, false, con);
   
   
   if (req.getParameter("player1") != null) {
      player1 = req.getParameter("player1").trim();
   }
   if (req.getParameter("player2") != null) {
      player2 = req.getParameter("player2").trim();
   }
   if (req.getParameter("player3") != null) {
      player3 = req.getParameter("player3").trim();
   }
   if (req.getParameter("player4") != null) {
      player4 = req.getParameter("player4").trim();
   }
   if (req.getParameter("player5") != null) {
      player5 = req.getParameter("player5").trim();
   }
   if (req.getParameter("player6") != null) {
      player6 = req.getParameter("player6").trim();
   }
   if (req.getParameter("player7") != null) {
      player7 = req.getParameter("player7").trim();
   }
   if (req.getParameter("player8") != null) {
      player8 = req.getParameter("player8").trim();
   }
   if (req.getParameter("player9") != null) {
      player9 = req.getParameter("player9").trim();
   }
   if (req.getParameter("player10") != null) {
      player10 = req.getParameter("player10").trim();
   }
   if (req.getParameter("player11") != null) {
      player11 = req.getParameter("player11").trim();
   }
   if (req.getParameter("player12") != null) {
      player12 = req.getParameter("player12").trim();
   }
   if (req.getParameter("player13") != null) {
      player13 = req.getParameter("player13").trim();
   }
   if (req.getParameter("player14") != null) {
      player14 = req.getParameter("player14").trim();
   }
   if (req.getParameter("player15") != null) {
      player15 = req.getParameter("player15").trim();
   }
   if (req.getParameter("player16") != null) {
      player16 = req.getParameter("player16").trim();
   }
   if (req.getParameter("player17") != null) {
      player17 = req.getParameter("player17").trim();
   }
   if (req.getParameter("player18") != null) {
      player18 = req.getParameter("player18").trim();
   }
   if (req.getParameter("player19") != null) {
      player19 = req.getParameter("player19").trim();
   }
   if (req.getParameter("player20") != null) {
      player20 = req.getParameter("player20").trim();
   }
   if (req.getParameter("player21") != null) {
      player21 = req.getParameter("player21").trim();
   }
   if (req.getParameter("player22") != null) {
      player22 = req.getParameter("player22").trim();
   }
   if (req.getParameter("player23") != null) {
      player23 = req.getParameter("player23").trim();
   }
   if (req.getParameter("player24") != null) {
      player24 = req.getParameter("player24").trim();
   }
   if (req.getParameter("player25") != null) {
      player25 = req.getParameter("player25").trim();
   }
   
   if (req.getParameter("gstPrompt") != null) {
      gstType = req.getParameter("gstPrompt").trim();           // get the Guest Type, if selected
   }
   if (req.getParameter("letterPrompt") != null) {
      letter = req.getParameter("letterPrompt").trim();         // get the Letter, if selected
   }
   
   
   
   String temp = req.getParameter("letterSlot");     // get the player position being changed (1 - 25)
   
   int playerSlot = Integer.parseInt(temp);       
   
   int i = 0;
   
   
   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  Get the walk/cart options available
   //
   try {

      getParms.getCourse(con, parmc, "");  // get course parms
   }
   catch (Exception e1) {
   }

       
   //
   //  Check if letter is an actaul letter or a guest type
   //
   if (!letter.equals("")) {      // if letter
      
      //
      //  Prompt user for member name from list of members with last name starting with this letter
      //
      letter = letter + "%";

      String first = "";
      String mid = "";
      String last = "";
      String bname = "";
      String wname = "";
      String dname = "";
      String wc = "";

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
      out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\" />");
      out.println("<Title>Member Tee Time Request Page</Title>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/mobile/style.css\" type=\"text/css\" media=\"screen\">");


      //  ********* put scripts here ******************
      //
      //*******************************************************************
      //  Build and submit the form when user selects a name.
      //*******************************************************************
      //
      out.println("<script type=\"text/javascript\">");            // Submit name script
      out.println("<!--");
      out.println("function subname(namewc, slot) {");
      out.println("del = ':';");                               // deliminator is a colon
      out.println("array = namewc.split(del);");               // split string into 2 pieces (name, wc)
      out.println("var name = array[0];");
      out.println("var wc = array[1];");      
      out.println(" eval(\"document.forms['playerform'].player\" +slot+ \".value = name;\");");   // set name in player parm
      out.println(" eval(\"document.forms['playerform'].p\" +slot+ \"cw.value = wc;\");");        // set the cw value
      out.println(" document.forms['playerform'].submit();");                        // submit the form
      out.println("}");
      out.println("// -->");
      out.println("</script>");

      out.println("</HEAD>");
      out.println("<body>");

      out.println(SystemUtils.BannerMobileSlot());                              // banner w/o Home and Logout links

      out.println("<div class=\"content\">");
      out.println("<div class=\"headertext\">");  
         out.println("Select Name from List");
      out.println("</div>");
            
      if (caller.equals("lottery")) {     // if lottery request      
         out.println("<form action=\"Member_lott\" method=\"post\" name=\"playerform\">");      
      } else {            // tee time    
         out.println("<form action=\"Member_slot\" method=\"post\" name=\"playerform\">");
      }

      //
      // add all the hidden parms required
      //
      outputParms(req, out);
         
   
      try {

         pstmt = con.prepareStatement (
                  "SELECT name_last, name_first, name_mi, wc FROM member2b " +
                  "WHERE name_last LIKE ? AND inact = 0 ORDER BY name_last, name_first, name_mi");

         pstmt.clearParameters();               // clear the parms
         pstmt.setString(1, letter);            // put the parm in stmt
         rs = pstmt.executeQuery();             // execute the prepared stmt

         out.println("<select size=\"1\" name=\"bname\" onChange=\"subname(this.form.bname.value, " +playerSlot+ ")\">");

         out.println("<option value=\" \">-select a name-</option>");      // was "disabled" but Chrome and Safari will not show it with that tag.  We need this to appear first in
                                                                           // the list in case the user wants to select the first name (allows the movename to work when they select the name)
         
         while(rs.next()) {

            last = rs.getString(1);
            first = rs.getString(2);
            mid = rs.getString(3);
            wc = rs.getString(4);           // walk/cart preference

            if (club.equals( "cherryhills" )) {        // if cherry hills, no default c/w

               wc = "";

            } else {

               i = 0;
               loopi3:
               while (i < 16) {             // make sure wc is supported

                  if (parmc.tmodea[i].equals( wc )) {

                     break loopi3;
                  }
                  i++;
               }
               if (i > 15) {       // if we went all the way without a match

                  wc = parmc.tmodea[0];    // use default option
               }
            }

            i = 0;

            if (mid.equals("")) {

               bname = first + " " + last;
               dname = last + ", " + first;
            } else {

               bname = first + " " + mid + " " + last;
               dname = last + ", " + first + " " + mid;
            }

            //
            //  Make sure member is not already included in the tee time
            //
            if (!bname.equals(player1) && !bname.equals(player2) && !bname.equals(player3) && !bname.equals(player4) && !bname.equals(player5) && 
                !bname.equals(player6) && !bname.equals(player7) && !bname.equals(player8) && !bname.equals(player9) && !bname.equals(player10) && 
                !bname.equals(player11) && !bname.equals(player12) && !bname.equals(player13) && !bname.equals(player14) && !bname.equals(player15) && 
                !bname.equals(player16) && !bname.equals(player17) && !bname.equals(player18) && !bname.equals(player19) && !bname.equals(player20) && 
                !bname.equals(player21) && !bname.equals(player22) && !bname.equals(player23) && !bname.equals(player24) && !bname.equals(player25)) {
            
               wname = bname + ":" + wc;              // combine name:wc for script

               out.println("<option value=\"" + wname + "\">" + dname + "</option>");
            }
         }

         out.println("</select>");

         pstmt.close();
      }
      catch (Exception ignore) {

      }
      
      out.println("<BR><div><input type=submit value=\"Continue\"></div></form>");
   
      out.println("</div></body></html>");                 
      out.close();
           
      
   } else {      // must be guest type
      
      //
      //  User selected a guest type and this club requires names for guests - prompt user for the name
      //
       
      // See if this guest type uses the guest tracking system
      use_guestdb = Common_guestdb.isGuestTypeConfigured(gstType, sess_activity_id, con);
      

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
      out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\" />");
      out.println("<Title>Member Tee Time Request Page</Title>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/mobile/style.css\" type=\"text/css\" media=\"screen\">");
      //  any scripts would go here      
      out.println("</HEAD>");
      out.println("<body>");

      out.println(SystemUtils.BannerMobileSlot());                              // banner w/o Home and Logout links

      out.println("<div class=\"content\">");
      out.println("<div class=\"headertext\">");
      if (use_guestdb) {
         out.println("Please Select a Guest");
      } else {
         out.println("Please Enter Your Guest's Full Name");
      }
      out.println("</div>");

      if (caller.equals("lottery")) {     // if lottery request
         out.println("<form action=\"Member_lott\" method=\"post\" name=\"playerform\">");
      } else {            // tee time
         out.println("<form action=\"Member_slot\" method=\"post\" name=\"playerform\">");
      }

      String parmName = "player" +playerSlot;       // build name of the parm to exclude from hidden parm list (name of parm built below)

      //
      // add all the hidden parms required
      //
      outputParms(req, out, parmName);             // add the hidden parms excluding this one

      // See if guest tracking is used for this guest
      if (use_guestdb) {
          
          out.println("<BR><p align=\"center\">Select an existing guest from the list" + ( guestdbTbaAllowed ? ", or select TBA if guest is unknown at this time" : "" ) + ".<br><br>");  //, or click Add New Guest to create a new guest.<BR><BR>");
          out.println("<input type=\"hidden\" name=\"" + parmName + "\" value=\"" + gstType + " \">");
          out.println("<input type=\"hidden\" name=\"guest_slot\" value=\"" + parmName + "\">");

          // Display list of this member's guests
          alphaTable.guestdbList(user, 1, sess_activity_id, 0, out, con);

          out.println("<BR>");

      } else {      // Guest tracking not used

          out.println("<BR><p align=\"center\">Enter the name of your guest <b>after</b> the guest type below (i.e. " +gstType+ " John Smith).<BR><BR>");

          out.println("Guest Name:&nbsp;<input type=\"text\" name=\"" +parmName+ "\" id=\"" +parmName+ "\" value=\"" +gstType+ " \" size=\"34\" maxlength=\"43\"></p>");
      }

      out.println("<BR><p align=\"center\"><input type=submit value=\"Continue\"></p>");

      out.println("</form>");
      
      /*
      
      if (use_guestdb) {
          
          //  Add a new guest name
          * 
          * 
          * perform similar function as we do when we prompt user to select from the existing guest names??????
          * 
 
      }
      * 
      */
      
      
      out.println("</div></body></html>");                 
      out.close();
      
   }
            
 }   // end of namePrompt
 
 

 // *********************************************************
 //   Output the hidden parms
 // *********************************************************

 public static void outputParms(HttpServletRequest req, PrintWriter out) {
    
   outputParms(req, out, "");  
 }

 public static void outputParms(HttpServletRequest req, PrintWriter out, String parmName) {

    
   //
   // add all the hidden parms required
   //
   if (!parmName.equals("player1")) {           // if this parm to be included
      out.println("<input type=\"hidden\" name=\"player1\" value=\"" + req.getParameter("player1") + "\">");
   }
   if (!parmName.equals("player2")) {
      if (req.getParameter("player2") != null) {
         out.println("<input type=\"hidden\" name=\"player2\" value=\"" + req.getParameter("player2") + "\">");
      }
   }
   if (!parmName.equals("player3")) {
      if (req.getParameter("player3") != null) {
         out.println("<input type=\"hidden\" name=\"player3\" value=\"" + req.getParameter("player3") + "\">");
      }
   }
   if (!parmName.equals("player4")) {
      if (req.getParameter("player4") != null) {
         out.println("<input type=\"hidden\" name=\"player4\" value=\"" + req.getParameter("player4") + "\">");
      }
   }
   if (!parmName.equals("player5")) {
      if (req.getParameter("player5") != null) {
         out.println("<input type=\"hidden\" name=\"player5\" value=\"" + req.getParameter("player5") + "\">");
      }
   }
   if (!parmName.equals("player6")) {
      if (req.getParameter("player6") != null) {
         out.println("<input type=\"hidden\" name=\"player6\" value=\"" + req.getParameter("player6") + "\">");
      }
   }
   if (!parmName.equals("player7")) {
      if (req.getParameter("player7") != null) {
         out.println("<input type=\"hidden\" name=\"player7\" value=\"" + req.getParameter("player7") + "\">");
      }
   }
   if (!parmName.equals("player8")) {
      if (req.getParameter("player8") != null) {
         out.println("<input type=\"hidden\" name=\"player8\" value=\"" + req.getParameter("player8") + "\">");
      }
   }
   if (!parmName.equals("player9")) {
      if (req.getParameter("player9") != null) {
         out.println("<input type=\"hidden\" name=\"player9\" value=\"" + req.getParameter("player9") + "\">");
      }
   }
   if (!parmName.equals("player10")) {
      if (req.getParameter("player10") != null) {
         out.println("<input type=\"hidden\" name=\"player10\" value=\"" + req.getParameter("player10") + "\">");
      }
   }
   if (!parmName.equals("player11")) {
      if (req.getParameter("player11") != null) {
         out.println("<input type=\"hidden\" name=\"player11\" value=\"" + req.getParameter("player11") + "\">");
      }
   }
   if (!parmName.equals("player12")) {
      if (req.getParameter("player12") != null) {
         out.println("<input type=\"hidden\" name=\"player12\" value=\"" + req.getParameter("player12") + "\">");
      }
   }
   if (!parmName.equals("player13")) {
      if (req.getParameter("player13") != null) {
         out.println("<input type=\"hidden\" name=\"player13\" value=\"" + req.getParameter("player13") + "\">");
      }
   }
   if (!parmName.equals("player14")) {
      if (req.getParameter("player14") != null) {
         out.println("<input type=\"hidden\" name=\"player14\" value=\"" + req.getParameter("player14") + "\">");
      }
   }
   if (!parmName.equals("player15")) {
      if (req.getParameter("player15") != null) {
         out.println("<input type=\"hidden\" name=\"player15\" value=\"" + req.getParameter("player15") + "\">");
      }
   }
   if (!parmName.equals("player16")) {
      if (req.getParameter("player16") != null) {
         out.println("<input type=\"hidden\" name=\"player16\" value=\"" + req.getParameter("player16") + "\">");
      }
   }
   if (!parmName.equals("player17")) {
      if (req.getParameter("player17") != null) {
         out.println("<input type=\"hidden\" name=\"player17\" value=\"" + req.getParameter("player17") + "\">");
      }
   }
   if (!parmName.equals("player18")) {
      if (req.getParameter("player18") != null) {
         out.println("<input type=\"hidden\" name=\"player18\" value=\"" + req.getParameter("player18") + "\">");
      }
   }
   if (!parmName.equals("player19")) {
      if (req.getParameter("player19") != null) {
         out.println("<input type=\"hidden\" name=\"player19\" value=\"" + req.getParameter("player19") + "\">");
      }
   }
   if (!parmName.equals("player20")) {
      if (req.getParameter("player20") != null) {
         out.println("<input type=\"hidden\" name=\"player20\" value=\"" + req.getParameter("player20") + "\">");
      }
   }
   if (!parmName.equals("player21")) {
      if (req.getParameter("player21") != null) {
         out.println("<input type=\"hidden\" name=\"player21\" value=\"" + req.getParameter("player21") + "\">");
      }
   }
   if (!parmName.equals("player22")) {
      if (req.getParameter("player22") != null) {
         out.println("<input type=\"hidden\" name=\"player22\" value=\"" + req.getParameter("player22") + "\">");
      }
   }
   if (!parmName.equals("player23")) {
      if (req.getParameter("player23") != null) {
         out.println("<input type=\"hidden\" name=\"player23\" value=\"" + req.getParameter("player23") + "\">");
      }
   }
   if (!parmName.equals("player24")) {
      if (req.getParameter("player24") != null) {
         out.println("<input type=\"hidden\" name=\"player24\" value=\"" + req.getParameter("player24") + "\">");
      }
   }
   if (!parmName.equals("player25")) {
      if (req.getParameter("player25") != null) {
         out.println("<input type=\"hidden\" name=\"player25\" value=\"" + req.getParameter("player25") + "\">");
      }
   }
   
   if (req.getParameter("p1cw") != null) {
      out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + req.getParameter("p1cw") + "\">");
   }
   if (req.getParameter("p2cw") != null) {
      out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + req.getParameter("p2cw") + "\">");
   }
   if (req.getParameter("p3cw") != null) {
      out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + req.getParameter("p3cw") + "\">");
   }
   if (req.getParameter("p4cw") != null) {
      out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + req.getParameter("p4cw") + "\">");
   }
   if (req.getParameter("p5cw") != null) {
      out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + req.getParameter("p5cw") + "\">");
   }
   if (req.getParameter("p6cw") != null) {
      out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + req.getParameter("p6cw") + "\">");
   }
   if (req.getParameter("p7cw") != null) {
      out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + req.getParameter("p7cw") + "\">");
   }
   if (req.getParameter("p8cw") != null) {
      out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + req.getParameter("p8cw") + "\">");
   }
   if (req.getParameter("p9cw") != null) {
      out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + req.getParameter("p9cw") + "\">");
   }
   if (req.getParameter("p10cw") != null) {
      out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + req.getParameter("p10cw") + "\">");
   }
   if (req.getParameter("p11cw") != null) {
      out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + req.getParameter("p11cw") + "\">");
   }
   if (req.getParameter("p12cw") != null) {
      out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + req.getParameter("p12cw") + "\">");
   }
   if (req.getParameter("p13cw") != null) {
      out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + req.getParameter("p13cw") + "\">");
   }
   if (req.getParameter("p14cw") != null) {
      out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + req.getParameter("p14cw") + "\">");
   }
   if (req.getParameter("p15cw") != null) {
      out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + req.getParameter("p15cw") + "\">");
   }
   if (req.getParameter("p16cw") != null) {
      out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + req.getParameter("p16cw") + "\">");
   }
   if (req.getParameter("p17cw") != null) {
      out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + req.getParameter("p17cw") + "\">");
   }
   if (req.getParameter("p18cw") != null) {
      out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + req.getParameter("p18cw") + "\">");
   }
   if (req.getParameter("p19cw") != null) {
      out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + req.getParameter("p19cw") + "\">");
   }
   if (req.getParameter("p20cw") != null) {
      out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + req.getParameter("p20cw") + "\">");
   }
   if (req.getParameter("p21cw") != null) {
      out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + req.getParameter("p21cw") + "\">");
   }
   if (req.getParameter("p22cw") != null) {
      out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + req.getParameter("p22cw") + "\">");
   }
   if (req.getParameter("p23cw") != null) {
      out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + req.getParameter("p23cw") + "\">");
   }
   if (req.getParameter("p24cw") != null) {
      out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + req.getParameter("p24cw") + "\">");
   }
   if (req.getParameter("p25cw") != null) {
      out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + req.getParameter("p25cw") + "\">");
   }
   if (req.getParameter("p91") != null) {
      out.println("<input type=\"hidden\" name=\"p91\" value=\"" + req.getParameter("p91") + "\">");
   }
   if (req.getParameter("p92") != null) {
      out.println("<input type=\"hidden\" name=\"p92\" value=\"" + req.getParameter("p92") + "\">");
   }
   if (req.getParameter("p93") != null) {
      out.println("<input type=\"hidden\" name=\"p93\" value=\"" + req.getParameter("p93") + "\">");
   }
   if (req.getParameter("p94") != null) {
      out.println("<input type=\"hidden\" name=\"p94\" value=\"" + req.getParameter("p94") + "\">");
   }
   if (req.getParameter("p95") != null) {
      out.println("<input type=\"hidden\" name=\"p95\" value=\"" + req.getParameter("p95") + "\">");
   }
   if (req.getParameter("p96") != null) {
      out.println("<input type=\"hidden\" name=\"p96\" value=\"" + req.getParameter("p96") + "\">");
   }
   if (req.getParameter("p97") != null) {
      out.println("<input type=\"hidden\" name=\"p97\" value=\"" + req.getParameter("p97") + "\">");
   }
   if (req.getParameter("p98") != null) {
      out.println("<input type=\"hidden\" name=\"p98\" value=\"" + req.getParameter("p98") + "\">");
   }
   if (req.getParameter("p99") != null) {
      out.println("<input type=\"hidden\" name=\"p99\" value=\"" + req.getParameter("p99") + "\">");
   }
   if (req.getParameter("p910") != null) {
      out.println("<input type=\"hidden\" name=\"p910\" value=\"" + req.getParameter("p910") + "\">");
   }
   if (req.getParameter("p911") != null) {
      out.println("<input type=\"hidden\" name=\"p911\" value=\"" + req.getParameter("p911") + "\">");
   }
   if (req.getParameter("p912") != null) {
      out.println("<input type=\"hidden\" name=\"p912\" value=\"" + req.getParameter("p912") + "\">");
   }
   if (req.getParameter("p913") != null) {
      out.println("<input type=\"hidden\" name=\"p913\" value=\"" + req.getParameter("p913") + "\">");
   }
   if (req.getParameter("p914") != null) {
      out.println("<input type=\"hidden\" name=\"p914\" value=\"" + req.getParameter("p914") + "\">");
   }
   if (req.getParameter("p915") != null) {
      out.println("<input type=\"hidden\" name=\"p915\" value=\"" + req.getParameter("p915") + "\">");
   }
   if (req.getParameter("p916") != null) {
      out.println("<input type=\"hidden\" name=\"p916\" value=\"" + req.getParameter("p916") + "\">");
   }
   if (req.getParameter("p917") != null) {
      out.println("<input type=\"hidden\" name=\"p917\" value=\"" + req.getParameter("p917") + "\">");
   }
   if (req.getParameter("p918") != null) {
      out.println("<input type=\"hidden\" name=\"p918\" value=\"" + req.getParameter("p918") + "\">");
   }
   if (req.getParameter("p919") != null) {
      out.println("<input type=\"hidden\" name=\"p919\" value=\"" + req.getParameter("p919") + "\">");
   }
   if (req.getParameter("p920") != null) {
      out.println("<input type=\"hidden\" name=\"p920\" value=\"" + req.getParameter("p920") + "\">");
   }
   if (req.getParameter("p921") != null) {
      out.println("<input type=\"hidden\" name=\"p921\" value=\"" + req.getParameter("p921") + "\">");
   }
   if (req.getParameter("p922") != null) {
      out.println("<input type=\"hidden\" name=\"p922\" value=\"" + req.getParameter("p922") + "\">");
   }
   if (req.getParameter("p923") != null) {
      out.println("<input type=\"hidden\" name=\"p923\" value=\"" + req.getParameter("p923") + "\">");
   }
   if (req.getParameter("p924") != null) {
      out.println("<input type=\"hidden\" name=\"p924\" value=\"" + req.getParameter("p924") + "\">");
   }
   if (req.getParameter("p925") != null) {
      out.println("<input type=\"hidden\" name=\"p925\" value=\"" + req.getParameter("p925") + "\">");
   }

   if (req.getParameter("gp1") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp1\" value=\"" + req.getParameter("gp1") + "\">");
   }
   if (req.getParameter("gp2") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp2\" value=\"" + req.getParameter("gp2") + "\">");
   }
   if (req.getParameter("gp3") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp3\" value=\"" + req.getParameter("gp3") + "\">");
   }
   if (req.getParameter("gp4") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp4\" value=\"" + req.getParameter("gp4") + "\">");
   }
   if (req.getParameter("gp5") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp5\" value=\"" + req.getParameter("gp5") + "\">");
   }
   if (req.getParameter("gp6") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp6\" value=\"" + req.getParameter("gp6") + "\">");
   }
   if (req.getParameter("gp7") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp7\" value=\"" + req.getParameter("gp7") + "\">");
   }
   if (req.getParameter("gp8") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp8\" value=\"" + req.getParameter("gp8") + "\">");
   }
   if (req.getParameter("gp9") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp9\" value=\"" + req.getParameter("gp9") + "\">");
   }
   if (req.getParameter("gp10") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp10\" value=\"" + req.getParameter("gp10") + "\">");
   }
   if (req.getParameter("gp11") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp11\" value=\"" + req.getParameter("gp11") + "\">");
   }
   if (req.getParameter("gp12") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp12\" value=\"" + req.getParameter("gp12") + "\">");
   }
   if (req.getParameter("gp13") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp13\" value=\"" + req.getParameter("gp13") + "\">");
   }
   if (req.getParameter("gp14") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp14\" value=\"" + req.getParameter("gp14") + "\">");
   }
   if (req.getParameter("gp15") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp15\" value=\"" + req.getParameter("gp15") + "\">");
   }
   if (req.getParameter("gp16") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp16\" value=\"" + req.getParameter("gp16") + "\">");
   }
   if (req.getParameter("gp17") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp17\" value=\"" + req.getParameter("gp17") + "\">");
   }
   if (req.getParameter("gp18") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp18\" value=\"" + req.getParameter("gp18") + "\">");
   }
   if (req.getParameter("gp19") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp19\" value=\"" + req.getParameter("gp19") + "\">");
   }
   if (req.getParameter("gp20") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp20\" value=\"" + req.getParameter("gp20") + "\">");
   }
   if (req.getParameter("gp21") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp21\" value=\"" + req.getParameter("gp21") + "\">");
   }
   if (req.getParameter("gp22") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp22\" value=\"" + req.getParameter("gp22") + "\">");
   }
   if (req.getParameter("gp23") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp23\" value=\"" + req.getParameter("gp23") + "\">");
   }
   if (req.getParameter("gp24") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp24\" value=\"" + req.getParameter("gp24") + "\">");
   }
   if (req.getParameter("gp25") != null) {   // if any of the custom gift pack parms include        
      out.println("<input type=\"hidden\" name=\"gp25\" value=\"" + req.getParameter("gp25") + "\">");
   }

   if (req.getParameter("guest_id1") != null) out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + req.getParameter("guest_id1") + "\">");
   if (req.getParameter("guest_id2") != null) out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + req.getParameter("guest_id2") + "\">");
   if (req.getParameter("guest_id3") != null) out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + req.getParameter("guest_id3") + "\">");
   if (req.getParameter("guest_id4") != null) out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + req.getParameter("guest_id4") + "\">");
   if (req.getParameter("guest_id5") != null) out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + req.getParameter("guest_id5") + "\">");
   if (req.getParameter("guest_id6") != null) out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + req.getParameter("guest_id6") + "\">");
   if (req.getParameter("guest_id7") != null) out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + req.getParameter("guest_id7") + "\">");
   if (req.getParameter("guest_id8") != null) out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + req.getParameter("guest_id8") + "\">");
   if (req.getParameter("guest_id9") != null) out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + req.getParameter("guest_id9") + "\">");
   if (req.getParameter("guest_id10") != null) out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + req.getParameter("guest_id10") + "\">");
   if (req.getParameter("guest_id11") != null) out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + req.getParameter("guest_id11") + "\">");
   if (req.getParameter("guest_id12") != null) out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + req.getParameter("guest_id12") + "\">");
   if (req.getParameter("guest_id13") != null) out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + req.getParameter("guest_id13") + "\">");
   if (req.getParameter("guest_id14") != null) out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + req.getParameter("guest_id14") + "\">");
   if (req.getParameter("guest_id15") != null) out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + req.getParameter("guest_id15") + "\">");
   if (req.getParameter("guest_id16") != null) out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + req.getParameter("guest_id16") + "\">");
   if (req.getParameter("guest_id17") != null) out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + req.getParameter("guest_id17") + "\">");
   if (req.getParameter("guest_id18") != null) out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + req.getParameter("guest_id18") + "\">");
   if (req.getParameter("guest_id19") != null) out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + req.getParameter("guest_id19") + "\">");
   if (req.getParameter("guest_id20") != null) out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + req.getParameter("guest_id20") + "\">");
   if (req.getParameter("guest_id21") != null) out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + req.getParameter("guest_id21") + "\">");
   if (req.getParameter("guest_id22") != null) out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + req.getParameter("guest_id22") + "\">");
   if (req.getParameter("guest_id23") != null) out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + req.getParameter("guest_id23") + "\">");
   if (req.getParameter("guest_id24") != null) out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + req.getParameter("guest_id24") + "\">");
   if (req.getParameter("guest_id25") != null) out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + req.getParameter("guest_id25") + "\">");

   out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + req.getParameter("sdate") + "\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"" + req.getParameter("stime") + "\">");
   
   out.println("<input type=\"hidden\" name=\"notes\" value=\"" + req.getParameter("notes") + "\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"" + req.getParameter("day") + "\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + req.getParameter("fb") + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + req.getParameter("index") + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + req.getParameter("course") + "\">");
   out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + req.getParameter("returnCourse") + "\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"" + req.getParameter("p5") + "\">");
   out.println("<input type=\"hidden\" name=\"lname\" value=\"" + req.getParameter("lname") + "\">");
   out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + req.getParameter("lstate") + "\">");
   out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + req.getParameter("lottid") + "\">");
   out.println("<input type=\"hidden\" name=\"slots\" value=\"" + req.getParameter("slots") + "\">");
   out.println("<input type=\"hidden\" name=\"hide\" value=\"" + req.getParameter("hide") + "\">");
   out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + req.getParameter("mins_before") + "\">");
   out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + req.getParameter("mins_after") + "\">");
   out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + req.getParameter("checkothers") + "\">");
   out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + req.getParameter("displayOpt") + "\">");
   out.println("<input type=\"hidden\" name=\"custom_int\" value=\"" + req.getParameter("custom_int") + "\">");
   out.println("<input type=\"hidden\" name=\"return\" value=\"yes\">");                // required for proper processing in _lott or _slot 
        
 }   // end of outputParms

 
}
