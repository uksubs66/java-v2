/***************************************************************************************
 *   Attribute:  This class represents an attribute of an object
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

import javax.servlet.http.*;

/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly generate an attribute value.
 *
 ***************************************************************************************
 **/

public class Attribute {

  // Initialize the attributes
  public static String VIEW = "view";
  public static String EDIT = "edit";

  protected String label = "Label not initialized";
  protected String value = null;
  protected String name = "";
  protected String mode = VIEW;
  protected String size = "15";
  protected String maxLength = "15";
  protected String helpText = "";

  protected String onChange = "";
  protected String onBlur = "";

  /**
  ***************************************************************************************
  *
  * This constructor initializes the attribute with a label and a string value.
  *
  ***************************************************************************************
  **/

  public Attribute(String theLabel, String theValue){

     label = theLabel;
     value = theValue;

  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the attribute with a name, label, a string value, and a mode.
  *
  ***************************************************************************************
  **/

  public Attribute(String theName, String theLabel, String theValue, String theMode){

     label = theLabel;
     value = theValue;
     mode = theMode;
     name = theName;
  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the label for this attribute when displayed in the user interface.
  *
  * @param theLabel the string text to use as the label.
  *
  ***************************************************************************************
  **/

  public void setLabel(String theLabel){

    label = theLabel;
  }

  /**
  ***************************************************************************************
  *
  * Returns the text to be used as the label for this attribute when displayed in the user
  * interface.
  *
  * @return the text to use as the label.
  *
  *
  ***************************************************************************************
  **/

  public String getLabel(){

    return label;
  }

  /**
  ***************************************************************************************
  *
  * Sets the script to run on in the event of a change of this attribute.
  *
  * @param theScript the script to run.
  *
  ***************************************************************************************
  **/

  public void setOnChange(String theScript){

    onChange = theScript;
  }

  /**
  ***************************************************************************************
  *
  * Returns the script to run in the user interface when this field changes.
  *
  * @return the script to run.
  *
  *
  ***************************************************************************************
  **/

  public String getOnChange(){

    return onChange;
  }


  /**
  ***************************************************************************************
  *
  * Sets the name to use to identify this attribute.  This name should be unique for all atttributes
  * especially when used within edit mode.
  *
  * @param theName the name to use to identify this attribute.
  *
  ***************************************************************************************
  **/

  public void setName(String theName){

    name = theName;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name that identifies this attribute.
  *
  * @return the name to use for this attribute.
  *
  ***************************************************************************************
  **/

  public String getName(){

    return name;
  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the value for this attribute when displayed in the user interface.
  *
  * @param theValue the string to use as the value.
  *
  ***************************************************************************************
  **/

  public void setValue(String theValue){

    value = theValue;
  }

  /**
  ***************************************************************************************
  *
  * Returns the string to be used as the value for this attribute when displayed in the user
  * interface.
  *
  * @return the string to use as the value.
  *
  *
  ***************************************************************************************
  **/

  public String getValue(){

    return value;
  }

  /**
  ***************************************************************************************
  *
  * Sets the mode to use when displaying this attribute in the user interface.
  *
  * @param theValue the string to use as the value.
  *
  ***************************************************************************************
  **/

  public void setMode(String theMode){

    mode = theMode;
  }

  /**
  ***************************************************************************************
  *
  * Returns the mode to be used when displaying this attribute in the user
  * interface.
  *
  * @return the mode.
  *
  *
  ***************************************************************************************
  **/

  public String getMode(){

    return mode;
  }

  /**
  ***************************************************************************************
  *
  * Sets the size to use when displaying this attribute in the user interface when in edit mode.
  *
  * @param theSize the width for the input field.
  *
  ***************************************************************************************
  **/

  public void setSize(String theSize){

    size = theSize;
  }

  /**
  ***************************************************************************************
  *
  * Returns the size to be used when displaying this attribute in the user
  * interface in edit mode.
  *
  * @return the size.
  *
  *
  ***************************************************************************************
  **/

  public String getSize(){

    return size;
  }

   /**
  ***************************************************************************************
  *
  * Sets the maximum length to allow when displaying this attribute in the user interface when in edit mode.
  *
  * @param theMaxLength the maximum length for the input field.
  *
  ***************************************************************************************
  **/

  public void setMaxLength(String theMaxLength){

    maxLength = theMaxLength;
  }

  /**
  ***************************************************************************************
  *
  * Returns the maximum length to be used when displaying this attribute in the user
  * interface in edit mode.
  *
  * @return the maximum length.
  *
  *
  ***************************************************************************************
  **/

  public String getMaxLength(){

    return maxLength;
  }

    /**
  ***************************************************************************************
  *
  * Sets the help text to use when displaying this attribute in the ui.
  *
  * @param theHelpText the text to display.
  *
  ***************************************************************************************
  **/

  public void setHelpText(String theHelpText){

    helpText = theHelpText;
  }

  /**
  ***************************************************************************************
  *
  * Returns the help text to use when displaying this attribute in the user
  * interface in edit mode.
  *
  * @return the help text.
  *
  *
  ***************************************************************************************
  **/

  public String getHelpText(){

    return helpText;
  }

  /**
  ***************************************************************************************
  *
  * Updates the value of this attribute with the data from the form.
  *
  * @param request the request object that contains the posted form data
  * @param response the response object
  *
  *
  ***************************************************************************************
  **/

  public void update(HttpServletRequest request, HttpServletResponse response)
  {

    if (getMode() == EDIT)
    {
      String newValue = request.getParameter(getName());

      if (newValue != null)
      {
        value = newValue;
      }
    }

  }

}