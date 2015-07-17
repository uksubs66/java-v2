/***************************************************************************************
 *   LetterChooser:  This class will render a component for selecting a letter to
 *                           search by.
 *
 *
 *   created: 1/14/2004   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.misc;

import java.io.*;

public class LetterChooser {

  //*****************************************************
  // Initialize the attributes
  //*****************************************************

  public static void renderWithPartnerList(String header, String subHeader,  String currentAction, String nextAction, String submitUrl, PrintWriter out)
  {
    renderContent(header, subHeader, currentAction, nextAction, submitUrl, out);
    out.println("<td align=\"center\" colspan=\"4\">");
    out.print("<input type=\"submit\" value=\"Partner List\" name=\"buddy\"></td>");
    out.print("</tr></table>");
  }

  public static void renderWithViewAll(String header, String subHeader, String currentAction, String nextAction, String submitUrl, PrintWriter out)
  {
    renderContent(header, subHeader, currentAction, nextAction, submitUrl, out);
    out.println("<td align=\"center\" colspan=\"4\">");
    out.print("<input type=\"submit\" value=\"View All\" name=\"letter\"></td>");
    out.print("</tr></table>");

  }


  public static void render(String header, String subHeader, String currentAction, String nextAction, String submitUrl, PrintWriter out)
  {
    renderContent(header, subHeader, currentAction, nextAction, submitUrl, out);
    out.println("<td align=\"center\" colspan=\"4\">");
    out.print("&nbsp;&nbsp;");
    out.print("</tr></table>");

  }

  private static void renderContent(String header, String subHeader, String currentAction, String nextAction, String submitUrl, PrintWriter out)
  {

    out.print("<input type=\"hidden\" name=\"long\" value=\"long\">");
    out.print("<table align=\"center\" border=\"2\" bgcolor=\"#F5F5DC\">");
    out.print("<tr bgcolor=\"#336633\">");
    out.print("<td colspan=\"6\" align=\"center\">");
    out.print("<font color=\"#ffffff\" size=\"2\"><b>" + header + "</b>");
    out.print("</font></td></tr><tr>");
    out.print("<td colspan=\"6\" align=\"center\"><font size=\"2\">" + subHeader + "</font>");
    out.print("</td></tr>");

    char[] theCharsG1 = {'A', 'B', 'C', 'D', 'E', 'F'};
    String theLettersG1 = new String(theCharsG1);
    char[] theCharsG2 = {'G', 'H', 'I', 'J', 'K', 'L'};
    String theLettersG2 = new String(theCharsG2);
    char[] theCharsG3 = {'M', 'N', 'O', 'P', 'Q', 'R'};
    String theLettersG3 = new String(theCharsG3);
    char[] theCharsG4 = {'S', 'T', 'U', 'V', 'W', 'X'};
    String theLettersG4 = new String(theCharsG4);
    char[] theCharsG5 = {'Y', 'Z', ' ', ' ', ' ', ' '};
    String theLettersG5 = new String(theCharsG5);

    out.print("<tr>");
    drawLetterRow(theCharsG1, currentAction, nextAction, submitUrl, out);
    out.print("</tr>");

    out.print("<tr>");
    drawLetterRow(theCharsG2, currentAction, nextAction, submitUrl, out);
    out.print("</tr>");

    out.print("<tr>");
    drawLetterRow(theCharsG3, currentAction, nextAction, submitUrl, out);
    out.print("</tr>");

    out.print("<tr>");
    drawLetterRow(theCharsG4, currentAction, nextAction, submitUrl, out);
    out.print("</tr>");

    out.print("<tr>");
    drawLetterRow(theCharsG5, currentAction, nextAction, submitUrl, out);

    out.println("<input type=\"hidden\" name=\"letter\">");

  }

  private static void drawLetterRow(char[] theLetters, String currentAction, String nextAction, String submitUrl, PrintWriter out)
  {

    for(int i=0; i<theLetters.length; i++)
    {
      if (theLetters[i] != ' '){
        String searchUrl = "search('" + submitUrl + "', '" + currentAction + "','" + nextAction + "', '" + theLetters[i] + "')";
        out.print("<td align=\"center\"><input type=\"button\" onClick=\"" + searchUrl + ";\" value=\"" + theLetters[i] + "\" name=\"" + theLetters[i] + "\"></td>");
      }

    }
  }
}