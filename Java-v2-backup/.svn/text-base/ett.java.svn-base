/***************************************************************************************
 *   Purpose: This servlet is used to test sending emails from within ForeTees
 *
 *
 *   Notes:
 *                  
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import javax.naming.*;
import java.util.*;
import java.sql.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.io.*;

//import org.apache.commons.lang.*;

import com.foretees.common.Connect;
import com.foretees.common.Utilities;
import com.foretees.common.sendEmail;
import com.foretees.common.parmPOS;
import com.foretees.common.TimedSocket;
import com.foretees.common.AESencrypt;

//import com.foretees.common.BasicSHA256;
//import java.security.SecureRandom;

//import javax.xml.parsers.*;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceRef;
import javax.xml.namespace.QName;
import java.net.URL;

import java.net.Socket;
import java.net.ServerSocket;

import org.ibsservices.*; // IBS

import sun.misc.BASE64Decoder;


import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;

import com.ngn.services._2007._03._20.headerdata.Credentials;
import com.ngn.services._2007._03._20.memberservice.MemberService;
import com.ngn.services._2007._03._20.memberservice.MemberService_Service;
import com.ngn.services._2007._03._20.memberservice.UpdateMembersResponse;
import com.ngn.services._2007._03._20.memberservice.FetchMembersByClubRequest;
import com.ngn.services._2007._03._20.memberservice.FetchMembersResponse;
import com.ngn.services._2007._03._20.memberdata.MemberList;
import com.ngn.services._2007._03._20.memberdata.MemberInfo;
import com.ngn.services._2007._03._20.memberdata.ArrayOfMemberHandicapInfo;
import com.ngn.services._2007._03._20.memberdata.MemberHandicapInfo;
import com.foretees.common.Connect;


public class ett extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    if (req.getParameter("abacus") != null) {
        
        abacus(out);
        return;

    } else if (req.getParameter("tinymce") != null) {

        tinymce(req, out);
        return;
        
    }



    if (req.getParameter("asdf") != null) {

        File file=new File("//usr//local//tomcat//webapps//demov4//caddies.csv");

        boolean exists = file.exists();

        if (exists) {

            out.println("File found.");

        } else {

            out.println("Not found.");

        }

        return;

    }


    if (req.getParameter("todo") != null && req.getParameter("todo").equals("gn21")) {

        doGolfNetTest(req, resp, out);
        return;
    }




    if (req.getParameter("todo") != null && req.getParameter("todo").equals("aes")) {

        String text = "Here is the new text that we are going to try.";
        String textDec = "";
        byte[] textEnc = null;

        String md5 = AESencrypt.getMD5("thishouldbemd5ed").toString();

        Long keyTime = (System.currentTimeMillis() / 1000L)/3600; // get hours since Epoch
        
        String key = AESencrypt.encryptionKey + keyTime.toString();

        //BASE64Encoder base64encoder = new BASE64Encoder();
        //byte[] decoded = base64encoder.decodeBuffer( encoded );

        try {
            //textEnc = AESencrypt.encrypt(text, key);
        } catch (Exception exc) {

            out.println("<p>ERR encrypt: " + exc.toString() + "</p>");
        }

        //BASE64Decoder base64decoder = new BASE64Decoder();
        //byte[] decoded = base64decoder.decodeBuffer( encoded );

        try {
            //textDec = AESencrypt.decrypt(textEnc, key);
        } catch (Exception exc) {

            out.println("<p>ERR decrypt: " + exc.toString() + "</p>");
        }

        try {
        out.println("<br>Plain Text : " + text);
        out.println("<br>MD5 : " + md5);
        out.println("<br>Encrypted Text : " + textEnc.toString());
        } catch (Exception exc) {

            out.println("<p>ERR decrypt: " + exc.toString() + "</p>");
        }
        try {
        out.println("<br>Decrypted Text : " + textDec);
        } catch (Exception exc) {

            out.println("<p>ERR decrypt: " + exc.toString() + "</p>");
        }

    }


/*
    if (req.getParameter("todo") != null && req.getParameter("todo").equals("getImage")) {

        Connection con = null;
          Statement stmt = null;
          ResultSet rs = null;
          Blob len1 = null;

          try {

             con = dbConn.Connect("demov4");
             stmt = con.createStatement();
             rs = stmt.executeQuery("SELECT * FROM member_photos WHERE id = 1");

             if (rs.next()) {

                len1 = rs.getBlob("photo");

                int len = (int)len1.length();
                byte [] b = new byte[len];
                InputStream readImg = rs.getBinaryStream(1);

                int index=readImg.read(b, 0, len);
                //System.out.println("index"+index);
                resp.reset();
                resp.setContentType("image/jpg");
                resp.getOutputStream().write(b,0,len);
                resp.getOutputStream().flush();

             }

             stmt.close();

          } catch (Exception e1) {

              //Utilities.logError("DinReq Error in sendEmail.sendIt() for " + club + ": " + e1.getMessage() + ", " + e1.toString());

          } finally {

              try { rs.close(); }
              catch (SQLException ignored) {}

              try { stmt.close(); }
              catch (SQLException ignored) {}
          }


        return;

    }
*/
    
      
    //resp.setContentType("text/html");
    //PrintWriter out = resp.getWriter();
 
    out.println("<html>");
    out.println("<head>");
    out.println("<title>4T-ETT</title>");
    out.println("</head>");
    out.println("");
    out.println("");


    if (req.getParameter("todo") != null && req.getParameter("todo").equals("runjob-asdf")) {

    //
    // JOB TO ENSURE ALL CHILD RESERVATIONS ARE MARKED AT MEMBER_CREATED IF THEIR PARENT IS
    //
    try {

        // get connection to dining db
        Connection con_d = Connect.getDiningCon();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs2 = null;

        int count = 0;
        int count_rr = 0;


        pstmt = con_d.prepareStatement ("" +
                        "SELECT id " +
                        "FROM reservations " +
                        "WHERE parent_id is null AND member_created = true");


        rs = pstmt.executeQuery();

        while (rs.next()) {

            count++;


            pstmt2 = con_d.prepareStatement ("" +
                            "UPDATE reservations " +
                            "SET member_created = true " +
                            "WHERE parent_id = " + rs.getString(1));


            count_rr += pstmt2.executeUpdate();




        }

        out.println("<p>count=" + count + "<br>count_rr=" + count_rr + "</p>");

        /*
        int id = 0;
        int count = 0;
        String user_identity = "";

        pstmt = con_d.prepareStatement ("" +
                        "SELECT id, user_identity " +
                        "FROM people " +
                        "WHERE id <> -123;");

        //pstmt.setInt(1, 10477);

        rs = pstmt.executeQuery();

        while (rs.next()) {

            id = rs.getInt("id");
            user_identity = rs.getString("user_identity");

            if (user_identity != null) {

                out.print("\"" + user_identity + "\" -> ");

                int pos = user_identity.indexOf(":");


                for (int i=pos; i < 10; i++) {

                    user_identity = " " + user_identity;
                }

                out.println("\"" + user_identity + "\"");


                pstmt2 = con_d.prepareStatement ("" +
                                "UPDATE people " +
                                "SET user_identity = ? " +
                                "WHERE id = ?");

                pstmt2.setString(1, user_identity);
                pstmt2.setInt(2, id);

                count = pstmt2.executeUpdate();
                
            }

        }
        */

    } catch (Exception exc) {

        out.println("<p>ERROR: " + exc.toString() + "</p>");

    }
    
    
    } // end runjob



