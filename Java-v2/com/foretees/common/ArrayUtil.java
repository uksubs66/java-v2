/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;          // mysql
import javax.sql.*;         // postgres
import java.util.regex.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Owner
 */
public class ArrayUtil {
    
    private static Map<String, Map<Integer, String>> fieldNameCache = new ConcurrentHashMap<String, Map<Integer, String>>();
    private static Map<String, Pattern> patternCache = new ConcurrentHashMap<String, Pattern>();
    
    
    public static Pattern getPattern(String pattern){
        Pattern result = patternCache.get(pattern);
        if(result == null){
            result = Pattern.compile(pattern);
        }
        return result;
    }
    
    // Build fieldname with index and cache in map to avoid creating garbage, or pull from cache and return.
    public static String getFieldName(String fieldName, int index){
        
        
        Map<Integer, String> fieldMap = fieldNameCache.get(fieldName);
        
        if(fieldMap == null){
            fieldMap = new ConcurrentHashMap<Integer, String>();
            fieldNameCache.put(fieldName, fieldMap);
        }
        
        String fieldWIndex = fieldMap.get(index);
        
        if(fieldWIndex == null){
            fieldWIndex = fieldName.replace("%", Integer.toString(index + 1));
            fieldMap.put(index, fieldWIndex);
        }
        
        return fieldWIndex;
        
    }
    
    /*
     * Sets an arrray to a prepared statement, starting with startParameterIndex, return next parameterIndex
     */
    public static int setArrayToPreparedStatment(PreparedStatement pstmt, String[] array, int startParameterIndex) throws Exception {
        
        for(int i = 0; i < array.length; i++){
            pstmt.setString(startParameterIndex++, array[i]);
        }

        return startParameterIndex;
        
    }
    
    public static int setArrayToPreparedStatment(PreparedStatement pstmt, int[] array, int startParameterIndex) throws Exception {
        
        for(int i = 0; i < array.length; i++){
            pstmt.setInt(startParameterIndex, array[i]);
            startParameterIndex ++;
        }

        return startParameterIndex;
        
    }
    
    public static int setArrayToPreparedStatment(PreparedStatement pstmt, short[] array, int startParameterIndex) throws Exception {
        
        for(int i = 0; i < array.length; i++){
            pstmt.setInt(startParameterIndex, array[i]);
            startParameterIndex ++;
        }

        return startParameterIndex;
        
    }
    
    public static int setArrayToPreparedStatment(PreparedStatement pstmt, Integer[] array, int startParameterIndex) throws Exception {
        
        for(int i = 0; i < array.length; i++){
            pstmt.setInt(startParameterIndex, array[i]);
            startParameterIndex ++;
        }

        return startParameterIndex;
        
    }
    
    public static int setArrayToPreparedStatment(PreparedStatement pstmt, Short[] array, int startParameterIndex) throws Exception {
        
        for(int i = 0; i < array.length; i++){
            pstmt.setInt(startParameterIndex, array[i]);
            startParameterIndex ++;
        }

        return startParameterIndex;
        
    }
    
    public static int setArrayToPreparedStatment(PreparedStatement pstmt, float[] array, int startParameterIndex) throws Exception {
        
        for(int i = 0; i < array.length; i++){
            pstmt.setFloat(startParameterIndex, array[i]);
            startParameterIndex ++;
        }

        return startParameterIndex;
        
    }
    
    public static int setArrayToPreparedStatment(PreparedStatement pstmt, Float[] array, int startParameterIndex) throws Exception {
        
        for(int i = 0; i < array.length; i++){
            pstmt.setFloat(startParameterIndex, array[i]);
            startParameterIndex ++;
        }

        return startParameterIndex;
        
    }
    
    public static int setArrayToPreparedStatment(PreparedStatement pstmt, long[] array, int startParameterIndex) throws Exception {
        
        for(int i = 0; i < array.length; i++){
            pstmt.setFloat(startParameterIndex, array[i]);
            startParameterIndex ++;
        }

        return startParameterIndex;
        
    }
    
