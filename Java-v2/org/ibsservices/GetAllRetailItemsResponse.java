
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
 *         &lt;element name="GetAllRetailItemsResult" type="{http://ibsservices.org/}ArrayOfRetailInventoryItem" minOccurs="0"/>
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
    "getAllRetailItemsResult",
    "asMessage"
})
@XmlRootElement(name = "GetAllRetailItemsResponse")
public class GetAllRetailItemsResponse {

    @XmlElement(name = "GetAllRetailItemsResult")
    protected ArrayOfRetailInventoryItem getAllRetailItemsResult;
    @XmlElement(name = "a_sMessage")
    protected String asMessage;

    /**
     * Gets the value of the getAllRetailItemsResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRetailInventoryItem }
     *     
     */
    public ArrayOfRetailInventoryItem getGetAllRetailItemsResult() {
        return getAllRetailItemsResult;
    }

    /**
     * Sets the value of the getAllRetailItemsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRetailInventoryItem }
     *     
     */
    public void setGetAllRetailItemsResult(ArrayOfRetailInventoryItem value) {
        this.getAllRetailItemsResult = value;
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
