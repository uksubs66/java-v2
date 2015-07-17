/***************************************************************************************
 *   Image:  This class represents an image that can be rendered in an html ui
 *
 *
 *   created: 01/07/2004   jag
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

public class Image implements Serializable {
    
    private static final long serialVersionUID = 1L;

  // Initialize the attributes

  private String alt = "";
  private String name = "";
  private String src = "";
  private String hspace = "";
  private String border = "0";

  /**
  ***************************************************************************************
  *
  * This constructor initializes the image with a name and source location.
  *
  ***************************************************************************************
  **/

  public Image(String theName, String theSrc){

     name = theName;
     src = theSrc;

  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the attribute with a name, source location, and alternate text
  ***************************************************************************************
  **/

  public Image(String theName, String theSrc, String theAlt){

     name = theName;
     src = theSrc;
     alt = theAlt;
  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the alternate text for this image when displayed in the user interface.
  *
  * @param theAlt the string text to use as the alternate text.
  *
  ***************************************************************************************
  **/

  public void setAlt(String theAlt){

    alt = theAlt;
  }

  /**
  ***************************************************************************************
  *
  * Returns the text to be used as the alternate text for this image when displayed in the user
  * interface.
  *
  * @return the text to use as the alternate text.
  *
  *
  ***************************************************************************************
  **/

  public String getAlt(){

    return alt;
  }

  /**
  ***************************************************************************************
  *
  * Sets the name to use to identify this image.
  *
  * @param theName the name to use to identify this image.
  *
  ***************************************************************************************
  **/

  public void setName(String theName){

    name = theName;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name that identifies this image.
  *
  * @return the name to use for this image.
  *
  ***************************************************************************************
  **/

  public String getName(){

    return name;
  }

  /**
  ***************************************************************************************
  *
  * Sets the source location for the image.
  *
  * @param theSrc the source location of the image.
  *
  ***************************************************************************************
  **/

  public void setSrc(String theSrc){

    src = theSrc;
  }

  /**
  ***************************************************************************************
  *
  * Returns the source location for the image
  *
  * @return the source location for the image
  *
  *
  ***************************************************************************************
  **/

  public String getSrc(){

    return src;
  }

  /**
  ***************************************************************************************
  *
  * Sets the horizontal spacing to use for this image when displaying this image in the user interface.
  *
  * @param theHspace the horizontal spacing for this image.
  *
  ***************************************************************************************
  **/

  public void setHspace(String theHspace){

    hspace = theHspace;
  }

  /**
  ***************************************************************************************
  *
  * Returns the horizonal spacing to be used when displaying this image in the user
  * interface.
  *
  * @return the hspace.
  *
  *
  ***************************************************************************************
  **/

  public String getHspace(){

    return hspace;
  }

  /**
  ***************************************************************************************
  *
  * Sets the border size to use when displaying this image in the user interface.
  *
  * @param theBorder the border size for this image.
  *
  ***************************************************************************************
  **/

  public void setBorder(String theBorder){

    border = theBorder;
  }

  /**
  ***************************************************************************************
  *
  * Returns the border size to be used when displaying this attribute in the user
  * interface.
  *
  * @return the size.
  *
  *
  ***************************************************************************************
  **/

  public String getBorder(){

    return border;
  }

}