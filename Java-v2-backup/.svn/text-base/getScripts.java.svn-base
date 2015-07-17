/***************************************************************************************
 *  getScript:  Gets all core scripts for use by script builder
 *
 *
 *  Called by:     build_scripts php
 *
 *  Revisions: 
 *
 *
 *
 ***************************************************************************************
 */

import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

import com.foretees.common.ProcessConstants;

public class getScripts extends HttpServlet {


   String rev = SystemUtils.REVLEVEL;           
   
 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
     
    resp.setHeader("Pragma","no-cache");
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");
    resp.setDateHeader("Expires",0);
    resp.setContentType("application/json");
    
    Map<String, List<String>> jsMap = new LinkedHashMap<String, List<String>>();
    Map<String, List<String>> cssMap = new LinkedHashMap<String, List<String>>();
    Map<String, Map<String, List<String>>> respMap = new LinkedHashMap<String, Map<String, List<String>>>();
    
    // Core JS
    jsMap.put("core.js", Common_skin.getCoreJsList("", ProcessConstants.SCRIPT_MODE_NEWSKIN, null));
    
    // Core responsive JS
    jsMap.put("core_rwd.js", Common_skin.getCoreJsList("", ProcessConstants.SCRIPT_MODE_RWD, null));
    
    // Core transitional JS
    jsMap.put("core_trans.js", Common_skin.getCoreJsList("", ProcessConstants.SCRIPT_MODE_SLOT_TRANSITIONAL, null));

    // Core accouting JS
    jsMap.put("core_accounting.js", Common_skin.getCoreJsList("", ProcessConstants.SCRIPT_MODE_ACCOUNTING, null));
    
    // Core invoice display JS
    jsMap.put("core_invoice.js", Common_skin.getCoreJsList("", ProcessConstants.SCRIPT_MODE_INVOICE, null));
    
    // Core proshop hybrid JS
    jsMap.put("core_proshop_hybrid.js", Common_skin.getCoreJsList("", ProcessConstants.SCRIPT_MODE_PROSHOP_HYBRID, null));
    
    // Core Login JS
    jsMap.put("core_login.js", Common_skin.getCoreJsList("", ProcessConstants.SCRIPT_MODE_LOGIN, null));
    
    // Core CSS
    cssMap.put("core.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_NEWSKIN, 0, null));
    cssMap.put("core_dining.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_NEWSKIN, ProcessConstants.DINING_ACTIVITY_ID, null));
    cssMap.put("core_flxrez.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_NEWSKIN, 2, null));
    
    // Core responsive CSS
    cssMap.put("core_rwd.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_RWD, 0, null));
    //cssMap.put("core_dining_rwd.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_RWD, ProcessConstants.DINING_ACTIVITY_ID, null));
    //cssMap.put("core_flxrez_rwd.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_RWD, 2, null));
    
    // Core transitional CSS
    cssMap.put("core_trans.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_SLOT_TRANSITIONAL, 0, null));
    cssMap.put("core_dining_trans.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_SLOT_TRANSITIONAL, ProcessConstants.DINING_ACTIVITY_ID, null));
    cssMap.put("core_flxrez_trans.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_SLOT_TRANSITIONAL, 2, null));

    // Core accoutning CSS
    cssMap.put("core_accounting.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_ACCOUNTING, 0, null));
    
    // Core invoice display CSS
    cssMap.put("core_invoice.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_INVOICE, 0, null));
    
    // Core proshop hybrid CSS
    cssMap.put("core_proshop_hybrid.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_PROSHOP_HYBRID, 0, null));
    
    // Core Login CSS
    cssMap.put("core_login.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_LOGIN, 0, null));
    
    respMap.put("js", jsMap);
    respMap.put("css", cssMap);
    
    Gson gson = new Gson();
    
    PrintWriter out = resp.getWriter();
    
    out.print(gson.toJson(respMap));
    
    out.close();
    
 }
 
}