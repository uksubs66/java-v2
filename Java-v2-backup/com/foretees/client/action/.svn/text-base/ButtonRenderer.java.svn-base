/***************************************************************************************
 *   ButtonRenderer:  This class will render a button in the correct
 *   style and format
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 * 
 *     5/29/09   Add a label (id=) on the anchor button if it is the Send button for email page.
 *
 ***************************************************************************************
 */

package com.foretees.client.action;

import java.io.*;

import com.foretees.client.HTMLConstants;
import com.foretees.client.StyleSheetConstants;

/**
 ***************************************************************************************
 *
 *   This class will generate html to display an action as a button in the user interface.
 *
 ***************************************************************************************
 **/

public class ButtonRenderer {

  // Initialize the attributes
  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();

  /**
  ***************************************************************************************
  *
  * Generates the html necessary to present this action as a button in the user interface.
  *
  * @param action the action to use to construct the html.
  * @param out the writer to print the html.
  *
  *
  ***************************************************************************************
  **/

  public static void render(Action action, PrintWriter out)
  {

    if (action != null  && !(action.isSelected())){
       
      String label = action.getLabel();

      out.print("<table class=\"btn\"><tr><td nowrap>");

      out.print("<a class=\"btnHref\" ");

      if (label.equals("Send")) out.print(" id=\"sendLink\" ");   // Send button from Email Tool page (label it)

      renderToolTip(action, out);

      out.print(" href=\"" + action.getUrl() + "\">" + label + "</a>");

      out.print("</td></tr></table>");
    }

    out.flush();

  }

  /**
  ***************************************************************************************
  *
  * Generates the alt and title tags on for the tooltip if one exists on the action.
  *
  * @param action the action to use to look for the tooltip
  * @param out the writer to print the html.
  *
  *
  ***************************************************************************************
  **/

  private static void renderToolTip(Action action, PrintWriter out)
  {

    if (!(action.getToolTip().equals(""))){
      out.print(" " + hc.ALT + action.getToolTip() + hc.CMP_STR + " " + hc.TITLE + action.getToolTip() + hc.CMP_STR + " ");
    }

  }

}