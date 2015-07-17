/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.reportItems.BillableCount;
import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
import com.foretees.common.timeUtil;
import org.apache.commons.lang.*;
import java.sql.*;          // mysql
//import java.util.UUID;
//import javax.naming.*;
//import javax.servlet.http.*;
//import java.io.*;
import java.util.*;
import java.lang.*;
import org.joda.time.DateTimeZone;
/**
 *
 * @author John Kielkopf
 */
public class InvoicingRuleTypeData {
    
    
    public Long club_id;
    
    public InvoicingRuleType invoicing_rule_type;
    
    public List<MemberType> member_types;
    public List<MembershipType> membership_types;
    
    public List<MemberType> use_member_types;
    public List<MembershipType> use_membership_types;

    public BillableCount billable_count;
    public List<BillableCount> billable_count_details;
    
    public InvoiceItem invoice_item;
    public Float quantity;
    
    public Long billing_start;
    public Long billing_end;

    public Long updated;
    public String last_error;
    
    public InvoicingRuleTypeData(){}; // Empty parm
    
    public InvoicingRuleTypeData(
            InvoicingRuleType invoicing_rule_type, 
            List<MemberType> member_types,
            List<MembershipType> membership_types, 
            List<MemberType> use_member_types, 
            List<MembershipType> use_membership_types){
        this.invoicing_rule_type = invoicing_rule_type;
        this.member_types = member_types;
        this.membership_types = membership_types;
        this.use_member_types = use_member_types;
        this.use_membership_types = use_membership_types;
        
    };
    
    public final boolean loadMemberShipTypes(Connection con_club){

        membership_types = MembershipType.getListByActivityId(0, con_club);
        use_membership_types = new ArrayList<MembershipType>();
        
        if(membership_types == null){
            last_error = String.format(ApiConstants.error_unknown_condition, classNameLower()+".loadMemberShipTypes");
            return false;
        } else {
            for(MembershipType r:membership_types){
                // set default selected
                if(!r.name.toLowerCase().startsWith("social")){
                    use_membership_types.add(r);
                }
            }
        }
        
        return true;
        
    }
    
    public final boolean loadMemberTypes(Connection con_club){
        
        member_types = MemberType.getList(con_club);
        use_member_types = new ArrayList<MemberType>();

        if(member_types == null){
            last_error = String.format(ApiConstants.error_unknown_condition, classNameLower()+".loadMemberTypes");
            return false;
        } else {
            String compare;
            for(MemberType r:member_types){
                // set default selected
                compare = r.name.toLowerCase();
                if(!compare.startsWith("junior") && !compare.startsWith("dependent")){
                    use_member_types.add(r);
                }
            }
        }
        
        return true;
        
    }
    
    public final void loadBillableCounts(){
        if(club_id == null){
            last_error = "No club ID specified.";
            return;
        }
        Connection con_club = ApiCommon.getConnection(club_id);
        loadBillableCounts(con_club);
        Connect.close(con_club);
    };
    
    public final void loadBillableCounts(String club_code){
        Connection con_club = ApiCommon.getConnection(club_code);
        loadBillableCounts(con_club);
        Connect.close(con_club);
    };
    
    public final void loadBillableCounts(long club_id){
        Connection con_club = ApiCommon.getConnection(club_id);
        loadBillableCounts(con_club);
        Connect.close(con_club);
    };
    
