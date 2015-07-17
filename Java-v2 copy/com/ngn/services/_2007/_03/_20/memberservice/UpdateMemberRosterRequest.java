
package com.ngn.services._2007._03._20.memberservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.ngn.services._2007._03._20.headerdata.Credentials;
import com.ngn.services._2007._03._20.memberdata.MemberRosterList;


/**
 * <p>Java class for UpdateMemberRosterRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateMemberRosterRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SourceTournamentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Credentials" type="{http://services.ngn.com/2007/03/20/HeaderData}Credentials" minOccurs="0"/>
 *         &lt;element name="MemberRosterList" type="{http://services.ngn.com/2007/03/20/MemberData}MemberRosterList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateMemberRosterRequest", propOrder = {
    "transactionId",
    "sourceTournamentId",
    "credentials",
    "memberRosterList"
})
public class UpdateMemberRosterRequest {

    @XmlElement(name = "TransactionId")
    protected String transactionId;
    @XmlElement(name = "SourceTournamentId")
    protected String sourceTournamentId;
    @XmlElement(name = "Credentials")
    protected Credentials credentials;
    @XmlElement(name = "MemberRosterList")
    protected MemberRosterList memberRosterList;

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
    }

    /**
     * Gets the value of the sourceTournamentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceTournamentId() {
        return sourceTournamentId;
    }

    /**
     * Sets the value of the sourceTournamentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceTournamentId(String value) {
        this.sourceTournamentId = value;
    }

    /**
     * Gets the value of the credentials property.
     * 
     * @return
     *     possible object is
     *     {@link Credentials }
     *     
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Sets the value of the credentials property.
     * 
     * @param value
     *     allowed object is
     *     {@link Credentials }
     *     
     */
    public void setCredentials(Credentials value) {
        this.credentials = value;
    }

    /**
     * Gets the value of the memberRosterList property.
     * 
     * @return
     *     possible object is
     *     {@link MemberRosterList }
     *     
     */
    public MemberRosterList getMemberRosterList() {
        return memberRosterList;
    }

    /**
     * Sets the value of the memberRosterList property.
     * 
     * @param value
     *     allowed object is
     *     {@link MemberRosterList }
     *     
     */
    public void setMemberRosterList(MemberRosterList value) {
        this.memberRosterList = value;
    }

}
