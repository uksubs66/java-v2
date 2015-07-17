/***************************************************************************************
 *   WizardRenderer:  This class will render a selected step in a wizard
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.wizard;

import java.io.*;

import com.foretees.client.HTMLConstants;

import com.foretees.client.ScriptHelper;
import com.foretees.client.StyleSheetConstants;

import com.foretees.client.form.FormModel;
import com.foretees.client.form.FormRenderer;

/**
 ***************************************************************************************
 *
 *   This class will generate html to display a wizard step.
 *
 ***************************************************************************************
 **/

public class WizardRenderer {

  // Initialize the attributes

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();

  /**
  ***************************************************************************************
  *
  * Generates the html necessary to present a wizard step.
  *
  * @param model the wizard model to use to construct the wizard.
  * @param out the writer to print the html.
  *
  ***************************************************************************************
  **/

  public static void render(WizardModel wizard, PrintWriter out)
  {


    drawHeader( wizard.getLabel(), null, null, out);
    int step = wizard.getSelectedStep();
    FormModel model = null;

    if (step > -1)
      model = (wizard.getStep(step)).getForm();

    if (model != null){

      //render the top part of the wizard

      //render the form

      FormRenderer.render(model, out);

      //render the botton part of the wizard


    }

    drawEndPageContentWrapper(out);
    out.flush();
  }

  /**
  ***************************************************************************************
  *
  * This method will draw the beginning of a wizard page that includes the html, head, and
  * body tag, as well as the wizard header bar.  This should be called at the beginning of all
  * wizard pages for consistency.
  *
  * @param pageTitle the title for the current page being show
  * @param stepLabel the text to use for this
  * @param jsOnLoad the javascript method to use when loading the page.  A null or empty string
  *                 will not generate the onLoad method to in the body tag.
  * @param jsOnUnLoad the javascript method to use when unloading the page.  A null or empty string
  *                 will not generated the onUnLoad method in the body tag.
  * @param out the output stream in which to write the html.
  *
  ***************************************************************************************
  **/

   public static void drawHeader( String pageTitle, String jsOnLoad, String jsOnUnLoad, PrintWriter out)
  {

    out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">");
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<title>" + pageTitle + "</title>");

    ScriptHelper.insertHTMLForJavascriptFile(out);
    ScriptHelper.insertHTMLForStyleSheet(out);

    out.println("</head>");
    out.println("<body");

    if (jsOnLoad != null && !(jsOnLoad.equals("")))
      out.print(" onLoad=\"" + jsOnLoad + "\"");
    if (jsOnUnLoad != null && !(jsOnUnLoad.equals("")))
      out.print(" onUnload=\"" + jsOnUnLoad + "\"");

    String header = "";

    out.print("><table class=\"wizHdr\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"ccccaa\">");
    out.print("<tr><td valign=\"top\" align=\"left\">");
    out.print(pageTitle);
    out.print("</td></td></tr></table>");

  }

  /**
  ***************************************************************************************
  *
  * This method will draw the closing body and html tags.  This method should be called
  * last in any html page.
  *
  * @param out the output stream in which to write the html.
  *
  ***************************************************************************************
  **/

  public static void drawEndPageContentWrapper(PrintWriter out)
  {

    out.println("</body></html>");


  }


}