/***************************************************************************************
 *   HeaderRenderer:  This class will render the columns for a table
  *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.table;

import java.io.*;

import com.foretees.client.HTMLConstants;
import com.foretees.client.StyleSheetConstants;

public class HeaderRenderer {

  //*****************************************************
  // Initialize the attributes
  //*****************************************************

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();
  private static String COLUMN_BEGIN = "<th class=\"colHdr\">";
  private static String COLUMN_W_SUBLABEL_BEGIN = "<th class=\"colHdr\"><table class=\"sub\" align=\"center\"><tr><th align=\"center\" class=\"colSub\">";
  private static String COLUMN_W_SUBLABEL_MID = "</th></tr><tr><th class=\"colHdrSub\">";
  private static String COLUMN_W_SUBLABEL_END = "</th></tr></table></th>";

  public static void render(ColumnModel theColumnModel, PrintWriter out)
  {


    if (theColumnModel != null)
    {

      for (int i=0; i<theColumnModel.size(); i ++)
      {

        Column column = theColumnModel.get(i);

        boolean hasSubLabel = false;

        //test if we have a sub label for the column header, we need to do special handling
        //to get the appropriate look.
        if (column.getSubLabel() != null && !(column.getSubLabel().equals("")))
        {
          hasSubLabel = true;
        }

        if (hasSubLabel)
          out.print(COLUMN_W_SUBLABEL_BEGIN);
        else
          out.print(COLUMN_BEGIN);

        if (column != null){

          if(column.isSelected()){

            //this will need to be implemented if we support sorting
          }
          else{
            out.print(column.getLabel());
          }

          if (hasSubLabel)
            out.print(COLUMN_W_SUBLABEL_MID + column.getSubLabel());



        }
        else
        {
          out.print(hc.NON_BREAK_SPACE);
        }

        if (hasSubLabel)
          out.print(COLUMN_W_SUBLABEL_END);
        else
          out.print(hc.E_TH);



      }
    }

     out.flush();

  }

}