    public final void loadBillableCounts(Connection con_club){
        
        billable_count_details = new ArrayList<BillableCount>();
        billable_count = new BillableCount("Total",0,0,0,0);
        
        ClubOptions clubOpt = new ClubOptions(con_club);
        
        if(invoicing_rule_type.count_adults){
            // Adult count
            // Get results based on mship type and m type
            String[] mship_sql_params = new String[use_membership_types.size()];
            Arrays.fill(mship_sql_params, "?");
            
            String[] mtype_sql_params = new String[use_member_types.size()];
            Arrays.fill(mtype_sql_params, "?");
            
            String count_sql = ""
                    + "SELECT "
                    + "    ms.mship as name,"
                    + "    COUNT(IF(m.inact = 1,1,NULL)) as inactive, "
                    + "    COUNT(IF(m.billable = 0 AND m.inact = 0,1,NULL)) as non_billable, "
                    + "    COUNT(IF(m.billable = 1 AND m.inact = 0,1,NULL)) as billable, "
                    + "    COUNT(m.m_ship) as total "
                    + "  FROM mship5 ms "
                    + "    LEFT OUTER JOIN member2b m "
                    + "      ON m.m_ship = ms.mship "
                    + "  WHERE "
                    + "        ms.activity_id = 0 "
                    + "    AND ms.mship in (" + StringUtils.join(mship_sql_params,",") + ") "
                    + "    AND m.m_type in (" + StringUtils.join(mtype_sql_params,",") + ") "
                    + "  GROUP BY ms.mship ";

            PreparedStatement pstmt = null;
            ResultSet rs = null;


            // Get member types
            try {
                pstmt = con_club.prepareStatement(count_sql);
                
                int i = 1;
                
                // Set IN list for m_ships
                for(MembershipType r : use_membership_types){
                    pstmt.setString(i++,r.name);
                }
                // Set IN list for m_types
                for(MemberType r : use_member_types){
                    pstmt.setString(i++,r.name);
                }

                rs = pstmt.executeQuery();

                while (rs.next()) {
                    BillableCount r = new BillableCount(
                            "Golf Adults: "+rs.getString("name"),
                            rs.getInt("inactive")+(clubOpt.golf?0:rs.getInt("billable")),
                            rs.getInt("non_billable"),
                            (clubOpt.golf?rs.getInt("billable"):0),
                            rs.getInt("total"));

                    billable_count_details.add(r);
                    billable_count.inactive += r.inactive;
                    billable_count.non_billable += r.non_billable;
                    billable_count.billable += r.billable;
                    billable_count.total += r.total;
                } 
            } catch(Exception e) {
                Connect.logError(className()+".loadBillableCounts: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
            } finally {
                Connect.close(rs, pstmt);
            }
            
        } else if (invoicing_rule_type.count_members){
            // Member count
            // Get results based on mship type
            String[] mship_sql_params = new String[use_membership_types.size()];
            Arrays.fill(mship_sql_params, "?");

            String count_sql = ""
                    + "SELECT "
                    + "    ms.mship as name,"
                    + "    COUNT(IF(m.inact = 1,1,NULL)) as inactive, "
                    + "    COUNT(IF(m.billable = 0 AND m.inact = 0,1,NULL)) as non_billable, "
                    + "    COUNT(IF(m.billable = 1 AND m.inact = 0,1,NULL)) as billable, "
                    + "    COUNT(m.m_ship) as total "
                    + "  FROM mship5 ms "
                    + "    LEFT OUTER JOIN member2b m "
                    + "      ON m.m_ship = ms.mship "
                    + "  WHERE "
                    + "        ms.activity_id = 0 "
                    + "    AND ms.mship in (" + StringUtils.join(mship_sql_params,",") + ") "
                    + "  GROUP BY ms.mship ";

            PreparedStatement pstmt = null;
            ResultSet rs = null;

            // Get member types
            try {
                pstmt = con_club.prepareStatement(count_sql);
                
                int i = 1;
                
                // Set IN list for m_ships
                for(MembershipType r : use_membership_types){
                    pstmt.setString(i++,r.name);
                }

                rs = pstmt.executeQuery();

                while (rs.next()) {
                    BillableCount r = new BillableCount(
                            "Golf Members: "+rs.getString("name"),
                            rs.getInt("inactive")+(clubOpt.golf?0:rs.getInt("billable")),
                            rs.getInt("non_billable"),
                            (clubOpt.golf?rs.getInt("billable"):0),
                            rs.getInt("total"));
                    billable_count_details.add(r);
                    billable_count.inactive += r.inactive;
                    billable_count.non_billable += r.non_billable;
                    billable_count.billable += r.billable;
                    billable_count.total += r.total;
                } 
            } catch(Exception e) {
                Connect.logError(className()+".loadBillableCounts: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
            } finally {
                Connect.close(rs, pstmt);
            }
            
        } else if (invoicing_rule_type.count_courts){
            // FlxRez
            // Check how many courts/activities there are
            String count_sql = ""
                    + "SELECT "
                    + "    a.activity_name as name,"
                    + "    SUM(IF(a2.activity_id IS NULL AND FIND_IN_SET(0,CONCAT_WS(',',a5.enabled,a4.enabled,a3.enabled,a2.enabled,a.enabled)) < 1,1,0)) as non_billable, "
                    + "    SUM(IF(a2.activity_id IS NOT NULL AND FIND_IN_SET(0,CONCAT_WS(',',a5.enabled,a4.enabled,a3.enabled,a2.enabled,a.enabled)) < 1,1,0)) as billable, "
                    + "    SUM(IF(FIND_IN_SET(0,CONCAT_WS(',',a5.enabled,a4.enabled,a3.enabled,a2.enabled,a.enabled)) > 0,1,0)) as inactive, "
                    + "    COUNT(a.activity_id) as total "
                    + "  FROM activities a "
                    + "    LEFT OUTER JOIN activities a2 "
                    + "      ON a2.parent_id = a.activity_id "
                    + "    LEFT OUTER JOIN activities a3 "
                    + "      ON a3.parent_id = a2.activity_id "
                    + "    LEFT OUTER JOIN activities a4 "
                    + "      ON a4.parent_id = a3.activity_id "
                    + "    LEFT OUTER JOIN activities a5 "
                    + "      ON a5.parent_id = a4.activity_id "
                    + "  WHERE a.parent_id = 0 OR a.parent_id IS NULL "
                    + "  GROUP BY a.activity_name ";

            PreparedStatement pstmt = null;
            ResultSet rs = null;

            // Get member types
            try {
                pstmt = con_club.prepareStatement(count_sql);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    BillableCount r = new BillableCount(
                            "FlxRez: "+rs.getString("name"),
                            rs.getInt("inactive")+(clubOpt.flxrez?0:rs.getInt("billable")),
                            rs.getInt("non_billable"),
                            (clubOpt.flxrez?rs.getInt("billable"):0),
                            rs.getInt("total"));
                    billable_count_details.add(r);
                    billable_count.inactive += r.inactive;
                    billable_count.non_billable += r.non_billable;
                    billable_count.billable += r.billable;
                    billable_count.total += r.total;
                } 
            } catch(Exception e) {
                Connect.logError(className()+".loadBillableCounts: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
            } finally {
                Connect.close(rs, pstmt);
            }
            
        } else if (clubOpt.flxrez && invoicing_rule_type.count_courts){
            // Dining
            BillableCount r = new BillableCount(
                    "Dining",
                    clubOpt.dining?0:1, // dining not active?
                    0,
                    clubOpt.dining?1:0, // dining active?
                    1);
            billable_count_details.add(r);
            billable_count.inactive += r.inactive;
            billable_count.non_billable += r.non_billable;
            billable_count.billable += r.billable;
            billable_count.total += r.total;

        } else {
            // Flat rate
            BillableCount r = new BillableCount(
                    "Flat-rate",
                    0, // non-active
                    0, // non-billable
                    1, // billable
                    1); // total
            billable_count_details.add(r);
            billable_count.inactive += r.inactive;
            billable_count.non_billable += r.non_billable;
            billable_count.billable += r.billable;
            billable_count.total += r.total;
        }
        
        if(invoice_item != null && invoice_item.last_error == null){
            quantity = (float)billable_count.billable;
            if(invoice_item.maximum_qty != null && invoice_item.maximum_qty > 0 && quantity > invoice_item.maximum_qty){
                quantity = invoice_item.maximum_qty;
            } else if (invoice_item.maximum_qty == null){
                invoice_item.maximum_qty = (float)0;
            }
            if(invoice_item.minimum_qty != null && invoice_item.minimum_qty > 0 & quantity < invoice_item.minimum_qty){
                quantity = invoice_item.minimum_qty;
            } else if (invoice_item.minimum_qty == null){
                invoice_item.minimum_qty = (float)0;
            }
            if(invoice_item.count_before_min != null && !invoice_item.count_before_min){
                if(billable_count.billable >= invoice_item.minimum_qty){
                    billable_count.skipped_before_min = invoice_item.minimum_qty;
                    billable_count.counted_after_min = billable_count.billable - invoice_item.minimum_qty;
                } else {
                    billable_count.skipped_before_min = (float)billable_count.billable;
                    billable_count.counted_after_min = (float)0;
                }
                if(quantity > billable_count.counted_after_min){
                    quantity = billable_count.counted_after_min;
                } else if(quantity < billable_count.counted_after_min && (billable_count.counted_after_min <= invoice_item.maximum_qty || invoice_item.maximum_qty == 0)){
                    quantity = billable_count.counted_after_min;
                }
            } else {
                billable_count.skipped_before_min = (float)0;
                billable_count.counted_after_min = (float)billable_count.billable;
            }
            updateDescription();
            
        }
        
        updated = timeUtil.getCurrentUnixTime();
        
    }
    
    public final void updateDescription(){
        if(invoice_item.description != null){
            //DecimalFormat df = new DecimalFormat("0.00##");
            DateTimeZone ftz = timeUtil.getServerTimeZone();
            String description = invoice_item.description
                    .replace("%MIN%", ApiCommon.simple(invoice_item.minimum_qty))
                    .replace("%MAX%", ApiCommon.simple(invoice_item.maximum_qty))
                    .replace("%QTY%", ApiCommon.simple(quantity))
                    .replace("%RATE%", "$"+String.format(Locale.US,"%,.2f",invoice_item.rate))
                    .replace("%AMOUNT%", "$"+String.format(Locale.US,"%,.2f",invoice_item.rate*quantity))
                    .replace("%NONBILLABLE%", billable_count.non_billable.toString())
                    .replace("%SKIPPEDBEFOREMIN%", billable_count.skipped_before_min.toString())
                    .replace("%COUNTEDAFTERMIN%", billable_count.counted_after_min.toString())
                    .replace("%INACTIVE%", billable_count.inactive.toString())
                    .replace("%BILLABLE%", billable_count.billable.toString())
                    .replace("%TOTAL%", billable_count.total.toString())
                    .replace("%START_YEAR%", timeUtil.formatTzDate(ftz, billing_start, "yyyy"))
                    .replace("%START_MONTH%", timeUtil.formatTzDate(ftz, billing_start, "M"))
                    .replace("%START_MONTH_NAME%", timeUtil.formatTzDate(ftz, billing_start, "MMMM"))
                    .replace("%START_DAY%", timeUtil.formatTzDate(ftz, billing_start, "d"))
                    .replace("%START_DAY_ORDINAL%", timeUtil.formatTzDate(ftz, billing_start, "%od%"))
                    .replace("%START_DAY_NAME%", timeUtil.formatTzDate(ftz, billing_start, "EEEE"))
                    .replace("%END_YEAR%", timeUtil.formatTzDate(ftz, billing_end, "yyyy"))
                    .replace("%END_MONTH%", timeUtil.formatTzDate(ftz, billing_end, "M"))
                    .replace("%END_MONTH_NAME%", timeUtil.formatTzDate(ftz, billing_end, "MMMM"))
                    .replace("%END_DAY%", timeUtil.formatTzDate(ftz, billing_end, "d"))
                    .replace("%END_DAY_ORDINAL%", timeUtil.formatTzDate(ftz, billing_end, "%od%"))
                    .replace("%END_DAY_NAME%", timeUtil.formatTzDate(ftz, billing_end, "EEEE"))
                    ;
            invoice_item.description = description;

        }
    }
    
    public final List<String> difference(String club_name, InvoicingRuleTypeData old){
        Connection con_club = ApiCommon.getConnection(club_name);
        List<String> result = difference(con_club, old);
        Connect.close(con_club);
        return result;
    }
    
    public final List<String> difference(long club_id, InvoicingRuleTypeData old){
        Connection con_club = ApiCommon.getConnection(club_id);
        List<String> result = difference(con_club, old);
        Connect.close(con_club);
        return result;
    }
    
    public final List<String> difference(Connection con_club, InvoicingRuleTypeData old){
        
        List<String> result = new ArrayList<String>();
        
        if(old == null){
            return result;
        }
        
        InvoicingRuleType cirt = this.invoicing_rule_type;
        InvoicingRuleType oirt = old.invoicing_rule_type;
        
        if(!cirt.compare(oirt)){
            // Rule type has changed
            result.add("Invoicing rule type has changed.");
            // Since it's a different rule type now, don't bother with any other checks.
            return result;
        }
        
        // Check member/membership types, if Member or adult type check
        if(cirt.count_members || cirt.count_adults){
            // Golf mode:
            Map<String, Integer> counts;
            if (cirt.count_adults) {
                // Check mtypes:
                counts = new HashMap<String, Integer>();
                // Create a lookup map for the curent types
                for (MemberType r : this.member_types) {
                    counts.put(r.name.toLowerCase(), r.count);
                }
                for (MemberType r : old.member_types) {
                    if (counts.get(r.name.toLowerCase()) == null) {
                        if (r.count > 0) {
                            // An mtype, with potentially billable members, used to exist, but has been removed
                            result.add("Member types have been removed.");
                            break;
                        }
                    } else {
                        counts.remove(r.name.toLowerCase());
                    }
                }
                for (Integer c : counts.values()) {
                    // Any types left are new
                    if (c > 0) {
                        // A mship, with potentially billable members, exists that didn't before
                        result.add("Member types have been added.");
                        break;
                    }
                }
            }

            // Check mships:
            counts = new HashMap<String, Integer>();
            // Create a lookup map for the curent types
            for (MembershipType r : this.membership_types) {
                counts.put(r.name.toLowerCase(), r.count);
            }
            for (MembershipType r : old.membership_types) {
                if (counts.get(r.name.toLowerCase()) == null) {
                    if (r.count > 0) {
                        // An mship, with potentially billable members, used to exist, but has been removed
                        result.add("Membership types have been removed.");
                        break;
                    }
                } else {
                    counts.remove(r.name.toLowerCase());
                }
            }
            for (Integer c : counts.values()) {
                // Any types left are new
                if (c > 0) {
                    // An mship, with potentially billable members, exists that didn't before
                    result.add("Membership types have been added.");
                    break;
                }
            }
        }
        
        // Check billing counts
        // Back-up our original old count
        BillableCount old_bc = new BillableCount(old.billable_count);
        
        // Run new counts using our old settings
        old.loadBillableCounts(con_club);
        BillableCount new_bc = old.billable_count;
        
        // Check if counts have changed enough to trigger a warning
        if(old_bc.billable == 0 || new_bc.billable == 0){
            if(old_bc.billable != new_bc.billable){
                // Either there were billable and there arn't now, or there are now but wasn't before.
                result.add("Billable count has changed more than expected.");
            } else {
                // No billable before or now.  Do nothing.
            }
        } else if((old_bc.non_billable == 0 || new_bc.non_billable == 0) && old_bc.non_billable != new_bc.non_billable){
                // Either there were non-billable and there arn't now, or there are now but wasn't before.
                result.add("Non-billable count has changed more than expected.");
        } else {
            Integer onb = old_bc.non_billable;
            Integer nnb = new_bc.non_billable;
            if(onb == 0 && nnb == 0){
                // There are no non-billable, old or new.  We need the count to be > 0, else the ratio calculation will fail
                onb ++;
                nnb ++;
            }
            Float old_ratio = ((float)onb/(float)old_bc.billable);
            Float new_ratio = ((float)nnb/(float)new_bc.billable);

            Float ratio_diff = Math.abs(old_ratio - new_ratio);
            float ratio_test = (float).05; // Sensitivity of billable to non-billable ratio check. (this will probably need to change)
            if(ratio_diff > ratio_test){
                // Billable to non-billable ratio change has crossed threshold
                result.add("Billable to non-billable ratio has changed more than expected.");
            }
        }
        
        return result;
    }
    
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
