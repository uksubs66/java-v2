/***************************************************************************************
 *   LayoutHelper:  This utility class will draw html that provides layout
 *
 *
 *   created: 10/03/2003   jag
 *
 *
 *   last updated:
 *
 *      10/18/04 RDP Get the current year for the copyright notice.
 *                   Change table width from 100% to 85% in drawBeginPageContentWrapper
 *                   and in drawBeginMainBodyContentWrapper and foretees.css.
 *
 *
 ***************************************************************************************
 */

package com.foretees.client.layout;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import com.foretees.client.action.Action;
import com.foretees.client.action.ActionHelper;
import com.foretees.client.action.ActionModel;
import com.foretees.client.action.ButtonRenderer;
import com.foretees.client.action.ButtonBarRenderer;


import com.foretees.client.HTMLConstants;
import com.foretees.client.ScriptHelper;
import com.foretees.client.StyleSheetConstants;

import com.foretees.common.ProcessConstants;

/**
 ***************************************************************************************
 *
 *  This helper class contains commonly used html sections to provide consistent layouts
 *  through all pages.
 *
 ***************************************************************************************
 **/

public class LayoutHelper {

  // Initialize the attributes

  private static HTMLConstants hc = new HTMLConstants();
  private static StyleSheetConstants sc = new StyleSheetConstants();
  private static String versionId = ProcessConstants.CODEBASE;

  /**
  ***************************************************************************************
  *
  * This method will draw the beginning of the page that includes the logo, weather image,
  * body tag, and tabs.  This should be called at the beginning of an main html page.
  *
  * @param userType the type of member that is logged in ADMIN, PROSHOP, MEMBER.  This will determine
  *                 the text at the top of the page and the tabs that get shown
  * @param pageTitle the title for the current page being show
  * @param selectedTab the name of the action related to the tab that should show as hightlighted
  * @param jsOnLoad the javascript method to use when loading the page.  A null or empty string
  *                 will not generate the onLoad method to in the body tag.
  * @param jsOnUnLoad the javascript method to use when unloading the page.  A null or empty string
  *                 will not generated the onUnLoad method in the body tag.
  * @param out the output stream in which to write the html.
  *
  ***************************************************************************************
  **/

  public static void drawHeader( String userType, String pageTitle, String selectedTab, String jsOnLoad, String jsOnUnLoad, PrintWriter out)
  {

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int year = cal.get(Calendar.YEAR);            // get the year

    out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">");
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<title>" + pageTitle + "</title>");

    ScriptHelper.insertHTMLForJavascriptFile(out);
    ScriptHelper.insertHTMLForStyleSheet(out);
    ScriptHelper.drawPreloadImageScript(userType, out);

    out.println("</head>");
    out.println("<body");

    if (jsOnLoad != null && !(jsOnLoad.equals("")))
      out.print(" onLoad=\"" + jsOnLoad + "\"");
    if (jsOnUnLoad != null && !(jsOnUnLoad.equals("")))
      out.print(" onUnload=\"" + jsOnUnLoad + "\"");

    String header = "";

    if (userType.equals(ProcessConstants.ADMIN))
    {
      header = "System Administration ";
    }
    else if (userType.equals(ProcessConstants.PROSHOP))
    {
      header = "Pro Shop Tee Time Management";
    }
    else if (userType.equals(ProcessConstants.MEMBER))
    {
      header = "Member Tee Time Management";
    }

