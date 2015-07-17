
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Options complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Options">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CreditLimit" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="FinanceCharge" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="FinanceRate" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="GratuityRate" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="IndividualStatementPrint" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="MasterStatementPrint" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Subtotal" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="ExtensionsOnStatement" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StatementType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ViewOnInternet" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ReciprocalMember" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="DiscountClub" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="AROnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="SalesTaxExemption" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ActivateMembershipOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="ExpireMembersipOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="IncludeReceiptsInStatement" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="BillingCycle" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Options", propOrder = {
    "creditLimit",
    "financeCharge",
    "financeRate",
    "gratuityRate",
    "individualStatementPrint",
    "masterStatementPrint",
    "subtotal",
    "extensionsOnStatement",
    "statementType",
    "viewOnInternet",
    "reciprocalMember",
    "discountClub",
    "arOnly",
    "salesTaxExemption",
    "activateMembershipOn",
    "expireMembersipOn",
    "includeReceiptsInStatement",
    "billingCycle"
})
public class Options {

    @XmlElement(name = "CreditLimit")
    protected float creditLimit;
    @XmlElement(name = "FinanceCharge")
    protected float financeCharge;
    @XmlElement(name = "FinanceRate")
    protected float financeRate;
    @XmlElement(name = "GratuityRate")
    protected float gratuityRate;
    @XmlElement(name = "IndividualStatementPrint")
    protected boolean individualStatementPrint;
    @XmlElement(name = "MasterStatementPrint")
    protected boolean masterStatementPrint;
    @XmlElement(name = "Subtotal")
    protected float subtotal;
    @XmlElement(name = "ExtensionsOnStatement")
    protected String extensionsOnStatement;
    @XmlElement(name = "StatementType")
    protected String statementType;
    @XmlElement(name = "ViewOnInternet")
    protected boolean viewOnInternet;
    @XmlElement(name = "ReciprocalMember")
    protected boolean reciprocalMember;
    @XmlElement(name = "DiscountClub")
    protected CodeDescriptionPair discountClub;
    @XmlElement(name = "AROnly")
    protected boolean arOnly;
    @XmlElement(name = "SalesTaxExemption")
    protected boolean salesTaxExemption;
    @XmlElement(name = "ActivateMembershipOn", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar activateMembershipOn;
    @XmlElement(name = "ExpireMembersipOn", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expireMembersipOn;
    @XmlElement(name = "IncludeReceiptsInStatement")
    protected boolean includeReceiptsInStatement;
    @XmlElement(name = "BillingCycle")
    protected int billingCycle;

    /**
     * Gets the value of the creditLimit property.
     * 
     */
    public float getCreditLimit() {
        return creditLimit;
    }

    /**
     * Sets the value of the creditLimit property.
     * 
     */
    public void setCreditLimit(float value) {
        this.creditLimit = value;
    }

    /**
     * Gets the value of the financeCharge property.
     * 
     */
    public float getFinanceCharge() {
        return financeCharge;
    }

    /**
     * Sets the value of the financeCharge property.
     * 
     */
    public void setFinanceCharge(float value) {
        this.financeCharge = value;
    }

    /**
     * Gets the value of the financeRate property.
     * 
     */
    public float getFinanceRate() {
        return financeRate;
    }

    /**
     * Sets the value of the financeRate property.
     * 
     */
    public void setFinanceRate(float value) {
        this.financeRate = value;
    }

    /**
     * Gets the value of the gratuityRate property.
     * 
     */
    public float getGratuityRate() {
        return gratuityRate;
    }

    /**
     * Sets the value of the gratuityRate property.
     * 
     */
    public void setGratuityRate(float value) {
        this.gratuityRate = value;
    }

    /**
     * Gets the value of the individualStatementPrint property.
     * 
     */
    public boolean isIndividualStatementPrint() {
        return individualStatementPrint;
    }

    /**
     * Sets the value of the individualStatementPrint property.
     * 
     */
    public void setIndividualStatementPrint(boolean value) {
        this.individualStatementPrint = value;
    }

    /**
     * Gets the value of the masterStatementPrint property.
     * 
     */
    public boolean isMasterStatementPrint() {
        return masterStatementPrint;
    }

    /**
     * Sets the value of the masterStatementPrint property.
     * 
     */
    public void setMasterStatementPrint(boolean value) {
        this.masterStatementPrint = value;
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
     * Gets the value of the extensionsOnStatement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionsOnStatement() {
        return extensionsOnStatement;
    }

    /**
     * Sets the value of the extensionsOnStatement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionsOnStatement(String value) {
        this.extensionsOnStatement = value;
    }

    /**
     * Gets the value of the statementType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatementType() {
        return statementType;
    }

    /**
     * Sets the value of the statementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatementType(String value) {
        this.statementType = value;
    }

    /**
     * Gets the value of the viewOnInternet property.
     * 
     */
    public boolean isViewOnInternet() {
        return viewOnInternet;
    }

    /**
     * Sets the value of the viewOnInternet property.
     * 
     */
    public void setViewOnInternet(boolean value) {
        this.viewOnInternet = value;
    }

    /**
     * Gets the value of the reciprocalMember property.
     * 
     */
    public boolean isReciprocalMember() {
        return reciprocalMember;
    }

    /**
     * Sets the value of the reciprocalMember property.
     * 
     */
    public void setReciprocalMember(boolean value) {
        this.reciprocalMember = value;
    }

    /**
     * Gets the value of the discountClub property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getDiscountClub() {
        return discountClub;
    }

    /**
     * Sets the value of the discountClub property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setDiscountClub(CodeDescriptionPair value) {
        this.discountClub = value;
    }

    /**
     * Gets the value of the arOnly property.
     * 
     */
    public boolean isAROnly() {
        return arOnly;
    }

    /**
     * Sets the value of the arOnly property.
     * 
     */
    public void setAROnly(boolean value) {
        this.arOnly = value;
    }

    /**
     * Gets the value of the salesTaxExemption property.
     * 
     */
    public boolean isSalesTaxExemption() {
        return salesTaxExemption;
    }

    /**
     * Sets the value of the salesTaxExemption property.
     * 
     */
    public void setSalesTaxExemption(boolean value) {
        this.salesTaxExemption = value;
    }

    /**
     * Gets the value of the activateMembershipOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getActivateMembershipOn() {
        return activateMembershipOn;
    }

    /**
     * Sets the value of the activateMembershipOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setActivateMembershipOn(XMLGregorianCalendar value) {
        this.activateMembershipOn = value;
    }

    /**
     * Gets the value of the expireMembersipOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpireMembersipOn() {
        return expireMembersipOn;
    }

    /**
     * Sets the value of the expireMembersipOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpireMembersipOn(XMLGregorianCalendar value) {
        this.expireMembersipOn = value;
    }

    /**
     * Gets the value of the includeReceiptsInStatement property.
     * 
     */
    public boolean isIncludeReceiptsInStatement() {
        return includeReceiptsInStatement;
    }

    /**
     * Sets the value of the includeReceiptsInStatement property.
     * 
     */
    public void setIncludeReceiptsInStatement(boolean value) {
        this.includeReceiptsInStatement = value;
    }

    /**
     * Gets the value of the billingCycle property.
     * 
     */
    public int getBillingCycle() {
        return billingCycle;
    }

    /**
     * Sets the value of the billingCycle property.
     * 
     */
    public void setBillingCycle(int value) {
        this.billingCycle = value;
    }

}
