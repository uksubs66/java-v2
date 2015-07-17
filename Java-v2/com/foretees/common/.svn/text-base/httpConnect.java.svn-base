/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

/**
 *
 * @author Owner
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
 
import javax.net.ssl.HttpsURLConnection;
 
public class httpConnect {
 
	private static final String USER_AGENT = "Mozilla/5.0";
 
        public static final String[] keyVal(String key, String value){
            
            return new String[]{key, value};
            
        }
        
        public static final String[] keyVal(String key, Integer value){
            
            return new String[]{key, value.toString()};
            
        }
        
        public static String[] flattenParams(List<String[]> params){
            
            List<String> flatParams = new ArrayList<String>();
            
            String[] param;
            
            for(int i = 0; i < params.size(); i++){
                param = params.get(i);
                flatParams.add(param[0]);
                flatParams.add(param[1]);
            }
            
            return flatParams.toArray(new String[flatParams.size()]);
 
        }
        
        public static String getUri(String[] params) {
            return getUri(params, false);
        }
        
        public static String getUri(String[] params, boolean cacheBuster) {
            
            StringBuilder uri = new StringBuilder();
            
            for (int i = 0; i < params.length; i += 2) {
                if (params[i] != null) {
                    if (i > 0) {
                        uri.append("&");
                    }
                    uri.append(Utilities.URLEncode(params[i]));
                    if (i + 1 < params.length && params[i + 1] != null) {
                        uri.append("=");
                        uri.append(Utilities.URLEncode(params[i + 1]));
                    }
                }
            }
            if(cacheBuster){
                if (uri.length() > 0) {
                    uri.append("&");
                }
                uri.append("_");
                uri.append(timeUtil.getCurrentUnixTime());
            }
            
            return uri.toString();
            
        }
        
        // HTTP GET request
	public static final String get(String url, List<String[]> params) {
            return get(url, flattenParams(params));
        }
 
	// HTTP GET request
	public static final String get(String url, String[] params) {
                
                String uri = getUri(params);
                String join = uri.isEmpty()?"":(url.indexOf("?")>-1?"&":"?");
                
                try{
                    URL obj = new URL(url+join+uri);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("GET");

                    //add request header
                    con.setRequestProperty("User-Agent", USER_AGENT);

                    //int responseCode = con.getResponseCode();
                    //System.out.println("\nSending 'GET' request to URL : " + url);
                    //System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                    }
                    in.close();

                    //print result
                    return response.toString();
                } catch(Exception exc) {
                    Utilities.logError("httpConnect.get: Url="+url.toString()+uri.toString()+" Err=" + exc.toString());
                    return null;
                }
 
	}
        
        // HTTP POST request
	public static final String post(String url, List<String[]> params) {
            return post(url, flattenParams(params));
        }
 
	// HTTP POST request
	public static final String post(String url, String[] params) {
 
                String uri = getUri(params);

                //Utilities.logDebug( "JGK2","httpConnect.post connect:"+url+"?"+uri );
                
                try {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    //HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

                    //add reuqest header
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", USER_AGENT);
                    //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
                    con.setRequestProperty("charset", "utf-8");
                    con.setRequestProperty("Content-Length", Integer.toString(uri.toString().getBytes().length));
                    con.setUseCaches (false);
                    // Send post request
                    
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(uri.toString());
                    wr.flush();
                    wr.close();

                    //int responseCode = con.getResponseCode();
                    //System.out.println("\nSending 'POST' request to URL : " + url);
                    //System.out.println("Post parameters : " + uri.toString());
                    //System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                    }
                    in.close();
                    con.disconnect();

                    //Utilities.logDebug( "JGK2","httpConnect.post response:"+response.toString() );
                    
                    return response.toString();
                } catch(Exception exc) {
                    Utilities.logError("httpConnect.post: Url="+url.toString()+" Err=" + exc.toString());
                    return null;
                }
 
	}
 
}