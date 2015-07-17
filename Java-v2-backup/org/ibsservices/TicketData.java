
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for TicketData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TicketData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TicketDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Number" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Subtotal" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="ServiceCharge" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Gratuities" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Surcharges" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Tax1" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Tax2" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Tax3" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="TotalCharges" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="ChitInternalID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Register" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PurchaserExtension" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PurchaserFirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PurchaserLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ChitDetails" type="{http://ibsservices.org/}ArrayOfChitDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TicketData", propOrder = {
    "ticketDate",
    "number",
    "description",
    "subtotal",
    "serviceCharge",
    "gratuities",
    "surcharges",
    "tax1",
    "tax2",
    "tax3",
    "totalCharges",
    "chitInternalID",
    "register",
    "purchaserExtension",
    "purchaserFirstName",
    "purchaserLastName",
    "chitDetails"
})
public class TicketData {

    @XmlElement(name = "TicketDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar ticketDate;
    @XmlElement(name = "Number")
    protected long number;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "Subtotal")
    protected float subtotal;
    @XmlElement(name = "ServiceCharge")
    protected float serviceCharge;
    @XmlElement(name = "Gratuities")
    protected float gratuities;
    @XmlElement(name = "Surcharges")
    protected float surcharges;
    @XmlElement(name = "Tax1")
    protected float tax1;
    @XmlElement(name = "Tax2")
    protected float tax2;
    @XmlElement(name = "Tax3")
    protected float tax3;
    @XmlElement(name = "TotalCharges")
    protected float totalCharges;
    @XmlElement(name = "ChitInternalID")
    protected String chitInternalID;
    @XmlElement(name = "Register")
    protected String register;
    @XmlElement(name = "PurchaserExtension")
    protected String purchaserExtension;
    @XmlElement(name = "PurchaserFirstName")
    protected String purchaserFirstName;
    @XmlElement(name = "PurchaserLastName")
    protected String purchaserLastName;
    @XmlElement(name = "ChitDetails")
    protected ArrayOfChitDetail chitDetails;

    /**
     * Gets the value of the ticketDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTicketDate() {
        return ticketDate;
    }

    /**
     * Sets the value of the ticketDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTicketDate(XMLGregorianCalendar value) {
        this.ticketDate = value;
    }

    /**
     * Gets the value of the number property.
     * 
     */
    public long getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     */
    public void setNumber(long value) {
        this.number = value;
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
     * Gets the value of the subtotal property.
     * 
     */
    public float getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the value of the subtotal property.
     * 
     */
    public void setSubtotal(float value) {
        this.subtotal = value;
    }

    /**
     * Gets the value of the serviceCharge property.
     * 
     */
    public float getServiceCharge() {
        return serviceCharge;
    }

    /**
     * Sets the value of the serviceCharge property.
     * 
     */
    public void setServiceCharge(float value) {
        this.serviceCharge = value;
    }

    /**
     * Gets the value of the gratuities property.
     * 
     */
    public float getGratuities() {
        return gratuities;
    }

    /**
     * Sets the value of the gratuities property.
     * 
     */
    public void setGratuities(float value) {
        this.gratuities = value;
    }

    /**
     * Gets the value of the surcharges property.
     * 
     */
    public float getSurcharges() {
        return surcharges;
    }

    /**
     * Sets the value of the surcharges property.
     * 
     */
    public void setSurcharges(float value) {
        this.surcharges = value;
    }

    /**
     * Gets the value of the tax1 property.
     * 
     */
    public float getTax1() {
        return tax1;
    }

    /**
     * Sets the value of the tax1 property.
     * 
     */
    public void setTax1(float value) {
        this.tax1 = value;
    }

    /**
     * Gets the value of the tax2 property.
     * 
     */
    public float getTax2() {
        return tax2;
    }

    /**
     * Sets the value of the tax2 property.
     * 
     */
    public void setTax2(float value) {
        this.tax2 = value;
    }

    /**
     * Gets the value of the tax3 property.
     * 
     */
    public float getTax3() {
        return tax3;
    }

    /**
     * Sets the value of the tax3 property.
     * 
     */
    public void setTax3(float value) {
        this.tax3 = value;
    }

    /**
     * Gets the value of the totalCharges property.
     * 
     */
    public float getTotalCharges() {
        return totalCharges;
    }

    /**
     * Sets the value of the totalCharges property.
     * 
     */
    public void setTotalCharges(float value) {
        this.totalCharges = value;
    }

    /**
     * Gets the value of the chitInternalID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChitInternalID() {
        return chitInternalID;
    }

    /**
     * Sets the value of the chitInternalID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChitInternalID(String value) {
        this.chitInternalID = value;
    }

    /**
     * Gets the value of the register property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegister() {
        return register;
    }

    /**
     * Sets the value of the register property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegister(String value) {
        this.register = value;
    }

    /**
     * Gets the value of the purchaserExtension property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaserExtension() {
        return purchaserExtension;
    }

    /**
     * Sets the value of the purchaserExtension property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaserExtension(String value) {
        this.purchaserExtension = value;
    }

    /**
     * Gets the value of the purchaserFirstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaserFirstName() {
        return purchaserFirstName;
    }

    /**
     * Sets the value of the purchaserFirstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaserFirstName(String value) {
        this.purchaserFirstName = value;
    }

    /**
     * Gets the value of the purchaserLastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaserLastName() {
        return purchaserLastName;
    }

    /**
     * Sets the value of the purchaserLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaserLastName(String value) {
        this.purchaserLastName = value;
    }

    /**
     * Gets the value of the chitDetails property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfChitDetail }
     *     
     */
    public ArrayOfChitDetail getChitDetails() {
        return chitDetails;
    }

    /**
     * Sets the value of the chitDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfChitDetail }
     *     
     */
    public void setChitDetails(ArrayOfChitDetail value) {
        this.chitDetails = value;
    }

}
