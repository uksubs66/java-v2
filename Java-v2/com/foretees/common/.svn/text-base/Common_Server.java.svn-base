/***************************************************************************************
 *   Common_Server:  This class defines constants used to maintain multiple servers all
 *                   running the ForeTees application.
 *
 *
 *   created: 3/01/2006   Bob
 *
 *   last updated:       ******* keep this accurate *******
 *
 *                  04-30-13  Add preliminary CDN support
 *                  09-17-08  Moved to common.ProcessConstants
 *                  08-14-08  This value is no longer SET in this class but rather in
 *                            the command line that starts tomcat (-Dserver_id=#)
 *
 ***************************************************************************************
 */

package com.foretees.common;

import com.foretees.common.ProcessConstants;


public class Common_Server {

    //
    //  Server Id values - change this for each server - MUST be unique in each server!!!!!!!!!!!!
    //
    //  This value can be displayed and changed by Support_serverid from any server.
    //

    //static Properties props = System.getProperties();
    //public static int SERVER_ID = Integer.parseInt(props.getProperty("server_id"));
    
    public static int SERVER_ID = ProcessConstants.SERVER_ID;

    public static String [] ftCDN; // global array of content hosts

    public static int ftCDN_hosts = -1;
    public static int ftCDN_index = 0;

    private static boolean initialized = false;

    public static void init() {

        
        int hosts_found = 0;

        // fake it for staging
        if (Common_Server.SERVER_ID == 50) {

            // load these from v5.cdn_hosts
            hosts_found = 3;

            // once we know how many we have then we get our array
            ftCDN = new String[hosts_found];

            ftCDN[0] = "http://192.30.32.146";
            ftCDN[1] = "http://dev.foretees.com";
            ftCDN[2] = "http://staging.foretees.com";

        } else {

            ftCDN = new String[1];

            ftCDN[0] = "";
        }

        ftCDN_hosts = hosts_found;

        initialized = true;

    }


    public static String getContentServer() {


        String baseURL = "";
        
        // Disable CDN
        if(true == true){
            return baseURL;
        }

        // make sure the init has run
        if (ftCDN_hosts == -1) init();

        // if no hosts were found then skip
        if (ftCDN_hosts > 0) {

            final int index = ftCDN_index;

            if (Common_Server.SERVER_ID == 50 && initialized) {

                // testing enviroment
                baseURL = ftCDN[index];

                if (index == ftCDN_index) {

                    // still the same or came back around - bump it
                    ftCDN_index = (index == (ftCDN_hosts - 1) || ftCDN_index >= ftCDN_hosts) ? 0 : ftCDN_index + 1;

                } else {

                    // it's already been bumped - do nothing

                }

            } else {

                // production enviroment
                //baseURL = "http://192.30.32.146";

            }

        } // end if hosts found

        return baseURL;

    }

}