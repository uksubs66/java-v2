
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Minimum complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Minimum">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="guid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Minimum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PurchaseAmount" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="MonthsRequired" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Minimum", propOrder = {
    "guid",
    "description",
    "minimum",
    "purchaseAmount",
    "deadline",
    "monthsRequired"
})
public class Minimum {

    protected String guid;
    protected String description;
    @XmlElement(name = "Minimum")
    protected String minimum;
    @XmlElement(name = "PurchaseAmount")
    protected float purchaseAmount;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar deadline;
    @XmlElement(name = "MonthsRequired")
    protected String monthsRequired;

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
     * Gets the value of the minimum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinimum() {
        return minimum;
    }

    /**
     * Sets the value of the minimum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinimum(String value) {
        this.minimum = value;
    }

    /**
     * Gets the value of the purchaseAmount property.
     * 
     */
    public float getPurchaseAmount() {
        return purchaseAmount;
    }

    /**
     * Sets the value of the purchaseAmount property.
     * 
     */
    public void setPurchaseAmount(float value) {
        this.purchaseAmount = value;
    }

    /**
     * Gets the value of the deadline property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeadline() {
        return deadline;
    }

    /**
     * Sets the value of the deadline property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeadline(XMLGregorianCalendar value) {
        this.deadline = value;
    }

    /**
     * Gets the value of the monthsRequired property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonthsRequired() {
        return monthsRequired;
    }

    /**
     * Sets the value of the monthsRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonthsRequired(String value) {
        this.monthsRequired = value;
    }

}