    out.print("><table margin=\"0\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"ccccaa\">");
    out.print("<tr><td valign=\"top\" align=\"left\">");
    out.print("<img src=\"" +versionId+ "images/foretees.gif\" border=\"0\">");
    out.print("</td><td align=\"center\">");
    out.print("<font size=\"5\">" + header + "</font>");
    out.print("</td><td align=\"center\">");
    out.print("<p><a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\"><img src=\"" +versionId+ "images/weather.gif\" border=\"0\"></a>");
    out.print("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
    out.print("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
    out.print("<font size=\"1\" color=\"#000000\">ForeTees, LLC " +year+ " All rights reserved.");
    out.print("</font></p></td></tr></table>");

    ActionHelper.drawTabs(userType, selectedTab, out);
  }

  /**
  ***************************************************************************************
  *
  * This method will draw the beginning of the main part of the page that includes the opening
  * body tag, a separator, the beginning of the table to use as a layout and the beginning
  * of the main row for the page.  This should be called at the beginning of an html page.
  *
  * @param jsOnLoad the javascript method to use when loading the page.  A null or empty string
  *                 will not generate the onLoad method to in the body tag.
  * @param jsOnUnLoad the javascript method to use when unloading the page.  A null or empty string
  *                 will not generated the onUnLoad method in the body tag.
  * @param out the output stream in which to write the html.
  *
  ***************************************************************************************
  **/

  public static void drawBeginPageContentWrapper(String jsOnLoad, String jsOnUnLoad, PrintWriter out)
  {

    out.print("<body ");
    if (jsOnLoad != null && !(jsOnLoad.equals("")))
      out.print(" onLoad=\"" + jsOnLoad + "\"");
    if (jsOnUnLoad != null && !(jsOnUnLoad.equals("")))
      out.print(" onUnload=\"" + jsOnUnLoad + "\"");

    out.print(">");

    out.println("<table align=\"center\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>");


  }

  /**
  ***************************************************************************************
  *
  * This method will draw the beginning of a table cell in the main row of the page and a
  * a header for the page that includes page level actions.  In general this method is
  * used when rendering a page that contains a left side navigation bar and a right side
  * main content section.
  *
  * @param thePageLabel the text to display as the page header
  * @param theActions the action model that contains the actions to display in the page
  *                   header.  These actions will be displayed as buttons.
  * @param out the output stream in which to write the html.
  *
  ***************************************************************************************
  **/

  public static void drawBeginMainBodyContentWrapper(String thePageLabel, ActionModel theActions, PrintWriter out)
  {

    out.println("<table align=\"center\" class=\"frm\" border=\"20\" cellspacing=\"0\" cellpadding=\"0\">");
    out.println("<tr valign=\"top\">");


    if (thePageLabel == null || thePageLabel.equals(""))
    {
      thePageLabel = HTMLConstants.NON_BREAK_SPACE;
    }


    out.print("<td class=\"mnCnt\" >");
    out.print("<table class=\"pgHdr\">");
    out.print("<tr><td class=\"pgHdr\">" + thePageLabel + "</td>");
    out.print("<td class=\"pgMnu\">");

    ButtonBarRenderer.render(theActions, out);


    out.print("</td><td class=\"pgHdr\">&nbsp;&nbsp;</td></tr></table>");
  }

   /**
  ***************************************************************************************
  *
  * This method will draw the end of the main table used for layout and the separator at
  * bottom of the page.
  *
  * @param out the output stream in which to write the html.
  *
  ***************************************************************************************
  **/

  public static void drawEndMainBodyContentWrapper(PrintWriter out)
  {
    out.print("</td></tr></table>");
  }

   /**
  ***************************************************************************************
  *
  * This method will draw footer for main pages.  This call should be made just before
  * a call to drawEndPageContentWrapper()
  *
  * @param out the output stream in which to write the html.
  *
  ***************************************************************************************
  **/

  public static void drawFooter(PrintWriter out)
  {

   // out.print("<table class=\"ftr\">");
   // out.print("<tr><td align=\"center\">");
   // out.print("<font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
   // out.print("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
   // out.print("<font size=\"1\" color=\"#000000\">ForeTees, LLC 2004 All rights reserved.");
   // out.print("</font></td><td>&nbsp;</td></tr></table>");
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

   public static void drawWizardHeader( String pageTitle, String jsOnLoad, String jsOnUnLoad, PrintWriter out)
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
