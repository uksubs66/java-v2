
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ClubDiningItemBaseClass complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClubDiningItemBaseClass">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InvItemID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TaxIncluded" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PriceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EventDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="PriceCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeptID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClubDiningItemBaseClass", propOrder = {
    "invItemID",
    "number",
    "type",
    "description",
    "taxIncluded",
    "priceType",
    "eventDate",
    "priceCategory",
    "price",
    "deptID"
})
@XmlSeeAlso({
    ClubDiningRetailInventoryItem.class,
    ClubDiningFBInventoryItem.class
})
public class ClubDiningItemBaseClass {

    @XmlElement(name = "InvItemID")
    protected String invItemID;
    @XmlElement(name = "Number")
    protected String number;
    @XmlElement(name = "Type")
    protected String type;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "TaxIncluded")
    protected boolean taxIncluded;
    @XmlElement(name = "PriceType")
    protected String priceType;
    @XmlElement(name = "EventDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar eventDate;
    @XmlElement(name = "PriceCategory")
    protected String priceCategory;
    @XmlElement(name = "Price")
    protected String price;
    @XmlElement(name = "DeptID")
    protected String deptID;

    /**
     * Gets the value of the invItemID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvItemID() {
        return invItemID;
    }

    /**
     * Sets the value of the invItemID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvItemID(String value) {
        this.invItemID = value;
    }

    /**
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
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
     * Gets the value of the taxIncluded property.
     * 
     */
    public boolean isTaxIncluded() {
        return taxIncluded;
    }

    /**
     * Sets the value of the taxIncluded property.
     * 
     */
    public void setTaxIncluded(boolean value) {
        this.taxIncluded = value;
    }

    /**
     * Gets the value of the priceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriceType() {
        return priceType;
    }

    /**
     * Sets the value of the priceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriceType(String value) {
        this.priceType = value;
    }

    /**
     * Gets the value of the eventDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEventDate() {
        return eventDate;
    }

    /**
     * Sets the value of the eventDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEventDate(XMLGregorianCalendar value) {
        this.eventDate = value;
    }

    /**
     * Gets the value of the priceCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriceCategory() {
        return priceCategory;
    }

    /**
     * Sets the value of the priceCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriceCategory(String value) {
        this.priceCategory = value;
    }

    /**
     * Gets the value of the price property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrice(String value) {
        this.price = value;
    }

    /**
     * Gets the value of the deptID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeptID() {
        return deptID;
    }

    /**
     * Sets the value of the deptID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeptID(String value) {
        this.deptID = value;
    }

}
