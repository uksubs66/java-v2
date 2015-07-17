
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for MemberInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MemberInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PasswordQuestion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PasswordAnswer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Salutation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MiddleInitial" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NickName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HomePhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkPhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MobilePhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MobileCarrierType" type="{http://services.ngn.com/2007/03/20/MemberData}MobileCarrierType"/>
 *         &lt;element name="ClubName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Gender" type="{http://services.ngn.com/2007/03/20/MemberData}Gender"/>
 *         &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="Culture" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Employer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="JobTitle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AddressLine1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AddressLine2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Region" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CountryCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SourceClubId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MembershipLevel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MembershipType" type="{http://services.ngn.com/2007/03/20/MemberData}MembershipType"/>
 *         &lt;element name="MembershipCategory" type="{http://services.ngn.com/2007/03/20/MemberData}MembershipCategory"/>
 *         &lt;element name="TournamentTeeType" type="{http://services.ngn.com/2007/03/20/MemberData}TournamentTeeType"/>
 *         &lt;element name="Division" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExpirationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="TransferFromSourceClubId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SendWelcomeEmail" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OptInAllEmail" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="FriendlyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="LocalId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="BagStorageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LockerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsActive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsCardActive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="CardId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AllowNotifications" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AllowNewsletters" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AllowPrintNewsLetters" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ClubGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CreatedOn" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="UpdatedOn" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="Action" type="{http://services.ngn.com/2007/03/20/MemberData}MemberAction"/>
 *         &lt;element name="HandicapInfo" type="{http://services.ngn.com/2007/03/20/MemberData}ArrayOfMemberHandicapInfo" minOccurs="0"/>
 *         &lt;element name="SubGroups" type="{http://services.ngn.com/2007/03/20/MemberData}ArrayOfMemberSubgroups" minOccurs="0"/>
 *         &lt;element name="ClubMemberships" type="{http://services.ngn.com/2007/03/20/MemberData}ArrayOfMemberClubMemberships" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="SourceUserId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MemberInfo", propOrder = {
    "username",
    "password",
    "passwordQuestion",
    "passwordAnswer",
    "email",
    "salutation",
    "firstName",
    "lastName",
    "middleInitial",
    "nickName",
    "homePhone",
    "workPhone",
    "mobilePhone",
    "mobileCarrierType",
    "clubName",
    "gender",
    "dateOfBirth",
    "culture",
    "employer",
    "jobTitle",
    "addressLine1",
    "addressLine2",
    "city",
    "region",
    "postalCode",
    "countryCode",
    "sourceClubId",
    "membershipLevel",
    "membershipType",
    "membershipCategory",
    "tournamentTeeType",
    "division",
    "expirationDate",
    "transferFromSourceClubId",
    "sendWelcomeEmail",
    "optInAllEmail",
    "friendlyId",
    "localId",
    "bagStorageId",
    "lockerId",
    "isActive",
    "isCardActive",
    "cardId",
    "allowNotifications",
    "allowNewsletters",
    "allowPrintNewsLetters",
    "clubGroupId",
    "createdOn",
    "updatedOn",
    "action",
    "handicapInfo",
    "subGroups",
    "clubMemberships"
})
public class MemberInfo {

    @XmlElement(name = "Username")
    protected String username;
    @XmlElement(name = "Password")
    protected String password;
    @XmlElement(name = "PasswordQuestion")
    protected String passwordQuestion;
    @XmlElement(name = "PasswordAnswer")
    protected String passwordAnswer;
    @XmlElement(name = "Email", required = true, nillable = true)
    protected String email;
    @XmlElement(name = "Salutation", required = true, nillable = true)
    protected String salutation;
    @XmlElement(name = "FirstName")
    protected String firstName;
    @XmlElement(name = "LastName")
    protected String lastName;
    @XmlElement(name = "MiddleInitial", required = true, nillable = true)
    protected String middleInitial;
    @XmlElement(name = "NickName", required = true, nillable = true)
    protected String nickName;
    @XmlElement(name = "HomePhone", required = true, nillable = true)
    protected String homePhone;
    @XmlElement(name = "WorkPhone", required = true, nillable = true)
    protected String workPhone;
    @XmlElement(name = "MobilePhone", required = true, nillable = true)
    protected String mobilePhone;
    @XmlElement(name = "MobileCarrierType", required = true, nillable = true)
    protected MobileCarrierType mobileCarrierType;
    @XmlElement(name = "ClubName", required = true, nillable = true)
    protected String clubName;
    @XmlElement(name = "Gender", required = true)
    protected Gender gender;
    @XmlElement(name = "DateOfBirth", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    @XmlElement(name = "Culture")
    protected String culture;
    @XmlElement(name = "Employer", required = true, nillable = true)
    protected String employer;
    @XmlElement(name = "JobTitle", required = true, nillable = true)
    protected String jobTitle;
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
    @XmlElement(name = "SourceClubId")
    protected String sourceClubId;
    @XmlElement(name = "MembershipLevel", required = true, nillable = true)
    protected String membershipLevel;
    @XmlElement(name = "MembershipType", required = true, nillable = true)
    protected MembershipType membershipType;
    @XmlElement(name = "MembershipCategory", required = true, nillable = true)
    protected MembershipCategory membershipCategory;
    @XmlElement(name = "TournamentTeeType", required = true, nillable = true)
    protected TournamentTeeType tournamentTeeType;
    @XmlElement(name = "Division", required = true, nillable = true)
    protected String division;
    @XmlElement(name = "ExpirationDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar expirationDate;
    @XmlElement(name = "TransferFromSourceClubId", required = true, nillable = true)
    protected String transferFromSourceClubId;
    @XmlElement(name = "SendWelcomeEmail")
    protected boolean sendWelcomeEmail;
    @XmlElement(name = "OptInAllEmail")
    protected boolean optInAllEmail;
    @XmlElement(name = "FriendlyId")
    protected int friendlyId;
    @XmlElement(name = "LocalId")
    protected int localId;
    @XmlElement(name = "BagStorageId")
    protected String bagStorageId;
    @XmlElement(name = "LockerId")
    protected String lockerId;
    @XmlElement(name = "IsActive")
    protected boolean isActive;
    @XmlElement(name = "IsCardActive")
    protected boolean isCardActive;
    @XmlElement(name = "CardId")
    protected String cardId;
    @XmlElement(name = "AllowNotifications")
    protected boolean allowNotifications;
    @XmlElement(name = "AllowNewsletters")
    protected boolean allowNewsletters;
    @XmlElement(name = "AllowPrintNewsLetters")
    protected boolean allowPrintNewsLetters;
    @XmlElement(name = "ClubGroupId")
    protected int clubGroupId;
    @XmlElement(name = "CreatedOn", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdOn;
    @XmlElement(name = "UpdatedOn", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar updatedOn;
    @XmlElement(name = "Action", required = true)
    protected MemberAction action;
    @XmlElement(name = "HandicapInfo")
    protected ArrayOfMemberHandicapInfo handicapInfo;
    @XmlElement(name = "SubGroups")
    protected ArrayOfMemberSubgroups subGroups;
    @XmlElement(name = "ClubMemberships")
    protected ArrayOfMemberClubMemberships clubMemberships;
    @XmlAttribute(name = "SourceUserId")
    protected String sourceUserId;
    @XmlAttribute(name = "TransactionId")
    protected String transactionId;

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the passwordQuestion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPasswordQuestion() {
        return passwordQuestion;
    }

    /**
     * Sets the value of the passwordQuestion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPasswordQuestion(String value) {
        this.passwordQuestion = value;
    }

    /**
     * Gets the value of the passwordAnswer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPasswordAnswer() {
        return passwordAnswer;
    }

    /**
     * Sets the value of the passwordAnswer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPasswordAnswer(String value) {
        this.passwordAnswer = value;
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
     * Gets the value of the salutation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalutation() {
        return salutation;
    }

    /**
     * Sets the value of the salutation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalutation(String value) {
        this.salutation = value;
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
     * Gets the value of the middleInitial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleInitial() {
        return middleInitial;
    }

    /**
     * Sets the value of the middleInitial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleInitial(String value) {
        this.middleInitial = value;
    }

    /**
     * Gets the value of the nickName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Sets the value of the nickName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNickName(String value) {
        this.nickName = value;
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
     * Gets the value of the clubName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClubName() {
        return clubName;
    }

    /**
     * Sets the value of the clubName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClubName(String value) {
        this.clubName = value;
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
     * Gets the value of the culture property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCulture() {
        return culture;
    }

    /**
     * Sets the value of the culture property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCulture(String value) {
        this.culture = value;
    }

    /**
     * Gets the value of the employer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployer() {
        return employer;
    }

    /**
     * Sets the value of the employer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployer(String value) {
        this.employer = value;
    }

    /**
     * Gets the value of the jobTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets the value of the jobTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobTitle(String value) {
        this.jobTitle = value;
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

    /**
     * Gets the value of the membershipLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMembershipLevel() {
        return membershipLevel;
    }

    /**
     * Sets the value of the membershipLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMembershipLevel(String value) {
        this.membershipLevel = value;
    }

    /**
     * Gets the value of the membershipType property.
     * 
     * @return
     *     possible object is
     *     {@link MembershipType }
     *     
     */
    public MembershipType getMembershipType() {
        return membershipType;
    }

    /**
     * Sets the value of the membershipType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MembershipType }
     *     
     */
    public void setMembershipType(MembershipType value) {
        this.membershipType = value;
    }

    /**
     * Gets the value of the membershipCategory property.
     * 
     * @return
     *     possible object is
     *     {@link MembershipCategory }
     *     
     */
    public MembershipCategory getMembershipCategory() {
        return membershipCategory;
    }

    /**
     * Sets the value of the membershipCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link MembershipCategory }
     *     
     */
    public void setMembershipCategory(MembershipCategory value) {
        this.membershipCategory = value;
    }

    /**
     * Gets the value of the tournamentTeeType property.
     * 
     * @return
     *     possible object is
     *     {@link TournamentTeeType }
     *     
     */
    public TournamentTeeType getTournamentTeeType() {
        return tournamentTeeType;
    }

    /**
     * Sets the value of the tournamentTeeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TournamentTeeType }
     *     
     */
    public void setTournamentTeeType(TournamentTeeType value) {
        this.tournamentTeeType = value;
    }

    /**
     * Gets the value of the division property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDivision() {
        return division;
    }

    /**
     * Sets the value of the division property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDivision(String value) {
        this.division = value;
    }

    /**
     * Gets the value of the expirationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the value of the expirationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpirationDate(XMLGregorianCalendar value) {
        this.expirationDate = value;
    }

    /**
     * Gets the value of the transferFromSourceClubId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransferFromSourceClubId() {
        return transferFromSourceClubId;
    }

    /**
     * Sets the value of the transferFromSourceClubId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransferFromSourceClubId(String value) {
        this.transferFromSourceClubId = value;
    }

    /**
     * Gets the value of the sendWelcomeEmail property.
     * 
     */
    public boolean isSendWelcomeEmail() {
        return sendWelcomeEmail;
    }

    /**
     * Sets the value of the sendWelcomeEmail property.
     * 
     */
    public void setSendWelcomeEmail(boolean value) {
        this.sendWelcomeEmail = value;
    }

    /**
     * Gets the value of the optInAllEmail property.
     * 
     */
    public boolean isOptInAllEmail() {
        return optInAllEmail;
    }

    /**
     * Sets the value of the optInAllEmail property.
     * 
     */
    public void setOptInAllEmail(boolean value) {
        this.optInAllEmail = value;
    }

    /**
     * Gets the value of the friendlyId property.
     * 
     */
    public int getFriendlyId() {
        return friendlyId;
    }

    /**
     * Sets the value of the friendlyId property.
     * 
     */
    public void setFriendlyId(int value) {
        this.friendlyId = value;
    }

    /**
     * Gets the value of the localId property.
     * 
     */
    public int getLocalId() {
        return localId;
    }

    /**
     * Sets the value of the localId property.
     * 
     */
    public void setLocalId(int value) {
        this.localId = value;
    }

    /**
     * Gets the value of the bagStorageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBagStorageId() {
        return bagStorageId;
    }

    /**
     * Sets the value of the bagStorageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBagStorageId(String value) {
        this.bagStorageId = value;
    }

    /**
     * Gets the value of the lockerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLockerId() {
        return lockerId;
    }

    /**
     * Sets the value of the lockerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLockerId(String value) {
        this.lockerId = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     */
    public boolean isIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     */
    public void setIsActive(boolean value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the isCardActive property.
     * 
     */
    public boolean isIsCardActive() {
        return isCardActive;
    }

    /**
     * Sets the value of the isCardActive property.
     * 
     */
    public void setIsCardActive(boolean value) {
        this.isCardActive = value;
    }

    /**
     * Gets the value of the cardId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardId() {
        return cardId;
    }

    /**
     * Sets the value of the cardId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardId(String value) {
        this.cardId = value;
    }

    /**
     * Gets the value of the allowNotifications property.
     * 
     */
    public boolean isAllowNotifications() {
        return allowNotifications;
    }

    /**
     * Sets the value of the allowNotifications property.
     * 
     */
    public void setAllowNotifications(boolean value) {
        this.allowNotifications = value;
    }

    /**
     * Gets the value of the allowNewsletters property.
     * 
     */
    public boolean isAllowNewsletters() {
        return allowNewsletters;
    }

    /**
     * Sets the value of the allowNewsletters property.
     * 
     */
    public void setAllowNewsletters(boolean value) {
        this.allowNewsletters = value;
    }

    /**
     * Gets the value of the allowPrintNewsLetters property.
     * 
     */
    public boolean isAllowPrintNewsLetters() {
        return allowPrintNewsLetters;
    }

    /**
     * Sets the value of the allowPrintNewsLetters property.
     * 
     */
    public void setAllowPrintNewsLetters(boolean value) {
        this.allowPrintNewsLetters = value;
    }

    /**
     * Gets the value of the clubGroupId property.
     * 
     */
    public int getClubGroupId() {
        return clubGroupId;
    }

    /**
     * Sets the value of the clubGroupId property.
     * 
     */
    public void setClubGroupId(int value) {
        this.clubGroupId = value;
    }

    /**
     * Gets the value of the createdOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreatedOn() {
        return createdOn;
    }

    /**
     * Sets the value of the createdOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreatedOn(XMLGregorianCalendar value) {
        this.createdOn = value;
    }

    /**
     * Gets the value of the updatedOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Sets the value of the updatedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUpdatedOn(XMLGregorianCalendar value) {
        this.updatedOn = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link MemberAction }
     *     
     */
    public MemberAction getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link MemberAction }
     *     
     */
    public void setAction(MemberAction value) {
        this.action = value;
    }

    /**
     * Gets the value of the handicapInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMemberHandicapInfo }
     *     
     */
    public ArrayOfMemberHandicapInfo getHandicapInfo() {
        return handicapInfo;
    }

    /**
     * Sets the value of the handicapInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMemberHandicapInfo }
     *     
     */
    public void setHandicapInfo(ArrayOfMemberHandicapInfo value) {
        this.handicapInfo = value;
    }

    /**
     * Gets the value of the subGroups property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMemberSubgroups }
     *     
     */
    public ArrayOfMemberSubgroups getSubGroups() {
        return subGroups;
    }

    /**
     * Sets the value of the subGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMemberSubgroups }
     *     
     */
    public void setSubGroups(ArrayOfMemberSubgroups value) {
        this.subGroups = value;
    }

    /**
     * Gets the value of the clubMemberships property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMemberClubMemberships }
     *     
     */
    public ArrayOfMemberClubMemberships getClubMemberships() {
        return clubMemberships;
    }

    /**
     * Sets the value of the clubMemberships property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMemberClubMemberships }
     *     
     */
    public void setClubMemberships(ArrayOfMemberClubMemberships value) {
        this.clubMemberships = value;
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
