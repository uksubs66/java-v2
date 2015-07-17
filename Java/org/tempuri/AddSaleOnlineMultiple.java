
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="pRequests" type="{http://tempuri.org/}ArrayOfOnlineSaleRequest" minOccurs="0"/>
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
    "pRequests"
})
@XmlRootElement(name = "AddSaleOnlineMultiple")
public class AddSaleOnlineMultiple {

    protected ArrayOfOnlineSaleRequest pRequests;

    /**
     * Gets the value of the pRequests property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfOnlineSaleRequest }
     *     
     */
    public ArrayOfOnlineSaleRequest getPRequests() {
        return pRequests;
    }

    /**
     * Sets the value of the pRequests property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfOnlineSaleRequest }
     *     
     */
    public void setPRequests(ArrayOfOnlineSaleRequest value) {
        this.pRequests = value;
    }

}
