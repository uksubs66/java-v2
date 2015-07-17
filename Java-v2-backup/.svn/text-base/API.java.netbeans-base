/*
 * AJAX/JSON Interface for ForeTees API
 * 
 * *** STATUS ***
 * This API is under active development.  
 * It may yet change drastically and should not, yet, be used by organizations outside of ForeTees.
 * 
 * *** USE ***
 * CLUB SPECIFIC CUSTOMS, SQL STATEMENTS AND CONNECTION OBJECTS SHOULD *NOT* BE IMPLEMENTED HERE!  
 * Keep the glue for each command as simple and short as possible. 
 * Create proper objects and handlers for any data you are accessing in com.fortees.api.  Follow existing examples for creating API objects.
 * Do NOT let GET commands access request parameters directly.  Expand the ApiGetCommand object as needed, or create an object and send it as JSON under ApiGetCommand.data.
 * This API, or any of its objects, should NOT be made aware of any legacy modes (RWD, New Skin, premier, etc.)  Focus on use and expansions of UserAccess levels instead.
 * 
 * This is intended as an access layer for existing API data structures/objects in com.foretees.api.*.  
 * Building complex data structures and direct database access must _not_ be done in this servlet.
 * No other servlets should make calls to methods in this servlet.  All common code should be in com.foretees.api.* or com.foretees.common.*.
 * 
 * *** ACCESS LEVELS ***
 * Ensure any command you add checks user/member account access (ua.access object) properly.  
 * Expand ua.access (com.foretees.api.records.UserAccess) object as needed.  
 * Try to use existing access levels, if they are relevant.
 * 
 * *** NOTES ***
 * Where possible, use club_id and member_id instead of club and username.
 * Use Long for record ID parameters in record objects, even when the DB id column is Integer.
 * Avoid primitives (int, long, boolean, etc.) when coding public record object parameters.  Use classes (Integer, Long, Boolean, etc.) instead.  
 *     When null, the classes to not transmit data when encoded as JSON.  This is desired behavior.
 * Avoid setting defaults for most public record object parameters. (Use: public String name;  Do not use: public String name = "";)
 * Avoid club customs (club.equals("my_club")).  Convert the "custom" to a "feature".  Make it usable by any club via club and/or member parameter(s).
 * Avoid use of legacy libraries (Utilities, SystemUtils, etc.).
 * 
 * *** TO-DO ***
 * Clean up VerifyUser.verifyMem() and VerifyUser.verifyPro()
 * .
 * Re-write Common_webapi and move its functionality outside of its servlet.
 * 
 * Move from using "if(ApiRecord.last_error != null)" checks to throwing/catching errors.  
 *  -- (This change may have a slight performance impact in some cases, but will result in cleaner and more stable code.  
 *      It'll require a number of changes, so it should be done before this API is expanded much further.)
 * 
 * Possibly move to using BOON instead of GSON for json serialization/deserialization if performance ever becomes an issue:
 *   https://github.com/boonproject/boon/wiki/Boon-JSON-in-five-minutes
 * 
 */

import java.io.*;
import java.util.*;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// NOTE: If you have to import <code>com.foretees.common.Connect</code> or <code>java.sql.*</code> here, reevaluate your approach. 
//       See "USE" above.
import com.foretees.common.reqUtil;
import com.foretees.common.timeUtil;
import com.foretees.common.Common_skin;
import com.foretees.common.ProcessConstants;
import com.foretees.api.*;
import com.foretees.api.records.*;
import com.foretees.api.reportItems.*;
import com.google.gson.*; // for json
import com.google.gson.reflect.*; // for json

import org.joda.time.*;

/**
 *
 * @author Owner
 */
public class API extends HttpServlet {
    
    
    private enum Command {
        
        // Authentication
        authToken, singleUseKey,
        
        // Users/members
        user, users, userAccess,
        // Member/Membership types
        membershipTypes, memberTypes,
        
        //  Date/time
        intervals,
        
        // Invoice
        invoice, invoiceAndDetails, invoices, 
        invoicesUnpaid, invoicesPastdue, invoicesUnsent, invoicesByClubInvoicingId, invoiceItem, invoiceItems, invoiceTerms,
        invoiceSetPaid, invoiceSetVoided, invoiceSendToClub,
        
        invoiceDetailsByUnpaidCommission,
        
        invoicesFromClubInvoicingRules,
        
        invoiceGetPdf,
        
        invoiceGetHtml,
        
        // Invoice details
        invoiceItemType, invoiceItemTypes, invoiceItemsByTypeId, 
        invoiceItemsByClubInvoicingId, invoiceItemsByInvoiceId, 
        invoiceItemsByClubInvoicingRuleId, invoicingRuleTypes, 
        
        invoiceDetailSetCommissionPaid,
        
        
        // Invoice rules
        invoicingRuleTypeData, invoicingRuleTypeDataByClubInvoicingId, invoicingRuleTypeDataByClubInvoicingRuleId,
        invoicingRuleTypeDataByClubInvoicingRuleDetailId, 
        
        // Tax rates/rules/groups
        taxRate, taxRates, taxGroup, taxGroups, taxGroupDetail, taxGroupDetails, taxGroupDetailsByGroupId,
        
        //  Club records
        clubs, clubInvoicing, clubInvoicingRule, clubInvoicingRules, clubInvoicingRuleDetail, clubInvoicingRuleDetails,
        
        // Invoice Payments
        //payments, paymentDetails, paymentTypes,
        
        // Commission Reports
        commissionBySalesPersonAndDate,

        // Dashboard reports
        loginsByDayByClub,
        clubMembersByBirthDate,
        clubVipMembersWithReservations,
        clubActivitySignupCountsByDate,
        clubCalendar,
        
        // Settings
        foreteesSetting,
        foreteesSettings,
        foreteesAnnouncement,
        foreteesAnnouncements,
        foreteesAnnouncementByMember,
        foreteesAnnouncementsByMember,
        foreteesAnnouncementsByActiveDate,
        foreteesAnnouncementsUnreadByMember,
        
        // Non-json Export -- used by postExport method
        xml, excel, // same -- for pqGrid compatibility
        csv,
        blob,
        
        // Fail
        undefined, invalid;
        
        public static Command fromString(String s) {
            try {
                return valueOf(s);
            } catch (Exception e) {
                return invalid;
            }
        }
    }
    
    private static final Set<String> noAuthCommands = getNoAuthCommands();
    
    
    
    private static final String command_undefined = "undefined";
    
    private static final String error_access_denied = "Access denied for command: ";
    private static final String error_unexpected_fault = "Unexpected fault: ";
    private static final String error_club_db_connection = "Unable to establish database connection.";
    private static final String error_expired_session = "API Access denied. Session expired.";
    private static final String error_no_command = "No API command specified.";
    private static final String error_json_command_syntax = "Invalid json command object: ";
    private static final String error_json_request_syntax = "Invalid json object for command: ";
    private static final String error_duplicate_command = "API Error. Command requested more than once: ";
    private static final String error_no_id = "No record ID given.";
    private static final String error_no_club_id = "No club ID given.";
    private static final String error_must_be_used_alone = "Command cannot be used with others:";
    private static final String error_no_access_to_resource = "You do not have access to view the requested resource.";
    
    private static Set<String> getNoAuthCommands(){
        Set<String> result = new HashSet<String>();
        // Commands that don't require authentication
        result.add("authToken");
        return result;
    }
    
