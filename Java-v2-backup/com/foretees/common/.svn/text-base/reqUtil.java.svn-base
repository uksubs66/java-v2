/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;


import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
//import java.util.regex.*;
import java.sql.*;
import javax.servlet.http.*;
import org.apache.commons.lang.*;
/**
 *
 * @author Owner
 */
public class reqUtil {
    
    
  /**
  * getSessionString - returns string parameter from session, or default
  *
  * @param req or session (request or session object).
  * @param param name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static String getSessionString(HttpServletRequest req, String param, String def){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getSessionString(session, param, def);
 }
 
  public static String getSessionString(HttpSession session, String param, String def) {

     Object attr = null;
     
     if(session != null){
         attr = session.getAttribute(param);
         if(attr != null && attr instanceof String){
             return (String) attr;
         }
     }
     return def;
 }
  
    /**
  * getSessionBoolean - returns string parameter from session, or default
  *
  * @param req or session (request or session object).
  * @param param name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Boolean getSessionBoolean(HttpServletRequest req, String param, Boolean def){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getSessionBoolean(session, param, def);
 }
 
  public static Boolean getSessionBoolean(HttpSession session, String param, Boolean def) {

     Object attr = null;
     
     if(session != null){
         attr = session.getAttribute(param);
         if(attr != null && attr instanceof Boolean){
             return (Boolean) attr;
         }
     }
     return def;
 }
  
   /**
  * getSessionInteger - returns string parameter from session, or default
  *
  * @param req or session (request or session object).
  * @param param name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Integer getSessionInteger(HttpServletRequest req, String param, Integer def){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getSessionInteger(session, param, def);
 }
 
  public static Integer getSessionInteger(HttpSession session, String param, Integer def) {

     Object attr = null;
     
     if(session != null){
         attr = session.getAttribute(param);
         if(attr != null && attr instanceof Integer){
             return (Integer) attr;
         }
     }
     return def;
 }
  
    /**
  * getSessionIntegerFromString - returns string parameter from session, or default
  *
  * @param req or session (request or session object).
  * @param param name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Integer getSessionIntegerFromString(HttpServletRequest req, String param, Integer def){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getSessionIntegerFromString(session, param, def);
 }
 
  public static Integer getSessionIntegerFromString(HttpSession session, String param, Integer def) {

     String attr = getSessionString(session, param, null);
     Integer result = def;
     if(attr != null){
         try{
             result = Integer.parseInt(attr);
         }catch(Exception e){
             result = def;
         }
     }
     return result;
 }
  
  /**
  * getSessionLong - returns string parameter from session, or default
  *
  * @param req or session (request or session object).
  * @param param name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Long getSessionLong(HttpServletRequest req, String param, Long def){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getSessionLong(session, param, def);
 }
 
  public static Long getSessionLong(HttpSession session, String param, Long def) {
      
     Object attr = null;
     
     if(session != null){
         attr = session.getAttribute(param);
         if(attr != null && attr instanceof Long){
             return (Long) attr;
         }
     }
     return def;
 }
  
   /**
  * getSessionObject - returns string parameter from session, or default
  *
  * @param req or session (request or session object).
  * @param param name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Object getSessionObject(HttpServletRequest req, String param, Object def){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getSessionObject(session, param, def);
 }
 
  public static Object getSessionObject(HttpSession session, String param, Object def) {

     Object attr = null;
     
     if(session != null){
         attr = session.getAttribute(param);
         if(attr != null){
             return (Object) attr;
         }
     }
     return def;
 }
  
 /**
  * getRequestString - returns string attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static String getRequestString(HttpServletRequest req, String param, String def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof String){
             return (String) attr;
         }
     }
     return def;
 }
  
 /**
  * getRequestInteger - returns integer attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Integer getRequestInteger(HttpServletRequest req, String param, Integer def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof Integer){
             return (Integer) attr;
         }
     }
     return def;
 }
  
  /**
  * getRequestLong - returns long attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Long getRequestLong(HttpServletRequest req, String param, Long def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof Long){
             return (Long) attr;
         }
     }
     return def;
 }
  
  /**
  * getRequestBoolean - returns boolean attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Boolean getRequestBoolean(HttpServletRequest req, String param, Boolean def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof Boolean){
             return (Boolean) attr;
         }
     }
     return def;
 }
  
    /**
  * getRequestStringBuilder - returns StringBuilder attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static StringBuilder getRequestStringBuilder(HttpServletRequest req, String param, StringBuilder def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof StringBuilder){
             return (StringBuilder) attr;
         }
     }
     return def;
 }
  
    /**
  * getRequestTimeZone - returns object attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static TimeZone getRequestTimeZone(HttpServletRequest req, String param, TimeZone def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof TimeZone){
             return (TimeZone) attr;
         }
     }
     return def;
 }
  
  /**
  * getRequestObject - returns object attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Object getRequestObject(HttpServletRequest req, String param, Object def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null){
             return (Object) attr;
         }
     }
     return def;
 }
  
    /**
  * getRequestMap - returns object attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Map getRequestMap(HttpServletRequest req, String param, Map def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof Map){
             return (Map) attr;
         }
     }
     return def;
 }
  
   /**
  * getRequestConnection - returns object attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Connection getRequestConnection(HttpServletRequest req, String param, Connection def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof Connection){
             return (Connection) attr;
         }
     }
     return def;
 }
  
     /**
  * getRequestParmClub - returns object attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static parmClub getRequestParmClub(HttpServletRequest req, String param, parmClub def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof parmClub){
             return (parmClub) attr;
         }
     }
     return def;
 }
  

   /**
  * getParameterString - returns string attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
     public static String getParameterString(HttpServletRequest req, String param, String def) {

         Object p = null;
         if(req != null){
             p = req.getParameter(param);
             if(p == null){
                return def;
            }
             if(p != null && p instanceof String){
                 return (String) p;
             }
         }
         return def;
     }
  
    /**
  * getParameterInteger - returns integer attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Integer getParameterInteger(HttpServletRequest req, String param, Integer def) {

        String p = null;
        if (req != null) {
            p = req.getParameter(param);
            if(p == null){
                return def;
            }
            Integer r = def;
            try {
                r = Integer.parseInt(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }
    
     /**
  * getParameterLong - returns long attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Long getParameterLong(HttpServletRequest req, String param, Long def) {

        String p = null;
        if (req != null) {
            p = req.getParameter(param);
            if(p == null){
                return def;
            }
            Long r = def;
            try {
                r = Long.parseLong(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }
    
    /**
  * getParameterBoolean - returns long attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Boolean getParameterBoolean(HttpServletRequest req, String param, Boolean def) {

        String p = null;
        if (req != null) {
            return parseBoolean(req.getParameter(param), def);
        }
        return def;
    }
    
    /**
  * getParameterFloat - returns Float attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Float getParameterFloat(HttpServletRequest req, String param, Float def) {

        String p = null;
        if (req != null) {
            p = req.getParameter(param);
            if(p == null){
                return def;
            }
            Float r = def;
            try {
                r = Float.parseFloat(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }
    
    

   /**
  * getQueryString - returns string attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
     public static String getQueryString(HttpServletRequest req, String param, String def) {

         Object p = null;
         if(req != null){
             p = getQuery(req, param);
             if(p == null){
                return def;
            }
             if(p != null && p instanceof String){
                 return (String) p;
             }
         }
         return def;
     }
  
    /**
  * getQueryInteger - returns integer attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Integer getQueryInteger(HttpServletRequest req, String param, Integer def) {

        String p = null;
        if (req != null) {
            p = getQuery(req, param);
            if(p == null){
                return def;
            }
            Integer r = def;
            try {
                r = Integer.parseInt(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }
    
     /**
  * getQueryLong - returns long attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Long getQueryLong(HttpServletRequest req, String param, Long def) {

        String p = null;
        if (req != null) {
            p = getQuery(req, param);
            if(p == null){
                return def;
            }
            Long r = def;
            try {
                r = Long.parseLong(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }
    
    /**
  * getQueryBoolean - returns long attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Boolean getQueryBoolean(HttpServletRequest req, String param, Boolean def) {

        String p = null;
        if (req != null) {
            return parseBoolean(getQuery(req, param), def);
        }
        return def;
    }
    
    
    
    
   /**
  * getHeaderString - returns string attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
     public static String getHeaderString(HttpServletRequest req, String param, String def) {

         Object p = null;
         if(req != null){
             p = req.getHeader(param);
             if(p == null){
                return def;
            }
             if(p != null && p instanceof String){
                 return (String) p;
             }
         }
         return def;
     }
  
    /**
  * getHeaderInteger - returns integer attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Integer getHeaderInteger(HttpServletRequest req, String param, Integer def) {

        String p = null;
        if (req != null) {
            p = req.getHeader(param);
            if(p == null){
                return def;
            }
            Integer r = def;
            try {
                r = Integer.parseInt(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }
    
     /**
  * getHeaderLong - returns long attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Long getHeaderLong(HttpServletRequest req, String param, Long def) {

        String p = null;
        if (req != null) {
            p = req.getHeader(param);
            if(p == null){
                return def;
            }
            Long r = def;
            try {
                r = Long.parseLong(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }
    
    /**
  * getHeaderBoolean - returns long attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Boolean getHeaderBoolean(HttpServletRequest req, String param, Boolean def) {

        String p = null;
        if (req != null) {
            return parseBoolean(p = req.getHeader(param), def);
        }
        return def;
    }
    
    
    public static Boolean parseBoolean(String string, Boolean def){
        
        String p = null;
        if (string != null) {
            p = string;
            if(p.equals("1")){
                return true;
            }
            if(p.equals("0")){
                return false;
            }
            if(p.equalsIgnoreCase("Yes")){
                return true;
            }
            if(p.equalsIgnoreCase("No")){
                return false;
            }
            if(p.equalsIgnoreCase("Y")){
                return true;
            }
            if(p.equalsIgnoreCase("N")){
                return false;
            }
            if(p.equalsIgnoreCase("True")){
                return true;
            }
            if(p.equalsIgnoreCase("False")){
                return false;
            }
            Boolean r = def;
            try {
                r = Boolean.parseBoolean(p);
            } catch (NumberFormatException nfe) {
                r = def;
            }
            return r;
        }
        return def;
        
    }
    
    /**
  * getQueryFloat - returns Float attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Float getQueryFloat(HttpServletRequest req, String param, Float def) {

        String p = null;
        if (req != null) {
            p = getQuery(req, param);
            if(p == null){
                return def;
            }
            Float r = def;
            try {
                r = Float.parseFloat(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }

    /**
     * getBody
     *
     * @param req (request object).
     *
     * @return result.
     **/
    public static StringBuilder getRequestBody(HttpServletRequest req) {

        StringBuilder result = new StringBuilder();

        try {
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
        }
        return result;
    }
    
