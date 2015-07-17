
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
 *         &lt;element name="GetSystemCodeAllActiveMinimumsForDepartmentResult" type="{http://ibsservices.org/}ArrayOfMinimumCodeData" minOccurs="0"/>
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
    "getSystemCodeAllActiveMinimumsForDepartmentResult",
    "asMessage"
})
@XmlRootElement(name = "GetSystemCodeAllActiveMinimumsForDepartmentResponse")
public class GetSystemCodeAllActiveMinimumsForDepartmentResponse {

    @XmlElement(name = "GetSystemCodeAllActiveMinimumsForDepartmentResult")
    protected ArrayOfMinimumCodeData getSystemCodeAllActiveMinimumsForDepartmentResult;
    @XmlElement(name = "a_sMessage")
    protected String asMessage;

    /**
     * Gets the value of the getSystemCodeAllActiveMinimumsForDepartmentResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMinimumCodeData }
     *     
     */
    public ArrayOfMinimumCodeData getGetSystemCodeAllActiveMinimumsForDepartmentResult() {
        return getSystemCodeAllActiveMinimumsForDepartmentResult;
    }

    /**
     * Sets the value of the getSystemCodeAllActiveMinimumsForDepartmentResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMinimumCodeData }
     *     
     */
    public void setGetSystemCodeAllActiveMinimumsForDepartmentResult(ArrayOfMinimumCodeData value) {
        this.getSystemCodeAllActiveMinimumsForDepartmentResult = value;
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
