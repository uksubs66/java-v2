/***************************************************************************************
 *   Cell:  This class represents a cell in row
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

import javax.servlet.http.*;

import com.foretees.client.StyleSheetConstants;
import com.foretees.client.attribute.Attribute;
import com.foretees.client.action.ActionModel;

import com.foretees.client.attribute.Checkbox;
import com.foretees.client.attribute.DatePicker;
import com.foretees.client.attribute.SelectionList;

import com.foretees.client.layout.Separator;

public class Cell {

  Object content = null;
  String bgColor = null;
  private String styleShtClass = StyleSheetConstants.TABLE_CELL;
  private int colspan = 1;
  private String cellId = "";

  //*****************************************************
  // Initialize the attributes
  //*****************************************************


  public Cell(ActionModel theActionModel){

     content = theActionModel;

  }

  public Cell(String theCellContent){

     content = theCellContent;

  }

  public Cell(Object theCellContent){

     content = theCellContent;

  }

  public Cell(SelectionList theCellContent){

     content = theCellContent;

  }

  public Cell(Separator theCellContent){
     content = theCellContent;

  }

  public Object getContent(){

    return content;

  }

  public String getBackgroundColor(){

    return bgColor;

  }

  public void setBackgroundColor(String theColor){

    bgColor = theColor;

  }

  /**
  ***************************************************************************************
  *
  * Sets the colspan for this cell.
  *
  * @param theColSpan the number of columns to span for this cell.
  *
  ***************************************************************************************
  **/

  public void setColSpan(int theColspan)
  {

    colspan = theColspan;
  }

  /**
  ***************************************************************************************
  *
  * Returns the colspan for this cell.
  *
  * @return the number of columns to span.
  *
  ***************************************************************************************
  **/

  public int getColSpan()
  {

    return colspan;
  }

   /**
  ***************************************************************************************
  *
  * Sets the style sheet class to use for this cell.
  *
  * @param theClass the name of the style sheet class to use for this cell.
  *
  ***************************************************************************************
  **/

  public void setStyleSheetClass(String theClass)
  {

    styleShtClass = theClass;
  }

  /**
  ***************************************************************************************
  *
  * Returns the style sheet class to use for this cell.
  *
  * @return the name of the style sheet class.
  *
  ***************************************************************************************
  **/

  public String getStyleSheetClass()
  {

    return styleShtClass;
  }

  /**
  ***************************************************************************************
  *
  * Sets the string to use to identify this cell.  This id should be unique for all cells
  * when used in side a component with multi cells that will be submitted for processing.
  *
  * @param theId the string to use to identify this cell.
  *
  ***************************************************************************************
  **/

  public void setId(String theId){

    cellId = theId;
  }

  /**
  ***************************************************************************************
  *
  * Returns the string that identifies this cell.
  *
  * @return the cell id.
  *
  ***************************************************************************************
  **/

  public String getId(){

    return cellId;
  }


  public void update(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
  {

    if (content != null)
    {
      if (content instanceof Attribute)
      {
        ((Attribute)content).update(request, response);
      }
      else if (content instanceof SelectionList)
      {
        ((SelectionList)content).update(request, response, out);
      }
      else if (content instanceof DatePicker)
      {
        ((DatePicker)content).update(request, response, out);
      }
      else if (content instanceof TableModel)
      {
        ((TableModel)content).update(request, response, out);
      }
      else if (content instanceof Checkbox)
      {
        ((Checkbox)content).update(request, response);
      }
    }

  }

  /**
  ***************************************************************************************
  *
  * Updates the id of the cells content with the provided value
  *
  * @param newId the new id for the cell and the content.
  *
  ***************************************************************************************
  **/

  public void updateContentId(String newId)
  {

    if (content != null)
    {
      if (content instanceof Attribute)
      {
        ((Attribute)content).setName(newId);
      }
      else if (content instanceof SelectionList)
      {
        ((SelectionList)content).setName(newId);
      }
      else if (content instanceof TableModel)
      {
        //((TableModel)content).setId(newId);
      }
    }

  }

}