
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MinimumCodeData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MinimumCodeData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="guid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="frequency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Months" type="{http://ibsservices.org/}Months" minOccurs="0"/>
 *         &lt;element name="Accounting" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="Statistic" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="TaxIncluded" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Prepaid" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Terminate" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="TerminatePeriod" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="AddPrivilege" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Privilege" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="DisplayOnRetailRegister" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="DisplayOnFBRegister" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="DisplayOnRetailReceipt" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="DisplayOnFBReceipt" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MinimumCodeData", propOrder = {
    "guid",
    "description",
    "frequency",
    "months",
    "accounting",
    "statistic",
    "taxIncluded",
    "prepaid",
    "terminate",
    "terminatePeriod",
    "addPrivilege",
    "privilege",
    "displayOnRetailRegister",
    "displayOnFBRegister",
    "displayOnRetailReceipt",
    "displayOnFBReceipt"
})
public class MinimumCodeData {

    protected String guid;
    @XmlElement(name = "Description")
    protected String description;
    protected String frequency;
    @XmlElement(name = "Months")
    protected Months months;
    @XmlElement(name = "Accounting")
    protected CodeDescriptionPair accounting;
    @XmlElement(name = "Statistic")
    protected CodeDescriptionPair statistic;
    @XmlElement(name = "TaxIncluded")
    protected boolean taxIncluded;
    @XmlElement(name = "Prepaid")
    protected boolean prepaid;
    @XmlElement(name = "Terminate")
    protected boolean terminate;
    @XmlElement(name = "TerminatePeriod")
    protected float terminatePeriod;
    @XmlElement(name = "AddPrivilege")
    protected boolean addPrivilege;
    @XmlElement(name = "Privilege")
    protected CodeDescriptionPair privilege;
    @XmlElement(name = "DisplayOnRetailRegister")
    protected boolean displayOnRetailRegister;
    @XmlElement(name = "DisplayOnFBRegister")
    protected boolean displayOnFBRegister;
    @XmlElement(name = "DisplayOnRetailReceipt")
    protected boolean displayOnRetailReceipt;
    @XmlElement(name = "DisplayOnFBReceipt")
    protected boolean displayOnFBReceipt;

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
     * Gets the value of the frequency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Sets the value of the frequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrequency(String value) {
        this.frequency = value;
    }

    /**
     * Gets the value of the months property.
     * 
     * @return
     *     possible object is
     *     {@link Months }
     *     
     */
    public Months getMonths() {
        return months;
    }

    /**
     * Sets the value of the months property.
     * 
     * @param value
     *     allowed object is
     *     {@link Months }
     *     
     */
    public void setMonths(Months value) {
        this.months = value;
    }

    /**
     * Gets the value of the accounting property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getAccounting() {
        return accounting;
    }

    /**
     * Sets the value of the accounting property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setAccounting(CodeDescriptionPair value) {
        this.accounting = value;
    }

    /**
     * Gets the value of the statistic property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getStatistic() {
        return statistic;
    }

    /**
     * Sets the value of the statistic property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setStatistic(CodeDescriptionPair value) {
        this.statistic = value;
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
     * Gets the value of the prepaid property.
     * 
     */
    public boolean isPrepaid() {
        return prepaid;
    }

    /**
     * Sets the value of the prepaid property.
     * 
     */
    public void setPrepaid(boolean value) {
        this.prepaid = value;
    }

    /**
     * Gets the value of the terminate property.
     * 
     */
    public boolean isTerminate() {
        return terminate;
    }

    /**
     * Sets the value of the terminate property.
     * 
     */
    public void setTerminate(boolean value) {
        this.terminate = value;
    }

    /**
     * Gets the value of the terminatePeriod property.
     * 
     */
    public float getTerminatePeriod() {
        return terminatePeriod;
    }

    /**
     * Sets the value of the terminatePeriod property.
     * 
     */
    public void setTerminatePeriod(float value) {
        this.terminatePeriod = value;
    }

    /**
     * Gets the value of the addPrivilege property.
     * 
     */
    public boolean isAddPrivilege() {
        return addPrivilege;
    }

    /**
     * Sets the value of the addPrivilege property.
     * 
     */
    public void setAddPrivilege(boolean value) {
        this.addPrivilege = value;
    }

    /**
     * Gets the value of the privilege property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getPrivilege() {
        return privilege;
    }

    /**
     * Sets the value of the privilege property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setPrivilege(CodeDescriptionPair value) {
        this.privilege = value;
    }

    /**
     * Gets the value of the displayOnRetailRegister property.
     * 
     */
    public boolean isDisplayOnRetailRegister() {
        return displayOnRetailRegister;
    }

    /**
     * Sets the value of the displayOnRetailRegister property.
     * 
     */
    public void setDisplayOnRetailRegister(boolean value) {
        this.displayOnRetailRegister = value;
    }

    /**
     * Gets the value of the displayOnFBRegister property.
     * 
     */
    public boolean isDisplayOnFBRegister() {
        return displayOnFBRegister;
    }

    /**
     * Sets the value of the displayOnFBRegister property.
     * 
     */
    public void setDisplayOnFBRegister(boolean value) {
        this.displayOnFBRegister = value;
    }

    /**
     * Gets the value of the displayOnRetailReceipt property.
     * 
     */
    public boolean isDisplayOnRetailReceipt() {
        return displayOnRetailReceipt;
    }

    /**
     * Sets the value of the displayOnRetailReceipt property.
     * 
     */
    public void setDisplayOnRetailReceipt(boolean value) {
        this.displayOnRetailReceipt = value;
    }

    /**
     * Gets the value of the displayOnFBReceipt property.
     * 
     */
    public boolean isDisplayOnFBReceipt() {
        return displayOnFBReceipt;
    }

    /**
     * Sets the value of the displayOnFBReceipt property.
     * 
     */
    public void setDisplayOnFBReceipt(boolean value) {
        this.displayOnFBReceipt = value;
    }

}