    public static int setArrayToPreparedStatment(PreparedStatement pstmt, Long[] array, int startParameterIndex) throws Exception {
        
        for(int i = 0; i < array.length; i++){
            pstmt.setLong(startParameterIndex, array[i]);
            startParameterIndex ++;
        }

        return startParameterIndex;
        
    }
    
    public static int setArrayToPreparedStatment(PreparedStatement pstmt, boolean[] array, int startParameterIndex) throws Exception {
        
        for(int i = 0; i < array.length; i++){
            pstmt.setBoolean(startParameterIndex, array[i]);
            startParameterIndex ++;
        }

        return startParameterIndex;
        
    }
    
    public static int setArrayToPreparedStatment(PreparedStatement pstmt, Boolean[] array, int startParameterIndex) throws Exception {
        
        for(int i = 0; i < array.length; i++){
            pstmt.setBoolean(startParameterIndex, array[i]);
            startParameterIndex ++;
        }

        return startParameterIndex;
        
    }
    
    public static String[] getStringArrayFromResultSet(ResultSet rs, String fieldName, int length) throws Exception {
        
        String[] result = new String[length];
        
        for(int i = 0; i < length; i++){
            result[i] = rs.getString(getFieldName(fieldName, i));
        }
        
        return result;
        
    }

    public static int[] getIntArrayFromResultSet(ResultSet rs, String fieldName, int length) throws Exception {
        
        int[] result = new int[length];
        
        for(int i = 0; i < length; i++){
            result[i] = rs.getInt(getFieldName(fieldName, i));
        }
        
        return result;
        
    }
    
    public static Integer[] getIntegerArrayFromResultSet(ResultSet rs, String fieldName, int length) throws Exception {
        
        Integer[] result = new Integer[length];
        
        for(int i = 0; i < length; i++){
            result[i] = rs.getInt(getFieldName(fieldName, i));
        }
        
        return result;
        
    }
    
    public static Float[] getFloatArrayFromResultSet(ResultSet rs, String fieldName, int length) throws Exception {
        
        Float[] result = new Float[length];
        
        for(int i = 0; i < length; i++){
            result[i] = rs.getFloat(getFieldName(fieldName, i));
        }
        
        return result;
        
    }
    
    public static Short[] getShortArrayFromResultSet(ResultSet rs, String fieldName, int length) throws Exception {
        
        Short[] result = new Short[length];
        
        for(int i = 0; i < length; i++){
            result[i] = rs.getShort(getFieldName(fieldName, i));
        }
        
        return result;
        
    }
    
    public static List<String> getStringListFromDelemetedField(String field, String delimiter) {
        
        if(field == null){
            return new ArrayList<String>();
        } else {
            return Arrays.asList(field.split(delimiter));
        }

    }
    
    public static List<Integer> getIntegerListFromDelemetedField(String field, String delimiter) {
        
        if(field == null){
            return new ArrayList<Integer>();
        } else {
            return Arrays.asList(stringToInteger(field.split(delimiter), 0));
        }

    }
    
    public static List<Long> getLongListFromDelemetedField(String field, String delimiter) {
        
        if(field == null){
            return new ArrayList<Long>();
        } else {
            return Arrays.asList(stringToLong(field.split(delimiter), (long)0));
        }

    }
    
    public static Integer[] stringToInteger(String[] stringArray){
        return stringToInteger(stringArray, null);
    }
    
    public static Integer[] stringToInteger(String[] stringArray, Integer defaultIfNull){
        
        Integer[] result = new Integer[stringArray.length];
        
        for(int i = 0; i < stringArray.length; i++){
            try{
                result[i] = Integer.parseInt(stringArray[i]);
            }
            catch(NumberFormatException nfe){
                //Not an integer
                result[i] = defaultIfNull;
            }
        }
        
        return result;
        
    }
    
    public static Long[] stringToLong(String[] stringArray){
        return stringToLong(stringArray, null);
    }
    
