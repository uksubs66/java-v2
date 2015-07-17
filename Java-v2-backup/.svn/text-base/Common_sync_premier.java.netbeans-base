/***************************************************************************************
 *   Common_sync_premier:  This servlet will process a roster sync file from Flexscape for a Premier site.
 *
 *   called:  Common_sync - via TimerSync.java (timer mechanism)
 *            Support_sync - to process a single club
 *
 *
 *   created:  4/05/13   Bob P.
 *
 *
 *   updated:
 * 
 * 
 *        4/22/14   Added Persimmon Woods (pwgolf) to flexSync(). 
 *        3/25/14   FS - Mission Viejo CC (missionviejo) - Fixed mship so it's no longer getting pulled from the entity_id field.
 *        3/19/14   FS - Updated flexSync() to read the newly added email2 field.
 *        3/12/14   Pinehurst CC (pinehurstcountryclub) - Do not sync middle initials.
 *        3/10/14   Added Pinehurst CC (pinehurstcountryclub) to flexSync().
 *        3/06/14   Added Mission Viejo (missionviejo) to flexSync().
 *       12/09/13   CC of York (ccyork) - Updated mship processing with an additional mapping.
 *       10/23/13   CC of York (ccyork) - Moved from standard sync processing, and updated for premier processing.
 *       10/21/13   Tonto Verde (tontoverde) - Turned off sync temporarily
 *       10/18/13   Added Tonto Verde (tontoverde) to premier sync processing.
 *        7/15/13   Park Meadows CC (parkmeadowscc) - Updated mship processing with an additional mapping.
 *        5/22/13   Moved Park Meadows CC (parkmeadowscc) from Common_sync.
 *  
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import javax.mail.internet.*;
import javax.mail.*;
import com.foretees.common.FeedBack;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;
import com.foretees.common.sendEmail;
import com.foretees.member.Member;


public class Common_sync_premier {

 static final int TIMER_SERVER = SystemUtils.TIMER_SERVER;
 static int setInactLimit = 200;

 static String rev = SystemUtils.REVLEVEL;

 static String host = SystemUtils.HOST;
 static String port = SystemUtils.PORT;
 static String efrom = SystemUtils.EFROM;
 static String emailFT = "support@foretees.com";



 //
 //   Method to process files from FlexScape for a Premier system
 //
 public static void flexSync(Connection con, InputStreamReader isr, String club, boolean forceSetInact) {

   PreparedStatement pstmt2 = null;
   Statement stmt = null;
   Statement stmt2 = null;
   Statement stmt3 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;


   Member member = new Member();

   String line = "";
   String password = "";
   String temp = "";

   int i = 0;

   // Values from Flexscape records
   //
   String fname = "";
   String lname = "";
   String mi = "";
   String suffix = "";
   String posid = "";
   String gender = "";
   String ghin = "";
   String memid = "";
   String flexid = "";
   String mNum = "";
   String u_hndcp = "";
   String c_hndcp = "";
   String mship = "";
   String mtype = "";
   String bag = "";
   String email = "";
   String email2 = "";
   String phone = "";
   String phone2 = "";
   String phone3 = "";
   String phone4 = "";
   String mobile = "";
   String primary = "";
   String active = "";
   String entity_id = "";
   String custom_data = "";
   String custom_data2 = "";
   String custom_string = "";
   String custom_string2 = "";
   

   String mship2 = ""; // used to tell if match was found

   float u_hcap = 0;
   float c_hcap = 0;
   int birth = 0;
   int default_activity_id = -1;

   // Values from ForeTees records
   //
   String fname_old = "";
   String lname_old = "";
   String mi_old = "";
   String mship_old = "";
   String mtype_old = "";
   String email_old = "";
   String mNum_old = "";
   String ghin_old = "";
   String bag_old = "";
   String posid_old = "";
   String email2_old = "";
   String phone_old = "";
   String phone2_old = "";
   String suffix_old = "";
   String memid_old = "";
   String flexid_old = "";
   String custom_string_old = "";
   String custom_string2_old = "";

   float u_hcap_old = 0;
   float c_hcap_old = 0;
   int birth_old = 0;
   int default_activity_id_old = 0;

   // Values for New ForeTees records
   //
   String fname_new = "";
   String lname_new = "";
   String mi_new = "";
   String mship_new = "";
   String mtype_new = "";
   String email_new = "";
   String mNum_new = "";
   String ghin_new = "";
   String bag_new = "";
   String posid_new = "";
   String email2_new = "";
   String phone_new = "";
   String phone2_new = "";
   String suffix_new = "";
   String memid_new = "";
   String last_mship = "";
   String last_mnum = "";
   String flexid_new = "";
   String custom_string_new = "";
   String custom_string2_new = "";

   float u_hcap_new = 0;
   float c_hcap_new = 0;
   int birth_new = 0;
   int default_activity_id_new = 0;
   int rcount = 0;
   int newCount = 0;
   int modCount = 0;
   int setInactCount = 0;
   int work = 0;

   String errorMsg = "Error in Common_sync_premier.flexSync: ";

   boolean failed = false;
   boolean changed = false;
   boolean skip = false;
   boolean headerFound = false;
   boolean found = false;
   boolean memidChanged = false;
   boolean nameChanged = false;
   boolean useflexid = false;
   boolean getCustomData = false;
   boolean getCustomData2 = false;
   boolean getGhinData = false;
   boolean getBagData = false;
   boolean getPhone3 = false;    // Extra Phone Field
   boolean getPhone4 = false;    // Extra Phone Field
   
   String mNum_curr = "";
   int depCount = 0;
   
   if (club.equals("waialae") || club.equalsIgnoreCase("thelegendclubs")) {
       getCustomData = true;
   }
   
   if (club.equals("mediterra")) {
       getPhone3 = true;
       getPhone4 = true;
   }
   
   if (club.equals("brooklawn")) {
       getCustomData = true;
       getCustomData2 = true;
   }
   
   SystemUtils.logErrorToFile("FlexScape Premier: Error log for " + club + "\nStart time: " + new java.util.Date().toString() + "\n", club, false);

   try {

      BufferedReader br = new BufferedReader(isr);

      while (true) {

          try {

             line = br.readLine();

             if (line == null) {
                break;
             }

             //  Skip the 1st row (header row)

             if (headerFound == false) {

                headerFound = true;

             } else {

                skip = false;
                found = false;        // default to club NOT found

                entity_id = "";
                custom_data = "";
                custom_data2 = "";


                //  Remove the dbl quotes and check for embedded commas

                line = cleanRecord4( line );       // insert a ? if 2 commas found w/o data between them
                line = cleanRecord2( line );       // remove double quotes and embedded commas
                line = cleanRecord4( line );       // check for empty fields again - insert ? between 2 consecutive commas

                rcount++;                          // count the records

                //  parse the line to gather all the info

                StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

                if ( tok.countTokens() > 10 ) {     // enough data ?

                    flexid = tok.nextToken();    //A                                       
                    memid = tok.nextToken();    
                    tok.nextToken();          // eat this value, not used
                    fname = tok.nextToken();
                    mi = tok.nextToken();       //E
                    lname = tok.nextToken();
                    gender = tok.nextToken();
                    email = tok.nextToken();
                    email2 = tok.nextToken();   //I
                    phone = tok.nextToken();
                    phone2 = tok.nextToken();
                    temp = tok.nextToken();             // usr_birthday column
                    primary = tok.nextToken();          // usr_relationship column
                    mship = tok.nextToken();            // grp_name column

                    if (tok.countTokens() > 0) {
                        entity_id = tok.nextToken();    // entity_id col - if provided
                    }

                    if (club.equals("brooklawn")) {    // Two additional phone numbers are being included in their roster, which we don't care about
                        tok.nextToken();
                        tok.nextToken();
                    }
                    
                    if (getCustomData && tok.countTokens() > 0) {
                        custom_data = tok.nextToken();
                    }

                    if (getCustomData2 && tok.countTokens() > 0) {
                        custom_data2 = tok.nextToken();
                    }

                    mNum = "";
                    suffix = "";
                    mtype = "";
                    bag = "";
                    ghin = "";
                    u_hndcp = "";
                    c_hndcp = "";
                    posid = "";
                    mobile = "";
                    active = "";
                    birth = 0;
                    default_activity_id = -1;

                    if (getGhinData && tok.countTokens() > 0) {
                        ghin = tok.nextToken();
                    }

                    if (getBagData && tok.countTokens() > 0) {
                        bag = tok.nextToken();
                    }

                    if (getPhone3 && tok.countTokens() > 0) {
                        phone3 = tok.nextToken();
                    }
                    
                    if (getPhone4 && tok.countTokens() > 0) {
                        phone4 = tok.nextToken();
                    }

                    //
                    //  Check for ? (not provided)
                    //
                    if (flexid.equals( "?" )) {

                        flexid = "";
                    }
                    if (memid.equals( "?" )) {

                        memid = "";
                    }
                    if (fname.equals( "?" )) {

                        fname = "";
                    }
                    if (mi.equals( "?" )) {

                        mi = "";
                    }
                    if (lname.equals( "?" )) {

                        lname = "";
                    }
                    if (mship.equals( "?" )) {

                        mship = "";
                    }
                    if (gender.equals( "?" )) {

                        gender = "";
                    }
                    if (email.equals( "?" )) {

                        email = "";
                    }
                    if (email2.equals( "?" )) {

                        email2 = "";
                    }
                    if (phone.equals( "?" )) {

                        phone = "";
                    }
                    if (phone2.equals( "?" )) {

                        phone2 = "";
                    }
                    if (phone3.equals( "?" )) {

                        phone3 = "";
                    }
                    if (phone4.equals( "?" )) {

                        phone4 = "";
                    }
                    if (temp.equals( "?" ) || temp.equals( "0" )) {

                        temp = "";
                    }
                    if (primary.equals( "?" )) {

                        primary = "";
                    }
                    if (entity_id.equals( "?" )) {

                        entity_id = "";
                    }
                    if (custom_data.equals("?")) {

                        custom_data = "";
                    }
                    if (custom_data2.equals("?")) {

                        custom_data2 = "";
                    }
                    if (ghin.equals("?")) {

                        ghin = "";
                    }
                    if (bag.equals("?")) {

                        bag = "";
                    }

                    //
                    //  Check for bad first name
                    //
                    if (!fname.startsWith("a") && !fname.startsWith("b") && !fname.startsWith("c") && !fname.startsWith("d") && !fname.startsWith("e") && 
                        !fname.startsWith("f") && !fname.startsWith("g") && !fname.startsWith("h") && !fname.startsWith("i") && !fname.startsWith("j") && 
                        !fname.startsWith("k") && !fname.startsWith("l") && !fname.startsWith("m") && !fname.startsWith("n") && !fname.startsWith("o") && 
                        !fname.startsWith("p") && !fname.startsWith("q") && !fname.startsWith("r") && !fname.startsWith("s") && !fname.startsWith("t") && 
                        !fname.startsWith("u") && !fname.startsWith("v") && !fname.startsWith("w") && !fname.startsWith("x") && !fname.startsWith("y") && 
                        !fname.startsWith("z") && !fname.startsWith("A") && !fname.startsWith("B") && !fname.startsWith("C") && !fname.startsWith("D") && 
                        !fname.startsWith("E") && !fname.startsWith("F") && !fname.startsWith("G") && !fname.startsWith("H") && !fname.startsWith("I") && 
                        !fname.startsWith("J") && !fname.startsWith("K") && !fname.startsWith("L") && !fname.startsWith("M") && !fname.startsWith("N") && 
                        !fname.startsWith("O") && !fname.startsWith("P") && !fname.startsWith("Q") && !fname.startsWith("R") && !fname.startsWith("S") && 
                        !fname.startsWith("T") && !fname.startsWith("U") && !fname.startsWith("V") && !fname.startsWith("W") && !fname.startsWith("X") && 
                        !fname.startsWith("Y") && !fname.startsWith("Z")) {

                        fname = "";          // skip this record
                    }

                    if (club.equals("mediterra") && memid.equals("") && mship.equalsIgnoreCase("Employee")) {
                        memid = "emp" + flexid;
                    }


                    //
                    //  Determine if we should process this record (does it meet the minimum requirements?)
                    //
                    if (!flexid.equals( "" ) && !memid.equals( "" ) && !lname.equals( "" ) && !fname.equals( "" )) {

                        //
                        //  Remove spaces, etc. from name fields
                        //
                        tok = new StringTokenizer( fname, " " );     // delimiters are space

                        fname = tok.nextToken();                     // remove any spaces and middle name

                        if ( tok.countTokens() > 0 ) {

                            mi = tok.nextToken();                     // over-write mi if already there
                            
                            if (mi.startsWith("?") || mi.startsWith("(") || mi.startsWith("\"")) {    // Attempt to fix bad middle initials
                                mi = "";
                            }
                        }

                        if (!suffix.equals( "" )) {                     // if suffix provided

                            tok = new StringTokenizer( suffix, " " );     // delimiters are space

                            suffix = tok.nextToken();                     // remove any extra (only use one value)
                        }

                        tok = new StringTokenizer( lname, " " );     // delimiters are space

                        lname = tok.nextToken();                     // remove suffix and spaces

                        if (!suffix.equals( "" )) {                  // if suffix provided

                            lname = lname + "_" + suffix;             // append suffix to last name

                        } else {                                     // sufix after last name ?

                            if ( tok.countTokens() > 0 ) {

                                suffix = tok.nextToken();
                                lname = lname + "_" + suffix;          // append suffix to last name
                            }
                        }

                        //
                        //  Determine the handicaps
                        //
                        u_hcap = -99;                    // indicate no hndcp
                        c_hcap = -99;                    // indicate no c_hndcp


                        //
                        //  convert birth date (mm/dd/yyyy to yyyymmdd)
                        //
                        if (!temp.equals( "" )) {

                            String b1 = "";
                            String b2 = "";
                            String b3 = "";
                            int mm = 0;
                            int dd = 0;
                            int yy = 0;

                            tok = new StringTokenizer( temp, "/-" );     // delimiters are / & -

                            if ( tok.countTokens() > 2 ) {

                                b1 = tok.nextToken();
                                b2 = tok.nextToken();
                                b3 = tok.nextToken();

                                mm = Integer.parseInt(b1);
                                dd = Integer.parseInt(b2);
                                yy = Integer.parseInt(b3);

                                if (yy < 100) {            // if only 2 digits

                                    if (yy < 15) {

                                        yy = 2000 + yy;

                                    } else {

                                        yy = 1900 + yy;
                                    }
                                }

                                if (yy < 1900) {                             // check for invalid date

                                    birth = 0;

                                } else { 

                                    birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd
                                }

                            } else {            // try 'Jan 20, 1951' format

                                tok = new StringTokenizer( temp, ", " );          // delimiters are comma and space

                                if ( tok.countTokens() > 2 ) {

                                    b1 = tok.nextToken();
                                    b2 = tok.nextToken();
                                    b3 = tok.nextToken();

                                    if (b1.startsWith( "Jan" )) {
                                        mm = 1;
                                    } else if (b1.startsWith( "Feb" )) {
                                        mm = 2;
                                    } else if (b1.startsWith( "Mar" )) {
                                        mm = 3;
                                    } else if (b1.startsWith( "Apr" )) {
                                        mm = 4;
                                    } else if (b1.startsWith( "May" )) {
                                        mm = 5;
                                    } else if (b1.startsWith( "Jun" )) {
                                        mm = 6;
                                    } else if (b1.startsWith( "Jul" )) {
                                        mm = 7;
                                    } else if (b1.startsWith( "Aug" )) {
                                        mm = 8;
                                    } else if (b1.startsWith( "Sep" )) {
                                        mm = 9;
                                    } else if (b1.startsWith( "Oct" )) {
                                        mm = 10;
                                    } else if (b1.startsWith( "Nov" )) {
                                        mm = 11;
                                    } else if (b1.startsWith( "Dec" )) {
                                        mm = 12;
                                    } else {
                                        mm = Integer.parseInt(b1);
                                    }

                                    dd = Integer.parseInt(b2);
                                    yy = Integer.parseInt(b3);

                                    birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd

                                    if (yy < 1900) {                             // check for invalid date

                                        birth = 0;
                                    }
                                }
                            }
                        }

                        password = lname;

                        //
                        //  if lname is less than 4 chars, fill with 1's
                        //
                        int length = password.length();

                        while (length < 4) {

                            password = password + "1";
                            length++;
                        }

                        //
                        //  Verify the email addresses
                        //
                        if (!email.equals( "" )) {      // if specified

                            email = email.trim();           // remove spaces

                            FeedBack feedback = (member.isEmailValid(email));

                            if (!feedback.isPositive()) {    // if error

                                email = "";                   // do not use it
                            }
                        }
                        if (!email2.equals( "" )) {      // if specified

                            email2 = email2.trim();           // remove spaces

                            FeedBack feedback = (member.isEmailValid(email2));

                            if (!feedback.isPositive()) {    // if error

                                email2 = "";                   // do not use it
                            }
                        }

                        // if email #1 is empty then assign email #2 to it
                        if (email.equals("")) email = email2;


                        //
                        // *********************************************************************
                        //
                        //   The following will be dependent on the club - customized
                        //
                        // *********************************************************************
                        //




                         //******************************************************************
                         //   Demo Roger - test
                         //******************************************************************
                         //
                         if (club.equals("demoroger")) {

                             found = true;        // club found

                             mship = "Golf";       // for all *********************

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if flexid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (memid.equals("")) {

                                 skip = true;              // skip record if memid not provided
                                 SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;       // memid is the member number

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;

                                 memid = flexid;          // use Flexscape's member id for our username !!!!


                                 /*
                                 if (mship.equals("1") || mship.equals("2") || mship.equals("3") || mship.equals("4")
                                  || mship.equals("20") || mship.equals("21") || mship.equals("22") || mship.equals("23")) {
                                     mship = "Resident Member";
                                 } else if (mship.equals("14") || mship.equals("15")) {
                                     mship = "Corporate Member";
                                 } else if (mship.equals("5") || mship.equals("6")) {
                                     mship = "Junior Sporting Member";
                                 } else if (mship.equals("8") || mship.equals("26")) {
                                     mship = "Non-Resident Member";
                                 } else if (mship.equals("31") || mship.equals("32")) {
                                     mship = "Regional Member";
                                 } else if (mship.equals("11") || mship.equals("13") || mship.equals("24") || mship.equals("25")) {
                                     mship = "Resident Junior Member";
                                 } else if (mship.equals("18") || mship.equals("19")) {
                                     mship = "Senior Member";
                                 } else if (mship.equals("7") || mship.equals("9")) {
                                     mship = "Sporting Member";
                                 } else if (mship.equals("17")) {
                                     mship = "Surviving Spouse Member";
                                 } else if (mship.equals("16")) {
                                     mship = "Widow Member";
                                 } else {
                                     skip = true;
                                     SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }
                                 * 
                                 */

                                 if (primary.equals("0")) {

                                     mtype = "Full Golf - Male";

                                     if (gender.equals("")) {
                                         gender = "M";
                                     }

                                 } else if (primary.equals("1")) {

                                     mtype = "Full Golf - Female";

                                     if (gender.equals("")) {
                                         gender = "F";
                                     }

                                 } else {

                                     mtype = "Dependent";
                                 }

                                 fname = toTitleCase(fname);
                                 lname = toTitleCase(lname);

                             }
                         }      // end of if ????????


                         //******************************************************************
                         //   Park Meadows CC - parkmeadowscc
                         //******************************************************************
                         //
                         if (club.equals("parkmeadowscc")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;

                                 if (mNum.indexOf("-") != -1) {
                                     mNum = mNum.substring(0, mNum.length() - 4);
                                 }

                                 if (mship.equalsIgnoreCase("Social")) {
                                     mship = "Social";
                                 // } else if (!mship.startsWith("Employee") && !mship.equalsIgnoreCase("House Account")) {
                                 } else if (mship.equalsIgnoreCase("Trial")) {
                                     mship = "Trial";  
                                 } else if (!mship.equalsIgnoreCase("House Account")) {
                                     mship = "Golf";
                                 } else {
                                     skip = true;              // skip record if webid not provided
                                     SystemUtils.logErrorToFile("NON-GOLF MEMBERSHIP TYPE", club, true);
                                 }

                                 if (memid.endsWith("-000")) {

                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         mtype = "Primary Male";
                                     }

                                 } else if (memid.endsWith("-001")) {

                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Spouse Male";
                                     } else {
                                         mtype = "Spouse Female";
                                     }

                                 } else {

                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Dependent Female";
                                     } else {
                                         mtype = "Dependent Male";
                                     }
                                 }

                             }
                         }      // end of if parkmeadowscc


                         //******************************************************************
                         //   Tonto Verde CC - tontoverde
                         //******************************************************************
                         //
                         /*
                         if (club.equals("tontoverde")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             /*if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else *//*if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 String[] tempMemid = memid.split("-");

                                 if (tempMemid.length == 2) {
                                     memid = tempMemid[1];
                                 }

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 memid = "T" + memid;

                                 if (memid.endsWith("G") || memid.endsWith("g")) {
                                     memid = memid.substring(0, memid.length() - 1);
                                 }

                                 mNum = memid;                             

                                 posid = memid;

                                 mship = "Social";  // THIS NEEDS TO CHANGE AFTER 11/1 WHEN THEY ADD PROPER MSHIP VALUES TO FILE.  FOR NOW, WE ARE NOT SYNCING MSHIPS FOR OLD RECORDS, AND ARE SETTING ALL NEW RECORDS TO SOCIAL

                                 if (primary.equals("1")) {
                                     memid += "A";
                                 }

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Adult Female";
                                 } else {
                                     gender = "M";
                                     mtype = "Adult Male";
                                 }
                             }
                         }      // end of if tontoverde
                    */
                         //******************************************************************
                         //   CC of York - ccyork
                         //******************************************************************
                         //
                         if (club.equals( "ccyork" )) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             /*if (mship.equals( "" )) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else*/ if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (memid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;   

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;

                                 if (mNum.equals(mNum_curr)) {
                                     if (primary.equals("2")) {
                                         depCount++;
                                     }
                                 } else {
                                     depCount = 0;
                                     mNum_curr = mNum;
                                 }

                                 if (mship.equals("1") || mship.equals("2") || mship.equals("3") || mship.equals("4")
                                  || mship.equals("20") || mship.equals("21") || mship.equals("22") || mship.equals("23")
                                  || mship.equals("12")) {
                                     mship = "Resident Member";
                                 } else if (mship.equals("14") || mship.equals("15")) {
                                     mship = "Corporate Member";
                                 } else if (mship.equals("5") || mship.equals("6")) {
                                     mship = "Junior Sporting Member";
                                 } else if (mship.equals("8") || mship.equals("26")) {
                                     mship = "Non-Resident Member";
                                 } else if (mship.equals("31") || mship.equals("32")) {
                                     mship = "Regional Member";
                                 } else if (mship.equals("11") || mship.equals("13") || mship.equals("24") || mship.equals("25")) {
                                     mship = "Resident Junior Member";
                                 } else if (mship.equals("18") || mship.equals("19")) {
                                     mship = "Senior Member";
                                 } else if (mship.equals("7") || mship.equals("9") || mship.equals("10")) {
                                     mship = "Sporting Member";
                                 } else if (mship.equals("17")) {
                                     mship = "Surviving Spouse Member";
                                 } else if (mship.equals("16")) {
                                     mship = "Widow Member";
                                 } else if (mship.equals("94")) {
                                     mship = "Preview Member";
                                 } else if (mship.equals("99")) {
                                     mship = "No Dues";
                                 } else {
                                     skip = true;
                                     SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ") "+lname + ", " + fname+" :"+memid, club, true);
                                 }

                                 if (primary.equals("0") || primary.equals("1")) {

                                    if (primary.equals("0")) {

                                        if (gender.equals("")) {
                                            gender = "M";
                                        }

                                    } else if (primary.equals("1")) {

                                        //memid += "A";

                                        if (gender.equals("")) {
                                            gender = "F";
                                        }
                                    }

                                    if (gender.equalsIgnoreCase("F")) {
                                        mtype = "Adult Female";
                                    } else {
                                        mtype = "Adult Male";
                                    }

                                 } else if (primary.equals("2")) {

                                     Calendar cal = new GregorianCalendar();        // get todays date
                                     int year = cal.get(Calendar.YEAR);
                                     int month = cal.get(Calendar.MONTH)+1;
                                     int day = cal.get(Calendar.DAY_OF_MONTH);

                                     year = year - 18;                              // date to determine if < 18 yrs old

                                     int date18 = (year * 10000) + (month * 100) + day;

                                     if (birth > date18) {
                                         if (gender.equalsIgnoreCase("F")) {
                                             mtype = "Junior Female 17 and Under";
                                         } else {
                                             mtype = "Junior Male 17 and Under";
                                         }
                                     } else {
                                         if (gender.equalsIgnoreCase("F")) {
                                             mtype = "Dependent Female 18 - 23";
                                         } else {
                                             mtype = "Dependent Male 18 - 23";
                                         }
                                     }
                                 }

                                 if (mNum.endsWith("A") || mNum.endsWith("B") || mNum.endsWith("C") || mNum.endsWith("D") || mNum.endsWith("E") 
                                         || mNum.endsWith("F") || mNum.endsWith("G") || mNum.endsWith("H") || mNum.endsWith("I")) {
                                     mNum = mNum.substring(0, mNum.length() - 1);
                                 }

                                 fname = toTitleCase(fname);
                                 lname = toTitleCase(lname);

                             }
                         }      // end of if ccyork


                         //******************************************************************
                         //   Mission Viejo - missionviejo
                         //******************************************************************
                         //
                         if (club.equals("missionviejo")) {

                             found = true;        // club found

                             entity_id = "";

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;

                                 if (!gender.equalsIgnoreCase("F") && !gender.equalsIgnoreCase("M")) {
                                     gender = "M";
                                 }

                                 if (primary.equals("0")) {
                                     memid += "A";
                                     mtype = "Gold";
                                 } else if (primary.equals("1")) {
                                     memid += "B";
                                     mtype = "Green";
                                 } else if (primary.equals("2")) {

                                     // for Juniors, their memid is already taken care of (C1, C2, C3, etc in file).  Just set their mship/mtype
                                     mship = "Junior";

                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Junior Female";
                                     } else {
                                         mtype = "Junior Male";
                                     }
                                 }

                                 if (mship.equalsIgnoreCase("Designee Golf") || mship.equalsIgnoreCase("Equity") || mship.equalsIgnoreCase("Preview")
                                     || mship.equalsIgnoreCase("Ambassador Preview") || mship.equalsIgnoreCase("Corporate Transfer") || mship.equalsIgnoreCase("Equity Founding")
                                     || mship.equalsIgnoreCase("Junior Executive") || mship.equalsIgnoreCase("Junior Executive Under 30") || mship.equalsIgnoreCase("Non-Resident Member") || mship.equalsIgnoreCase("Diplomat Program")) {
                                     mship = "Equity";
                                 } else if (mship.equalsIgnoreCase("Employee Account")) {
                                     mship = "Staff";
                                 } else if (mship.equalsIgnoreCase("Equity (LOA)")) {
                                     mship = "Leave of Absence Designee";
                                 } else if (mship.equalsIgnoreCase("Honorary") || mship.equalsIgnoreCase("Honorary Delegate") || mship.equalsIgnoreCase("Senior") 
                                         || mship.equalsIgnoreCase("Spec Full Delegate Golf")) {
                                     mship = "Senior";
                                 } else if (mship.equalsIgnoreCase("Spouse")) {
                                     mship = "Spouse";
                                 } else if (mship.equalsIgnoreCase("Membership Dues") || mship.equalsIgnoreCase("Membership Dues (T)") 
                                         || mship.equalsIgnoreCase("Special Reserve") || mship.equalsIgnoreCase("Special Social Dues")) {
                                     mship = "Reserve";
                                 } else if (mship.equalsIgnoreCase("Junior")) {
                                     mship = "Junior";
                                 } else if (mship.equalsIgnoreCase("Reserve Preview")) {
                                     mship = "Social";
                                 } else {
                                     skip = true;
                                     SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }

                             }
                         }      // end of if missionviejo


                         //******************************************************************
                         //   Pinehurst CC - pinehurstcountryclub
                         //******************************************************************
                         //
                         if (club.equals("pinehurstcountryclub")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;

                                 if (primary.equals("0")) {

                                     memid += "0";

                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         gender = "M";
                                         mtype = "Primary Male";
                                     }
                                 } else if (primary.equals("1")) {

                                     memid += "1";

                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Secondary Male";
                                     } else {
                                         gender = "F";
                                         mtype = "Secondary Female";
                                     }
                                 }

                                 if (mship.endsWith("Proprietary Loyalty") || mship.startsWith("Age Based") || mship.equalsIgnoreCase("Business") || mship.equalsIgnoreCase("Honorary") 
                                         || mship.equalsIgnoreCase("Life") || mship.equalsIgnoreCase("No Dues") || mship.equalsIgnoreCase("Non Resident") || mship.equalsIgnoreCase("Over 80") 
                                         || mship.equalsIgnoreCase("Proprietary") || mship.equalsIgnoreCase("Subscription") || mship.equalsIgnoreCase("Ladies 9 Holers") || mship.equalsIgnoreCase("Resident Emeritus")) {
                                     mship = "Proprietary";
                                 } else if (mship.equalsIgnoreCase("Associate") || mship.equalsIgnoreCase("Associate Over 80")) {
                                     mship = "Associate";
                                 } else if (mship.equalsIgnoreCase("Clubhouse") || mship.equalsIgnoreCase("Social")) {
                                     mship = "Clubhouse";
                                 } else {
                                     skip = true;
                                     SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }      

                                 fname = toTitleCase(fname);
                                 lname = toTitleCase(lname);                
                             }
                         }      // end of if pinehurstcountryclub


                         //******************************************************************
                         //   Persimmon Woods - pwgolf
                         //******************************************************************
                         //
                         if (club.equals("pwgolf")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;

                                 mNum = mNum.substring(0, mNum.length() - 4);

                                 if (primary.equals("0") || primary.equals("1")) {

                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Adult Female";
                                     } else {
                                         gender = "M";
                                         mtype = "Adult Male";
                                     }
                                 } else {
                                     mtype = "Dependents";
                                 }

                                 if (mship.equalsIgnoreCase("Corp Dining Membership") || mship.equalsIgnoreCase("Dining Membership") || mship.equalsIgnoreCase("Inactive")) {
                                     mship = "Dining";
                                 } else if (mship.equalsIgnoreCase("Corp.Family") || mship.equalsIgnoreCase("Corp. Individual") || mship.equalsIgnoreCase("Family") 
                                         || mship.equalsIgnoreCase("Honorary") || mship.equalsIgnoreCase("Individual") || mship.equalsIgnoreCase("Miscellaneous") 
                                         || mship.equalsIgnoreCase("Student") || mship.equalsIgnoreCase("1/2 Price Members") || mship.equalsIgnoreCase("Investor") 
                                         || mship.equalsIgnoreCase("Junior 3 Membership") || mship.equalsIgnoreCase("Golf Industry Affiliate")) {
                                     mship = "Golf";
                                 } else if (mship.equalsIgnoreCase("Previous Investor")) {
                                     mship = "Investors";
                                 } else if (mship.startsWith("Junior 1")) {
                                     mship = "Junior Member 1";
                                 } else if (mship.startsWith("Junior 2")) {
                                     mship = "Junior Member 2";
                                 } else if (mship.equalsIgnoreCase("Senior") || mship.equalsIgnoreCase("Senior Family Membership")) {
                                     mship = "Senior";
                                 }  else {
                                     skip = true;
                                     SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }                     
                             }
                         }      // end of if pwgolf


                         //******************************************************************
                         //   Hillwood CC - hillwoodcc
                         //******************************************************************
                         //
                         if (club.equals( "hillwoodcc" )) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals( "" )) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;   

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }                   

                                 // Reduce -00# dash-value to -# instead, where # is the primary indicator
                                 if (memid.indexOf("-") != -1) {
                                     memid = memid.substring(0, memid.length() - 3) + memid.substring(memid.length() - 1);
                                 } 

                                 // If -0, remove the dash-value from the username
                                 if (memid.endsWith("-0")) {
                                     memid = memid.substring(0, memid.length() - 2);
                                 }

                                 mNum = memid;

                                 if (mNum.indexOf("-") != -1) {
                                     mNum = mNum.substring(0, mNum.length() - 2);
                                 }

                                 if (mship.equalsIgnoreCase("Honorary")) {
                                     mship = "Honorary";
                                 } else if (mship.equalsIgnoreCase("Intermediate")) {
                                     mship = "Intermediate";
                                 } else if (mship.equalsIgnoreCase("Junior")) {
                                     mship = "Junior";
                                 } else if (mship.equalsIgnoreCase("Non-Resident")) {
                                     mship = "Non Resident";
                                 } else if (mship.equalsIgnoreCase("Pre-Resident")) {
                                     mship = "Pre-Resident";
                                 } else if (mship.equalsIgnoreCase("Resident")) {
                                     mship = "Resident";
                                 } else if (mship.startsWith("Senior - A")) {
                                     mship = "Senior A";
                                 } else if (mship.startsWith("Senior - R")) {
                                     mship = "Senior Resident";
                                 } else if (mship.startsWith("Pre-Social")) {
                                     mship = "Pre-Social";
                                 } else if (mship.startsWith("Social")) {
                                     mship = "Social";
                                 } else if (mship.startsWith("Super Senior")) {
                                     mship = "Super Senior";
                                 } else if (mship.startsWith("Whitworth - Athletic")) {
                                     mship = "Whitworth - Athletic";
                                 } else {
                                     skip = true;              // skip record if webid not provided
                                     SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }

                                 if (memid.endsWith("-1")) {

                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Spouse Male";
                                     } else {
                                         mtype = "Spouse Female";
                                     }

                                 } else if (memid.endsWith("-2") || memid.endsWith("-3") || memid.endsWith("-4") || memid.endsWith("-5") 
                                         || memid.endsWith("-6") || memid.endsWith("-7") || memid.endsWith("-8") || memid.endsWith("-9")) {

                                     Calendar cal = new GregorianCalendar();        // get todays date
                                     int year = cal.get(Calendar.YEAR);
                                     int month = cal.get(Calendar.MONTH)+1;
                                     int day = cal.get(Calendar.DAY_OF_MONTH);

                                     year = year - 18;                              // date to determine if < 18 yrs old

                                     int date18 = (year * 10000) + (month * 100) + day;

                                     year = year - 8;                              // date to determine if < 26 yrs old

                                     int date26 = (year * 10000) + (month * 100) + day;

                                     if (birth > date18) {
                                         mtype = "Dependent up to 17";
                                     } else if (birth > date26) {
                                         mtype = "Dependent 18 to 25";
                                     } else {
                                         mtype = "Dependent";
                                     }

                                 } else {

                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         mtype = "Primary Male";
                                     }

                                 }

                             }
                         }      // end of if hillwoodcc


                         //******************************************************************
                         //   Canterbury GC (canterburygc)
                         //******************************************************************
                         //
                         if (club.equals( "canterburygc" )) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals( "" )) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;   

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }                   

                                 mNum = memid;

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Adult Female";
                                 } else {
                                     gender = "M";
                                     mtype = "Adult Male";
                                 }

                                 if (primary.equals("1") || primary.equals("2")) {

                                     if (!mNum.endsWith("A")) {
                                         mtype = "Dependent";
                                     }

                                     mNum = mNum.substring(0, mNum.length() - 1);
                                 }                      

                                 if (mship.equalsIgnoreCase("SOCIAL")) {
                                     mship = "Social";
                                 } else if (mship.equalsIgnoreCase("SOCIALGLF")) {
                                     mship = "Social w/Golf";
                                 } else if (mship.equalsIgnoreCase("STAFF") || mship.equalsIgnoreCase("STAFF2")) {
                                     mship = "Staff";
                                 } else if (mship.equalsIgnoreCase("SPOUSE")) {

                                     PreparedStatement pstmtx = null;
                                     ResultSet rsx = null;

                                     try {

                                         pstmtx = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                                         pstmtx.clearParameters();
                                         pstmtx.setString(1, mNum);

                                         rsx = pstmtx.executeQuery();

                                         if (rsx.next()) {
                                             mship = rsx.getString("m_ship");
                                         } else {
                                             skip = true;
                                             SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN SPOUSE MEMBERSHIP TYPE (" + mship + ")", club, true);
                                         }

                                     } catch (Exception e) {
                                         Utilities.logError("Common_sync_premier.flexSync - " + club + " - failed looking up primary mship - Error = " + e.toString());
                                     } finally {
                                         Connect.close(rsx, pstmtx);
                                     }

                                 } else if (mship.equalsIgnoreCase("CHILD") || mship.equalsIgnoreCase("LOA")) {
                                     skip = true;
                                     SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 } else {    // All others get set to "Golf"
                                     mship = "Golf";
                                 }                             
                             }
                         }      // end of if canterburygc


                         //******************************************************************
                         //   Blackhawk CC (blackhawkcc)
                         //******************************************************************
                         //
                         if (club.equals( "blackhawkcc" )) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals( "" )) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;   

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }                   

                                 mNum = memid;

                                 if (mNum.contains("-")) {
                                     mNum = mNum.substring(0, mNum.length() - 4);
                                 }

                                 if (primary.equals("")) {    // Some members are coming over with a blank value, and it's causing them to not sync, so just default it to "0"
                                     primary = "0";
                                 }

                                 if (memid.endsWith("-000")) {

                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         gender = "M";
                                         mtype = "Primary Male";
                                     }

                                     memid = mNum;

                                 } else if (memid.endsWith("-001")) {

                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Spouse Male";
                                     } else {
                                         gender = "F";
                                         mtype = "Spouse Female";
                                     }

                                     memid = mNum + "A";

                                 } else {

                                     if (memid.endsWith("-002")) {
                                         memid = mNum + "B";
                                     } else if (memid.endsWith("-003")) {
                                         memid = mNum + "C";
                                     } else if (memid.endsWith("-004")) {
                                         memid = mNum + "D";
                                     } else if (memid.endsWith("-005")) {
                                         memid = mNum + "E";
                                     } else if (memid.endsWith("-006")) {
                                         memid = mNum + "F";
                                     } else if (memid.endsWith("-007")) {
                                         memid = mNum + "G";
                                     } else if (memid.endsWith("-008")) {
                                         memid = mNum + "H";
                                     } else if (memid.endsWith("-009")) {
                                         memid = mNum + "I";
                                     }

                                     mtype = "Dependent";
                                 } 

                                 if (mtype.equalsIgnoreCase("Dependent") || (mship.equalsIgnoreCase("Student Annual") && (primary.equals("0") || primary.equals("5")))) {

                                     if (birth != 0) {

                                         Calendar cal = new GregorianCalendar();        // get todays date
                                         int year = cal.get(Calendar.YEAR);
                                         int month = cal.get(Calendar.MONTH) + 1;
                                         int day = cal.get(Calendar.DAY_OF_MONTH);

                                         int mmddToday = (month * 100) + day;

                                         int yy = birth / 10000;
                                         int mm = (birth - (yy * 10000)) / 100;
                                         int dd = birth - ((yy * 10000) + (mm * 100));
                                         int mmddBirth = (mm * 100) + dd;

                                         int date10 = ((year - 10) * 10000) + (month * 100) + day;
                                         int date14 = ((year - 14) * 10000) + (month * 100) + day;
                                         int date26 = ((year - 26) * 10000) + (month * 100) + day;
                                         int date27 = ((year - 27) * 10000) + (month * 100) + day;

                                         if (birth > date10) {
                                             mtype = "Dependent 9 and under";
                                         } else if (birth > date14) {
                                             mtype = "Dependent 10 to 13";
                                         } else if (birth > date26 || (birth > date27 && mmddToday > mmddBirth)) {    // When deps turn 26, they have access for the remainder of that calendar year.
                                             mtype = "Dependent 14 and over";
                                         } else {
                                             skip = true;              // skip record if webid not provided
                                             SystemUtils.logErrorToFile("DEPENDENT OVER 26 (" + lname + ", " + fname + ")", club, true);
                                         }
                                     }
                                 }

                                 if (mship.equalsIgnoreCase("Honorary Life") || mship.equalsIgnoreCase("Honorary Regular")  
                                         || mship.equalsIgnoreCase("Management") || mship.equalsIgnoreCase("Regular Golf") || mship.equalsIgnoreCase("Senior Life") 
                                         || mship.equalsIgnoreCase("Professional") || mship.equalsIgnoreCase("Intermediate") || mship.equalsIgnoreCase("Intermediate 1 Family") 
                                         || mship.equalsIgnoreCase("Intermediate 2 Family") || mship.equalsIgnoreCase("Professional Family") 
                                         || ((mship.equalsIgnoreCase("Single") || mship.equalsIgnoreCase("Individual") 
                                         || mship.equalsIgnoreCase("Intermediate 1 Individual") || mship.equalsIgnoreCase("Intermediate 2 Individual") 
                                         || mship.equalsIgnoreCase("Professional Individual")) && primary.equals("0"))) {
                                     mship = "Golf";
                                 } else if (mship.equalsIgnoreCase("Honorary Social") || mship.equalsIgnoreCase("Senior Social") || mship.equalsIgnoreCase("Social") 
                                         || mship.equalsIgnoreCase("Intermediate Social") 
                                         || ((mship.equalsIgnoreCase("Single") || mship.equalsIgnoreCase("Individual") || mship.equalsIgnoreCase("Student Annual") 
                                         || mship.equalsIgnoreCase("Intermediate 1 Individual") || mship.equalsIgnoreCase("Intermediate 2 Individual") 
                                         || mship.equalsIgnoreCase("Professional Individual")) 
                                         && (primary.equals("1") || primary.equals("2") || primary.equals("3") || primary.equals("4")))) {
                                     mship = "Dining";
                                 } else if (mship.equalsIgnoreCase("Social Plus")) {
                                     mship = "Social Plus";
                                 } else if (mship.equalsIgnoreCase("Student Annual") && (primary.equals("0") || primary.equals("5"))) {
                                     mship = "Student";  
                                 } else {
                                     skip = true;              // skip record if webid not provided
                                     SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }                             
                             }
                         }      // end of if blackhawkcc



                         //******************************************************************
                         //   Bel-Air Bay Club - belairbayclub
                         //******************************************************************
                         //
                         if (club.equals("belairbayclub")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 default_activity_id = 9999;

                                 posid = memid;

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;

                                 mship = "Dining";

                                 while (mNum.endsWith("A") || mNum.endsWith("B") || mNum.endsWith("C")  || mNum.endsWith("D") || mNum.endsWith("E") 
                                         || mNum.endsWith("F") || mNum.endsWith("G") || mNum.endsWith("H")  || mNum.endsWith("I") || mNum.endsWith("J") 
                                         || mNum.endsWith("K") || mNum.endsWith("L") || mNum.endsWith("M") || mNum.endsWith("N") || mNum.endsWith("O") 
                                         || mNum.endsWith("P") || mNum.endsWith("Q") || mNum.endsWith("R") || mNum.endsWith("S") || mNum.endsWith("T") 
                                         || mNum.endsWith("U") || mNum.endsWith("V") || mNum.endsWith("W") || mNum.endsWith("X") || mNum.endsWith("Y") 
                                         || mNum.endsWith("Z")) {
                                     mNum = mNum.substring(0, mNum.length() - 1);
                                 }
                                 if (primary.equalsIgnoreCase("2")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype="Dependent Female";
                                     } else {
                                         mtype="Dependent Male";
                                     }

                                 } else if (primary.equalsIgnoreCase("1")) {
                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype="Spouse Male";
                                     } else {
                                         mtype="Spouse Female";
                                     } 
                                 } else {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype="Primary Female";
                                     } else {
                                         mtype="Primary Male";
                                     } 
                                 }


                             }
                         } //end of if belairbayclub    

                         //******************************************************************
                         //   St. Albans - bstalbans
                         //******************************************************************
                         //
                         if (club.equals("stalbans")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;

                                 if (primary.equalsIgnoreCase("1") || primary.equalsIgnoreCase("4")) {

                                     if (mNum.toUpperCase().endsWith("S")) {
                                         mNum = mNum.substring(0, mNum.length() - 1);
                                     }
                                     memid = mNum + "A";

                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Spouse Male";
                                     } else {
                                         mtype = "Spouse Female";
                                     }
                                 } else if (primary.equalsIgnoreCase("0")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Member Female";
                                     } else {
                                         mtype = "Member Male";
                                     }
                                 } else {

                                     if (mNum.toUpperCase().endsWith("C1")) {
                                         mNum = mNum.substring(0, mNum.length() - 2);
                                         memid = mNum + "B";
                                     } else if (mNum.toUpperCase().endsWith("C2")) {
                                         mNum = mNum.substring(0, mNum.length() - 2);
                                         memid = mNum + "C";
                                     } else if (mNum.toUpperCase().endsWith("C3")) {
                                         mNum = mNum.substring(0, mNum.length() - 2);
                                         memid = mNum + "D";
                                     } else if (mNum.toUpperCase().endsWith("C4")) {
                                         mNum = mNum.substring(0, mNum.length() - 2);
                                         memid = mNum + "E";
                                     } else if (mNum.toUpperCase().endsWith("C5")) {
                                         mNum = mNum.substring(0, mNum.length() - 2);
                                         memid = mNum + "F";
                                     } else if (mNum.toUpperCase().endsWith("C6")) {
                                         mNum = mNum.substring(0, mNum.length() - 2);
                                         memid = mNum + "G";
                                     } else if (mNum.toUpperCase().endsWith("C7")) {
                                         mNum = mNum.substring(0, mNum.length() - 2);
                                         memid = mNum + "H";
                                     } else if (mNum.toUpperCase().endsWith("C8")) {
                                         mNum = mNum.substring(0, mNum.length() - 2);
                                         memid = mNum + "I";
                                     } else if (mNum.toUpperCase().endsWith("C9")) {
                                         mNum = mNum.substring(0, mNum.length() - 2);
                                         memid = mNum + "J";
                                     }

                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Junior Female";
                                     } else {
                                         mtype = "Junior Male";
                                     }

                                     lname += "*";
                                 }


    //                             if (mship.equalsIgnoreCase("Board of Directors") && !entity_id.equals("")) {
    //                                 mship=entity_id;
    //                             }
    //                             if (mship.equalsIgnoreCase("Wine Vault") && !entity_id.equals("")) {
    //                                 mship=entity_id;
    //                             }


                                 if (mship.equalsIgnoreCase("Associate Corporate Sponsored BT") || mship.equalsIgnoreCase("Sponsor") || mship.equalsIgnoreCase("Associate Corporate-Sponsored") || mship.equalsIgnoreCase("Associate Corporate Sponsored BT Sponsor")) {
                                     mship = "Associate Corporate Sponsored";
                                 } else if (mship.equalsIgnoreCase("Corporate Partnership EntertainAssociate") || mship.equalsIgnoreCase("Corporate Partnership Entertainment") || mship.equalsIgnoreCase("Corporate Partnership Golf")) {
                                     mship = "Corporate Partnership";
                                 } else if (mship.equalsIgnoreCase("Corporate-Sponsored") || mship.equalsIgnoreCase("Corporate-sponsored BT") || mship.equalsIgnoreCase("Corporate-Sponsored BT Sponsor")) {
                                     mship = "Corporate Sponsored";
                                 } else if (mship.equalsIgnoreCase("Foundation Member") || mship.equalsIgnoreCase("Foundation Sponsor")) {
                                     mship = "Foundation";
                                 } else if (mship.equalsIgnoreCase("Legacy BT Sponsor") || mship.equalsIgnoreCase("Legacy BT") || mship.equalsIgnoreCase("Legacy")) {
                                     mship = "Legacy";
                                 } else if (mship.equalsIgnoreCase("Social (Swim/Tennis)") || mship.equalsIgnoreCase("Social BT") || mship.equalsIgnoreCase("Social Sponsor BT") || mship.equalsIgnoreCase("Social/Dining")) {
                                     mship = "Social";
                                 } else if (mship.equalsIgnoreCase("Special (SAP)") || mship.equalsIgnoreCase("Special") || mship.equalsIgnoreCase("Special Management Empl.")) {
                                     mship = "Special";
                                 } else if (mship.equalsIgnoreCase("Young Executive") || mship.equalsIgnoreCase("Young Executive BT") || mship.equalsIgnoreCase("Young Executive BT Sponsor")) {
                                     mship = "Young Executive";
                                 } else if (mship.equalsIgnoreCase("Non-Resident")) {
                                     mship = "Non Resident";
                                 }

                             }
                         } //end of if St. Albans 

                         //******************************************************************
                         //   Sonnenalp Golf Club - sonnenalp
                         //******************************************************************
                         //
                         if (club.equals("sonnenalp")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {



                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }


                                 if (memid.endsWith("-000")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;

                                 } else if (memid.endsWith("-001")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "A";

                                 } else if (memid.endsWith("-002")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "B";

                                 } else if (memid.endsWith("-003")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "C";

                                 } else if (memid.endsWith("-004")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "D";

                                 } else if (memid.endsWith("-005")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "E";

                                 } else if (memid.endsWith("-006")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "F";

                                 } else if (memid.endsWith("-007")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "G";

                                 } else if (memid.endsWith("-008")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "H";

                                 } else if (memid.endsWith("-009")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "I";

                                 } else if (memid.endsWith("-010")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "J";

                                 } else if (memid.endsWith("-011")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "K";

                                 } else if (memid.endsWith("-012")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "L";

                                 } else if (memid.endsWith("-013")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "M";

                                 } else if (memid.endsWith("-014")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "N";

                                 } else if (memid.endsWith("-015")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "O";

                                 } else if (memid.endsWith("-016")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "P";

                                 } else if (memid.endsWith("-017")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "Q";

                                 } else if (memid.endsWith("-018")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "R";

                                 } else if (memid.endsWith("-019")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "S";

                                 } else if (memid.endsWith("-020")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "T";

                                 }

                                 if (primary.equalsIgnoreCase("2")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Female Child";
                                     } else {
                                         mtype = "Male Child";
                                     }
                                 } else if (primary.equalsIgnoreCase("1")) {
                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Spouse Male";
                                     } else {
                                         mtype = "Spouse Female";
                                     }
                                 } else if (primary.equalsIgnoreCase("0")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         mtype = "Primary Male";
                                     }
                                 }

                                 if (mship.equalsIgnoreCase("CORPORATE") || mship.equalsIgnoreCase("Full Dues Member") || mship.equalsIgnoreCase("Non-Dues Member")) {
                                     mship = "Full Golf";
                                 } else if (mship.equalsIgnoreCase("Social Dues Member")) {
                                     mship = "Social Member";
                                 } else if (mship.equalsIgnoreCase("Sports Dues Member")) {
                                     mship = "Sports Member";
                                 } else if (mship.equalsIgnoreCase("Sports Vertical Non Dues Member")) {
                                     mship = "Sports Vertical Member";
                                 } else if (mship.equalsIgnoreCase("Employee House Acct")) {
                                     skip = true;              // skip record if Employee House Account
                                     SystemUtils.logErrorToFile("UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }
                                 posid = mNum;

                             }
                         } //end of if Sonnenalp Golf Club 

                         //******************************************************************
                         //   La Cumbre Country Club - lacumbrecc
                         //******************************************************************
                         //
                         if (club.equalsIgnoreCase("lacumbrecc")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {


                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }
                                 if (memid.endsWith("-000")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;

                                 } else if (memid.endsWith("-001")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "A";

                                 } else if (memid.endsWith("-002")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "B";

                                 } else if (memid.endsWith("-003")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "C";

                                 } else if (memid.endsWith("-004")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "D";

                                 } else if (memid.endsWith("-005")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "E";

                                 } else if (memid.endsWith("-006")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "F";

                                 } else if (memid.endsWith("-007")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "G";

                                 } else if (memid.endsWith("-008")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "H";

                                 } else if (memid.endsWith("-009")) {
                                     memid = memid.substring(0, memid.length() - 4);
                                     mNum = memid;
                                     memid += "I";

                                 }

                                 if (primary.equalsIgnoreCase("2") || primary.equalsIgnoreCase("4")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Dependant Female";
                                     } else {
                                         mtype = "Dependant Male";
                                     }
                                 } else if (primary.equalsIgnoreCase("1")) {
                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Spouse Male";
                                     } else {
                                         mtype = "Spouse Female";
                                     }
                                 } else if (primary.equalsIgnoreCase("0")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         mtype = "Primary Male";
                                     }
                                 }
                                 if (mship.equalsIgnoreCase("Assoc Junior Dues") || mship.equalsIgnoreCase("Assoc Regular Dues")) {
                                     mship = "Associate Golf";
                                 } else if (mship.equalsIgnoreCase("Non Res Dues")) {
                                     mship = "Non Resident Golf";
                                 } else if (mship.equalsIgnoreCase("Regular Dues") || mship.equalsIgnoreCase("Regular Dues - 2")) {
                                     mship = "Regular Golf";
                                 } else if (mship.equalsIgnoreCase("Social Dues") || mship.equalsIgnoreCase("Social Dues - 2")) {
                                     mship = "Social Clubhouse";
                                 } else if (mship.equalsIgnoreCase("Tennis Dues") || mship.equalsIgnoreCase("Tennis Dues - 2")) {
                                     mship = "Tennis";
                                 } else if (mship.equalsIgnoreCase("Comp.") || mship.equalsIgnoreCase("Other Clubs") || mship.equalsIgnoreCase("Resigned W / Balance") || mship.equalsIgnoreCase("Social Dues")
                                         || mship.equalsIgnoreCase("Social Dues - 2") || mship.equalsIgnoreCase("Tennis Dues") || mship.equalsIgnoreCase("Tennis Dues - 2") || mship.equalsIgnoreCase("0")) {
                                     skip = true;            // skip record if mship one of above
                                     SystemUtils.logErrorToFile("UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }


                             }

                         } //end of if Sonnenalp Golf Club 

                         //******************************************************************
                         //   The Club At Mediterra - mediterra
                         //******************************************************************
                         //
                         if (club.equalsIgnoreCase("mediterra")) {

                             found = true;        // club found
                             String tempPhone = "";

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {
                                 
                                 // phone2 is already set properly
                                 phone = phone4;
                                 custom_string = phone3;

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }
                                 if (memid.endsWith("-A")) {
                                     memid = memid.substring(0, memid.length() - 2);
                                     mNum = memid;
                                     memid += "A";

                                 } else if (memid.endsWith("-B")) {
                                     memid = memid.substring(0, memid.length() - 2);
                                     mNum = memid;
                                     memid += "B";

                                 } else if (memid.endsWith("-C")) {
                                     memid = memid.substring(0, memid.length() - 2);
                                     mNum = memid;
                                     memid += "C";

                                 } else if (memid.endsWith("-D")) {
                                     memid = memid.substring(0, memid.length() - 2);
                                     mNum = memid;
                                     memid += "D";

                                 } else if (memid.endsWith("-E")) {
                                     memid = memid.substring(0, memid.length() - 2);
                                     mNum = memid;
                                     memid += "E";

                                 } else if (memid.endsWith("-F")) {
                                     memid = memid.substring(0, memid.length() - 2);
                                     mNum = memid;
                                     memid += "F";

                                 } else if (memid.endsWith("-G")) {
                                     memid = memid.substring(0, memid.length() - 2);
                                     mNum = memid;
                                     memid += "G";

                                 } else if (memid.endsWith("-H")) {
                                     memid = memid.substring(0, memid.length() - 2);
                                     mNum = memid;
                                     memid += "H";

                                 } else if (memid.endsWith("-I")) {
                                     memid = memid.substring(0, memid.length() - 2);
                                     mNum = memid;
                                     memid += "I";

                                 } else {
                                     mNum = memid;
                                 }
                                 
                                 if (mship.equalsIgnoreCase("Builders") || mship.equalsIgnoreCase("Golf Pending Orientation") || mship.equalsIgnoreCase("Rennaissance") 
                                         || mship.equalsIgnoreCase("Social Pending Orientation") || mship.equalsIgnoreCase("Sports Pending Orientation")) {
                                     skip = true;            // skip record if mship one of above
                                     SystemUtils.logErrorToFile("NON-GOLF MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }

                                 if (primary.equalsIgnoreCase("2") || primary.equalsIgnoreCase("5")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Dependant Female";
                                     } else {
                                         mtype = "Dependant Male";
                                     }
                                 } else if (primary.equalsIgnoreCase("1")) {
                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Spouse Male";
                                     } else {
                                         mtype = "Spouse Female";
                                     }
                                 } else if (primary.equalsIgnoreCase("0")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         mtype = "Primary Male";
                                     }
                                 }
                             }
                         } //end of if mediterra 

                         //******************************************************************
                         //   Meadow Springs Country Club  - meadowsprings
                         //******************************************************************
                         //
                         if (club.equalsIgnoreCase("meadowsprings")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;
                                 mNum = memid;

                                 if (mship.equalsIgnoreCase("Employees")) {
                                     mship = "Employee";
                                 }

                                 if (primary.equalsIgnoreCase("2")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Junior Female";
                                     } else {
                                         mtype = "Junior Male";
                                     }

                                 } else if (primary.equalsIgnoreCase("0") || primary.equalsIgnoreCase("1") ||  primary.equalsIgnoreCase("4")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Adult Female";
                                     } else {
                                         mtype = "Adult Male";
                                     }
                                 }


                             }

                         } //end of if meadowsprings 


                         //******************************************************************
                         //   Waialae Country Club - waialae
                         //******************************************************************
                         //
                         if (club.equalsIgnoreCase("waialae")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;
                                 
                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid.substring(0, memid.length() - 4);

                                 custom_string = mship;
                                 custom_string2 = custom_data;

                                 fname = toTitleCase(fname);
                                 lname = toTitleCase(lname);

                                 // Determine Spouse Golf/Non Golf based on a different code than the main mship mappings
                                 if (custom_string2.equalsIgnoreCase("SP")) {
                                     mship = "Spouse/Dependent Non Golf";
                                 } else if (custom_string2.equalsIgnoreCase("SP-G")) {
                                     mship = "Spouse/Dependent Golf";
                                 }

                                 // Use mship data for rest of members
                                 if (mship.equalsIgnoreCase("BS") || mship.equalsIgnoreCase("WB") || mship.equalsIgnoreCase("XB") 
                                         || mship.equalsIgnoreCase("EB") || mship.equalsIgnoreCase("DB") || mship.equalsIgnoreCase("OB") 
                                         || mship.equalsIgnoreCase("NB") || mship.equalsIgnoreCase("AB")) {
                                     mship = "Beachside";
                                 } else if (mship.equalsIgnoreCase("XL")) {
                                     mship = "Ex Res Limited Golf";
                                 } else if (mship.equalsIgnoreCase("XO") || mship.equalsIgnoreCase("XT") || mship.equalsIgnoreCase("ES")) {
                                     mship = "Ex Res Non Golf";
                                 } else if (mship.equalsIgnoreCase("XR") || mship.equalsIgnoreCase("XS") || mship.equalsIgnoreCase("EGS")) {
                                     mship = "Ex Res Regular Golf";
                                 } else if (mship.equalsIgnoreCase("EG")) {
                                     mship = "Ex Res Widow Golf";
                                 } else if (mship.equalsIgnoreCase("NL") || mship.equalsIgnoreCase("LS")) {
                                     mship = "Non Res Limited Golf";
                                 } else if (mship.equalsIgnoreCase("IG") || mship.equalsIgnoreCase("NS") || mship.equalsIgnoreCase("OS")) {
                                     mship = "Non Res Non Golf";
                                 } else if (mship.equalsIgnoreCase("NR") || mship.equalsIgnoreCase("NG")) {
                                     mship = "Non Res Regular Golf";
                                 } else if (mship.equalsIgnoreCase("OG")) {
                                     mship = "Non Res Widow Golf";
                                 } else if (mship.equalsIgnoreCase("WS") || mship.equalsIgnoreCase("WT")) {
                                     mship = "Res Non Golf Widow";
                                 } else if (mship.equalsIgnoreCase("AL") || mship.equalsIgnoreCase("LG")) {
                                     mship = "Resident Limited Golf";
                                 } else if (mship.equalsIgnoreCase("AR") || mship.equalsIgnoreCase("GS") || mship.equalsIgnoreCase("RG") || mship.equalsIgnoreCase("SR")) {
                                     mship = "Resident Regular Golf";
                                 } else if (mship.equalsIgnoreCase("GSW") || mship.equalsIgnoreCase("WG")) {
                                     mship = "Resident Widow Golf";
                                 } else if (mship.equalsIgnoreCase("SL")) {
                                     mship = "Senior Limited Golf";
                                 } else if (mship.equalsIgnoreCase("SO")) {
                                     mship = "Social";
                                 } else if (mship.equalsIgnoreCase("LL")) {
                                     mship = "Special Limited Golf";
                                 } else if (mship.equalsIgnoreCase("HO") || mship.equalsIgnoreCase("HT")) {
                                     mship = "Special Resident Golf";
                                 } else if (mship.equalsIgnoreCase("DG")) {
                                     mship = "Spouse/Dependent Golf";
                                 } else if (mship.equalsIgnoreCase("DT")) {
                                     mship = "Spouse/Dependent Non Golf";
                                 } else if (mship.equalsIgnoreCase("TN")) {
                                     mship = "Tennis";
                                 }

                                 if (primary.equals("0")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         mtype = "Primary Male";
                                     }
                                 } else if (primary.equals("1")) {
                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Spouse Male";
                                     } else {
                                         mtype = "Spouse Female";
                                     }
                                 } else {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Dependent Female";
                                     } else {
                                         mtype = "Dependent Male";
                                     }
                                 }

                             }

                         } //end of if waialae


                         //******************************************************************
                         //   Bald Peak Colony Club - baldpeak
                         //******************************************************************
                         //
                         if (club.equalsIgnoreCase("baldpeak")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;
                                 mNum =  memid;    
                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 fname = toTitleCase(fname);
                                 lname = toTitleCase(lname);

                                 if (mship.equalsIgnoreCase("House") || mship.equalsIgnoreCase("House W SS Golf Provilieges") || mship.equalsIgnoreCase("House W/SS Golf Privileges")) {
                                     mship = "House Members";
                                 } else if (mship.equalsIgnoreCase("Regular") || mship.equalsIgnoreCase("Regular (Single)") || mship.equalsIgnoreCase("Regular 36-40")
                                         || mship.equalsIgnoreCase("Regular 41-45") || mship.equalsIgnoreCase("Regular Under 36")) {
                                     mship = "Regular Members";
                                 } else if (mship.equalsIgnoreCase("Senior (Single)") || mship.equalsIgnoreCase("Senior")) {
                                     mship = "Senior";
                                 } else if (mship.equalsIgnoreCase("Tennis")) {
                                     mship = "Tennis";
                                 } else if (mship.equalsIgnoreCase("Senior Inactive")) {
                                     mship = "Social"; 
                                 } else if (mship.equalsIgnoreCase("Guest Account")) {
                                     mship = "Club Guest"; 
                                 } else if (mship.equalsIgnoreCase("Reciprocal")) {
                                     mship = "Reciprocal"; 
                                 } else if (mship.equalsIgnoreCase("Seasonal") || mship.equalsIgnoreCase("Seasonal Members")) {
                                     mship = "Seasonal Member"; 
                                 } else if (mship.equalsIgnoreCase("Gift Certificate") || mship.equalsIgnoreCase("Golf Package") || mship.equalsIgnoreCase("No Chg Seas Member") || mship.equalsIgnoreCase("Over 10 Rounds Mbr")) {
                                     skip = true;            // skip record if mship one of above
                                     SystemUtils.logErrorToFile("UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 } else {
                                     skip = true;            // skip record
                                     SystemUtils.logErrorToFile("UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }
                                 

                                 if (primary.equals("0")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         mtype = "Primary Male";
                                     }
                                 } else if (primary.equals("1")) {
                                     memid += "A"; 
                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Spouse Male";
                                     } else {
                                         mtype = "Spouse Female";
                                     }
                                 } else {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Dependent Female";
                                     } else {
                                         mtype = "Dependent Male";
                                     }
                                 }

                             }

                         } //end of if baldpeak    


                        //******************************************************************
                        //   Colorado Springs CC - coloradospringscountryclub
                        //******************************************************************
                        //
                        if (club.equals("coloradospringscountryclub")) {

                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {

                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (flexid.equals("")) {

                                skip = true;              // skip record if flexid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else {

                                while (memid.startsWith("0")) {
                                    memid = memid.substring(1);
                                }

                                mNum = memid;

                                posid = mNum;

                                while (posid.length() < 8) {
                                    posid = "0" + posid;
                                }

                                primary = mNum.substring(mNum.length() - 1);

                                if (mNum.endsWith("-000") || mNum.endsWith("-001")) {

                                    if (gender.equalsIgnoreCase("F")) {
                                        mtype = "Adult Female";
                                    } else {
                                        mtype = "Adult Male";
                                    }

                                } else if (mNum.endsWith("-002") || mNum.endsWith("-003") || mNum.endsWith("-004") || mNum.endsWith("-005")
                                        || mNum.endsWith("-006") || mNum.endsWith("-007") || mNum.endsWith("-008") || mNum.endsWith("-009")) {

                                    mtype = "Youth";
                                }
                                
                                if (mship.equalsIgnoreCase("Corp Jr Executive II")) {
                                    mship = "Corporate Junior Executive";
                                } else if (mship.equalsIgnoreCase("Recreational") || mship.equalsIgnoreCase("Clubhouse")) {

                                    skip = true;              // skip record if webid not provided
                                    SystemUtils.logErrorToFile("NON-GOLF MEMBERSHIP TYPE - SKIPPED", club, true);
                                }

                            }
                        }      // end of if coloradospringscountryclub       
                        


                        //******************************************************************
                        //   Windstar Club - windstarclub
                        //******************************************************************
                        //
                        if (club.equals("windstarclub")) {

                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {

                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (flexid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (memid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else {

                                posid = memid;

                                while (memid.startsWith("0")) {
                                    memid = memid.substring(1);
                                }

                                mNum = memid;

                                //
                                //  Convert the mship
                                //
                                if (mship.equalsIgnoreCase("Golf Equity") || mship.equalsIgnoreCase("Golf Non Resident") || mship.equalsIgnoreCase("Golf Non-Equity Member")
                                        || mship.equalsIgnoreCase("Golf Resident Zero Buy In") || mship.equalsIgnoreCase("Golf - $1.00 Equity (Resident)")
                                        || mship.equalsIgnoreCase("Junior Golf") || mship.equalsIgnoreCase("Contemporary 35") || mship.equalsIgnoreCase("Contemporary 45")
                                        || mship.equalsIgnoreCase("Contemporary 55")) {

                                    mship = "Full Golf";

                                } else if (mship.startsWith("Golf Socia")) {

                                    mship = "Social Golf";

                                } else if (mship.equalsIgnoreCase("Executive Golf") || mship.equalsIgnoreCase("Junior Executive Golf")) {

                                    mship = "Executive Golf";

                                } else if (!mship.equalsIgnoreCase("Tenant Trans Golf") && !mship.equalsIgnoreCase("Tenant Golf")) {

                                    skip = true;              // skip record if non-golf or unknown mship
                                    SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                }

                                if (primary.equals("0") || primary.equals("1")) {

                                    if (primary.equals("0")) {

                                        if (gender.equals("")) {
                                            gender = "M";
                                        }

                                    } else if (primary.equals("1")) {

                                        memid += "A";

                                        if (gender.equals("")) {
                                            gender = "F";
                                        }
                                    }

                                    if (gender.equalsIgnoreCase("F")) {
                                        mtype = "Adult Female";
                                    } else {
                                        mtype = "Adult Male";
                                    }

                                } else {

                                    mtype = "Dependent";

                                    if (primary.equals("2")) {
                                        memid += "B";
                                    } else if (primary.equals("3")) {
                                        memid += "C";
                                    } else if (primary.equals("4")) {
                                        memid += "D";
                                    } else if (primary.equals("5")) {
                                        memid += "E";
                                    } else if (primary.equals("6")) {
                                        memid += "F";
                                    } else if (primary.equals("7")) {
                                        memid += "G";
                                    } else if (primary.equals("8")) {
                                        memid += "H";
                                    } else if (primary.equals("9")) {
                                        memid += "I";
                                    }
                                }

                                lname = toTitleCase(lname);

                            }
                        }      // end of if windstarclub
                        
                        //******************************************************************
                        //   Des Moines Golf & Country Club - dmgcc
                        //******************************************************************
                        //
                        if (club.equals("dmgcc")) {

                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {

                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (flexid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (memid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else {

                                posid = memid;

                                while (memid.startsWith("0")) {
                                    memid = memid.substring(1);
                                }

                                if (memid.endsWith("-000")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    mNum = memid;
                                } else if (memid.endsWith("-001")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    mNum = memid;
                                    memid = memid + "A";
                                } else if (memid.endsWith("-002")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    mNum = memid;
                                    memid = memid + "B";
                                } else if (memid.endsWith("-003")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    mNum = memid;
                                    memid = memid + "C";
                                } else if (memid.endsWith("-004")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    mNum = memid;
                                    memid = memid + "D";
                                } else if (memid.endsWith("-005")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    mNum = memid;
                                    memid = memid + "E";
                                } else if (memid.endsWith("-006")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    mNum = memid;
                                    memid = memid + "F";
                                } else if (memid.endsWith("-007")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    mNum = memid;
                                    memid = memid + "G";
                                } else if (memid.endsWith("-008")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    mNum = memid;
                                    memid = memid + "H";
                                } else if (memid.endsWith("-009")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    mNum = memid;
                                    memid = memid + "I";
                                }
                                
                                if (mship.equalsIgnoreCase("Employees")) {
                                    mship = "Employees";
                                } else if (mship.equalsIgnoreCase("Non-Resident")) {
                                    mship = "Non-Resident";
                                } else if (mship.equalsIgnoreCase("Annual Regular") || mship.equalsIgnoreCase("Intermediate") || mship.equalsIgnoreCase("Junior") || mship.equalsIgnoreCase("Regular") 
                                        || mship.equalsIgnoreCase("Remarried SS - Senior") || mship.equalsIgnoreCase("Remarried Surviving Spouse - Regular") || mship.equalsIgnoreCase("Senior Other") || mship.equalsIgnoreCase("SS - Non Matching")
                                        || mship.equalsIgnoreCase("Senior Spouse") || mship.equalsIgnoreCase("Senior Spouse Regular - After 1/1/79") || mship.equalsIgnoreCase("Spouse - Matching") || mship.equalsIgnoreCase("Sr. Social/Regular - After 1/1/79") 
                                        || mship.equalsIgnoreCase("Sr. Reg - After 1/1/79") || mship.equalsIgnoreCase("Sr. Social/Regular") || mship.equalsIgnoreCase("Senior Regular") || mship.equalsIgnoreCase("Life") 
                                        || mship.equalsIgnoreCase("Clergy") || mship.equalsIgnoreCase("Disability") || mship.equalsIgnoreCase("Widow")) {
                                    mship = "Regular Golf";
                                } else if (mship.equalsIgnoreCase("Junior Social") || mship.equalsIgnoreCase("Senior Social") || mship.equalsIgnoreCase("Social") || mship.equalsIgnoreCase("Sr. Social - After 1/1/79") 
                                        || mship.equalsIgnoreCase("Annual Social") || mship.equalsIgnoreCase("Remarried Surviving Spouse - Social") || mship.equalsIgnoreCase("Social Wait List")){
                                    mship = "Social";
                                } else if (mship.equalsIgnoreCase("Social on Golf Wait List - Over 30") || mship.equalsIgnoreCase("Social on Golf Wait List - Under 30")) {
                                    mship = "Golf Wait List";
                                } else if (mship.equalsIgnoreCase("Other Clubs") || mship.equalsIgnoreCase("House") || mship.equalsIgnoreCase("Leave of Absence Golf") || mship.equalsIgnoreCase("Leave of Absence Social")) {
                                    mship = "Other Clubs";
                                } else {
                                     skip = true;           
                                     SystemUtils.logErrorToFile("UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                }


                                if (primary.equals("0")) {
                                   if (gender.equalsIgnoreCase("F")) {
                                       mtype = "Primary Female";
                                   } else {
                                       gender = "M";
                                       mtype = "Primary Male";                                   
                                   }
                                } else if (primary.equals("1")) {
                                    if (gender.equalsIgnoreCase("M")) {
                                        mtype = "Spouse Male";
                                    } else {
                                        gender = "F";
                                        mtype = "Spouse Female";                                     
                                    }
                                } else {
                                    mtype = "Dependent";
                                }

                                lname = toTitleCase(lname);

                                }
                        }      // end of if dmgcc
                        
                        //******************************************************************
                        //   Martis Camp Club - martiscamp
                        //******************************************************************
                        //
                        if (club.equals("martiscamp")) {

                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {

                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (flexid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (memid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else {

                                posid = memid;

                                while (memid.startsWith("0")) {
                                    memid = memid.substring(1);
                                }

                                if (primary.equals("0")) {
                                    if (gender.equalsIgnoreCase("F")) {
                                        mtype = "Primary Female";
                                    } else {
                                        mtype = "Primary Male";
                                    }
                                    mNum = memid;
                                } else if (primary.equals("1")) {
                                    if (gender.equalsIgnoreCase("M")) {
                                        mtype = "Spouse Male";
                                    } else {
                                        mtype = "Spouse Female";
                                    }
                                    mNum = memid.substring(0, memid.length() - 1);
                                } else {

                                    mtype = "Dependent";
                                    mNum = memid.substring(0, memid.length() - 1);
                                }

                                //lname = toTitleCase(lname);
                                if (mship.equalsIgnoreCase("Golf") || mship.equalsIgnoreCase("Heritage Founder") || mship.equalsIgnoreCase("Honorary") || 
                                        mship.equalsIgnoreCase("Invitational Golf") || mship.equalsIgnoreCase("Legacy Founder") || mship.equalsIgnoreCase("Partners")) {
                                    mship = "Golf";
                                } else if (mship.equalsIgnoreCase("Social") || mship.equalsIgnoreCase("Social Founder")) {
                                    mship = "Social"; 
                                } else if (mship.equalsIgnoreCase("Extended Family")) {
                                    mship = "Extended Family";
                                } else {
                                    skip = true;
                                    SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);                                
                                }
                            }
                        }      // end of if martiscamp
                        //******************************************************************
                        //   Blue Hills Country Club - bluehillscc
                        //******************************************************************
                        //
                        if (club.equals("bluehillscc")) {

                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {

                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (flexid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (memid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else {
                                
                                
                                while (memid.startsWith("0")) {
                                    memid = memid.substring(1);
                                }                                    

                                posid = memid;
                                mNum = memid;


                                 while (mNum.endsWith("A") || mNum.endsWith("B") || mNum.endsWith("C")  || mNum.endsWith("D") || mNum.endsWith("E") 
                                         || mNum.endsWith("F") || mNum.endsWith("G") || mNum.endsWith("H")  || mNum.endsWith("I") || mNum.endsWith("J") 
                                         || mNum.endsWith("K") || mNum.endsWith("L") || mNum.endsWith("M") || mNum.endsWith("N") || mNum.endsWith("O") 
                                         || mNum.endsWith("P") || mNum.endsWith("Q") || mNum.endsWith("R") || mNum.endsWith("S") || mNum.endsWith("T") 
                                         || mNum.endsWith("U") || mNum.endsWith("V") || mNum.endsWith("W") || mNum.endsWith("X") || mNum.endsWith("Y") 
                                         || mNum.endsWith("Z")) {
                                     mNum = mNum.substring(0, mNum.length() - 1);
                                 }



                                if (primary.equals("0")) {
                                    if (gender.equalsIgnoreCase("F")) {
                                        mtype = "Primary Female";
                                    } else {
                                        mtype = "Primary Male";
                                    }
                                    mNum = memid;
                                } else if (primary.equals("1")) {
                                    if (gender.equalsIgnoreCase("M")) {
                                        mtype = "Spouse Male";
                                    } else {
                                        mtype = "Spouse Female";
                                    }
                                    mNum = memid.substring(0, memid.length() - 1);
                                } else {

                                    mtype = "Dependent";
                                    mNum = memid.substring(0, memid.length() - 1);
                                }
                                if (mship.equalsIgnoreCase("Ad Tennis")) {
                                    mship = "Ad Tennis";
                                } else if (mship.equalsIgnoreCase("Century")) {
                                    mship = "Century";
                                } else if (mship.equalsIgnoreCase("Emeritus")) {
                                    mship = "Emeritus";
                                } else if (mship.equalsIgnoreCase("Foundation")) {
                                    mship = "Foundation";
                                } else if (mship.equalsIgnoreCase("Intermediate")) {
                                    mship = "Intermediate";
                                } else if (mship.equalsIgnoreCase("Junior Tennis")) {
                                    mship = "Junior Tennis";
                                } else if (mship.equalsIgnoreCase("Managers")) {
                                    mship = "Managers";
                                } else if (mship.equalsIgnoreCase("Non Resident")) {
                                    mship = "Non Resident";
                                } else if (mship.equalsIgnoreCase("Senior Foundation")) {
                                    mship = "Foundation";
                                } else if (mship.equalsIgnoreCase("Social")) {
                                    mship = "Social";
                                } else if (mship.equalsIgnoreCase("Special Spouse (Widow)")) {
                                    mship = "SP Spouce";
                                } else if (mship.equalsIgnoreCase("Dining")) {
                                    mship = "Dining";
                                }else if (mship.equalsIgnoreCase("Spouse")) {
                                     
                                    PreparedStatement pstmtx = null;
                                     ResultSet rsx = null;

                                     try {

                                         pstmtx = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                                         pstmtx.clearParameters();
                                         pstmtx.setString(1, mNum);

                                         rsx = pstmtx.executeQuery();

                                         if (rsx.next()) {
                                             mship = rsx.getString("m_ship");
                                         } else {
                                             skip = true;
                                             SystemUtils.logErrorToFile("PRIMARY NOT FOUND(" + mNum + ")"+ lname + ", " + fname  , club, true);
                                         }

                                     } catch (Exception e) {
                                         Utilities.logError("Common_sync_premier.flexSync - " + club + " - failed looking up primary mship - Error = " + e.toString());
                                     } finally {
                                         Connect.close(rsx, pstmtx);
                                     }
                                }


                                lname = toTitleCase(lname);
                                fname = toTitleCase(fname);

                            }
                        }      // end of if bluehillscc
                        //******************************************************************
                        //   Glen Oaks Country Club - glenoaks
                        //******************************************************************
                        //
                        if (club.equals("glenoaks")) {

                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {

                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (flexid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (memid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else {
                                
                                
                                while (memid.startsWith("0")) {
                                    memid = memid.substring(1);
                                }                                    

                                posid = memid;
                                mNum = memid;


                                 while (mNum.endsWith("A") || mNum.endsWith("B") || mNum.endsWith("C")  || mNum.endsWith("D") || mNum.endsWith("E") 
                                         || mNum.endsWith("F") || mNum.endsWith("G") || mNum.endsWith("H")  || mNum.endsWith("I") || mNum.endsWith("J") 
                                         || mNum.endsWith("K") || mNum.endsWith("L") || mNum.endsWith("M") || mNum.endsWith("N") || mNum.endsWith("O") 
                                         || mNum.endsWith("P") || mNum.endsWith("Q") || mNum.endsWith("R") || mNum.endsWith("S") || mNum.endsWith("T") 
                                         || mNum.endsWith("U") || mNum.endsWith("V") || mNum.endsWith("W") || mNum.endsWith("X") || mNum.endsWith("Y") 
                                         || mNum.endsWith("Z")) {
                                     mNum = mNum.substring(0, mNum.length() - 1);
                                 }



                                if (primary.equals("0")) {
                                    String tempmNum = "";
                                    tempmNum = mNum.substring(0,1);
                                    tempmNum += mNum.substring(2);
                                    mNum = tempmNum;                    //remove extra 0 at start of mNum so it matches with family
                                    
                                    if (gender.equalsIgnoreCase("F")) {
                                        mtype = "Adult Female";
                                    } else {
                                        mtype = "Adult Male";
                                    }

                                } else if (primary.equals("1")) {
                                    if (gender.equalsIgnoreCase("M")) {
                                        mtype = "Adult Male";
                                    } else {
                                        mtype = "Adult Female";
                                    }

                                } else {
                                    mtype = "Junior";
                                } 

                                if (mship.equalsIgnoreCase("Golf") || mship.equalsIgnoreCase("Jr Golf")) {
                                    mship = "Golf";
                                } else if (mship.equalsIgnoreCase("Employees")) {
                                    mship = "Employees"; 
                                } else if (mship.equalsIgnoreCase("Social")) {
                                    mship = "Social";
                                } else if (mship.equalsIgnoreCase("Sports")) {
                                    mship = "Sports";
                                } else {
                                    skip = true;
                                    SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);                                
                                }                               


                                lname = toTitleCase(lname);
                                fname = toTitleCase(fname);

                            }
                        }      // end of if bluehillscc


                         //******************************************************************
                         //   CC of North Carolina - ccofnc
                         //******************************************************************
                         //
                         if (club.equals("ccofnc")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;

                                 if (mNum.endsWith("A")) {
                                     mNum = mNum.substring(0, mNum.length() - 1);
                                 }
                                 
                                 if (primary.equals("0") || primary.equals("1")) {

                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         gender = "M";
                                         mtype = "Primary Male";
                                     }
                                 } else {
                                     mtype = "Dependent";
                                 }

                                 if (mship.equalsIgnoreCase("Employee")) {
                                     mship = "Employee";
                                 } else if (mship.equalsIgnoreCase("Immediate Family")) {
                                     mship = "Immediate Family";
                                 } else if (mship.equalsIgnoreCase("Junior - Spouse")) {
                                     mship = "Junior";
                                 } else if (mship.equalsIgnoreCase("Juniors")) {
                                     mship = "Juniors";
                                 } else if (mship.equalsIgnoreCase("Moore County") || mship.equalsIgnoreCase("Moore County - Spouse")) {
                                     mship = "Moore County";
                                 } else if (mship.startsWith("National")) {
                                     mship = "National";
                                 } else if (mship.equalsIgnoreCase("North Carolina") || mship.equalsIgnoreCase("North Carolina - Spouse")) {
                                     mship = "North Carolina";
                                 } else if (mship.startsWith("Patriot Associate")) {
                                     mship = "Patriot Associate";
                                 } else if (mship.startsWith("Prospective Member-Mbr Dir") || mship.equalsIgnoreCase("Spouse Prospective Member-Mbr Dir")) {
                                     mship = "Prospective Member";
                                 } else if (mship.equalsIgnoreCase("Resident") || mship.equalsIgnoreCase("Resident - Spouse")) {
                                     mship = "Resident";
                                 } else if (mship.equalsIgnoreCase("Moore County - Non Golfing") || mship.equalsIgnoreCase("North Carolina - Non Golfing") 
                                         || mship.equalsIgnoreCase("Resident - Non Golfing") || mship.startsWith("Social") || mship.startsWith("Spouse")) {
                                     mship = "Social";
                                 } else {
                                     skip = true;
                                     SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }                     
                             }
                         }      // end of if ccofnc
                         //******************************************************************
                         //   The Club At Old Hawthorne - oldhawthorne
                         //******************************************************************
//                        
                         if (club.equals("oldhawthorne")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;
                                 mNum = mNum.substring(0, memid.length() - 4);

                                 
                                if (primary.equals("0")) {
                                   if (gender.equalsIgnoreCase("F")) {
                                       mtype = "Primary Female";
                                   } else {
                                       gender = "M";
                                       mtype = "Primary Male";                                   
                                   }

                                } else if (primary.equals("1")) {
                                    if (gender.equalsIgnoreCase("M")) {
                                        mtype = "Spouse Male";
                                    } else {
                                        gender = "F";
                                        mtype = "Spouse Female";                                     
                                    }
                                    
                                } else {

                                    mtype = "Dependent";
                                }
                                if (memid.endsWith("-000")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                } else if (memid.endsWith("-001")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    memid +="A";
                                } else if (memid.endsWith("-002")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    memid +="B";
                                } else if (memid.endsWith("-003")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    memid +="C";
                                } else if (memid.endsWith("-004")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    memid +="D";
                                } else if (memid.endsWith("-005")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    memid +="E";
                                } else if (memid.endsWith("-006")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    memid +="F";
                                } else if (memid.endsWith("-007")) {
                                    memid = memid.substring(0, memid.length() - 4);
                                    memid +="G";
                                }
                                
                                 if (mship.equalsIgnoreCase("Athletic NRes/Fam") || mship.equalsIgnoreCase("Athletic NRes/Single") || mship.equalsIgnoreCase("Athletic Res/Fam") || mship.equalsIgnoreCase("Athletic Res/Single")) {
                                     mship = "Athletic"; 
                                 } else if (mship.equalsIgnoreCase("Comp") || mship.equalsIgnoreCase("Employees") || mship.equalsIgnoreCase("Honorary") || mship.equalsIgnoreCase("Owners") || mship.equalsIgnoreCase("Trade Out")) {
                                     mship = "Honorary"; 
                                 } else if (mship.equalsIgnoreCase("Non Resident Local Golf") || mship.equalsIgnoreCase("Non Res/Single Golf") || mship.equalsIgnoreCase("Local Junior Family") 
                                         || mship.equalsIgnoreCase("Local Family NT") || mship.equalsIgnoreCase("Legacy 1") || mship.equalsIgnoreCase("Local Single") || mship.equalsIgnoreCase("Local Family")) {
                                     mship = "Local Golf";
                                 } else if (mship.equalsIgnoreCase("Social Resident") || mship.equalsIgnoreCase("Social Non-Resident")) {
                                     mship = "Social";
                                 } else if (mship.equalsIgnoreCase("Regional Family")) {
                                     mship = "Regional Family";
                                 } else if (mship.equalsIgnoreCase("Regional Single")) {
                                     mship = "Regional Single";
                                 } else if (mship.equalsIgnoreCase("Student")) {
                                     mship = "Student";
                                 } else if (mship.equalsIgnoreCase("National Family")) {
                                     mship = "National Family";
                                 } else if (mship.equalsIgnoreCase("National Single")) {
                                     mship = "National Single";
                                 } else if (mship.equalsIgnoreCase("Local Junior Single")) {
                                     mship = "Local Single";
                                 } else if (mship.equalsIgnoreCase("Local Junior Family")) {
                                     mship = "Local Family";
                                 } else if (mship.equalsIgnoreCase("Corporate Local Single")) {
                                     mship = "Corporate-Local-Single";
                                 }

                    
                             }
                         }      // end of if oldhawthorne
                         //******************************************************************
                         //   Manufacturers Golf & Country Club - mg-cc
                         //******************************************************************
                         //
                         if (club.equalsIgnoreCase("mg-cc")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;
                                 mNum =  memid;    
                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 fname = toTitleCase(fname);
                                 lname = toTitleCase(lname);
                                
                                 if (primary.equals("0")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         mtype = "Primary Male";
                                     }
                                 } else if (primary.equals("1")) {
                                     memid += "A"; 
                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Spouse Male";
                                     } else {
                                         mtype = "Spouse Female";
                                     }
                                 } else {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Dependent Female";
                                     } else {
                                         mtype = "Dependent Male";
                                     }
                                 }
                                 if (mNum.endsWith("A") || mNum.endsWith("B") || mNum.endsWith("C") || mNum.endsWith("D") || mNum.endsWith("E") || mNum.endsWith("F") || mNum.endsWith("G") || mNum.endsWith("H") || mNum.endsWith("I")) {
                                     mNum = mNum.substring(0,mNum.length()-1);
                                 }

                             }

                         } //end of if mg-cc 
                        
                        //******************************************************************
                        //   The Legend Clubs (B B MH)- thelegendclubs
                        //******************************************************************
//                        
                        if (club.equals("thelegendclubs")) {

                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {

                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (flexid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (memid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else {
                                
                                
                                while (memid.startsWith("0")) {
                                    memid = memid.substring(1);
                                }                                    

                                posid = memid;
                                mNum = memid;

                                 while (mNum.endsWith("A") || mNum.endsWith("B") || mNum.endsWith("C")  || mNum.endsWith("D") || mNum.endsWith("E") 
                                         || mNum.endsWith("F") || mNum.endsWith("G") || mNum.endsWith("H")  || mNum.endsWith("I") || mNum.endsWith("J") 
                                         || mNum.endsWith("K") || mNum.endsWith("L") || mNum.endsWith("M") || mNum.endsWith("N") || mNum.endsWith("O") 
                                         || mNum.endsWith("P") || mNum.endsWith("Q") || mNum.endsWith("R") || mNum.endsWith("S") || mNum.endsWith("T") 
                                         || mNum.endsWith("U") || mNum.endsWith("V") || mNum.endsWith("W") || mNum.endsWith("X") || mNum.endsWith("Y") 
                                         || mNum.endsWith("Z")) {
                                     mNum = mNum.substring(0, mNum.length() - 1);
                                 }
                                 
                                 mtype = custom_data;                              

                            }
                        }      // end of if thelegendclubs
//                        
                         //******************************************************************
                         //   Gallery Golf Club - gallerygolf
                         //******************************************************************
                         //
                         if (club.equals("gallerygolf")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;

                                 if (mNum.toUpperCase().endsWith("A") || mNum.toUpperCase().endsWith("B") || mNum.toUpperCase().endsWith("C") 
                                         || mNum.toUpperCase().endsWith("D") || mNum.toUpperCase().endsWith("E") || mNum.toUpperCase().endsWith("F")) {
                                     mNum = mNum.substring(0, mNum.length() - 1);
                                 }
                                 
                                 if (primary.equals("0") || primary.equals("1")) {

                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Adult Female";
                                     } else {
                                         gender = "M";
                                         mtype = "Adult Male";
                                     }
                                 } else {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Child Female";
                                     } else {
                                         gender = "M";
                                         mtype = "Child Male";
                                     }
                                 }
                                 
                                 if (mship.equalsIgnoreCase("Corporate") || mship.equalsIgnoreCase("Founding") || mship.equalsIgnoreCase("Golf")
                                         || mship.equalsIgnoreCase("Golf - SR") || mship.equalsIgnoreCase("Golf-nref") || mship.equalsIgnoreCase("Honorary")
                                         || mship.equalsIgnoreCase("Invitation") || mship.equalsIgnoreCase("Jr Founding") || mship.equalsIgnoreCase("Junior 45") 
                                         || mship.equalsIgnoreCase("Junior") || mship.equalsIgnoreCase("Senior Golf")) {
                                     mship = "Regular Golf";
                                 
                                 } else if (mship.equalsIgnoreCase("No Charge") || mship.equalsIgnoreCase("Pulte") || mship.equalsIgnoreCase("Pulte ACH")
                                         || mship.equalsIgnoreCase("SP Fitness") || mship.equalsIgnoreCase("Sports") || mship.equalsIgnoreCase("Tennis")) {
                                     mship = "Sports/Fitness";
                                 
                                 } else if (mship.equalsIgnoreCase("Golf-trial") || mship.equalsIgnoreCase("Trial Golf")) {
                                     mship = "Trial Golf";
                                 
                                 } else if (mship.equalsIgnoreCase("Staff")) {
                                     mship = "Manager";
                                 
                                 } else if (mship.equalsIgnoreCase("Sports Plus")) {
                                     mship = "Sports Plus Golf";
                                 
                                 } else {
                                     skip = true;
                                     SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }

                    
                             }
                         }      // end of if gallerygolf     
                         
                        //******************************************************************
                        //   Patterson Club
                        //******************************************************************
                        //
                        if (club.equals("pattersonclub")) {

                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {

                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (flexid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (fname.equalsIgnoreCase("Survey") || fname.equalsIgnoreCase("Admin")) {
                                
                                skip = true;
                                SystemUtils.logErrorToFile("SKIPPED: 'Survey' or 'Admin' FIRST NAME! name: " + lname + ", " + fname + " - " + work, club, true);
                                
                            } else {

                                posid = memid;

                                while (memid.startsWith("0")) {
                                    memid = memid.substring(1);
                                }

                                mNum = memid;
                                
                                fname = toTitleCase(fname);
                                lname = toTitleCase(lname);

                                if (mNum.endsWith("A") || mNum.endsWith("B") || mNum.endsWith("C") || mNum.endsWith("D") || mNum.endsWith("E") 
                                        || mNum.endsWith("F") || mNum.endsWith("G") || mNum.endsWith("H") || mNum.endsWith("I")) {
                                    
                                    
                                    if (mNum.endsWith("A")) {
                                        if (gender.equalsIgnoreCase("M")) {
                                            mtype = "Spouse Male";
                                        } else {
                                            mtype = "Spouse Female";
                                        }
                                    } else {
                                        mtype = "Dependent";
                                    }
                                    
                                    mNum = mNum.substring(0, mNum.length() - 1);
                                    
                                } else {
                                    if (gender.equalsIgnoreCase("F")) {
                                        mtype = "Primary Female";
                                    } else {
                                        mtype = "Primary Male";
                                    }
                                }
                                
                                if (mship.equalsIgnoreCase("Active-Associate") || mship.equalsIgnoreCase("Associate") || mship.equalsIgnoreCase("Dependent")) {
                                    mship = "Associate";
                                } else if (mship.equalsIgnoreCase("Active- Dining & Social") || mship.equalsIgnoreCase("Dependents-DS") || mship.equalsIgnoreCase("Spouse Dining and Social")) {
                                    mship = "DS";
                                } else if (mship.equalsIgnoreCase("Active-Dining & Social SNR")) {
                                    mship = "DSSR";
                                } else if (mship.equalsIgnoreCase("Active-Dining & Social SSr")) {
                                    mship = "DSSSR";
                                } else if (mship.equalsIgnoreCase("Active-Full Privilege") || mship.equalsIgnoreCase("Dependants-Full Privilege") || mship.equalsIgnoreCase("Spouses-Full Privilege")) {
                                    mship = "FP";
                                } else if (mship.equalsIgnoreCase("Active-Full Privilege U41")) {
                                    mship = "FP40U";
                                } else if (mship.equalsIgnoreCase("Full Privilege Intermediate")) {
                                    mship = "FPI";
                                } else if (mship.equalsIgnoreCase("Active Full Privilege JNR") || mship.equalsIgnoreCase("Spouse-FPJr")) {
                                    mship = "FPJR";
                                } else if (mship.equalsIgnoreCase("Active Full Privilege NRES")) {
                                    mship = "FPNR";
                                } else if (mship.equalsIgnoreCase("Active-Full Privilege Senior")) {
                                    mship = "FPSR";
                                } else if (mship.equalsIgnoreCase("Active-Full Privilege SSr") || mship.equalsIgnoreCase("Spouse-FP Super Senior")) {
                                    mship = "FPSSR";
                                } else if (mship.equalsIgnoreCase("Active-HTP") || mship.equalsIgnoreCase("Active-HTP Intermediate") || mship.equalsIgnoreCase("Active-HTP Junior") 
                                        || mship.equalsIgnoreCase("Dependants-HTP") || mship.equalsIgnoreCase("Spouses- HTP")) {
                                    mship = "HTP";
                                } else if (mship.equalsIgnoreCase("Active - HTPNG") || mship.equalsIgnoreCase("DEP-HTPNG") || mship.equalsIgnoreCase("SPS-HTPNG")) {
                                    mship = "HTPNG";
                                } else if (mship.equalsIgnoreCase("Active-HTP Non Resident")) {
                                    mship = "NTPNR";
                                } else if (mship.equalsIgnoreCase("Active-HTP Senior")) {
                                    mship = "NTPSR";
                                } else if (mship.equalsIgnoreCase("Reciprocal Clubs")) {
                                    mship = "Recip";
                                } else {
                                    skip = true;
                                    SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                }
                            }
                        }  // end of IF club = pattersonclub
                        //******************************************************************
                        //   Brooklawn Country Club - brooklawn
                        //******************************************************************
                        //
                        if (club.equals("brooklawn")) {

                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {

                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (flexid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else if (memid.equals("")) {

                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                            } else {
                                
                                mNum = memid;
                                posid = memid;
                                
                                //  clean up mNum
                                if (!mNum.equals("")) {

                                    mNum = mNum.toUpperCase();

                                    if (mNum.length() > 2 && (mNum.endsWith("S1") || mNum.endsWith("C1") || mNum.endsWith("C2") || mNum.endsWith("C3") || mNum.endsWith("C4")
                                            || mNum.endsWith("C5") || mNum.endsWith("C6") || mNum.endsWith("C7") || mNum.endsWith("C8") || mNum.endsWith("C9"))) {

                                        mNum = mNum.substring(0, mNum.length() - 2);

                                    } else if (mNum.length() > 1 && mNum.endsWith("S")) {

                                        mNum = mNum.substring(0, mNum.length() - 1);
                                    }
                                }

                                if (!custom_data.equals("")) {
                                    email = custom_data;
                                }
                                if (!custom_data2.equals("")) {
                                    email2 = custom_data2;
                                } else {
                                    email2 = "";    // They don't want the default email2 to apply.  Only the custom one, if present!!
                                }
                                
                                //  set defaults
                                if (gender.equalsIgnoreCase("F")) {

                                    gender = "F";               // Female

                                } else {

                                    gender = "M";               // default to Male
                                }

                                suffix = "";     // done with suffix for now

                                //
                                //  Set the Member Type
                                //
                                if (primary.equalsIgnoreCase("0")) {

                                    mtype = "Primary Male";

                                    if (gender.equals("F")) {

                                        mtype = "Primary Female";
                                    }

                                } else if (primary.equalsIgnoreCase("1")) {

                                    mtype = "Spouse Male";

                                    if (gender.equals("F")) {

                                        mtype = "Spouse Female";
                                    }

                                } else if (primary.equalsIgnoreCase("2")) {

                                    mtype = "Dependent";

                                } else {

                                    skip = true;
                                }

                                //
                                //  Determine the age in years
                                //
                                Calendar cal = new GregorianCalendar();       // get todays date

                                int year = cal.get(Calendar.YEAR);
                                int month = cal.get(Calendar.MONTH) + 1;
                                int day = cal.get(Calendar.DAY_OF_MONTH);

                                year = year - 24;             // backup 24 years

                                int oldDate = (year * 10000) + (month * 100) + day;   // get date

                                if (mtype.equals("Dependent") && birth > 0 && birth < oldDate) {

                                    skip = true;
                                }
                                if (mship.equalsIgnoreCase("Employee")) {
                                    mship = "Employee";
                                } else if (mship.equalsIgnoreCase("Intermediate Resident Family") || mship.equalsIgnoreCase("Intermediate Resident Individual")) {
                                    mship = "Intermediate Resident";
                                } else if (mship.equalsIgnoreCase("Junior Family Legacy") || mship.equalsIgnoreCase("Junior Individual Legacy")) {
                                    mship = "Junior Legacy";
                                } else if (mship.equalsIgnoreCase("Junior Individual") || mship.equalsIgnoreCase("Junior Family")) {
                                    mship = "Junior";
                                } else if (mship.equalsIgnoreCase("Legacy Intermediate Family") || mship.equalsIgnoreCase("Legacy Intermediate Individual")) {
                                    mship = "Legacy Intermediate";
                                } else if (mship.equalsIgnoreCase("Regular Resident Family") || mship.equalsIgnoreCase("Resident Individual")) {
                                    mship = "Resident";
                                } else if (mship.equalsIgnoreCase("Senior Family") || mship.equalsIgnoreCase("Senior Individual")) {
                                    mship = "Senior";
                                } else if (mship.equalsIgnoreCase("Senior Plus Family") || mship.equalsIgnoreCase("Senior Plus Individual")) {
                                    mship = "Senior Plus";
                                } else if (mship.equalsIgnoreCase("Non Legacy Social Individual") || mship.equalsIgnoreCase("Non Legacy Social Family")) {
                                    mship = "Non Legacy Social";
                                } else if (mship.equalsIgnoreCase("Legacy Social Individual") || mship.equalsIgnoreCase("Legacy Social Family")) {
                                    mship = "Legacy Social";
                                } else if (mship.equalsIgnoreCase("Non Resident Family")) {
                                    mship = "Non Resident Family";
                                } else if (mship.equalsIgnoreCase("Non Resident Individual")) {
                                    mship = "Non Resident Individual";
                                } else if (mship.equalsIgnoreCase("Senior Legacy")) {
                                    mship = "Senior Legacy";
                                } else {
                                    skip = true;
                                    SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                }

                            }
                        }      // end of if brooklawn
                         //******************************************************************
                         //   Mesa Verde CC - mesaverdecc
                         //******************************************************************
                         //
                         if (club.equals("mesaverdecc")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;

                                 while (memid.startsWith("0")) {
                                     memid = memid.substring(1);
                                 }

                                 mNum = memid;

                                 if (mNum.toUpperCase().endsWith("A") || mNum.toUpperCase().endsWith("B") || mNum.toUpperCase().endsWith("C") 
                                         || mNum.toUpperCase().endsWith("D") || mNum.toUpperCase().endsWith("E") || mNum.toUpperCase().endsWith("F") || mNum.toUpperCase().endsWith("G")) {
                                     mNum = mNum.substring(0, mNum.length() - 1);
                                 }

                                 if (primary.equals("0") || primary.equals("1")) {
                                     mtype = "Member";
                                 } else {
                                     mtype = "Junior Child";
                                 }
                                 
                                 if (mship.equalsIgnoreCase("Equity Golf") || mship.equalsIgnoreCase("Equity Golf Child") || mship.equalsIgnoreCase("Equity Golf Spouse") 
                                         || mship.equalsIgnoreCase("Equity Junior") || mship.equalsIgnoreCase("Equity Junior Child") || mship.equalsIgnoreCase("Equity Junior Spouse") 
                                         || mship.equalsIgnoreCase("Golf Widow")) {
                                     mship = "Golf";
                                 } else if (mship.equalsIgnoreCase("Equity Tennis") || mship.equalsIgnoreCase("Equity Tennis Child") || mship.equalsIgnoreCase("Equity Tennis Spouse") 
                                         || mship.equalsIgnoreCase("Social Tennis") || mship.equalsIgnoreCase("Social Tennis Child") || mship.equalsIgnoreCase("Social Tennis Spouse") 
                                         || mship.equalsIgnoreCase("Tennis Widow")) {
                                     mship = "Tennis";
                                 } else if (mship.equalsIgnoreCase("Honorary Member") || mship.equalsIgnoreCase("Honorary Member Child") || mship.equalsIgnoreCase("Honorary Member Spouse") || mship.equalsIgnoreCase("Honored Guest")) {
                                     mship = "Honorary";
                                 } else if (mship.equalsIgnoreCase("Social Golf") || mship.equalsIgnoreCase("Social Golf Spouse")) {
                                     mship = "Social";
                                 } else {
                                    skip = true;
                                    SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }
                    
                             }
                         }      // end of if mesaverdecc 
                         //******************************************************************
                         //   Riverton CC - rivertoncc
                         //******************************************************************
                         //
                         if (club.equals("rivertoncc")) {

                             found = true;        // club found

                             //
                             //  Determine if we should process this record
                             //
                             if (mship.equals("")) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else if (flexid.equals("")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                             } else {

                                 posid = memid;

                                 mNum = memid;
                                 fname = toTitleCase(fname);
                                 lname = toTitleCase(lname);

                                 if (mNum.contains("-")) {
                                     mNum = mNum.substring(0, mNum.length() - 2);    // Strip of trailing '-#' from mNum
                                 }

                                 if (memid.endsWith("-1")) {
                                     memid = memid.substring(0, memid.length() - 2) + "A";
                                 } else if (memid.endsWith("-2")) {
                                     memid = memid.substring(0, memid.length() - 2) + "B";
                                 } else if (memid.endsWith("-3")) {
                                     memid = memid.substring(0, memid.length() - 2) + "C";
                                 } else if (memid.endsWith("-4")) {
                                     memid = memid.substring(0, memid.length() - 2) + "D";
                                 } else if (memid.endsWith("-5")) {
                                     memid = memid.substring(0, memid.length() - 2) + "E";
                                 } else if (memid.endsWith("-6")) {
                                     memid = memid.substring(0, memid.length() - 2) + "F";
                                 } else if (memid.endsWith("-7")) {
                                     memid = memid.substring(0, memid.length() - 2) + "G";
                                 } else if (memid.endsWith("-8")) {
                                     memid = memid.substring(0, memid.length() - 2) + "H";
                                 } else if (memid.endsWith("-9")) {
                                     memid = memid.substring(0, memid.length() - 2) + "I";
                                 }
                                
                                 if (mship.equalsIgnoreCase("CLERGY/SINGLE CLERGY")) {
                                     mship = "Clergy";
                                 } else if (mship.equalsIgnoreCase("CORPORATE GOLF")) {
                                     mship = "Corp. Golf";
                                 } else if (mship.equalsIgnoreCase("GOLF (Age 41 & Older)")) {
                                     mship = "Golf";
                                 } else if (mship.equalsIgnoreCase("CORPORATE HOUSE") || mship.equalsIgnoreCase("HOUSE/SOCIAL") || mship.equalsIgnoreCase("SENIOR HOUSE")) {
                                     mship = "House";
                                 } else if (mship.equalsIgnoreCase("HONORARY")) {
                                     mship = "Honorary";
                                 } else if (mship.equalsIgnoreCase("INTERMEDIATE GOLF (Age 19-31)")) {
                                     mship = "Intermediate";
                                 } else if (mship.equalsIgnoreCase("JR. IND GOLF HOUSE (Age 14-18)") || mship.equalsIgnoreCase("JUNIOR IND GOLF (Age 14-18)")) {
                                     mship = "Junior Individual";
                                 } else if (mship.equalsIgnoreCase("NON-RESIDENT")) {
                                     mship = "Non Resident";
                                 } else if (mship.equalsIgnoreCase("SENIOR GOLD 2000") || mship.equalsIgnoreCase("SENIOR GOLD LEGACY")) {
                                     mship = "Senior Gold";
                                 } else if (mship.equalsIgnoreCase("SENIOR GOLF")) {
                                     mship = "Senior Golf";
                                 } else if (mship.equalsIgnoreCase("SINGLE GOLF (Age 41 & Older)")) {
                                     mship = "Single Golf";
                                 } else if (mship.equalsIgnoreCase("TENNIS")) {
                                     mship = "Tennis";
                                 } else if (mship.equalsIgnoreCase("YOUNG PROF. (Age 32-40)")) {
                                     mship = "Young Pro";
                                 } else if (mship.equalsIgnoreCase("WOMENS GOLF") || mship.equalsIgnoreCase("WOMENS SENIOR GOLF") 
                                         || mship.equalsIgnoreCase("SURVIVING SPOUSE")) {
                                     mship = "Women's Golf";
                                 } else if (mship.equalsIgnoreCase("DEPENDENT")) {
                                     
                                     PreparedStatement pstmtx = null;
                                     ResultSet rsx = null;
                                     
                                     try {
                                         pstmtx = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                                         pstmtx.clearParameters();
                                         pstmtx.setString(1, mNum);
                                         
                                         rsx = pstmtx.executeQuery();
                                         
                                         if (rsx.next()) {
                                             mship = rsx.getString("m_ship");
                                         } else {
                                             skip = true;
                                             SystemUtils.logErrorToFile("SKIPPED - PRIMARY NOT FOUND FOR DEPENDENT RECORD (Spouse/Dependent) user=" + memid, club, true);
                                         }
                                     } catch (Exception e) {
                                         Utilities.logError("Common_sync_premier.flexSync - " + club + " - Error looking up primary record for user=" + memid + ", error=" + e.toString());
                                     } finally {
                                         Connect.close(rsx, pstmtx);
                                     }
                                     
                                 } else {
                                    skip = true;
                                    SystemUtils.logErrorToFile("SKIPPED - NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                 }
                                 
                                 if (primary.equals("1")) {
                                     if (gender.equalsIgnoreCase("M")) {
                                         mtype = "Spouse Male";
                                     } else {
                                         mtype = "Spouse Female";
                                     }
                                 } else if (primary.equals("0")) {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Primary Female";
                                     } else {
                                         mtype = "Primary Male";
                                     } 
                                 } else {
                                     mtype = "Dependent";
                                 }
                                 

                             }
                         }      // end of if rivertoncc 
                         
//                         //******************************************************************
//                         //   Meridian Golf Club - meridiangc
//                         //******************************************************************
//                         //
//                         if (club.equals("meridiangc")) {
//
//                             found = true;        // club found
//
//                             //
//                             //  Determine if we should process this record
//                             //
//                             if (mship.equals("")) {
//
//                                 skip = true;            // skip record if mship not one of above
//                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
//
//                             } else if (flexid.equals("")) {
//
//                                 skip = true;              // skip record if webid not provided
//                                 SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
//
//                             } else {
//
//                                 posid = memid;
//
//                                 while (memid.startsWith("0")) {
//                                     memid = memid.substring(1);
//                                 }
//
//                                 mNum = memid;
//
//
//                                 if (primary.equals("1")) {
//                                     memid +="A";
//                                     if (gender.equalsIgnoreCase("M")) {
//                                         mtype = "Secondary Male";
//                                     } else {
//                                         mtype = "Secondary Female";
//                                     }
//                                 } else if (primary.equals("0")) {
//                                     if (gender.equalsIgnoreCase("F")) {
//                                         mtype = "Primary Female";
//                                     } else {
//                                         mtype = "Primary Male";
//                                     }
//                                 } else {
//                                     skip = true;    // Skip dependents for now.  May add later
//                                     SystemUtils.logErrorToFile("SKIPPED - DEPENDENT RECORD primary=" + primary, club, true);
//                                 }
//
//                                 if (mship.equalsIgnoreCase("Members") || mship.equalsIgnoreCase("MGM") || mship.equalsIgnoreCase("Owner")) {
//                                     mship = "Member";
//                                 } else if (mship.equalsIgnoreCase("Range")) {
//                                     mship = "Range";
//                                 } else if (mship.equalsIgnoreCase("Dues Class 25") || mship.equalsIgnoreCase("Staff")) {
//                                     mship = "Employee";
//                                 } else {
//                                     skip = true;
//                                     SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
//                                 }                     
//                             }
//                         }      // end of if meridiangc
                         
                        //******************************************************************
                        //   The Edison Club - edisonclub
                        //******************************************************************
                        //
                        if (club.equals("edisonclub")) {
                            
                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {
                                
                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                                
                            } else if (flexid.equals("")) {
                                
                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                                
                            } else {
                                
                                posid = memid;
                                
                                while (memid.startsWith("0")) {
                                    memid = memid.substring(1);
                                }
                                
                                mNum = memid;
                                
                                
                                if (primary.equals("1")) {
                                    mNum = mNum.substring(0, mNum.length() - 1);
                                    if (gender.equalsIgnoreCase("M")) {
                                        mtype = "Spouse Male";
                                    } else {
                                        mtype = "Spouse Female";
                                    }
                                } else if (primary.equals("2")) {
                                    mNum = mNum.substring(0, mNum.length() - 1);
                                    mtype = "Dependent";
                                } else {
                                    if (gender.equalsIgnoreCase("F")) {
                                        mtype = "Primary Female";
                                    } else {
                                        mtype = "Primary Male";
                                    }
                                }
                                
                                if (mship.equalsIgnoreCase("Jr. Associate *Y*") || mship.equalsIgnoreCase("Jr. Associate *M*")) {
                                    mship = "Junior Associate";
                                    
                                } else if (mship.equalsIgnoreCase("Super Sr. Family *M*") || mship.equalsIgnoreCase("Sp-Super Sr. Family *M*") || mship.equalsIgnoreCase("Super Sr. Single *Y*") || mship.equalsIgnoreCase("Super Sr. Single *M*")) {
                                    mship = "Super Sr Family";
                                    
                                } else if (mship.equalsIgnoreCase("Honorary No F/B Min.") || mship.equalsIgnoreCase("Honorary B - Honorary") || mship.equalsIgnoreCase("Sp-Honorary B") || mship.equalsIgnoreCase("Sp-Honorary No F/B Min.")
                                         || mship.equalsIgnoreCase("Honorary B")) {
                                    mship = "Honorary";
                                } else if (mship.equalsIgnoreCase("Golf-Reg Family *Y*") || mship.equalsIgnoreCase("Sp-Golf-Reg Family *Y*") || mship.equalsIgnoreCase("Posting") || mship.equalsIgnoreCase("Golf-Reg Family *M*")
                                        || mship.equalsIgnoreCase("Sp-Golf-Reg Family *M*") || mship.equalsIgnoreCase("Dep-Golf-Reg Family *M*") || mship.equalsIgnoreCase("Junior 38 Family *M*") || mship.equalsIgnoreCase("Sp-Junior 38 Family *M*")
                                        || mship.equalsIgnoreCase("Non-Res Family *Y*") || mship.equalsIgnoreCase("Sp-Non-Res Family *Y*") || mship.equalsIgnoreCase("PGA Family *Y*") || mship.equalsIgnoreCase("Sp-PGA Family *Y*")
                                        || mship.equalsIgnoreCase("Dep-Golf-Reg Family *Y*")) {
                                    mship = "Family";
                                } else if (mship.equalsIgnoreCase("Golf-Single Male *Y*") || mship.equalsIgnoreCase("Non-Res Single *Y*") || mship.equalsIgnoreCase("Golf-Reg Male *M*") || mship.equalsIgnoreCase("PGA Single *Y*")
                                        || mship.equalsIgnoreCase("Sp-Golf-Reg Male *M*") || mship.equalsIgnoreCase("Golf-Single Female *Y*") || mship.equalsIgnoreCase("Golf-Business") || mship.equalsIgnoreCase("Sp-Golf-Business")
                                        || mship.equalsIgnoreCase("Other Clubs") || mship.equalsIgnoreCase("Golf-Reg Female *M*")) {
                                    mship = "Single";
                                    
                                } else if (mship.equalsIgnoreCase("Social/House *Y*") || mship.equalsIgnoreCase("Tennis/Pool *Y*") || mship.equalsIgnoreCase("Sp-Tennis/Pool *Y*") || mship.equalsIgnoreCase("Tennis/Pool *M*")
                                        || mship.equalsIgnoreCase("Sp-Tennis/Pool *M*") || mship.equalsIgnoreCase("Tennis Only *Y*") || mship.equalsIgnoreCase("Sp-Tennis Only *Y*") || mship.equalsIgnoreCase("Leave Of Absence *Y*")
                                        || mship.equalsIgnoreCase("Sp-Social/House *Y*") || mship.equalsIgnoreCase("Guest") || mship.equalsIgnoreCase("Sp-Leave Of Absence *Y*")) {
                                    mship = "Social";
                                } else if (mship.equalsIgnoreCase("Junior 40 Family *Y*") || mship.equalsIgnoreCase("Sp-Junior 40 Family *Y*") || mship.equalsIgnoreCase("Junior 40 Family *M*") || mship.equalsIgnoreCase("Sp-Junior 40 Family *M*")
                                        || mship.equalsIgnoreCase("Junior 39 Family *Y*") || mship.equalsIgnoreCase("Sp-Junior 39 Family *Y*") || mship.equalsIgnoreCase("Junior 37 Family *Y*") || mship.equalsIgnoreCase("Sp-Junior 37 Family *Y*")
                                        || mship.equalsIgnoreCase("Sp-Junior 31 Family *Y*") || mship.equalsIgnoreCase("Junior 36 Family *Y*") || mship.equalsIgnoreCase("Sp-Junior 36 Family *Y*") || mship.equalsIgnoreCase("Golf-Jr 30 Family *Y*")
                                        || mship.equalsIgnoreCase("Sp-Golf-Jr 30 Family *Y") || mship.equalsIgnoreCase("Junior 27 Family *Y*") || mship.equalsIgnoreCase("Sp-Junior 27 Family *Y*") || mship.equalsIgnoreCase("Junior 32 Family *Y*")
                                        || mship.equalsIgnoreCase("Sp-Junior 32 Family *Y*") || mship.equalsIgnoreCase("Junior 34 Family *M*") || mship.equalsIgnoreCase("Sp-Junior 34 Family *M*") || mship.equalsIgnoreCase("Junior 29 Family *Y*")
                                        || mship.equalsIgnoreCase("Sp-Junior 29 Family *Y*") || mship.equalsIgnoreCase("Junior 33 Family *Y*") || mship.equalsIgnoreCase("Sp-Junior 33 Family *Y*") || mship.equalsIgnoreCase("Junior 31 Family *Y*")) {
                                    mship = "Junior Family";                                    
                                    
                                } else if (mship.equalsIgnoreCase("Junior 32 Single *M*") || mship.equalsIgnoreCase("Junior 34 Single *Y*") || mship.equalsIgnoreCase("Junior 25 Single *Y*") || mship.equalsIgnoreCase("Junior 31 Single *Y*")
                                        || mship.equalsIgnoreCase("Junior 26 Single *Y*") || mship.equalsIgnoreCase("Junior 23 Single *Y*") || mship.equalsIgnoreCase("Junior 40 Single *M*") || mship.equalsIgnoreCase("Junior 27 Single *M*")
                                        || mship.equalsIgnoreCase("Junior 35 Single *Y*") || mship.equalsIgnoreCase("Junior 33 Single *M*") || mship.equalsIgnoreCase("Junior 32 Single *Y*") || mship.equalsIgnoreCase("Junior 24 Single *Y*")
                                        || mship.equalsIgnoreCase("Junior 37 Single *M*") || mship.equalsIgnoreCase("Junior 36 Single *Y*") || mship.equalsIgnoreCase("Junior 34 Single *M*") || mship.equalsIgnoreCase("Junior 25 Single *M*")
                                         || mship.equalsIgnoreCase("Junior 37 Single *Y*")) {
                                    mship = "Junior Single";
                                } else if (mship.equalsIgnoreCase("Golf-Sr Family *Y*") || mship.equalsIgnoreCase("Sp-Golf-Sr Family *Y*") || mship.equalsIgnoreCase("Golf-Sr Family *M*") || mship.equalsIgnoreCase("Sp-Golf-Sr Family *M*")) {
                                    mship = "Sr. Family";
                                    
                                } else if (mship.equalsIgnoreCase("Golf-Sr Male *Y*") || mship.equalsIgnoreCase("Golf-Sr Male *M*") || mship.equalsIgnoreCase("Golf-Sr Female *M*") || mship.equalsIgnoreCase("Golf-Sr Female *Y*")) {
                                    mship = "Sr. Single";
                                }
                            }
                        }      // end of if edisonclub
                        //******************************************************************
                        //   Avon Oaks Country Club - avonoakscc
                        //******************************************************************
                        //
                        if (club.equals("avonoakscc")) {
                            
                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {
                                
                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                                
                            } else if (flexid.equals("")) {
                                
                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                                
                            } else {
                                
                                posid = memid;
                                
                                while (memid.startsWith("0")) {
                                    memid = memid.substring(1);
                                }
                                
                                mNum = memid;
                                
                                
                                if (primary.equals("0")) {
                                    if (gender.equalsIgnoreCase("F")) {
                                        mtype = "Primary Female";
                                    } else {
                                        mtype = "Primary Male";
                                    }
                                    
                                    if (mship.equalsIgnoreCase("Tennis Family") || mship.equalsIgnoreCase("Tennis Single")) {
                                        mship = "Tennis";
                                    } else if (mship.equalsIgnoreCase("Corporate")) {
                                        mship = "Corporate";
                                    } else if (mship.equalsIgnoreCase("Family")) {
                                        mship = "Family";
                                    } else if (mship.equalsIgnoreCase("Golfing Premier")) {
                                        mship = "Golfing Premier";
                                    } else if (mship.equalsIgnoreCase("Golfing Single") || mship.equalsIgnoreCase("Golfing Single Junior")) {
                                        mship = "Golfing Single Mbr";
                                    } else if (mship.equalsIgnoreCase("Golfing Single Junior plus Spouse")) {
                                        mship = "Golfing Single Jr";
                                    } else if (mship.equalsIgnoreCase("Golfing Single plus Spouse") || mship.equalsIgnoreCase("Golfing Single plus Child")) {
                                        mship = "Golfing Single";
                                    } else if (mship.equalsIgnoreCase("Lifetime Family")) {
                                        mship = "Ltm Family";
                                    } else if (mship.equalsIgnoreCase("Lifetime Senior")) {
                                        mship = "Ltm Senior";
                                    } else if (mship.equalsIgnoreCase("Senior")) {
                                        mship = "Senior";
                                    } else if (mship.equalsIgnoreCase("Pool Social")) {
                                        mship = "Social";
                                    } else if (mship.equalsIgnoreCase("NO Charge") || mship.equalsIgnoreCase("Special")) {
                                        mship = "Special";
                                    } else if (mship.equalsIgnoreCase("Social Premier")) {
                                        mship = "Social Premier";
                                    } else {
                                        skip = true;
                                        SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                                    }
                                } else {
                                    mNum = mNum.substring(0, mNum.length() - 1);
                                    if (primary.equals("1")) {
                                        if (gender.equalsIgnoreCase("M")) {
                                            mtype = "Spouse Male";
                                        } else {
                                            mtype = "Spouse Female";
                                        }
                                    } else {
                                        mtype = "Dependent";
                                    }
                                    
                                    PreparedStatement pstmtx = null;
                                    ResultSet rsx = null;

                                    try {

                                        pstmtx = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                                        pstmtx.clearParameters();
                                        pstmtx.setString(1, mNum);

                                        rsx = pstmtx.executeQuery();

                                        if (rsx.next()) {
                                            mship = rsx.getString("m_ship");
                                        } else {
                                            skip = true;
                                            SystemUtils.logErrorToFile("PRIMARY NOT FOUND(" + mNum + ")" + lname + ", " + fname, club, true);
                                        }

                                    } catch (Exception e) {
                                        Utilities.logError("Common_sync_premier.flexSync - " + club + " - failed looking up primary mship - Error = " + e.toString());
                                    } finally {
                                        Connect.close(rsx, pstmtx);
                                    }
                                    if (mship.equalsIgnoreCase("Golfing Single Mbr")) {
                                        mship = "Social";
                                    }
                                    
                                    
                                }
                                
                                
                                

                                

                            }
                        }      // end of if edisonclub
                        //******************************************************************
                        //   Woodland Country Clu - woodlandcc
                        //******************************************************************
                        //
                        if (club.equals("woodlandcc")) {
                            
                            found = true;        // club found

                            //
                            //  Determine if we should process this record
                            //
                            if (mship.equals("")) {
                                
                                skip = true;            // skip record if mship not one of above
                                SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                                
                            } else if (flexid.equals("")) {
                                
                                skip = true;              // skip record if webid not provided
                                SystemUtils.logErrorToFile("FLEXID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                                
                            } else {                                
                                
                                while (memid.startsWith("0")) {
                                    memid = memid.substring(1);
                                }
                                
                                mNum = memid;
                                
                                
                                if (primary.equals("0")) {
                                    if (gender.equalsIgnoreCase("F")) {
                                        mtype = "Adult Female";
                                    } else {
                                        mtype = "Adult Male";
                                    }
                                    
                                    if (mship.equalsIgnoreCase("C awaiting A")) {
                                        mship = "C Waiting A Mmbr";
                                    } else if (mship.equalsIgnoreCase("Clubhouse Social")) {
                                        mship = "Clubhouse Member";
                                    } else if (mship.equalsIgnoreCase("Corporate Golf")) {
                                        mship = "Corporate Sponsored Mbr.";
                                    } else if (mship.equalsIgnoreCase("Family Social")) {
                                        mship = "Family Social Member";
                                    } else if (mship.equalsIgnoreCase("Golf")) {
                                        mship = "Golfing Member";
                                    } else if (mship.equalsIgnoreCase("Golf with OC ")) {
                                        mship = "Golf Member W/Oc";
                                    } else if (mship.equalsIgnoreCase("Honorary")) {
                                        mship = "Honorary";
                                    } else if (mship.equalsIgnoreCase("Jr Golf")) {
                                        mship = "Jr. Golf Member 30-34";
                                    } else if (mship.equalsIgnoreCase("Lady Clubhouse")) {
                                        mship = "Lady Clubhouse Member";
                                    } else if (mship.equalsIgnoreCase("Lady Family Social")) {
                                        mship = "Lady Fam. Social Mmbr";
                                    } else if (mship.equalsIgnoreCase("Lady Golf")) {
                                        mship = "Lady Golf Member";
                                    } else if (mship.equalsIgnoreCase("Lady Golf with OC")) {
                                        mship = "Lady Golf Member W/Oc";
                                    } else if (mship.equalsIgnoreCase("Non-Resident")) {
                                        mship = "Non-Resident";
                                    } else if (mship.equalsIgnoreCase("Sr Golf 2004")) {
                                        mship = "Sr. Golf Member-'04";
                                    } else if (mship.equalsIgnoreCase("Sr Golf with OC")) {
                                        mship = "Sr. Golf Member W/Oc";
                                    } else if (mship.equalsIgnoreCase("Super Jr. Golf")) {
                                        mship = "Super Jr. Golf Mbr <30";
                                    }
                                } else {
                                    mNum = mNum.substring(0, mNum.length() - 2);
                                    if (primary.equals("1")) {
                                        if (gender.equalsIgnoreCase("M")) {
                                            mtype = "Adult Male";
                                        } else {
                                            mtype = "Adult Female";
                                        }
                                    } else {
                                        if (gender.equalsIgnoreCase("F")) {
                                            mtype = "Junior Female";
                                        } else {
                                            mtype = "Junior Male";
                                        }
                                    }
                                    if (memid.endsWith("-1")) {
                                        memid = mNum+"A";
                                    } else if (memid.endsWith("-2")) {
                                        memid = mNum+"B";
                                    } else if (memid.endsWith("-3")) {
                                        memid = mNum+"C";
                                    } else if (memid.endsWith("-4")) {
                                        memid = mNum+"D";
                                    } else if (memid.endsWith("-5")) {
                                        memid = mNum+"E";
                                    } else if (memid.endsWith("-6")) {
                                        memid = mNum+"F";
                                    } else if (memid.endsWith("-7")) {
                                        memid = mNum+"G";
                                    }
                                    
                                    PreparedStatement pstmtx = null;
                                    ResultSet rsx = null;

                                    try {

                                        pstmtx = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                                        pstmtx.clearParameters();
                                        pstmtx.setString(1, mNum);

                                        rsx = pstmtx.executeQuery();

                                        if (rsx.next()) {
                                            mship = rsx.getString("m_ship");
                                        } else {
                                            skip = true;
                                            SystemUtils.logErrorToFile("PRIMARY NOT FOUND(" + mNum + ")" + lname + ", " + fname, club, true);
                                        }

                                    } catch (Exception e) {
                                        Utilities.logError("Common_sync_premier.flexSync - " + club + " - failed looking up primary mship - Error = " + e.toString());
                                    } finally {
                                        Connect.close(rsx, pstmtx);
                                    }                                                                        
                                }
                                posid = memid;
                            }
                        } 
                         //*********************************************
                         //  End of club specific processing
                         //*********************************************

                      } else {

                          skip = true;              // skip record if memid or name not provided
                          SystemUtils.logErrorToFile("USERNAME/NAME MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + memid, club, true);

                      }                 // end of IF minimum requirements met (memid, etc)



                      //
                      //******************************************************************
                      //  Common processing - add or update the member record
                      //******************************************************************
                      //
                      if (skip == false && found == true && !fname.equals("") && !lname.equals("")) {

                         //
                         //   now determine if we should update an existing record or add the new one
                         //
                         memid_old = "";
                         fname_old = "";
                         lname_old = "";
                         mi_old = "";
                         mship_old = "";
                         mtype_old = "";
                         email_old = "";
                         email2_old = "";
                         mNum_old = "";
                         posid_old = "";
                         phone_old = "";
                         phone2_old = "";
                         birth_old = 0;
                         ghin_old = "";
                         bag_old = "";
                         flexid_old = "";
                         flexid_new = "";
                         custom_string_old = "";
                         custom_string2_old = "";
                         default_activity_id_old = -1;
                         
                         String dupuser = "";
                         String dupmnum = "";

                         boolean memFound = false;
                         boolean dup = false;
                         memidChanged = false;
                         nameChanged = false;
                         changed = false;

                         //
                         //  Truncate the string values to avoid sql error
                         //
                         if (!mi.equals( "" )) {       // if mi specified

                            mi = truncate(mi, 1);           // make sure it is only 1 char
                         }
                         if (!memid.equals( "" )) {

                            memid = truncate(memid, 15);
                         }
                         if (!password.equals( "" )) {

                            password = truncate(password, 15);
                         }
                         if (!lname.equals( "" )) {

                            lname = truncate(lname, 20);
                         }
                         if (!fname.equals( "" )) {

                            fname = truncate(fname, 20);
                         }
                         if (!mship.equals( "" )) {

                            mship = truncate(mship, 30);
                         }
                         if (!mtype.equals( "" )) {

                            mtype = truncate(mtype, 30);
                         }
                         if (!email.equals( "" )) {

                            email = truncate(email, 50);
                         }
                         if (!email2.equals( "" )) {

                            email2 = truncate(email2, 50);
                         }
                         if (!mNum.equals( "" )) {

                            mNum = truncate(mNum, 15);
                         }
                         if (!ghin.equals( "" )) {

                            ghin = truncate(ghin, 16);
                         }
                         if (!bag.equals( "" )) {

                            bag = truncate(bag, 12);
                         }
                         if (!posid.equals( "" )) {

                            posid = truncate(posid, 15);
                         }
                         if (!phone.equals( "" )) {

                            phone = truncate(phone, 24);
                         }
                         if (!phone2.equals( "" )) {

                            phone2 = truncate(phone2, 24);
                         }
                         if (!suffix.equals( "" )) {

                            suffix = truncate(suffix, 4);
                         }
                         if (!custom_string.equals( "" )) {

                            custom_string = truncate(custom_string, 30);
                         }
                         if (!custom_string2.equals( "" )) {

                            custom_string2 = truncate(custom_string2, 30);
                         }
                         if (!flexid.equals( "" )) {

                             flexid = truncate(flexid, 15);
                         }



                         //
                         //   Always use the flexid 
                         //
                         try {
                             pstmt2 = con.prepareStatement (
                                      "SELECT * FROM member2b WHERE flexid = ?");

                             pstmt2.clearParameters();
                             pstmt2.setString(1, flexid);

                             rs = pstmt2.executeQuery();            // execute the prepared stmt

                             if(rs.next()) {

                                memid_old = rs.getString("username");
                                lname_old = rs.getString("name_last");
                                fname_old = rs.getString("name_first");
                                mi_old = rs.getString("name_mi");
                                mship_old = rs.getString("m_ship");
                                mtype_old = rs.getString("m_type");
                                email_old = rs.getString("email");
                                email2_old = rs.getString("email2");
                                mNum_old = rs.getString("memNum");
                                birth_old = rs.getInt("birth");
                                ghin_old = rs.getString("ghin");
                                bag_old = rs.getString("bag");
                                posid_old = rs.getString("posid");
                                phone_old = rs.getString("phone1");
                                phone2_old = rs.getString("phone2");
                                flexid_old = rs.getString("flexid");
                                custom_string_old = rs.getString("custom_string");
                                custom_string2_old = rs.getString("custom_string2");
                                default_activity_id_old = rs.getInt("default_activity_id");
                             }
                         } catch (Exception e) {
                             Utilities.logError("Common_sync_premier.flexSync - " + club + " - Failed looking up existing member record (flexid=" + flexid + ") - Error=" + e.toString());
                         } finally {
                             Connect.close(rs, pstmt2);
                         }

                         if (fname_old.equals("")) {
                             
                             //  New member - first check if name already exists
                             try {
                                 pstmt2 = con.prepareStatement(
                                         "SELECT username, memNum, flexid, name_last, name_first, name_mi, m_ship, m_type, email, email2, memNum, "
                                         + "birth, ghin, bag, posid, phone1, phone2, flexid, custom_string, custom_string2, default_activity_id, "
                                         + "IF(last_sync_date IN (DATE_FORMAT(CURDATE(), '%Y-%m-%d'), DATE_FORMAT(CURDATE() - INTERVAL 1 DAY, '%Y-%m-%d')), 1, 0) AS syncing "
                                         + "FROM member2b "
                                         + "WHERE name_last = ? AND name_first = ? AND name_mi = ? AND deleted = 0");

                                 pstmt2.clearParameters();
                                 pstmt2.setString(1, lname);
                                 pstmt2.setString(2, fname);
                                 pstmt2.setString(3, mi);
                                 rs = pstmt2.executeQuery();            // execute the prepared stmt

                                 if (rs.next()) {
                                     
                                     dupuser = rs.getString("username");          // get this username
                                     dupmnum = rs.getString("memNum");
                                     
                                     if (club.equals("optoutclub")) {    // Add clubs here if they don't want flexids updated
                                         dup = true;
                                     } else {

                                         //  name already exists - see if this is the same member
                                         if (!dupmnum.equals("") && dupmnum.equals(mNum)) {   // if name and mNum match, then memid or webid must have changed

                                             flexid_new = flexid;                  // set new ids
                                             memid_new = dupuser;
                                             memid_old = dupuser;                    // update this record
                                             
                                             lname_old = rs.getString("name_last");
                                             fname_old = rs.getString("name_first");
                                             mi_old = rs.getString("name_mi");
                                             mship_old = rs.getString("m_ship");
                                             mtype_old = rs.getString("m_type");
                                             email_old = rs.getString("email");
                                             email2_old = rs.getString("email2");
                                             mNum_old = rs.getString("memNum");
                                             birth_old = rs.getInt("birth");
                                             ghin_old = rs.getString("ghin");
                                             bag_old = rs.getString("bag");
                                             posid_old = rs.getString("posid");
                                             phone_old = rs.getString("phone1");
                                             phone2_old = rs.getString("phone2");
                                             flexid_old = rs.getString("flexid");
                                             custom_string_old = rs.getString("custom_string");
                                             custom_string2_old = rs.getString("custom_string2");
                                             default_activity_id_old = rs.getInt("default_activity_id");
                                             
                                             flexid = flexid_old;    // Set this so that the update statement later updates the correct record

                                             memFound = true;                      // update the member
                                        
                                         } else {
                                             dup = true;        // dup member - do not add
                                         }
                                     }
                                 }
                             } catch (Exception e) {
                                 Utilities.logError("Common_sync_premier.flexSync - " + club + " - Failed looking up duplicate member record (first|mi|last=" + fname + "|" + mi + "|" + lname + ") - Error=" + e.toString());
                             } finally {
                                 Connect.close(rs, pstmt2);
                             }
                         } else {
                             memFound = true;
                         }
                         
                         if (memFound) {            // if member found
                           
                            memid_new = memid_old;

                            /*
                            if (club.equals("foresthighlands")) {       // don't change usernames (initial setup, can remove later if desired)
                                memid = memid_old;
                            }
                            * 
                            */

                            /*  We have been turning this off for nearly every Premier client, as it continues to cause more problems with people not syncing than not updating usernames will prevent.
                             *  As a result, it's being turned off for all premier clubs to avoid future issues, but IS safe to turn on for specific clubs at a later date, if needed. - BSK 5/6/14
                            if (!memid.equals( memid_old ) && !club.equals("tontoverde") && !club.equals("pinehurstcountryclub") && !club.equals("pwgolf") && !club.equals("ccyork")
                                    && !club.equals("hillwoodcc")) {       // if username has changed

                               memid_new = memid;                   // use new memid
                               changed = true;
                               memidChanged = true;
                            }
                             */
                            
                            if (flexid_new.equals("")) {    // Default to old value if not updated in the duplicate user check above
                                flexid_new = flexid_old;
                            }

                            if ((club.equals("ccyork") || club.equalsIgnoreCase("sonnenalp") || club.equals("meadowsprings")) && !lname_old.equals("") || club.equalsIgnoreCase("baldpeak")) {
                                lname = lname_old;
                            }

                            lname_new = lname_old;

                            if (!lname.equals( "" ) && !lname_old.equals( lname )) {

                               lname_new = lname;         // set value from Flexscape record
                               nameChanged = true;
                               changed = true;
                            }

                            fname_new = fname_old;

                            if (!club.equals("belairbayclub")) {
                                fname = fname_old;         // Default for Premier is to not sync first names! Only perform for listed clubs.
                            }

                            if (!fname.equals( "" ) && !fname_old.equals( fname )) {

                               fname_new = fname;         // set value from Flexscape record
                               nameChanged = true;
                               changed = true;
                            }

                            if (club.equals("foresthighlands") || club.equals("pinehurstcountryclubs") || club.equals("blackhawkcc") || club.equals("stalbans") || club.equalsIgnoreCase("sonnenalp") || club.equalsIgnoreCase("meadowsprings")) {
                                mi = mi_old;
                            }

                            mi_new = mi_old;

                            if (!mi.equals( "" ) && !mi_old.equals( mi )) {

                               mi_new = mi;         // set value from Flexscape record
                               nameChanged = true;
                               changed = true;
                            }


                            if (club.equals("tontoverde")) {
                                mship = mship_old;
                            }

                            mship_new = mship_old;

                            if (!mship_old.equals( mship )) {   // if the mship has changed

                                mship_new = mship;         // set value from Flexscape record
                                changed = true;
                            }

                            if ((club.equals("missionviejo") || club.equals("pinehurstcountryclub") || club.equals("sonnenalp") || club.equals("pattersonclub") 
                                    || club.equals("mesaverdecc") || club.equals("meridiangc") || club.equals("brooklawn")) && !mtype_old.equals("")) {
                                mtype = mtype_old;
                            }

                            mtype_new = mtype_old;

                            if (!mtype.equals( "" ) && !mtype_old.equals( mtype )) {
                                mtype_new = mtype;         // set value from Flexscape record
                                changed = true;
                            }

                            birth_new = birth_old;

                            if (birth > 0 && birth != birth_old) {

                               birth_new = birth;         // set value from Flexscape record
                               changed = true;
                            }

                            ghin_new = ghin_old;

                            if (!ghin.equals("") && !ghin_old.equals(ghin)) {

                               ghin_new = ghin;
                               changed = true;
                            }

                            bag_new = bag_old;

                            if (!bag.equals("") && !bag_old.equals(bag)) {

                               bag_new = bag;
                               changed = true;
                            }

                            posid_new = posid_old;

                            if (!posid.equals( "" ) && !posid_old.equals( posid )) {

                               posid_new = posid;         // set value from Flexscape record
                               changed = true;
                            }

                            phone_new = phone_old;

                            if (!phone.equals( "" ) && !phone_old.equals( phone )) {

                               phone_new = phone;         // set value from Flexscape record
                               changed = true;
                            }

                            phone2_new = phone2_old;

                            if (!phone2.equals( "" ) && !phone2_old.equals( phone2 )) {

                               phone2_new = phone2;         // set value from Flexscape record
                               changed = true;
                            }

                            email_new = email_old;        // do not change emails

                            if (club.equals("oceanreef") || club.equals("hillwoodcc") || club.equalsIgnoreCase("sonnenalp") || club.equals("coloradospringscountryclub")) {
                                email = email_old;
                            }

                            if (!email.equals( "" ) && !email_old.equals( email )) {

                               email_new = email;         // set value from Flexscape record
                               changed = true;
                            }

                            email2_new = email2_old;

                            // Don't change email2 for these clubs
                            if (club.equals("parkmeadowscc") || club.equals("tontoverde") || club.equals("missionviejo") || club.equals("pinehurstcountryclub") 
                                    || club.equalsIgnoreCase("sonnenalp") || club.equals("coloradospringscountryclub") || club.equals("windstarclub")) {
                                email2 = email2_old;
                            }

                            if (!email2.equals( "" ) && !email2_old.equals( email2 )) {

                               email2_new = email2;         // set value from Flexscape record
                               changed = true;
                            }

                            custom_string_new = custom_string_old;

                            if (!custom_string.equals("") && !custom_string_old.equals(custom_string)) {

                                custom_string_new = custom_string;
                                changed = true;
                            }

                            custom_string2_new = custom_string2_old;

                            if (!custom_string2.equals("") && !custom_string2_old.equals(custom_string2)) {

                                custom_string2_new = custom_string2;
                                changed = true;
                            }

                            default_activity_id_new = default_activity_id_old;

                            if (default_activity_id != -1 && default_activity_id != default_activity_id_old) {

                                default_activity_id_new = default_activity_id;
                                changed = true;
                            }

                            // don't allow both emails to be the same
                            if (email_new.equalsIgnoreCase(email2_new)) email2_new = "";

                            //
                            //  NOTE:  mNums can change for this club!!
                            //
                            //         DO NOT change the flexid!!!!!!!!!
                            //
                            
                                         ///||||||||||||||||||||||||||\\\
                                        /// do not sync member numbers \\\
                                       ///||||||||||||||||||||||||||||||\\\
                            if (club.equalsIgnoreCase("sonnenalp") && !mNum_old.equals("")) {
                                mNum = mNum_old;
                            }
                            
                            mNum_new = mNum_old;

                            if (!mNum.equals( "" ) && !mNum_old.equals( mNum )) {

                               mNum_new = mNum;         // set value from Flexscape record

                               //
                               //  mNum changed - change it for all records that match the old mNum.
                               //                 This is because most dependents are not included in web site roster,
                               //                 but are in our roster.
                               //
                               if (club.equals("coloradospringscountryclub") || club.equals("oceanreef")) {

                                   pstmt2 = con.prepareStatement (
                                   "UPDATE member2b SET memNum = ? " +
                                   "WHERE memNum = ?");

                                   pstmt2.clearParameters();        // clear the parms
                                   pstmt2.setString(1, mNum_new);
                                   pstmt2.setString(2, mNum_old);
                                   pstmt2.executeUpdate();

                                   pstmt2.close();              // close the stmt
                               }
                            }


                            //
                            //  Update our record
                            //
                            if (changed == true) {

                               modCount++;             // count records changed
                            }

                            try {

                               pstmt2 = con.prepareStatement (
                                        "UPDATE member2b SET username = ?, name_last = ?, name_first = ?, "
                                        + "name_mi = ?, m_ship = ?, m_type = ?, email = ?, "
                                        + "memNum = ?, birth = ?, ghin = ?, bag = ?, posid = ?, "
                                        + "email2 = ?, phone1 = ?, phone2 = ?, inact = 0, last_sync_date = now(), "
                                        + "gender = ?, default_activity_id = ?, custom_string = ?, custom_string2 = ?, "
                                        + "flexid = ? "
                                        + "WHERE flexid = ?");

                               pstmt2.clearParameters();        // clear the parms
                               pstmt2.setString(1, memid_new);
                               pstmt2.setString(2, lname_new);
                               pstmt2.setString(3, fname_new);
                               pstmt2.setString(4, mi_new);
                               pstmt2.setString(5, mship_new);
                               pstmt2.setString(6, mtype_new);
                               pstmt2.setString(7, email_new);
                               pstmt2.setString(8, mNum_new);
                               pstmt2.setInt(9, birth_new);
                               pstmt2.setString(10, ghin_new);
                               pstmt2.setString(11, bag_new);
                               pstmt2.setString(12, posid_new);
                               pstmt2.setString(13, email2_new);
                               pstmt2.setString(14, phone_new);
                               pstmt2.setString(15, phone2_new);
                               pstmt2.setString(16, gender);
                               pstmt2.setInt(17, default_activity_id_new);
                               pstmt2.setString(18, custom_string_new);
                               pstmt2.setString(19, custom_string2_new);
                               pstmt2.setString(20, flexid_new);
                               pstmt2.setString(21, flexid);
                               pstmt2.executeUpdate();

                               pstmt2.close();              // close the stmt

                            }
                            catch (Exception e9) {

                               //errorMsg = errorMsg + " Error updating record (#" +rcount+ ") for " +club+ ", line = " +line+ ": " + e9.getMessage();   // build msg
                               //SystemUtils.logError(errorMsg);                                                  // log it
                               errorMsg = "Error in Common_sync_premier.flexSync: ";
                               SystemUtils.logErrorToFile("UPDATE MYSQL ERROR! - SKIPPED - name: " + lname + ", " + fname + " - " + memid + " - ERR: " + e9.toString(), club, true);
                            }


                            //
                            //  Now, update other tables if the username has changed
                            //
                            if ((memidChanged || nameChanged) && !memid_new.equals("") && !memid_old.equals("")) {

                               StringBuffer mem_name = new StringBuffer( fname_new );       // get the new first name

                               if (!mi_new.equals( "" )) {
                                  mem_name.append(" " +mi_new);               // new mi
                               }
                               mem_name.append(" " +lname_new);               // new last name

                               String newName = mem_name.toString();          // convert to one string

                               Admin_editmem.updTeecurr(newName, memid_new, memid_old, con);      // update teecurr with new values

                               Admin_editmem.updTeepast(newName, memid_new, memid_old, con);      // update teepast with new values

                               Admin_editmem.updLreqs(newName, memid_new, memid_old, con);        // update lreqs with new values

                               Admin_editmem.updPartner(memid_new, memid_old, con);        // update partner with new values

                               Admin_editmem.updEvents(newName, memid_new, memid_old, con);        // update evntSignUp with new values

                               Admin_editmem.updLessons(newName, memid_new, memid_old, con);       // update the lesson books with new values

                               Admin_editmem.updDemoClubs(memid_new, memid_old, con);              // update demo_clubs_usage with new values
                            }

                            // If organization_id is greater than 0, Dining system is in use.  Push updates to this member's record over to the dining system database
                            if (Utilities.getOrganizationId(con) > 0) {
                                Admin_editmem.updDiningDB(memid_new, con);
                            }

                         } else {

                            if (dup == false) {

                               //
                               //  New member - add it
                               //
                               newCount++;             // count records added

                               try {

                                  pstmt2 = con.prepareStatement (
                                     "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                                     "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                                     "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, " +
                                     "webid, last_sync_date, gender, flexid, default_activity_id, custom_string, custom_string2) " +
                                     "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,'',?,?,?,'',?,'',now(),?,?,?,?,?)");

                                  pstmt2.clearParameters();        // clear the parms
                                  pstmt2.setString(1, memid);        // put the parm in stmt
                                  pstmt2.setString(2, password);
                                  pstmt2.setString(3, lname);
                                  pstmt2.setString(4, fname);
                                  pstmt2.setString(5, mi);
                                  pstmt2.setString(6, mship);
                                  pstmt2.setString(7, mtype);
                                  pstmt2.setString(8, email);
                                  pstmt2.setFloat(9, c_hcap);
                                  pstmt2.setFloat(10, u_hcap);
                                  pstmt2.setString(11, mNum);
                                  pstmt2.setString(12, ghin);
                                  pstmt2.setString(13, bag);
                                  pstmt2.setInt(14, birth);
                                  pstmt2.setString(15, posid);
                                  pstmt2.setString(16, email2);
                                  pstmt2.setString(17, phone);
                                  pstmt2.setString(18, phone2);
                                  pstmt2.setString(19, suffix);
                                  pstmt2.setString(20, gender);
                                  pstmt2.setString(21, flexid);
                                  pstmt2.setInt(22, default_activity_id);
                                  pstmt2.setString(23, custom_string);
                                  pstmt2.setString(24, custom_string2);
                                  pstmt2.executeUpdate();          // execute the prepared stmt

                                  pstmt2.close();              // close the stmt

                                  // If organization_id is greater than 0, Dining system is in use.  Push updates to this member's record over to the dining system database
                                  if (Utilities.getOrganizationId(con) > 0) {
                                      Admin_editmem.updDiningDB(memid, con);
                                  }

                               }
                               catch (Exception e8) {

                                  //errorMsg = errorMsg + " Error adding record (#" +rcount+ ") for " +club+ ", line = " +line+ ": " + e8.getMessage();   // build msg
                                  //SystemUtils.logError(errorMsg);                                                  // log it
                                  errorMsg = "Error in Common_sync_premier.flexSync: ";
                                  SystemUtils.logErrorToFile("INSERT MYSQL ERROR! - SKIPPED - name: " + lname + ", " + fname + " - " + memid + " - ERR: " + e8.toString(), club, true);
                               }

                            } else {      // Dup name

                       //        errorMsg = errorMsg + " Duplicate Name found, name = " +fname+ " " +lname+ ", record #" +rcount+ " for " +club+ ", line = " +line;   // build msg
                       //        SystemUtils.logError(errorMsg);                                                  // log it
                               errorMsg = "Error in Common_sync_premier.flexSync: ";
                               SystemUtils.logErrorToFile("DUPLICATE NAME! - SKIPPED - name: " + lname + ", " + fname + " - " + memid, club, true);
                            }
                         }
                      }   // end of IF skip
                   }   // end of IF record valid (enough tokens)

             }   // end of IF header row

          } catch (Exception e3) {

              errorMsg = errorMsg + " Error processing member record (record #" + rcount + ") for " + club + ", line = " + line + ": " + e3.getMessage();   // build msg
              SystemUtils.logError(errorMsg);                                                  // log it
              errorMsg = "Error in Common_sync_premier.flexSync: ";
          }
          
      }   // end of while

      //
      //  Done with this file for this club - now set any members that were excluded from this file inactive
      //
      if (found == true) {       // if we processed this club
          
          // Unless manually force-overridden, check to see if more than 20 members are about to be set inactive, and if so, log an error and SKIP setting them inactive.
          if (!forceSetInact) {
              setInactCount = checkSyncSetInactCount(con);
          }

          // Set anyone inactive that didn't sync, but has at one point.
          if (setInactCount < setInactLimit) {

              String inact_statement = "UPDATE member2b SET inact = 1 "
                      + "WHERE last_sync_date != DATE(now()) AND last_sync_date != '0000-00-00' AND inact = 0";

              // If club uses dining, we need to make sure we push data for the inactive members over to the dining system one last time.
              if (Utilities.getOrganizationId(con) > 0) {
                  try {

                      // Get list of members in question
                      stmt2 = con.createStatement();
                      rs2 = stmt2.executeQuery("SELECT username, dining_id FROM member2b WHERE last_sync_date != DATE(now()) AND last_sync_date != '0000-00-00' AND inact = 0");

                      // Set those members inactive in ForeTees
                      try {
                          stmt3 = con.createStatement();
                          stmt3.executeUpdate(inact_statement);
                      } catch (Exception e) {
                          Utilities.logError("Common_sync.mFirstSync - " + club + " - Error setting members inactive - ERROR: " + e.toString());
                      } finally {
                          Connect.close(stmt3);
                      }

                      // Use list gathered earlier to push the now-inactive member data over to the Dining database
                      while (rs2.next()) {
                          if (rs2.getInt("dining_id") != 0) {
                              Admin_editmem.updDiningDB(rs2.getString("username"), con);
                          }
                      }

                  } catch (Exception e) {
                      Utilities.logError("Common_sync.mFirstSync - " + club + " - Error pushing inactive member data to dining system - ERROR: " + e.toString());
                  } finally {
                      Connect.close(rs2, stmt2);
                  }
              } else {    // Club doesn't use dining, just set members inactive.
                  try {
                      stmt3 = con.createStatement();
                      stmt3.executeUpdate(inact_statement);
                  } catch (Exception e) {
                      Utilities.logError("Common_sync.mFirstSync - " + club + " - Error setting members inactive - ERROR: " + e.toString());
                  } finally {
                      Connect.close(stmt3);
                  }
              }
              
         } else {
             Utilities.logDebug("BSK", "flexSync - Roster Sync for (" + club + ") was about to set (" + setInactCount + ") members inactive. Action skipped. Contact club to verify.");    // Temp to make it easier to spot
             Utilities.logError("flexSync - Roster Sync for (" + club + ") was about to set (" + setInactCount + ") members inactive. Action skipped. Contact club to verify.");
         }
          
         //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
         setRSind(con, club);
      }

   }
   catch (Exception e3) {

      errorMsg = errorMsg + " Error processing roster (record #" +rcount+ ") for " +club+ ", line = " +line+ ": " + e3.getMessage();   // build msg
      SystemUtils.logError(errorMsg);                                                  // log it
      errorMsg = "Error in Common_sync_premier.flexSync: ";
   }

 }


 // *********************************************************
 //  Convert Upper case names to title case (Bob P...)
 // *********************************************************

 private final static String toTitleCase( String s ) {

      char[] ca = s.toCharArray();

      boolean changed = false;
      boolean capitalise = true;

      for ( int i=0; i<ca.length; i++ ) {
         char oldLetter = ca[i];
         if ( oldLetter <= '/'
              || ':' <= oldLetter && oldLetter <= '?'
              || ']' <= oldLetter && oldLetter <= '`' ) {
            /* whitespace, control chars or punctuation */
            /* Next normal char should be capitalized */
            capitalise = true;
         } else {
            char newLetter  = capitalise
                              ? Character.toUpperCase(oldLetter)
                              : Character.toLowerCase(oldLetter);
            ca[i] = newLetter;
            changed |= (newLetter != oldLetter);
            capitalise = false;
         }
      } // end for

      return new String (ca);

 } // end toTitleCase


 // *********************************************************
 //  Remove dbl quotes and embedded commas from record
 //  Replace 2 dbl quotes in a row with a ?
 // *********************************************************

 private final static String cleanRecord2( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length];
      char letter;
      char lastLetter = 'a';          // init for complier
      int i2 = 0;
      boolean inquotes = false;

      for ( int i=0; i<ca.length; i++ ) {
         letter = ca[i];
         if ( letter != '"' ) {            // if not a quote
            if ( letter == ',' ) {         // is it a comma?
               if (inquotes == false) {    // skip commas while in between quotes
                  ca2[i2] = letter;        // save good letter
                  i2++;
               }
            } else {                       // not a quote or a comma - keep it

               ca2[i2] = letter;        // save good letter
               i2++;
            }

         } else {                      // quote - skip it or replace it, and check for 'between quotes'

            if (lastLetter == '"') {     // if 2 quotes in a row

               ca2[i2] = '?';            // replace with a '?'
               i2++;
            }

            if (inquotes == true) {

               inquotes = false;       // exit 'between quotes' mode

            } else {

               inquotes = true;        // enter 'between quotes' mode
            }
         }
         lastLetter = letter;          // save last letter
      }

      char[] ca3 = new char [i2];

      for ( int i=0; i<i2; i++ ) {
         letter = ca2[i];        // get from first array
         ca3[i] = letter;             // move to correct size array
      }

      return new String (ca3);

 } // end cleanRecord2



 private final static String cleanRecord4( String s ) {

     while (s.contains(",,")) {
         s = s.replace(",,", ",?,");
     }

     if (s.endsWith(",")) {
         s += "?";
     }

     return s;
     
 }// end cleanRecord4
 
 
 
 // *********************************************************
 //  Return a string with the specified length from a possibly longer field
 // *********************************************************

 private final static String truncate( String s, int slength ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [slength];


      if (slength < ca.length) {       // if string is longer than allowed

         for ( int i=0; i<slength; i++ ) {
            ca2[i] = ca[i];
         } // end for

      } else {

         return (s);
      }

      return new String (ca2);

 } // end truncate


    /**
     * checkSyncSetInactCount - Returns a count of how many active, previously syncing members are about to be set inactive by the roster sync
     * 
     * @param con Connection to club database
     * @return Count of members who are about to be set inactive
     */
    private static int checkSyncSetInactCount(Connection con) {

        Statement stmt = null;
        ResultSet rs = null;

        int setInactCount = 0;
        
        try {

            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT count(*) FROM member2b WHERE last_sync_date != DATE(now()) && last_sync_date != '0000-00-00' && inact = 0");

            if (rs.next()) {
                setInactCount = rs.getInt(1);
            }

        } catch (Exception exc) {
            Utilities.logError("Utilities.checkSetInactCount - Error getting count of those to be set inactive - ERR: " + exc.toString());            
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}
        }

        return setInactCount;
    }
    

 // *********************************************************
 //  Set Roster Sync indicator in Club table
 // *********************************************************

 private final static void setRSind(Connection con, String club) {


   //
   //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
         "UPDATE club5 SET rsync = 1");

      pstmt.clearParameters();
      pstmt.executeUpdate();

      pstmt.close();

   }
   catch (Exception exc) {
   }

 }

 
}
