
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for DiscountCodeData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DiscountCodeData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="guid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AccountingCode" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="Stat" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="PromptForAmount" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="DiscountAmount" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="StartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="EndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="StartTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="EndTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ValidDays" type="{http://ibsservices.org/}Week" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DiscountCodeData", propOrder = {
    "guid",
    "description",
    "type",
    "accountingCode",
    "stat",
    "promptForAmount",
    "discountAmount",
    "startDate",
    "endDate",
    "startTime",
    "endTime",
    "validDays"
})
public class DiscountCodeData {

    protected String guid;
    protected String description;
    protected String type;
    @XmlElement(name = "AccountingCode")
    protected CodeDescriptionPair accountingCode;
    @XmlElement(name = "Stat")
    protected CodeDescriptionPair stat;
    @XmlElement(name = "PromptForAmount")
    protected boolean promptForAmount;
    @XmlElement(name = "DiscountAmount")
    protected boolean discountAmount;
    @XmlElement(name = "StartDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startDate;
    @XmlElement(name = "EndDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endDate;
    @XmlElement(name = "StartTime")
    protected long startTime;
    @XmlElement(name = "EndTime")
    protected long endTime;
    @XmlElement(name = "ValidDays")
    protected Week validDays;

    /**
     * Gets the value of the guid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuid(String value) {
        this.guid = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the accountingCode property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getAccountingCode() {
        return accountingCode;
    }

    /**
     * Sets the value of the accountingCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setAccountingCode(CodeDescriptionPair value) {
        this.accountingCode = value;
    }

    /**
     * Gets the value of the stat property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getStat() {
        return stat;
    }

    /**
     * Sets the value of the stat property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setStat(CodeDescriptionPair value) {
        this.stat = value;
    }

    /**
     * Gets the value of the promptForAmount property.
     * 
     */
    public boolean isPromptForAmount() {
        return promptForAmount;
    }

    /**
     * Sets the value of the promptForAmount property.
     * 
     */
    public void setPromptForAmount(boolean value) {
        this.promptForAmount = value;
    }

    /**
     * Gets the value of the discountAmount property.
     * 
     */
    public boolean isDiscountAmount() {
        return discountAmount;
    }

    /**
     * Sets the value of the discountAmount property.
     * 
     */
    public void setDiscountAmount(boolean value) {
        this.discountAmount = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
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
     * Gets the value of the startTime property.
     * 
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     */
    public void setStartTime(long value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     */
    public void setEndTime(long value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the validDays property.
     * 
     * @return
     *     possible object is
     *     {@link Week }
     *     
     */
    public Week getValidDays() {
        return validDays;
    }

    /**
     * Sets the value of the validDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link Week }
     *     
     */
    public void setValidDays(Week value) {
        this.validDays = value;
    }

}
