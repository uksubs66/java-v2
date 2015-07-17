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
import javax.activation.*;
import com.foretees.common.FeedBack;
import com.foretees.common.Utilities;
import com.foretees.common.sendEmail;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;


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
   ResultSet rs = null;


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
   String mobile = "";
   String primary = "";
   String active = "";
   String entity_id = "";
   

   String mship2 = ""; // used to tell if match was found

   float u_hcap = 0;
   float c_hcap = 0;
   int birth = 0;

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

   float u_hcap_old = 0;
   float c_hcap_old = 0;
   int birth_old = 0;

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

   float u_hcap_new = 0;
   float c_hcap_new = 0;
   int birth_new = 0;
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
   boolean useflexid = false;
   
   String mNum_curr = "";
   int depCount = 0;


   SystemUtils.logErrorToFile("FlexScape Premier: Error log for " + club + "\nStart time: " + new java.util.Date().toString() + "\n", club, false);

   try {

      BufferedReader br = new BufferedReader(isr);

      while (true) {

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

            
            //  Remove the dbl quotes and check for embedded commas

            line = cleanRecord4( line );       // insert a ? if 2 commas found w/o data between them
            line = cleanRecord2( line );       // remove double quotes and embedded commas
            line = cleanRecord4( line );       // check for empty fields again - insert ? between 2 consecutive commas

            rcount++;                          // count the records

            //  parse the line to gather all the info

            StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

            if ( tok.countTokens() > 10 ) {     // enough data ?

                flexid = tok.nextToken();
                memid = tok.nextToken();
                tok.nextToken();          // eat this value, not used
                fname = tok.nextToken();
                mi = tok.nextToken();
                lname = tok.nextToken();
                gender = tok.nextToken();
                email = tok.nextToken();
                email2 = tok.nextToken();
                phone = tok.nextToken();
                phone2 = tok.nextToken();
                temp = tok.nextToken();             // usr_birthday column
                primary = tok.nextToken();          // usr_relationship column
                mship = tok.nextToken();            // grp_name column

                if (tok.countTokens() > 0) {

                    entity_id = tok.nextToken();    // entity_id col - if provided

                } else {

                    entity_id = "";
                }

                mNum = "";
                suffix = "";
                mtype = "";
                email2 = "";
                bag = "";
                ghin = "";
                u_hndcp = "";
                c_hndcp = "";
                posid = "";
                mobile = "";
                active = "";

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
                if (temp.equals( "?" ) || temp.equals( "0" )) {

                    temp = "";
                }
                if (primary.equals( "?" )) {

                    primary = "";
                }
                if (entity_id.equals( "?" )) {

                    entity_id = "";
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
                    birth = 0;

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
                                 SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
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
                         
                         mship = entity_id;
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
                             } else if (primary.equals("2") || primary.equals("3") || primary.equals("4") || primary.equals("5") || primary.equals("6")) {
                                 
                                 if (gender.equals("F")) {
                                     mtype = "Junior Female";
                                 } else {
                                     mtype = "Junior Male";
                                 }
                                 
                                 if (primary.equals("2")) {
                                     memid += "C";
                                 } else if (primary.equals("3")) {
                                     memid += "D";
                                 } else if (primary.equals("4")) {
                                     memid += "E";
                                 } else if (primary.equals("5")) {
                                     memid += "F";
                                 } else if (primary.equals("6")) {
                                     memid += "G";
                                 }
                             }
                             
                             if (mship.equalsIgnoreCase("Designee Golf") || mship.equalsIgnoreCase("Equity")) {
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
                                     mtype = "Primary Male";
                                 } else {
                                     gender = "M";
                                     mtype = "Primary Female";
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
                                     || mship.equalsIgnoreCase("Proprietary") || mship.equalsIgnoreCase("Subscription")) {
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
                     mNum_old = "";
                     posid_old = "";
                     phone_old = "";
                     phone2_old = "";
                     birth_old = 0;
                     flexid_old = "";

                     memidChanged = false;
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
                     if (!flexid.equals( "" )) {
                         
                         flexid = truncate(flexid, 15);
                     }


                     
                     //
                     //   Always use the flexid 
                     //
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
                        mNum_old = rs.getString("memNum");
                        birth_old = rs.getInt("birth");
                        posid_old = rs.getString("posid");
                        phone_old = rs.getString("phone1");
                        phone2_old = rs.getString("phone2");
                     }
                     pstmt2.close();              // close the stmt

                     if (!fname_old.equals( "" )) {            // if member found

                        changed = false;                       // init change indicator

                        memid_new = memid_old;

                        /*
                        if (club.equals("foresthighlands")) {       // don't change usernames (initial setup, can remove later if desired)
                            memid = memid_old;
                        }
                        * 
                        */

                        if (!memid.equals( memid_old ) && !club.equals("tontoverde")) {       // if username has changed

                           memid_new = memid;                   // use new memid
                           changed = true;
                           memidChanged = true;
                        }

                        lname_new = lname_old;

                        if (!lname.equals( "" ) && !lname_old.equals( lname )) {

                           lname_new = lname;         // set value from Flexscape record
                           changed = true;
                        }

                        fname_new = fname_old;

                        fname = fname_old;         // DO NOT change first names

/*
                        if (!fname.equals( "" ) && !fname_old.equals( fname )) {

                           fname_new = fname;         // set value from Flexscape record
                           changed = true;
                        }
*/
                        if (club.equals("foresthighlands") || club.equals("pinehurstcountryclubs")) {
                            mi = mi_old;
                        }

                        mi_new = mi_old;

                        if (!mi.equals( "" ) && !mi_old.equals( mi )) {

                           mi_new = mi;         // set value from Flexscape record
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

                        if ((club.equals("missionviejo") || club.equals("pinehurstcountryclub")) && !mtype_old.equals("")) {
                            mtype = mtype_old;
                        }
                        
                        mtype_new = mtype_old;

                        if (!mtype.equals( "" ) && !mtype_old.equals( mtype )) {
                            mtype_new = mtype;         // set value from Flexscape record
                            changed = true;
                        }

                        if (birth > 0 && birth != birth_old) {

                           birth_new = birth;         // set value from Flexscape record
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

                        if (club.equals("coloradospringscountryclub") || club.equals("oceanreef") || club.equals("hillwoodcc")) {
                            email = email_old;
                        }

                        if (!email.equals( "" ) && !email_old.equals( email )) {

                           email_new = email;         // set value from Flexscape record
                           changed = true;
                        }

                        email2_new = email2_old;

                        // Don't change email2 for these clubs
                        if (club.equals("parkmeadowscc") || club.equals("tontoverde") || club.equals("missionviejo") || club.equals("pinehurstcountryclub")) {
                            email2 = email2_old;
                        }

                        if (!email2.equals( "" ) && !email2_old.equals( email2 )) {

                           email2_new = email2;         // set value from Flexscape record
                           changed = true;
                        }

                        // don't allow both emails to be the same
                        if (email_new.equalsIgnoreCase(email2_new)) email2_new = "";

                        //
                        //  NOTE:  mNums can change for this club!!
                        //
                        //         DO NOT change the flexid!!!!!!!!!
                        //
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
                           "UPDATE member2b SET username = ?, name_last = ?, name_first = ?, " +
                           "name_mi = ?, m_ship = ?, m_type = ?, email = ?, " +
                           "memNum = ?, birth = ?, posid = ?, phone1 = ?, " +
                           "phone2 = ?, inact = 0, last_sync_date = now(), gender = ? " +
                           "WHERE flexid = ?");

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
                           pstmt2.setString(10, posid_new);
                           pstmt2.setString(11, phone_new);
                           pstmt2.setString(12, phone2_new);
                           pstmt2.setString(13, gender);
                           pstmt2.setString(14, flexid);
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
                        if (memidChanged == true) {

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
                        
                        //
                        //  New member - first check if name already exists
                        //
                        boolean dup = false;

                        pstmt2 = con.prepareStatement (
                                 "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                        pstmt2.clearParameters();
                        pstmt2.setString(1, lname);
                        pstmt2.setString(2, fname);
                        pstmt2.setString(3, mi);
                        rs = pstmt2.executeQuery();            // execute the prepared stmt

                        if (rs.next()) {

                           dup = true;
                        }
                        pstmt2.close();              // close the stmt

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
                                 "webid, last_sync_date, gender, flexid) " +
                                 "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,'',?,?,?,'',?,'',now(),?,?)");

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
                              pstmt2.executeUpdate();          // execute the prepared stmt

                              pstmt2.close();              // close the stmt

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

      }   // end of while

      //
      //  Done with this file for this club - now set any members that were excluded from this file inactive
      //
      if (found == true) {       // if we processed this club
          
         // Unless manually force-overridden, check to see if more than 20 members are about to be set inactive, and if so, log an error and SKIP setting them inactive.
         if (!forceSetInact) setInactCount = checkSyncSetInactCount(con);

         if (setInactCount < setInactLimit) {

              // Set anyone inactive that didn't sync, but has at one point.
              pstmt2 = con.prepareStatement (
                      "UPDATE member2b SET inact = 1 " +
                      "WHERE last_sync_date != DATE(now()) AND last_sync_date != '0000-00-00' AND inact = 0");

              pstmt2.clearParameters();        // clear the parms
              pstmt2.executeUpdate();

              pstmt2.close();              // close the stmt
              
         } else {
             Utilities.logDebug("BSK", "flexSync - Roster Sync for (" + club + ") was about to set (" + setInactCount + ") members inactive. Action skipped. Contact club to verify.");    // Temp to make it easier to spot
             Utilities.logError("flexSync - Roster Sync for (" + club + ") was about to set (" + setInactCount + ") members inactive. Action skipped. Contact club to verify.");
         }


          //
          //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
          //
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