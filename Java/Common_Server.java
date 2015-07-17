/***************************************************************************************
 *   Common_Server:  This class defines constants used to maintain multiple servers all
 *                   running the ForeTees application.
 *
 *
 *   created: 3/01/2006   Bob
 *
 *   last updated:       ******* keep this accurate *******
 *
 *                  09-17-08  Moved to common.ProcessConstants
 *                  08-14-08  This value is no longer SET in this class but rather in
 *                            the command line that starts tomcat (-Dserver_id=#)
 *
 ***************************************************************************************
 */


//import java.util.Properties;
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
}