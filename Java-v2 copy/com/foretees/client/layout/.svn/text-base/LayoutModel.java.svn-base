/***************************************************************************************
 *   LayoutModel:  This class represents an html table used to layout elements
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *   04/14/10  Updated the rows array decleration so that it's not using unchecked raw type
 *
 ***************************************************************************************
 */

package com.foretees.client.layout;

import java.io.*;
import javax.servlet.http.*;
import java.util.ArrayList;

import com.foretees.client.table.Column;
import com.foretees.client.table.RowModel;


public class LayoutModel extends RowModel {

  //*****************************************************
  // Initialize the attributes
  //*****************************************************

  private ArrayList<RowModel> rows = new ArrayList<RowModel>();

  private int numColumns = 1;

   /**
  ***************************************************************************************
  *
  * Returns the number of rows for this form.
  *
  * @return the number of rows.
  *
  ***************************************************************************************
  **/

  public int numRows()
  {

    return rows.size();

  }

  /**
  ***************************************************************************************
  *
  * Sets the number of columns for this form.
  *
  * @param theNumColumns the number of columns for this form.
  *
  ***************************************************************************************
  **/

  public void setNumColumns(int theNumColumns)
  {

    numColumns = theNumColumns;
  }

  /**
  ***************************************************************************************
  *
  * Returns the number of columns for this form.
  *
  * @return the number of columns.
  *
  ***************************************************************************************
  **/

  public int getNumColumns()
  {

    return numColumns;
  }

  public void addRow(RowModel theRow)
  {

    rows.add(theRow);
  }

  public RowModel getRow(int theIndex)
  {

    return (RowModel)(rows.get(theIndex));
  }

  /**
  ***************************************************************************************
  *
  * Returns the row that has the given row id.
  *
  * @return the row model, null if a row is not found with that id.
  *
  ***************************************************************************************
  **/

  public RowModel getRow(String rowId)
  {

    RowModel row = null;
    for (int i=0; i<rows.size(); i++)
    {

      RowModel nxtRow = (RowModel)rows.get(i);
      if ((nxtRow.getId()).equals(rowId))
      {
        row = nxtRow;
        break;
      }
    }
    return row;

  }

  public int size()
  {

    return rows.size();

  }

  /**
  ***************************************************************************************
  *
  * Updates the table with the table data from the http request.
  *
  * @param request the HttpServletRequest that contains the form data
  * @param response the HttpServletResponse
  * @param out the print writer
  *
  *
  ***************************************************************************************
  **/

  public void update(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
  {


    for (int i=0; i<rows.size(); i++)
    {

      RowModel row = this.getRow(i);

      if (row != null)
      {
        row.update(request, response, out);
      }


    }
  }

}