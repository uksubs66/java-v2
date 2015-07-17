
package com.ngn.services._2007._03._20.memberservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.ngn.services._2007._03._20.headerdata.Credentials;


/**
 * <p>Java class for FetchMembersBySearchRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FetchMembersBySearchRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Credentials" type="{http://services.ngn.com/2007/03/20/HeaderData}Credentials" minOccurs="0"/>
 *         &lt;element name="LastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NetworkId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Region" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ClubManagementGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Clubid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SourceClubId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FetchMembersBySearchRequest", propOrder = {
    "transactionId",
    "credentials",
    "lastName",
    "firstName",
    "userName",
    "email",
    "networkId",
    "region",
    "clubManagementGroupId",
    "clubid",
    "sourceClubId"
})
public class FetchMembersBySearchRequest {

    @XmlElement(name = "TransactionId")
    protected String transactionId;
    @XmlElement(name = "Credentials")
    protected Credentials credentials;
    @XmlElement(name = "LastName")
    protected String lastName;
    @XmlElement(name = "FirstName")
    protected String firstName;
    @XmlElement(name = "UserName")
    protected String userName;
    @XmlElement(name = "Email")
    protected String email;
    @XmlElement(name = "NetworkId")
    protected int networkId;
    @XmlElement(name = "Region")
    protected String region;
    @XmlElement(name = "ClubManagementGroupId")
    protected int clubManagementGroupId;
    @XmlElement(name = "Clubid")
    protected int clubid;
    @XmlElement(name = "SourceClubId")
    protected String sourceClubId;

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
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
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
     * Gets the value of the region property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegion(String value) {
        this.region = value;
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

    /**
     * Gets the value of the clubid property.
     * 
     */
    public int getClubid() {
        return clubid;
    }

    /**
     * Sets the value of the clubid property.
     * 
     */
    public void setClubid(int value) {
        this.clubid = value;
    }

    /**
     * Gets the value of the sourceClubId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceClubId() {
        return sourceClubId;
    }

    /**
     * Sets the value of the sourceClubId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceClubId(String value) {
        this.sourceClubId = value;
    }

}
