
package com.ngn.services._2007._03._20.memberservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.ngn.services._2007._03._20.memberdata.MemberList;


/**
 * <p>Java class for FetchMembersResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FetchMembersResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MemberList" type="{http://services.ngn.com/2007/03/20/MemberData}MemberList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FetchMembersResponse", propOrder = {
    "transactionId",
    "memberList"
})
public class FetchMembersResponse {

    @XmlElement(name = "TransactionId")
    protected String transactionId;
    @XmlElement(name = "MemberList")
    protected MemberList memberList;

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
     * Gets the value of the memberList property.
     * 
     * @return
     *     possible object is
     *     {@link MemberList }
     *     
     */
    public MemberList getMemberList() {
        return memberList;
    }

    /**
     * Sets the value of the memberList property.
     * 
     * @param value
     *     allowed object is
     *     {@link MemberList }
     *     
     */
    public void setMemberList(MemberList value) {
        this.memberList = value;
    }

}
