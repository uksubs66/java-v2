/***************************************************************************************
 *   FormRenderer:  This class will render an html form given a FormModel.
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
import java.util.ArrayList;

import com.foretees.client.HTMLConstants;
import com.foretees.client.StyleSheetConstants;

import com.foretees.client.action.ActionModel;
import com.foretees.client.action.ButtonBarRenderer;

import com.foretees.client.attribute.HiddenInput;
import com.foretees.client.form.FormRowRenderer;
import com.foretees.client.table.RowModel;

/**
 ***************************************************************************************
 *
 *   This class will generate html to display a form and its contents.
 *
 ***************************************************************************************
 **/

public class FormRenderer {

  // Initialize the attributes

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();

  /**
  ***************************************************************************************
  *
  * Generates the html necessary to present a form and its contents.
  *
  * @param model the form model to use to construct the form.
  * @param out the writer to print the html.
  *
  ***************************************************************************************
  **/

  public static void render(FormModel model, PrintWriter out)
  {

    if (model != null){

      int rows = model.numRows();

      if (rows > 0){

        ArrayList helpSteps = model.getHelpSteps();
        boolean printHelp = false;
        if (helpSteps != null && helpSteps.size()>0)
        {
          printHelp = true;
        }

        if (printHelp)
        {
          //print a wrapper table, one cell for the form, the other for the steps.
          out.print("<table width=\"100%\" valign=\"top\" align=\"center\" class=\"frmWrp\">");
          out.print("<tr><td width=\"65%\" valign=\"top\">");
        }

        out.print("<table cellspacing=\"0px\" cellpadding=\"2px\" width=\"100%\" valign=\"top\" align=\"center\" class=\"" + model.getStyleSheetClass() + "\">");

        out.print("<form name=\"" + model.getName() + "\" method=\"" + model.getMethod() + "\"");

        if (model.getTarget() != null && !(model.getTarget().equals("")))
        {
          out.print(" target=\"" + model.getTarget() + "\"");
        }

        if (model.getEncType() != null && !(model.getEncType().equals("")))
        {
          out.print(" enctype=\"" + model.getEncType() + "\"");
        }

        out.print(">");

        //print out the rows
        for (int i=0; i<rows; i ++){
          RowModel row = model.getRow(i);

          FormRowRenderer.render(row, out);

        }
        renderActions(model, out);


        renderHiddenInputs(model, out);

        out.print("</form>");
        out.println(hc.E_TBL);

        if (printHelp)
        {
          out.println("</td><td class=\"frmHlp\" valign=\"top\">");

          //print the embedded help steps if there is any

          out.print("<center><b><u>Instructions</u></b></center>");
          out.println("<ul>");   

          for (int i=0; i<helpSteps.size(); i++)
          {
           // out.println("&middot;&nbsp;&nbsp;" + helpSteps.get(i) + "<br><br>");
            out.println("<li>" + helpSteps.get(i) + "</li><br>");

          }

          out.println("</ul>");   
          //end of wrapper when help is printed
          out.println("</td></tr></table>");
        }
      }

      out.flush();
    }


  }

  private static void renderHiddenInputs(FormModel model, PrintWriter out)
  {

    if (model.numHiddenInputs() > 0){

      for(int i=0; i<model.numHiddenInputs(); i++)
      {
        HiddenInput input = model.getHiddenInput(i);
        out.println("<input type=\"hidden\" name=\"" + input.getName() +  "\" value=\"" + input.getValue() + "\">");

      }
    }
  }

  private static void renderActions(FormModel model, PrintWriter out)
  {

    ActionModel actions = model.getActions();
    //if we don't have any actions, don't take up the space to render an empty row
    if (actions != null && actions.size()>0){
      out.print("<tr><td colspan=\"" + model.getNumColumns() + "\"><hr>");
      ButtonBarRenderer.render(model.getActions(), out);
      out.println("</td></tr>");
    }
  }
}