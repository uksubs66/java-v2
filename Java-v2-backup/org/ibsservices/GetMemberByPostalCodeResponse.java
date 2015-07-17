
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
 *         &lt;element name="GetMemberByPostalCodeResult" type="{http://ibsservices.org/}ArrayOfMemberData" minOccurs="0"/>
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
    "getMemberByPostalCodeResult",
    "asMessage"
})
@XmlRootElement(name = "GetMemberByPostalCodeResponse")
public class GetMemberByPostalCodeResponse {

    @XmlElement(name = "GetMemberByPostalCodeResult")
    protected ArrayOfMemberData getMemberByPostalCodeResult;
    @XmlElement(name = "a_sMessage")
    protected String asMessage;

    /**
     * Gets the value of the getMemberByPostalCodeResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMemberData }
     *     
     */
    public ArrayOfMemberData getGetMemberByPostalCodeResult() {
        return getMemberByPostalCodeResult;
    }

    /**
     * Sets the value of the getMemberByPostalCodeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMemberData }
     *     
     */
    public void setGetMemberByPostalCodeResult(ArrayOfMemberData value) {
        this.getMemberByPostalCodeResult = value;
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
