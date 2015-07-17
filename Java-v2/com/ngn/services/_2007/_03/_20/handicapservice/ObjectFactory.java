
package com.ngn.services._2007._03._20.handicapservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.ngn.services._2007._03._20.handicapservice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _UpdateHandicapsRequest_QNAME = new QName("http://services.ngn.com/2007/03/20/HandicapService", "UpdateHandicapsRequest");
    private final static QName _UpdateHandicapsResponse_QNAME = new QName("http://services.ngn.com/2007/03/20/HandicapService", "UpdateHandicapsResponse");
    private final static QName _FetchHandicapsResponse_QNAME = new QName("http://services.ngn.com/2007/03/20/HandicapService", "FetchHandicapsResponse");
    private final static QName _FetchHandicapListByUserRequest_QNAME = new QName("http://services.ngn.com/2007/03/20/HandicapService", "FetchHandicapListByUserRequest");
    private final static QName _FetchHandicapListByNetworkIdRequest_QNAME = new QName("http://services.ngn.com/2007/03/20/HandicapService", "FetchHandicapListByNetworkIdRequest");
    private final static QName _FetchHandicapByUserRequest_QNAME = new QName("http://services.ngn.com/2007/03/20/HandicapService", "FetchHandicapByUserRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.ngn.services._2007._03._20.handicapservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UpdateHandicapsRequest }
     * 
     */
    public UpdateHandicapsRequest createUpdateHandicapsRequest() {
        return new UpdateHandicapsRequest();
    }

    /**
     * Create an instance of {@link FetchHandicapsResponse }
     * 
     */
    public FetchHandicapsResponse createFetchHandicapsResponse() {
        return new FetchHandicapsResponse();
    }

    /**
     * Create an instance of {@link FetchHandicapListByNetworkIdRequest }
     * 
     */
    public FetchHandicapListByNetworkIdRequest createFetchHandicapListByNetworkIdRequest() {
        return new FetchHandicapListByNetworkIdRequest();
    }

    /**
     * Create an instance of {@link FetchHandicapByUserRequest }
     * 
     */
    public FetchHandicapByUserRequest createFetchHandicapByUserRequest() {
        return new FetchHandicapByUserRequest();
    }

    /**
     * Create an instance of {@link UpdateHandicapsResponse }
     * 
     */
    public UpdateHandicapsResponse createUpdateHandicapsResponse() {
        return new UpdateHandicapsResponse();
    }

    /**
     * Create an instance of {@link FetchHandicapListByUserRequest }
     * 
     */
    public FetchHandicapListByUserRequest createFetchHandicapListByUserRequest() {
        return new FetchHandicapListByUserRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateHandicapsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/HandicapService", name = "UpdateHandicapsRequest")
    public JAXBElement<UpdateHandicapsRequest> createUpdateHandicapsRequest(UpdateHandicapsRequest value) {
        return new JAXBElement<UpdateHandicapsRequest>(_UpdateHandicapsRequest_QNAME, UpdateHandicapsRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateHandicapsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/HandicapService", name = "UpdateHandicapsResponse")
    public JAXBElement<UpdateHandicapsResponse> createUpdateHandicapsResponse(UpdateHandicapsResponse value) {
        return new JAXBElement<UpdateHandicapsResponse>(_UpdateHandicapsResponse_QNAME, UpdateHandicapsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchHandicapsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/HandicapService", name = "FetchHandicapsResponse")
    public JAXBElement<FetchHandicapsResponse> createFetchHandicapsResponse(FetchHandicapsResponse value) {
        return new JAXBElement<FetchHandicapsResponse>(_FetchHandicapsResponse_QNAME, FetchHandicapsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchHandicapListByUserRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/HandicapService", name = "FetchHandicapListByUserRequest")
    public JAXBElement<FetchHandicapListByUserRequest> createFetchHandicapListByUserRequest(FetchHandicapListByUserRequest value) {
        return new JAXBElement<FetchHandicapListByUserRequest>(_FetchHandicapListByUserRequest_QNAME, FetchHandicapListByUserRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchHandicapListByNetworkIdRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/HandicapService", name = "FetchHandicapListByNetworkIdRequest")
    public JAXBElement<FetchHandicapListByNetworkIdRequest> createFetchHandicapListByNetworkIdRequest(FetchHandicapListByNetworkIdRequest value) {
        return new JAXBElement<FetchHandicapListByNetworkIdRequest>(_FetchHandicapListByNetworkIdRequest_QNAME, FetchHandicapListByNetworkIdRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchHandicapByUserRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/HandicapService", name = "FetchHandicapByUserRequest")
    public JAXBElement<FetchHandicapByUserRequest> createFetchHandicapByUserRequest(FetchHandicapByUserRequest value) {
        return new JAXBElement<FetchHandicapByUserRequest>(_FetchHandicapByUserRequest_QNAME, FetchHandicapByUserRequest.class, null, value);
    }

}
