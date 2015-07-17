
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for FRequestOptions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FRequestOptions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MemberType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="BalanceStatus" type="{http://ibsservices.org/}BalanceStatusTypes"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FRequestOptions", propOrder = {
    "memberType",
    "endDate",
    "balanceStatus"
})
public class FRequestOptions {

    @XmlElement(name = "MemberType")
    protected String memberType;
    @XmlElement(name = "EndDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endDate;
    @XmlElement(name = "BalanceStatus", required = true)
    protected BalanceStatusTypes balanceStatus;

    /**
     * Gets the value of the memberType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMemberType() {
        return memberType;
    }

    /**
     * Sets the value of the memberType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMemberType(String value) {
        this.memberType = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the balanceStatus property.
     * 
     * @return
     *     possible object is
     *     {@link BalanceStatusTypes }
     *     
     */
    public BalanceStatusTypes getBalanceStatus() {
        return balanceStatus;
    }

    /**
     * Sets the value of the balanceStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link BalanceStatusTypes }
     *     
     */
    public void setBalanceStatus(BalanceStatusTypes value) {
        this.balanceStatus = value;
    }

}
