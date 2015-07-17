
package com.ngn.services._2007._03._20.memberservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.ngn.services._2007._03._20.headerdata.Credentials;


/**
 * <p>Java class for FetchMemberByNetworkIdRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FetchMemberByNetworkIdRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Credentials" type="{http://services.ngn.com/2007/03/20/HeaderData}Credentials" minOccurs="0"/>
 *         &lt;element name="NetworkId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ClubId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ClubManagementGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FetchMemberByNetworkIdRequest", propOrder = {
    "transactionId",
    "credentials",
    "networkId",
    "clubId",
    "clubManagementGroupId"
})
public class FetchMemberByNetworkIdRequest {

    @XmlElement(name = "TransactionId")
    protected String transactionId;
    @XmlElement(name = "Credentials")
    protected Credentials credentials;
    @XmlElement(name = "NetworkId")
    protected int networkId;
    @XmlElement(name = "ClubId")
    protected int clubId;
    @XmlElement(name = "ClubManagementGroupId")
    protected int clubManagementGroupId;

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
     * Gets the value of the networkId property.
     * 
     */
    public int getNetworkId() {
        return networkId;
    }

    /**
     * Sets the value of the networkId property.
     * 
     */
    public void setNetworkId(int value) {
        this.networkId = value;
    }

    /**
     * Gets the value of the clubId property.
     * 
     */
    public int getClubId() {
        return clubId;
    }

    /**
     * Sets the value of the clubId property.
     * 
     */
    public void setClubId(int value) {
        this.clubId = value;
    }

    /**
     * Gets the value of the clubManagementGroupId property.
     * 
     */
    public int getClubManagementGroupId() {
        return clubManagementGroupId;
    }

    /**
     * Sets the value of the clubManagementGroupId property.
     * 
     */
    public void setClubManagementGroupId(int value) {
        this.clubManagementGroupId = value;
    }

}