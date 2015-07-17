/***************************************************************************************
 *   Common_ghin:   This servlet contains all the common process for the ghin scorelink
 *
 *
 *   Called by:     Joe 
 *
 *
 *
 *   Created:       09/21/2006
 *
 *
 *   Revisions:  
 *
 *                  
 *                  
 ***************************************************************************************
 */


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.net.*;
import java.lang.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;


import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;


public class Common_ghin extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;
    static String VASP = "t7pv5";
    
 private static Document getDocument(DocumentBuilder builder, String urlString) {

    try {

        URL url = new URL( urlString );

        try {

            URLConnection URLconnection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;

            int responseCode = httpConnection.getResponseCode();
            
            if ( responseCode == HttpURLConnection.HTTP_OK) {

                InputStream in = httpConnection.getInputStream();

                try {
                    Document doc = builder.parse(in);
                    return doc;
                } catch(org.xml.sax.SAXException e) {
                    e.printStackTrace() ;
                }
                
            } else {

                System.out.println( "HTTP connection response != HTTP_OK" );
            }
        } catch ( IOException e ) { 
            e.printStackTrace();
        }
    } catch ( MalformedURLException e ) {  
        e.printStackTrace ( ) ;
    }
    
    return null;
}
 
 
 public static int postScore(String pGhinNum, long pDate, int pClubparmID, int pScore, String pType, int pHoles, PrintWriter out) {

    String [] attrNames = null;
    attrNames = new String [1];
    attrNames[0] = "all";
    //attrNames[1] = "Success";
    //attrNames[1] = "Message";
    //attrNames[2] = "Test";
    processDocument("Login", attrNames, out); // TPPComm  Login
    
    return -2;
 }
 
 
 public static void processDocument(String elementName, String [] attrNames, PrintWriter out) {
    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    try {

        //String urlString = "http://mt5493.ghinconnect.com/ghin/iihbp52.cgi?playno=3096020&datein=20070131&ratein=73.1&slpin=131&scrin=79&typein=T&holin=1&clubno=famou&postassoc=56&postclub=995&vasp=t7pv5&course=Newton+C+C";
        //String urlString = "http://www.foretees.com/postTest.htm";
        String urlString = "http://www.foretees.com/inquiryTest.htm";
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = getDocument(builder, urlString);

        if (document != null) {

            Node [] matchNodes = getAttributes(document, elementName, attrNames, out);

            if (matchNodes != null) {
                
                out.println("<br>matchNodes.length="+matchNodes.length);
                //printNodes(matchNodes, out);
                
                for ( int i = 0; i < matchNodes.length; i++ ) {

                    out.print("<br>");
                    out.print( matchNodes[i].getNodeName() + " = " + matchNodes[i].getNodeValue() );
                }
    
            } else {
                out.println("Element \"" + elementName + "\" not found in document.");
            }
            
        } else {
            
            out.println("No XML created from URL=" + urlString);
        }
            
  } catch (ParserConfigurationException e) { 
      e.printStackTrace();
  }
}
    
    
 public static Node [] getAttributes(Document document, String elementName, String [] attrNames, PrintWriter out) {
    
    // Get elements with the given tag name
    // (matches on * too)
    NodeList nodes = document.getElementsByTagName(elementName);
    if (nodes.getLength() < 1) return null;
    out.println(elementName + " node count is " + nodes.getLength());
    
    Node firstElement = nodes.item(0);
    NamedNodeMap nnm = firstElement.getAttributes();
   
    if (nnm != null) {
        
        Node [] matchNodes = new Node[attrNames.length];
        
        out.println("<br>attrNames.length = " + attrNames.length);

        for (int i = 0; i < attrNames.length; i++) {

            boolean all = attrNames[i].equalsIgnoreCase("all");
            if (all) {
                // named node map
                int nnmLength = nnm.getLength();
                out.println("<br>Looking for all.<br>nnmLength=" + nnmLength);
                matchNodes = new Node[ nnmLength ];

                for ( int j = 0; j < nnmLength; j++) {
                    matchNodes[j] = nnm.item(j);
                }
                return matchNodes;

            } else {

                matchNodes[i] = nnm.getNamedItem(attrNames[i]);
                if ( matchNodes[i] == null ) {
                    matchNodes[i] = document.createAttribute(attrNames[i]);
                    ((Attr)matchNodes[i]).setValue("?");
                }
            } // if

        } // for
      
      return matchNodes;
   }
   
   return null;
}
 

 public static void printNodes(Node [] nodes, PrintWriter out) {

    // walk through the nodes
    for ( int i = 0; i < nodes.length; i++ ) {
        
        Node node = nodes[i];
        out.print( "<br>" );
        out.print( node.getNodeName() + "=" + node.getNodeValue() );
        
    }

}
 
 
 public static int postScoreTest(String pGhinNum, long pDate, int pClubparmID, int pScore, String pType, int pHoles, PrintWriter out) {
    
    String fill = "0";
    
    String request = "http://www.foretees.com" +
                 "/test.php?" +
            "playno="+pGhinNum+"&" +
            "datein="+pDate+"&" +
            "ratein="+fill+"&" +
            "slpin="+fill+"&" +
            "srcin="+pScore+"&" +
            "typein="+pType+"&" +
            "holin="+pHoles+"&" +
            "clubno="+fill+"&" +
            "postassoc="+fill+"&" +
            "postclub="+fill+"&" +
            "vasp="+VASP;
    
    String agent = "Mozilla/4.0";    
    int rc = -1;
    int i = 0;
    DataOutputStream dataOut;
    DataInputStream dataIn;
    String line;
    StringBuffer buffer;
    
    HttpURLConnection conn = null;

    out.println("<p>request=" + request + "</p>");
    
    try {
        
            out.println("<br>Attempting.");
            URL url = new URL( request );
            //conn = (HttpURLConnection) Connector.open( url );
            conn = (HttpURLConnection) url.openConnection();
            out.println("<br>Connected?");
            conn.setRequestProperty( "User-Agent", agent );
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Length", "0" );
            //conn.setRequestProperty("Transfer-Encoding", "chunked" );
            out.println("<br>Done setting.");

/*            // not fully working
            DataOutputStream dout = new DataOutputStream( conn.getOutputStream() );
            dout.writeBytes( "" );
            dout.flush();
            dout.close();
            BufferedReader reader = new BufferedReader( new InputStreamReader( conn.getInputStream() ));
            String response = reader.readLine();

            while( null != response ) {
                out.println("<br>Reading line.");
                System.out.println( response );
                response = reader.readLine();
            }
*/
            
            
            
/*            // this works
            rc = conn.getResponseCode();
            out.println("<br>Response Code = " + rc);
            InputStream responseBody = conn.getInputStream();
            
            buffer = new StringBuffer();
            //responseBody = conn.getInputStream();
            dataIn = new DataInputStream(responseBody);
            while ((line = dataIn.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
                i++;
            }
            
            out.println(buffer.toString());
*/
            
    } 
    catch ( MalformedURLException e ) { 
            out.println("<br>MalformedURLException Error = " + e.toString());
    }
    catch ( IOException e ) { 
            out.println("<br>IOException Error = " + e.toString());
    }
    
    
    
    /*
    url = new URL ("http://www.foretees.com/test.php");
    
    try { urlConn = url.openConnection(); }
    catch(Exception e) { out.println("Can not connect, " + e.toString()); out.close(); return; }
    if (urlConn == null) { out.println("Could not connect."); out.close(); return; }
    
    urlConn.setDoInput(true);
    urlConn.setDoOutput(true);
    urlConn.setUseCaches(false);
    urlConn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
    dataOut = new DataOutputStream(urlConn.getOutputStream());
    String content;
    content = "name=" + URLEncoder.encode("Paul Thomas", "UTF-8");
    content += "&id=" + URLEncoder.encode("72", "UTF-8");
    dataOut.writeBytes(content);
    dataOut.flush();
    dataOut.close();
    dataIn = new DataInputStream(urlConn.getInputStream());
    String tmp;
    
    while ((tmp = dataIn.readLine()) != null) { out.println(tmp); }
    
    dataIn.close();
    */
    return i;
 }
 
}