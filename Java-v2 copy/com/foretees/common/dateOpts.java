/***************************************************************************************
 *   dateOpts:  Output the selected date options for a 'select' statement.
 *
 *       called by:  Proshop_add????  (all)
 *
 *
 *   created:  2/16/2005   Bob P.
 *
 *
 *   last updated:
 *
 *     10/17/07  Add more methods (from Common_config)
 *
 ***************************************************************************************
 */


package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;


public class dateOpts {


/**
 //************************************************************************
 //
 //  Output the month options
 //
 //************************************************************************
 **/

 public static void opMonth(PrintWriter out, int thisMonth) {


    if (thisMonth == 1) {                                             // start with current month
       out.println("<option selected value=\"01\">JAN</option>");
    } else {
       out.println("<option value=\"01\">JAN</option>");
    }
    if (thisMonth == 2) {
       out.println("<option selected value=\"02\">FEB</option>");
    } else {
       out.println("<option value=\"02\">FEB</option>");
    }
    if (thisMonth == 3) {
       out.println("<option selected value=\"03\">MAR</option>");
    } else {
       out.println("<option value=\"03\">MAR</option>");
    }
    if (thisMonth == 4) {
       out.println("<option selected value=\"04\">APR</option>");
    } else {
       out.println("<option value=\"04\">APR</option>");
    }
    if (thisMonth == 5) {
       out.println("<option selected value=\"05\">MAY</option>");
    } else {
       out.println("<option value=\"05\">MAY</option>");
    }
    if (thisMonth == 6) {
       out.println("<option selected value=\"06\">JUN</option>");
    } else {
       out.println("<option value=\"06\">JUN</option>");
    }
    if (thisMonth == 7) {
       out.println("<option selected value=\"07\">JUL</option>");
    } else {
       out.println("<option value=\"07\">JUL</option>");
    }
    if (thisMonth == 8) {
       out.println("<option selected value=\"08\">AUG</option>");
    } else {
       out.println("<option value=\"08\">AUG</option>");
    }
    if (thisMonth == 9) {
       out.println("<option selected value=\"09\">SEP</option>");
    } else {
       out.println("<option value=\"09\">SEP</option>");
    }
    if (thisMonth == 10) {
       out.println("<option selected value=\"10\">OCT</option>");
    } else {
       out.println("<option value=\"10\">OCT</option>");
    }
    if (thisMonth == 11) {
       out.println("<option selected value=\"11\">NOV</option>");
    } else {
       out.println("<option value=\"11\">NOV</option>");
    }
    if (thisMonth == 12) {
       out.println("<option selected value=\"12\">DEC</option>");
    } else {
       out.println("<option value=\"12\">DEC</option>");
    }

 }


/**
 //************************************************************************
 //
 //  Output the day options
 //
 //************************************************************************
 **/

 public static void opDay(PrintWriter out, int thisDay) {


    if (thisDay == 1) {                                                     // start with today
       out.println("<option selected selected value=\"01\">1</option>");
    } else {
       out.println("<option value=\"01\">1</option>");
    }
    if (thisDay == 2) {
       out.println("<option selected value=\"02\">2</option>");
    } else {
       out.println("<option value=\"02\">2</option>");
    }
    if (thisDay == 3) {
       out.println("<option selected value=\"03\">3</option>");
    } else {
       out.println("<option value=\"03\">3</option>");
    }
    if (thisDay == 4) {
       out.println("<option selected value=\"04\">4</option>");
    } else {
       out.println("<option value=\"04\">4</option>");
    }
    if (thisDay == 5) {
       out.println("<option selected value=\"05\">5</option>");
    } else {
       out.println("<option value=\"05\">5</option>");
    }
    if (thisDay == 6) {
       out.println("<option selected value=\"06\">6</option>");
    } else {
       out.println("<option value=\"06\">6</option>");
    }
    if (thisDay == 7) {
       out.println("<option selected value=\"07\">7</option>");
    } else {
       out.println("<option value=\"07\">7</option>");
    }
    if (thisDay == 8) {
       out.println("<option selected value=\"08\">8</option>");
    } else {
       out.println("<option value=\"08\">8</option>");
    }
    if (thisDay == 9) {
       out.println("<option selected value=\"09\">9</option>");
    } else {
       out.println("<option value=\"09\">9</option>");
    }
    if (thisDay == 10) {
       out.println("<option selected value=\"10\">10</option>");
    } else {
       out.println("<option value=\"10\">10</option>");
    }
    if (thisDay == 11) {
       out.println("<option selected value=\"11\">11</option>");
    } else {
       out.println("<option value=\"11\">11</option>");
    }
    if (thisDay == 12) {
       out.println("<option selected value=\"12\">12</option>");
    } else {
       out.println("<option value=\"12\">12</option>");
    }
    if (thisDay == 13) {
       out.println("<option selected value=\"13\">13</option>");
    } else {
       out.println("<option value=\"13\">13</option>");
    }
    if (thisDay == 14) {
       out.println("<option selected value=\"14\">14</option>");
    } else {
       out.println("<option value=\"14\">14</option>");
    }
    if (thisDay == 15) {
       out.println("<option selected value=\"15\">15</option>");
    } else {
       out.println("<option value=\"15\">15</option>");
    }
    if (thisDay == 16) {
       out.println("<option selected value=\"16\">16</option>");
    } else {
       out.println("<option value=\"16\">16</option>");
    }
    if (thisDay == 17) {
       out.println("<option selected value=\"17\">17</option>");
    } else {
       out.println("<option value=\"17\">17</option>");
    }
    if (thisDay == 18) {
       out.println("<option selected value=\"18\">18</option>");
    } else {
       out.println("<option value=\"18\">18</option>");
    }
    if (thisDay == 19) {
       out.println("<option selected value=\"19\">19</option>");
    } else {
       out.println("<option value=\"19\">19</option>");
    }
    if (thisDay == 20) {
       out.println("<option selected value=\"20\">20</option>");
    } else {
       out.println("<option value=\"20\">20</option>");
    }
    if (thisDay == 21) {
       out.println("<option selected value=\"21\">21</option>");
    } else {
       out.println("<option value=\"21\">21</option>");
    }
    if (thisDay == 22) {
       out.println("<option selected value=\"22\">22</option>");
    } else {
       out.println("<option value=\"22\">22</option>");
    }
    if (thisDay == 23) {
       out.println("<option selected value=\"23\">23</option>");
    } else {
       out.println("<option value=\"23\">23</option>");
    }
    if (thisDay == 24) {
       out.println("<option selected value=\"24\">24</option>");
    } else {
       out.println("<option value=\"24\">24</option>");
    }
    if (thisDay == 25) {
       out.println("<option selected value=\"25\">25</option>");
    } else {
       out.println("<option value=\"25\">25</option>");
    }
    if (thisDay == 26) {
       out.println("<option selected value=\"26\">26</option>");
    } else {
       out.println("<option value=\"26\">26</option>");
    }
    if (thisDay == 27) {
       out.println("<option selected value=\"27\">27</option>");
    } else {
       out.println("<option value=\"27\">27</option>");
    }
    if (thisDay == 28) {
       out.println("<option selected value=\"28\">28</option>");
    } else {
       out.println("<option value=\"28\">28</option>");
    }
    if (thisDay == 29) {
       out.println("<option selected value=\"29\">29</option>");
    } else {
       out.println("<option value=\"29\">29</option>");
    }
    if (thisDay == 30) {
       out.println("<option selected value=\"30\">30</option>");
    } else {
       out.println("<option value=\"30\">30</option>");
    }
    if (thisDay == 31) {
       out.println("<option selected value=\"31\">31</option>");
    } else {
       out.println("<option value=\"31\">31</option>");
    }

 }


/**
 //************************************************************************
 //
 //  Output the year options
 //
 //************************************************************************
 **/

