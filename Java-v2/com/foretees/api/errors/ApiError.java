/*
 * Will eventualy replace the text-only response currently used in the API?
 * 
 * That, or we will move to throwing errors, even though there could be performance implications...
 * 
 */
package com.foretees.api.errors;

/**
 *
 * @author Owner
 */
public class ApiError {
    
    public String error; // String description of error
    public int status_code; // status code.  Use applicable HTTP status codes.
    
}