/*
    String tmp = "Memorial Day Open Play 1pm SG You are registered for this event. If you need to make changes to your reservation, please call the golf shop at (651)770-3421 and ask for Judy.";

    out.println("<br>len=" + tmp.length());
    out.println("<br>tmp=" + tmp);
    out.println("<br><br>");
    out.println("<br>new=" + sendEmail.iCalFoldContentLine(tmp));
*/











    



    //
    // TESTING CODE FOR NEW IBS INTERFACE
    //

    boolean ok = false;

    try {
/*
      //URL url = new URL("https://webres.goibs.com/WebMemberAPITestEx/IBSWebMemberAPI.asmx");
        URL url = new URL("https://webres.goibs.com/WebMemberAPIEx/IBSWebMemberAPI.asmx");

        IBSWebMemberAPI service = null;

        service = new IBSWebMemberAPI(url, new QName("http://ibsservices.org/", "IBSWebMemberAPI"));

        IBSWebMemberAPISoap port = service.getIBSWebMemberAPISoap();

      //ok = port.areYouThere("ftuser", "test");
        ok = port.areYouThere("foretees", "baltgc");

        out.println("RESULT=" + ok);

        javax.xml.ws.Holder<String> holderMessage = new javax.xml.ws.Holder<String>();
        javax.xml.ws.Holder<String> holderResponse = new javax.xml.ws.Holder<String>();
*/

/*
        // PULL ALL MEMBER DATA
        port.getAllMembersString("ftuser", "test", "CSV", holderMessage, holderResponse);

        String [] line = holderResponse.value.split("\\n");

        String [] fields = null;
        String [] data = null;

        String memberID = "";
        String memberExt = "";
        String firstName = "";
        String middleInitial = "";
        String lastName = "";

        for (int i=1; i < line.length; i++) { // start with 1 so we skip the header row

            // reset
            memberID = "";
            memberExt = "";
            firstName = "";
            middleInitial = "";
            lastName = "";

            fields = line[i].split(",");    // get each column in to the fields string array

            // if Status (field3) is 'active' and Level (field4) is 'member' then continue
            if (fields[3].equalsIgnoreCase("Active") && fields[4].equalsIgnoreCase("Member")) {

                memberID = fields[0];
                memberExt = fields[2];

                data = fields[6].split("\\|");   // field that contains the name parts (FirstName|MiddleInitial|LastName|Suffix|Title|BirthDate|Greetings)
                
                firstName = data[0];
                middleInitial = data[1];
                lastName = data[2];

            }


        }
*/



        // PULL ALL RETAIL INVENTORY ITEMS


/*
        String tmp = "" +
            "\"Code\",\"Type\",\"Description\",\"MinimumCode|guid|code|description|type|isempty\",\"IsMinimum\",\"IsCertificate\",\"Certificate|guid|code|description|type|isempty\",\"IsOnlineGiftCard\",\"TaxIncluded\",\"LoyaltyPointsAssign|Silver|Gold|Platinum\",\"LoyaltyPointsRedeem\",\"notes\",\"AccountingCode|guid|code|description|type|isempty\",\"Stat|guid|code|description|type|isempty\",\"ItemClass|guid|code|description|type|isempty\",\"Taxes\",\"Gratuities\",\"Discounts\",\"Minimums\",\"IsTeeTimeItem\",\"Holes\",\"DefaultReservableItem\",\"UseTeeTimePrice\",\"PromptForPrice\",\"AskForMemberSwipe\",\"SellableAtPointOfSale\",\"SpecialOrderItem\",\"CreditBookItem\",\"AverageCost\",\"RetailPrice\",\"IsCreditBookItem\",\"AskGuestInfo\",\"HotKeyLookupCode\",\"Pricing|SecondLevelQuantity|SecondLevelPrice|ThirdLevelQuantity|ThirdLevelPrice|LabelExtraLine\"/n" +
            "\"100000001001\",\"InventoryItem\",\"GF 18 Public\",\"00000000-0000-0000-0000-000000000000|||Minimum|true\",\"false\",\"false\",\"00000000-0000-0000-0000-000000000000|||Certificate|true\",\"false\",\"false\",\"0|0|0\",\"0\",\"\",\"5f762d9b-6772-4923-be2d-4a758241d61a|4010-01|Green Fees||false\",\"9a5d451f-1f93-4a6d-aed4-b30513c07ec0|1000|18 Public||false\",\"e07d8564-bda1-48b8-a554-459c4b79c69e|AA|18 Green Fees||false\",\"\",\"\",\"\",\"\",\"false\",\"0\",\"false\",\"false\",\"false\",\"false\",\"false\",\"false\",\"false\",\"0\",\"0\",\"false\",\"false\",\"\",\"0|0|0|0|\"/n" +
            "\"100000001002\",\"InventoryItem\",\"GF 18 Public\",\"00000000-0000-0000-0000-000000000000|||Minimum|true\",\"false\",\"false\",\"00000000-0000-0000-0000-000000000000|||Certificate|true\",\"false\",\"false\",\"0|0|0\",\"0\",\"\",\"5f762d9b-6772-4923-be2d-4a758241d61a|4010-01|Green Fees||false\",\"9a5d451f-1f93-4a6d-aed4-b30513c07ec0|1000|18 Public||false\",\"e07d8564-bda1-48b8-a554-459c4b79c69e|AA|18 Green Fees||false\",\"\",\"\",\"\",\"\",\"false\",\"0\",\"false\",\"false\",\"false\",\"false\",\"false\",\"false\",\"false\",\"0\",\"0\",\"false\",\"false\",\"\",\"0|0|0|0|\"/n" +
            "\"100000001003\",\"InventoryItem\",\"GF 18 Public\",\"00000000-0000-0000-0000-000000000000|||Minimum|true\",\"false\",\"false\",\"00000000-0000-0000-0000-000000000000|||Certificate|true\",\"false\",\"false\",\"0|0|0\",\"0\",\"\",\"5f762d9b-6772-4923-be2d-4a758241d61a|4010-01|Green Fee||false\",\"9a5d451f-1f93-4a6d-aed4-b30513c07ec0|1000|18 Public||false\",\"e07d8564-bda1-48b8-a554-459c4b79c69e|AA|18 Green Fees||false\",\"\",\"\",\"\",\"\",\"false\",\"0\",\"false\",\"false\",\"false\",\"false\",\"false\",\"false\",\"false\",\"0\",\"0\",\"false\",\"false\",\"\",\"0|0|0|0|\"/n" +
            "\"100000001004\",\"InventoryItem\",\"18 Public w/cart\",\"00000000-0000-0000-0000-000000000000|||Minimum|true\",\"false\",\"false\",\"00000000-0000-0000-0000-000000000000|||Certificate|true\",\"false\",\"false\",\"10|15|20\",\"0\",\"\",\"5f762d9b-6772-4923-be2d-4a758241d61a|4010-01|Cart Fees||false\",\"9a5d451f-1f93-4a6d-aed4-b30513c07ec0|1000|18 Public||false\",\"e07d8564-bda1-48b8-a554-459c4b79c69e|AA|18 Green Fees||false\",\"\",\"\",\"\",\"\",\"true\",\"18\",\"false\",\"true\",\"false\",\"false\",\"false\",\"false\",\"false\",\"0\",\"9.52\",\"false\",\"false\",\"\",\"0|0|0|0|\"/n";
*/
/*
        port.getAllRetailItemsString("ftuser", "test", "CSV", holderMessage, holderResponse);

        String [] line = holderResponse.value.split("\\n");

        ArrayList tmpList = new ArrayList();

        String [] fields = null;
        String [] data = null;

        for (int i=1; i < line.length; i++) { // start with 1 so we skip the header row

            fields = line[i].split(",");    // get each column in to the fields string array

            data = fields[12].split("\\|");   // field that contains the info we are looking for

            tmpList.add(data[2]);

        }

        // remove duplicates
        HashSet hashSet = new HashSet(tmpList);
        ArrayList invItems = new ArrayList(hashSet);
        Collections.sort(invItems);

        for (Object item : invItems)
            out.println("<br>" + item);
*/



        // PULL ALL DEPARTMENT CODES
        //port.getSystemCodeAllRetailDepartmentsString("ftuser", "test", "CSV", holderMessage, holderResponse);




        // PULL ALL TENDER CODES (pass the proper dept uid - from the get retail dept codes)
        //port.getTenderAllTenderMethodsForDepartmentString("ftuser", "test", "55452bac-40fd-4928-9f45-e85d48352c90", "CSV", holderMessage, holderResponse);
        
        
        //out.println("<p>Message: " + holderMessage.value + "</p>");
        //out.println("<p>Response: " + holderResponse.value + "</p>");



        
/*
        // POST A POS TRANSACTION

        String xml_batch = "" +

                "<Root>" +
                    "<Details>" +
                        "<BatchDate>2011-02-16</BatchDate>" +
                        "<BatchName>ForeTees Batch</BatchName>" +
                        "<uidDeptID>9418C948-FB79-4992-8128-C3701E4D30E0</uidDeptID>" + // 55452bac-40fd-4928-9f45-e85d48352c90
                        "<BatchDetails>" +
                            "<TranID>1</TranID>" +
                            "<MemberNumber>10001</MemberNumber>" +
                            "<MemberExtension>000</MemberExtension>" +
                            "<EmplNumber>99</EmplNumber>" +
                            "<Taxes>" +
                                "<uidTaxID>89F5BF8B-7957-42BF-81CB-975BCFD1520F</uidTaxID>" +
                                "<TaxAmount>3.46</TaxAmount>" +
                            "</Taxes>" +
                            "<Items>" +
                                "<InvNumber>3010001003</InvNumber>" +
                                "<Price>19.05</Price>" +
                                "<uidInvMenuID>2888C6D1-FE27-456D-ACEC-523E8581812E</uidInvMenuID>" +
                                "<Quantity>1</Quantity>" +
                            "</Items>" +
                            "<Items>" +
                                "<InvNumber>3010001002</InvNumber>" +
                                "<Price>16.02</Price>" +
                                "<uidInvMenuID>2888C6D1-FE27-456D-ACEC-523E8581812E</uidInvMenuID>" +
                                "<Quantity>2</Quantity>" +
                            "</Items>" +
                            "<Tenders>" +
                                "<uidTenderID>6114556D-F64F-48F3-B2F3-36C8BE4110E4</uidTenderID>" + // 0FFDFC90-4BF5-491D-82EA-74A5752180B9
                                "<TenderAmount>54.55</TenderAmount>" +
                            "</Tenders>" +
                        "</BatchDetails>" +
                    "</Details>" +
                "</Root>";
*/
/*
                            "<Items>" +
                                "<InvNumber>100400001001</InvNumber>" +
                                "<Price>19.05</Price>" +
                                "<uidInvMenuID>486044b5-4105-4fbd-af22-00b98434c321</uidInvMenuID>" +
                                "<Quantity>2</Quantity>" +
                            "</Items>" +
                                        */
/*
        javax.xml.ws.Holder<Boolean> holderResult = new javax.xml.ws.Holder<Boolean>();

        port.createTickets("ftuser", "test", xml_batch, holderMessage, holderResult);


        out.println("<p>Message: " + holderMessage.value + "</p>");
        out.println("<p>Result: " + holderResult.value + "</p>");
*/



    } catch (Exception exc) {

        out.println("<p>ERROR: " + exc.toString() + "</p>");

    }

    //out.println("RESULT=" + ok);