 public static void opYear(PrintWriter out, int thisYear) {


    out.println("<option value=\"" +thisYear+ "\">" +thisYear+ "</option>");
    thisYear++;    
    out.println("<option value=\"" +thisYear+ "\">" +thisYear+ "</option>");
    thisYear++;    
    out.println("<option value=\"" +thisYear+ "\">" +thisYear+ "</option>");
    thisYear++;    
    out.println("<option value=\"" +thisYear+ "\">" +thisYear+ "</option>");
    thisYear++;    
    out.println("<option value=\"" +thisYear+ "\">" +thisYear+ "</option>");
    thisYear++;    
    out.println("<option value=\"" +thisYear+ "\">" +thisYear+ "</option>");

 }


 // *********************************************************
 //
 //  Output the color selection list.
 //
 //
 //     Called by:  Proshop_addevnt
 //                 Proshop_addfives
 //                 Proshop_addgrest
 //                 Proshop_addlottery
 //                 Proshop_addmrest
 //                 Proshop_events
 //                 Proshop_fives
 //                 Proshop_grest
 //                 Proshop_lottery
 //                 Proshop_mrest
 //
 //
 // *********************************************************


 public static void displayColors(PrintWriter out) {


    displayColorsAll("", out);     // use default color
 }


 public static void displayColorsAll(String color, PrintWriter out) {


   out.println("<select size=\"1\" name=\"color\">");

   if (color.equals( "" ) || color.equals( "Default" )) {

      out.println("<option selected value=\"Default\">Default (none)</option>");

   } else {

      out.println("<option selected value=\"" + color + "\">" + color + "</option>");
   }

   out.println("<option value=\"Antiquewhite\">Antique White</option>");
   out.println("<option value=\"Aqua\">Aqua</option>");
   out.println("<option value=\"Aquamarine\">Aquamarine</option>");
   out.println("<option value=\"Beige\">Beige</option>");
   out.println("<option value=\"Bisque\">Bisque</option>");
   out.println("<option value=\"Blanchedalmond\">Blanched Almond</option>");
   out.println("<option value=\"Blue\">Blue</option>");
   out.println("<option value=\"Blueviolet\">Blueviolet</option>");
   out.println("<option value=\"Brown\">Brown</option>");
   out.println("<option value=\"Burlywood\">Burlywood</option>");
   out.println("<option value=\"Cadetblue\">Cadetblue</option>");
   out.println("<option value=\"Chartreuse\">Chartreuse</option>");
   out.println("<option value=\"Chocolate\">Chocolate</option>");
   out.println("<option value=\"Coral\">Coral</option>");
   out.println("<option value=\"Cornflowerblue\">Cornflowerblue</option>");
   out.println("<option value=\"Cornsilk\">Cornsilk</option>");
   out.println("<option value=\"Crimson\">Crimson</option>");
   out.println("<option value=\"Cyan\">Cyan</option>");
   out.println("<option value=\"Darkblue\">Darkblue</option>");
   out.println("<option value=\"Darkcyan\">Darkcyan</option>");
   out.println("<option value=\"Darkgoldenrod\">Darkgoldenrod</option>");
   out.println("<option value=\"Darkgray\">Darkgray</option>");
   out.println("<option value=\"Darkgreen\">Darkgreen</option>");
   out.println("<option value=\"Darkkhaki\">Darkkhaki</option>");
   out.println("<option value=\"Darkmagenta\">Darkmagenta</option>");
   out.println("<option value=\"Darkolivegreen\">Darkolivegreen</option>");
   out.println("<option value=\"Darkorange\">Darkorange</option>");
   out.println("<option value=\"Darkorchid\">Darkorchid</option>");
   out.println("<option value=\"Darkred\">Darkred</option>");
   out.println("<option value=\"Darksalmon\">Darksalmon</option>");
   out.println("<option value=\"Darkseagreen\">Darkseagreen</option>");
   out.println("<option value=\"Darkslateblue\">Darkslateblue</option>");
   out.println("<option value=\"Darkslategray\">Darkslategray</option>");
   out.println("<option value=\"Darkturquoise\">Darkturquoise</option>");
   out.println("<option value=\"Darkviolet\">Darkviolet</option>");
   out.println("<option value=\"Deeppink\">Deeppink</option>");
   out.println("<option value=\"Deepskyblue\">Deepskyblue</option>");
   out.println("<option value=\"Default\">Default (none)</option>");
   out.println("<option value=\"Dimgray\">Dimgray</option>");
   out.println("<option value=\"Dodgerblue\">Dodgerblue</option>");
   out.println("<option value=\"Firebrick\">Firebrick</option>");
   out.println("<option value=\"Forestgreen\">Forestgreen</option>");
   out.println("<option value=\"Fuchsia\">Fuchsia</option>");
   out.println("<option value=\"Gainsboro\">Gainsboro</option>");
   out.println("<option value=\"Gold\">Gold</option>");
   out.println("<option value=\"Goldenrod\">Goldenrod</option>");
   out.println("<option value=\"Gray\">Gray</option>");
   out.println("<option value=\"Green\">Green</option>");
   out.println("<option value=\"Greenyellow\">Greenyellow</option>");
   out.println("<option value=\"Hotpink\">Hotpink</option>");
   out.println("<option value=\"Indianred\">Indianred</option>");
   out.println("<option value=\"Indigo\">Indigo</option>");
   out.println("<option value=\"Ivory\">Ivory</option>");
   out.println("<option value=\"Khaki\">Khaki</option>");
   out.println("<option value=\"Lavender\">Lavender</option>");
   out.println("<option value=\"Lavenderblush\">Lavenderblush</option>");
   out.println("<option value=\"Lawngreen\">Lawngreen</option>");
   out.println("<option value=\"Lemonchiffon\">Lemonchiffon</option>");
   out.println("<option value=\"Lightblue\">Lightblue</option>");
   out.println("<option value=\"Lightcoral\">Lightcoral</option>");
   out.println("<option value=\"Lightgoldenrodyellow\">Lightgoldenrodyellow</option>");
   out.println("<option value=\"Lightgreen\">Lightgreen</option>");
   out.println("<option value=\"Lightgrey\">Lightgrey</option>");
   out.println("<option value=\"Lightpink\">Lightpink</option>");
   out.println("<option value=\"Lightsalmon\">Lightsalmon</option>");
   out.println("<option value=\"Lightseagreen\">Lightseagreen</option>");
   out.println("<option value=\"Lightskyblue\">Lightskyblue</option>");
   out.println("<option value=\"Lightslategray\">Lightslategray</option>");
   out.println("<option value=\"Lightsteelblue\">Lightsteelblue</option>");
   out.println("<option value=\"Lime\">Lime</option>");
   out.println("<option value=\"Limegreen\">Limegreen</option>");
   out.println("<option value=\"Linen\">Linen</option>");
   out.println("<option value=\"Magenta\">Magenta</option>");
   out.println("<option value=\"Mediumauqamarine\">Mediumauqamarine</option>");
   out.println("<option value=\"Mediumblue\">Mediumblue</option>");
   out.println("<option value=\"Mediumorchid\">Mediumorchid</option>");
   out.println("<option value=\"Mediumpurple\">Mediumpurple</option>");
   out.println("<option value=\"Mediumseagreen\">Mediumseagreen</option>");
   out.println("<option value=\"Mediumslateblue\">Mediumslateblue</option>");
   out.println("<option value=\"Mediumspringgreen\">Mediumspringgreen</option>");
   out.println("<option value=\"Mediumturquoise\">Mediumturquoise</option>");
   out.println("<option value=\"Mediumvioletred\">Mediumvioletred</option>");
   out.println("<option value=\"Mistyrose\">Mistyrose</option>");
   out.println("<option value=\"Moccasin\">Moccasin</option>");
   out.println("<option value=\"Navajowhite\">Navajowhite</option>");
   out.println("<option value=\"Navy\">Navy</option>");
   out.println("<option value=\"Oldlace\">Oldlace</option>");
   out.println("<option value=\"Olive\">Olive</option>");
   out.println("<option value=\"Olivedrab\">Olivedrab</option>");
   out.println("<option value=\"Orange\">Orange</option>");
   out.println("<option value=\"Orangered\">Orangered</option>");
   out.println("<option value=\"Orchid\">Orchid</option>");
   out.println("<option value=\"Palegoldenrod\">Palegoldenrod</option>");
   out.println("<option value=\"Palegreen\">Palegreen</option>");
   out.println("<option value=\"Paleturquoise\">Paleturquoise</option>");
   out.println("<option value=\"Palevioletred\">Palevioletred</option>");
   out.println("<option value=\"Papayawhip\">Papayawhip</option>");
   out.println("<option value=\"Peachpuff\">Peachpuff</option>");
   out.println("<option value=\"Peru\">Peru</option>");
   out.println("<option value=\"Pink\">Pink</option>");
   out.println("<option value=\"Plum\">Plum</option>");
   out.println("<option value=\"Powderblue\">Powderblue</option>");
   out.println("<option value=\"Purple\">Purple</option>");
   out.println("<option value=\"Red\">Red</option>");
   out.println("<option value=\"Rosybrown\">Rosybrown</option>");
   out.println("<option value=\"Royalblue\">Royalblue</option>");
   out.println("<option value=\"Saddlebrown\">Saddlebrown</option>");
   out.println("<option value=\"Salmon\">Salmon</option>");
   out.println("<option value=\"Sandybrown\">Sandybrown</option>");
   out.println("<option value=\"Seagreen\">Seagreen</option>");
   out.println("<option value=\"Sienna\">Sienna</option>");
   out.println("<option value=\"Silver\">Silver</option>");
   out.println("<option value=\"Skyblue\">Skyblue</option>");
   out.println("<option value=\"Slateblue\">Slateblue</option>");
   out.println("<option value=\"Slategray\">Slategray</option>");
   out.println("<option value=\"Springgreen\">Springgreen</option>");
   out.println("<option value=\"Steelblue\">Steelblue</option>");
   out.println("<option value=\"Tan\">Tan</option>");
   out.println("<option value=\"Teal\">Teal</option>");
   out.println("<option value=\"Thistle\">Thistle</option>");
   out.println("<option value=\"Tomato\">Tomato</option>");
   out.println("<option value=\"Turquoise\">Turquoise</option>");
   out.println("<option value=\"Violet\">Violet</option>");
   out.println("<option value=\"Wheat\">Wheat</option>");
   out.println("<option value=\"Yellow\">Yellow</option>");
   out.println("<option value=\"YellowGreen\">YellowGreen</option>");
   out.println("</select>");
 }


