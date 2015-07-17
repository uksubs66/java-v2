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

    
    
    public static List<Integer> getIntegerListFromReq(HttpServletRequest req, String name) {

        List<Integer> result = new ArrayList<Integer>();
        String search;
        String last_search = "";
        Integer test = null;
        int i = 0;
        do {
            search = name.replace("%", Integer.toString(i + 1));
            test = reqUtil.getParameterInteger(req, search, null);
            if(test != null){
                result.add(test);
            }
            i++;
            if(search.equals(last_search)){
                break; // we don't want to get stuck in an infinate loop;
            } else {
                last_search = search;
            }
        } while (test != null);
        return result;
        
    }
    
    public static List<String> getStringListFromReq(HttpServletRequest req, String name) {

        List<String> result = new ArrayList<String>();
        String search;
        String last_search = "";
        String test = null;
        int i = 0;
        do {
            search = name.replace("%", Integer.toString(i + 1));
            test = reqUtil.getParameterString(req, search, null);
            if(test != null){
                result.add(test);
            }
            i++;
            if(search.equals(last_search)){
                break; // we don't want to get stuck in an infinate loop;
            } else {
                last_search = search;
            }
        } while (test != null);
        return result;
        
    }
    /*
    public static int[] getIntArrayFromReq(HttpServletRequest req, String name, int size, int null_default) {

        int[] result = new int[size];
        String search = "";

        for (int i = 0; i < size; i++) {
            search = name.replace("%", "" + (i + 1));
            result[i] = ((req.getParameter(search) != null) ? Integer.parseInt(req.getParameter(search)) : null_default);
        }
        return result;
        
    }
    */
    public static int[] getIntArrayFromReq(HttpServletRequest req, String name, int size, int null_default) {
        return getIntArrayFromReq(req, name, 0, size, null_default);
    }

    public static int[] getIntArrayFromReq(HttpServletRequest req, String name, int start, int size, int null_default) {

        int[] result = new int[size];
        String search = "";

        for (int i = start; i < size+start; i++) {
            search = name.replace("%", Integer.toString(i + 1));
            result[i-start] = reqUtil.getParameterInteger(req,search,null_default);
        }
        return result;
        
    }
    
    public static String[] getStringArrayFromReq(HttpServletRequest req, String name, int size, String null_default) {
        return getStringArrayFromReq(req, name, 0, size, null_default);
    }

    public static String[] getStringArrayFromReq(HttpServletRequest req, String name, int start, int size, String null_default) {

        String[] result = new String[size];
        String search = "";

        for (int i = start; i < size+start; i++) {
            search = name.replace("%", Integer.toString(i + 1));
            result[i-start] = reqUtil.getParameterString(req,search,null_default);
        }
        return result;
        
    }
    
    public static String[] getStringArrayFromReq(HttpServletRequest req, String[] names, int size, String null_default) {
        return getStringArrayFromReq(req, names, 0, size, null_default);
    }

    public static String[] getStringArrayFromReq(HttpServletRequest req, String[] names, int start, int size, String null_default) {

        String[] result = new String[size];
        String search = "";
        boolean found;
        String test;
        
        for (int i = start; i < size+start; i++) {
            found = false;
            for(int i2 = 0; i2 < names.length; i2++){
                search = names[i2].replace("%", Integer.toString(i + 1));
                test = req.getParameter(search);
                if(test != null){
                    result[i-start] = test;
                    found = true;
                    break;
                }
            }
            if(!found){
                result[i] = null_default;
            }
        }
        return result;
        
    }
    
    public static int findNullInReq(HttpServletRequest req, String name, int size) {

        int result = 0;
        String search = "";

        for (int i = 0; i < size; i++) {
            search = name.replace("%", Integer.toString(i + 1));
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
        
        StringBuilder result = new StringBuilder();
        
        for(int i = start; (i < main_array.length) && (i < length); i++){
            result.append(String.format(format_string, (i+1), main_array[i]));
        }
        
        return result.toString();
        
    }
    public static String formatArray(Integer[] main_array, int start, int length, String format_string){
        
        StringBuilder result = new StringBuilder();
        
        for(int i = start; (i < main_array.length) && (i < length); i++){
            result.append(String.format(format_string, (i+1), main_array[i]));
        }
        
        return result.toString();
        
    }
    public static String formatArray(Float[] main_array, int start, int length, String format_string){
        
        StringBuilder result = new StringBuilder();
        
        for(int i = start; (i < main_array.length) && (i < length); i++){
            result.append(String.format(format_string, (i+1), main_array[i]));
        }
        
        return result.toString();
        
    }
    public static String formatArray(Short[] main_array, int start, int length, String format_string){
        
        StringBuilder result = new StringBuilder();
        
        for(int i = start; (i < main_array.length) && (i < length); i++){
            result.append(String.format(format_string, (i+1), main_array[i]));
        }
        
        return result.toString();
        
    }
    
    public static Map<String, Object> makeButton(String text) {
        return makeButton(text, null, null, null, null);
    }
    
    public static Map<String, Object> makeButton(String text, String action) {
        return makeButton(text, action,null, null);
    }
    
    public static Map<String, Object> makeButton(String text, String[] suppress) {
        return makeButton(text, null, null, suppress);
    }
    
    public static Map<String, Object> makeButton(String text, String[] suppress, Map<String, String> options) {
        return makeButton(text, null, null, suppress, options);
    }
    
    public static Map<String, Object> makeButton(String text, Map<String, String> set) {
        return makeButton(text, null, set, null);
    }
    
    public static Map<String, Object> makeButton(String text, Map<String, String> set, Map<String, String> options) {
        return makeButton(text, null, set, null, options);
    }
    
    public static Map<String, Object> makeButton(String text, Map<String, String> set, String[] suppress) {
        return makeButton(text, null, set, suppress);
    }
    
    public static Map<String, Object> makeButton(String text, Map<String, String> set, String[] suppress, Map<String, String> options) {
        return makeButton(text, null, set, suppress, options);
    }
    
    public static Map<String, Object> makeButton(String text, String action, Map<String, String> set, String[] suppress) {
        return makeButton(text, action, set, suppress, null);
    }
    
    public static Map<String, Object> makeButton(String text, String action, Map<String, String> set, String[] suppress, Map<String, String> options) {
        Map<String, Object> button = new LinkedHashMap<String, Object>();
        button.put("value",text);
        if(action != null){
            button.put("action",action);
        }
        if(suppress != null){
            button.put("suppress",suppress);
        }
        if(set != null){
            button.put("set",set);
        }
        if(options != null){
            for(Map.Entry<String,String> option : options.entrySet()){
                button.put(option.getKey(),option.getValue());
            }
        }
        return button;
    }
    
    public static Map<String, String> keyValMap(String key) {
        return keyValMap(new String[]{key});
    }
    
    public static Map<String, String> keyValMap(String[] keyval) {
        Map<String, String> set = new LinkedHashMap<String, String>();
        for(int i = 0; i < keyval.length; i+=2){
            String val = "";
            if(i+1 < keyval.length){
                val = keyval[i+1];
            }
            set.put(keyval[i],val);
        }
        return set;
    }
    
    public static Map<String, Object> fieldHtml(String html) {
        return makeFieldMap(html, "html", "", 0, 0, "", false, null, null, null, null, null);
    }
    
    public static Map<String, Object> label(String html) {
        return makeFieldMap(html, "label", "", 0, 0, "", false, null, null, null, null, null);
    }
    
    public static Map<String, Object> startFieldBlock(String label) {
        return makeFieldMap(label, "fieldblock", "", 0, 0, "", false, null, null, null, null, null);
    }
    
    public static Map<String, Object> endFieldBlock() {
        return makeFieldMap(null, "end-fieldblock", "", 0, 0, "", false, null, null, null, null, null);
    }
    
    public static Map<String, Object> dateField(String key, String label, String value, String default_date, String min_date, String max_date, String submit_on_change) {
        return makeFieldMap(label, "date", null, 0, 0, value, false, null, null, null, key, null, min_date, max_date, default_date, submit_on_change);
    }
    
    public static Map<String, Object> startRadioBlock(String key, String label, String value) {
        return makeFieldMap(label, "radioblock", null, 0, 0, value, false, null, null, null, key, null);
    }
    
    public static Map<String, Object> endRadioBlock() {
        return makeFieldMap(null, "end-radioblock", null, 0, 0, null, false, null, null, null, null, null);
    }
    
    public static Map<String, Object> radioSelect(String key, String submit_name, String label, String value, Boolean checked) {
        return radioSelect(key, submit_name, label, value, checked, null);
    }
    
    public static Map<String, Object> radioSelect(String key, String submit_name, String label, String value, Boolean checked, String fclass) {
        return makeFieldMap(label, "radioselect", fclass, 0, 0, value, false, null, null, null, null, key, submit_name, null, null, null, null, checked);
    }
    
    public static Map<String, Object> select(String key, String submit_name, String label, String value, Map<String, Object> option_map) {
        return makeFieldMap(label, "select", null, 0, 0, value, false, null, null, option_map, key, submit_name, null, null, null, null, null);
    }
    
    public static Map<String, Object> select(String key, String submit_name, String label, String value, String[] options) {
        return makeFieldMap(label, "select", null, 0, 0, value, false, options, null, null, null, key, submit_name, null, null, null, null, null);
    }
    
    public static Map<String, Object> select(String key, String submit_name, String label, String value, String[] options, boolean required) {
        return makeFieldMap(label, "select", null, 0, 0, value, required, options, null, null, null, key, submit_name, null, null, null, null, null);
    }
    
    public static Map<String, Object> text(String key, String submit_name, String label, String value, boolean required) {
        
        return makeFieldMap(label, "text", null, 0, 0, value, required, null, null, null, key, submit_name);
    }

    public static Map<String, Object> fieldMap(String key, String submit_name, String label, String type, String value) {
        return makeFieldMap(label, type, "", 0, 0, value, false, null, null, null, key, submit_name);
    }
    
    public static Map<String, Object> fieldMap(String key, String submit_name, String label, String type, String value, String fclass, Integer maxlen) {
        return makeFieldMap(label, type, fclass, 0, maxlen, value, false, null, null, null, key, submit_name);
    }
    
    public static Map<String, Object> fieldMap(String key, String submit_name, String label, String type, String value, String fclass, Integer maxlen, Integer size, Boolean required, String[] options, String[] values, Map<String, Object> option_map) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, options, values, option_map, key, submit_name);
    }
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, null, null, null, null);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String key) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, null, null, null, key);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, options, null, null, null);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String key) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, options, null, null, key);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String[] values) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, options, values, null, null);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String[] values, String key) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, options, values, null, key);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, Map<String, Object> options_map) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, null, null, options_map, null);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, Map<String, Object> options_map, String key) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, null, null, options_map, key);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String[] values, Map<String, Object> option_map) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, options, values, option_map, null);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String[] values, Map<String, Object> option_map, String key) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, options, values, option_map, key, null);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String[] values, Map<String, Object> option_map, String key, String submit_name) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, options, values, option_map, key, submit_name, null, null, null, null);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String[] values, Map<String, Object> option_map, String key, String submit_name, String min_date, String max_date, String default_date, String submit_on_change) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, options, values, option_map, key, submit_name, min_date, max_date, default_date, submit_on_change, null);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String[] values, Map<String, Object> option_map, String key, String submit_name, String min_date, String max_date, String default_date, String submit_on_change, Boolean checked) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, options, values, option_map, null, key, submit_name, min_date, max_date, default_date, submit_on_change, checked);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String[] values, List<Map<String, Object>> option_list, String key, String submit_name, String min_date, String max_date, String default_date, String submit_on_change, Boolean checked) {
        return makeFieldMap(label, type, fclass, size, maxlen, value, required, options, values, null, option_list, key, submit_name, min_date, max_date, default_date, submit_on_change, checked);
    }
    
    public static Map<String, Object> makeFieldMap(String label, String type, String fclass, int size, int maxlen, String value, boolean required, String[] options, String[] values, Map<String, Object> option_map, List<Map<String, Object>> option_list, String key, String submit_name, String min_date, String max_date, String default_date, String submit_on_change, Boolean checked) {

        Map<String, Object> field_map = new LinkedHashMap<String, Object>();

        if(key != null){
            field_map.put("key", key);
        }
        if(submit_on_change != null){
            field_map.put("submit_on_change", submit_on_change);
        }
        if(submit_name != null){
            field_map.put("submit_name", submit_name);
        }
        if(max_date != null){
            field_map.put("max_date", max_date);
        }
        if(min_date != null){
            field_map.put("min_date", min_date);
        }
        if(checked != null){
            field_map.put("checked", checked);
        }
        if(default_date != null){
            field_map.put("default_date", default_date);
        }
        field_map.put("label", label);
        field_map.put("value", value);
        field_map.put("required", required);
        field_map.put("size", size);
        field_map.put("maxlen", maxlen);
        if(fclass != null){
            field_map.put("class", fclass);
        }
        field_map.put("type", type);
        if(option_list != null){
            field_map.put("options", option_list); 
        }else if(option_map != null){
            option_list = new ArrayList<Map<String, Object>>();
            for(Map.Entry<String, Object> entry : option_map.entrySet()){
                Map option = new HashMap<String, Object>();
                option.put("text", entry.getKey());
                option.put("value", entry.getValue());
                option_list.add(option);
            }
            field_map.put("options", option_list);
        } else if (options != null) {
            option_list = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < options.length; i++) {
                Map option = new HashMap<String, Object>();
                option.put("text", options[i]);
                if(values != null && options.length == values.length){
                    option.put("value", values[i]);
                }else{
                    option.put("value", options[i]);
                }
                option_list.add(option);
            }
            field_map.put("options", option_list);
        }
        return field_map;

    }
    
    
}  // end of class
