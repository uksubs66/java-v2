/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Owner
 */
public class uaUtil {

    public static boolean isMobile(HttpServletRequest req) {
        if (req != null) {
            return getRequestHeader(req, "User-Agent", "").matches(
                    "(.*)(Mobile|iP(hone|od|ad)|Android|Touch|BlackBerry|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune)(.*)");
        } else {
            return false;
        }
    }

    public static boolean isUnsupportedBrowser(HttpServletRequest req) {
        // Web tv or other browsers
        if (req != null) {
            return getRequestHeader(req, "User-Agent", "").matches(
                    "MSIE 4.0");
        } else {
            return false;
        }
    }
    
    public static String getUnsupportedMessage(HttpServletRequest req) {

        // Check browsers that we don't support, and return a message on what to do
        String ua = getRequestHeader(req, "User-Agent", "");
        
        htmlTags tag = new htmlTags(req);

        if(ua.matches("(.*)MSIE 7\\.(.*)IEMobile(.*)")
        ){
            // Windows Phone 7.0/7.1
            return tag.getTag("div","<h2>We've detected that you are using Windows Phone 7.0.</h2>  "
                    + "<p>ForeTee's mobile interface does not work with Windows Phone 7.0, "
                    + "however you can <b>update your existing device to Windows 7.8</b>.</p> "
                    + "<p><a href=\"https://www.windowsphone.com/en-US/How-to/wp7/update-central\" target=\"_blank\"></p>"
                    + "<p>Please review Microsoft's instructions on updating your operating system here</a>.</p>"
                    + "<p>You may continue using ForeTees' standard interface, "
                    + "but we strongly recommend updating your operating system.</p>", "unsupportedDevice");
        }
        if(ua.matches("(.*)MSIE 6\\.(.*)IEMobile(.*)")
        ){
            // Windows Phone 6.x
            return tag.getTag("div","<h2>We've detected that you are using an unsupported mobile device.</h2>"
                    + "<p>You may attempt using ForeTees below, "
                    + "but we strongly recomend updating your device.</p>", "unsupportedDevice");
        }
        
        return null;

    }

    public static String getRequestHeader(HttpServletRequest req, String header, String def) {
        if (req != null) {
            if (req.getHeader(header) != null) {
                return req.getHeader(header);
            } else {
                return def;
            }
        } else {
            return def;
        }
    }
}
