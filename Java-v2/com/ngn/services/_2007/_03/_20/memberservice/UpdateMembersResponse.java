
package com.ngn.services._2007._03._20.memberservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.ngn.services._2007._03._20.memberdata.MemberResponseList;


/**
 * <p>Java class for UpdateMembersResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateMembersResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MemberResponseList" type="{http://services.ngn.com/2007/03/20/MemberData}MemberResponseList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateMembersResponse", propOrder = {
    "transactionId",
    "memberResponseList"
})
public class UpdateMembersResponse {

    @XmlElement(name = "TransactionId")
    protected String transactionId;
    @XmlElement(name = "MemberResponseList")
    protected MemberResponseList memberResponseList;

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
     * Gets the value of the memberResponseList property.
     * 
     * @return
     *     possible object is
     *     {@link MemberResponseList }
     *     
     */
    public MemberResponseList getMemberResponseList() {
        return memberResponseList;
    }

    /**
     * Sets the value of the memberResponseList property.
     * 
     * @param value
     *     allowed object is
     *     {@link MemberResponseList }
     *     
     */
    public void setMemberResponseList(MemberResponseList value) {
        this.memberResponseList = value;
    }

}
