/***************************************************************************************
 *   ColumnModel:  This class represents the columns to display in a table
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *   04/14/10  Updated the rows & columns array decleration so that they're not using unchecked raw type
 *
 ***************************************************************************************
 */

package com.foretees.client.table;

import java.util.ArrayList;
import java.io.Serializable;


public class ColumnModel implements Serializable {
    
    private static final long serialVersionUID = 1L;

  //*****************************************************
  // Initialize the attributes
  //*****************************************************

  private ArrayList<RowModel> rows = new ArrayList<RowModel>();
  private ArrayList<Column> columns = new ArrayList<Column>();

  public void add(Column theColumn)
  {

    columns.add(theColumn);
  }

  public Column get(int theIndex)
  {

    return (columns.get(theIndex));
  }


  public int size()
  {

    return columns.size();

  }

}