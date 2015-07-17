
package com.ngn.services._2007._03._20.scoreservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.ngn.services._2007._03._20.scoreservice package. 
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

    private final static QName _FetchScoresResponse_QNAME = new QName("http://services.ngn.com/2007/03/20/ScoreService", "FetchScoresResponse");
    private final static QName _FetchScoresByClubRequest_QNAME = new QName("http://services.ngn.com/2007/03/20/ScoreService", "FetchScoresByClubRequest");
    private final static QName _FetchScoresBySourceRequest_QNAME = new QName("http://services.ngn.com/2007/03/20/ScoreService", "FetchScoresBySourceRequest");
    private final static QName _FetchHandicapScoresByHandicapIdRequest_QNAME = new QName("http://services.ngn.com/2007/03/20/ScoreService", "FetchHandicapScoresByHandicapIdRequest");
    private final static QName _FetchScoresByUserRequest_QNAME = new QName("http://services.ngn.com/2007/03/20/ScoreService", "FetchScoresByUserRequest");
    private final static QName _UpdateScoresRequest_QNAME = new QName("http://services.ngn.com/2007/03/20/ScoreService", "UpdateScoresRequest");
    private final static QName _UpdateScoreIdsRequest_QNAME = new QName("http://services.ngn.com/2007/03/20/ScoreService", "UpdateScoreIdsRequest");
    private final static QName _FetchScoresByNetworkIdRequest_QNAME = new QName("http://services.ngn.com/2007/03/20/ScoreService", "FetchScoresByNetworkIdRequest");
    private final static QName _UpdateScoresResponse_QNAME = new QName("http://services.ngn.com/2007/03/20/ScoreService", "UpdateScoresResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.ngn.services._2007._03._20.scoreservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UpdateScoreIdsRequest }
     * 
     */
    public UpdateScoreIdsRequest createUpdateScoreIdsRequest() {
        return new UpdateScoreIdsRequest();
    }

    /**
     * Create an instance of {@link UpdateScoresResponse }
     * 
     */
    public UpdateScoresResponse createUpdateScoresResponse() {
        return new UpdateScoresResponse();
    }

    /**
     * Create an instance of {@link UpdateScoresRequest }
     * 
     */
    public UpdateScoresRequest createUpdateScoresRequest() {
        return new UpdateScoresRequest();
    }

    /**
     * Create an instance of {@link FetchScoresByUserRequest }
     * 
     */
    public FetchScoresByUserRequest createFetchScoresByUserRequest() {
        return new FetchScoresByUserRequest();
    }

    /**
     * Create an instance of {@link FetchHandicapScoresByHandicapIdRequest }
     * 
     */
    public FetchHandicapScoresByHandicapIdRequest createFetchHandicapScoresByHandicapIdRequest() {
        return new FetchHandicapScoresByHandicapIdRequest();
    }

    /**
     * Create an instance of {@link FetchScoresResponse }
     * 
     */
    public FetchScoresResponse createFetchScoresResponse() {
        return new FetchScoresResponse();
    }

    /**
     * Create an instance of {@link FetchScoresByNetworkIdRequest }
     * 
     */
    public FetchScoresByNetworkIdRequest createFetchScoresByNetworkIdRequest() {
        return new FetchScoresByNetworkIdRequest();
    }

    /**
     * Create an instance of {@link FetchScoresBySourceRequest }
     * 
     */
    public FetchScoresBySourceRequest createFetchScoresBySourceRequest() {
        return new FetchScoresBySourceRequest();
    }

    /**
     * Create an instance of {@link FetchScoresByClubRequest }
     * 
     */
    public FetchScoresByClubRequest createFetchScoresByClubRequest() {
        return new FetchScoresByClubRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchScoresResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/ScoreService", name = "FetchScoresResponse")
    public JAXBElement<FetchScoresResponse> createFetchScoresResponse(FetchScoresResponse value) {
        return new JAXBElement<FetchScoresResponse>(_FetchScoresResponse_QNAME, FetchScoresResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchScoresByClubRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/ScoreService", name = "FetchScoresByClubRequest")
    public JAXBElement<FetchScoresByClubRequest> createFetchScoresByClubRequest(FetchScoresByClubRequest value) {
        return new JAXBElement<FetchScoresByClubRequest>(_FetchScoresByClubRequest_QNAME, FetchScoresByClubRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchScoresBySourceRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/ScoreService", name = "FetchScoresBySourceRequest")
    public JAXBElement<FetchScoresBySourceRequest> createFetchScoresBySourceRequest(FetchScoresBySourceRequest value) {
        return new JAXBElement<FetchScoresBySourceRequest>(_FetchScoresBySourceRequest_QNAME, FetchScoresBySourceRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchHandicapScoresByHandicapIdRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/ScoreService", name = "FetchHandicapScoresByHandicapIdRequest")
    public JAXBElement<FetchHandicapScoresByHandicapIdRequest> createFetchHandicapScoresByHandicapIdRequest(FetchHandicapScoresByHandicapIdRequest value) {
        return new JAXBElement<FetchHandicapScoresByHandicapIdRequest>(_FetchHandicapScoresByHandicapIdRequest_QNAME, FetchHandicapScoresByHandicapIdRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchScoresByUserRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/ScoreService", name = "FetchScoresByUserRequest")
    public JAXBElement<FetchScoresByUserRequest> createFetchScoresByUserRequest(FetchScoresByUserRequest value) {
        return new JAXBElement<FetchScoresByUserRequest>(_FetchScoresByUserRequest_QNAME, FetchScoresByUserRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateScoresRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/ScoreService", name = "UpdateScoresRequest")
    public JAXBElement<UpdateScoresRequest> createUpdateScoresRequest(UpdateScoresRequest value) {
        return new JAXBElement<UpdateScoresRequest>(_UpdateScoresRequest_QNAME, UpdateScoresRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateScoreIdsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/ScoreService", name = "UpdateScoreIdsRequest")
    public JAXBElement<UpdateScoreIdsRequest> createUpdateScoreIdsRequest(UpdateScoreIdsRequest value) {
        return new JAXBElement<UpdateScoreIdsRequest>(_UpdateScoreIdsRequest_QNAME, UpdateScoreIdsRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchScoresByNetworkIdRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/ScoreService", name = "FetchScoresByNetworkIdRequest")
    public JAXBElement<FetchScoresByNetworkIdRequest> createFetchScoresByNetworkIdRequest(FetchScoresByNetworkIdRequest value) {
        return new JAXBElement<FetchScoresByNetworkIdRequest>(_FetchScoresByNetworkIdRequest_QNAME, FetchScoresByNetworkIdRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateScoresResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.ngn.com/2007/03/20/ScoreService", name = "UpdateScoresResponse")
    public JAXBElement<UpdateScoresResponse> createUpdateScoresResponse(UpdateScoresResponse value) {
        return new JAXBElement<UpdateScoresResponse>(_UpdateScoresResponse_QNAME, UpdateScoresResponse.class, null, value);
    }

}
