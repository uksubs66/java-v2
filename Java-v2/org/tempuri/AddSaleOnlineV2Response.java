
package org.tempuri;

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
 *         &lt;element name="AddSaleOnlineV2Result" type="{http://tempuri.org/}OnlineSaleResult" minOccurs="0"/>
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
    "addSaleOnlineV2Result"
})
@XmlRootElement(name = "AddSaleOnlineV2Response")
public class AddSaleOnlineV2Response {

    @XmlElement(name = "AddSaleOnlineV2Result")
    protected OnlineSaleResult addSaleOnlineV2Result;

    /**
     * Gets the value of the addSaleOnlineV2Result property.
     * 
     * @return
     *     possible object is
     *     {@link OnlineSaleResult }
     *     
     */
    public OnlineSaleResult getAddSaleOnlineV2Result() {
        return addSaleOnlineV2Result;
    }

    /**
     * Sets the value of the addSaleOnlineV2Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link OnlineSaleResult }
     *     
     */
    public void setAddSaleOnlineV2Result(OnlineSaleResult value) {
        this.addSaleOnlineV2Result = value;
    }

}
