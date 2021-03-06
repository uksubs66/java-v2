
package com.ngn.services._2007._03._20.memberservice;

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
@WebService(name = "MemberService", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    com.ngn.services._2007._03._20.memberservice.ObjectFactory.class,
    com.ngn.services._2007._03._20.memberdata.ObjectFactory.class,
    com.ngn.services._2007._03._20.commondata.ObjectFactory.class,
    com.ngn.services._2007._03._20.headerdata.ObjectFactory.class
})
public interface MemberService {


    /**
     * 
     * @param updateMembersRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.UpdateMembersResponse
     */
    @WebMethod(operationName = "UpdateMembers", action = "http://services.ngn.com/2007/03/20/MemberService/UpdateMembers")
    @WebResult(name = "UpdateMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "UpdateMembersResult")
    public UpdateMembersResponse updateMembers(
        @WebParam(name = "UpdateMembersRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "UpdateMembersRequest")
        UpdateMembersRequest updateMembersRequest);

    /**
     * 
     * @param updateTournamnetMembersRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.UpdateMembersResponse
     */
    @WebMethod(operationName = "UpdateTournamentMembers", action = "http://services.ngn.com/2007/03/20/MemberService/UpdateTournamentMembers")
    @WebResult(name = "UpdateMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "UpdateTournamentMembersResult")
    public UpdateMembersResponse updateTournamentMembers(
        @WebParam(name = "UpdateTournamentMembersRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "UpdateTournamnetMembersRequest")
        UpdateTournamentMembersRequest updateTournamnetMembersRequest);

    /**
     * 
     * @param updateMemberIdsRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.UpdateMembersResponse
     */
    @WebMethod(operationName = "UpdateMemberIds", action = "http://services.ngn.com/2007/03/20/MemberService/UpdateMemberIds")
    @WebResult(name = "UpdateMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "UpdateMemberIdsResult")
    public UpdateMembersResponse updateMemberIds(
        @WebParam(name = "UpdateMemberIdsRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "UpdateMemberIdsRequest")
        UpdateMemberIdsRequest updateMemberIdsRequest);

    /**
     * 
     * @param updateMemberRosterRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.UpdateMemberRosterResponse
     */
    @WebMethod(operationName = "UpdateMemberRoster", action = "http://services.ngn.com/2007/03/20/MemberService/UpdateMemberRoster")
    @WebResult(name = "UpdateMemberRosterResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "UpdateMemberRosterResult")
    public UpdateMemberRosterResponse updateMemberRoster(
        @WebParam(name = "UpdateMemberRosterRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "UpdateMemberRosterRequest")
        UpdateMemberRosterRequest updateMemberRosterRequest);

    /**
     * 
     * @param updateMemberIdsAddSource
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.UpdateMembersResponse
     */
    @WebMethod(operationName = "UpdateMemberIdsAddSource", action = "http://services.ngn.com/2007/03/20/MemberService/UpdateMemberIdsAddSource")
    @WebResult(name = "UpdateMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "UpdateMemberIdsAddSourceResult")
    public UpdateMembersResponse updateMemberIdsAddSource(
        @WebParam(name = "UpdateMemberIdsAddSourceRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "UpdateMemberIdsAddSource")
        UpdateMemberIdsAddSourceRequest updateMemberIdsAddSource);

    /**
     * 
     * @param fetchMembersByUserRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.FetchMembersResponse
     */
    @WebMethod(operationName = "FetchMemberByUser", action = "http://services.ngn.com/2007/03/20/MemberService/FetchMemberByUser")
    @WebResult(name = "FetchMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMemberByUserResult")
    public FetchMembersResponse fetchMemberByUser(
        @WebParam(name = "FetchMemberByUserRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMembersByUserRequest")
        FetchMemberByUserRequest fetchMembersByUserRequest);

    /**
     * 
     * @param fetchTournamentSSORequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.FetchTournamentSSOResponse
     */
    @WebMethod(operationName = "FetchTournamentSSO", action = "http://services.ngn.com/2007/03/20/MemberService/FetchTournamentSSO")
    @WebResult(name = "FetchTournamentSSOResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchTournamentSSOResult")
    public FetchTournamentSSOResponse fetchTournamentSSO(
        @WebParam(name = "FetchTournamentSSORequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchTournamentSSORequest")
        FetchTournamentSSORequest fetchTournamentSSORequest);

    /**
     * 
     * @param fetchMemberByUsernameRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.FetchMembersResponse
     */
    @WebMethod(operationName = "FetchMemberByUsername", action = "http://services.ngn.com/2007/03/20/MemberService/FetchMemberByUsername")
    @WebResult(name = "FetchMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMemberByUsernameResult")
    public FetchMembersResponse fetchMemberByUsername(
        @WebParam(name = "FetchMemberByUsernameRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMemberByUsernameRequest")
        FetchMemberByUsernameRequest fetchMemberByUsernameRequest);

    /**
     * 
     * @param fetchMemberByNetworkIdRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.FetchMembersResponse
     */
    @WebMethod(operationName = "FetchMemberByNetworkId", action = "http://services.ngn.com/2007/03/20/MemberService/FetchMemberByNetworkId")
    @WebResult(name = "FetchMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMemberByNetworkIdResult")
    public FetchMembersResponse fetchMemberByNetworkId(
        @WebParam(name = "FetchMemberByNetworkIdRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMemberByNetworkIdRequest")
        FetchMemberByNetworkIdRequest fetchMemberByNetworkIdRequest);

    /**
     * 
     * @param fetchMemberByCardIdRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.FetchMembersResponse
     */
    @WebMethod(operationName = "FetchMemberByCardId", action = "http://services.ngn.com/2007/03/20/MemberService/FetchMemberByCardId")
    @WebResult(name = "FetchMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMemberByCardIdResult")
    public FetchMembersResponse fetchMemberByCardId(
        @WebParam(name = "FetchMemberByCardIdRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMemberByCardIdRequest")
        FetchMemberByCardIdRequest fetchMemberByCardIdRequest);

    /**
     * 
     * @param fetchMembersBySearchRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.FetchMembersSearchResponse
     */
    @WebMethod(operationName = "FetchMembersBySearch", action = "http://services.ngn.com/2007/03/20/MemberService/FetchMembersBySearch")
    @WebResult(name = "FetchMembersSearchResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMembersBySearchResult")
    public FetchMembersSearchResponse fetchMembersBySearch(
        @WebParam(name = "FetchMembersBySearchRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMembersBySearchRequest")
        FetchMembersBySearchRequest fetchMembersBySearchRequest);

    /**
     * 
     * @param fetchMemberSourceByUserRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.FetchMemberSourceByUserResponse
     */
    @WebMethod(operationName = "FetchMemberSourceByUser", action = "http://services.ngn.com/2007/03/20/MemberService/FetchMemberSourceByUser")
    @WebResult(name = "FetchMemberSourceByUserResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMemberSourceByUserResult")
    public FetchMemberSourceByUserResponse fetchMemberSourceByUser(
        @WebParam(name = "FetchMemberSourceByUserRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMemberSourceByUserRequest")
        FetchMemberSourceByUserRequest fetchMemberSourceByUserRequest);

    /**
     * 
     * @param fetchMembersByClubRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.FetchMembersResponse
     */
    @WebMethod(operationName = "FetchMembersByClub", action = "http://services.ngn.com/2007/03/20/MemberService/FetchMembersByClub")
    @WebResult(name = "FetchMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMembersByClubResult")
    public FetchMembersResponse fetchMembersByClub(
        @WebParam(name = "FetchMembersByClubRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMembersByClubRequest")
        FetchMembersByClubRequest fetchMembersByClubRequest);

    /**
     * 
     * @param fetchMembersByClubNoSourceRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.FetchMembersResponse
     */
    @WebMethod(operationName = "FetchMembersByClubNoSource", action = "http://services.ngn.com/2007/03/20/MemberService/FetchMembersByClubNoSource")
    @WebResult(name = "FetchMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMembersByClubNoSourceResult")
    public FetchMembersResponse fetchMembersByClubNoSource(
        @WebParam(name = "FetchMembersByClubNoSourceRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMembersByClubNoSourceRequest")
        FetchMembersByClubNoSourceRequest fetchMembersByClubNoSourceRequest);

    /**
     * 
     * @param fetchMembersBySourceRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.FetchMembersResponse
     */
    @WebMethod(operationName = "FetchMembersBySource", action = "http://services.ngn.com/2007/03/20/MemberService/FetchMembersBySource")
    @WebResult(name = "FetchMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMembersBySourceResult")
    public FetchMembersResponse fetchMembersBySource(
        @WebParam(name = "FetchMembersBySourceRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMembersBySourceRequest")
        FetchMembersBySourceRequest fetchMembersBySourceRequest);

    /**
     * 
     * @param fetchMembersWithoutSourceRequest
     * @return
     *     returns com.ngn.services._2007._03._20.memberservice.FetchMembersResponse
     */
    @WebMethod(operationName = "FetchMembersWithoutSource", action = "http://services.ngn.com/2007/03/20/MemberService/FetchMembersWithoutSource")
    @WebResult(name = "FetchMembersResponse", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMembersWithoutSourceResult")
    public FetchMembersResponse fetchMembersWithoutSource(
        @WebParam(name = "FetchMembersWithoutSourceRequest", targetNamespace = "http://services.ngn.com/2007/03/20/MemberService", partName = "FetchMembersWithoutSourceRequest")
        FetchMembersWithoutSourceRequest fetchMembersWithoutSourceRequest);

}
