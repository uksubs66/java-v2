/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api;

import com.foretees.common.AESencrypt;

/**
 *
 * @author Owner
 */
public class ApiConstants {
    
    // Parameters
    public static String parameter_command = "command";
    public static String parameter_skip_update_check = "skip_update_check";
    public static String parameter_auth_token = "auth_token";
    public static String parameter_single_use_key = "sua_key";
    public static String parameter_device_code = "dc"; // For mobile web API Aceess compatibility
    public static String parameter_member_id = "mid"; // For mobile web API Aceess compatibility
    public static String parameter_club_id = "cid"; // For mobile web API Aceess compatibility
    
    public static String header_auth_token = "X-FtApi-Token";
    public static String header_single_use_key = "X-FtApi-SingleUseToken";
    public static String header_device_code = "X-FtApi-DeviceCode"; // For mobile web API Aceess compatibility
    public static String header_member_id = "X-FtApi-MemberId"; // For mobile web API Aceess compatibility
    public static String header_club_id = "X-FtApi-ClubId"; // For mobile web API Aceess compatibility
    
    public static String sess_user_id = "global_user_id";
    
    // Contact fallbacks (these are only in case there is a failure to access an applicable setting in v5.settings)
    public static String invoicing_email_subject = "ForeTees Invoice #%INVOICE_NUMBER% - %INVOICE_DATE%";
    public static String invoicing_email_template = "Please review attached Invoice #%INVOICE_NUMBER% due on %INVOICE_DUE_DATE%";
    public static String invoicing_email_from = "billing@foretees.com";
    public static String invoicing_email_stats = "john.kielkopf@gmail.com";
    
    
    // Keys:  KEEP PRIVATE!!!
    public static String auth_token_gen_key = "80IS<Zr>5i243jq'M8$Rz36i%6D~ZO"; // Don't change unless you know what you're doing
    public static byte[] auth_token_gen_iv = AESencrypt.getMD5("hQq%d/X59;(:+K9UrFJM3VyS#NIJ'-"); // Don't change unless you know what you're doing
    public static String session_auth_token_key = "api_auth_token";
    
    
    // System account:  KEEP PRIVATE!!!
    public static long system_account_user_id = 1;
    public static String system_account_password = "dhheii9087sk39skalm"; // Don't change unless you know what you're doing
    public static String system_account_auth_token = "UP42E0w4IOM/H9o9URLcDxvLwrx2GzwULAESIMbnT0ybOG6nO7K7CtAgvvP0mI5c"; // Don't change unless you know what you're doing
    
    
    // Errors/Notices (Don't use colons ':' in these.)
    public static String error_empty_name = "Name cannot be empty.";
    public static String error_empty_email = "Email cannot be empty.";
    public static String error_null_record = "Null %1s record encountered.";
    public static String error_unknown_condition = "Unknown condition loading %1s record";
    public static String error_db_select = "Error loading %1s record. Having trouble communicating with database server.";
    
    public static String error_delete = "Error deleting %1s record; %2s";
    public static String error_delete_null_id = "Unable to delete %1s. Null %2s id.";
    public static String error_delete_bound = "Error deleting %1s. Record is linked with others and cannot be deleted.";
    public static String error_delete_no_detail_id = "Unable to bind %1s to group. No group id provided.";
    
    public static String error_voiding_null_id = "Unable to void %1s. Null %2s id.";
    public static String error_voiding = "Error voiding %1s record; %2s";
    
    public static String error_sending_to_club = "Error sending invoice #%1s to club; %2s";
    
    public static String error_update = "Error updating %1s record: %2s";
    public static String error_update_duplicate_name = "A %1s with the name \"%2s\" already exists.";
    public static String error_update_duplicate_email = "A %1s with the email address \"%2s\" already exists.";
    public static String error_update_duplicate_binding_record = "A %1s with the ids \"%2s\" and \"%3s\" already exists.";
    public static String error_update_no_detail_id = "Unable to bind %1s to group. No group id provided.";
    public static String error_update_detail_no_id = "Unable to bind %1s to group. No record id provided.";
    public static String error_update_changed = "The %1s \"%2s\" has changed.  The data you are saving may be stale.";
    public static String error_update_changed_prompt = "Are you sure you want to continue?";
    
    public static String error_insert = "Error creating %1s record %2s";
    
    public static String error_savepoint = "Unable to create savepoint.";
    
    public static String error_resultset = "Error loading fields from %1s ResultSet";
    
    public static String error_loading_by_name = "Error loading %1s by name \"%2s\"";
    public static String error_loading_by_id = "Error loading %1s by name \"%2s\"";
    
    public static String error_finding_by_name = "Unable to find %1s with name \"%2s\"";
    public static String error_finding_by_id = "Unable to find %1s with id %2s";
    
    
    // Names
    public static String txt_class_detail = " group_detail";
    
}