    public static Long[] stringToLong(String[] stringArray, Long defaultIfNull){
        
        Long[] result = new Long[stringArray.length];
        
        for(int i = 0; i < stringArray.length; i++){
            try{
                result[i] = Long.parseLong(stringArray[i]);
            }
            catch(NumberFormatException nfe){
                //Not an integer
                result[i] = defaultIfNull;
            }
        }
        
        return result;
        
    }
    
    // Array "contains" for primitive arrays
    public static boolean contains(boolean[] array, boolean search){
        for(int i = 0; i < array.length; i++){
            if(array[i] == search){
                return true;
            }
        }
        return false;
    }
    public static boolean contains(int[] array, int search){
        for(int i = 0; i < array.length; i++){
            if(array[i] == search){
                return true;
            }
        }
        return false;
    }
    public static boolean contains(float[] array, float search){
        for(int i = 0; i < array.length; i++){
            if(array[i] == search){
                return true;
            }
        }
        return false;
    }
    public static boolean contains(short[] array, short search){
        for(int i = 0; i < array.length; i++){
            if(array[i] == search){
                return true;
            }
        }
        return false;
    }
    
    public static boolean containsIgnoreCase(String[] array, String search){
        for(int i = 0; i < array.length; i++){
            if(array[i].equalsIgnoreCase(search)){
                return true;
            }
        }
        return false;
    }
    
    public static boolean containsIgnoreCase(String[] array, String search, int segmentSize, int searchLength){
        int segpos = 0;
        for(int i = 0; i < array.length; i++){
            if(segmentSize > 0){
                segpos = i-((int)(Math.floor(i/segmentSize))*segmentSize);
            }
            if(segpos >= searchLength && segpos > 0 && segmentSize > 0 && searchLength > 0){
                i = segmentSize * (segpos+1);
            }
            if(i >= array.length){
                break;
            }
            if(array[i].equalsIgnoreCase(search)){
                return true;
            }
        }
        return false;
    }
    
    public static Integer indexOfIgnoreCase(String[] array, String search, int segmentSize, int searchLength){
        int segpos = 0;
        for(int i = 0; i < array.length; i++){
            if(segmentSize > 0){
                segpos = i-((int)(Math.floor(i/segmentSize))*segmentSize);
            }
            if(segpos >= searchLength && segpos > 0 && segmentSize > 0 && searchLength > 0){
                i = segmentSize * (segpos+1);
            }
            if(i >= array.length){
                break;
            }
            if(array[i].equalsIgnoreCase(search)){
                return i;
            }
        }
        return null;
    }
    
    public static boolean containsStartsWith(String[] a, String s){
        for(int i = 0; i < a.length; i++){
            if(a[i].startsWith(s)){
                return true;
            }
        }
        return false;
    }
    