    public static String getQuery(HttpServletRequest req, String key){
        return getQueryMap(req).get(key);
    }
    
    public static String urlDecode(String string){
        String result = null;
        try{
            result = URLDecoder.decode(string, "UTF-8");
        }catch(Exception e){
            result = null;
        }
        return result;
    }

    public static Map<String, String> getQueryMap(HttpServletRequest req) {
        
        Map<String, String> queryMap = null;
        
        try{
            queryMap = (Map<String, String>) req.getAttribute("req_queryMap");
        } catch(Exception e) {
            
        }
        
        if (queryMap == null) {
            queryMap = new LinkedHashMap<String, String>();
            String query = req.getQueryString();
            if (query != null) {
                String[] pairs = query.split("&");
                String[] keyValue;
                for (String pair : pairs) {
                    keyValue = pair.split("=");
                    if (keyValue.length == 1) {
                        queryMap.put(urlDecode(keyValue[0]), "");
                    } else {
                        queryMap.put(urlDecode(keyValue[0]), urlDecode(keyValue[1]));
                    }
                }
            }
            req.setAttribute("req_queryMap", queryMap);
        }
        
        return queryMap;
    }
    
    // return URL used to access current servlet (sans query string)
    public static String getServerUrl(HttpServletRequest request){
        
        StringBuilder result = new StringBuilder();
        result.append(getScheme(request));
        result.append("://");
        result.append(getServerName(request));
        result.append(getPortExtension(request));
        
        return result.toString();
        
    }
    
