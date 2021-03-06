
package com.ngn.services._2007._03._20.scoreservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "ScoreService", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    com.ngn.services._2007._03._20.commondata.ObjectFactory.class,
    com.ngn.services._2007._03._20.scoredata.ObjectFactory.class,
    com.ngn.services._2007._03._20.scoreservice.ObjectFactory.class,
    com.ngn.services._2007._03._20.headerdata.ObjectFactory.class
})
public interface ScoreService {


    /**
     * 
     * @param updateScoresRequest
     * @return
     *     returns com.ngn.services._2007._03._20.scoreservice.UpdateScoresResponse
     */
    @WebMethod(operationName = "UpdateScores", action = "http://services.ngn.com/2007/03/20/ScoreService/UpdateScores")
    @WebResult(name = "UpdateScoresResponse", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "UpdateScoresResult")
    public UpdateScoresResponse updateScores(
        @WebParam(name = "UpdateScoresRequest", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "UpdateScoresRequest")
        UpdateScoresRequest updateScoresRequest);

    /**
     * 
     * @param updateScoreIdsRequest
     * @return
     *     returns com.ngn.services._2007._03._20.scoreservice.UpdateScoresResponse
     */
    @WebMethod(operationName = "UpdateScoreIds", action = "http://services.ngn.com/2007/03/20/ScoreService/UpdateScoreIds")
    @WebResult(name = "UpdateScoresResponse", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "UpdateScoreIdsResult")
    public UpdateScoresResponse updateScoreIds(
        @WebParam(name = "UpdateScoreIdsRequest", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "UpdateScoreIdsRequest")
        UpdateScoreIdsRequest updateScoreIdsRequest);

    /**
     * 
     * @param fetchScoresByUserRequest
     * @return
     *     returns com.ngn.services._2007._03._20.scoreservice.FetchScoresResponse
     */
    @WebMethod(operationName = "FetchScoresByUser", action = "http://services.ngn.com/2007/03/20/ScoreService/FetchScoresByUser")
    @WebResult(name = "FetchScoresResponse", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "FetchScoresByUserResult")
    public FetchScoresResponse fetchScoresByUser(
        @WebParam(name = "FetchScoresByUserRequest", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "FetchScoresByUserRequest")
        FetchScoresByUserRequest fetchScoresByUserRequest);

    /**
     * 
     * @param fetchScoresByNetworkIdRequest
     * @return
     *     returns com.ngn.services._2007._03._20.scoreservice.FetchScoresResponse
     */
    @WebMethod(operationName = "FetchScoresByNetworkId", action = "http://services.ngn.com/2007/03/20/ScoreService/FetchScoresByNetworkId")
    @WebResult(name = "FetchScoresResponse", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "FetchScoresByNetworkIdResult")
    public FetchScoresResponse fetchScoresByNetworkId(
        @WebParam(name = "FetchScoresByNetworkIdRequest", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "FetchScoresByNetworkIdRequest")
        FetchScoresByNetworkIdRequest fetchScoresByNetworkIdRequest);

    /**
     * 
     * @param fetchScoresByClubRequest
     * @return
     *     returns com.ngn.services._2007._03._20.scoreservice.FetchScoresResponse
     */
    @WebMethod(operationName = "FetchScoresByClub", action = "http://services.ngn.com/2007/03/20/ScoreService/FetchScoresByClub")
    @WebResult(name = "FetchScoresResponse", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "FetchScoresByClubResult")
    public FetchScoresResponse fetchScoresByClub(
        @WebParam(name = "FetchScoresByClubRequest", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "FetchScoresByClubRequest")
        FetchScoresByClubRequest fetchScoresByClubRequest);

    /**
     * 
     * @param fetchScoresBySourceRequest
     * @return
     *     returns com.ngn.services._2007._03._20.scoreservice.FetchScoresResponse
     */
    @WebMethod(operationName = "FetchScoresBySource", action = "http://services.ngn.com/2007/03/20/ScoreService/FetchScoresBySource")
    @WebResult(name = "FetchScoresResponse", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "FetchScoresBySourceResult")
    public FetchScoresResponse fetchScoresBySource(
        @WebParam(name = "FetchScoresBySourceRequest", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "FetchScoresBySourceRequest")
        FetchScoresBySourceRequest fetchScoresBySourceRequest);

    /**
     * 
     * @param fetchHandicapScoresByHandicapIdRequest
     * @return
     *     returns com.ngn.services._2007._03._20.scoreservice.FetchScoresResponse
     */
    @WebMethod(operationName = "FetchHandicapScoresByHandicapId", action = "http://services.ngn.com/2007/03/20/ScoreService/FetchHandicapScoresByHandicapId")
    @WebResult(name = "FetchScoresResponse", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "FetchHandicapScoresByHandicapIdResult")
    public FetchScoresResponse fetchHandicapScoresByHandicapId(
        @WebParam(name = "FetchHandicapScoresByHandicapIdRequest", targetNamespace = "http://services.ngn.com/2007/03/20/ScoreService", partName = "FetchHandicapScoresByHandicapIdRequest")
        FetchHandicapScoresByHandicapIdRequest fetchHandicapScoresByHandicapIdRequest);

}
