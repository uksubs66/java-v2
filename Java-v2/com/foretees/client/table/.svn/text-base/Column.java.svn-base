/***************************************************************************************
 *   Column:  This class represents a column in table
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.table;

import java.io.*;
import java.io.Serializable;

public class Column implements Serializable {
    
    private static final long serialVersionUID = 1L;

  //*****************************************************
  // Initialize the attributes
  //*****************************************************

  private String name = "";
  private String label = "Label not initialized";
  private String subLabel = "";

  private String url = "";
  private String tooltip = "";
  private boolean selected = false;


  public Column(String theName, String theLabel, String theUrl, boolean isSelected)
  {
    label = theLabel;
    url = theUrl;
    selected = isSelected;
    name = theName;
  }

  public Column(String theName, String theLabel, String theToolTip, String theUrl)
  {
    label = theLabel;
    url = theUrl;
    selected = false;
    tooltip = theToolTip;
    name = theName;
  }

  public Column(String theName, String theLabel, String theToolTip)
  {

    label = theLabel;
    selected = false;
    tooltip = theToolTip;
    name = theName;
  }

  public Column(String theName, String theLabel)
  {

    label = theLabel;
    name = theName;
  }

  public void setLabel(String theLabel){

    label = theLabel;
  }

  public String getLabel(){

    return label;
  }

  public void setSubLabel(String theSubLabel){

    subLabel = theSubLabel;
  }

  public String getSubLabel(){

    return subLabel;
  }

  public void setUrl(String theUrl){

    url = theUrl;
  }

  public String getUrl(){

    return url;
  }

  public void setToolTip(String theToolTip){

    tooltip = theToolTip;
  }

  public String getToolTip(){

    return tooltip;
  }

  public void setName(String theName){

    name = theName;
  }

  public String getName(){

    return name;
  }

  public void setSelected(boolean isSelected){

    selected = isSelected;
  }

  public boolean isSelected(){

    return selected;
  }
}