 // *********************************************************
 //
 //  Other common methods.
 //
 //    Called by:  Proshop_memNotice (others to follow)
 //
 // *********************************************************

 public static void displayTees(String fb, PrintWriter out) {

   out.println("&nbsp;&nbsp;Tees:&nbsp;&nbsp;");
     out.println("<select size=\"1\" name=\"fb\">");
     if (fb.equals( "Both" )) {
        out.println("<option selected value=\"Both\">Both</option>");
     } else {
        out.println("<option value=\"Both\">Both</option>");
     }
     if (fb.equals( "Front" )) {
        out.println("<option selected value=\"Front\">Front</option>");
     } else {
        out.println("<option value=\"Front\">Front</option>");
     }
     if (fb.equals( "Back" )) {
        out.println("<option selected value=\"Back\">Back</option>");
     } else {
        out.println("<option value=\"Back\">Back</option>");
     }
   out.println("</select><br><br>");

 }


 public static void displayStartDate(long s_month, long s_day, long s_year, PrintWriter out) {

   int year = 0;
   int i = 0;
   int i2 = 0;
      
   //
   //  Get the current year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   year = cal.get(Calendar.YEAR);
  
   i2 = year - 2000;                 // get index (year after 2000)
   i2 += 3;                          // i2 = last year to list (curent year + 3)
  
   out.println("&nbsp;&nbsp;Start Date:&nbsp;&nbsp;&nbsp;");
     out.println("Month:&nbsp;&nbsp;");
     out.println("<select size=\"1\" name=\"smonth\">");
    if (s_month == 1) {
       out.println("<option selected value=\"01\">JAN</option>");
    } else {
       out.println("<option value=\"01\">JAN</option>");
    }
    if (s_month == 2) {
       out.println("<option selected value=\"02\">FEB</option>");
    } else {
       out.println("<option value=\"02\">FEB</option>");
    }
    if (s_month == 3) {
       out.println("<option selected value=\"03\">MAR</option>");
    } else {
       out.println("<option value=\"03\">MAR</option>");
    }
    if (s_month == 4) {
       out.println("<option selected value=\"04\">APR</option>");
    } else {
       out.println("<option value=\"04\">APR</option>");
    }
    if (s_month == 5) {
       out.println("<option selected value=\"05\">MAY</option>");
    } else {
       out.println("<option value=\"05\">MAY</option>");
    }
    if (s_month == 6) {
       out.println("<option selected value=\"06\">JUN</option>");
    } else {
       out.println("<option value=\"06\">JUN</option>");
    }
    if (s_month == 7) {
       out.println("<option selected value=\"07\">JUL</option>");
    } else {
       out.println("<option value=\"07\">JUL</option>");
    }
    if (s_month == 8) {
       out.println("<option selected value=\"08\">AUG</option>");
    } else {
       out.println("<option value=\"08\">AUG</option>");
    }
    if (s_month == 9) {
       out.println("<option selected value=\"09\">SEP</option>");
    } else {
       out.println("<option value=\"09\">SEP</option>");
    }
    if (s_month == 10) {
       out.println("<option selected value=\"10\">OCT</option>");
    } else {
       out.println("<option value=\"10\">OCT</option>");
    }
    if (s_month == 11) {
       out.println("<option selected value=\"11\">NOV</option>");
    } else {
       out.println("<option value=\"11\">NOV</option>");
    }
    if (s_month == 12) {
       out.println("<option selected value=\"12\">DEC</option>");
    } else {
       out.println("<option value=\"12\">DEC</option>");
    }
     out.println("</select>");

     out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
     out.println("<select size=\"1\" name=\"sday\">");
    if (s_day == 1) {
       out.println("<option selected selected value=\"01\">1</option>");
    } else {
       out.println("<option value=\"01\">1</option>");
    }
    if (s_day == 2) {
       out.println("<option selected value=\"02\">2</option>");
    } else {
       out.println("<option value=\"02\">2</option>");
    }
    if (s_day == 3) {
       out.println("<option selected value=\"03\">3</option>");
    } else {
       out.println("<option value=\"03\">3</option>");
    }
    if (s_day == 4) {
       out.println("<option selected value=\"04\">4</option>");
    } else {
       out.println("<option value=\"04\">4</option>");
    }
    if (s_day == 5) {
       out.println("<option selected value=\"05\">5</option>");
    } else {
       out.println("<option value=\"05\">5</option>");
    }
    if (s_day == 6) {
       out.println("<option selected value=\"06\">6</option>");
    } else {
       out.println("<option value=\"06\">6</option>");
    }
    if (s_day == 7) {
       out.println("<option selected value=\"07\">7</option>");
    } else {
       out.println("<option value=\"07\">7</option>");
    }
    if (s_day == 8) {
       out.println("<option selected value=\"08\">8</option>");
    } else {
       out.println("<option value=\"08\">8</option>");
    }
    if (s_day == 9) {
       out.println("<option selected value=\"09\">9</option>");
    } else {
       out.println("<option value=\"09\">9</option>");
    }
    if (s_day == 10) {
       out.println("<option selected value=\"10\">10</option>");
    } else {
       out.println("<option value=\"10\">10</option>");
    }
    if (s_day == 11) {
       out.println("<option selected value=\"11\">11</option>");
    } else {
       out.println("<option value=\"11\">11</option>");
    }
    if (s_day == 12) {
       out.println("<option selected value=\"12\">12</option>");
    } else {
       out.println("<option value=\"12\">12</option>");
    }
    if (s_day == 13) {
       out.println("<option selected value=\"13\">13</option>");
    } else {
       out.println("<option value=\"13\">13</option>");
    }
    if (s_day == 14) {
       out.println("<option selected value=\"14\">14</option>");
    } else {
       out.println("<option value=\"14\">14</option>");
    }
    if (s_day == 15) {
       out.println("<option selected value=\"15\">15</option>");
    } else {
       out.println("<option value=\"15\">15</option>");
    }
    if (s_day == 16) {
       out.println("<option selected value=\"16\">16</option>");
    } else {
       out.println("<option value=\"16\">16</option>");
    }
    if (s_day == 17) {
       out.println("<option selected value=\"17\">17</option>");
    } else {
       out.println("<option value=\"17\">17</option>");
    }
    if (s_day == 18) {
       out.println("<option selected value=\"18\">18</option>");
    } else {
       out.println("<option value=\"18\">18</option>");
    }
    if (s_day == 19) {
       out.println("<option selected value=\"19\">19</option>");
    } else {
       out.println("<option value=\"19\">19</option>");
    }
    if (s_day == 20) {
       out.println("<option selected value=\"20\">20</option>");
    } else {
       out.println("<option value=\"20\">20</option>");
    }
    if (s_day == 21) {
       out.println("<option selected value=\"21\">21</option>");
    } else {
       out.println("<option value=\"21\">21</option>");
    }
    if (s_day == 22) {
       out.println("<option selected value=\"22\">22</option>");
    } else {
       out.println("<option value=\"22\">22</option>");
    }
    if (s_day == 23) {
       out.println("<option selected value=\"23\">23</option>");
    } else {
       out.println("<option value=\"23\">23</option>");
    }
    if (s_day == 24) {
       out.println("<option selected value=\"24\">24</option>");
    } else {
       out.println("<option value=\"24\">24</option>");
    }
    if (s_day == 25) {
       out.println("<option selected value=\"25\">25</option>");
    } else {
       out.println("<option value=\"25\">25</option>");
    }
    if (s_day == 26) {
       out.println("<option selected value=\"26\">26</option>");
    } else {
       out.println("<option value=\"26\">26</option>");
    }
    if (s_day == 27) {
       out.println("<option selected value=\"27\">27</option>");
    } else {
       out.println("<option value=\"27\">27</option>");
    }
    if (s_day == 28) {
       out.println("<option selected value=\"28\">28</option>");
    } else {
       out.println("<option value=\"28\">28</option>");
    }
    if (s_day == 29) {
       out.println("<option selected value=\"29\">29</option>");
    } else {
       out.println("<option value=\"29\">29</option>");
    }
    if (s_day == 30) {
       out.println("<option selected value=\"30\">30</option>");
    } else {
       out.println("<option value=\"30\">30</option>");
    }
    if (s_day == 31) {
       out.println("<option selected value=\"31\">31</option>");
    } else {
       out.println("<option value=\"31\">31</option>");
    }
    out.println("</select>");


    out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"syear\">");
      
