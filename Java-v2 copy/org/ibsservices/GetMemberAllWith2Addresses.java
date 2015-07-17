
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="a_sUserID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a_sUserPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a_sPrimaryAddressType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a_sSecondaryAddressType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a_sIncludeFreeFields" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a_sMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "asUserID",
    "asUserPass",
    "asPrimaryAddressType",
    "asSecondaryAddressType",
    "asIncludeFreeFields",
    "asMessage"
})
@XmlRootElement(name = "GetMemberAllWith2Addresses")
public class GetMemberAllWith2Addresses {

    @XmlElement(name = "a_sUserID")
    protected String asUserID;
    @XmlElement(name = "a_sUserPass")
    protected String asUserPass;
    @XmlElement(name = "a_sPrimaryAddressType")
    protected String asPrimaryAddressType;
    @XmlElement(name = "a_sSecondaryAddressType")
    protected String asSecondaryAddressType;
    @XmlElement(name = "a_sIncludeFreeFields")
    protected String asIncludeFreeFields;
    @XmlElement(name = "a_sMessage")
    protected String asMessage;

    /**
     * Gets the value of the asUserID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASUserID() {
        return asUserID;
    }

    /**
     * Sets the value of the asUserID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASUserID(String value) {
        this.asUserID = value;
    }

    /**
     * Gets the value of the asUserPass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASUserPass() {
        return asUserPass;
    }

    /**
     * Sets the value of the asUserPass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASUserPass(String value) {
        this.asUserPass = value;
    }

    /**
     * Gets the value of the asPrimaryAddressType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASPrimaryAddressType() {
        return asPrimaryAddressType;
    }

    /**
     * Sets the value of the asPrimaryAddressType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASPrimaryAddressType(String value) {
        this.asPrimaryAddressType = value;
    }

    /**
     * Gets the value of the asSecondaryAddressType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASSecondaryAddressType() {
        return asSecondaryAddressType;
    }

    /**
     * Sets the value of the asSecondaryAddressType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASSecondaryAddressType(String value) {
        this.asSecondaryAddressType = value;
    }

    /**
     * Gets the value of the asIncludeFreeFields property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASIncludeFreeFields() {
        return asIncludeFreeFields;
    }

    /**
     * Sets the value of the asIncludeFreeFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASIncludeFreeFields(String value) {
        this.asIncludeFreeFields = value;
    }

    /**
     * Gets the value of the asMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getASMessage() {
        return asMessage;
    }

    /**
     * Sets the value of the asMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setASMessage(String value) {
        this.asMessage = value;
    }

}