    @Override
    public void init() throws ServletException {
        
        //ApiTasks.start();  //We'll do this in Login.java for now
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.  Only place commands for RETRIVING records here.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String method = reqUtil.getParameterString(request, "method", "GET");
        if(method.equals("EXPORT")){
            getExport(request, response);
            return;
        }
        
        Common_skin.setNoCacheJson(response);
        
        Gson gson = new Gson();
        
        List<ApiGetCommand> commands = null;

        ApiResponse apiResp = new ApiResponse();
        
        
        String command_json = reqUtil.getQueryString(request, "commands", null);
        if(command_json == null){
            // Larger command lists may be from request body (POSTed)
            command_json = reqUtil.getRequestBody(request).toString();
            if(command_json != null && command_json.isEmpty()){
                command_json = null;
            }
        }
        
        String single_command = reqUtil.getParameterString(request, ApiConstants.parameter_command, null);
        
        if (single_command != null) {
            // Single command sent as individual parameters.
            // Load ApiGetCommand with values and inject into ArrayList
            commands = new ArrayList<ApiGetCommand>();
            ApiGetCommand cmd = new ApiGetCommand(
                    single_command,
                    (request.getParameter("as_object") != null),
                    (request.getParameter("exclude_disabled") != null),
                    reqUtil.getParameterLong(request, "id", null),
                    reqUtil.getParameterLong(request, "detail_id", null),
                    reqUtil.getParameterLong(request, "club_id", null),
                    reqUtil.getParameterLong(request, "parent_id", null),
                    reqUtil.getParameterLong(request, "member_id", null),
                    reqUtil.getParameterInteger(request, "activity_id", null),
                    reqUtil.getParameterString(request, "date_start", null),
                    reqUtil.getParameterString(request, "date_end", null),
                    reqUtil.getParameterInteger(request, "req_month", null),
                    reqUtil.getParameterInteger(request, "req_year", null),
                    request.getParameter("club_cal") != null?true:null,
                    request.getParameter("events_only") != null?true:null,
                    request.getParameterValues("string_list") != null?Arrays.asList(request.getParameterValues("string_list")):null,
                    reqUtil.getParameterString(request, "data", null));
            commands.add(cmd);
        } else if (command_json != null) {
            // Multiple commands - decode into List of ApiGetCommands
            try {
                commands = Arrays.asList(gson.fromJson(command_json, ApiGetCommand[].class));
            } catch (Exception e) {
                apiResp.error = error_json_command_syntax + e.toString();
            }
        }
        
        User ua = ApiAccess.getUser(request);
        
        apiResp.auth_token = ua.auth_token;
        if(ua.last_error != null){
            // Unable to authenticate user.
            // Check if any of our commands require authentication
            for (ApiGetCommand cmd : commands) {
                if (!noAuthCommands.contains(cmd.command)) {
                    // Command requires authentication.  Throw error reported by getUser.
                    apiResp.error = ua.last_error;
                }
            }
        }
        
        // Should this be moved to ApiAccess.getUser, with a time_zone parameter added to User?
        DateTimeZone tz;
        if(ua.club != null){
            tz = timeUtil.getClubTimeZone(ApiCommon.getConnection(ua.club));
        } else {
            tz = timeUtil.getClubTimeZone("");
        }
        
        if(reqUtil.getQueryString(request, "debug_echo", null) != null){
            // Used for clients to debug their sent commands
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(commands));
            out.close();
            return;
        }
        
        //apiResp.debug.put("user_record", ua); // Return authenticated user record in debug (remove this before production!)
        
        Set<String> command_check = new HashSet<String>();

