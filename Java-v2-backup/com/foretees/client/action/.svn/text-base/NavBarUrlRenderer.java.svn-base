/***************************************************************************************
 *   NavBarUrlRenderer:  This class will render a left nav bar url in the correct
 *   style and format
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
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
 *   This class will generate html to display an action as a hyperlink in the user interface.
 *
 ***************************************************************************************
 **/

public class NavBarUrlRenderer {

  // Initialize the attributes
  private String label = "No label specified";
  private String url = "No url specified";

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();

  /**
  ***************************************************************************************
  *
  * Generates the html necessary to present this action as a hyperlink in the user interface.
  *
  * @param action the action to use to construct the hyperlink.
  * @param out the writer to print the html.
  *
  *
  ***************************************************************************************
  **/

  public static void render(Action action, PrintWriter out)
  {

    if (action != null){

      if(action.isSelected()){
        out.print(hc.A + hc.CLASS + sc.NAV_BAR_HREF_SEL + hc.CMP_STR);

        renderToolTip(action, out);

        out.print(">" + action.getLabel() + hc.E_A);
      }
      else{
        out.print(hc.A + hc.CLASS + sc.NAV_BAR_HREF + hc.CMP_STR);

        renderToolTip(action, out);

        out.print(hc.HREF + action.getUrl() + hc.CMP_STR + ">" + action.getLabel() + hc.E_A);
      }

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