    public static boolean isHttps(HttpServletRequest request){
        return getScheme(request).matches("^https");
    }
    /*
     // return URL used to access current servlet (sans query string)
    public static String getClubFromQueryString(HttpServletRequest request){
        return getParameterString(request, ProcessConstants.RP_CLUB, null);
    }
    
     // return URL used to access current servlet (sans query string)
    public static String getClubFromRequest(HttpServletRequest request){

        String result = getClubFromQueryString(request);
        if(result == null && isHttps(request)){
            // get from server name (c-clubname.[testing/dev/web/...].foretees.com)
            String server_name = getServerName(request).toLowerCase();
            if(server_name != null && server_name.matches("^(c|p)\\-[a-z0-9\\-]+\\.[a-zA-Z0-9\\-]+\\.[a-zA-Z0-9\\-]+")){
                String[] server_name_parts = server_name.split("\\.");
                String[] club_name_parts = server_name_parts[0].split("\\-");
                //String club_or_proshop = club_name_parts[0]; // "c" or "p"
                result = club_name_parts[1];
            }
        }
        return result;
        
    }
     * */
    
    // return scheme
    public static String getScheme(HttpServletRequest request){
        
        String scheme = request.getHeader("X-Forwarded-Scheme");
        if(scheme == null || scheme.isEmpty()){
            scheme = request.getScheme();
            
        }
        return scheme;
        
    }
    
