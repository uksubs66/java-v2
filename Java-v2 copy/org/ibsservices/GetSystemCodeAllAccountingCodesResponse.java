
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
 *         &lt;element name="GetSystemCodeAllAccountingCodesResult" type="{http://ibsservices.org/}ArrayOfAccountingCodeData" minOccurs="0"/>
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
    "getSystemCodeAllAccountingCodesResult",
    "asMessage"
})
@XmlRootElement(name = "GetSystemCodeAllAccountingCodesResponse")
public class GetSystemCodeAllAccountingCodesResponse {

    @XmlElement(name = "GetSystemCodeAllAccountingCodesResult")
    protected ArrayOfAccountingCodeData getSystemCodeAllAccountingCodesResult;
    @XmlElement(name = "a_sMessage")
    protected String asMessage;

    /**
     * Gets the value of the getSystemCodeAllAccountingCodesResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAccountingCodeData }
     *     
     */
    public ArrayOfAccountingCodeData getGetSystemCodeAllAccountingCodesResult() {
        return getSystemCodeAllAccountingCodesResult;
    }

    /**
     * Sets the value of the getSystemCodeAllAccountingCodesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAccountingCodeData }
     *     
     */
    public void setGetSystemCodeAllAccountingCodesResult(ArrayOfAccountingCodeData value) {
        this.getSystemCodeAllAccountingCodesResult = value;
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
