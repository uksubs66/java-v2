/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
import java.sql.*;          // mysql
//import java.util.UUID;
//import javax.naming.*;
import javax.servlet.http.*;
//import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
/**
 *
 * @author John Kielkopf
 */
public class MemberType {
    
    public Long id; // Not currently used/accurate (only an array index.  There is no MemberTypes table, yet.)
    public String name;
    public Integer count;
    
    public Integer type_status; // If you must check this, do so like: if(member_type.type_status == MemberType.TYPE_PRIMARY).  DO _NOT_ USE: if(member_type.type_status == 1)
    public String type_status_text;
    public String type_status_abrv;
    
    public String gender;
    
    public Boolean disabled = false;
    public Long updated;
    
    public String last_error;
    
    // Part of hack to determine status of a MemberType until a member_type table is created
    // Some of this is just for coloring and/or sort order.
    public static Integer TYPE_PRIMARY = 1;
    public static Integer TYPE_SECONDARY = 2;
    public static Integer TYPE_SPOUSE = 3;
    public static Integer TYPE_HONORARY = 4;
    public static Integer TYPE_SENIOR = 5;
    public static Integer TYPE_STUDENT = 6;
    public static Integer TYPE_YOUNG_ADULT = 7;
    public static Integer TYPE_JUNIOR = 8;
    public static Integer TYPE_DEPENDENT = 9;
    
    private static final String MATCH_NOTHING = "^\b$"; // Regexp that will never match. (Empty string can't have a word boundry.) Used to disable a given type status
    private static final String MATCH_ANY = "^[^$]"; // Regexp that will match any non-empty member type
    private static final String MATCH_ALL = "^"; // Regexp that will match all member types, including empty
    private static final String MATCH_MALE = "(^|[^a-zA-Z])(Male|Boy'{0,1}s{0,1}|Men'{0,1}s{0,1}|Man'{0,1}s{0,1})($|[^a-zA-Z])";
    private static final String MATCH_FEMALE = "(^|[^a-zA-Z])(Female|Ladie'{0,1}s{0,1}|Lady'{0,1}s{0,1}|Girl'{0,1}s{0,1}|Womens{0,1}|Woman'{0,1}s{0,1})($|[^a-zA-Z])";
    private static final String MATCH_ADULT = "(^|[^a-zA-Z])(Adult|Senior|Honorary|Primary|Spouse)($|[^a-zA-Z])";
    
    private final static Map<String, String> type_primary_regex = loadTypePrimaryRegexp();
    private final static Map<String, String> type_secondary_regex = loadTypeSecondaryRegexp();
    private final static Map<String, String> type_spouse_regex = loadTypeSpouseRegexp();
    private final static Map<String, String> type_honorary_regex = loadTypeHonoraryRegexp();
    private final static Map<String, String> type_senior_regex = loadTypeSeniorRegexp();
    private final static Map<String, String> type_student_regex = loadTypeStudentRegexp();
    private final static Map<String, String> type_young_adult_regex = loadTypeYoungAdultRegexp();
    private final static Map<String, String> type_junior_regex = loadTypeJuniorRegexp();
    private final static Map<String, String> type_dependent_regex = loadTypeDependentRegexp();
    
    private final static Map<String, String> type_primary_by_username_regex = loadTypePrimaryByUsernameRegexp();
    private final static Map<String, String> type_spouse_by_username_regex = loadTypeSpouseByUsernameRegexp();
    private final static Map<String, String> type_dependent_by_username_regex = loadTypeDependentByUsernameRegexp();
    
