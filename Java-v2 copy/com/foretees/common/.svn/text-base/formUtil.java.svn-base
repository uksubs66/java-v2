/***************************************************************************************
 *   formUtil:  Misc. Utilities for working with form data, building forms, 
 *              verifying form variables, etc.
 *
 *   called by:  Member_slot
 *
 *   created:    12/08/2011   John K.
 *
 *   last updated:
 *
 *        12/08/2011  Created.
 *
 ***************************************************************************************
 */
package com.foretees.common;

//import java.io.*;
import java.util.*;
//import java.sql.*;
//import javax.servlet.*;
import javax.servlet.http.*;
//import org.apache.commons.lang.*;
/*
import javax.servlet.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;
 */

public class formUtil {

    public static int[] getIntArrayFromReq(HttpServletRequest req, String name, int size, int null_default) {

        int[] result = new int[size];
        String search = "";

        for (int i = 0; i < size; i++) {
            search = name.replace("%", "" + (i + 1));
            result[i] = ((req.getParameter(search) != null) ? Integer.parseInt(req.getParameter(search)) : null_default);
        }
        return result;
        
    }

    public static String[] getStringArrayFromReq(HttpServletRequest req, String name, int size, String null_default) {

        String[] result = new String[size];
        String search = "";

        for (int i = 0; i < size; i++) {
            search = name.replace("%", "" + (i + 1));
            result[i] = ((req.getParameter(search) != null) ? req.getParameter(search) : null_default);
        }
        return result;
        
    }
    
    public static int findNullInReq(HttpServletRequest req, String name, int size) {

        int result = 0;
        String search = "";

        for (int i = 0; i < size; i++) {
            search = name.replace("%", "" + (i + 1));
            if(req.getParameter(search) != null){
                result++;
            }
        }
        return result;
        
    }
    
    public static int getCountOfStringArrayByRegex(String[] source_array, String r_exp){
        
        int result = 0;
        
        for(int i = 0; i < source_array.length; i++){
            if((source_array[i] != null) && (source_array[i].matches(r_exp))){
                result ++;
            }
        }
        
        return result;
        
    }
    
    public static int getCountOfStringArrayByRegex(String[] source_array, String r_exp, int start, int length){
        
        int result = 0;
        
        for(int i = start; (i < source_array.length) && i < length; i++){
            if((source_array[i] != null) && (source_array[i].matches(r_exp))){
                result ++;
            }
        }
        
        return result;
        
    }
    
    // Return index of string found, +1 : 0 = not found 
    public static int findStringInArray(String[] source_array, String search, int start, int length){
        
        for(int i = start; i < source_array.length && i < length; i++){
            if((source_array[i] != null) && (source_array[i].equals(search))){
                return (i+1);
            }
        }
        return 0;
        
        
    }
    
    public static int compareArrayValues(String[] array_1, String[] array_2){
        
        int result = 0;
        
        for(int i = 0; (i < array_1.length) && (i < array_2.length); i++){
            if(array_1[i].equals(array_2[i])){
                result ++;
            }
        }
        
        return result;
        
    }
    
    //
    //  Return a string concatenating each element in an array, using:
    //  Sting.format(format_string,[index of array element+1],[value of array element],[extra_parameters array] )
    //  Starting at the [start] element, and continuing until length, or the end of the array.
    //
    public static String formatArray(String[] main_array, int start, int length, String format_string){
        
        StringBuffer result = new StringBuffer();
        
        for(int i = start; (i < main_array.length) && (i < length); i++){
            result.append(String.format(format_string, (i+1), main_array[i]));
        }
        
        return result.toString();
        
    }
    public static String formatArray(Integer[] main_array, int start, int length, String format_string){
        
        StringBuffer result = new StringBuffer();
        
        for(int i = start; (i < main_array.length) && (i < length); i++){
            result.append(String.format(format_string, (i+1), main_array[i]));
        }
        
        return result.toString();
        
    }
    public static String formatArray(Float[] main_array, int start, int length, String format_string){
        
        StringBuffer result = new StringBuffer();
        
        for(int i = start; (i < main_array.length) && (i < length); i++){
            result.append(String.format(format_string, (i+1), main_array[i]));
        }
        
        return result.toString();
        
    }
    public static String formatArray(Short[] main_array, int start, int length, String format_string){
        
        StringBuffer result = new StringBuffer();
        
        for(int i = start; (i < main_array.length) && (i < length); i++){
            result.append(String.format(format_string, (i+1), main_array[i]));
        }
        
        return result.toString();
        
    }
    
    public static Map<String, Object> makeFieldMap(String name, String type, String fclass, int size, int maxlen, String value, boolean required) {
        return makeFieldMap(name, type, fclass, size, maxlen, value, required, null, null, null);
    }
    
    public static Map<String, Object> makeFieldMap(String name, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options) {
        return makeFieldMap(name, type, fclass, size, maxlen, value, required, options, null, null);
    }
    
    public static Map<String, Object> makeFieldMap(String name, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String[] values) {
        return makeFieldMap(name, type, fclass, size, maxlen, value, required, options, values, null);
    }
    
    public static Map<String, Object> makeFieldMap(String name, String type, String fclass, int size, int maxlen, String value, boolean required, Map<String, Object> options_map) {
        return makeFieldMap(name, type, fclass, size, maxlen, value, required, null, null, options_map);
    }
    
    public static Map<String, Object> makeFieldMap(String name, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String[] values, Map<String, Object> option_map) {

        Map<String, Object> field_map = new LinkedHashMap<String, Object>();

        field_map.put("name", name);
        field_map.put("value", value);
        field_map.put("required", required);
        field_map.put("size", size);
        field_map.put("maxlen", maxlen);
        field_map.put("class", fclass);
        field_map.put("type", type);
        if(option_map != null){
            field_map.put("options", option_map);
        } else if (options != null) {
            option_map = new LinkedHashMap<String, Object>();
            for (int i = 0; i < options.length; i++) {
                if(values != null && options.length == values.length){
                    option_map.put(options[i], values[i]);
                }else{
                    option_map.put(options[i], options[i]);
                }
            }
            field_map.put("options", option_map);
        }
        return field_map;

    }
    
}  // end of class
