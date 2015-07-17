/***************************************************************************************
 *   ButtonBarRenderer:  This class will render a set of buttons in horizontally
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
import com.foretees.client.action.ActionModel;
import com.foretees.client.action.ButtonRenderer;

/**
 ***************************************************************************************
 *
 *   This class will generate html to display an action model as a vertical list of
 *   links in the user interface.
 *
 ***************************************************************************************
 **/

public class ButtonBarRenderer {

  // Initialize the attributes

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();

  /**
  ***************************************************************************************
  *
  * Generates the html necessary to present this action model as a horizontal list of
  * buttons in the user interface.
  *
  * @param model the action model to use to construct the buttons.
 * @param out the writer to print the html.
  *
  *
  ***************************************************************************************
  **/

  public static void render(ActionModel model, PrintWriter out)
  {

    if (model != null && model.size()>0)
    {

      out.print("<table class=\"pgMnu\">");
      out.print("<tr>");

      for (int i=0; i<model.size(); i++){

        Object nxtObj = model.get(i);

        if ( nxtObj instanceof Action)
        {
          Action action = (Action)nxtObj;

          out.print("<td>");

          //render the actions as buttons
          ButtonRenderer.render(action, out);

          out.print("</td>");
        }

      }
      out.println("</tr></table>");
      out.flush();
    }

  }
}