/*
    String pass = "password";
    //String salt = "6msWWKYm";

    String hash = "";

    try {
        hash = BasicSHA256.SHA256(pass + "gxL5oGgy");
    } catch (Exception exc) {
        out.println("<br>HASH ERROR: " + exc.toString());
    }

    out.println("<br>HASH=" + hash);


    String newSalt = "";
    
    try {
        newSalt = BasicSHA256.getSalt(6);
    } catch (Exception exc) {
        out.println("<br>SALT ERROR: " + exc.toString());
    }
    out.println("<br>SALT=" + newSalt);
    out.println("<br>\"" + newSalt + "\"");
    out.println("<br>Length=" + newSalt.length());

    char[] chars = newSalt.toCharArray();

    StringBuffer hex = new StringBuffer();

    for(int i = 0; i < chars.length; i++){
        hex.append(Integer.toHexString((int)chars[i]));
    }

    out.println("<br>" + hex.toString());
*/
    
    
/*
    SecureRandom r = new SecureRandom();
    byte[] salt = new byte[20];
    r.nextBytes(salt);


    String result = new String(salt); // salt.toString()
    
    //result = StringUtils.chomp(result);

    out.println("<br>R1=" + r.nextInt(94));
    out.println("<br>R2=" + r.nextInt(94));

    out.println("<br>SALT1=" + result);
    out.println("<br>SALT2=" + salt.toString());


    String salt_key = "";
    int salt_length = 6;
    int ascii = 0;

    do {

        ascii = 40 + r.nextInt(82);
        salt_key = salt_key + (char)ascii;

    } while (salt_key.length() < salt_length);

    out.println("<br>salt_key=" + salt_key);
*/


/*
    try {

        String lname = "Not Connected";
        int id = -1;

        Context ctx = new InitialContext();
        if(ctx == null )
            throw new Exception("Boom - No Context");

        // /jdbc/postgres is the name of the resource above
        javax.sql.DataSource ds = (javax.sql.DataSource)ctx.lookup("java:comp/env/jdbc/postgres");

        if (ds != null) {

            Connection conn = ds.getConnection();

            if(conn != null)
            {
                lname = "Got Connection "+conn.toString();
                Statement stmt = conn.createStatement();
                ResultSet rst = stmt.executeQuery("select * from people where id = 1");
                if(rst.next()) {
                    lname = rst.getString("last_name");
                    id = rst.getInt("id");
                }
                conn.close();
            } else {
                out.println("<br>conn is null!");
            }

        } else {
            out.println("<br>ds is null!");
        }

        out.println("<br>id=" + id);
        out.println("<br>lname=" + lname);

    } catch(Exception e) {
        out.println(e.toString());
    }
*/





/*  
    out.println("<script>");
    out.println("var upload_number = 2;");
    out.println("function addFileInput() {");
    out.println("if(upload_number > 3) { alert('Sorry you can only upload 3 files.'); exit(0); }");
    out.println(" var d = document.createElement(\"div\");");
    out.println(" var l = document.createElement(\"a\");");
    out.println(" var file = document.createElement(\"input\");");
    out.println(" file.setAttribute(\"type\", \"file\");");
    out.println(" file.setAttribute(\"id\", \"attachment\"+upload_number);");
    out.println(" file.setAttribute(\"name\", \"attachment\"+upload_number);");
    out.println(" l.setAttribute(\"href\", \"javascript:removeFileInput('f\"+upload_number+\"')\");");
    out.println(" l.appendChild(document.createTextNode(\"Remove\"))");
    out.println(" d.setAttribute(\"id\", \"f\"+upload_number);");
    out.println(" d.appendChild(file);");
    out.println(" d.appendChild(l);");
    out.println(" document.getElementById(\"moreUploads\").appendChild(d);");
    out.println(" upload_number++;");
    out.println("}");
    out.println("function removeFileInput(i) {");
    out.println(" var elm = document.getElementById(i);");
    out.println(" document.getElementById(\"moreUploads\").removeChild(elm);");
    out.println(" upload_number--;");
    out.println("}");
    out.println("</script>");

    out.println("<body onload=\"document.myForm.enctype='application/x-www-form-urlencoded';\">");
    out.println("");

    out.println("<FORM ENCTYPE=\"multipart/form-data\" method=post action=\"ett\" name=myForm>"); // multipart/form-data application/x-www-form-urlencoded
    //out.println("<INPUT TYPE=\"file\" NAME=\"attachment\">");
    //out.println("<INPUT TYPE=\"submit\" VALUE=\"Upload\">");
    out.println("<INPUT TYPE=\"text\" name=var1 VALUE=\"Blah Blah\">");
    out.println("<INPUT TYPE=\"hidden\" name=var2 VALUE=\"Foo Bar\">");
    out.println("<input type=\"file\" name=\"attachment1\" id=\"attachment1\" onchange=\"document.getElementById('moreUploadsLink').style.display = 'block';\">");
    out.println("<div id=\"moreUploads\"></div>");
    out.println("<div id=\"moreUploadsLink\" style=\"display:none;\"><a href=\"javascript:addFileInput();\">Attach another File</a></div>");

    out.println("<br><input type=button value=' Send ' onclick=\"sendEmail()\">");

    out.println("</FORM>");
    out.println("");
    out.println("<script>");
    out.println("function sendEmail() {");
    out.println(" document.myForm.enctype='multipart/form-data';");
    out.println(" document.myForm.submit();");
    out.println(" ");
    out.println(" ");
    out.println("}");
    out.println("</script>");
    out.println("");
    out.println("");


    out.println("<form>");

    out.println("<>");


    out.println("</FORM>");
*/


