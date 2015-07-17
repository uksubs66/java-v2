
package com.ngn.services._2007._03._20.handicapservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.ngn.services._2007._03._20.handicapdata.HandicapUpdateList;
import com.ngn.services._2007._03._20.headerdata.Credentials;


/**
 * <p>Java class for UpdateHandicapsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateHandicapsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Credentials" type="{http://services.ngn.com/2007/03/20/HeaderData}Credentials" minOccurs="0"/>
 *         &lt;element name="HandicapList" type="{http://services.ngn.com/2007/03/20/HandicapData}HandicapUpdateList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateHandicapsRequest", propOrder = {
    "transactionId",
    "credentials",
    "handicapList"
})
public class UpdateHandicapsRequest {

    @XmlElement(name = "TransactionId")
    protected String transactionId;
    @XmlElement(name = "Credentials")
    protected Credentials credentials;
    @XmlElement(name = "HandicapList")
    protected HandicapUpdateList handicapList;

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
     * Gets the value of the credentials property.
     * 
     * @return
     *     possible object is
     *     {@link Credentials }
     *     
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Sets the value of the credentials property.
     * 
     * @param value
     *     allowed object is
     *     {@link Credentials }
     *     
     */
    public void setCredentials(Credentials value) {
        this.credentials = value;
    }

    /**
     * Gets the value of the handicapList property.
     * 
     * @return
     *     possible object is
     *     {@link HandicapUpdateList }
     *     
     */
    public HandicapUpdateList getHandicapList() {
        return handicapList;
    }

    /**
     * Sets the value of the handicapList property.
     * 
     * @param value
     *     allowed object is
     *     {@link HandicapUpdateList }
     *     
     */
    public void setHandicapList(HandicapUpdateList value) {
        this.handicapList = value;
    }

}
