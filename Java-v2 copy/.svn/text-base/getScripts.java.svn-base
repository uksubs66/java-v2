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
    jsMap.put("core_rwd.js", Common_skin.getCoreJsList("", 1, null));
    
    // Core CSS
    cssMap.put("core.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_NEWSKIN, 0, null));
    cssMap.put("core_dining.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_NEWSKIN, ProcessConstants.DINING_ACTIVITY_ID, null));
    cssMap.put("core_flxrez.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_NEWSKIN, 2, null));
    
    // Core responsive CSS
    cssMap.put("core_rwd.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_RWD, 0, null));
    cssMap.put("core_dining_rwd.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_RWD, ProcessConstants.DINING_ACTIVITY_ID, null));
    cssMap.put("core_flxrez_rwd.css", Common_skin.getCoreCssList("", ProcessConstants.SCRIPT_MODE_RWD, 2, null));
    
    respMap.put("js", jsMap);
    respMap.put("css", cssMap);
    
    Gson gson = new Gson();
    
    PrintWriter out = resp.getWriter();
    
    out.print(gson.toJson(respMap));
    
    out.close();
    
 }
 
}