    // Part of hack to determine status of a MemberType, until a member_type table is created that
    // that allows this to be more easily defined.  
    // These REGEX expressions are used to match member types to a given subset (Primary, secondary etc.), based on club and member type text.
    // Note that these expressions must be both MYSQL and JAVA compatible. (Don't use more advanced expressions, like look-arounds.)
    //  
    // Quick REGEXP primer: http://www.regular-expressions.info/quickstart.html
    //
    //  Using a club's DB name and the text of a member type to decide certain aspects of a member is not optimal, 
    //  however, due to its use in a fair amount of legacy code, it's currently the only way.
    //  Before many more clubs/data is added to this hack, we should seriously consider adding a member_type table, migrating member types from
    //  club5, and refering to member types everywhere by its ID in this new table.  This would allow flexability and extensibility in a more maintainable way.
    //
    private static Map<String, String> loadTypePrimaryRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("medinahcc", "Member$"); // Ends with "Member"
        result.put("desertmountain", "^Primary"); // Starts with "Primary"
        result.put("oswegolake", "^Adult Male$"); // Equals "Adult Male"
        result.put("virginiacc", "^Adult"); // Starts with "Adult"
        result.put("pinehurstcountryclub", "^Primary"); // Starts with "Primary"
        result.put("loscoyotes", "^Primary"); // Starts with "Primary"
        result.put("mpccpb", MATCH_NOTHING); // mpccpb matches using username (just in case we ever use default)
        result.put("denvercc", "(^|[^a-zA-Z])(Primary|Member)($|[^a-zA-Z])"); // Contains words "Primary" or "Member" (Using [^a-zA-Z] instead of \b to detect word boundries allows hyphens and underscores to be word boundries)
        //result.put(DEFAULT_MAP_KEY, "(^|[^a-zA-Z])(Primary|Member)($|[^a-zA-Z])"); // Contains words "Primary" or "Member"
        return result;
    }
    
    private static Map<String, String> loadTypePrimaryByUsernameRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("mpccpb", "[^\\.][^1-9][^0-9]{0,1}$"); // Username doesn't end with ".1" through ".99" (this regex does require the username to be at least 2 characters.  Shorter names will not match as Primary)
        return result;
    }
    
    private static Map<String, String> loadTypeSecondaryRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("pinehurstcountryclub", "^Secondary"); // Starts with "Secondary"
        result.put("loscoyotes", "^Secondary"); // Starts with "Secondary"
        result.put("mpccpb", MATCH_NOTHING); // mpccpb matches using username (just in case we ever use default)
        //result.put(DEFAULT_MAP_KEY, "(^|[^a-zA-Z])Secondary($|[^a-zA-Z])"); // Contains word "Secondary"
        return result;
    }
    
    private static Map<String, String> loadTypeSpouseRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("medinahcc", "Spouse$"); // Ends with "Spouse"
        result.put("desertmountain", "^Spouse"); // Starts with "Spouse"
        result.put("oswegolake", "(Ladies$|^Adult Female$)"); // Ends with "Ladies" or equals "Adult Female"
        result.put("pinehurstcountryclub", "^Qualified"); // Starts with "Qualified"
        result.put("mpccpb", MATCH_NOTHING); // mpccpb matches using username (just in case we ever use default)
        result.put("denvercc", "(^|[^a-zA-Z])Spouse($|[^a-zA-Z])"); // Contains word "Spouse"
        //result.put(DEFAULT_MAP_KEY, "(^|[^a-zA-Z])Spouse($|[^a-zA-Z])"); // Contains word "Spouse"
        return result;
    }
    
    private static Map<String, String> loadTypeSpouseByUsernameRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("mpccpb", "\\.1$"); // Username ends with ".1"
        return result;
    }
    
    private static Map<String, String> loadTypeHonoraryRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("virginiacc", "^Honorary"); // Starts with "Honorary"
        result.put("mpccpb", MATCH_NOTHING); // mpccpb matches using username (just in case we ever use default)
        //result.put(DEFAULT_MAP_KEY, "(^|[^a-zA-Z])Honorary($|[^a-zA-Z])"); // Contains word "Honorary"
        return result;
    }
    
    private static Map<String, String> loadTypeSeniorRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("virginiacc", "^Senior"); // Starts with "Senior"
        result.put("mpccpb", MATCH_NOTHING); // mpccpb matches using username (just in case we ever use default)
        //result.put(DEFAULT_MAP_KEY, "(^|[^a-zA-Z])Senior($|[^a-zA-Z])"); // Contains word "Senior"
        return result;
    }
    
    private static Map<String, String> loadTypeStudentRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("virginiacc", "^Student"); // Starts with "Student"
        result.put("mpccpb", MATCH_NOTHING); // mpccpb matches using username (just in case we ever use default)
        //result.put(DEFAULT_MAP_KEY, "(^|[^a-zA-Z])Student($|[^a-zA-Z])"); // Contains word "Student"
        return result;
    }
    
    private static Map<String, String> loadTypeYoungAdultRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("virginiacc", "^Young Adult"); // Starts with "Young Adult"
        result.put("mpccpb", MATCH_NOTHING); // mpccpb matches using username (just in case we ever use default)
        //result.put(DEFAULT_MAP_KEY, "(^|[^a-zA-Z])Young Adult($|[^a-zA-Z])"); // Contains word "Young Adult"
        return result;
    }
    
    private static Map<String, String> loadTypeJuniorRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("virginiacc", MATCH_ANY); // Any mem type that's not yet been matched
        result.put("loscoyotes", MATCH_ANY); // Any mem type that's not yet been matched
        result.put("mpccpb", MATCH_NOTHING); // mpccpb matches using username (just in case we ever use default)
        //result.put(DEFAULT_MAP_KEY, "(^|[^a-zA-Z])Junior($|[^a-zA-Z])"); // Contains word "Junior"
        return result;
    }
    
    private static Map<String, String> loadTypeDependentRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("medinahcc", MATCH_ANY); // Any mem type that's not yet been matched
        result.put("desertmountain", MATCH_ANY); // Any mem type that's not yet been matched
        result.put("oswegolake", MATCH_ANY); // Any mem type that's not yet been matched
        result.put("pinehurstcountryclub", MATCH_ANY); // Any mem type that's not yet been matched
        result.put("denvercc", MATCH_ANY); // Any mem type that's not yet been matched
        result.put("mpccpb", MATCH_NOTHING); // mpccpb matches using username (just in case we ever use default)
        //result.put(DEFAULT_MAP_KEY, MATCH_ANY); // Any mem type that's not yet been matched
        return result;
    }
    
    private static Map<String, String> loadTypeDependentByUsernameRegexp(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("mpccpb", MATCH_ANY); // Any username that's not yet been matched
        return result;
    }
    
    private final static Map<Integer, String> type_status_text_lookup = loadTypeStatusText();
    private final static Map<Integer, String> type_status_abrv_lookup = loadTypeStatusAbrv();
    
    
    private static final String DEFAULT_MAP_KEY = "default_key"; 
    private static final String SKIP_DEFAULT_KEY = "skip_default_key";
    
    // Pattern cache (cache of compiled regex expressions)
    private static Map<String, Pattern> regex_pattern_cache = new ConcurrentHashMap<String, Pattern>();
    
    // Cache status lookups
    private static Map<String, Map<String, Integer>> type_status_club_cache = new ConcurrentHashMap<String, Map<String, Integer>>();
    
    private static Integer getFromTypeStatusCache(String club_db_name, String mtype){
        
        Integer result = null;
        
        Map<String, Integer> type_status_cache = type_status_club_cache.get(club_db_name);
        if(type_status_cache != null){
            result = type_status_cache.get(mtype);
        }
        return result;
        
    }
    
    private static void setTypeStatusCache(String club_db_name, String mtype, Integer type_status){
        
        Map<String, Integer> type_status_cache = type_status_club_cache.get(club_db_name);
        if(type_status_cache == null){
            type_status_cache = new ConcurrentHashMap<String, Integer>();
            type_status_club_cache.put(club_db_name, type_status_cache);
        }
        type_status_cache.put(mtype, type_status);
    }
    
    // Cache gender lookups
    private static Map<String, String> gender_from_mtype_cache = new HashMap<String, String>();
    
    
    //
    public MemberType(){}; // Empty parm
    
    public MemberType(Long id, String name, Integer count){
        this.id = id;
        this.name = name;
        this.count = count;
    };
    
    public MemberType(String club_db_name, String mtype, String username, String member_gender){
        this.name = mtype;
        this.gender = getGenderFromMType(mtype, member_gender);
        this.type_status = getMtypeStatus(club_db_name, mtype, username);
        if(type_status != null){
            this.type_status_text = getTypeStatusText(type_status);
            this.type_status_abrv = getTypeStatusAbrv(type_status);
        }
    };
    
    
    
    public static String getTypeStatusText(Integer type_status){
        return type_status_text_lookup.get(type_status);
    }
    
    private static Map<Integer, String> loadTypeStatusText(){
        Map<Integer, String> result = new HashMap<Integer, String>();
        
        result.put(TYPE_PRIMARY, "Primary");
        result.put(TYPE_SECONDARY, "Secondary");
        result.put(TYPE_SPOUSE, "Spouse");
        result.put(TYPE_HONORARY, "Honorary");
        result.put(TYPE_SENIOR, "Senior");
        result.put(TYPE_STUDENT, "Student");
        result.put(TYPE_YOUNG_ADULT, "Young Adult");
        result.put(TYPE_JUNIOR, "Junior");
        result.put(TYPE_DEPENDENT, "Dependent");
        
        return result;
        
    }
    
    public static String getTypeStatusAbrv(Integer type_status){
        return type_status_abrv_lookup.get(type_status);
    }
    
    private static Map<Integer, String> loadTypeStatusAbrv(){
        Map<Integer, String> result = new HashMap<Integer, String>();
        /* A-Z and 0-9 only.  No special charaters, spaces, etc.  3 letters long. */
        result.put(TYPE_PRIMARY, "Pri");
        result.put(TYPE_SECONDARY, "Sec");
        result.put(TYPE_SPOUSE, "Spo");
        result.put(TYPE_HONORARY, "Hon");
        result.put(TYPE_SENIOR, "Snr");
        result.put(TYPE_STUDENT, "Stu");
        result.put(TYPE_YOUNG_ADULT, "Yng");
        result.put(TYPE_JUNIOR, "Jnr");
        result.put(TYPE_DEPENDENT, "Dep");
        
        return result;
        
    }
    
    private static String getRegExp(Map<String, String> map, String key){
        // See if there's a rexexp for this key (club)
        String result = map.get(key);
        if(result == null && !key.equals(SKIP_DEFAULT_KEY)){
            // See if there is a default
            result = map.get(DEFAULT_MAP_KEY);
        }
        if(result == null){
            // Return a regex that will match nothing
            result = MATCH_NOTHING;
        }
        return result;
    }
    
    private static String getRegExpIfSql(String regex, String if_true, String if_false){
        return getRegExpIfSql(regex, if_true, if_false, "m_type");
    }
    private static String getRegExpIfUsernameSql(String regex, String if_true, String if_false){
        return getRegExpIfSql(regex, if_true, if_false, "username");
    }
    private static String getRegExpIfSql(String regex, String if_true, String if_false, String field){
        StringBuilder result = new StringBuilder();
        result.append(" IF('");
        result.append(regex.replaceAll("(')","\\$1")); // escape any characters we may need to (this is not exaustive.  Only use trusted data)
        result.append(" ' REGEXP ");
        result.append(field);
        result.append(",");
        result.append(if_true);
        result.append(",");
        result.append(if_false);
        result.append(") ");
        return result.toString();
    }
    
    // Hack to generate SQL used determine status/order of a MemberType until a member_type table is created to handle this sort of thing
    // Used when querying members
    public static String getTypeStatusSql(String club_db_name, String username){
        
        StringBuilder result = new StringBuilder();
        result.append(
                getRegExpIfSql(
                    getRegExp(type_primary_regex, club_db_name), // If m_type matched primary regex for club
                    TYPE_PRIMARY.toString(), // return primary as "mtype_status" (as string for ease in using method, but is returned as Integer in SQL)
                    getRegExpIfSql( 
                        getRegExp(type_secondary_regex, club_db_name), // Else, try matching secondary, and so on.
                            TYPE_SECONDARY.toString(),
                            getRegExpIfSql(
                                getRegExp(type_spouse_regex, club_db_name),
                                    TYPE_SPOUSE.toString(),
                                    getRegExpIfSql(
                                        getRegExp(type_honorary_regex, club_db_name),
                                            TYPE_HONORARY.toString(),
                                            getRegExpIfSql(
                                                getRegExp(type_senior_regex, club_db_name),
                                                    TYPE_SENIOR.toString(),
                                                    getRegExpIfSql(
                                                        getRegExp(type_student_regex, club_db_name),
                                                            TYPE_STUDENT.toString(),
                                                            getRegExpIfSql(
                                                                getRegExp(type_young_adult_regex, club_db_name),
                                                                    TYPE_YOUNG_ADULT.toString(),
                                                                    getRegExpIfSql(
                                                                        getRegExp(type_junior_regex, club_db_name),
                                                                            TYPE_JUNIOR.toString(),
                                                                            getRegExpIfSql(
                                                                                getRegExp(type_dependent_regex, club_db_name),
                                                                                    TYPE_DEPENDENT.toString(),
                                                                                    (username==null?"NULL":
                                                                                        // If a username was passed, let's check that 
                                                                                        getRegExpIfUsernameSql(
                                                                                            getRegExp(type_primary_by_username_regex, club_db_name),
                                                                                                TYPE_PRIMARY.toString(),
                                                                                                getRegExpIfUsernameSql(
                                                                                                    getRegExp(type_spouse_by_username_regex, club_db_name),
                                                                                                        TYPE_SPOUSE.toString(),
                                                                                                        getRegExpIfUsernameSql(
                                                                                                            getRegExp(type_dependent_by_username_regex, club_db_name),
                                                                                                                TYPE_DEPENDENT.toString(),
                                                                                                                "NULL"
                                                                                                            )
                                                                                                    )
                                                                                            )
                                                                                    )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        
        result.append(" AS mtype_status ");
        
        return result.toString();
        
    }
    public static String getGenderSql(String club_db_name, String username){
        
        StringBuilder result = new StringBuilder();
        
        result.append(
                getRegExpIfSql("^M$", "1", 
                    getRegExpIfSql("^F$", "0", 
                        getRegExpIfSql(MATCH_MALE, "1", 
                            getRegExpIfSql(MATCH_FEMALE, "0", 
                            "NULL", // NULL = Unknown gender
                            "m_type"),
                        "m_type"), 
                    "gender"), 
                "gender")
            );
        
        result.append(" AS mtype_gender ");
        
        return result.toString();
        
    }
    
    // Do not call this with regex expressions that change often, or will never be re-used!! You'll eventually eat all your RAM if you do!
    public static boolean match(String string, String regex){
        if(regex == null || string == null){
            return false;
        }
        Pattern pattern = regex_pattern_cache.get(regex);
        if(pattern == null){
            // Compile our pattern
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            // Save it for later
            regex_pattern_cache.put(regex, pattern);
        }
        return pattern.matcher(string).find();
    }
    
    public static Integer getMtypeStatus(String club_db_name, String mtype, String username){
        
        
        // See if we can get the member type status from the username
        // (may not be that big of a performance hit, vs. the number of user names we'd need to cache, so don't bother caching it)
        if(match(username, type_primary_by_username_regex.get(club_db_name))) {
            return TYPE_PRIMARY;
        } else if(match(username, type_spouse_by_username_regex.get(club_db_name))) {
            return TYPE_SPOUSE;
        } else if(match(username, type_dependent_by_username_regex.get(club_db_name))) {
            return TYPE_DEPENDENT;
        }
        
        // See if the mtype status is in cache.
        Integer result = getFromTypeStatusCache(club_db_name, mtype);
        
        if(result != null && result < 0){
            return null; // less than zero signifies null in cache
        } else if (result != null){
            return result; // Return the cached result
        } else {
            // Not in cache. Figure out what it is. (Expensive.)
            if(match(mtype, type_primary_regex.get(club_db_name))){
                result = TYPE_PRIMARY;
            } else if(match(mtype, type_secondary_regex.get(club_db_name))) {
                result = TYPE_SECONDARY;
            } else if(match(mtype, type_spouse_regex.get(club_db_name))) {
                result = TYPE_SPOUSE;
            } else if(match(mtype, type_honorary_regex.get(club_db_name))) {
                result = TYPE_HONORARY;
            } else if(match(mtype, type_senior_regex.get(club_db_name))) {
                result = TYPE_SENIOR;
            } else if(match(mtype, type_student_regex.get(club_db_name))) {
                result = TYPE_STUDENT;
            } else if(match(mtype, type_young_adult_regex.get(club_db_name))) {
                result = TYPE_YOUNG_ADULT;
            } else if(match(mtype, type_junior_regex.get(club_db_name))) {
                result = TYPE_JUNIOR;
            } else if(match(mtype, type_dependent_regex.get(club_db_name))) {
                result = TYPE_DEPENDENT;
            }
            
            // Cache the result.  
            // If result is null, store as -1, so we can detect the difference between a cached "null" result and a cache miss. 
            setTypeStatusCache(club_db_name, mtype, (result==null?-1:result));

        }
        
        
        return result;
        
    }
    
    public static String getGenderFromMType(String mtype, String member_gender){
        String result = null; // unknown gender by defult
        // Check if meber's gender field has a proper gender setting
        if(member_gender != null){
            if(member_gender.equalsIgnoreCase("f")){
                result = "F"; // female
            } else if(member_gender.equalsIgnoreCase("m")) {
                result = "M"; // male;
            }
        }
        if(result == null){
            // gender wasn't specififed in member table's gender field.  Check mtype
            result = gender_from_mtype_cache.get(mtype);
            if(result == null){
                // Not in cache
                if(match(mtype, MATCH_FEMALE)){
                    result = "F";
                } else if(match(mtype, MATCH_MALE)){
                    result = "M";
                }
                gender_from_mtype_cache.put(mtype, (result==null?"":result)); // If no match, store "" for unknown
            }
        }
        if(result != null && result.isEmpty()){
            result = null; // Convert any "" back to null
        }
        return result;
    }
    
    
    public static List<MemberType> getList(long club_id) {
        Connection con_club = ApiCommon.getConnection(club_id);
        List<MemberType> result = getList(con_club);
        Connect.close(con_club);
        return result;
    }
    
    public static List<MemberType> getList(String club) {
        Connection con_club = ApiCommon.getConnection(club);
        List<MemberType> result = getList(con_club);
        Connect.close(con_club);
        return result;
    }
    
    public static List<MemberType> getList(HttpServletRequest req) {
        Connection con_club = Connect.getCon(req);
        List<MemberType> result = getList(con_club);
        Connect.close(con_club);
        return result;
    }
    
    public static List<MemberType> getList(Connection con_club){
        
        List<MemberType> result = new ArrayList<MemberType>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String error = null;
        
        MemberType r = new MemberType();
        
        try {
            Map<String, Integer> counts = new HashMap<String, Integer>();
            pstmt = con_club.prepareStatement(""
                    + "SELECT m_type, COUNT(*) AS count "
                    + "  FROM member2b "
                    + "  WHERE billable != 0 AND inact != 1 "
                    + "  GROUP BY m_type ");
            rs = pstmt.executeQuery();
            
            while(rs.next()){
                counts.put(rs.getString("m_type").toLowerCase(), rs.getInt("count"));
            }

            pstmt = con_club.prepareStatement(ApiCommon.sql_club_parms);

            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String[] array = ArrayUtil.getStringArrayFromResultSet(rs, "mem%", 24);
                for(int i = 0; i < array.length; i++ ){
                    if(!array[i].isEmpty()){
                        Integer count = counts.get(array[i].toLowerCase());
                        if(count == null){
                            r = new MemberType((long)i,array[i],0);
                        } else {
                            r = new MemberType((long)i,array[i],count);
                        }
                        result.add(r);
                    }
                }
            } 
        } catch(Exception e) {
            error = String.format(ApiConstants.error_db_select, r.classNameLower(), e.toString());
            Connect.logError(r.className() + ".getList: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
        
        if(error != null){
            Connect.logError(r.className() + ".getList: Err=" + error);
            return null;
        } else {
            return result;
        } 

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
