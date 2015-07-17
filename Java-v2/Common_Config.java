/***************************************************************************************
 *   Common_Config:  This servlet will process common Config functions for the Proshop side.
 *
 *
 *
 *   created: 10/11/2006   Bob P.
 *
 *   last updated:       ******* keep this accurate *******
 *
 *     4/01/14  Updated displayActivitySelectSheet to also use the sort_by field for ordering activities.
 *    10/22/13  Added makePrivateHelp method to display help information for the Make Tee Time Private option.
 *     6/28/13  Updated displayActivitySheetSelect() method to include label tags to make the checkboxes easier to select.
 *    11/03/12  Updated displayActivitySheetSelect() method to also print the name of each court's parent activity, if the parent is not the root activity. 
 *              Courts will now be ordered by parent_id and then activity_name.
 *    12/22/11  Updated buildEventCategoryOptions to display a note indicating where Event Categories can be set up if none were found.
 *    12/20/11  Added printEventCategoryHiddenInputs for easy print out of hidden category selections for passing from servlet to servlet (case 2076).
 *    12/20/11  Added buildEventCategoryOptions method to build select options of category names for the given activity_id (case 2076).
 *     6/27/11  Add new methods for displayStartDate and displayEndDate to allow user to specify if they want to allow future years.
 *    10/14/10  Add override method for displayActivitySheetSelect so we can pass a csv of allowable locations to select from
 *     5/10/10  Add buildReportCals method to provide a common method to build custom date range calendars for reports, etc.
 *    12/14/09  Updated displayStartDate & DisplayEndDate to allow displaying of previous years
 *    11/02/09  Addded displayActivitySheetSelect() method to display a checkbox-list of all relevant activity_sheets and
 *              buildLocationsString method to form a CSV string from a submitted form that used displayActivitySheetSelect()
 *     6/16/08  Modify displayHrMinToD to handle incoming 24hr hour parts
 *     4/20/08  Added displayHrMinToD for building generic time related elements for forms  
 *     2/23/07  Add several methods for mem-notice config (to be shared by others later).
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// ForeTees imports
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;


public class Common_Config {



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

     displayStartDate("&nbsp;&nbsp;Start Date:&nbsp;&nbsp;&nbsp;", s_month, s_day, s_year, false, true, out);

 }

 public static void displayStartDate(long s_month, long s_day, long s_year, boolean incPrevYears, PrintWriter out) {
     
     displayStartDate("&nbsp;&nbsp;Start Date:&nbsp;&nbsp;&nbsp;", s_month, s_day, s_year, incPrevYears, true, out);
     
 }
 
 public static void displayStartDate(long s_month, long s_day, long s_year, boolean incPrevYears, boolean incFutureYears, PrintWriter out) {
     
     displayStartDate("&nbsp;&nbsp;Start Date:&nbsp;&nbsp;&nbsp;", s_month, s_day, s_year, incPrevYears, incFutureYears, out);
     
 }
 
 public static void displayStartDate(String label, long s_month, long s_day, long s_year, boolean incPrevYears, PrintWriter out) {

     displayStartDate(label, s_month, s_day, s_year, incPrevYears, true, out);
     
 }

 public static void displayStartDate(String label, long s_month, long s_day, long s_year, boolean incPrevYears, boolean incFutureYears, PrintWriter out) {


   out.println(label); // "&nbsp;&nbsp;Start Date:&nbsp;&nbsp;&nbsp;"
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

    //
    //  Get the current year
    //
    Calendar cal = new GregorianCalendar();
    int year = cal.get(Calendar.YEAR);

    for (int y = 2002; y < (year + 3); y++) {   // start checking 2002 and go to 'current year + 3'

       if (s_year == y) {                       // first check if display year is the selected year

           out.println("<option selected value=\"" + y + "\">" + y + "</option>");

       } else {

          if (incFutureYears == true || y <= year) {      // if including future years OR still on past/present years

              if (incPrevYears && y < year) {      // if including previous years

                  out.println("<option value=\"" + y + "\">" + y + "</option>");

              } else if (year <= y) {              // if this year or future year

                  out.println("<option value=\"" + y + "\">" + y + "</option>");

              }
          }
       }

    }

    out.println("</select><br><br>");

 }


 public static void displayEndDate(long e_month, long e_day, long e_year, PrintWriter out) {

     displayEndDate(e_month, e_day, e_year, false, true, out);

 }


 public static void displayEndDate(long e_month, long e_day, long e_year, boolean incPrevYears, PrintWriter out) {

     displayEndDate(e_month, e_day, e_year, incPrevYears, true, out);

 }


 public static void displayEndDate(long e_month, long e_day, long e_year, boolean incPrevYears, boolean incFutureYears, PrintWriter out) {


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

    //
    //  Get the current year
    //
    Calendar cal = new GregorianCalendar();
    int year = cal.get(Calendar.YEAR);

    for (int y = 2002; y < (year + 6); y++) {   // start checking 2002 and go to 'current year + 6'

       if (e_year == y) {                       // first check if display year is the selected year

           out.println("<option selected value=\"" + y + "\">" + y + "</option>");

       } else {

          if (incFutureYears == true || y <= year) {      // if including future years OR still on past/present years

              if (incPrevYears && y < year) {      // if including previous years

                  out.println("<option value=\"" + y + "\">" + y + "</option>");

              } else if (year <= y) {              // if this year or future year

                out.println("<option value=\"" + y + "\">" + y + "</option>");

              }
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
    if (shr == 12 || shr == 0) {
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
    if (ehr == 12 || ehr == 0) {
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


 //
 // Common method for building HTML option tags using strings
 //
 public static void buildOption(String value, String display, String selected, PrintWriter out) {
     
     out.print("<option value=\""+ value +"\"");
     if (value.equalsIgnoreCase(selected)) out.print(" selected");
     out.print(">" + display +"</option>");
     
 }
 
 
 //
 // Common method for building HTML option tags using integers
 //
 public static void buildOption(int value, int display, int selected, PrintWriter out) {
     
     out.print("<option value=\""+ value +"\"");
     if (value == selected) out.print(" selected");
     out.print(">" + display +"</option>");
     
 }
 
 
 //
 // Common method for building HTML option tags using integers
 //
 public static void buildOption(int value, String display, int selected, PrintWriter out) {
     
     out.print("<option value=\""+ value +"\"");
     if (value == selected) out.print(" selected");
     out.print(">" + display +"</option>");
     
 }
 
 
// public static void displayNumericOptions(int start, int end, int step, boolean blnDoubleDigit, PrintWriter out)
 
 
 
 //
 // Common method for building HTML option tags using integers
 //
 public static void displayCourseSelection(String selectedCourse, boolean includeAll, Connection con, PrintWriter out) {

    try {
        
        String course = "";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT courseName FROM clubparm2");

        out.println("&nbsp;&nbsp;Course:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"course\"\">");

        while (rs.next()) {

            course = rs.getString(1);

            if (course.equals( selectedCourse )) {
                out.println("<option selected value=\"" + course + "\">" + course + "</option>");
            } else {
                out.println("<option value=\"" + course + "\">" + course + "</option>");
            }
        }
                
        if (includeAll) {
            
            if (selectedCourse.equals( "-ALL-" )) {
                out.println("<option selected value=\"-ALL-\">-ALL-</option>");
            } else {
                out.println("<option value=\"-ALL-\">-ALL-</option>");
            }
        }

        out.println("</select>");
        out.println("<br><br>");
   
    } catch (Exception ignore) {}
    
 }

 /**
  * displayActivitySheetSelection - Print out the list of checkboxes for the activity sheets under the current activity
  *
  * @param locations_csv Comma Seperated Value list of all the currently selected activity_ids
  * @param activity_id Root activity id to look at the children of
  * @param defaultChecked indicates if the checkboxes are defaulted to checked or not. true = checked
  * @param con Connection to club database
  * @param out Output printer
  */
 public static void displayActivitySheetSelect(String locations_csv, int activity_id, boolean defaultChecked, Connection con, PrintWriter out) {

     displayActivitySheetSelect(locations_csv, "", activity_id, defaultChecked, con, out);

 }


 /**
  * displayActivitySheetSelection - Print out the list of checkboxes for the activity sheets under the current activity
  *
  * @param locations_csv Comma Seperated Value list of all the currently selected activity_ids
  * @param avail_locations Comma Seperated Value list of all the configured locations (only these locations will appear in the select list)
  * @param activity_id Root activity id to look at the children of
  * @param defaultChecked indicates if the checkboxes are defaulted to checked or not. true = checked
  * @param con Connection to club database
  * @param out Output printer
  */
 public static void displayActivitySheetSelect(String locations_csv, String avail_locations, int activity_id, boolean defaultChecked, Connection con, PrintWriter out) {

     Statement stmt = null;
     Statement stmt2 = null;
     ResultSet rs = null;
     ResultSet rs2 = null;

     String in = getActivity.buildInString(activity_id, 1, con);
     int i = 0;
     try {

         boolean checked = false;
         String sql = "" +
                 "SELECT a1.activity_id, a1.activity_name, IF(a1.parent_id = 0, '', a2.activity_name) AS parent_name " +
                 "FROM activities a1 " +
                 "LEFT OUTER JOIN activities a2 ON a1.parent_id = a2.activity_id " +
                 "WHERE a1.activity_id IN (" + in + ") AND " +
                 "a1.activity_id NOT IN (SELECT parent_id FROM activities) " +
                 ((avail_locations.equals("")) ? "" : "AND a1.activity_id IN (" + avail_locations + ") ") +
                 "ORDER BY a1.parent_id, a1.sort_by, a1.activity_name";
         stmt = con.createStatement();
         rs = stmt.executeQuery( sql );

         out.println("<!-- SQL=" + sql + " -->");
         
         while ( rs.next() ) {

             if (!locations_csv.equals("")) {
                 stmt2 = con.createStatement();
                 rs2 = stmt2.executeQuery( "SELECT true FROM club5 WHERE " + rs.getInt("activity_id") + " IN (" + locations_csv + ")" );

                 checked = rs2.next();
             } else {
                 checked = defaultChecked;
             }

             out.println("<br>&nbsp;&nbsp;&nbsp; <input type=checkbox name='actChkBox_" + i + "' id='actChkBox_" + i + "' value='" + rs.getInt("activity_id") + "'" + ((checked) ? " checked" : "") + ">"
                     + "<label for='actChkBox_" + i + "'>&nbsp; " 
                     + (!rs.getString("parent_name").equals("") ? rs.getString("parent_name") + " - " : "") + rs.getString("activity_name") + "</label>");
             i++;
         }

     } catch (Exception exc) {
         out.println("Error in Common_Config loading activities: i=" + i + ", err=" + exc.toString());
         //Utilities.logError("Error in Common_Config loading activities: i=" + i + ", err=" + exc.toString());

     } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { stmt.close(); }
         catch (Exception ignore) {}

         try { rs2.close(); }
         catch (Exception ignore) {}

         try { stmt2.close(); }
         catch (Exception ignore) {}

         out.println("<input type=hidden name=location_count value='" + i + "'>");

     }
 }


 /**
  * buildLocationsString - Takes a request object and builds a comma seperated value list of all the checked activity ids submitted by a form using
  *                        the DisplayActivitySheetSelection method
  *
  * @param req Request object
  * @return - locations_csv - Comma seperated value list of all selected activity_ids
  */
 public static String buildLocationsString(HttpServletRequest req) {

     String locations_csv = "";
     
     int location_count = 0;

     ArrayList<Integer> locations = new ArrayList<Integer>();

     //
     // Get all the checked activities (courts or locations) from the form
     // this is where the event will take place
     //
     try {

         if (req.getParameter("location_count") != null) location_count = Integer.parseInt(req.getParameter("location_count"));

         for (int i = 0; i <= location_count; i++) {

             if (req.getParameter("actChkBox_" + i) != null) {

                 try {

                     //out.println("<!-- i=" + req.getParameter("actChkBox_" + i) + " -->");
                     locations.add(Integer.parseInt(req.getParameter("actChkBox_" + i)));

                 } catch (Exception ignore) { }
             }
         }

         for (int i = 0; i < locations.size(); i++) {

             locations_csv += locations.get(i) + ",";
         }

         if (!locations_csv.equals("")) {

             locations_csv = locations_csv.substring(0, locations_csv.length() - 1);

         }

     } catch (Exception exc) {
         locations_csv = "";
     }

     return locations_csv;
 }


 public static void displayHrMinToD(int time, String label, String elHr, String elMin, String elToD, PrintWriter out) {

    int hr = time / 100;
    int min = time - (hr * 100);

    String ampm = (hr >= 12) ? "PM" : "AM";

    displayHrMinToD(hr, min, ampm, label, elHr, elMin, elToD, out);

 }


 public static void displayHrMinToD(int hr, int min, String ampm, String label, String elHr, String elMin, String elToD, PrintWriter out) {

    if (hr > 12) hr -= 12; // handle hr coming in as a 24hr format
    if (hr == 0) hr = 12;
    
    if (!label.equals("")) out.print(label + "&nbsp;&nbsp;&nbsp;&nbsp; ");
    out.print("hr &nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"" + elHr + "\">");

    buildOption(1, 1, hr, out);
    buildOption(2, 2, hr, out);
    buildOption(3, 3, hr, out);
    buildOption(4, 4, hr, out);
    buildOption(5, 5, hr, out);
    buildOption(6, 6, hr, out);
    buildOption(7, 7, hr, out);
    buildOption(8, 8, hr, out);
    buildOption(9, 9, hr, out);
    buildOption(10, 10, hr, out);
    buildOption(11, 11, hr, out);
    buildOption(12, 12, hr, out);

    out.print("</select>");
    
    out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
    out.print("<input type=\"text\" name=\"" + elMin + "\" size=\"2\" maxlength=\"2\" value=" + SystemUtils.ensureDoubleDigit(min) + ">");
    
    out.print("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"" + elToD + "\">");
    
    buildOption("PM", "PM", ampm, out);
    buildOption("AM", "AM", ampm, out);
    
    out.print("</select>");
 }

 
 
 //
 // Common method for building the Custom Date Range Calendars for Reports (or other uses)
 //
 public static void buildReportCals(String servletName, PrintWriter out) {
    
    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

    Calendar cal = new GregorianCalendar();     // get today's date
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;
    int day = cal.get(Calendar.DAY_OF_MONTH);

    
    //
    //   Build and output the custom date range calendars - with form 
    //
    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
      out.println("<form action=\"" +servletName+ "\" method=\"post\" target=\"bot\">");
      out.println("<input type=\"hidden\" name=\"custom2\" value=\"yes\">");

      out.println("<tr><td>");
        out.println("<font size=\"2\">");
        out.println("<div id=\"awmobject1\">");        // allow menus to show over this box
        out.println("Start Date:&nbsp;&nbsp;&nbsp;");
        out.println("Month:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"smonth\">");
             out.println("<option selected value=\"01\">JAN</option>");
             out.println("<option value=\"02\">FEB</option>");
             out.println("<option value=\"03\">MAR</option>");
             out.println("<option value=\"04\">APR</option>");
             out.println("<option value=\"05\">MAY</option>");
             out.println("<option value=\"06\">JUN</option>");
             out.println("<option value=\"07\">JUL</option>");
             out.println("<option value=\"08\">AUG</option>");
             out.println("<option value=\"09\">SEP</option>");
             out.println("<option value=\"10\">OCT</option>");
             out.println("<option value=\"11\">NOV</option>");
             out.println("<option value=\"12\">DEC</option>");
        out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"sday\">");

        for (int i=1; i<=31; i++) {

            out.println("<option value=\"" + i + "\">" + i + "</option>");

        }

        out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"syear\">");

        for (int i=2003; i<=year; i++) {

            buildOption(i, i, year, out);

        }
        out.println("</select></div><br><br>");
        
        out.println("<div id=\"awmobject2\">");        // allow menus to show over this box
        out.println("End Date:&nbsp;&nbsp;&nbsp;&nbsp;");
        out.println("Month:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"emonth\">");

            buildOption(1,  "JAN", month, out);
            buildOption(2,  "FEB", month, out);
            buildOption(3,  "MAR", month, out);
            buildOption(4,  "APR", month, out);
            buildOption(5,  "MAY", month, out);
            buildOption(6,  "JUN", month, out);
            buildOption(7,  "JUL", month, out);
            buildOption(8,  "AUG", month, out);
            buildOption(9,  "SEP", month, out);
            buildOption(10, "OCT", month, out);
            buildOption(11, "NOV", month, out);
            buildOption(12, "DEC", month, out);

        out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"eday\">");

        for (int i=1; i<=31; i++) {

            buildOption(i, i, day, out);

        }
        out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"eyear\">");

        for (int i=2003; i<=year; i++) {

           buildOption(i, i, year, out);

        }
        out.println("</select></div><br><br>");

    out.println("<p align=\"center\"><input type=\"submit\" value=\"Continue\"></p>");
    out.println("</td></tr></table>");    
 }
 
 public static void buildEventCategoryOptions(int activity_id, int event_id, PrintWriter out, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     int category_count = 0;
     
     ArrayList<Integer> selected_ids = new ArrayList<Integer>();
     
     // If this is an existing event, populate an array list of existing category_ids selected for this event so we know if we need to check the checkbox for them later
     if (event_id != 0) {    
         
         try {

             pstmt = con.prepareStatement("SELECT category_id FROM event_category_bindings WHERE event_id = ? ORDER BY category_id");
             pstmt.clearParameters();
             pstmt.setInt(1, event_id);

             rs = pstmt.executeQuery();

             while (rs.next()) {

                 selected_ids.add(rs.getInt("category_id"));
             }

         } catch (Exception exc) {
             Utilities.logError("Common_Config.buildEventCategoryOptions - Error looking up previously selected categories for this event - ERR: " + exc.toString());
         } finally {

             try { rs.close(); }
             catch (Exception ignore) { }

             try { pstmt.close(); }
             catch (Exception ignore) { }
         }
     }
     
     // Print out category checkboxes and names for selection
     try {
         
         int category_id = 0;
         
         boolean checked = false;
         
         pstmt = con.prepareStatement("SELECT category_id, category_name FROM event_categories WHERE activity_id = ? ORDER BY category_name");
         pstmt.clearParameters();
         pstmt.setInt(1, activity_id);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
             
             checked = false;
             
             category_id = rs.getInt("category_id");
             
             category_count++;
             
             for (int i=0; i<selected_ids.size(); i++) {
                 
                 if (category_id == selected_ids.get(i)) {
                     checked = true;
                     break;
                 }
             }
             
             out.println("<label><input type=\"checkbox\" name=\"category_id_" + category_id + "\" value=\"1\"" + (checked ? " checked" : "") + ">&nbsp;&nbsp;" + rs.getString("category_name") + "</label><br>");
         }
         
         // If no categories were found, display a message indicating where they can be configured.
         if (category_count == 0) {
             out.println("(Event Categories can be configured from the System Config &gt; Event Setup menu)");
         }
         
     } catch (Exception exc) {
         Utilities.logError("Common_Config.buildEventCategoryOptions - Error listing event categories - ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
 }
 
 /**
  * printEventCategoryHiddenInputs - Standardized printing out of category_id checkbox values to pass on to another page. To make simple in case future changes need to be made.
  * 
  * @param category_ids ArrayList of category_ids that need to be printed as hidden inputs
  * @param out Output stream2
  */
 public static void printEventCategoryHiddenInputs(ArrayList<Integer> category_ids, PrintWriter out) {
     
     for (int i=0; i<category_ids.size(); i++) {
         
         if (category_ids.get(i) != 0) {
             out.println("<input type=\"hidden\" name=\"category_id_" + category_ids.get(i) + "\" value=\"1\">");
         }
     }
 }

 public static void makePrivateHelp(PrintWriter out) {

      out.println(SystemUtils.HeadTitle("Make Private - Help"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>\'Make Tee Time Private\' Option</H3>");
      out.println("<table style=\"text-align:left\">");
      out.println("<tr><td>The make private option allows staff users the ability to make particular tee times private on the member view.</td></tr>");
      out.println("<tr><td><br>The following options are available:</td></tr>");
      out.println("<br><tr><td><ul>");
      out.println("<li><span style=\"font-weight:bold;\">No</span> - Tee time will display normally (default)</li>");
      out.println("<li><span style=\"font-weight:bold;\">Hide Names</span> - Member/Guest names will be displayed as \"Member\" and \"Guest\" on the member view</li>");
      out.println("<li><span style=\"font-weight:bold;\">Hide Tee Time</span> - This tee time will not be displayed on the member view</li>");
      out.println("</ul></td></tr>");
      out.println("<tr><td><br><span style=\"font-weight:bold\">Note</span>: If your club has the 'Hide Member Names' option enabled in Club Options, "
              + "the 'No' option described above will not be present when booking a tee time, and instead the 'Hide Names' option will be selected by default.");
      out.println("</td></tr>");
      out.println("</table>");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:self.close();\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

 }
}
