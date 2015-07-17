
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.ngn.services._2007._03._20.commondata.ResponseType;


/**
 * <p>Java class for MemberResponseInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MemberResponseInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Action" type="{http://services.ngn.com/2007/03/20/MemberData}MemberAction"/>
 *         &lt;element name="ResponseType" type="{http://services.ngn.com/2007/03/20/CommonData}ResponseType"/>
 *         &lt;element name="ResponseMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FriendlyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *       &lt;attribute name="SourceUserId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MemberResponseInfo", propOrder = {
    "action",
    "responseType",
    "responseMessage",
    "username",
    "friendlyId"
})
public class MemberResponseInfo {

    @XmlElement(name = "Action", required = true)
    protected MemberAction action;
    @XmlElement(name = "ResponseType", required = true)
    protected ResponseType responseType;
    @XmlElement(name = "ResponseMessage")
    protected String responseMessage;
    @XmlElement(name = "Username")
    protected String username;
    @XmlElement(name = "FriendlyId")
    protected int friendlyId;
    @XmlAttribute(name = "SourceUserId")
    protected String sourceUserId;
    @XmlAttribute(name = "TransactionId")
    protected String transactionId;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link MemberAction }
     *     
     */
    public MemberAction getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link MemberAction }
     *     
     */
    public void setAction(MemberAction value) {
        this.action = value;
    }

    /**
     * Gets the value of the responseType property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseType }
     *     
     */
    public ResponseType getResponseType() {
        return responseType;
    }

    /**
     * Sets the value of the responseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseType }
     *     
     */
    public void setResponseType(ResponseType value) {
        this.responseType = value;
    }

    /**
     * Gets the value of the responseMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * Sets the value of the responseMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseMessage(String value) {
        this.responseMessage = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the friendlyId property.
     * 
     */
    public int getFriendlyId() {
        return friendlyId;
    }

    /**
     * Sets the value of the friendlyId property.
     * 
     */
    public void setFriendlyId(int value) {
        this.friendlyId = value;
    }

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
