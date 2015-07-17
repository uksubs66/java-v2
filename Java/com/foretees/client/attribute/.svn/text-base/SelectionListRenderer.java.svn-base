/***************************************************************************************
 *   SelectionListRenderer:  This class will render a html select component
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.attribute;

import java.io.*;

import com.foretees.client.HTMLConstants;
import com.foretees.client.StyleSheetConstants;

import com.foretees.client.attribute.Option;
import com.foretees.client.attribute.SelectionList;

public class SelectionListRenderer {

  //*****************************************************
  // Initialize the attributes
  //*****************************************************

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();
  private static String SELECT_BEGIN = "<select name=\"";
  private static String SELECT_END = "</select>";
  private static String OPTION_BEGIN = "<option value=\"";
  private static String OPTION_END = "</option>";

  public static void render(SelectionList theList, PrintWriter out)
  {

   boolean alwaysDisplayAsList = theList.getAlwaysShowAsList();

   if (theList != null)
    {

      String label = theList.getLabel();
      if (label != null && !(label.equals("")))
      {
        out.print(label + ":&nbsp;");
      }

      if (!alwaysDisplayAsList && (theList.size() == 1 && theList.isRequired()))
      {

        //if the size of the list is 1 and a value is required
        //then just print out the value in view mode and add a hidden field so that
        //the default value gets posted
        Option option = theList.getOption(0);
        String displayName = option.getName();

        if (displayName == null || displayName.equals(""))
        {
          out.print(hc.NON_BREAK_SPACE);
        }
        else
        {
          out.print(displayName);
          out.print("<input type=\"hidden\" name=\"" + theList.getName() + "\" value=\"" + displayName + "\">");
        }

      }
      else if (theList.size() == 0 && !alwaysDisplayAsList)
      {
        out.print(hc.NON_BREAK_SPACE);
      }
      else {

        out.print(SELECT_BEGIN + theList.getName() + "\" size=\"" + theList.getSize() + "\" onClick=\"" + theList.getOnClick() + "\"");

        if (theList.isMultiSelect())
        {
          out.print(" multiple");
        }

        out.print(" >");

        if (!(theList.isRequired()))
        {
          out.print(OPTION_BEGIN + "" + "\"");

          out.print(">" + "" + OPTION_END);

        }

        for (int i=0; i<theList.size(); i++){

          Option option  = theList.getOption(i);

          if ( option != null)
          {
            out.print(OPTION_BEGIN + option.getValue() + "\"");

            if (option.isSelected())
            {
              out.print(" selected");
            }

            out.print(">" + option.getName() + OPTION_END);

          }
        }

        out.println("</select>");
      }
    }
    else
    {
      out.print(">" + hc.NON_BREAK_SPACE);
    }

    out.flush();
  }

}