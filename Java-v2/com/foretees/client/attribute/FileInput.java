/***************************************************************************************
 *   FileInput:  This class represents a html file type input field
 *
 *
 *   created: 02/18/2010   pts
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.attribute;

import java.io.Serializable;

/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly generate a hidden input field.
 *
 ***************************************************************************************
 **/

public class FileInput implements Serializable {
    
    private static final long serialVersionUID = 1L;

  // Initialize the attributes

  String label = "Label not initialized";
  String value = null;
  String name = null;
  String onChange = null;

  /**
  ***************************************************************************************
  *
  * This constructor initializes the input with a name and value.
  *
  ***************************************************************************************
  **/

  public FileInput(String theName, String theLabel){

     name = theName;
     label = theLabel;

  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the label for this checkbox when displayed in the user interface.
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
  * Returns the text to be used as the label for this checkbox when displayed in the user
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
  * Sets the name to use to identify this hidden field.  This name should be unique for all input
  * fields used within a single form.
  *
  * @param theName the name to use to identify this hidden field.
  *
  ***************************************************************************************
  **/

  public void setName(String theName){

    name = theName;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name that identifies this hidden field.
  *
  * @return the name to use for this hidden field.
  *
  ***************************************************************************************
  **/

  public String getName(){

    return name;
  }

  /**
  ***************************************************************************************
  *
  * Returns the string reported as the value for this file input.  This value various by
  * browser and should not be trusted.  Read-only.
  *
  * @return the string reported as the value.
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

}