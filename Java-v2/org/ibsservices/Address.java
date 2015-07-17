
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Address complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Address">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Address1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Address2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="State" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Country" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="DayPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DayPhoneExt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NightPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NightPhoneExt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MobilePhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FaxPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MasterEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExtensionEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SeasonalStart" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SeasonalEnd" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Address", propOrder = {
    "address1",
    "address2",
    "city",
    "state",
    "postalCode",
    "country",
    "dayPhone",
    "dayPhoneExt",
    "nightPhone",
    "nightPhoneExt",
    "mobilePhone",
    "faxPhone",
    "masterEmail",
    "extensionEmail",
    "type",
    "seasonalStart",
    "seasonalEnd"
})
public class Address {

    @XmlElement(name = "Address1")
    protected String address1;
    @XmlElement(name = "Address2")
    protected String address2;
    @XmlElement(name = "City")
    protected String city;
    @XmlElement(name = "State")
    protected CodeDescriptionPair state;
    @XmlElement(name = "PostalCode")
    protected String postalCode;
    @XmlElement(name = "Country")
    protected CodeDescriptionPair country;
    @XmlElement(name = "DayPhone")
    protected String dayPhone;
    @XmlElement(name = "DayPhoneExt")
    protected String dayPhoneExt;
    @XmlElement(name = "NightPhone")
    protected String nightPhone;
    @XmlElement(name = "NightPhoneExt")
    protected String nightPhoneExt;
    @XmlElement(name = "MobilePhone")
    protected String mobilePhone;
    @XmlElement(name = "FaxPhone")
    protected String faxPhone;
    @XmlElement(name = "MasterEmail")
    protected String masterEmail;
    @XmlElement(name = "ExtensionEmail")
    protected String extensionEmail;
    protected String type;
    @XmlElement(name = "SeasonalStart", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar seasonalStart;
    @XmlElement(name = "SeasonalEnd", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar seasonalEnd;

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
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setState(CodeDescriptionPair value) {
        this.state = value;
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
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setCountry(CodeDescriptionPair value) {
        this.country = value;
    }

    /**
     * Gets the value of the dayPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDayPhone() {
        return dayPhone;
    }

    /**
     * Sets the value of the dayPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDayPhone(String value) {
        this.dayPhone = value;
    }

    /**
     * Gets the value of the dayPhoneExt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDayPhoneExt() {
        return dayPhoneExt;
    }

    /**
     * Sets the value of the dayPhoneExt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDayPhoneExt(String value) {
        this.dayPhoneExt = value;
    }

    /**
     * Gets the value of the nightPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNightPhone() {
        return nightPhone;
    }

    /**
     * Sets the value of the nightPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNightPhone(String value) {
        this.nightPhone = value;
    }

    /**
     * Gets the value of the nightPhoneExt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNightPhoneExt() {
        return nightPhoneExt;
    }

    /**
     * Sets the value of the nightPhoneExt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNightPhoneExt(String value) {
        this.nightPhoneExt = value;
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
     * Gets the value of the faxPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFaxPhone() {
        return faxPhone;
    }

    /**
     * Sets the value of the faxPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFaxPhone(String value) {
        this.faxPhone = value;
    }

    /**
     * Gets the value of the masterEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMasterEmail() {
        return masterEmail;
    }

    /**
     * Sets the value of the masterEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMasterEmail(String value) {
        this.masterEmail = value;
    }

    /**
     * Gets the value of the extensionEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionEmail() {
        return extensionEmail;
    }

    /**
     * Sets the value of the extensionEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionEmail(String value) {
        this.extensionEmail = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the seasonalStart property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSeasonalStart() {
        return seasonalStart;
    }

    /**
     * Sets the value of the seasonalStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSeasonalStart(XMLGregorianCalendar value) {
        this.seasonalStart = value;
    }

    /**
     * Gets the value of the seasonalEnd property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSeasonalEnd() {
        return seasonalEnd;
    }

    /**
     * Sets the value of the seasonalEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSeasonalEnd(XMLGregorianCalendar value) {
        this.seasonalEnd = value;
    }

}