/*
    out.println("<form method=get>");

    out.println("<input type=hidden name=todo value=getImage>");
    out.println("<input type=text name=imgId value='1'>");
    out.println("<input type=submit value=Display>");

    out.println("</form>");

    out.println("<form method=post enctype=\"multipart/form-data\">");

    //out.println("<input type=hidden name=todo value=getImage>");
    out.println("<input type=file name=attachement1>");
    out.println("<input type=submit value=Upload>");

    out.println("</form>");

*/

/*
    if (req.getParameter("thefile") != null) {

        out.println("<pre>" + req.getParameter("thefile") + "</pre>");
        

    }
*/



    out.println("</body>");
    out.println("</html>");
    out.close();

 }


@SuppressWarnings("unchecked")
 public void doPost_photos(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    out.println("<html>");
    out.println("<head>");
    out.println("<title>4T-ETT</title>");
    out.println("</head>");
    out.println("<body>");

    out.println("");
    out.println("");

    if (!ServletFileUpload.isMultipartContent(req)) {
        
        out.println("No File Uploaded!"); 
        return;
    }

    ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
    servletFileUpload.setSizeMax(1024 * 512); // 512KB
    List fileItemsList = null;
    Dictionary fields = new Hashtable();

    try {

        fileItemsList = servletFileUpload.parseRequest(req);

    } catch (org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException exc) {

        out.println("ERROR: Too Big! " + exc.getMessage());
        return;

    } catch (Exception exc) {

        out.println("ERROR: " + toString());
        return;
    }

    Iterator it = fileItemsList.iterator();

    while ( it.hasNext() ) {

        FileItem fileItem = (FileItem)it.next();
        if (fileItem.isFormField()) {

            /* The file item contains a simple name-value pair of a form field */
            out.println("<br>Adding name=" + fileItem.getFieldName() + ", value=" + fileItem.getString());

            if (fileItem.getString() != null && fileItem.getFieldName() != null) {
                fields.put(fileItem.getFieldName(), (String)fileItem.getString());
            }

        } else {

            /* The file item contains an uploaded file */
            out.println("<br>filename=" + fileItem.getName() + ", fieldName=" + fileItem.getFieldName() + ", size=" + (fileItem.getSize() / 1024) + "KB");

            Connection con = null;
            PreparedStatement pstmt = null;

            try {

                con = dbConn.Connect("demov4");
                pstmt = con.prepareStatement (
                        "INSERT INTO member_photos (username, photo) VALUES ('6700', ?)");

                pstmt.clearParameters();
                pstmt.setString(1, (String)fileItem.getString());
                pstmt.executeUpdate();

            } catch (Exception e1) {

                out.println("Error: " + e1.getMessage());

            } finally {

                try { pstmt.close(); }
                catch (SQLException ignored) {}
            }

        }

    } // end while


 }

