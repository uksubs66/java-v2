
package com.ngn.services._2007._03._20.coursedata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClubServiceInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClubServiceInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Address1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Address2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Region" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CountryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsActive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ClubCourses" type="{http://services.ngn.com/2007/03/20/CourseData}ArrayOfClubCourses" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ClubId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="ClubManagementGroupId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClubServiceInfo", propOrder = {
    "name",
    "address1",
    "address2",
    "city",
    "region",
    "postalCode",
    "countryCode",
    "isActive",
    "clubCourses"
})
public class ClubServiceInfo {

    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Address1")
    protected String address1;
    @XmlElement(name = "Address2")
    protected String address2;
    @XmlElement(name = "City")
    protected String city;
    @XmlElement(name = "Region")
    protected String region;
    @XmlElement(name = "PostalCode")
    protected String postalCode;
    @XmlElement(name = "CountryCode")
    protected String countryCode;
    @XmlElement(name = "IsActive")
    protected boolean isActive;
    @XmlElement(name = "ClubCourses")
    protected ArrayOfClubCourses clubCourses;
    @XmlAttribute(name = "ClubId", required = true)
    protected int clubId;
    @XmlAttribute(name = "ClubManagementGroupId", required = true)
    protected int clubManagementGroupId;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the address1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Sets the value of the address1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress1(String value) {
        this.address1 = value;
    }

    /**
     * Gets the value of the address2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Sets the value of the address2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress2(String value) {
        this.address2 = value;
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
     * Gets the value of the clubCourses property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfClubCourses }
     *     
     */
    public ArrayOfClubCourses getClubCourses() {
        return clubCourses;
    }

    /**
     * Sets the value of the clubCourses property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfClubCourses }
     *     
     */
    public void setClubCourses(ArrayOfClubCourses value) {
        this.clubCourses = value;
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
