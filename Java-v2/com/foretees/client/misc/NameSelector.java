/***************************************************************************************
 *   NameSelector:  This class will render a component for selecting displaying a list of members
 *                  by last name
 *
 *
 *   created: 1/19/2004   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.misc;

import java.io.*;
import com.foretees.client.misc.LetterChooser;
import com.foretees.client.attribute.SelectionList;
import com.foretees.client.attribute.SelectionListRenderer;


public class NameSelector implements Serializable {
    
    private static final long serialVersionUID = 1L;

  //*****************************************************
  // Initialize the attributes
  //*****************************************************

  private String selectorName = "";
  private String submitUrl = "";
  private String currentAction = "";
  private String nextAction = "";
  private String onClick = "";
  private String[] names = new String[0];
  private SelectionList selectionListWithNames = null;



  public NameSelector(String theSelectorName, String theSubmitUrl, String[] theNames)
  {
    selectorName = theSelectorName;
    submitUrl = theSubmitUrl;
    names = theNames;
  }

  /**
  ***************************************************************************************
  *
  * Sets the name to use to identify this name selector.
  *
  * @param theSelectorName the name to use to identify this form.
  *
  ***************************************************************************************
  **/

  public void setSelectorName(String theSelectorName)
  {

    selectorName = theSelectorName;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name that identifies this name selector.
  *
  * @return the name.
  *
  ***************************************************************************************
  **/

  public String getSelectorName()
  {

    return selectorName;
  }

  /**
  ***************************************************************************************
  *
  * Sets the selection list to use for displaying the names
  *
  * @param theSelectionList the selection list that contains the names to choose from.
  *
  ***************************************************************************************
  **/

  public void setSelectionList(SelectionList theSelectionList)
  {

    selectionListWithNames = theSelectionList;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name that identifies this name selector.
  *
  * @return the name.
  *
  ***************************************************************************************
  **/

  public SelectionList getSelectionList()
  {

    return selectionListWithNames;
  }


  /**
  ***************************************************************************************
  *
  * Sets the submit url to use for this name selector.
  *
  * @param theSubmitUrl the url to use for submitting.
  *
  ***************************************************************************************
  **/

  public void setSubmitUrl(String theSubmitUrl)
  {

    submitUrl = theSubmitUrl;
  }

  /**
  ***************************************************************************************
  *
  * Returns the url to use when submitting this name selector.
  *
  * @return the url.
  *
  ***************************************************************************************
  **/

  public String getSubmitUrl()
  {

    return submitUrl;
  }

  /**
  ***************************************************************************************
  *
  * Sets the names to use for this name selector.
  *
  * @param names the names to display.
  *
  ***************************************************************************************
  **/

  public void setNames(String[] theNames)
  {

    names = theNames;
  }

  /**
  ***************************************************************************************
  *
  * Returns the names to use when populating the name selector.
  *
  * @return the names.
  *
  ***************************************************************************************
  **/

  public String[] getNames()
  {

    return names;
  }

  /**
  ***************************************************************************************
  *
  * Sets the current action, used for setting the currentAction request parameter in javascript..
  *
  * @param theCurrentAction the name of the current action.
  *
  ***************************************************************************************
  **/

  public void setCurrentAction(String theCurrentAction)
  {

    currentAction = theCurrentAction;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name for the currentAction to use for the request.
  *
  * @return the action name.
  *
  ***************************************************************************************
  **/

  public String getCurrentAction()
  {

    return currentAction;
  }

  /**
  ***************************************************************************************
  *
  * Sets the next action, used for setting the nextAction request parameter in javascript..
  *
  * @param theNextAction the name of the next action.
  *
  ***************************************************************************************
  **/

  public void setNextAction(String theNextAction)
  {

    nextAction = theNextAction;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name for the nextAction to use for the request.
  *
  * @return the action name.
  *
  ***************************************************************************************
  **/

  public String getNextAction()
  {

    return nextAction;
  }

  /**
  ***************************************************************************************
  *
  * Sets the function to execute upon the onClick event for this component
  *
  * @param theFunction the function to execute.
  *
  ***************************************************************************************
  **/

  public void setOnClick(String theFunction)
  {

    onClick = theFunction;
  }

  /**
  ***************************************************************************************
  *
  * Returns the function to use when clicking on the list.
  *
  * @return the function.
  *
  ***************************************************************************************
  **/

  public String getOnClick()
  {

    return onClick;
  }

  public void render(PrintWriter out)
  {

    out.println("<table align=\"center\" width=\"100%\"><tr>");
    if (names != null || selectionListWithNames != null)
    {
      out.println("<td>");
      //if there was a letter selected, then we need to display the names from the query
      if (selectionListWithNames == null)
      {

        selectionListWithNames = new SelectionList("listOfNames", "", names, "");
      }
      selectionListWithNames.setOnClick(onClick);
      selectionListWithNames.setSize("9");
      selectionListWithNames.setAlwaysShowAsList(true);
      String header = selectionListWithNames.getLabel();
      selectionListWithNames.setLabel("");

      out.print("<table align=\"center\" border=\"2\" bgcolor=\"#F5F5DC\">");
      out.print("<tr bgcolor=\"#336633\">");
      out.print("<td colspan=\"6\" align=\"center\">");
      out.print("<font color=\"#ffffff\" size=\"2\"><b>Name List</b>");
      out.print("</font></td></tr><tr>");
      out.print("<td colspan=\"6\" align=\"center\"><font size=\"2\">Click on a name to add</font>");
      out.print("</td></tr><tr><td>");

      SelectionListRenderer.render(selectionListWithNames, out);

      out.println("</td></tr></table>");



      out.println("</td>");

    }

    out.println("<td>");

    LetterChooser chooser = new LetterChooser();

    chooser.render("Member List", "Last name begins with:", currentAction, nextAction, submitUrl, out);

    out.println("</td></tr></table>");

    out.flush();


  }

}