/*
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    


    out.println("<html>");
    out.println("<head>");
    out.println("<title>4T-ETT</title>");
    out.println("</head>");
    out.println("<body>");
    //out.println("<br>isMultipart=" + isMultipart);
    //out.println("<br>var1=" + req.getParameter("var1"));
    //out.println("<br>var2=" + req.getParameter("var2"));
    out.println("");
    out.println("");
    out.println("");

    boolean isMultipart = ServletFileUpload.isMultipartContent(req);
    out.println("<br>isMultipart=" + isMultipart);
    
    ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
    servletFileUpload.setSizeMax(1024 * 512); // 512KB
    List fileItemsList = null;
    Dictionary fields = new Hashtable();

    try {

        fileItemsList = servletFileUpload.parseRequest(req);
    
    } catch (org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException exc) {

        out.println("ERROR: Too Big! " + exc.getMessage());
        return;

    } catch (Exception exc) {

        out.println("ERROR: " + toString());
        return;
    }

    Iterator it = fileItemsList.iterator();

    while ( it.hasNext() ) {

        FileItem fileItem = (FileItem)it.next();
        if (fileItem.isFormField()) {

            /* The file item contains a simple name-value pair of a form field */  /*
            out.println("<br>Adding name=" + fileItem.getFieldName() + ", value=" + fileItem.getString());

            if (fileItem.getString() != null && fileItem.getFieldName() != null) {
                fields.put(fileItem.getFieldName(), (String)fileItem.getString());
            } else {
                out.println(" was null...");
            }

        } else {
            
            /* The file item contains an uploaded file */  /*
            out.println("<br>filename=" + fileItem.getName() + ", fieldName=" + fileItem.getFieldName() + ", size=" + (fileItem.getSize() / 1024) + "KB");

            try {

                //InternetHeaders headers = new InternetHeaders();
                MimeBodyPart bodyPart = new MimeBodyPart();
                DataSource ds = new ByteArrayDataSource(
                        fileItem.get(),
                        fileItem.getContentType(),fileItem.getName());
                bodyPart.setDataHandler(new DataHandler(ds));
                bodyPart.setDisposition("attachment; filename=\"" + fileItem.getName() + "\"");
                bodyPart.setFileName(fileItem.getName());

                fields.put(fileItem.getFieldName(), bodyPart);

            } catch (Exception exc) {

                out.println("ERROR2: " + toString());
            }

        }

    } // end while


    if(fields.get("var1") != null) {
        
         out.println("<br>var1=" + (String)fields.get("var1") + "");

    }


    StringBuffer vCalMsg = new StringBuffer();
    vCalMsg.append("" +
        "BEGIN:VCALENDAR\n" +
        "PRODID:-//ForeTees//NONSGML v1.0//EN\n" +
        "METHOD:PUBLISH\n" +
        "BEGIN:VEVENT\n" +
        "DTSTAMP:20100217T162000\n" +
        "DTSTART:20100301T083000\n" +
        "SUMMARY:Reservation\n" +
        "LOCATION:Demo Club\n" +
        "DESCRIPTION:Round of golf.\n" +
        "URL:http://www1.foretees.com/demov4\n" +
        "END:VEVENT\n" +
        "END:VCALENDAR");

    
    String txtBody = "Reservaton Info\n\n1: Paul Sindelar\n2: John Sindelar\n3: X\n4: TBD\n\n";

    
    String htmlBody = "<html><body>";
    htmlBody += "<h3>ForeTees</h3>";
    htmlBody += txtBody.replace("\n", "<br>");
    htmlBody += "<p><a href=\"Login?verify\">Link</a></p>";
    htmlBody += "</body></html>";
        
/*
    Dictionary fields = new Hashtable();


    if(req.getContentType() != null && req.getContentType().startsWith("multipart/form-data")) {

        out.println("<br>req.getContentType=" + req.getContentType());

        try {
            fields = getUpload(req, resp, out);
        } catch (Exception exc) {
            out.println("<br><br>ERROR: " + exc.toString());
        }

    }
*/
/*
    try {

        MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap)MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes("text/calendar ics ICS");

        MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        
        Properties properties = new Properties();
        Session mailSess;

        properties.put("mail.smtp.host", "216.243.184.88");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", "true");

        mailSess = Session.getInstance(properties, getAuthenticator("support@foretees.com", "fikd18"));

        MimeMessage message = new MimeMessage(mailSess);

        message.setFrom(new InternetAddress("auto-send@foretees.com"));
        message.setSubject( "ETT Test Message" );
        message.setSentDate(new java.util.Date());
        message.addHeader("X-Mail-Calendar-Part:", "Yes");
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("paul@foretees.com"));



        //
        // ADD PLAIN TEXT BODY
        //
        
            // If a Content-Transfer-Encoding header field appears as part of a message header,
            // it applies to the entire body of that message. If a Content-Transfer-Encoding header
            // field appears as part of a body part's headers, it applies only to the body
            // of that body part. If an entity is of type "multipart" or "message", the
            // Content-Transfer- Encoding is not permitted to have any value other than
            // a bit width (e.g., "7bit", "8bit", etc.) or "binary".


        //msgBodyPart.addHeader("Content-Transfer-Encoding", "quoted-printable");

        BodyPart txtBodyPart = new MimeBodyPart();
        txtBodyPart.setContent(txtBody, "text/plain");
        //message.setText(txtBody);


        //
        // ADD HTML BODY
        //
        BodyPart htmlBodyPart = new MimeBodyPart();
        //htmlBodyPart.addHeader("Content-Transfer-Encoding", "quoted-printable");
        htmlBodyPart.setContent(htmlBody, "text/html");

/*

        //
        // ADD CALENDAR
        //
        BodyPart icsBodyPart = new MimeBodyPart();
        //icsBodyPart.setFileName("foretees.ics"); // OUTLOOK recommends not setting filename
        //icsBodyPart.addHeader("Content-Transfer-Encoding", "7bit");
        
            // Bodyparts can be designated `attachment' to indicate that they are
            // separate from the main body of the mail message, and that their
            // display should not be automatic, but contingent upon some further
            // action of the user.

        icsBodyPart.addHeader("Content-Disposition", "attachment"); // ;filename=foretees.ics
        icsBodyPart.addHeader("Content-Class", "urn:content-classes:calendarmessage");
        
        icsBodyPart.setContent(vCalMsg.toString(), "text/calendar;method=REQUEST"); // this way the email renders properly on desktops but ical is not attachment on iPhone
        //icsBodyPart.setContent(vCalMsg.toString(), "application/octet-stream;name=foretees.ics"); // this way the email does not render properly on desktops but ical is an attachment on iPhone
        
*/
 /*
        
        Multipart mpRoot = new MimeMultipart("mixed");
        Multipart mpContent = new MimeMultipart("alternative");

        // Create a body part to house the multipart/alternative Part
        MimeBodyPart contentPartRoot = new MimeBodyPart();
        contentPartRoot.setContent(mpContent);

        // Add the root body part to the root multipart
        mpRoot.addBodyPart(contentPartRoot);

        mpContent.addBodyPart(txtBodyPart);
        mpContent.addBodyPart(htmlBodyPart);
//        mpRoot.addBodyPart(icsBodyPart);

        if(fields.get("attachment1") == null) {
             out.println("<p>No Attachment Found.</p>");
        } else {
      
            BodyPart body = new MimeBodyPart(), attachment = (BodyPart)fields.get("attachment1");
            mpRoot.addBodyPart(attachment);
        }

        if(fields.get("attachment2") != null) {

            BodyPart body = new MimeBodyPart(), attachment = (BodyPart)fields.get("attachment2");
            mpRoot.addBodyPart(attachment);

        }

        if(fields.get("attachment3") != null) {

            BodyPart body = new MimeBodyPart(), attachment = (BodyPart)fields.get("attachment3");
            mpRoot.addBodyPart(attachment);
            
        }

        message.setContent(mpRoot);
        message.saveChanges();
        
        Transport.send(message);
        
    } catch (Exception exc) {

        out.println("<br><br>Error sending. Err = " + exc.getMessage());

    } finally {

        out.println("<p>Done...</p>");

    }

    out.println("</body>");
    out.println("</html>");
    out.close();

 }
*/


/*
 private Dictionary getUpload(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
    throws IOException, MessagingException {


      String boundary = request.getHeader("Content-Type");
      int pos = boundary.indexOf('=');
      boundary = boundary.substring(pos + 1);
      boundary = "--" + boundary;
      ServletInputStream in =
         request.getInputStream();
      byte[] bytes = new byte[512];
      int state = 0;
      ByteArrayOutputStream buffer =
         new ByteArrayOutputStream();
      String name = null,
             value = null,
             filename = null,
             contentType = null;
      Dictionary fields = new Hashtable();

      int i = in.readLine(bytes,0,512);
      while(-1 != i)
      {
         String st = new String(bytes,0,i);
         if(st.startsWith(boundary))
         {
            state = 0;
            if(null != name)
            {
               if(value != null)
                  fields.put(name,
                     value.substring(0,
                           // -2 to remove CR/LF
                           value.length() - 2));
               else if(buffer.size() > 2)
               {
                  InternetHeaders headers =
                     new InternetHeaders();
                  MimeBodyPart bodyPart =
                     new MimeBodyPart();
                  DataSource ds =
                     new ByteArrayDataSource(
                        buffer.toByteArray(),
                        contentType,filename);
                  bodyPart.setDataHandler(
                     new DataHandler(ds));
                  bodyPart.setDisposition(
                     "attachment; filename=\"" +
                     filename + "\"");
                  bodyPart.setFileName(filename);
                  fields.put(name,bodyPart);
               }
               name = null;
               value = null;
               filename = null;
               contentType = null;
               buffer = new ByteArrayOutputStream();
            }
         }
         else if(st.startsWith(
            "Content-Disposition: form-data") &&
            state == 0)
         {
            StringTokenizer tokenizer =
               new StringTokenizer(st,";=\"");
            while(tokenizer.hasMoreTokens())
            {
               String token = tokenizer.nextToken();
               if(token.startsWith(" name"))
               {
                  name = tokenizer.nextToken();
                  state = 2;
               }
               else if(token.startsWith(" filename"))
               {
                  filename = tokenizer.nextToken();
                  StringTokenizer ftokenizer =
                     new StringTokenizer(filename,"\\/:");
                  filename = ftokenizer.nextToken();
                  while(ftokenizer.hasMoreTokens())
                     filename = ftokenizer.nextToken();
                  state = 1;
                  break;
               }
            }
         }
         else if(st.startsWith("Content-Type") &&
                 state == 1)
         {
            pos = st.indexOf(":");
            // + 2 to remove the space
            // - 2 to remove CR/LF
            contentType =
               st.substring(pos + 2,st.length() - 2);
         }
         else if(st.equals("\r\n") && state == 1)
            state = 3;
         else if(st.equals("\r\n") && state == 2)
            state = 4;
         else if(state == 4)
            value = value == null ? st : value + st;
         else if(state == 3)
            buffer.write(bytes,0,i);
         i = in.readLine(bytes,0,512);

      } // end while loop

      return fields;

  }
*/
 