        if ((commands == null || commands.isEmpty()) && apiResp.error == null) {
            apiResp.error = error_no_command;
        } else if (apiResp.error == null){
            // Proccess command List
            for (ApiGetCommand cmd : commands) {
                
                // Set/force command defaults if user is club bound (Member, Pro, etc.)
                if(ua.club_id != null && ua.access.bind_to_club){
                    // This is a club user/member.  Force the command to be bound to their club.
                    cmd.club_id = ua.club_id;
                }

                // Check if we've already processed a command with the same request_id/command
                if (command_check.contains(cmd.request_id != null?cmd.request_id:cmd.command)) {
                    // We cannot execute the same command, with the same request ID, twice.
                    if (cmd.request_id != null) {
                        apiResp.error = error_duplicate_command + cmd.command + ": " + cmd.request_id;
                    } else {
                        apiResp.error = error_duplicate_command + cmd.command;
                    }

                } else {
                    // Add request_id/command to check list
                    command_check.add(cmd.request_id != null?cmd.request_id:cmd.command);
                    // Run the command
                    Command ecmd = Command.fromString(cmd.command);
                    switch (ecmd) {

                        case authToken:
                            // This, and authTokens in general, need to be expanded to support club bound users (Members, Proshop, etc.)
                            String token = null;
                            if (cmd.data != null) {
                                // use credentials passed in cmd
                                ApiCredentials record = new ApiCredentials();
                                try {
                                    record = gson.fromJson(cmd.data, ApiCredentials.class);
                                    token = ApiAccess.getAuthenticationToken(record.club, record.username, record.password);
                                } catch (Exception e) {
                                    apiResp.error = jsonRequestError(cmd.data, e.toString());
                                }
                                if (token == null && apiResp.error == null) {
                                    apiResp.error = "Invalid API authentication credentials.";
                                }
                            } else {
                                token = ApiAccess.getAuthenticationToken(request);
                                if (token == null && apiResp.error == null) {
                                    apiResp.error = error_expired_session;
                                }
                            }
                            if (token != null) {
                                addResult(cmd, apiResp, token);
                                apiResp.auth_token = token;
                            }
                            break;

                        // Warning:  this "GET" command creates time limited SingleUseAccessKey records.
                        case singleUseKey:
                            if (ua.access.single_use_key) {
                                SingleUseAccessKey data = new SingleUseAccessKey(ua);
                                if (data.last_error == null) {
                                    addResult(cmd, apiResp, data.access_key);
                                } else {
                                    apiResp.error = data.last_error;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        // foretees "users" (NOT club members/proshop)
                        case users:
                            if (ua.access.manage_users) {
                                List<User> data = User.getList(cmd.id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, User> dataMap = new LinkedHashMap<Long, User>();
                                        for (User r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;
                            
                        // foretees "settings" (NOT club members/proshop)
                        case foreteesSettings:
                            if (ua.access.manage_settings && !ua.access.bind_to_club) {
                                List<ForeTeesSetting> data = ForeTeesSetting.getList(cmd.id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, ForeTeesSetting> dataMap = new LinkedHashMap<Long, ForeTeesSetting>();
                                        for (ForeTeesSetting r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;
                            
                        case foreteesAnnouncements:
                            if (ua.access.manage_announcements && !ua.access.bind_to_club) {
                                List<ForeTeesAnnouncement> data = ForeTeesAnnouncement.getListById(cmd.id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, ForeTeesAnnouncement> dataMap = new LinkedHashMap<Long, ForeTeesAnnouncement>();
                                        for (ForeTeesAnnouncement r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;
                        
                        case foreteesAnnouncementsByMember:
                        case foreteesAnnouncementsByActiveDate:
                        case foreteesAnnouncementsUnreadByMember:
                            if (ua.access.view_foretees_announcements && ua.access.bind_to_club) {
                                List<ForeTeesAnnouncement> data;
                                //int load_mode = ForeTeesAnnouncement.getModeByActivityId(cmd.activity_id);
                                if(ecmd.equals(Command.foreteesAnnouncementsUnreadByMember)){
                                    data = ForeTeesAnnouncement.getUnreadByUsername(ForeTeesAnnouncement.MODE_ALL, ua.club, ua.username);
                                } else if (ecmd.equals(Command.foreteesAnnouncementsByActiveDate)) {
                                    data = ForeTeesAnnouncement.getActiveByUsername(ForeTeesAnnouncement.MODE_ALL, ua.club, ua.username);
                                } else {
                                    data = ForeTeesAnnouncement.getVisableByUsername(ForeTeesAnnouncement.MODE_ALL, ua.club, ua.username);
                                }
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, ForeTeesAnnouncement> dataMap = new LinkedHashMap<Long, ForeTeesAnnouncement>();
                                        for (ForeTeesAnnouncement r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;
                            
                       case foreteesAnnouncementByMember:
                            if (ua.access.view_foretees_announcements && ua.access.bind_to_club) {
                                ForeTeesAnnouncement data = new ForeTeesAnnouncement(cmd.id, ua.club, ua.username);
                                if (data.last_error == null) {
                                    addResult(cmd, apiResp, data);
                                    // Mark it as read
                                    data.markRead();
                                } else {
                                    apiResp.error = data.last_error;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        // foretees "users" (NOT club members/proshop)
                        case userAccess:
                            if (ua.access.manage_users) {
                                List<UserAccess> data = UserAccess.getList(cmd.id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, UserAccess> dataMap = new LinkedHashMap<Long, UserAccess>();
                                        for (UserAccess r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case taxRates:
                            if (ua.access.manage_invoices) {
                                List<TaxRate> data = TaxRate.getList(cmd.id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, TaxRate> dataMap = new LinkedHashMap<Long, TaxRate>();
                                        for (TaxRate r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case taxGroups:
                            if (ua.access.manage_invoices) {
                                List<TaxGroup> data = TaxGroup.getList(cmd.id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, TaxGroup> dataMap = new LinkedHashMap<Long, TaxGroup>();
                                        for (TaxGroup r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case taxGroupDetails:
                        case taxGroupDetailsByGroupId:
                            if (ua.access.manage_invoices) {
                                List<TaxGroupDetail> data;

                                if (ecmd.equals(Command.taxGroupDetails)) {
                                    data = TaxGroupDetail.getList(cmd.id);
                                } else {
                                    data = TaxGroupDetail.getListByGroupId(cmd.id);
                                }

                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, TaxGroupDetail> dataMap = new LinkedHashMap<Long, TaxGroupDetail>();
                                        for (TaxGroupDetail r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoiceItemTypes:
                            if (ua.access.manage_invoices) {
                                List<InvoiceItemType> data = InvoiceItemType.getList(cmd.id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, InvoiceItemType> dataMap = new LinkedHashMap<Long, InvoiceItemType>();
                                        for (InvoiceItemType r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoice:
                            if (ua.access.manage_invoices || ua.access.view_invoices) {
                                Invoice data;
                                if ((cmd.id == null || cmd.id == 0) && ua.access.manage_invoices) {
                                    // New, empty, invoice record.  
                                    data = new Invoice();
                                    data.details = new ArrayList<InvoiceDetail>();
                                    if (cmd.parent_id != null) {
                                        // Set some defaults.
                                        ClubInvoicing ci = new ClubInvoicing(cmd.parent_id);
                                        if (ci.last_error == null) {
                                            data.bill_to = ci.address;
                                            data.purchase_order = ci.default_po;
                                        } else {
                                            apiResp.error = ci.last_error;
                                            break;
                                        }
                                    }
                                } else {
                                    data = new Invoice(cmd.id, true); // load invoice, and its details
                                }
                                if (data.last_error == null) {
                                    if (ua.access.bind_to_club && !data.club_id.equals(ua.club_id)) {
                                        // Check access for proshop/club bound user
                                        apiResp.error = error_no_access_to_resource;
                                        break;
                                    }
                                    addResult(cmd, apiResp, data);
                                } else {
                                    apiResp.error = data.last_error;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoiceGetPdf:
                            if (ua.access.manage_invoices || ua.access.view_invoices) {
                                // NOTE:  Access for club bound user is checked in the subsequent call ExportFile will make to invoiceGetHtml
                                if (commands.size() > 1) {
                                    apiResp.error = error_must_be_used_alone;
                                    break;
                                }
                                apiResp.alternate_content_type = "plain/text";
                                if (cmd.id == null) {
                                    apiResp.error = "No invoice ID provided.";
                                    break;
                                }
                                SingleUseAccessKey tmpKey = new SingleUseAccessKey(ua); // Get a single use access key for ExportFile to use in its callback
                                if (tmpKey.last_error != null) {
                                    apiResp.error = tmpKey.last_error;
                                    break;
                                }
                                // Create a URL for our callback
                                String url = reqUtil.getServletUrl(request) + "?s_sdbg=on&"+ApiConstants.parameter_command+"=invoiceGetHtml&id=" + cmd.id // wkhtmltopdf doesn't like the compiled scripts for some reason, so use s_sdbg=on until I figure it out.
                                        + "&" + ApiConstants.parameter_single_use_key + "=" + URLEncoder.encode(tmpKey.access_key, "UTF-8");
                                // Set a filename for the download
                                String file_name = "foretees_invoice_" + cmd.id + "_" + timeUtil.getDateForFileName() + ".pdf"; // Probably should move this to a Invoice.getFilename() method.
                                apiResp.export = url;
                                // Download an HTML invoice and convert it to PDF 
                                // (Much of the Invoice HTML is created using client-side javascript.
                                //  ExportFile's loadPdfByUrl uses wkhtmltopdf to render the page as a client would, 
                                //  and then converts the render to a PDF.)
                                ExportFile data = new ExportFile().loadPdfByUrl(url, file_name);
                                if (data.last_error == null) {
                                    apiResp.alternate_content_type = data.media_type;
                                    apiResp.byte_export = data.byte_data;
                                    response.setHeader("content-disposition", "attachment; filename=" + data.file_name);
                                } else {
                                    apiResp.error = data.last_error;
                                }

                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoiceGetHtml:
                            // NOTE:  HTML generated here is useless unless it is rendered in a javascript enabled HTML5 compliant browser.
                            //        The included javascript and CSS does all the heavy lifting in rendering and pagination of the Invoice.
                            if (commands.size() > 1) {
                                apiResp.error = error_must_be_used_alone;
                                break;
                            }
                            apiResp.alternate_content_type = "text/html"; // This API call will be returning HTML, not JSON
                            if (ua.access.manage_invoices || ua.access.view_invoices) {
                                if (cmd.id == null) {
                                    apiResp.error = "No invoice ID provided.";
                                } else {
                                    Invoice data = new Invoice(cmd.id, true, true);
                                    if (data.last_error == null) {
                                        if (ua.access.bind_to_club && !data.club_id.equals(ua.club_id)) {
                                            // Check access for proshop/club bound user
                                            apiResp.error = error_no_access_to_resource;
                                            break;
                                        }
                                        request.setAttribute(ProcessConstants.RQA_RWD, true); // Invoice uses RWD
                                        request.setAttribute(ProcessConstants.RQA_INVOICE, true); // Force Invoice Mode
                                        StringBuilder html = new StringBuilder();
                                        html.append("<!DOCTYPE html><html lang=\"en-US\"><head><title>ForeTees Invoice #");
                                        html.append(cmd.id);
                                        html.append("</title>");
                                        html.append(Common_skin.getScripts("", 0, null, request, false));
                                        html.append("<script> $(function(){ftinvoice.display($('body'),");
                                        html.append(gson.toJson(data));
                                        html.append(")}); </script>");
                                        html.append("</head>");
                                        html.append("<body class=\"ftInvoiceBody\"></body>");
                                        html.append("</html>");
                                        apiResp.export = html.toString();
                                    } else {
                                        apiResp.error = data.last_error;
                                    }
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoicingRuleTypeDataByClubInvoicingId:
                            if (ua.access.manage_invoices) {
                                InvoicingRuleTypeData data = null;
                                // Lookup club invoicing rule detail using club invoicing rule id.
                                // cmd.id holds club_invoicing_id
                                // cmd.detail_id holds invoice_item_id
                                // cmd.data optionally holds JSON data for InvoicingRuleTypeData
                                // cmd.start_date optionally holds string date for "next_date" rule
                                if (cmd.id != null) {
                                    ClubInvoicingRule ci = new ClubInvoicingRule(cmd.id);
                                    if (ci.last_error != null) {
                                        apiResp.error = ci.last_error;
                                        break;
                                    }
                                    Long next_date = null;
                                    if (cmd.validateStartDate()) {
                                        next_date = cmd.getUnixStartDate();
                                    } else {
                                        apiResp.error = "No valid date specified";
                                        break;
                                    }
                                    if (cmd.detail_id == null) {
                                        apiResp.error = "No invoice item id specified";
                                        break;
                                    } else {
                                        ClubInvoicingRuleDetail cird = new ClubInvoicingRuleDetail();
                                        // No ClubInvoicingRuleDetail for this match.
                                        // Create an empty rule
                                        cird = new ClubInvoicingRuleDetail();
                                        // Fill it with some data
                                        cird.club_id = ci.club_id;
                                        cird.club_invoicing_id = ci.id;
                                        cird.invoice_item_id = cmd.detail_id;
                                        cird.interval_id = (long) 1;  // keep errors from triggering.

                                        if (cmd.data != null) {
                                            // JSON for rule_type_data was passed.  use it.
                                            cird.invoicing_rule_type_data = cmd.data;
                                        }
                                        cird.next_date = next_date;
                                        data = cird.getInvoicingRuleTypeData();
                                        if (data.last_error == null && cmd.force_refresh) {
                                            data.loadBillableCounts();
                                        }
                                    }

                                } else {
                                    apiResp.error = "No club invoicing id specified";
                                    break;
                                }

                                if (data != null && data.last_error == null) {
                                    addResult(cmd, apiResp, data);
                                } else if (data != null) {
                                    apiResp.error = data.last_error;
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoicingRuleTypeDataByClubInvoicingRuleId:
                            if (ua.access.manage_invoices) {
                                InvoicingRuleTypeData data = null;
                                // Lookup club invoicing rule detail using club invoicing rule id.
                                // cmd.id holds club_invoicing_rule_id
                                // cmd.detail_id holds invoice_item_id
                                // cmd.data optionally holds JSON data for InvoicingRuleTypeData
                                // cmd.start_date optionally holds string date for "next_date" rule
                                if (cmd.id != null) {
                                    ClubInvoicingRule cir = new ClubInvoicingRule(cmd.id);
                                    if (cir.last_error != null) {
                                        apiResp.error = cir.last_error;
                                        break;
                                    }
                                    Long next_date = cir.next_date;
                                    if (cmd.validateStartDate()) {
                                        next_date = cmd.getUnixStartDate();
                                    }
                                    if (cmd.detail_id == null) {
                                        apiResp.error = "No invoice item id specified";
                                        break;
                                    } else {
                                        ClubInvoicingRuleDetail cird = new ClubInvoicingRuleDetail(cmd.id, cmd.detail_id);
                                        if (cird.last_error != null) {
                                            // No ClubInvoicingRuleDetail for this match.
                                            // Create an empty rule
                                            cird = new ClubInvoicingRuleDetail();
                                            // Fill it with some data
                                            cird.club_id = cir.club_id;
                                            cird.club_invoicing_id = cir.club_invoicing_id;
                                            cird.invoice_item_id = cmd.detail_id;
                                            cird.interval_id = cir.interval_id;
                                        }
                                        if (cmd.data != null) {
                                            // JSON for rule_type_data was passed.  use it.
                                            cird.invoicing_rule_type_data = cmd.data;
                                        }
                                        cird.next_date = next_date;
                                        data = cird.getInvoicingRuleTypeData();
                                        if (data.last_error == null && cmd.force_refresh) {
                                            data.loadBillableCounts();
                                        }
                                    }

                                } else {
                                    apiResp.error = "No club invoicing rule id specified";
                                    break;
                                }

                                if (data != null && data.last_error == null) {
                                    addResult(cmd, apiResp, data);
                                } else if (data != null) {
                                    apiResp.error = data.last_error;
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoicingRuleTypeDataByClubInvoicingRuleDetailId:
                            if (ua.access.manage_invoices) {
                                InvoicingRuleTypeData data = null;
                                // Lookup club invoicing rule detail using club invoicing rule detail id.
                                // cmd.id holds club_invoicing_rule_detail_id
                                // cmd.detail_id holds invoice_item_id
                                // cmd.data optionally holds JSON data for InvoicingRuleTypeData
                                // cmd.start_date optionally holds string date for "next_date" rule
                                if (cmd.id != null) {
                                    ClubInvoicingRuleDetail cird = new ClubInvoicingRuleDetail().loadByIdAndInvoiceItemId(cmd.id, cmd.detail_id);
                                    if (cird.last_error != null) {
                                        apiResp.error = cird.last_error;
                                        break;
                                    }
                                    if (cmd.data != null) {
                                        // JSON for rule_type_data was passed.  use it.
                                        cird.invoicing_rule_type_data = cmd.data;
                                    }
                                    if (cmd.validateStartDate()) {
                                        cird.next_date = cmd.getUnixStartDate();
                                    }
                                    data = cird.getInvoicingRuleTypeData();
                                    if (data.last_error == null && cmd.force_refresh) {
                                        data.loadBillableCounts();
                                    }
                                } else {
                                    apiResp.error = "No club invoicing rule detail id specified";
                                    break;
                                }

                                if (data != null && data.last_error == null) {
                                    addResult(cmd, apiResp, data);
                                } else if (data != null) {
                                    apiResp.error = data.last_error;
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoiceTerms:
                            if (ua.access.manage_invoices) {
                                List<InvoiceTerms> data = InvoiceTerms.getList(cmd.id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, InvoiceTerms> dataMap = new LinkedHashMap<Long, InvoiceTerms>();
                                        for (InvoiceTerms r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoices:
                        case invoicesUnpaid:
                        case invoicesUnsent:
                        case invoicesPastdue:
                        case invoicesByClubInvoicingId:
                            if (ua.access.manage_invoices) {
                                List<Invoice> data = null;
                                switch (ecmd) {
                                    case invoicesUnpaid:
                                        data = Invoice.getUnpaidInvoices(cmd.id);
                                        break;
                                    case invoicesUnsent:
                                        data = Invoice.getUnsentInvoices(cmd.id);
                                        break;
                                    case invoicesPastdue:
                                        data = Invoice.getPastDueInvoices(cmd.id);
                                        break;
                                    case invoicesByClubInvoicingId:
                                        data = Invoice.getList(cmd.id);
                                        break;
                                    case invoices:
                                        data = Invoice.getList();
                                        break;
                                }
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, Invoice> dataMap = new LinkedHashMap<Long, Invoice>();
                                        for (Invoice r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoiceDetailsByUnpaidCommission:
                            if (ua.access.manage_invoices) {
                                List<InvoiceDetail> data = null;
                                switch (ecmd) {
                                    case invoiceDetailsByUnpaidCommission:
                                        data = InvoiceDetail.getUnpaidCommission(cmd.id);
                                        break;
                                }
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, InvoiceDetail> dataMap = new LinkedHashMap<Long, InvoiceDetail>();
                                        for (InvoiceDetail r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoiceItems:            // (Probably should have called this "Products", not InvoiceItems, since it's easy to confuse with InvoiceDetails 
                        case invoiceItemsByInvoiceId: //   -- but Quick Books called them "Items", so here we are.)
                        case invoiceItemsByClubInvoicingId:
                        case invoiceItemsByClubInvoicingRuleId:
                        case invoiceItemsByTypeId:
                            if (ua.access.manage_invoices) {
                                List<InvoiceItem> data = null;

                                switch (ecmd) {
                                    case invoiceItems:
                                        data = InvoiceItem.getList(cmd.id);
                                        break;

                                    case invoiceItemsByInvoiceId:
                                        if (cmd.id == null) {
                                            apiResp.error = error_no_id;
                                        } else {
                                            Invoice iv = new Invoice(cmd.id);
                                            if (iv.last_error != null) {
                                                apiResp.error = iv.last_error;
                                            } else if (iv.club_invoicing_id == null) {
                                                apiResp.error = error_unexpected_fault;
                                            } else {
                                                data = InvoiceItem.getListByClubInvoicingId(iv.club_invoicing_id);
                                            }
                                        }
                                        break;

                                    case invoiceItemsByClubInvoicingRuleId:
                                        if (cmd.id == null) {
                                            apiResp.error = error_no_id;
                                        } else {
                                            ClubInvoicingRule cir = new ClubInvoicingRule(cmd.id);
                                            if (cir.last_error != null) {
                                                apiResp.error = cir.last_error;
                                            } else if (cir.club_invoicing_id == null) {
                                                apiResp.error = error_unexpected_fault;
                                            } else {
                                                data = InvoiceItem.getListByClubInvoicingId(cir.club_invoicing_id);
                                            }
                                        }
                                        break;

                                    case invoiceItemsByClubInvoicingId:
                                        data = InvoiceItem.getListByClubInvoicingId(cmd.id);
                                        break;

                                    default:
                                        data = InvoiceItem.getListByTypeId(cmd.id);
                                        break;
                                }

                                if (apiResp.error == null && data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, InvoiceItem> dataMap = new LinkedHashMap<Long, InvoiceItem>();
                                        for (InvoiceItem r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else if (apiResp.error == null) {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case clubs:
                            if (ua.access.manage_invoices) {
                                List<Club> data;
                                if (cmd.parent_id != null) {
                                    data = Club.getListByClubInvoicingId(cmd.parent_id);
                                } else {
                                    data = Club.getList(cmd.club_id != null ? cmd.club_id : cmd.id);
                                }

                                if (data != null) {
                                    if (!cmd.as_object) {
                                        if (data.size() == 1) {
                                            // Load extended information if only loading a single club
                                            // (expensive call, so we don't want to do it for all)
                                            data.get(0).loadExtended();
                                        }
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, Club> dataMap = new LinkedHashMap<Long, Club>();
                                        for (Club r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case clubInvoicing:
                            if (ua.access.manage_invoices) {
                                List<ClubInvoicing> data = ClubInvoicing.getList(cmd.id, cmd.club_id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, ClubInvoicing> dataMap = new LinkedHashMap<Long, ClubInvoicing>();
                                        for (ClubInvoicing r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case clubInvoicingRules:
                            if (ua.access.manage_invoices) {
                                List<ClubInvoicingRule> data = ClubInvoicingRule.getList(cmd.id, cmd.parent_id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, ClubInvoicingRule> dataMap = new LinkedHashMap<Long, ClubInvoicingRule>();
                                        for (ClubInvoicingRule r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        // WARNING: This "GET" command creates and modifies records!!
                        //          New Invoices can/will be created and returned.
                        case invoicesFromClubInvoicingRules:
                            if (ua.access.manage_invoices) {
                                List<Invoice> data = ClubInvoicingRule.runReady(); // Run any rules that are ready to run.
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, Invoice> dataMap = new LinkedHashMap<Long, Invoice>();
                                        for (Invoice r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case clubInvoicingRuleDetails:
                            if (ua.access.manage_invoices) {
                                List<ClubInvoicingRuleDetail> data = ClubInvoicingRuleDetail.getList(cmd.id, cmd.parent_id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, ClubInvoicingRuleDetail> dataMap = new LinkedHashMap<Long, ClubInvoicingRuleDetail>();
                                        for (ClubInvoicingRuleDetail r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case invoicingRuleTypes:
                            if (ua.access.manage_invoices) {
                                List<InvoicingRuleType> data = InvoicingRuleType.getList(cmd.id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, InvoicingRuleType> dataMap = new LinkedHashMap<Long, InvoicingRuleType>();
                                        for (InvoicingRuleType r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case membershipTypes:
                            if (ua.access.manage_invoices || ua.access.bind_to_club) {
                                // If activity id is null, all activities will be returned.
                                List<MembershipType> data = MembershipType.getListByActivityId(cmd.activity_id, cmd.club_id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, MembershipType> dataMap = new LinkedHashMap<Long, MembershipType>();
                                        for (MembershipType r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case memberTypes:
                            if (ua.access.manage_invoices || ua.access.bind_to_club) {
                                List<MemberType> data = MemberType.getList(cmd.club_id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, MemberType> dataMap = new LinkedHashMap<Long, MemberType>();
                                        for (MemberType r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case intervals:
                            if (ua.access.manage_invoices) {
                                List<DbInterval> data = DbInterval.getList(cmd.id);
                                if (data != null) {
                                    if (!cmd.as_object) {
                                        addResult(cmd, apiResp, data);
                                    } else {
                                        LinkedHashMap<Long, DbInterval> dataMap = new LinkedHashMap<Long, DbInterval>();
                                        for (DbInterval r : data) {
                                            dataMap.put(r.id, r);
                                        }
                                        addResult(cmd, apiResp, dataMap);
                                    }
                                } else {
                                    apiResp.error = error_unexpected_fault;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case commissionBySalesPersonAndDate:
                            if (ua.access.manage_invoices) {
                                if (cmd.setValidOrNullDates()) {
                                    List<Commission> data = Commission.getListByDate(cmd.id, cmd.date_start, cmd.date_end);
                                    if (data != null) {
                                        if (!cmd.as_object) {
                                            addResult(cmd, apiResp, data);
                                        } else {
                                            LinkedHashMap<Long, Commission> dataMap = new LinkedHashMap<Long, Commission>();
                                            for (Commission r : data) {
                                                dataMap.put(r.id, r);
                                            }
                                            addResult(cmd, apiResp, dataMap);
                                        }
                                    } else {
                                        apiResp.error = error_unexpected_fault;
                                    }
                                } else {
                                    apiResp.error = cmd.last_error;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case loginsByDayByClub:
                            if (ua.access.manager_portal) {
                                if (cmd.validateDates()) {
                                    List<LoginCount> data = LoginCount.getList(ua.club, cmd.date_start, cmd.date_end);
                                    if (data != null) {
                                        if (!cmd.as_object) {
                                            addResult(cmd, apiResp, data);
                                        } else {
                                            LinkedHashMap<String, LoginCount> dataMap = new LinkedHashMap<String, LoginCount>();
                                            for (LoginCount r : data) {
                                                dataMap.put(r.date, r);
                                            }
                                            addResult(cmd, apiResp, dataMap);
                                        }
                                    } else {
                                        apiResp.error = error_unexpected_fault;
                                    }
                                } else {
                                    apiResp.error = cmd.last_error;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case clubActivitySignupCountsByDate:
                            if (ua.access.manager_portal) {
                                if (!cmd.validateEndDate()) {
                                    cmd.date_end = cmd.date_start;
                                }
                                if (cmd.validateDates()) {
                                    List<ActivitySignupCounts> data = ActivitySignupCounts.getListByDate(cmd.club_id, cmd.activity_id, cmd.date_start, cmd.date_end);
                                    if (data != null) {
                                        if (!cmd.as_object) {
                                            addResult(cmd, apiResp, data);
                                        } else {
                                            LinkedHashMap<String, ActivitySignupCounts> dataMap = new LinkedHashMap<String, ActivitySignupCounts>();
                                            for (ActivitySignupCounts r : data) {
                                                dataMap.put(r.date, r);
                                            }
                                            addResult(cmd, apiResp, dataMap);
                                        }
                                    } else {
                                        apiResp.error = error_unexpected_fault;
                                    }
                                } else {
                                    apiResp.error = cmd.last_error;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case clubVipMembersWithReservations:
                        case clubMembersByBirthDate:
                            if (ua.access.manager_portal) {
                                if (!cmd.validateEndDate()) {
                                    cmd.date_end = cmd.date_start;
                                }
                                if (cmd.validateDates()) {
                                    List<ClubMember> data;
                                    if (ecmd.equals(Command.clubVipMembersWithReservations)) {
                                        data = ClubMember.getVipsWithReservations(cmd.club_id, ua.member_id, cmd.activity_id, cmd.date_start, cmd.date_end, ClubMember.load_public);
                                    } else {
                                        data = ClubMember.getListByBirthDate(cmd.club_id, cmd.date_start, cmd.date_end, ClubMember.load_public);
                                    }
                                    if (data != null) {
                                        if (!cmd.as_object) {
                                            addResult(cmd, apiResp, data);
                                        } else {
                                            LinkedHashMap<Long, ClubMember> dataMap = new LinkedHashMap<Long, ClubMember>();
                                            for (ClubMember r : data) {
                                                dataMap.put(r.id, r);
                                            }
                                            addResult(cmd, apiResp, dataMap);
                                        }
                                    } else {
                                        apiResp.error = error_unexpected_fault;
                                    }
                                } else {
                                    apiResp.error = cmd.last_error;
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        // 
                        // clubCalendar is currently an interface to the legacy calendar API; Common_webapi.getCalendarData.
                        //  TODO: Common_webapi.getCalendarData needs to be rewritten and moved to com.foretees.api.
                        //        Some database/indexing changes are needed to improve performance of event/reservation lookups in the calendar
                        case clubCalendar:
                            if (ua.access.manager_portal) {
                                if (cmd.date_start != null && cmd.date_end != null && !cmd.validateDates()) {
                                    apiResp.error = cmd.last_error;
                                } else {
                                    cmd.last_error = null;
                                    Map<String, Object> calMapW = Common_webapi.getCalendarData(request, response, ProcessConstants.ALL_ACTIVITIES,
                                            ua.username, "", ua.club, ua.caller, ua.mship, ua.mtype, System.currentTimeMillis(), cmd, null);
                                    if (cmd.last_error == null) {
                                        Map<String, Object> calMapC = (Map<String, Object>) calMapW.get("foreTeesClubCalendarData");
                                        if (calMapC == null) {
                                            calMapC = (Map<String, Object>) calMapW.get("foreTeesMemberCalendarData");
                                        }
                                        if (calMapC == null) {
                                            apiResp.error = "Unexpected response from calendar API";
                                        } else {
                                            List<Map<String, Object>> data = (List<Map<String, Object>>) calMapC.get("days");
                                            if (data != null) {
                                                if (!cmd.as_object) {
                                                    addResult(cmd, apiResp, data);
                                                } else {
                                                    LinkedHashMap<String, Map<String, Object>> dataMap = new LinkedHashMap<String, Map<String, Object>>();
                                                    for (Map<String, Object> r : data) {
                                                        dataMap.put((String) r.get("date"), r);
                                                    }
                                                    addResult(cmd, apiResp, dataMap);
                                                }
                                            } else {
                                                apiResp.error = error_unexpected_fault;
                                            }
                                        }
                                    } else {
                                        apiResp.error = cmd.last_error;
                                    }
                                }
                            } else {
                                apiResp.error = error_access_denied;
                            }
                            break;

                        case undefined:
                            apiResp.error = error_no_command;
                            break;

                        case invalid:
                            apiResp.error = "Invalid command: GET \"" + cmd.command + "\"";
                            break;

                        default:
                            apiResp.error = "Command \"" + cmd.command + "\" cannot be used in GET.";
                            break;

                    }
                }

                if (checkError(apiResp, "GET " + cmd.command)) {
                    break; // exit command loop
                }
            }
        }
        
        apiResp.date = timeUtil.getDbDate(timeUtil.getDateTime(tz, timeUtil.getCurrentUnixTime())[timeUtil.DATE]);
        
        sendResponse(apiResp, response);

    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Simulate PUT and DELETE requests through POST
        String method = reqUtil.getQuery(request, "method");
        if(method != null){
            if(method.equals("PUT")){
                doPut(request, response);
                return;
            } else if (method.equals("DELETE")){
                doDelete(request, response);
                return;
            } else if (method.equals("GET")){
                doGet(request, response);
                return;
            } else if (method.equals("EXPORT")){
                // Not a real HTTP method -- must only be used through POST
                postExport(request, response);
                return;
            }
        }

    }
    
    // Not a real HTTP method -- must only be used through POST
    protected void postExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Common_skin.setNoCacheJson(response);
        
        ExportFile.purge(); // Purge all exports older than 10 min.
        
        ApiResponse apiResp = new ApiResponse();
        
        String command = reqUtil.getParameterString(request, "extension", reqUtil.getParameterString(request, "command", command_undefined));
        String content_type = reqUtil.getParameterString(request, "content_type", null);

        User ua = ApiAccess.getUser(request);
        
        apiResp.auth_token = ua.auth_token;
        if(ua.last_error != null){
            apiResp.error = ua.last_error;
        }

        if (apiResp.error == null) {
            Command ecmd = Command.fromString(command);
            switch (ecmd) {
               
                case excel:
                case xml:
                case csv:
                case blob:
                    if (ua.access.export_relay) {
                        apiResp.alternate_content_type = "text/plain";
                        ExportFile data = new ExportFile(ua.id, ua.club_id, ua.member_id,
                                reqUtil.getParameterString(request, "filename", "untitled"),
                                //(ecmd.equals(Command.csv))?"text/csv":"application/vnd.ms-excel",
                                content_type == null?"application/vnd.ms-excel":content_type,
                                reqUtil.getParameterString(request, "excel", reqUtil.getParameterString(request, "content", null)));
                        if (data.last_error == null) {
                            if (data.file_data == null) {
                                apiResp.error = "Cannot post NULL "+command+" data for EXPORT";
                                data.delete();
                            } else {
                                apiResp.export = data.file_id;
                            }
                        } else {
                            apiResp.error = data.last_error;
                        }
                        
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;

                case undefined:
                    apiResp.error = error_no_command;
                    break;

                case invalid:
                    apiResp.error = "Invalid command: EXPORT \"" + command + "\"";
                    break;

                default:
                    apiResp.error = "Command \"" + command + "\" cannot be used in EXPORT.";
                    break;

            }
        } else {
            // Not a valid user/session
            apiResp.error = error_expired_session;
        }
        checkError(apiResp, "POST EXPORT " + command);
        sendResponse(apiResp, response);
 
    }
    
    
    // Not a real HTTP method -- must only be used through GET
    protected void getExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        ExportFile.purge(); // Purge all exports older than 10 min.
        
        ApiResponse apiResp = new ApiResponse();
        
        apiResp.alternate_content_type = "text/plain";
        
        String file_id = reqUtil.getQueryString(request, "filename", reqUtil.getQueryString(request, "file_id", null));
        
        if(file_id == null){
            apiResp.error = "No file id provided.";
        }
        
        // No access required for single file download.  if they have the file ID that's enough
        //User ua = ApiAccess.getUser(request);
        
        //apiResp.auth_token = ua.auth_token;
        //if(ua.last_error != null){
            // We'll allow use with simply a valid file ID.  May need to change this behavior.
            //apiResp.error = ua.last_error;
        //}

        if (apiResp.error == null) {
            if (file_id == null) {
                apiResp.error = "No filename or file ID specified";
            } else {
                ExportFile data = new ExportFile(file_id);
                if(data.last_error != null){
                    apiResp.error = data.last_error;
                } else {
                    apiResp.alternate_content_type = data.media_type;
                    response.setHeader("content-disposition", "attachment; filename=" + data.file_name);
                    apiResp.byte_export = data.byte_data;
                    apiResp.export = data.file_data;
                    data.delete(); // We're done with it.  Delete it.
                }
            }
        } else {
            // Not a valid user/session
            apiResp.error = error_expired_session;
        }

        checkError(apiResp, "GET EXPORT");
        sendResponse(apiResp, response);
    
    }
    
    
    /** 
     * Handles the HTTP <code>PUT</code> method.  Only place commands for INSERTING/UPDATING records here.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Common_skin.setNoCacheJson(response);
        
        Gson gson = new Gson();
        
        ApiResponse apiResp = new ApiResponse();
                
        String request_body = reqUtil.getRequestBody(request).toString();
        String command = reqUtil.getQueryString(request, ApiConstants.parameter_command, command_undefined);
        boolean skip_update_check = reqUtil.getQueryBoolean(request, ApiConstants.parameter_skip_update_check, false);
        
        apiResp.debug.put("skip_update_check", skip_update_check);
        
        User ua = ApiAccess.getUser(request);
        
        apiResp.auth_token = ua.auth_token;
        if(ua.last_error != null){
            apiResp.error = ua.last_error;
        }

        if (apiResp.error == null) {
            Command ecmd = Command.fromString(command);
            switch (ecmd) {
                // foretees "users" (NOT club members/proshop)
                case user:
                    if (ua.access.manage_users) {
                        User record = new User();
                        try {
                            record = gson.fromJson(request_body, User.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        if(record.id != null && record.disabled != null && record.disabled && record.id.equals(ua.id)){
                            apiResp.error = "Sorry, you can not disable your own record.";
                        } else if(record.id != null && record.user_access_id != null && record.id.equals(ua.id) && !record.user_access_id.equals(ua.user_access_id)){
                            apiResp.error = "Sorry, you can not modify your own access level.";
                        } else {
                            User test_record = record;
                            if(record.id != null){
                                test_record = new User(record.id);
                                if(test_record.last_error != null){
                                    apiResp.error = test_record.last_error;
                                    break;
                                }
                            } else {
                                test_record.access = new UserAccess(test_record.user_access_id);
                                if(test_record.access.last_error != null){
                                    apiResp.error = test_record.access.last_error;
                                    break;
                                }
                            }
                            if(test_record.access.hierarchy_level > ua.access.hierarchy_level){
                                // Don't have access to edit this user
                                apiResp.error = "Sorry, you do not have sufficient access to edit or add user types of: "+test_record.access.name+".";
                                break;
                            }
                            record.save();
                            if (record.last_error != null) {
                                String new_password = record.password;
                                apiResp.error = record.last_error;
                                String authToken = reqUtil.getQueryString(request, ApiConstants.parameter_auth_token, null);
                                String sessionAuthToken = ApiAccess.getAuthenticationToken(request);
                                if(authToken == null){
                                    authToken = sessionAuthToken;
                                }
                                if (new_password != null && !new_password.isEmpty() && record.id.equals(ua.id)) {
                                    // We're changing the password of the current user.
                                    if (sessionAuthToken != null && authToken != null && sessionAuthToken.equals(authToken)) {
                                        // If this the auth of the current session, updtae it
                                        ApiAccess.setAuthenticationToken(request, null, ua.email, new_password);
                                    }
                                    authToken = ApiAccess.getAuthenticationToken(null, ua.email, new_password);
                                    apiResp.auth_token = authToken; // Return the new authentication token
                                }
                            }
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case foreteesSetting:
                    if (ua.access.manage_settings && !ua.access.bind_to_club) {
                        ForeTeesSetting record = new ForeTeesSetting();
                        try {
                            record = gson.fromJson(request_body, ForeTeesSetting.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        record.save();
                        if(record.last_error != null){
                            apiResp.error = record.last_error;
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case foreteesAnnouncement:
                    if (ua.access.manage_announcements && !ua.access.bind_to_club) {
                        ForeTeesAnnouncement record = new ForeTeesAnnouncement();
                        try {
                            record = gson.fromJson(request_body, ForeTeesAnnouncement.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        record.save();
                        if(record.last_error != null){
                            apiResp.error = record.last_error;
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case taxRate:
                    if (ua.access.manage_invoices) {
                        TaxRate record = new TaxRate();
                        try {
                            record = gson.fromJson(request_body, TaxRate.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        if(record.id != null && record.id.equals((long)1)){
                            apiResp.error = "Sorry, you can not modify the Nontaxable Tax Rate.";
                        } else {
                            record.save();
                            if(record.last_error != null){
                                apiResp.error = record.last_error;
                            }
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case taxGroup:
                    if (ua.access.manage_invoices) {
                        TaxGroup record = new TaxGroup();
                        try {
                            record = gson.fromJson(request_body, TaxGroup.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        if(record.id != null && record.id.equals((long)1)){
                            apiResp.error = "Sorry, you can not modify the Nontaxable Tax Group.";
                        } else {
                            record.save();
                            if(record.last_error != null){
                                apiResp.error = record.last_error;
                            }
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case taxGroupDetail:
                    if (ua.access.manage_invoices) {
                        TaxGroupDetail record = new TaxGroupDetail();
                        try {
                            record = gson.fromJson(request_body, TaxGroupDetail.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        if(record.tax_group_id != null && record.tax_group_id.equals((long)1)){
                            apiResp.error = "Sorry, you can not modify the Nontaxable Tax Group.";
                        } else {
                            record.save();
                            if(record.last_error != null){
                                apiResp.error = record.last_error;
                            }
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case invoiceItemType:
                    if (ua.access.manage_invoices) {
                        InvoiceItemType record = new InvoiceItemType();
                        try {
                            record = gson.fromJson(request_body, InvoiceItemType.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        record.save();
                        if(record.last_error != null){
                            apiResp.error = record.last_error;
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case invoiceItem:
                    if (ua.access.manage_invoices) {
                        InvoiceItem record = new InvoiceItem();
                        try {
                            record = gson.fromJson(request_body, InvoiceItem.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        record.save();
                        if(record.last_error != null){
                            apiResp.error = record.last_error;
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case invoice:
                case invoiceAndDetails:
                    if (ua.access.manage_invoices) {
                        Invoice record = new Invoice();
                        try {
                            record = gson.fromJson(request_body, Invoice.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        record.save(ecmd.equals(Command.invoiceAndDetails));
                        if(record.last_error != null){
                            apiResp.error = record.last_error;
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case invoiceSetPaid:
                case invoiceSendToClub:
                case invoiceSetVoided:
                    if (ua.access.manage_invoices) {
                        Invoice record = new Invoice();
                        List<Invoice> records = new ArrayList<Invoice>();
                        try {
                            record = gson.fromJson(request_body, Invoice.class);
                        } catch (Exception e){
                            // No single record sent.  See if it was a list of records
                            try {
                                records = gson.fromJson(request_body, new TypeToken<List<Invoice>>(){}.getType());
                                if(records == null || records.isEmpty()){
                                    apiResp.error = "No data sent for " + command;
                                    break;
                                }
                                
                            } catch (Exception e2){
                                // Not a single record or a list of records.  Probably a malformed request.
                                // Return the error from the single record check
                                apiResp.error = jsonRequestError(command, e.toString());
                                break;
                            }
                        }
                        if(records.isEmpty()){
                            records.add(record);
                        }
        
                        if(ecmd.equals(Command.invoiceSetPaid)){
                            apiResp.error = Invoice.setPaid(records);
                        } else if (ecmd.equals(Command.invoiceSendToClub)){
                            apiResp.error = Invoice.sendToClub(records);
                            if(apiResp.error == null){
                                apiResp.error = Invoice.sendEmailToClub(records);
                            }
                        } else if (ecmd.equals(Command.invoiceSetVoided)){
                            apiResp.error = Invoice.setVoided(records);
                        }
                        
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                    
                case invoiceDetailSetCommissionPaid:
                    if (ua.access.manage_invoices) {
                        InvoiceDetail record = new InvoiceDetail();
                        List<InvoiceDetail> records = new ArrayList<InvoiceDetail>();
                        try {
                            record = gson.fromJson(request_body, InvoiceDetail.class);
                        } catch (Exception e){
                            // No single record sent.  See if it was a list of records
                            try {
                                records = gson.fromJson(request_body, new TypeToken<List<InvoiceDetail>>(){}.getType());
                                if(records == null || records.isEmpty()){
                                    apiResp.error = "No data sent for " + command;
                                    break;
                                }
                                
                            } catch (Exception e2){
                                // Not a single record or a list of records.  Probably a malformed request.
                                // Return the error from the single record check
                                apiResp.error = jsonRequestError(command, e.toString());
                                break;
                            }
                        }
                        if(records.isEmpty()){
                            records.add(record);
                        }
        
                        apiResp.error = InvoiceDetail.setCommissionPaid(records);
                        
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case clubInvoicing:
                    if (ua.access.manage_invoices) {
                        ClubInvoicing record = new ClubInvoicing();
                        try {
                            record = gson.fromJson(request_body, ClubInvoicing.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        record.save();
                        if(record.last_error != null){
                            apiResp.error = record.last_error;
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case clubInvoicingRule:
                    if (ua.access.manage_invoices) {
                        ClubInvoicingRule record = new ClubInvoicingRule();
                        try {
                            record = gson.fromJson(request_body, ClubInvoicingRule.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        record.save(skip_update_check);
                        if(record.last_error != null){
                            apiResp.error = record.last_error;
                            apiResp.continue_prompt = record.continue_prompt; // need a more elegant way of doing this.  This will change -- try not to implement this method in too many location
                            apiResp.continue_parameter = ApiConstants.parameter_skip_update_check;
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case clubInvoicingRuleDetail:
                    if (ua.access.manage_invoices) {
                        ClubInvoicingRuleDetail record = new ClubInvoicingRuleDetail();
                        try {
                            record = gson.fromJson(request_body, ClubInvoicingRuleDetail.class);
                        } catch (Exception e){
                            apiResp.error = jsonRequestError(command, e.toString());
                            break;
                        }
                        record.save();
                        if(record.last_error != null){
                            apiResp.error = record.last_error;
                        }
                        apiResp.results.put(command, record);
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;

                case undefined:
                    apiResp.error = error_no_command;
                    break;

                case invalid:
                    apiResp.error = "Invalid command: PUT \"" + command + "\"";
                    break;

                default:
                    apiResp.error = "Command \"" + command + "\" cannot be used in PUT.";
                    break;

            }
        } else {
            // Not a valid user/session
            apiResp.error = error_expired_session;
        }
        
        checkError(apiResp, "PUT " + command);
        sendResponse(apiResp, response);    
    }
    
    /** 
     * Handles the HTTP <code>DELETE</code> method.  Only place commands for DELETING records here.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Common_skin.setNoCacheJson(response);
        
        Gson gson = new Gson();
        
        ApiResponse apiResp = new ApiResponse();

        Long id = reqUtil.getQueryLong(request, "id", null);
        String command = reqUtil.getQueryString(request, ApiConstants.parameter_command, command_undefined);
        
        User ua = ApiAccess.getUser(request);
        
        apiResp.auth_token = ua.auth_token;
        if(ua.last_error != null){
            apiResp.error = ua.last_error;
        }
        
        if ( apiResp.error == null) {
            switch (Command.fromString(command)) {
                // foretees "users" (NOT club members/proshop)
                case user:
                    if (ua.access.manage_users) {
                        User record = new User(id);
                        if(id.equals(ua.id)){
                            apiResp.error = "Sorry, you can not delete your own record.";
                        } else if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else if(record.access.hierarchy_level > ua.access.hierarchy_level){
                            // Don't have access to delete this user
                            apiResp.error = "Sorry, you do not have sufficient access to delete this user.";
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                   
                case foreteesSetting:
                    if (ua.access.manage_settings && !ua.access.bind_to_club) {
                        ForeTeesSetting record = new ForeTeesSetting(id);
                        if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case foreteesAnnouncement:
                    if (ua.access.manage_announcements && !ua.access.bind_to_club) {
                        ForeTeesAnnouncement record = new ForeTeesAnnouncement(id);
                        if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case taxRate:
                    if (ua.access.manage_invoices) {
                        TaxRate record = new TaxRate(id);
                        if(id.equals((long)1)){
                            apiResp.error = "Sorry, you can not delete the Nontaxable Tax Rate.";
                        } else if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case taxGroup:
                    if (ua.access.manage_invoices) {
                        TaxGroup record = new TaxGroup(id);
                        if(id.equals((long)1)){
                            apiResp.error = "Sorry, you can not delete the Nontaxable Tax Group.";
                        } else if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case taxGroupDetail:
                    if (ua.access.manage_invoices) {
                        
                        TaxGroupDetail record = new TaxGroupDetail(id);
                        if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else if(record.tax_group_id.equals((long)1)){
                            apiResp.error = "Sorry, you can not modify entries in the Nontaxable Tax Group.";
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case invoiceItemType:
                    if (ua.access.manage_invoices) {
                        InvoiceItemType record = new InvoiceItemType(id);
                        if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case invoiceItem:
                    if (ua.access.manage_invoices) {
                        InvoiceItem record = new InvoiceItem(id);
                        if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case invoice:
                    if (ua.access.manage_invoices) {
                        Invoice record = new Invoice(id);
                        if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case clubInvoicing:
                    if (ua.access.manage_invoices) {
                        ClubInvoicing record = new ClubInvoicing(id);
                        if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case clubInvoicingRule:
                    if (ua.access.manage_invoices) {
                        ClubInvoicingRule record = new ClubInvoicingRule(id);
                        if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;
                    
                case clubInvoicingRuleDetail:
                    if (ua.access.manage_invoices) {
                        ClubInvoicingRuleDetail record = new ClubInvoicingRuleDetail(id);
                        if(record.last_error != null){
                            // Couldn't load record before delete.
                            apiResp.error = record.last_error;
                        } else {
                            // Try to delete record
                            record.delete();
                            if(record.last_error != null){
                                // Couldn't load record.
                                apiResp.error = record.last_error;
                            }
                        }
                    } else {
                        apiResp.error = error_access_denied;
                    }
                    break;

                case undefined:
                    apiResp.error = error_no_command;
                    break;

                case invalid:
                    apiResp.error = "Invalid command: DELETE \"" + command + "\"";
                    break;

                default:
                    apiResp.error = "Command \"" + command + "\" cannot be used in DELETE.";
                    break;

            }
        } else if (ua != null) {
            // Not a valid user/session
            apiResp.error = error_expired_session;
        } else {
            apiResp.error = error_no_id;
        }

        checkError(apiResp, "DELETE " + command);
        sendResponse(apiResp, response);   
        
    }
    
    
    /*
     * Private methods for API.class
     * 
     * NOTE:  If you're tempted to make ANY of these public for use elsewhere, 
     *        MOVE THEM TO A RELAVENT CLASS IN: com/foretees/api or com/foretees/common!!!
     * 
     */
    
    private static String jsonRequestError(String command, String error){
        return error_json_request_syntax + command + " : " + error;
    }
    
    private static void addResult(ApiGetCommand cmd, ApiResponse apiResp, Object data){
        apiResp.results.put(cmd.request_id != null?cmd.request_id:cmd.command, data);
    }
    
    private static boolean checkError(ApiResponse apiResp, String command){
        
        if (apiResp.error != null) {
            if (apiResp.error.equals(error_access_denied)
                    || apiResp.error.equals(error_unexpected_fault)
                    || apiResp.error.equals(error_must_be_used_alone)) {
                apiResp.error  += command;
            }
            apiResp.success = false;
            return true;
        } else {
            apiResp.success = true;
            return false;
        }
        
    }
    
    private static void sendResponse(ApiResponse apiResp, HttpServletResponse response)
        throws ServletException, IOException {
        
        if(apiResp.alternate_content_type == null){
            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            out.write(gson.toJson(apiResp));
            out.close();
        } else {
            Common_skin.setNoCacheCustom(response, apiResp.alternate_content_type);
            if(apiResp.error != null){
                response.setStatus(400);
                PrintWriter out = response.getWriter();
                out.write(apiResp.error);
                out.close();
            } else {
                if(apiResp.byte_export.length > 0){
                    //out.close();
                    OutputStream dout = response.getOutputStream();
                    dout.write(apiResp.byte_export);
                    dout.close();
                } else {
                    PrintWriter out = response.getWriter();
                    out.write(apiResp.export);
                    out.close();
                }
            }
        }
        
    }
    

}
