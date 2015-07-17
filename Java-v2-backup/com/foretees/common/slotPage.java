/***************************************************************************************
 *   slotPage:  Methods to generate slot page html
 *
 *
 *   called by:  Member_slotm
 *
 *   created:    14/12/2011   John K.
 *
 *   last updated:
 *        12/14/2011  Created
 *
 *
 *
 ***************************************************************************************
 */
package com.foretees.common;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

// foretees imports
import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.nameLists;
import com.foretees.common.formUtil;
import com.foretees.common.parmSlotPage;

public class slotPage {

    public static void displaySlotPage(PrintWriter out, parmSlotPage parm) {

        Gson gson_obj = new Gson();

        //
        //  Output slot container.  javascript will build the slot page of passed parameters
        //

        out.println("<div class=\"slot_container\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(parm)) + "\"></div>");


    }
}  // end of class
