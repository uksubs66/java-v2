/***************************************************************************************
 *   HiddenInput:  This class represents a hidden html hidden input field
 *
 *
 *   created: 10/02/2003   jag
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

public class HiddenInput implements Serializable {
    
    private static final long serialVersionUID = 1L;

  // Initialize the attributes

  String value = null;
  String name = null;

  /**
  ***************************************************************************************
  *
  * This constructor initializes the input with a name and value.
  *
  ***************************************************************************************
  **/

  public HiddenInput(String theName, String theValue){

     name = theName;
     value = theValue;

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
  * Sets the text to use as the value for this hidden field.
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
  * Returns the string to be used as the value for this hidden field.
  *
  * @return the string to use as the value.
  *
  *
  ***************************************************************************************
  **/

  public String getValue(){

    return value;
  }


}