/*
class ByteArrayDataSource
   implements DataSource
{
   byte[] bytes;
   String contentType,
          name;

   ByteArrayDataSource(byte[] bytes,
                       String contentType,
                       String name)
   {
      this.bytes = bytes;
      if(contentType == null)
         this.contentType = "application/octet-stream";
      else
         this.contentType = contentType;
      this.name = name;
   }

   public String getContentType()
   {
      return contentType;
   }

   public InputStream getInputStream()
   {
      // remove the final CR/LF
      return new ByteArrayInputStream(
         bytes,0,bytes.length - 2);
   }

   public String getName()
   {
      return name;
   }

   public OutputStream getOutputStream()
      throws IOException
   {
      throw new FileNotFoundException();
   }
}
*/

/*
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

     doPost(req, resp, null);
 }
*/

 private static Authenticator getAuthenticator(final String user, final String pass) {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication(user, pass); // credentials
         //return new PasswordAuthentication("support@foretees.com", "fikd18"); // credentials
       }
    };

    return auth;
 }




 private static void doGolfNetTest(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {

    String club = "";
    String hdcpNum = "";
    int hdcp = 0;

    String first = "";
    String last = "";
    String username = "";
    String sourceUserId = "";
    int localId = 0;
    int friendlyId = 0;
    String sourceClubId = "";
    String displayHdcp = "";

    out.println("<p>BEGIN</p>");

    try {

        URL url = new URL("http://services.uat.ngn.com/MemberService.asmx");

        MemberService_Service service = null;

        service = new MemberService_Service(url, new QName("http://services.ngn.com/2007/03/20/MemberService", "MemberService"));

        MemberService ms = service.getMemberService();
        
        FetchMembersResponse response = ms.fetchMembersByClub(getFetchMembersByClubRequest(out));

        MemberList ml = response.getMemberList();

        List<MemberInfo> members = ml.getMember();

        MemberInfo mi = null;

        out.println("size=" + members.size());

        for (int i = 0; i < members.size(); i++) {

            displayHdcp = "";
            hdcp = 0;

            mi = members.get(i);

            sourceUserId = mi.getSourceUserId();

            first = mi.getFirstName();
            last = mi.getLastName();
            username = mi.getUsername();
            localId = mi.getLocalId();
            friendlyId = mi.getFriendlyId();
            sourceClubId = mi.getSourceClubId();

            // get members hdcp array
            ArrayOfMemberHandicapInfo amhi = mi.getHandicapInfo();

            List<MemberHandicapInfo> mhi = amhi.getMemberHandicapInfo();

            if (mhi.size() > 0) {

                // get hdcp info from hdcp array
                MemberHandicapInfo hdcpInfo = mhi.get(0);

                hdcp = hdcpInfo.getValue();
                displayHdcp = hdcpInfo.getDisplayHandicap();

            } else {

                displayHdcp = "MISSING";

            }

            out.println("<br>" + username + ", " + sourceClubId + ", " + sourceUserId + ", " + friendlyId + ", " + localId + ", " + first + " " + last + ", " + hdcp + ", " + displayHdcp);

        }


    } catch (Exception exc) {

        out.println("<p>ERROR: " + exc.toString() + "</p>");

    }

    out.println("<p>DONE</p>");

 }


 private static FetchMembersByClubRequest getFetchMembersByClubRequest(PrintWriter out) {


    FetchMembersByClubRequest request = new FetchMembersByClubRequest();

/*
    "transactionId",
    "credentials",
    "sourceClubId",
    "fromDate",
    "toDate",
    "activeOnly"
 */
            
    DatatypeFactory df = null;

    try {
        df = DatatypeFactory.newInstance();
    } catch (Exception igenore) {}


    //Calendar cal = new GregorianCalendar();         // get todays date
    
    //XMLGregorianCalendar xmlToDate = df.newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 0);
    
    //cal.add(Calendar.DATE, -1825);                  // apply the offset

    //XMLGregorianCalendar xmlFromDate = df.newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 0);


    XMLGregorianCalendar xmlFromDate = null;
    try {

      xmlFromDate = DatatypeFactory.newInstance()
        .newXMLGregorianCalendar(
            new GregorianCalendar(2008,10,1));

    } catch (DatatypeConfigurationException e) {

        // TODO Auto-generated catch block
        out.println("<p>xmlFromDate ERROR: " + e.toString() + "</p>");

    }

    XMLGregorianCalendar xmlToDate = null;
    try {

      xmlToDate = DatatypeFactory.newInstance()
        .newXMLGregorianCalendar(
            new GregorianCalendar(2012,8,20));

    } catch (DatatypeConfigurationException e) {

        // TODO Auto-generated catch block
        out.println("<p>xmlToDate ERROR: " + e.toString() + "</p>");

    }

    request.setTransactionId("1");
    request.setCredentials( getCredentials("") );
    request.setSourceClubId("3344");
    request.setFromDate(xmlFromDate);          // xmlFromDate
    request.setToDate(xmlToDate);   // xmlToDate
    request.setActiveOnly(true);

    return request;
 }


 private static Credentials getCredentials(String club) {

    // club is here only if we need to override these values

    String WS_USERNAME = "ForeteesService";
    String WS_PASSWORD = "123456";
    String WS_SOURCE = "Foretees";

    Credentials auth = new Credentials();
    auth.setUsername(WS_USERNAME);
    auth.setPassword(WS_PASSWORD);
    auth.setSource(WS_SOURCE);

    return auth;

 }

 private static void doCmd(HttpServletRequest req, HttpServletResponse resp) {

    PrintWriter out = null;
    //StringBuffer ta = new StringBuffer(512);
    String result = "";
    
    try {
        
        resp.setContentType("text/html");
        out = resp.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>CMDS</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("");
        out.println("");
        out.println("");
        out.println("");
        out.println("");


/*
        String cm = "ping javalessons.com -c 3";
        Process p = Runtime.getRuntime().exec(cm);
        InputStream in = p.getInputStream();
        int ch;
        StringBuffer sb = new StringBuffer(512);

        while ( ( ch = in.read() ) != -1 )
        {	sb.append((char) ch); }

        ta.append(sb.toString());
*/
        
        //
        // ADD CHECK TO MAKE SURE USER IS RUNINNG THIS ON NODE #1 !!!
        //
        
        String clubname = req.getParameter("clubname");
        
        if (clubname != null) {
        
            clubname = clubname.trim();
            
            if (clubname.indexOf(".") == -1) {

                String cm = "ln -s /usr/local/tomcat/webapps/ROOT/login.jsp /usr/local/tomcat/webapps/" + clubname + "/login.jsp";
                Process p = Runtime.getRuntime().exec(cm);
                out.println("<pre>" + cm + "</pre>");

                cm = "ln -s /usr/local/tomcat/webapps/ROOT/mlogin.jsp /usr/local/tomcat/webapps/" + clubname + "/mlogin.jsp";
                p = Runtime.getRuntime().exec(cm);
                out.println("<pre>" + cm + "</pre>");

                cm = "ls -lh /usr/local/tomcat/webapps/" + clubname;
                p = Runtime.getRuntime().exec(cm);
                out.println("<pre>" + cm + "</pre>");

                InputStream in = p.getInputStream();
                int ch;
                StringBuffer sb = new StringBuffer(512);

                while ( ( ch = in.read() ) != -1 )
                {	sb.append((char) ch); }

                //ta.append(sb.toString());

                result = sb.toString();
            
            } // no periods in clubname
        
        } // clubname not null
        
        
        
    } catch (Exception exc) {
    
        out.println("ERROR:" + exc.toString());

    }

    out.println("<pre>" + result + "</pre>");

    out.println("</body>");
    out.println("</html>");
    out.close();

 }

/*
 private void buildCharges(PrintWriter out) {


    out.println("<html>");
    out.println("<head>");
    out.println("<title>4T-ETT</title>");
    out.println("</head>");

    parmPOS parm = new parmPOS();

    parm.charges.clear();
    parm.sdate = "2011-03-01";

    addCharge(parm, "6700-000", "100001", 12.77, .13, 1);
    addCharge(parm, null, "100002", 22.77, 2.13, 2);
    addCharge(parm, "6701-001", "100003", 32.77, 3.13, 3);
    addCharge(parm, "6702-002", "100004", 42.77, 4.13, 4);
    addCharge(parm, null, "100005", 52.77, 5.13, 5);
    addCharge(parm, null, "100006", 62.77, 6.13, 6);
    addCharge(parm, null, "100007", 72.77, 7.13, 7);


    out.println("<br>Found " + parm.charges.size() + " member charges.");

    for (int i=0; i < parm.charges.size(); i++) {

        out.println("<br><br>Member: " + parm.charges.get(i).get(0) + " has " + (parm.charges.get(i).size() - 1) + " charges.");

        for (int i2 = 1; i2 < parm.charges.get(i).size(); i2++) {

            String blah = parm.charges.get(i).get(i2);

            out.println("<br>&nbsp;&nbsp;Charge Data: " + blah);

            String tmp[] = blah.split("\\|");

            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;Item: " + tmp[0]);
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;Price: " + tmp[1]);
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;Tax: " + tmp[2]);
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;Qty: " + tmp[3]);

        }

    }


    ArrayList<ArrayList<String>> batch = buildXmlFileForIBS(parm);

    //String batch = buildXmlFileForIBS(parm, null, out);

    for (int i=0; i < batch.size(); i++) {
        
        
        out.println("<br>POS-ID: " + batch.get(i).get(0) + "<br>BATCH: " + batch.get(i).get(1) + "");
        
    }


 }


 private ArrayList<ArrayList<String>> buildXmlFileForIBS(parmPOS parm) {

    double price = 0;
    double tax = 0;
    double total_tax = 0;
    double total_price = 0;
    int qty = 0;
    String tmp[];
    String batch = "";

    ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

    for (int i=0; i < parm.charges.size(); i++) {

        result.add(new ArrayList<String>());
        result.get(result.size() - 1).add(parm.charges.get(i).get(0)); // plug in the posid

        tmp = parm.charges.get(i).get(0).split("-");

        batch = "" +
            "<Root>" +
                "<Details>" +
                    "<BatchDate>" + parm.sdate + "</BatchDate>" +
                    "<BatchName>ForeTees Batch</BatchName>" +
                    "<uidDeptID>9418C948-FB79-4992-8128-C3701E4D30E0</uidDeptID>" +
                    "<BatchDetails>" +
                        "<TranID>" + 1 + "</TranID>" +
                        "<MemberNumber>" + tmp[0] + "</MemberNumber>" +
                        "<MemberExtension>" + tmp[1] + "</MemberExtension>" +
                        "<EmplNumber>99</EmplNumber>";


        for (int i2 = 1; i2 < parm.charges.get(i).size(); i2++) {

            tmp = parm.charges.get(i).get(i2).split("\\|");

            try {

                price = Double.parseDouble(tmp[1]);
                tax = Double.parseDouble(tmp[2]);
                qty = Integer.parseInt(tmp[3]);

            } catch (NumberFormatException e) { }

            batch += "" +
                 "<Items>" +
                    "<InvNumber>" + tmp[0] + "</InvNumber>" +
                    "<Price>" + price + "</Price>" +
                    "<Quantity>" + qty + "</Quantity>" +
                    "<uidInvMenuID>2888C6D1-FE27-456D-ACEC-523E8581812E</uidInvMenuID>" +
                 "</Items>";

            total_tax += (tax * qty);
            total_price += (price * qty) + (tax * qty);

        }

        batch += "" +
                        "<Taxes>" +
                            "<uidTaxID>89F5BF8B-7957-42BF-81CB-975BCFD1520F</uidTaxID>" +
                            "<TaxAmount>" + Math.scalb(total_tax, 2) + "</TaxAmount>" +
                        "</Taxes>" +
                        "<Tenders>" +
                            "<uidTenderID>6114556D-F64F-48F3-B2F3-36C8BE4110E4</uidTenderID>" +
                            "<TenderAmount>" + Math.scalb(total_price, 2) + "</TenderAmount>" +
                        "</Tenders>" +
                    "</BatchDetails>" +
                "</Details>" +
            "</Root>";

        result.get(result.size() - 1).add(batch);

        total_tax = 0;
        total_price = 0;
        
    }

    return result;

 }


 private String buildXmlFileForIBS2(parmPOS parm, Connection con, PrintWriter out) {

    double price = 0;
    double tax = 0;
    double total_tax = 0;
    double total_price = 0;
    int qty = 0;
    String tmp[];
    
    String batch = "" +
            "<Root>" +
                "<Details>" +
                    "<BatchDate>" + parm.sdate + "</BatchDate>" +
                    "<BatchName>ForeTees Batch</BatchName>" +
                    "<uidDeptID>9418C948-FB79-4992-8128-C3701E4D30E0</uidDeptID>" +
                    "<BatchDetails>" +
                        "<TranID>" + 1 + "</TranID>";

    for (int i=0; i < parm.charges.size(); i++) {

        tmp = parm.charges.get(i).get(0).split("-");

        batch += "<MemberNumber>" + tmp[0] + "</MemberNumber>" +
                 "<MemberExtension>" + tmp[1] + "</MemberExtension>" +
                 "<EmplNumber>99</EmplNumber>";


        for (int i2 = 1; i2 < parm.charges.get(i).size(); i2++) {

            tmp = parm.charges.get(i).get(i2).split("\\|");
            
            try {
            
                price = Double.parseDouble(tmp[1]);
                tax = Double.parseDouble(tmp[2]);
                qty = Integer.parseInt(tmp[3]);
            
            } catch (NumberFormatException e) { }
            
            batch += "" +
                 "<Items>" +
                    "<InvNumber>" + tmp[0] + "</InvNumber>" +
                    "<Price>" + price + "</Price>" +
                    "<Quantity>" + qty + "</Quantity>" +
                    "<uidInvMenuID>2888C6D1-FE27-456D-ACEC-523E8581812E</uidInvMenuID>" +
                 "</Items>";

            total_tax += (tax * qty);
            total_price += (price * qty) + (tax * qty);
            
        }

    }

    batch += "" +
                        "<Taxes>" +
                            "<uidTaxID>89F5BF8B-7957-42BF-81CB-975BCFD1520F</uidTaxID>" +
                            "<TaxAmount>" + total_tax + "</TaxAmount>" +
                        "</Taxes>" +
                        "<Tenders>" +
                            "<uidTenderID>6114556D-F64F-48F3-B2F3-36C8BE4110E4</uidTenderID>" +
                            "<TenderAmount>" + total_price + "</TenderAmount>" +
                        "</Tenders>" +
                    "</BatchDetails>" +
                "</Details>" +
            "</Root>";

    return batch;
 }


 private void addCharge(parmPOS parm, String posid, String invNumber, double price, double tax, int qty) {

    if (posid != null) {

        parm.charges.add(new ArrayList<String>());

        parm.charges.get(parm.charges.size() - 1).add(posid);

    }

    parm.charges.get(parm.charges.size() - 1).add(invNumber + "|" + price + "|" + tax + "|" + qty);

 }
*/


 private void abacus(PrintWriter out) {

    //
    // TEST CODE FOR ABACUS INTERFACE
    //

    String data = "PAUL|192.168.1.56|{BB69B169-223E-4971-B361-E7DB80A35F48}|~>|CHECKIN|616358979|265292313|1524|-1|1|0||01|MARRIOTT|20111109105000|75|0|YOUR GUEST|Y|GUEST||||||||||POS DEMO|0||||1|{FEC9339F-0248-4EBC-A4A5-A28B8DA1660D}||<~|";
    String address = "12.25.85.41";
    int port = 50;

    try {

        Utilities.logError("Abacus: Attempting TimedSocket socket connection.");

        // Connect to remote service
        Socket socket = TimedSocket.getSocket (address, port, 4000);

        Utilities.logError("Abacus: Connected to " + socket.getInetAddress() + " on port " + socket.getPort());

        // Set the socket timeout for ten seconds
        socket.setSoTimeout (10000);

        // Send a message to the client application
        //
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(data);

        Utilities.logError("Abacus: Data written to socket.");

        oos.close();

    } catch (InterruptedIOException iioe) {
        Utilities.logError ("Remote host timed out");/*
    } catch (MalformedURLException mue) {
        Utilities.logError ("Invalid URL");*/
    } catch (IOException ioe) {
        Utilities.logError ("Network I/O error - " + ioe);
    }

    Utilities.logError("Abacus: Done!");




/*
    out.print("Attempting to connect...");
    out.flush();

    try {

        //
        // Create a connection to the server socket on the server application
        //
        //InetAddress host = InetAddress.getLocalHost();

        Utilities.logError("Abacus: Attempting socket connection.");

        Socket socket = new Socket(address, port); // host.getHostName()

        Utilities.logError("Abacus: Connected to " + socket.getInetAddress() + " on port " + socket.getPort());

        out.println("<p>Connected to " + socket.getInetAddress() + " on port " + socket.getPort() + "</p>");
        out.flush();
        //
        // Send a message to the client application
        //
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(data);

        Utilities.logError("Abacus: Data written to socket.");

        //
        // Read and display the response message sent by server application
        //
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Utilities.logError("Abacus: Listening for response.");
        String message = (String) ois.readObject();
        Utilities.logError("Abacus: Response was " + message);
        out.println("Result: " + message);

        ois.close();
        oos.close();

    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    }
*/

    try {
/*
        ServerSocket srvr = new ServerSocket(1234);

        Socket skt = srvr.accept();

        out.print("Connected!<BR>");
        out.print("Sending string: '" + data + "'\n");

        PrintWriter send = new PrintWriter(skt.getOutputStream(), true);

        send.print(data);

        skt.close();

        srvr.close();
/*



        /*** GET RESPONSE

        Socket s = new Socket(address, port);

        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        out.print("Response: '");

        while (!in.ready()) {}
        out.println(in.readLine());

        out.print("'<br>");

        in.close();
        */
    } catch(Exception e) {

        out.print("<P>Error: " + e.toString() + "</P>");

    }

 }


 private void tinymce(HttpServletRequest req, PrintWriter out) {

    //
    //   File objects to get the current announcement page
    //
    File f;
    FileReader fr;
    BufferedReader br;
    String tmp = "";
    String path = "";

    out.println("<html>");
    out.println("<head>");

    out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/tiny_mce/tiny_mce.js\"></script>");

    out.println("<script type=\"text/javascript\">");

      out.println("tinyMCE.init({");

      out.println("content_css : \"/v5/assets/stylesheets/sitewide_dining.css\",");

      out.println("relative_urls : false,");
      out.println("mode : \"textareas\",");
      out.println("theme : \"advanced\",");
     // out.println("plugins : \"safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager,filemanager\",");
      out.println("plugins : \"safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager\",");

      //
      // Theme options - these are the default buttons that came with the product (refer to our custom button rows directly below)
      /*
      out.println("theme_advanced_buttons1 : \"save,newdocument,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,styleselect,formatselect,fontselect,fontsizeselect\",");
      out.println("theme_advanced_buttons2 : \"cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,|,insertdate,inserttime,preview,|,forecolor,backcolor\",");
      out.println("theme_advanced_buttons3 : \"tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,iespell,media,advhr,|,print,|,ltr,rtl,|,fullscreen\",");
      out.println("theme_advanced_buttons4 : \"insertlayer,moveforward,movebackward,absolute,|,styleprops,spellchecker,|,cite,abbr,acronym,del,ins,attribs,|,visualchars,nonbreaking,template,blockquote,pagebreak,|,insertfile,insertimage\",");
       */

      // Theme options
      out.println("theme_advanced_buttons1 : \"save,|,cut,copy,paste,pastetext,pasteword,|,search,replace,|,undo,redo,|,tablecontrols,|,removeformat,visualaid,|,charmap,insertdate,inserttime,emotions,hr,advhr,|,print,|,ltr,rtl,|,fullscreen,|,insertlayer,moveforward,movebackward,absolute,|,iespell,spellchecker\",");
      out.println("theme_advanced_buttons2 : \"formatselect,fontselect,fontsizeselect,styleprops,|,bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote,|,sub,sup,|,link,unlink,anchor,image,insertimage,|,cleanup,code,preview\",");
      out.println("theme_advanced_buttons3 : \"\",");
      out.println("theme_advanced_buttons4 : \"\",");   // we squeezed all desired buttons onto 2 rows - leave 3 & 4 blank (must be defined here to avoid the default)

      out.println("theme_advanced_toolbar_location : \"top\",");
      out.println("theme_advanced_toolbar_align : \"left\",");
      out.println("theme_advanced_resizing : true,");
      out.println("theme_advanced_statusbar_location : \"bottom\",");

      out.println("template_cdate_classes : \"cdate creationdate\",");
      out.println("template_mdate_classes : \"mdate modifieddate\",");
      out.println("template_cdate_format : \"%m/%d/%Y : %H:%M:%S\",");
      out.println("template_mdate_format : \"%m/%d/%Y : %H:%M:%S\",");

      // Skin options
      out.println("skin : \"o2k7\",");
      out.println("skin_variant : \"silver\",");

     // out.println("theme_advanced_statusbar_location : \"bottom\",");      // we don't need to show the file location info

      // Example content CSS (should be your site CSS)
      // out.println("content_css : \"css/example.css\",");

      // Drop lists for link/image/media/template dialogs
      out.println("template_external_list_url : \"js/template_list.js\",");
      out.println("external_link_list_url : \"js/link_list.js\",");
      out.println("external_image_list_url : \"js/image_list.js\",");
      out.println("media_external_list_url : \"js/media_list.js\",");

      // Replace values for the template plugin
      out.println("template_replace_values : {");
      out.println("username : \"Jim Adams\",");
      out.println("memnum : \"670\"");
      out.println("}");

      out.println("});");

    out.println("</script>");

    out.println("</head>");
    out.println("<body>");

    out.println("<textarea name=\"content\" style=\"width:100%;height:80%\">");

    try {

      //
      //   Get the announcement page
      try {
          path = req.getRealPath("");
          f = new File(path + "/announce/demov4_announce_tmpl.htm");
          fr = new FileReader(f);
          br = new BufferedReader(fr);
          if (!f.isFile()) {
              // do nothing
          }
      }
      catch (FileNotFoundException e) {
          out.println("<br><br><p align=center>Missing Announcement Page.</p>");
          out.println("</div></BODY></HTML>");
          return;
      }
      catch (SecurityException se) {
          out.println("<br><br><p align=center>Access Denied.</p>");
          out.println("</div></BODY></HTML>");
          return;
      }

      while( (tmp = br.readLine()) != null )
          out.println(tmp);


      try {
          br.close();
          fr.close();
      } catch(Exception ignore) {
          // do nothing
      } finally {
          br = null;
          fr = null;
          f = null;
      }
    
    } catch (Exception exc) {

    }

    out.println("</textarea>");

    out.println("</bodyt>");
    out.println("</html>");

 }

} // end servlet public class
