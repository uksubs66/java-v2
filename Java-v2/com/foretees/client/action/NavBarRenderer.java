/***************************************************************************************
 *   NavBarRenderer:  This class will render a left nav bar the correct
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
import com.foretees.client.action.ActionModel;

/**
 ***************************************************************************************
 *
 *   This class will generate html to display an action model as a vertical list of
 *   links in the user interface.
 *
 ***************************************************************************************
 **/

public class NavBarRenderer {

  // Initialize the attributes

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();

  /**
  ***************************************************************************************
  *
  * Generates the html necessary to present this action model as a vertical list of
  * links in the user interface.
  *
  * @param model the action model to use to construct the actions.
  * @param selectedAction the action to show as disabled if it exists in this action model.
  *                       A null value or empty string will display all actions as enabled.
  * @param out the writer to print the html.
  *
  *
  ***************************************************************************************
  **/

  public static void render(ActionModel model, String selectedAction, PrintWriter out)
  {

    out.println(hc.TD + hc.CLASS + sc.SIDE_NAV_BAR + hc.CMP_STR + hc.WIDTH + "25%\">");
    out.println(hc.DIV + hc.CLASS + sc.NAV_BAR_HDR + hc.CMP_STR + ">" + model.getLabel() + hc.E_DIV);
    out.println(hc.HR + hc.CLASS + sc.NAV_BAR_HDR_SEP + hc.CMP_STR + ">");
    out.println(hc.TBL + hc.CLASS + sc.NAV_BAR_TBL + hc.CMP_STR + ">");

    int ms = model.size();

    for (int i=0; i<ms; i ++){

      Object nxtObj = model.get(i);

      if ( nxtObj instanceof Action)
      {
        Action nxtAction = (Action)nxtObj;
        out.println(hc.TR + ">" + hc.TD + ">");

        if (selectedAction != null && (nxtAction.getName()).equals(selectedAction)){
          nxtAction.setSelected(true);
        }

        NavBarUrlRenderer.render(nxtAction, out);
        out.println(hc.E_TD + hc.E_TR);

        //reset the action selected to false so it isn't selected next time unless
        //specified by the caller
        nxtAction.setSelected(false);
      }


    }

    out.println(hc.E_TBL + hc.E_TD);
    out.flush();
  }
}