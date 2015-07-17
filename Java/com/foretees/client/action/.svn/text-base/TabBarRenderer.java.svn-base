/***************************************************************************************
 *   TabBarRenderer:  This class will render a tab bar in the correct style and format
 *
 *
 *   created: 01/07/2004   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *      5/20/10   Updated to make year current & fix table td's to remove underscores between tabs
 *      4/28/10   Updated render() to not stretch the maintop banner too much vertically
 *
 ***************************************************************************************
 */

package com.foretees.client.action;

import java.io.*;
import java.util.*;

import com.foretees.client.HTMLConstants;
import com.foretees.client.StyleSheetConstants;

import com.foretees.client.attribute.Image;

//import com.foretees.client.action.ActionModel;

/**
 ***************************************************************************************
 *
 *   This class will generate html to display an action model as a vertical list of
 *   tabs in the user interface.
 *
 ***************************************************************************************
 **/

public class TabBarRenderer {

  // Initialize the attributes

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();

  /**
  ***************************************************************************************
  *
  * Generates the html necessary to present this action model as a vertical list of
  * tabs in the user interface.
  *
  * @param model the action model to use to construct the actions.
  * @param selectedAction the action to show as disabled if it exists in this action model.
  *                       A null value or empty string will display all actions as enabled.
  * @param rev the revision for this software used to qualify the images
  * @param out the writer to print the html.
  *
  *
  ***************************************************************************************
  **/

  public static void render(ActionModel model, String selectedAction, String rev, PrintWriter out)
  {

    //preload the images for each of the actions in the action model
    out.println("<script type=\"text/javascript\">");

    int ms = model.size();

    for (int i=0; i<ms; i ++){

      Object nxtObj = model.get(i);

      if (nxtObj instanceof Action)
      {

        Action nxtAction = (Action)nxtObj;
        Image image = nxtAction.getImage();

        if (image != null && image.getSrc() != null)
        {
          out.println("preloadImage(\"/" + rev + image.getSrc() + "\");");
        }
      }

    }
    out.println("</script>");

    //
    //  Get this year
    //
    Calendar cal = new GregorianCalendar();       // get todays date
    int year = cal.get(Calendar.YEAR);            // get the year


    out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\" bgcolor=\"#CCCCAA\">");
    out.println("<center>");
    out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");
    out.println("<tr><td align=\"left\">&nbsp &nbsp;");
    out.println("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=\"0\" width=\"114\" height=\"34\">");
    out.println("</td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"5\">" + model.getLabel() +"</font>");
    out.println("</td>");
    out.println("<td align=\"center\">");
    out.println(" <p><a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\"><img src=\"/" +rev+ "/images/weather.gif\" border=0></a>");
    out.println("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
    out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
    out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC&nbsp;<br>" +year+ " All rights reserved.");
    out.println("</font></p>");
    out.println("</td>");
    out.println("</tr>");
    out.println("<tr><td align=\"center\" colspan=\"3\" valign=\"bottom\">");

    //Begin code to display images horizontally.

    for (int i=0; i<ms; i ++){
      Object nxtObj = model.get(i);

      if ( nxtObj instanceof Action)
      {
        Action nxtAction = (Action)nxtObj;

        Image image = nxtAction.getImage();

        out.print("<a href=\"/" + rev + nxtAction.getUrl() + "\" target=\"" + nxtAction.getTarget() + "\" onMouseOver=\"" + nxtAction.getOnMouseOver() + "\" onMouseOut=\"" + nxtAction.getOnMouseOut() + "\">");

        if (image != null && image.getSrc() != null)
        {
          out.print("<img name=\"/" + image.getName() + "\" src=\"/" + rev + image.getSrc() + "\" hspace=\"0\" border=\"0\" alt=\"" + image.getAlt() + "\">");
        }
        out.println("</a>");
      }
      else
      {
        //for now assume that this is for a separator
        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }


    }

    //End code to display images horizontally.
    out.println("</td>");
    out.println("</tr>");
    out.println("</table>");
    out.println("</center>");
    out.println("</body></html>");

    out.flush();
  }
}