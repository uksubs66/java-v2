/***************************************************************************************
 *   Admin_members:  This servlet will display a links to administer members
 *
 *   called by:  admin_maintop.htm
 *
 *
 *   created: 10/31/2003   JAG
 *
 *   last updated:
 *                 7/01/08  Add Email Report button to Member page.
 *
 *
 *
 ***************************************************************************************
 */


//third party imports
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

//foretees imports
import com.foretees.client.action.Action;
import com.foretees.client.action.ActionHelper;
import com.foretees.client.action.ActionModel;

import com.foretees.client.layout.LayoutHelper;

import com.foretees.common.ProcessConstants;
import com.foretees.common.help.Help;

import com.foretees.member.Member;

/**
***************************************************************************************
*
* This servlet will display the screen to allow the user to pick a letter for the last
* name of the members they want to view
*
***************************************************************************************
**/

public class Admin_members extends HttpServlet {

  //initialize attributes
  private static String versionId = ProcessConstants.CODEBASE;

  /**
  ***************************************************************************************
  *
  * This method will forward the request and response onto the the post method
  *
  ***************************************************************************************
  **/

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    doGet(req, resp);
  }

  /**
  ***************************************************************************************
  *
  * This method will display the page to allow the user to pick a letter for the last name
  * of the users they want to view
  *
  ***************************************************************************************
  **/

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    //
    //  Prevent caching so sessions are not mangled
    //
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

    if (session == null) {

      return;
    }

    //
    //  Build the HTML page (main menu)
    //

    ActionModel pageActions = new ActionModel();

    Action addMember = new Action(ActionHelper.ADD_MEMBER, "Add Member");
    addMember.setUrl("javascript:goTo('" + versionId + "servlet/Admin_addmem')");
    pageActions.add(addMember);

    Action stats = new Action("stats", "Database Report");
    stats.setUrl(versionId + "servlet/Admin_memlist?" + ActionHelper.NEXT_ACTION + "=report");
    pageActions.add(stats);

    Action emails = new Action("emails", "Email Report");
    emails.setUrl(versionId + "servlet/Admin_emailReport");
    pageActions.add(emails);

    Action userHelp = new Action(ActionHelper.HELP, "Help");
    userHelp.setUrl("javascript:openNewWindow('" + versionId + "help/ViewMembersOnlineHelp.html', 'EvntSetupOnlineHelp', '" + Help.WINDOW_SIZE + "')");
    pageActions.add(userHelp);

    out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Members Page"));
    LayoutHelper.drawBeginPageContentWrapper(null, null, out);
    LayoutHelper.drawBeginMainBodyContentWrapper("", pageActions, out);
    drawMainContent(out);
    LayoutHelper.drawEndMainBodyContentWrapper(out);
    LayoutHelper.drawFooter(out);
    LayoutHelper.drawEndPageContentWrapper(out);

    out.flush();

  }


  private void drawMainContent(PrintWriter out)
  {

    out.print("<form action=\"" + versionId + "servlet/Admin_memlist\" method=\"post\" target=\"bot\">");
    out.print("<input type=\"hidden\" name=\"long\" value=\"long\">");
    out.print("<table align=\"center\" border=\"2\" bgcolor=\"#F5F5DC\">");
    out.print("<tr bgcolor=\"#336633\">");
    out.print("<td colspan=\"6\" align=\"center\">");
    out.print("<font color=\"#ffffff\" size=\"2\"><b>Member List</b>");
    out.print("</font></td></tr><tr>");
    out.print("<td colspan=\"6\" align=\"center\"><font size=\"2\">Last name begins with:</font>");
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
    drawLetterRow(theCharsG1, out);
    out.print("</tr>");

    out.print("<tr>");
    drawLetterRow(theCharsG2, out);
    out.print("</tr>");

    out.print("<tr>");
    drawLetterRow(theCharsG3, out);
    out.print("</tr>");

    out.print("<tr>");
    drawLetterRow(theCharsG4, out);
    out.print("</tr>");

    out.print("<tr>");
    drawLetterRow(theCharsG5, out);
    out.print("</tr>");

    out.print("<tr><td align=\"center\" colspan=\"6\">");
    out.print("<font size=\"2\"><input type=\"submit\" value=\"View All\" name=\"letter\">");
    out.print("</tr></td>");


    out.print("</table></form>");

  }

  private static void drawLetterRow(char[] theLetters, PrintWriter out)
  {

    for(int i=0; i<theLetters.length; i++)
    {
      if (theLetters[i] != ' '){
        out.print("<td align=\"center\"><input type=\"submit\" value=\"" + theLetters[i] + "\" name=\"letter\"></td>");
      }

    }
  }

}
