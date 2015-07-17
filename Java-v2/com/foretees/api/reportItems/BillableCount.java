/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.reportItems;

//import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
//import com.foretees.common.ArrayUtil;
//import com.foretees.common.Connect;
//import com.foretees.common.timeUtil;
//import org.apache.commons.lang.*;
//import java.sql.*;          // mysql
//import java.util.UUID;
//import javax.naming.*;
//import javax.servlet.http.*;
//import java.io.*;
//import java.util.*;
/**
 *
 * @author John Kielkopf
 */
public class BillableCount {
    
    public String name;
    
    public Integer inactive; // inactive counts
    public Integer non_billable; // non-billable counts
    public Integer billable; // billable counts 
    public Float skipped_before_min; // not counted because it was lower thent he minimum quantity.
    public Float counted_after_min; // quantity to be used.
    public Integer total; // Total counted
    
    public BillableCount(){}; // Empty parm
    
    
    public BillableCount(
            String name, 
            Integer inactive,
            Integer non_billable,
            Integer billable,
            Integer total
            ){
        this.name = name;
        this.inactive = inactive;
        this.non_billable = non_billable;
        this.billable = billable;
        this.total = total;
        
    };
    
    public BillableCount(BillableCount bc){
        this.name = bc.name;
        this.inactive = bc.inactive;
        this.non_billable = bc.non_billable;
        this.billable = bc.billable;
        this.skipped_before_min = bc.skipped_before_min;
        this.counted_after_min = bc.counted_after_min;
        this.total = bc.total;
    };
    
    
    
    public final String className(){
        return this.getClass().getSimpleName();
    }
    
    public final String classNameProper(){
        return ApiCommon.formatClassName(className());
    }
    
    public final String classNameLower(){
        return ApiCommon.formatClassNameLower(className());
    }
    
}
