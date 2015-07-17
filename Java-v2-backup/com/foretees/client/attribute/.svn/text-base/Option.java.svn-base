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

package com.foretees.client.attribute;

import java.io.*;

/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly construct an html Option for a
 *   html Select component.
 *
 ***************************************************************************************
 **/

public class Option implements Serializable {
    
    private static final long serialVersionUID = 1L;

  // Initialize the attributes

  private String name = "";
  private String value = "";
  private boolean selected = false;

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name and value for the option.
  *
  ***************************************************************************************
  **/

  public Option(String theName, String theValue)
  {
    value = theValue;
    name = theName;
  }

  /**
  ***************************************************************************************
  * This constructor initializes the name and value for the option and also if this option
  * should be selected.
  *
  ***************************************************************************************
  **/

  public Option(String theName, String theValue, boolean isSelected)
  {
    value = theValue;
    name = theName;
    selected = isSelected;
  }


  /**
  ***************************************************************************************
  *
  * Sets the text to use as the display name for this option when displayed in the user interface.
  *
  * @param theLabel the string text to use as the label.
  *
  ***************************************************************************************
  **/

  public void setName(String theName){

    name = theName;

  }

  /**
  ***************************************************************************************
  *
  * Returns the text to be used as the display name for this option when displayed in the user
  * interface.
  *
  * @return the text to use as the label.
  *
  *
  ***************************************************************************************
  **/

  public String getName(){

    return name;
  }

  /**
  ***************************************************************************************
  *
  * Sets the value to use to identify this option.  This value should be unique for all options
  * within a selection list.
  *
  * @param theValue the value to use to identify this option.
  * @see SelectionList
  *
  ***************************************************************************************
  **/

  public void setValue(String theValue){

    value = theValue;

  }

  /**
  ***************************************************************************************
  *
  * Returns the name that identifies this option.
  *
  * @return the url to use for this action.
  *
  ***************************************************************************************
  **/

  public String getValue(){

    return value;
  }

  /**
  ***************************************************************************************
  *
  * Sets whether this option should show as selected in the user interface.
  *
  * @param isSelected indicates if this option should show as selected.
  * @see SelectionList
  *
  ***************************************************************************************
  **/

  public void setSelected(boolean isSelected){

    selected = isSelected;
  }

  /**
  ***************************************************************************************
  *
  * Returns true if this option should be displayed as selected in the user interface.
  *
  * @return true if this option should be selected.
  * @see SelectionList
  *
  ***************************************************************************************
  **/

  public boolean isSelected(){

    return selected;
  }
}