    // return port extentsion ":[port]"
    public static String getPortExtension(HttpServletRequest request){
        
        StringBuilder result = new StringBuilder();
        int port = request.getServerPort();
        if(port != 80 & port != 443){
            result.append(":");
            result.append(port);
        }
        return result.toString();
        
    }
    
     // return server
    public static String getServerName(HttpServletRequest request){
        
        String server_name = request.getHeader("X-Forwarded-Host");
        if(server_name == null || server_name.isEmpty()){
            server_name = request.getHeader("X-Forwarded-Server");
            if(server_name == null || server_name.isEmpty()){
                server_name = request.getServerName();
            }
        }
        return server_name;
        
    }
    
     // return client IP(s), potentially a string list
    public static String getRemoteAddrs(HttpServletRequest request){
        
        List<String> result = new ArrayList<String>();
        
        String test = request.getHeader("X-Forwarded-For");
        if(test != null && !test.isEmpty()){
            result.add(test);
        }
        test = request.getHeader("Forwarded"); // Probably should parse this (should be in format "for=[remote ip address]; proto=http; by=[proxy ip address]")
        if(test != null && !test.isEmpty()){
            result.add(test);
        }
        test = request.getRemoteAddr();
        if(test != null && !test.isEmpty()){
            result.add(test);
        }
        
        return StringUtils.join(result,"; ");
        
    }
    
    // return URL used to access current servlet (sans query string)
    public static String getServletUrl(HttpServletRequest request){
        
        StringBuilder result = new StringBuilder(getServerUrl(request));
        result.append(request.getContextPath());
        result.append(request.getServletPath());
        
        return result.toString();
        
    }
    
    public static void setAppModeFromUri(HttpServletRequest req, int defaultAppMode) {
        req.setAttribute(ProcessConstants.RQA_APPMODE, Utilities.getParameterInteger(req, "s_m", defaultAppMode)); // Store app mode in request object for later use
        req.setAttribute(ProcessConstants.RQA_RWD, Utilities.getBitFromRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_RWD));
    }

}
