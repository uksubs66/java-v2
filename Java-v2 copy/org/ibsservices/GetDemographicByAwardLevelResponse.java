
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
 *         &lt;element name="GetDemographicByAwardLevelResult" type="{http://ibsservices.org/}ArrayOfDemographicData" minOccurs="0"/>
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
    "getDemographicByAwardLevelResult",
    "asMessage"
})
@XmlRootElement(name = "GetDemographicByAwardLevelResponse")
public class GetDemographicByAwardLevelResponse {

    @XmlElement(name = "GetDemographicByAwardLevelResult")
    protected ArrayOfDemographicData getDemographicByAwardLevelResult;
    @XmlElement(name = "a_sMessage")
    protected String asMessage;

    /**
     * Gets the value of the getDemographicByAwardLevelResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfDemographicData }
     *     
     */
    public ArrayOfDemographicData getGetDemographicByAwardLevelResult() {
        return getDemographicByAwardLevelResult;
    }

    /**
     * Sets the value of the getDemographicByAwardLevelResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfDemographicData }
     *     
     */
    public void setGetDemographicByAwardLevelResult(ArrayOfDemographicData value) {
        this.getDemographicByAwardLevelResult = value;
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
