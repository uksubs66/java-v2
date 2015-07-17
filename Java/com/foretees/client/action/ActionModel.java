/***************************************************************************************
 *   Option:  This class represents an option in a selection list
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.action;

import java.util.ArrayList;

import com.foretees.client.layout.Separator;

/**
 ***************************************************************************************
 *
 *   This class holds a set of actions that should all be rendered together for a single
 *   purpose. For example a navigation bar or actions for a event.
 *
 ***************************************************************************************
 **/

public class ActionModel {

  // Initialize the attributes

  private ArrayList actions = new ArrayList();
  private String label = "Header label is not initialized";

   /**
  ***************************************************************************************
  *
  * This constructor initializes the action model with a label to use as the header when
  * the actions are rendered in the user interface.
  *
  ***************************************************************************************
  **/

  public void ActionModel(String theLabel)
  {
    label = theLabel;
  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the label for this action when displayed in the user interface.
  *
  * @param theLabel the string text to use as the label.
  *
  ***************************************************************************************
  **/

  public void setLabel(String theLabel)
  {

    label = theLabel;
  }

  /**
  ***************************************************************************************
  *
  * Returns the text to be used as the label for this action when displayed in the user
  * interface.
  *
  * @return the text to use as the label.
  *
  *
  ***************************************************************************************
  **/

  public String getLabel()
  {

    return label;
  }

  /**
  ***************************************************************************************
  *
  * Adds an action to this action model.
  *
  * @param theAction the Action to add to this ActionModel
  *
  ***************************************************************************************
  **/

  public void add(Action theAction)
  {

    actions.add(theAction);
  }

  /**
  ***************************************************************************************
  *
  * Adds a separator to this action model.
  *
  * @param theSeparator the Separator to add to this ActionModel
  *
  ***************************************************************************************
  **/

  public void add(Separator theSeparator)
  {

    actions.add(theSeparator);
  }

  /**
  ***************************************************************************************
  *
  * Returns the action or Separator from the action model at the specified index.
  *
  * @param theIndex the index to use to find the requested action or separator from the action model.
  * @return the action or separator from the specified index in this action model.
  *
  *
  ***************************************************************************************
  **/

  public Object get(int theIndex)
  {

    return actions.get(theIndex);
  }

  /**
  ***************************************************************************************
  *
  * Returns the number of actions that exist in this action model.  A -1 will be returned
  * if the action model is empty.
  *
  * @return the number of actions in the action model.
  *
  *
  ***************************************************************************************
  **/

  public int size()
  {

    return actions.size();

  }

    /**
  ***************************************************************************************
  *
  * Returns the Action that has the given id.
  *
  * @return the Action model, null if a action is not found with that id.
  *
  ***************************************************************************************
  **/

  public Action getAction(String actionId)
  {

    Action action = null;

    for (int i=0; i<actions.size(); i++)
    {

      Action nxtAction = (Action)actions.get(i);

      if (nxtAction.getName() != null){
        if ((nxtAction.getName()).equals(actionId))
        {
          action = nxtAction;
          break;
        }
      }
    }
    return action;

  }

}