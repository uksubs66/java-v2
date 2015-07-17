/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api;

import java.util.*;

/**
 *
 * @author Owner
 */
public class ApiResponse {
    
    public boolean success = false;
    public boolean prompt_for_override = false;
    public String error;
    public String auth_token;
    public String alternate_content_type;
    public String date;
    public String export;
    public byte[] byte_export = new byte[0];
    public String continue_prompt;
    public String continue_parameter;
    public Map<String, Object> results = new LinkedHashMap<String, Object>();
    public Map<String, Object> debug = new LinkedHashMap<String, Object>();
    
}