    public static Integer indexOfAll(int[] a, int[] s){
        
        for(int i = 0; i < a.length; i++){
            int found = 0;
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i] == s[i2]){
                    found ++;
                    if(found == s.length){
                        return i;
                    }
                }
            }
        }
        return null;
    }
    
    public static Integer indexOfAny(int[] a, int[] s){
        
        for(int i = 0; i < a.length; i++){
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i] == s[i2]){
                    return i;
                }
            }
        }
        return null;
    }
    
    public static Integer indexOfAll(String[] a, String[] s){
        
        for(int i = 0; i < a.length; i++){
            int found = 0;
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].equals(s[i2])){
                    found ++;
                    if(found == s.length){
                        return i;
                    }
                }
            }
        }
        return null;
    }
    
    public static Integer indexOfAny(String[] a, String[] s){
        
        for(int i = 0; i < a.length; i++){
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].equals(s[i2])){
                    return i;
                }
            }
        }
        return null;
    }
    
    public static Integer indexOfAllIgnoreCase(String[] a, String[] s){
        
        for(int i = 0; i < a.length; i++){
            int found = 0;
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].equalsIgnoreCase(s[i2])){
                    found ++;
                    if(found == s.length){
                        return i;
                    }
                }
            }
        }
        return null;
    }
    
    public static Integer indexOfAnyIgnoreCase(String[] a, String[] s){
        
        for(int i = 0; i < a.length; i++){
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].equalsIgnoreCase(s[i2])){
                    return i;
                }
            }
        }
        return null;
    }
    
    public static Integer indexOfAllEndsWith(String[] a, String[] s){
        
        for(int i = 0; i < a.length; i++){
            int found = 0;
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].endsWith(s[i2])){
                    found ++;
                    if(found == s.length){
                        return i;
                    }
                }
            }
        }
        return null;
    }
    
    public static Integer indexOfAnyEndsWith(String[] a, String[] s){
        
        for(int i = 0; i < a.length; i++){
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].endsWith(s[i2])){
                    return i;
                }
            }
        }
        return null;
    }
    
    public static Integer indexOfAllStartsWith(String[] a, String[] s){
        
        for(int i = 0; i < a.length; i++){
            int found = 0;
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].startsWith(s[i2])){
                    found ++;
                    if(found == s.length){
                        return i;
                    }
                }
            }
        }
        return null;
    }
    
    public static Integer indexOfAnyStartsWith(String[] a, String[] s){
        
        for(int i = 0; i < a.length; i++){
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].startsWith(s[i2])){
                    return i;
                }
            }
        }
        return null;
    }
    
    public static Integer indexOf(int[] a, int s){
        for(int i = 0; i < a.length; i++){
            if(a[i] == s){
                return i;
            }
        }
        return null;
    }
    
    public static Integer indexOf(String[] a, String s){
        for(int i = 0; i < a.length; i++){
            if(a[i].equals(s)){
                return i;
            }
        }
        return null;
    }
    
    public static Integer indexOfIgnoreCase(String[] a, String s){
        for(int i = 0; i < a.length; i++){
            if(a[i].equalsIgnoreCase(s)){
                return i;
            }
        }
        return null;
    }
    
    public static Integer indexOfStartsWith(String[] a, String s){
        for(int i = 0; i < a.length; i++){
            if(a[i].startsWith(s)){
                return i;
            }
        }
        return null;
    }
    
    public static Integer indexOfEndsWith(String[] a, String s){
        for(int i = 0; i < a.length; i++){
            if(a[i].endsWith(s)){
                return i;
            }
        }
        return null;
    }
    
    public static int countAll(int[] a, int[] s){
        
        int result = 0;
        for(int i = 0; i < a.length; i++){
            int found = 0;
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i] == s[i2]){
                    found ++;
                    if(found == s.length){
                        result ++;
                    }
                }
            }
        }
        return result;
    }
    
    public static int countAny(int[] a, int[] s){
        
        int result = 0;
        for(int i = 0; i < a.length; i++){
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i] == s[i2]){
                    result ++;
                    break;
                }
            }
        }
        return result;
    }
    
    public static int countAll(String[] a, String[] s){
        
        int result = 0;
        for(int i = 0; i < a.length; i++){
            int found = 0;
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].equals(s[i2])){
                    found ++;
                    if(found == s.length){
                        result ++;
                    }
                }
            }
        }
        return result;
    }
    
    public static int countAny(String[] a, String[] s){
        
        int result = 0;
        for(int i = 0; i < a.length; i++){
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].equals(s[i2])){
                    result ++;
                    break;
                }
            }
        }
        return result;
    }
    
    public static int countAllIgnoreCase(String[] a, String[] s){
        
        int result = 0;
        for(int i = 0; i < a.length; i++){
            int found = 0;
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].equalsIgnoreCase(s[i2])){
                    found ++;
                    if(found == s.length){
                        result ++;
                    }
                }
            }
        }
        return result;
    }
    
    public static int countAnyIgnoreCase(String[] a, String[] s){
        
        int result = 0;
        for(int i = 0; i < a.length; i++){
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].equalsIgnoreCase(s[i2])){
                    result ++;
                    break;
                }
            }
        }
        return result;
    }
    
    public static int countAllEndsWith(String[] a, String[] s){
        
        int result = 0;
        for(int i = 0; i < a.length; i++){
            int found = 0;
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].endsWith(s[i2])){
                    found ++;
                    if(found == s.length){
                        result ++;
                    }
                }
            }
        }
        return result;
    }
    
    public static int countAnyEndsWith(String[] a, String[] s){
        
        int result = 0;
        for(int i = 0; i < a.length; i++){
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].endsWith(s[i2])){
                    result ++;
                    break;
                }
            }
        }
        return result;
    }
    
    public static int countAllStartsWith(String[] a, String[] s){
        
        int result = 0;
        for(int i = 0; i < a.length; i++){
            int found = 0;
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].startsWith(s[i2])){
                    found ++;
                    if(found == s.length){
                        result ++;
                    }
                }
            }
        }
        return result;
    }
    
    public static int countAnyStartsWith(String[] a, String[] s){
        
        int result = 0;
        for(int i = 0; i < a.length; i++){
            for(int i2 = 0; i2 < s.length; i2++){
                if(a[i].startsWith(s[i2])){
                    result ++;
                    break;
                }
            }
        }
        return result;
    }
    
    public static int countAnyMatching(String[] a, String regex){

        Matcher m;
        Pattern p = getPattern(regex);
        int result = 0;
        for(int i = 0; i < a.length; i++){
            m = p.matcher(a[i]);
            if(m.find()){
                result ++;
            }
        }
        return result;
    }
    
    public static boolean[] combine(boolean[] a, boolean[] b){
        int length = a.length + b.length;
        boolean[] result = new boolean[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    
    public static int[] combine(int[] a, int[] b){
        int length = a.length + b.length;
        int[] result = new int[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    
    public static String[] combine(String[] a, String[] b){
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    
    public static String encodeCsvLine(String[] list) {
        return encodeCsvLine(Arrays.asList(list));
    }
    
    public static String encodeCsvLine(List<String> list) {
        
        StringBuilder results = new StringBuilder();
        int i= 0;
        for(String item : list){
            if(i > 0){
                results.append(",");
            }
            if(item.length() > 0){
                results.append("\"");
                results.append(item.replaceAll("\"", "\"\""));
                results.append("\"");
            }
            i++;
        }
        return results.toString();
        
    }
    
    public static List<String> parseCsvLine(Reader r) throws Exception {
        int ch = r.read();
        while (ch == '\r') {
            //ignore linefeed chars wherever, particularly just before end of file
            ch = r.read();
        }
        if (ch < 0) {
            return null;
        }
        List<String> store = new ArrayList<String>();
        StringBuffer curVal = new StringBuffer();
        boolean inquotes = false;
        boolean wasquotes = false;
        boolean started = false;
        String val;
        while (ch >= 0) {
            if (inquotes) {
                started = true;
                if (ch == '\"') {
                    inquotes = false;
                } else {
                    curVal.append((char) ch);
                }
            } else {
                if (ch == '\"') {
                    inquotes = true;
                    wasquotes = true;
                    if (started) {
                        // if this is the second quote in a value, add a quote
                        // this is for the double quote in the middle of a value
                        curVal.append('\"');
                    }
                } else if (ch == ',') {
                    if (curVal.length() > 0) {
                        val = curVal.toString();
                        if(!wasquotes && val.equalsIgnoreCase("null")){
                            store.add(null);
                        } else {
                            store.add(curVal.toString());
                        }
                    } else {
                        store.add(null);
                    }
                    curVal = new StringBuffer();
                    started = false;
                    wasquotes = false;
                } else if (ch == '\r') {
                    //ignore LF characters
                } else if (ch == '\n') {
                    //end of a line, break out
                    break;
                } else {
                    curVal.append((char) ch);
                }
            }
            ch = r.read();
        }
        if (curVal.length() > 0) {
            val = curVal.toString();
            if (!wasquotes && val.equalsIgnoreCase("null")) {
                store.add(null);
            } else {
                store.add(curVal.toString());
            }
        } else {
            store.add(null);
        }
        
        return store;
    }
    
}
