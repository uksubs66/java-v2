/***************************************************************************************
 *   RowRenderer:  This class will render a row in a table
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

import com.foretees.client.table.CellRenderer;

public class RowRenderer {

  //*****************************************************
  // Initialize the attributes
  //*****************************************************

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();
  private static String EVEN_ROW_BEGIN = "<tr valign=\"top\" class=\"rwDtaEve\">";
  private static String ODD_ROW_BEGIN = "<tr valign=\"top\" class=\"rwDtaOdd\">";

  public static void render(RowModel theRowModel, boolean evenRow, PrintWriter out)
  {
    renderContent(theRowModel, evenRow, false, "", out);
  }

  public static void render(RowModel theRowModel, boolean evenRow, boolean isSelectable,  String selectablePrefix, PrintWriter out)
  {
    renderContent(theRowModel, evenRow, isSelectable, selectablePrefix, out);
  }



  private static void renderContent(RowModel theRowModel, boolean evenRow, boolean isSelectable, String selectablePrefix, PrintWriter out)
  {


    if (evenRow)
    {
      out.print(EVEN_ROW_BEGIN);
    }
    else
    {
      out.print(ODD_ROW_BEGIN);
    }

    if (theRowModel != null){

      if (isSelectable)
      {
        out.println("<td class=\"cbCol\" width=\"2%\" ><input type=\"checkbox\" name=\"cb_" + selectablePrefix + ":" + theRowModel.getId() + "\" value=\"" + theRowModel.getId() + "\"></td>");
      }
      else
      {
        out.println("<input type=\"hidden\" name=\"cb_" + selectablePrefix + ":" + theRowModel.getId() + "\" value=\"" + theRowModel.getId() + "\">");
      }

      for (int i=0; i<theRowModel.size(); i++)
      {

        if (theRowModel.get(i) != null){

          CellRenderer.render(theRowModel.get(i), out);

        }

      }
    }
    else
    {
      //if the row is empty, just add one cell and make it stretch the length of the table
      out.println(hc.TD + " width=\"100%\">" + hc.NON_BREAK_SPACE + hc.E_TD);
    }

    out.print(hc.E_TR);
    out.flush();

  }

}