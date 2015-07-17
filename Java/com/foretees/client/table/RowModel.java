/***************************************************************************************
 *   RowModel:  This class represents a row in table
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *      04/14/10  Updated the rows array decleration so that it's not using unchecked raw type
 *
 ***************************************************************************************
 */

package com.foretees.client.table;

import java.io.*;
import java.util.ArrayList;

import javax.servlet.http.*;

import com.foretees.client.action.ActionModel;
import com.foretees.client.attribute.Checkbox;
import com.foretees.client.attribute.Attribute;
import com.foretees.client.attribute.FileInput;
import com.foretees.client.attribute.SelectionList;
import com.foretees.client.misc.NameSelector;

public class RowModel {

  //*****************************************************
  // Initialize the attributes
  //*****************************************************

  private ArrayList<Cell> cells = new ArrayList<Cell>();
  private String rowId = "";


  public void add(String theCellContent){

    add(theCellContent, "", 1);

  }

  public void add(String theCellContent, String styleSheetClass){

    add(theCellContent, styleSheetClass, 1);

  }

  public void add(String theCellContent, String styleSheetClass, int colspan){


    Cell cell = new Cell(theCellContent);
    cell.setStyleSheetClass(styleSheetClass);
    cell.setColSpan(colspan);
    cells.add(cell);

  }


  public void add(Checkbox theCellContent, String styleSheetClass){

    add(theCellContent, styleSheetClass, 1);

  }

  public void add(Checkbox theCellContent, String styleSheetClass, int colspan){


    Cell cell = new Cell(theCellContent);
    cell.setStyleSheetClass(styleSheetClass);
    cell.setColSpan(colspan);
    cells.add(cell);

  }

  public void add(SelectionList theCellContent){

    add(theCellContent, "");

  }

  public void add(SelectionList theCellContent, String styleSheetClass){

    Cell cell = new Cell(theCellContent);
    cell.setId(theCellContent.getName());
    cell.setStyleSheetClass(styleSheetClass);
    cells.add(cell);

  }


  public void add(Cell theCell){

    cells.add(theCell);

  }

  public void add(ActionModel theActionModel){

    Cell cell = new Cell(theActionModel);
    cells.add(cell);

  }

  public void add(Attribute theAttribute){

    add(theAttribute, "");

  }

  public void add(Attribute theCellContent, String styleSheetClass){

    Cell cell = new Cell(theCellContent);
    cell.setId(theCellContent.getName());
    cell.setStyleSheetClass(styleSheetClass);
    cells.add(cell);

  }

  public void add(Attribute theCellContent, String styleSheetClass, int colSpan){

    Cell cell = new Cell(theCellContent);
    cell.setId(theCellContent.getName());
    cell.setStyleSheetClass(styleSheetClass);
    cell.setColSpan(colSpan);
    cells.add(cell);

  }

  public void add(FileInput theCellContent, String styleSheetClass, int colSpan){

    Cell cell = new Cell(theCellContent);
    cell.setId(theCellContent.getName());
    cell.setStyleSheetClass(styleSheetClass);
    cell.setColSpan(colSpan);
    cells.add(cell);

  }

  public void add(TableModel theTable){

    Cell cell = new Cell(theTable);
    cells.add(cell);

  }

  public void add(TableModel theTable, String styleSheetClass, int colSpan){

    Cell cell = new Cell(theTable);
    cell.setStyleSheetClass(styleSheetClass);
    cell.setColSpan(colSpan);
    cells.add(cell);

  }

  public void add(NameSelector theSelector){

    Cell cell = new Cell(theSelector);
    cells.add(cell);

  }

  public void add(String cellId, NameSelector theSelector){

    Cell cell = new Cell(theSelector);
    cell.setId(cellId);
    cells.add(cell);

  }

  public Cell get(int theIndex){

   Object content = cells.get(theIndex);

   if (content instanceof Cell)
   {
      return (Cell)content;
   }
   else
   {
      return (new Cell(content));
   }

  }

  /**
  ***************************************************************************************
  *
  * Sets the string to use to identify this row.  This id should be unique for all rows
  * when used in side a component with multi rows that will be submitted for processing.
  *
  * @param theId the string to use to identify this row.
  *
  ***************************************************************************************
  **/

  public void setId(String theId){

    rowId = theId;
  }

  /**
  ***************************************************************************************
  *
  * Returns the string that identifies this row.
  *
  * @return the row id.
  *
  ***************************************************************************************
  **/

  public String getId(){

    return rowId;
  }

  public int size(){

    return cells.size();

  }

  /**
  ***************************************************************************************
  *
  * Returns the cell that has the given cell id.
  *
  * @return the cell model, null if a cell is not found with that id.
  *
  ***************************************************************************************
  **/

  public Cell getCell(String cellId)
  {

    Cell cell = null;

    for (int i=0; i<cells.size(); i++)
    {

      Cell nxtCell = cells.get(i);

      if (nxtCell.getId() != null){
        if ((nxtCell.getId()).equals(cellId))
        {
          cell = nxtCell;
          break;
        }
      }
    }
    return cell;

  }

   /**
  ***************************************************************************************
  *
  * Updates the cells in this row values in the form data.
  *
  * @param request the http request that contains the form data
  * @param response the response object for writing exceptions
  * @param out the writer to print any data.
  *
  ***************************************************************************************
  **/

  public void update(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
  {
    for (int j=0; j<this.size(); j++)
    {
      Cell cell = this.get(j);

      if (cell != null){
         cell.update(request, response, out);
      }

    }
  }


}