    for (i=2; i<i2; i++) {    // start checking 2002 and go to 'current year + 3'
      
       if (s_year == (2000 + i)) {
          out.println("<option selected value=\"" + (2000 + i) + "\">" + (2000 + i) + "</option>");
       } else {
          if (year <= (2000 + i)) {      // if this year or later
             out.println("<option value=\"" + (2000 + i) + "\">" + (2000 + i) + "</option>");
          }
       }
    }
      
    out.println("</select><br><br>");

 }


 public static void displayEndDate(long e_month, long e_day, long e_year, PrintWriter out) {

   int year = 0;
   int i = 0;
   int i2 = 0;

   //
   //  Get the current year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   year = cal.get(Calendar.YEAR);

   i2 = year - 2000;                 // get index (year after 2000)
   i2 += 10;                         // i2 = last year to list (curent year + 10)

   out.println("&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("Month:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"emonth\">");
    if (e_month == 1) {
       out.println("<option selected value=\"01\">JAN</option>");
    } else {
       out.println("<option value=\"01\">JAN</option>");
    }
    if (e_month == 2) {
       out.println("<option selected value=\"02\">FEB</option>");
    } else {
       out.println("<option value=\"02\">FEB</option>");
    }
    if (e_month == 3) {
       out.println("<option selected value=\"03\">MAR</option>");
    } else {
       out.println("<option value=\"03\">MAR</option>");
    }
    if (e_month == 4) {
       out.println("<option selected value=\"04\">APR</option>");
    } else {
       out.println("<option value=\"04\">APR</option>");
    }
    if (e_month == 5) {
       out.println("<option selected value=\"05\">MAY</option>");
    } else {
       out.println("<option value=\"05\">MAY</option>");
    }
    if (e_month == 6) {
       out.println("<option selected value=\"06\">JUN</option>");
    } else {
       out.println("<option value=\"06\">JUN</option>");
    }
    if (e_month == 7) {
       out.println("<option selected value=\"07\">JUL</option>");
    } else {
       out.println("<option value=\"07\">JUL</option>");
    }
    if (e_month == 8) {
       out.println("<option selected value=\"08\">AUG</option>");
    } else {
       out.println("<option value=\"08\">AUG</option>");
    }
    if (e_month == 9) {
       out.println("<option selected value=\"09\">SEP</option>");
    } else {
       out.println("<option value=\"09\">SEP</option>");
    }
    if (e_month == 10) {
       out.println("<option selected value=\"10\">OCT</option>");
    } else {
       out.println("<option value=\"10\">OCT</option>");
    }
    if (e_month == 11) {
       out.println("<option selected value=\"11\">NOV</option>");
    } else {
       out.println("<option value=\"11\">NOV</option>");
    }
    if (e_month == 12) {
       out.println("<option selected value=\"12\">DEC</option>");
    } else {
       out.println("<option value=\"12\">DEC</option>");
    }
    out.println("</select>");

    out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"eday\">");
    if (e_day == 1) {
       out.println("<option selected selected value=\"01\">1</option>");
    } else {
       out.println("<option value=\"01\">1</option>");
    }
    if (e_day == 2) {
       out.println("<option selected value=\"02\">2</option>");
    } else {
       out.println("<option value=\"02\">2</option>");
    }
    if (e_day == 3) {
       out.println("<option selected value=\"03\">3</option>");
    } else {
       out.println("<option value=\"03\">3</option>");
    }
    if (e_day == 4) {
       out.println("<option selected value=\"04\">4</option>");
    } else {
       out.println("<option value=\"04\">4</option>");
    }
    if (e_day == 5) {
       out.println("<option selected value=\"05\">5</option>");
    } else {
       out.println("<option value=\"05\">5</option>");
    }
    if (e_day == 6) {
       out.println("<option selected value=\"06\">6</option>");
    } else {
       out.println("<option value=\"06\">6</option>");
    }
    if (e_day == 7) {
       out.println("<option selected value=\"07\">7</option>");
    } else {
       out.println("<option value=\"07\">7</option>");
    }
    if (e_day == 8) {
       out.println("<option selected value=\"08\">8</option>");
    } else {
       out.println("<option value=\"08\">8</option>");
    }
    if (e_day == 9) {
       out.println("<option selected value=\"09\">9</option>");
    } else {
       out.println("<option value=\"09\">9</option>");
    }
    if (e_day == 10) {
       out.println("<option selected value=\"10\">10</option>");
    } else {
       out.println("<option value=\"10\">10</option>");
    }
    if (e_day == 11) {
       out.println("<option selected value=\"11\">11</option>");
    } else {
       out.println("<option value=\"11\">11</option>");
    }
    if (e_day == 12) {
       out.println("<option selected value=\"12\">12</option>");
    } else {
       out.println("<option value=\"12\">12</option>");
    }
    if (e_day == 13) {
       out.println("<option selected value=\"13\">13</option>");
    } else {
       out.println("<option value=\"13\">13</option>");
    }
    if (e_day == 14) {
       out.println("<option selected value=\"14\">14</option>");
    } else {
       out.println("<option value=\"14\">14</option>");
    }
    if (e_day == 15) {
       out.println("<option selected value=\"15\">15</option>");
    } else {
       out.println("<option value=\"15\">15</option>");
    }
    if (e_day == 16) {
       out.println("<option selected value=\"16\">16</option>");
    } else {
       out.println("<option value=\"16\">16</option>");
    }
    if (e_day == 17) {
       out.println("<option selected value=\"17\">17</option>");
    } else {
       out.println("<option value=\"17\">17</option>");
    }
    if (e_day == 18) {
       out.println("<option selected value=\"18\">18</option>");
    } else {
       out.println("<option value=\"18\">18</option>");
    }
    if (e_day == 19) {
       out.println("<option selected value=\"19\">19</option>");
    } else {
       out.println("<option value=\"19\">19</option>");
    }
    if (e_day == 20) {
       out.println("<option selected value=\"20\">20</option>");
    } else {
       out.println("<option value=\"20\">20</option>");
    }
    if (e_day == 21) {
       out.println("<option selected value=\"21\">21</option>");
    } else {
       out.println("<option value=\"21\">21</option>");
    }
    if (e_day == 22) {
       out.println("<option selected value=\"22\">22</option>");
    } else {
       out.println("<option value=\"22\">22</option>");
    }
    if (e_day == 23) {
       out.println("<option selected value=\"23\">23</option>");
    } else {
       out.println("<option value=\"23\">23</option>");
    }
    if (e_day == 24) {
       out.println("<option selected value=\"24\">24</option>");
    } else {
       out.println("<option value=\"24\">24</option>");
    }
    if (e_day == 25) {
       out.println("<option selected value=\"25\">25</option>");
    } else {
       out.println("<option value=\"25\">25</option>");
    }
    if (e_day == 26) {
       out.println("<option selected value=\"26\">26</option>");
    } else {
       out.println("<option value=\"26\">26</option>");
    }
    if (e_day == 27) {
       out.println("<option selected value=\"27\">27</option>");
    } else {
       out.println("<option value=\"27\">27</option>");
    }
    if (e_day == 28) {
       out.println("<option selected value=\"28\">28</option>");
    } else {
       out.println("<option value=\"28\">28</option>");
    }
    if (e_day == 29) {
       out.println("<option selected value=\"29\">29</option>");
    } else {
       out.println("<option value=\"29\">29</option>");
    }
    if (e_day == 30) {
       out.println("<option selected value=\"30\">30</option>");
    } else {
       out.println("<option value=\"30\">30</option>");
    }
    if (e_day == 31) {
       out.println("<option selected value=\"31\">31</option>");
    } else {
       out.println("<option value=\"31\">31</option>");
    }
    out.println("</select>");

    out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"eyear\">");

    for (i=2; i<i2; i++) {    // start checking 2002 and go to 'current year + 10'

       if (e_year == (2000 + i)) {
          out.println("<option selected value=\"" + e_year + "\">" + e_year + "</option>");
       } else {
          if (year <= (2000 + i)) {      // if this year or later
             out.println("<option value=\"" + (2000 + i) + "\">" + (2000 + i) + "</option>");
          }
       }
    }

    out.println("</select><br><br>");

 }


 public static void displayStartTime(int shr, int smin, String ssampm, PrintWriter out) {

   out.println("&nbsp;&nbsp;Start Time:");
   out.println("&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
     out.println("<select size=\"1\" name=\"start_hr\">");
    if (shr == 1) {
       out.println("<option selected selected value=\"01\">1</option>");
    } else {
       out.println("<option value=\"01\">1</option>");
    }
    if (shr == 2) {
       out.println("<option selected value=\"02\">2</option>");
    } else {
       out.println("<option value=\"02\">2</option>");
    }
    if (shr == 3) {
       out.println("<option selected value=\"03\">3</option>");
    } else {
       out.println("<option value=\"03\">3</option>");
    }
    if (shr == 4) {
       out.println("<option selected value=\"04\">4</option>");
    } else {
       out.println("<option value=\"04\">4</option>");
    }
    if (shr == 5) {
       out.println("<option selected value=\"05\">5</option>");
    } else {
       out.println("<option value=\"05\">5</option>");
    }
    if (shr == 6) {
       out.println("<option selected value=\"06\">6</option>");
    } else {
       out.println("<option value=\"06\">6</option>");
    }
    if (shr == 7) {
       out.println("<option selected value=\"07\">7</option>");
    } else {
       out.println("<option value=\"07\">7</option>");
    }
    if (shr == 8) {
       out.println("<option selected value=\"08\">8</option>");
    } else {
       out.println("<option value=\"08\">8</option>");
    }
    if (shr == 9) {
       out.println("<option selected value=\"09\">9</option>");
    } else {
       out.println("<option value=\"09\">9</option>");
    }
    if (shr == 10) {
       out.println("<option selected value=\"10\">10</option>");
    } else {
       out.println("<option value=\"10\">10</option>");
    }
    if (shr == 11) {
       out.println("<option selected value=\"11\">11</option>");
    } else {
       out.println("<option value=\"11\">11</option>");
    }
    if (shr == 12) {
       out.println("<option selected value=\"12\">12</option>");
    } else {
       out.println("<option value=\"12\">12</option>");
    }
     out.println("</select>");
     out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
     if (smin < 10) {
        out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + smin + " name=\"start_min\">");
     } else {
        out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + smin + " name=\"start_min\">");
     }
     out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
     out.println("<select size=\"1\" name=\"start_ampm\">");
    if (ssampm.equals( "AM" )) {
       out.println("<option selected value=\"00\">AM</option>");
    } else {
       out.println("<option value=\"00\">AM</option>");
    }
    if (ssampm.equals( "PM" )) {
       out.println("<option selected value=\"12\">PM</option>");
    } else {
       out.println("<option value=\"12\">PM</option>");
    }
     out.println("</select><br><br>");
 }


 public static void displayEndTime(int ehr, int emin, String seampm, PrintWriter out) {

   out.println("&nbsp;&nbsp;End Time:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
     out.println("<select size=\"1\" name=\"end_hr\">");
    if (ehr == 1) {
       out.println("<option selected selected value=\"01\">1</option>");
    } else {
       out.println("<option value=\"01\">1</option>");
    }
    if (ehr == 2) {
       out.println("<option selected value=\"02\">2</option>");
    } else {
       out.println("<option value=\"02\">2</option>");
    }
    if (ehr == 3) {
       out.println("<option selected value=\"03\">3</option>");
    } else {
       out.println("<option value=\"03\">3</option>");
    }
    if (ehr == 4) {
       out.println("<option selected value=\"04\">4</option>");
    } else {
       out.println("<option value=\"04\">4</option>");
    }
    if (ehr == 5) {
       out.println("<option selected value=\"05\">5</option>");
    } else {
       out.println("<option value=\"05\">5</option>");
    }
    if (ehr == 6) {
       out.println("<option selected value=\"06\">6</option>");
    } else {
       out.println("<option value=\"06\">6</option>");
    }
    if (ehr == 7) {
       out.println("<option selected value=\"07\">7</option>");
    } else {
       out.println("<option value=\"07\">7</option>");
    }
    if (ehr == 8) {
       out.println("<option selected value=\"08\">8</option>");
    } else {
       out.println("<option value=\"08\">8</option>");
    }
    if (ehr == 9) {
       out.println("<option selected value=\"09\">9</option>");
    } else {
       out.println("<option value=\"09\">9</option>");
    }
    if (ehr == 10) {
       out.println("<option selected value=\"10\">10</option>");
    } else {
       out.println("<option value=\"10\">10</option>");
    }
    if (ehr == 11) {
       out.println("<option selected value=\"11\">11</option>");
    } else {
       out.println("<option value=\"11\">11</option>");
    }
    if (ehr == 12) {
       out.println("<option selected value=\"12\">12</option>");
    } else {
       out.println("<option value=\"12\">12</option>");
    }
     out.println("</select>");
     out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
     if (emin < 10) {
        out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + emin + " name=\"end_min\">");
     } else {
        out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + emin + " name=\"end_min\">");
     }
     out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
     out.println("<select size=\"1\" name=\"end_ampm\">");
    if (seampm.equals( "AM" )) {
       out.println("<option selected value=\"00\">AM</option>");
    } else {
       out.println("<option value=\"00\">AM</option>");
    }
    if (seampm.equals( "PM" )) {
       out.println("<option selected value=\"12\">PM</option>");
    } else {
       out.println("<option value=\"12\">PM</option>");
    }
     out.println("</select><br><br>");
 }


 public static void displayRecurr(int mon, int tue, int wed, int thu, int fri, int sat, int sun, PrintWriter out) {

   out.println("&nbsp;&nbsp;Recurrence (select all that apply):");
     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     if (mon == 1) {
        out.println("<input type=\"checkbox\" name=\"mon\" checked value=\"1\">&nbsp;&nbsp;Every Monday");
     } else {
        out.println("<input type=\"checkbox\" name=\"mon\" value=\"1\">&nbsp;&nbsp;Every Monday");
     }
     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     if (tue == 1) {
        out.println("<input type=\"checkbox\" name=\"tue\" checked value=\"1\">&nbsp;&nbsp;Every Tuesday");
     } else {
        out.println("<input type=\"checkbox\" name=\"tue\" value=\"1\">&nbsp;&nbsp;Every Tuesday");
     }
     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     if (wed == 1) {
        out.println("<input type=\"checkbox\" name=\"wed\" checked value=\"1\">&nbsp;&nbsp;Every Wednesday");
     } else {
        out.println("<input type=\"checkbox\" name=\"wed\" value=\"1\">&nbsp;&nbsp;Every Wednesday");
     }
     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     if (thu == 1) {
        out.println("<input type=\"checkbox\" name=\"thu\" checked value=\"1\">&nbsp;&nbsp;Every Thursday");
     } else {
        out.println("<input type=\"checkbox\" name=\"thu\" value=\"1\">&nbsp;&nbsp;Every Thursday");
     }
     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     if (fri == 1) {
        out.println("<input type=\"checkbox\" name=\"fri\" checked value=\"1\">&nbsp;&nbsp;Every Friday");
     } else {
        out.println("<input type=\"checkbox\" name=\"fri\" value=\"1\">&nbsp;&nbsp;Every Friday");
     }
     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     if (sat == 1) {
        out.println("<input type=\"checkbox\" name=\"sat\" checked value=\"1\">&nbsp;&nbsp;Every Saturday");
     } else {
        out.println("<input type=\"checkbox\" name=\"sat\" value=\"1\">&nbsp;&nbsp;Every Saturday");
     }
     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     if (sun == 1) {
        out.println("<input type=\"checkbox\" name=\"sun\" checked value=\"1\">&nbsp;&nbsp;Every Sunday");
     } else {
        out.println("<input type=\"checkbox\" name=\"sun\" value=\"1\">&nbsp;&nbsp;Every Sunday");
     }
   out.println("<br><br>");
 }

}  // end of dateOpts class
