
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MemberIdSourceInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MemberIdSourceInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SourceUserId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="TargetSourceUserId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MemberIdSourceInfo", propOrder = {
    "sourceUserId"
})
public class MemberIdSourceInfo {

    @XmlElement(name = "SourceUserId")
    protected String sourceUserId;
    @XmlAttribute(name = "TargetSourceUserId")
    protected String targetSourceUserId;
    @XmlAttribute(name = "TransactionId")
    protected String transactionId;

    /**
     * Gets the value of the sourceUserId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceUserId() {
        return sourceUserId;
    }

    /**
     * Sets the value of the sourceUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceUserId(String value) {
        this.sourceUserId = value;
    }

    /**
     * Gets the value of the targetSourceUserId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetSourceUserId() {
        return targetSourceUserId;
    }

    /**
     * Sets the value of the targetSourceUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetSourceUserId(String value) {
        this.targetSourceUserId = value;
    }

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

}
