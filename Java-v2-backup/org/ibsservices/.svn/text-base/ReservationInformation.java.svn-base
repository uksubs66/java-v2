
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ReservationInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReservationInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReservationDateAndTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="NumberOfPlayers" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumberOfHoles" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ReservationSource" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="Facility" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="Tee" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="ConfirmationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BIA" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="BookedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Carts" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CreditCardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CreditCardExp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="ReservationNote" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Reseller" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="ResellerPlayerType" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="VoucherNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ChargeInfo" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="Exceptions" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="ReservationTotalAmount" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Players" type="{http://ibsservices.org/}ArrayOfPlayer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReservationInformation", propOrder = {
    "reservationDateAndTime",
    "numberOfPlayers",
    "numberOfHoles",
    "reservationSource",
    "facility",
    "tee",
    "confirmationNumber",
    "bia",
    "bookedDate",
    "carts",
    "creditCardNumber",
    "creditCardExp",
    "reservationNote",
    "reseller",
    "resellerPlayerType",
    "voucherNumber",
    "chargeInfo",
    "exceptions",
    "reservationTotalAmount",
    "players"
})
public class ReservationInformation {

    @XmlElement(name = "ReservationDateAndTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar reservationDateAndTime;
    @XmlElement(name = "NumberOfPlayers")
    protected int numberOfPlayers;
    @XmlElement(name = "NumberOfHoles")
    protected int numberOfHoles;
    @XmlElement(name = "ReservationSource")
    protected CodeDescriptionPair reservationSource;
    @XmlElement(name = "Facility")
    protected CodeDescriptionPair facility;
    @XmlElement(name = "Tee")
    protected CodeDescriptionPair tee;
    @XmlElement(name = "ConfirmationNumber")
    protected String confirmationNumber;
    @XmlElement(name = "BIA", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar bia;
    @XmlElement(name = "BookedDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar bookedDate;
    @XmlElement(name = "Carts")
    protected int carts;
    @XmlElement(name = "CreditCardNumber")
    protected String creditCardNumber;
    @XmlElement(name = "CreditCardExp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creditCardExp;
    @XmlElement(name = "ReservationNote")
    protected String reservationNote;
    @XmlElement(name = "Reseller")
    protected CodeDescriptionPair reseller;
    @XmlElement(name = "ResellerPlayerType")
    protected CodeDescriptionPair resellerPlayerType;
    @XmlElement(name = "VoucherNumber")
    protected String voucherNumber;
    @XmlElement(name = "ChargeInfo")
    protected Object chargeInfo;
    @XmlElement(name = "Exceptions")
    protected Object exceptions;
    @XmlElement(name = "ReservationTotalAmount")
    protected float reservationTotalAmount;
    @XmlElement(name = "Players")
    protected ArrayOfPlayer players;

    /**
     * Gets the value of the reservationDateAndTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReservationDateAndTime() {
        return reservationDateAndTime;
    }

    /**
     * Sets the value of the reservationDateAndTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReservationDateAndTime(XMLGregorianCalendar value) {
        this.reservationDateAndTime = value;
    }

    /**
     * Gets the value of the numberOfPlayers property.
     * 
     */
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * Sets the value of the numberOfPlayers property.
     * 
     */
    public void setNumberOfPlayers(int value) {
        this.numberOfPlayers = value;
    }

    /**
     * Gets the value of the numberOfHoles property.
     * 
     */
    public int getNumberOfHoles() {
        return numberOfHoles;
    }

    /**
     * Sets the value of the numberOfHoles property.
     * 
     */
    public void setNumberOfHoles(int value) {
        this.numberOfHoles = value;
    }

    /**
     * Gets the value of the reservationSource property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getReservationSource() {
        return reservationSource;
    }

    /**
     * Sets the value of the reservationSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setReservationSource(CodeDescriptionPair value) {
        this.reservationSource = value;
    }

    /**
     * Gets the value of the facility property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getFacility() {
        return facility;
    }

    /**
     * Sets the value of the facility property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setFacility(CodeDescriptionPair value) {
        this.facility = value;
    }

    /**
     * Gets the value of the tee property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getTee() {
        return tee;
    }

    /**
     * Sets the value of the tee property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setTee(CodeDescriptionPair value) {
        this.tee = value;
    }

    /**
     * Gets the value of the confirmationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    /**
     * Sets the value of the confirmationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfirmationNumber(String value) {
        this.confirmationNumber = value;
    }

    /**
     * Gets the value of the bia property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBIA() {
        return bia;
    }

    /**
     * Sets the value of the bia property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBIA(XMLGregorianCalendar value) {
        this.bia = value;
    }

    /**
     * Gets the value of the bookedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBookedDate() {
        return bookedDate;
    }

    /**
     * Sets the value of the bookedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBookedDate(XMLGregorianCalendar value) {
        this.bookedDate = value;
    }

    /**
     * Gets the value of the carts property.
     * 
     */
    public int getCarts() {
        return carts;
    }

    /**
     * Sets the value of the carts property.
     * 
     */
    public void setCarts(int value) {
        this.carts = value;
    }

    /**
     * Gets the value of the creditCardNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * Sets the value of the creditCardNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditCardNumber(String value) {
        this.creditCardNumber = value;
    }

    /**
     * Gets the value of the creditCardExp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreditCardExp() {
        return creditCardExp;
    }

    /**
     * Sets the value of the creditCardExp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreditCardExp(XMLGregorianCalendar value) {
        this.creditCardExp = value;
    }

    /**
     * Gets the value of the reservationNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReservationNote() {
        return reservationNote;
    }

    /**
     * Sets the value of the reservationNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReservationNote(String value) {
        this.reservationNote = value;
    }

    /**
     * Gets the value of the reseller property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getReseller() {
        return reseller;
    }

    /**
     * Sets the value of the reseller property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setReseller(CodeDescriptionPair value) {
        this.reseller = value;
    }

    /**
     * Gets the value of the resellerPlayerType property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getResellerPlayerType() {
        return resellerPlayerType;
    }

    /**
     * Sets the value of the resellerPlayerType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setResellerPlayerType(CodeDescriptionPair value) {
        this.resellerPlayerType = value;
    }

    /**
     * Gets the value of the voucherNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoucherNumber() {
        return voucherNumber;
    }

    /**
     * Sets the value of the voucherNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoucherNumber(String value) {
        this.voucherNumber = value;
    }

    /**
     * Gets the value of the chargeInfo property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getChargeInfo() {
        return chargeInfo;
    }

    /**
     * Sets the value of the chargeInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setChargeInfo(Object value) {
        this.chargeInfo = value;
    }

    /**
     * Gets the value of the exceptions property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getExceptions() {
        return exceptions;
    }

    /**
     * Sets the value of the exceptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setExceptions(Object value) {
        this.exceptions = value;
    }

    /**
     * Gets the value of the reservationTotalAmount property.
     * 
     */
    public float getReservationTotalAmount() {
        return reservationTotalAmount;
    }

    /**
     * Sets the value of the reservationTotalAmount property.
     * 
     */
    public void setReservationTotalAmount(float value) {
        this.reservationTotalAmount = value;
    }

    /**
     * Gets the value of the players property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPlayer }
     *     
     */
    public ArrayOfPlayer getPlayers() {
        return players;
    }

    /**
     * Sets the value of the players property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPlayer }
     *     
     */
    public void setPlayers(ArrayOfPlayer value) {
        this.players = value;
    }

}
