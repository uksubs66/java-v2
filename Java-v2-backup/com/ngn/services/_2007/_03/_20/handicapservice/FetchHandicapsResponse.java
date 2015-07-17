
package com.ngn.services._2007._03._20.handicapservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.ngn.services._2007._03._20.handicapdata.HandicapList;


/**
 * <p>Java class for FetchHandicapsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FetchHandicapsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HandicapList" type="{http://services.ngn.com/2007/03/20/HandicapData}HandicapList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FetchHandicapsResponse", propOrder = {
    "transactionId",
    "handicapList"
})
public class FetchHandicapsResponse {

    @XmlElement(name = "TransactionId")
    protected String transactionId;
    @XmlElement(name = "HandicapList")
    protected HandicapList handicapList;

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
    }

    /**
     * Gets the value of the handicapList property.
     * 
     * @return
     *     possible object is
     *     {@link HandicapList }
     *     
     */
    public HandicapList getHandicapList() {
        return handicapList;
    }

    /**
     * Sets the value of the handicapList property.
     * 
     * @param value
     *     allowed object is
     *     {@link HandicapList }
     *     
     */
    public void setHandicapList(HandicapList value) {
        this.handicapList = value;
    }

}
