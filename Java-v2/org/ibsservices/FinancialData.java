
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for FinancialData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinancialData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DateofGeneration" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="MemberID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Extension" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BillingPeriodClosingDate" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="isExtensionAccount" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="BalanceForward" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="PreviousBalanceDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="CurrentBalanceDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="StatementPrinted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Balance" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Balance30Day" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Balance60Day" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Balance90Day" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="BalanceOver90" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="MinimumStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="MinimumCycleEnd" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="MinimumUnspent" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="MinimumSpent" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="MinimumRequiredPurchase" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Transactions" type="{http://ibsservices.org/}ArrayOfTicketData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialData", propOrder = {
    "dateofGeneration",
    "memberID",
    "extension",
    "billingPeriodClosingDate",
    "isExtensionAccount",
    "balanceForward",
    "previousBalanceDate",
    "currentBalanceDate",
    "statementPrinted",
    "balance",
    "balance30Day",
    "balance60Day",
    "balance90Day",
    "balanceOver90",
    "minimumStartDate",
    "minimumCycleEnd",
    "minimumUnspent",
    "minimumSpent",
    "minimumRequiredPurchase",
    "transactions"
})
public class FinancialData {

    @XmlElement(name = "DateofGeneration", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateofGeneration;
    @XmlElement(name = "MemberID")
    protected String memberID;
    @XmlElement(name = "Extension")
    protected String extension;
    @XmlElement(name = "BillingPeriodClosingDate")
    protected int billingPeriodClosingDate;
    protected boolean isExtensionAccount;
    @XmlElement(name = "BalanceForward")
    protected float balanceForward;
    @XmlElement(name = "PreviousBalanceDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar previousBalanceDate;
    @XmlElement(name = "CurrentBalanceDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar currentBalanceDate;
    @XmlElement(name = "StatementPrinted")
    protected boolean statementPrinted;
    @XmlElement(name = "Balance")
    protected float balance;
    @XmlElement(name = "Balance30Day")
    protected float balance30Day;
    @XmlElement(name = "Balance60Day")
    protected float balance60Day;
    @XmlElement(name = "Balance90Day")
    protected float balance90Day;
    @XmlElement(name = "BalanceOver90")
    protected float balanceOver90;
    @XmlElement(name = "MinimumStartDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar minimumStartDate;
    @XmlElement(name = "MinimumCycleEnd", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar minimumCycleEnd;
    @XmlElement(name = "MinimumUnspent")
    protected float minimumUnspent;
    @XmlElement(name = "MinimumSpent")
    protected float minimumSpent;
    @XmlElement(name = "MinimumRequiredPurchase")
    protected float minimumRequiredPurchase;
    @XmlElement(name = "Transactions")
    protected ArrayOfTicketData transactions;

    /**
     * Gets the value of the dateofGeneration property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateofGeneration() {
        return dateofGeneration;
    }

    /**
     * Sets the value of the dateofGeneration property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateofGeneration(XMLGregorianCalendar value) {
        this.dateofGeneration = value;
    }

    /**
     * Gets the value of the memberID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMemberID() {
        return memberID;
    }

    /**
     * Sets the value of the memberID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMemberID(String value) {
        this.memberID = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtension(String value) {
        this.extension = value;
    }

    /**
     * Gets the value of the billingPeriodClosingDate property.
     * 
     */
    public int getBillingPeriodClosingDate() {
        return billingPeriodClosingDate;
    }

    /**
     * Sets the value of the billingPeriodClosingDate property.
     * 
     */
    public void setBillingPeriodClosingDate(int value) {
        this.billingPeriodClosingDate = value;
    }

    /**
     * Gets the value of the isExtensionAccount property.
     * 
     */
    public boolean isIsExtensionAccount() {
        return isExtensionAccount;
    }

    /**
     * Sets the value of the isExtensionAccount property.
     * 
     */
    public void setIsExtensionAccount(boolean value) {
        this.isExtensionAccount = value;
    }

    /**
     * Gets the value of the balanceForward property.
     * 
     */
    public float getBalanceForward() {
        return balanceForward;
    }

    /**
     * Sets the value of the balanceForward property.
     * 
     */
    public void setBalanceForward(float value) {
        this.balanceForward = value;
    }

    /**
     * Gets the value of the previousBalanceDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPreviousBalanceDate() {
        return previousBalanceDate;
    }

    /**
     * Sets the value of the previousBalanceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPreviousBalanceDate(XMLGregorianCalendar value) {
        this.previousBalanceDate = value;
    }

    /**
     * Gets the value of the currentBalanceDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCurrentBalanceDate() {
        return currentBalanceDate;
    }

    /**
     * Sets the value of the currentBalanceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCurrentBalanceDate(XMLGregorianCalendar value) {
        this.currentBalanceDate = value;
    }

    /**
     * Gets the value of the statementPrinted property.
     * 
     */
    public boolean isStatementPrinted() {
        return statementPrinted;
    }

    /**
     * Sets the value of the statementPrinted property.
     * 
     */
    public void setStatementPrinted(boolean value) {
        this.statementPrinted = value;
    }

    /**
     * Gets the value of the balance property.
     * 
     */
    public float getBalance() {
        return balance;
    }

    /**
     * Sets the value of the balance property.
     * 
     */
    public void setBalance(float value) {
        this.balance = value;
    }

    /**
     * Gets the value of the balance30Day property.
     * 
     */
    public float getBalance30Day() {
        return balance30Day;
    }

    /**
     * Sets the value of the balance30Day property.
     * 
     */
    public void setBalance30Day(float value) {
        this.balance30Day = value;
    }

    /**
     * Gets the value of the balance60Day property.
     * 
     */
    public float getBalance60Day() {
        return balance60Day;
    }

    /**
     * Sets the value of the balance60Day property.
     * 
     */
    public void setBalance60Day(float value) {
        this.balance60Day = value;
    }

    /**
     * Gets the value of the balance90Day property.
     * 
     */
    public float getBalance90Day() {
        return balance90Day;
    }

    /**
     * Sets the value of the balance90Day property.
     * 
     */
    public void setBalance90Day(float value) {
        this.balance90Day = value;
    }

    /**
     * Gets the value of the balanceOver90 property.
     * 
     */
    public float getBalanceOver90() {
        return balanceOver90;
    }

    /**
     * Sets the value of the balanceOver90 property.
     * 
     */
    public void setBalanceOver90(float value) {
        this.balanceOver90 = value;
    }

    /**
     * Gets the value of the minimumStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMinimumStartDate() {
        return minimumStartDate;
    }

    /**
     * Sets the value of the minimumStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMinimumStartDate(XMLGregorianCalendar value) {
        this.minimumStartDate = value;
    }

    /**
     * Gets the value of the minimumCycleEnd property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMinimumCycleEnd() {
        return minimumCycleEnd;
    }

    /**
     * Sets the value of the minimumCycleEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMinimumCycleEnd(XMLGregorianCalendar value) {
        this.minimumCycleEnd = value;
    }

    /**
     * Gets the value of the minimumUnspent property.
     * 
     */
    public float getMinimumUnspent() {
        return minimumUnspent;
    }

    /**
     * Sets the value of the minimumUnspent property.
     * 
     */
    public void setMinimumUnspent(float value) {
        this.minimumUnspent = value;
    }

    /**
     * Gets the value of the minimumSpent property.
     * 
     */
    public float getMinimumSpent() {
        return minimumSpent;
    }

    /**
     * Sets the value of the minimumSpent property.
     * 
     */
    public void setMinimumSpent(float value) {
        this.minimumSpent = value;
    }

    /**
     * Gets the value of the minimumRequiredPurchase property.
     * 
     */
    public float getMinimumRequiredPurchase() {
        return minimumRequiredPurchase;
    }

    /**
     * Sets the value of the minimumRequiredPurchase property.
     * 
     */
    public void setMinimumRequiredPurchase(float value) {
        this.minimumRequiredPurchase = value;
    }

    /**
     * Gets the value of the transactions property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTicketData }
     *     
     */
    public ArrayOfTicketData getTransactions() {
        return transactions;
    }

    /**
     * Sets the value of the transactions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTicketData }
     *     
     */
    public void setTransactions(ArrayOfTicketData value) {
        this.transactions = value;
    }

}
