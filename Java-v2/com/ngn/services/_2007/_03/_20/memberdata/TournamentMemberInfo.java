
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for TournamentMemberInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TournamentMemberInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SourceUserId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TournamentClubAssocGUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HomePhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkPhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MobilePhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MobileCarrierType" type="{http://services.ngn.com/2007/03/20/MemberData}MobileCarrierType"/>
 *         &lt;element name="Gender" type="{http://services.ngn.com/2007/03/20/MemberData}Gender"/>
 *         &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="AddressLine1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AddressLine2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Region" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CountryCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Action" type="{http://services.ngn.com/2007/03/20/MemberData}MemberTournamentAction"/>
 *       &lt;/sequence>
 *       &lt;attribute name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TournamentMemberInfo", propOrder = {
    "email",
    "firstName",
    "lastName",
    "sourceUserId",
    "tournamentClubAssocGUID",
    "homePhone",
    "workPhone",
    "mobilePhone",
    "mobileCarrierType",
    "gender",
    "dateOfBirth",
    "addressLine1",
    "addressLine2",
    "city",
    "region",
    "postalCode",
    "countryCode",
    "action"
})
public class TournamentMemberInfo {

    @XmlElement(name = "Email", required = true, nillable = true)
    protected String email;
    @XmlElement(name = "FirstName")
    protected String firstName;
    @XmlElement(name = "LastName")
    protected String lastName;
    @XmlElement(name = "SourceUserId")
    protected String sourceUserId;
    @XmlElement(name = "TournamentClubAssocGUID")
    protected String tournamentClubAssocGUID;
    @XmlElement(name = "HomePhone", required = true, nillable = true)
    protected String homePhone;
    @XmlElement(name = "WorkPhone", required = true, nillable = true)
    protected String workPhone;
    @XmlElement(name = "MobilePhone", required = true, nillable = true)
    protected String mobilePhone;
    @XmlElement(name = "MobileCarrierType", required = true, nillable = true)
    protected MobileCarrierType mobileCarrierType;
    @XmlElement(name = "Gender", required = true)
    protected Gender gender;
    @XmlElement(name = "DateOfBirth", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    @XmlElement(name = "AddressLine1", required = true, nillable = true)
    protected String addressLine1;
    @XmlElement(name = "AddressLine2", required = true, nillable = true)
    protected String addressLine2;
    @XmlElement(name = "City", required = true, nillable = true)
    protected String city;
    @XmlElement(name = "Region", required = true, nillable = true)
    protected String region;
    @XmlElement(name = "PostalCode", required = true, nillable = true)
    protected String postalCode;
    @XmlElement(name = "CountryCode", required = true, nillable = true)
    protected String countryCode;
    @XmlElement(name = "Action", required = true)
    protected MemberTournamentAction action;
    @XmlAttribute(name = "TransactionId")
    protected String transactionId;

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
     * Gets the value of the sourceUserId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceUserId() {
        return sourceUserId;
    }

    /**
     * Sets the value of the sourceUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceUserId(String value) {
        this.sourceUserId = value;
    }

    /**
     * Gets the value of the tournamentClubAssocGUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTournamentClubAssocGUID() {
        return tournamentClubAssocGUID;
    }

    /**
     * Sets the value of the tournamentClubAssocGUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTournamentClubAssocGUID(String value) {
        this.tournamentClubAssocGUID = value;
    }

    /**
     * Gets the value of the homePhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHomePhone() {
        return homePhone;
    }

    /**
     * Sets the value of the homePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHomePhone(String value) {
        this.homePhone = value;
    }

    /**
     * Gets the value of the workPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkPhone() {
        return workPhone;
    }

    /**
     * Sets the value of the workPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkPhone(String value) {
        this.workPhone = value;
    }

    /**
     * Gets the value of the mobilePhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * Sets the value of the mobilePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobilePhone(String value) {
        this.mobilePhone = value;
    }

    /**
     * Gets the value of the mobileCarrierType property.
     * 
     * @return
     *     possible object is
     *     {@link MobileCarrierType }
     *     
     */
    public MobileCarrierType getMobileCarrierType() {
        return mobileCarrierType;
    }

    /**
     * Sets the value of the mobileCarrierType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileCarrierType }
     *     
     */
    public void setMobileCarrierType(MobileCarrierType value) {
        this.mobileCarrierType = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link Gender }
     *     
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gender }
     *     
     */
    public void setGender(Gender value) {
        this.gender = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the addressLine1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * Sets the value of the addressLine1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressLine1(String value) {
        this.addressLine1 = value;
    }

    /**
     * Gets the value of the addressLine2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * Sets the value of the addressLine2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressLine2(String value) {
        this.addressLine2 = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
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
     * Gets the value of the postalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link MemberTournamentAction }
     *     
     */
    public MemberTournamentAction getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link MemberTournamentAction }
     *     
     */
    public void setAction(MemberTournamentAction value) {
        this.action = value;
    }

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

}
