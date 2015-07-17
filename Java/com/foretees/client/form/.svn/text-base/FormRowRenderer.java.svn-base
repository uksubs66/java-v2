/***************************************************************************************
 *   FormRowRenderer:  This class will render a row in a form
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.form;

import java.io.*;

import com.foretees.client.HTMLConstants;
import com.foretees.client.StyleSheetConstants;

import com.foretees.client.layout.LayoutModel;

import com.foretees.client.table.Cell;
import com.foretees.client.table.CellRenderer;
import com.foretees.client.table.RowModel;

public class FormRowRenderer {

  // Initialize the attributes

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();
  private static String ROW_BEGIN = "<tr valign=\"top\" class=\"frmDta\">";
  private static String TABLE_END = "</table></td>";



  public static void render(RowModel theRowModel,PrintWriter out)
  {

    if (theRowModel != null){

      if (theRowModel instanceof LayoutModel)
      {
        FormRowRenderer.render(((LayoutModel)theRowModel), out);
      }
      else{

        out.print(ROW_BEGIN);

        for (int i=0; i<theRowModel.size(); i++)
        {


          if (theRowModel.get(i) != null){


            Cell cell = theRowModel.get(i);
            CellRenderer.render(theRowModel.get(i), out);

          }

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

  private static void render(LayoutModel model, PrintWriter out)
  {

    if (model != null){

      int rows = model.numRows();

      if (rows > 0){

        String TABLE_BEGIN = "<td colspan=\"" + model.getNumColumns() + "\"><table width=\"100%\">";

        if (model != null){

          out.print(ROW_BEGIN);
          out.print(TABLE_BEGIN);

          //print out the rows
          for (int i=0; i<rows; i ++){
            RowModel row = model.getRow(i);

            FormRowRenderer.render(row, out);

          }

          out.print(TABLE_END);
          out.print(ROW_BEGIN);


        }
      }
    }

  }

}