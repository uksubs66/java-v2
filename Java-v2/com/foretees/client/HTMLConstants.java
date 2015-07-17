/***************************************************************************************
 *   HTMLConstants:  This class defines html begin and end tags to be used
 *   throughout renderers
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client;

import java.io.*;

/**
 ***************************************************************************************
 *
 *  This class is a utility that contains constants that represent html snipits.
 *
 ***************************************************************************************
 **/

public class HTMLConstants {

  public static String CMP_STR = "\"";
  // Tag  constants

  public static String A = "<a ";
  public static String E_A = "</a>";
  public static String TBL = "<table ";
  public static String E_TBL = "</table>";
  public static String TR = "<tr ";
  public static String E_TR = "</tr>";
  public static String TH = "<th ";
  public static String E_TH = "</th>";
  public static String TD = "<td ";
  public static String E_TD = "</td> ";
  public static String BR = "<br>";
  public static String HR = "<hr ";
  public static String DIV = "<div ";
  public static String E_DIV = "</div>";
  public static String NON_BREAK_SPACE = "&nbsp;&nbsp;";

  // Attribute constants
  public static String CLASS = "class=\"";
  public static String HREF = "href=\"";
  public static String WIDTH = "width=\"";
  public static String ALT = "alt=\"";
  public static String TITLE = "title=\"";


}
