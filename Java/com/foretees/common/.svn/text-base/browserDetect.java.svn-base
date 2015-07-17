/***************************************************************************************
 *   browserDetect:  This bean will enumerate a browser definition
 *
 *       called by:  
 *
 *
 *   created:  7/28/2006   Paul
 *
 *
 *   last updated:
 *
 *
 ***************************************************************************************
 */


package com.foretees.common;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;

public final class browserDetect implements Serializable {

	private HttpServletRequest request = null;
	private String userAgent = null;
	private boolean pda = false;
	private boolean ie = false;
	private boolean op = false;
	private boolean ns4 = false;
	private boolean moz = false;
	private boolean konq = false;
	private boolean saf = false;


	public void setRequest(HttpServletRequest req) {
		request = req;
		userAgent = request.getHeader("User-Agent");
		String user = userAgent.toLowerCase();
		if(user.indexOf("msie") != -1) {
			ie = true;
		} else if(user.indexOf("opera") != -1) {
			op = true;
		} else if(user.indexOf("mozilla/4") != -1) {
			ns4 = true;
		} else if(user.indexOf("gecko") != -1) {
			moz = true;
		} else if(user.indexOf("konqueror") != -1) {
			konq = true;
		} else if(user.indexOf("safari") != -1) {
			saf = true;
		}

		// check to see if it's a mobile device
		if( (user.indexOf("windows ce") != -1) || (user.indexOf("smartphone") != -1) )
			pda = true;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public boolean isPDA() {
		return pda;
	}

	public boolean isIE() {
		return ie;
	}

	public boolean isOpera() {
		return op;
	}

	public boolean isNS4() {
		return ns4;
	}

	public boolean isMoz() {
		return moz;
	}

}