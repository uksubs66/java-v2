/***************************************************************************************
 *   FeedBack:  This class can be used to build a list of messages to display to the user
 *
 *
 *   created: 11/05/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *          4/24/08  Update ArrayList to use String instead of raw types
 *
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.io.*;
import java.util.ArrayList;

/**
 ***************************************************************************************
 *
 *  This class can be used to build a list of messages to display to the user
 *
 ***************************************************************************************
 **/

public class FeedBack {

  //initialize attributes
  private ArrayList <String> messages = new ArrayList<String>(10);
  private boolean state = true;
  private String affectedField = "";

  /**
  ***************************************************************************************
  *
  * Sets if the feedback is positive or negative.  The default is positive.
  *
  * @param theStatus sets the status of the feedback
  *
  *
  ***************************************************************************************
  **/

  public void setPositive(boolean theState)
  {

    state = theState;
  }

  /**
  ***************************************************************************************
  *
  * Returns if the feedback is positive or negative.  The default is positive.
  *
  * @return is the feedback positive.
  *
  *
  ***************************************************************************************
  **/

  public boolean isPositive(){

    return state;
  }

  /**
  ***************************************************************************************
  *
  * Sets the name of the field in the form that was affected or invalid.
  *
  * @param theAffectedField sets the name of the form field.
  *
  *
  ***************************************************************************************
  **/

  public void setAffectedField(String theAffectedField)
  {

    affectedField = theAffectedField;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name of the field in the form that was affected or invalid.
  *
  * @return the name of the affected form field.
  *
  *
  ***************************************************************************************
  **/

  public String getAffectedField(){

    return affectedField;
  }

  /**
  ***************************************************************************************
  *
  * Add a message to the list of messages to display to the user.
  *
  * @param theMessage the message to display to the user.
  *
  *
  ***************************************************************************************
  **/

  public void addMessage(String theMessage){


    messages.add(theMessage);
  }

  /**
  ***************************************************************************************
  *
  * Removes all messages from the list.
  *
  ***************************************************************************************
  **/

  public void clearMessages(){


    messages = new ArrayList<String>(10);
  }

  /**
  ***************************************************************************************
  *
  * Returns the message from the action model at the specified index.
  *
  * @param theIndex the index to use to find the requested action from the action model.
  * @return the action from the specified index in this action model.
  *
  *
  ***************************************************************************************
  **/

  public String get(int theIndex)
  {

    return (String)(messages.get(theIndex));
  }

  /**
  ***************************************************************************************
  *
  * Returns the number of messages that exist in this FeedBack.  A -1 will be returned
  * if the messages are empty.
  *
  * @return the number of messages in the feedback.
  *
  *
  ***************************************************************************************
  **/

  public int size()
  {

    return messages.size();

  }

}
