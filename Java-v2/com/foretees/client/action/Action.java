/***************************************************************************************
 *   Action:  This class represents an html url or button
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *           01/07/2004   JAG  Added methods to support mouse over and mouse out events
 *
 ***************************************************************************************
 */

package com.foretees.client.action;

import com.foretees.client.attribute.Image;
import java.io.*;


/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly construct a url.
 *
 ***************************************************************************************
 **/

public class Action implements Serializable {
    
    private static final long serialVersionUID = 1L;

  // Initialize the attributes

  private String name = "";
  private String label = "Label not initialized";
  private String url = "Url not initialized";
  private String tooltip = "";
  private String target = "";
  private String method = "GET";
  private boolean selected = false;
  private Image image = null;
  private String onMouseOver = "";
  private String onMouseOut = "";

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name and label for the action as well as whether
  * the action is selected.
  *
  ***************************************************************************************
  **/

  public Action(String theName, String theLabel, String theUrl, boolean isSelected)
  {
    label = theLabel;
    url = theUrl;
    selected = isSelected;
    name = theName;
  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name and label for the action.
  *
  ***************************************************************************************
  **/

  public Action(String theName, String theLabel)
  {
    label = theLabel;
    name = theName;
  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name, label, tooltip, and url.
  *
  ***************************************************************************************
  **/

  public Action(String theName, String theLabel, String theToolTip, String theUrl)
  {
    label = theLabel;
    url = theUrl;
    selected = false;
    tooltip = theToolTip;
    name = theName;
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

  public void setLabel(String theLabel){

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

  public String getLabel(){

    return label;
  }

  /**
  ***************************************************************************************
  *
  * Sets the url to use when displaying this action in the user interface.  This can be an
  * absolute or relative url.
  *
  * Example:<br><br>
  *
  *   /com/foretees/onlinehelp.html<br><br>
  *
  *     or<br><br>
  *
  *   http://www.foretees.com
  *
  * @param theUrl the string url
  *
  ***************************************************************************************
  **/

  public void setUrl(String theUrl){

    url = theUrl;
  }

  /**
  ***************************************************************************************
  *
  * Returns the string to be used as the url for this action when displayed in the user
  * interface.
  *
  * @return the url to use for this action.
  *
  ***************************************************************************************
  **/

  public String getUrl(){

    return url;
  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the tooltip for this action when displayed in the user
  * interface.
  *
  * @param theToolTip the text to use for this action.
  *
  ***************************************************************************************
  **/

  public void setToolTip(String theToolTip){

    tooltip = theToolTip;
  }

  /**
  ***************************************************************************************
  *
  * Returns the string to be used as the tooltip for this action when displayed in the user
  * interface.
  *
  * @return the tooltip to use for this action.
  *
  ***************************************************************************************
  **/

  public String getToolTip(){

    return tooltip;
  }

  /**
  ***************************************************************************************
  *
  * Sets the name to use to identify this action.  This name should be unique for all actions
  * especially when used within an ActionModel.
  *
  * @param theName the name to use to identify this action.
  * @see ActionModel
  *
  ***************************************************************************************
  **/

  public void setName(String theName){

    name = theName;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name that identifies this action.
  *
  * @return the url to use for this action.
  *
  ***************************************************************************************
  **/

  public String getName(){

    return name;
  }

  /**
  ***************************************************************************************
  *
  * Sets the name of the browser window name to use as the target executing this action.
  *
  * @param theTarget the name of the browser window which should be the target for this action.
  *
  ***************************************************************************************
  **/

  public void setTarget(String theTarget){

    target = theTarget;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name of the browser window to target when this action is executed
  *
  * @return the name of the browser window.
  *
  ***************************************************************************************
  **/

  public String getTarget(){

    return target;
  }

  /**
  ***************************************************************************************
  *
  * Sets the form method to be used when this action is executed.  The default value is GET if nothing is specified.
  *
  * <br><br>The valid values are GET and POST.
  *
  * @param theMethod the method to use for this action.
  *
  ***************************************************************************************
  **/

  public void setMethod(String theMethod){

    method = theMethod;
  }

  /**
  ***************************************************************************************
  *
  * Returns the method to be used when executing this action.
  *
  * @return the method name.
  *
  ***************************************************************************************
  **/

  public String getMethod(){

    return method;
  }

  /**
  ***************************************************************************************
  *
  * Sets whether this action should show as selected in the user interface.  This is usually
  * relevant when the action is used within an ActionModel, for example a navigation bar.
  *
  * @param isSelected indicates if this action should show as selected.
  * @see ActionModel
  *
  ***************************************************************************************
  **/

  public void setSelected(boolean isSelected){

    selected = isSelected;
  }

  /**
  ***************************************************************************************
  *
  * Returns true if this action should be displayed as selected in the user interface.
  *
  * @return true if this action should be selected.
  * @see ActionModel
  *
  ***************************************************************************************
  **/

  public boolean isSelected(){

    return selected;
  }

  /**
  ***************************************************************************************
  *
  * Sets the image to use for this action when displayed in the user interface.
  *
  * @param theImage the image to use for this action
  *
  ***************************************************************************************
  **/

  public void setImage(Image theImage){

    image = theImage;
  }

  /**
  ***************************************************************************************
  *
  * Returns the image to use for this action when displayed in the user interface.
  *
  * @return the image for this action
  *
  *
  ***************************************************************************************
  **/

  public Image getImage(){

    return image;
  }

  /**
  ***************************************************************************************
  *
  * Sets the string to use for the onMouseOut event for this action
  *
  * @param theOnMouseOut the string to use for the onMouseOut event
  *
  ***************************************************************************************
  **/

  public void setOnMouseOut(String theOnMouseOut){

    onMouseOut = theOnMouseOut;
  }

  /**
  ***************************************************************************************
  *
  * Returns the string to use for the onMouseOut event for this action
  *
  * @return the string to use for the onMouseOut event.
  *
  ***************************************************************************************
  **/

  public String getOnMouseOut(){

    return onMouseOut;
  }

  /**
  ***************************************************************************************
  *
  * Sets the string to use for the onMouseOver event for this action
  *
  * @param theOnMouseOver the string to use for the onMouseOver event
  *
  ***************************************************************************************
  **/

  public void setOnMouseOver(String theOnMouseOver){

    onMouseOver = theOnMouseOver;
  }

  /**
  ***************************************************************************************
  *
  * Returns the string to use for the onMouseOver event for this action
  *
  * @return the string to use for the onMouseOver event.
  *
  ***************************************************************************************
  **/

  public String getOnMouseOver(){

    return onMouseOver;
  }
}
