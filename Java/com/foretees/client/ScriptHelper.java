/***************************************************************************************
 *   ScriptHelper:  This utility class will help with javascript related issues
 *
 *
 *   created: 10/03/2003   jag
 *
 *
 *   last updated:
 *
 *
 *
 ***************************************************************************************
 */

package com.foretees.client;

import java.io.*;

import com.foretees.common.ProcessConstants;

/**
 ***************************************************************************************
 *
 *  This helper class contains functions for inserting commonly used html such as
 *  stylesheet and javascript.
 *
 ***************************************************************************************
 **/

public class ScriptHelper {

  // Initialize the attributes
  private static String versionId = ProcessConstants.CODEBASE;

  /**
  ***************************************************************************************
  *
  * This method will return a string from the given string that has escaped any special
  * characters that could cause problems when used in such things as javascript.
  *
  * @param theString the string for which to escape characters.
  *
  ***************************************************************************************
  **/

  public static String escapeSpecialCharacters(String theString)
  {

   String sc = "\\\\";
   return theString.replaceAll("'", sc + "'");

  }

  /**
  ***************************************************************************************
  *
  * Generates an html link tag that includes the reference to the default style sheet.
  *
  * @param out the writer to print the html.
  *
  ***************************************************************************************
  **/

  public static void insertHTMLForStyleSheet(PrintWriter out)
  {

   out.println("<link rel=\"stylesheet\" href=\"" + versionId + "web utilities/foretees.css\" type=\"text/css\"></link>");


  }

  /**
  ***************************************************************************************
  *
  * Generates an html script tag that includes the reference to the default js file.
  *
  * @param out the writer to print the html.
  *
  ***************************************************************************************
  **/

  public static void insertHTMLForJavascriptFile(PrintWriter out)
  {

    out.println("<script type=\"text/javascript\" src=\"" + versionId + "web utilities/foretees.js\"></script>");


  }

  /**
  ***************************************************************************************
  *
  * Generates an html script tag and function calls that will preload the images for the tabs
  *
  * @param out the writer to print the html.
  *
  ***************************************************************************************
  **/

  public static void drawPreloadImageScript(String userType, PrintWriter out)
  {
    out.println("<script type=\"text/javascript\"><!--");

    // Preload images for rollover effects.
    String[] images = ImageHelper.getTabImages(userType);

    for (int i=0; i<images.length; i++)
    {
      out.println("preloadImage(\"" + images[i] + "\"");
    }

    out.print("// --></script>");
  }

}