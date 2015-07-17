/***************************************************************************************
 *   TableRenderer:  This class will render a table of data
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

import com.foretees.client.action.Action;
import com.foretees.client.action.ActionModel;
import com.foretees.client.action.ButtonBarRenderer;
import com.foretees.client.action.NavBarUrlRenderer;

import com.foretees.client.table.HeaderRenderer;
import com.foretees.client.table.TableModel;

public class TableRenderer {

  //*****************************************************
  // Initialize the attributes
  //*****************************************************

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();

  public static void render(TableModel model, PrintWriter out)
  {

    if (model != null){

      int ms = model.size();
      ColumnModel columns = model.getColumns();
      int nc = columns.size();

      int colspan = nc;

      if (model.isSelectable())
      {
        colspan = colspan + 1;
      }



      //if (model.getHelpText() != null && !(model.getHelpText().equals("")))
      //{
      ///  out.print(model.getHelpText() + "<br><br>");
      //}

      out.print("<table class=\"lst\" align=\"center\">");
      out.print("<tr class=\"rwDta\"><td colspan=\"" +  colspan + "\"><table class=\"tblHdrWrp\"><tr><th  class=\"tblHdr\">" + model.getLabel());

      if (model.printNumItems())
        out.print("&nbsp;&nbsp;(" + ms + "&nbsp;&nbsp;Items)");

      out.print("</th>");
      out.print("<th class=\"tblHdrCntr\">");

      if (model.isPaging())
      {
        out.print("&nbsp;&nbsp;Page " + model.getCurrentPage() + " of " + model.getNumberOfPages());
      }
      else
        out.print("&nbsp;&nbsp;");

      out.print("</th>");

      out.print("<th class=\"tblHdrRght\">");
      ActionModel contextActions = model.getContextActions();
      if (contextActions != null)
      {
        ButtonBarRenderer.render(contextActions, out);
      }
      else
        out.print("&nbsp;&nbsp;");
      out.print("</th>");

      out.print("</tr></table></td></tr>");

      ActionModel multiSelectActions = model.getMultiSelectActions();

      if (multiSelectActions != null)
      {
        //print out the table actions
        out.print("<tr class=\"tblAct\"><td colspan=\"" +  colspan + "\"><table width=\"100%\"><tr>");

        if (multiSelectActions != null)
        {
          out.print("<td nowrap align=\"left\">");
          for (int i=0; i<multiSelectActions.size();i++)
          {
            Action nxtAction = (Action)(multiSelectActions.get(i));
            NavBarUrlRenderer.render(nxtAction, out);
            out.print("&nbsp;&nbsp;");
          }
          out.print("</td>");
        }

        /*if (contextActions != null)
        {
          out.print("<td nowrap align=\"right\">");
          for (int i=0; i<contextActions.size();i++)
          {
            Action nxtAction = (Action)(contextActions.get(i));
            NavBarUrlRenderer.render(nxtAction, out);
            out.print("&nbsp;&nbsp;");
          }
          out.print("</td>");
        }*/

        out.print("</tr></table></td></tr>");
      }



      //print out the column headers
      out.print("<tr class=\"rwDta\">");

      if (model.isSelectable())
      {
        ///*<input type=\"checkbox\" name=\"cb_all_" + model.getId() + "\" value=\"all\">
        out.println("<td class=\"cbCol\" width=\"2%\" >&nbsp;</td>");
      }

      HeaderRenderer.render(model.getColumns(), out);
      out.print(hc.E_TR);

      boolean evenRow = false;

      int rowToStart = 0;
      int rowsToRender = ms;

      if (model.isPaging())
      {
        rowsToRender = model.getPageRowEnd();
        rowToStart = model.getPageRowStart();
      }

      //print out the rows
      for (int i=rowToStart; i<rowsToRender; i ++){
        RowModel row = model.getRow(i);

        RowRenderer.render(row, evenRow, model.isSelectable(), model.getId(), out);

        evenRow = !evenRow;

      }

      out.println(hc.E_TBL);


    }

    out.flush();
  }
}