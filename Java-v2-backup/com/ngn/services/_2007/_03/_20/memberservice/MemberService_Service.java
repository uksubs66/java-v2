
package com.ngn.services._2007._03._20.memberservice;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "MemberService", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", wsdlLocation = "http://services.uat.ngn.com/MemberService.asmx?WSDL")
public class MemberService_Service
    extends Service
{

    private final static URL MEMBERSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(com.ngn.services._2007._03._20.memberservice.MemberService_Service.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.ngn.services._2007._03._20.memberservice.MemberService_Service.class.getResource(".");
            url = new URL(baseUrl, "http://services.uat.ngn.com/MemberService.asmx?WSDL");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'http://services.uat.ngn.com/MemberService.asmx?WSDL', retrying as a local file");
            logger.warning(e.getMessage());
        }
        MEMBERSERVICE_WSDL_LOCATION = url;
    }

    public MemberService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public MemberService_Service() {
        super(MEMBERSERVICE_WSDL_LOCATION, new QName("http://services.ngn.com/2007/03/20/MemberService", "MemberService"));
    }

    /**
     * 
     * @return
     *     returns MemberService
     */
    @WebEndpoint(name = "MemberService")
    public MemberService getMemberService() {
        return super.getPort(new QName("http://services.ngn.com/2007/03/20/MemberService", "MemberService"), MemberService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns MemberService
     */
    @WebEndpoint(name = "MemberService")
    public MemberService getMemberService(WebServiceFeature... features) {
        return super.getPort(new QName("http://services.ngn.com/2007/03/20/MemberService", "MemberService"), MemberService.class, features);
    }

}
