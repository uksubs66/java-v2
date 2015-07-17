
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DemographicData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DemographicData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ibsservices.org/}MemberData">
 *       &lt;sequence>
 *         &lt;element name="Options" type="{http://ibsservices.org/}Options" minOccurs="0"/>
 *         &lt;element name="Profile" type="{http://ibsservices.org/}Profile" minOccurs="0"/>
 *         &lt;element name="Minimums" type="{http://ibsservices.org/}ArrayOfMinimum" minOccurs="0"/>
 *         &lt;element name="Extensions" type="{http://ibsservices.org/}ArrayOfExtension" minOccurs="0"/>
 *         &lt;element name="Privleges" type="{http://ibsservices.org/}ArrayOfPrivlege" minOccurs="0"/>
 *         &lt;element name="Messages" type="{http://ibsservices.org/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="Loyalty" type="{http://ibsservices.org/}Loyalty" minOccurs="0"/>
 *         &lt;element name="EZPay" type="{http://ibsservices.org/}EZPay" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DemographicData", propOrder = {
    "options",
    "profile",
    "minimums",
    "extensions",
    "privleges",
    "messages",
    "loyalty",
    "ezPay"
})
public class DemographicData
    extends MemberData
{

    @XmlElement(name = "Options")
    protected Options options;
    @XmlElement(name = "Profile")
    protected Profile profile;
    @XmlElement(name = "Minimums")
    protected ArrayOfMinimum minimums;
    @XmlElement(name = "Extensions")
    protected ArrayOfExtension extensions;
    @XmlElement(name = "Privleges")
    protected ArrayOfPrivlege privleges;
    @XmlElement(name = "Messages")
    protected ArrayOfString messages;
    @XmlElement(name = "Loyalty")
    protected Loyalty loyalty;
    @XmlElement(name = "EZPay")
    protected EZPay ezPay;

    /**
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link Options }
     *     
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link Options }
     *     
     */
    public void setOptions(Options value) {
        this.options = value;
    }

    /**
     * Gets the value of the profile property.
     * 
     * @return
     *     possible object is
     *     {@link Profile }
     *     
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     * 
     * @param value
     *     allowed object is
     *     {@link Profile }
     *     
     */
    public void setProfile(Profile value) {
        this.profile = value;
    }

    /**
     * Gets the value of the minimums property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMinimum }
     *     
     */
    public ArrayOfMinimum getMinimums() {
        return minimums;
    }

    /**
     * Sets the value of the minimums property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMinimum }
     *     
     */
    public void setMinimums(ArrayOfMinimum value) {
        this.minimums = value;
    }

    /**
     * Gets the value of the extensions property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfExtension }
     *     
     */
    public ArrayOfExtension getExtensions() {
        return extensions;
    }

    /**
     * Sets the value of the extensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfExtension }
     *     
     */
    public void setExtensions(ArrayOfExtension value) {
        this.extensions = value;
    }

    /**
     * Gets the value of the privleges property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPrivlege }
     *     
     */
    public ArrayOfPrivlege getPrivleges() {
        return privleges;
    }

    /**
     * Sets the value of the privleges property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPrivlege }
     *     
     */
    public void setPrivleges(ArrayOfPrivlege value) {
        this.privleges = value;
    }

    /**
     * Gets the value of the messages property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getMessages() {
        return messages;
    }

    /**
     * Sets the value of the messages property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setMessages(ArrayOfString value) {
        this.messages = value;
    }

    /**
     * Gets the value of the loyalty property.
     * 
     * @return
     *     possible object is
     *     {@link Loyalty }
     *     
     */
    public Loyalty getLoyalty() {
        return loyalty;
    }

    /**
     * Sets the value of the loyalty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Loyalty }
     *     
     */
    public void setLoyalty(Loyalty value) {
        this.loyalty = value;
    }

    /**
     * Gets the value of the ezPay property.
     * 
     * @return
     *     possible object is
     *     {@link EZPay }
     *     
     */
    public EZPay getEZPay() {
        return ezPay;
    }

    /**
     * Sets the value of the ezPay property.
     * 
     * @param value
     *     allowed object is
     *     {@link EZPay }
     *     
     */
    public void setEZPay(EZPay value) {
        this.ezPay = value;
    }

}
