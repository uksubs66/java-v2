
package com.ngn.services._2007._03._20.handicapdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for HandicapInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HandicapInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HandicapId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="SourceClubId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ClubName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ClubId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NetworkId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="HandicapType" type="{http://services.ngn.com/2007/03/20/HandicapData}HandicapType"/>
 *         &lt;element name="RecordType" type="{http://services.ngn.com/2007/03/20/HandicapData}HandicapRecordType"/>
 *         &lt;element name="HandicapSuffix" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EffectiveOn" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="CreatedOn" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *       &lt;/sequence>
 *       &lt;attribute name="HandicapValue" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="SourceUserId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HandicapInfo", propOrder = {
    "handicapId",
    "sourceClubId",
    "clubName",
    "clubId",
    "networkId",
    "handicapType",
    "recordType",
    "handicapSuffix",
    "effectiveOn",
    "createdOn"
})
public class HandicapInfo {

    @XmlElement(name = "HandicapId")
    protected long handicapId;
    @XmlElement(name = "SourceClubId")
    protected String sourceClubId;
    @XmlElement(name = "ClubName", required = true, nillable = true)
    protected String clubName;
    @XmlElement(name = "ClubId")
    protected int clubId;
    @XmlElement(name = "NetworkId")
    protected int networkId;
    @XmlElement(name = "HandicapType", required = true)
    protected String handicapType;
    @XmlElement(name = "RecordType", required = true)
    protected HandicapRecordType recordType;
    @XmlElement(name = "HandicapSuffix", required = true, nillable = true)
    protected String handicapSuffix;
    @XmlElement(name = "EffectiveOn", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar effectiveOn;
    @XmlElement(name = "CreatedOn", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdOn;
    @XmlAttribute(name = "HandicapValue", required = true)
    protected short handicapValue;
    @XmlAttribute(name = "SourceUserId")
    protected String sourceUserId;

    /**
     * Gets the value of the handicapId property.
     * 
     */
    public long getHandicapId() {
        return handicapId;
    }

    /**
     * Sets the value of the handicapId property.
     * 
     */
    public void setHandicapId(long value) {
        this.handicapId = value;
    }

    /**
     * Gets the value of the sourceClubId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceClubId() {
        return sourceClubId;
    }

    /**
     * Sets the value of the sourceClubId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceClubId(String value) {
        this.sourceClubId = value;
    }

    /**
     * Gets the value of the clubName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClubName() {
        return clubName;
    }

    /**
     * Sets the value of the clubName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClubName(String value) {
        this.clubName = value;
    }

    /**
     * Gets the value of the clubId property.
     * 
     */
    public int getClubId() {
        return clubId;
    }

    /**
     * Sets the value of the clubId property.
     * 
     */
    public void setClubId(int value) {
        this.clubId = value;
    }

    /**
     * Gets the value of the networkId property.
     * 
     */
    public int getNetworkId() {
        return networkId;
    }

    /**
     * Sets the value of the networkId property.
     * 
     */
    public void setNetworkId(int value) {
        this.networkId = value;
    }

    /**
     * Gets the value of the handicapType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandicapType() {
        return handicapType;
    }

    /**
     * Sets the value of the handicapType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandicapType(String value) {
        this.handicapType = value;
    }

    /**
     * Gets the value of the recordType property.
     * 
     * @return
     *     possible object is
     *     {@link HandicapRecordType }
     *     
     */
    public HandicapRecordType getRecordType() {
        return recordType;
    }

    /**
     * Sets the value of the recordType property.
     * 
     * @param value
     *     allowed object is
     *     {@link HandicapRecordType }
     *     
     */
    public void setRecordType(HandicapRecordType value) {
        this.recordType = value;
    }

    /**
     * Gets the value of the handicapSuffix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandicapSuffix() {
        return handicapSuffix;
    }

    /**
     * Sets the value of the handicapSuffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandicapSuffix(String value) {
        this.handicapSuffix = value;
    }

    /**
     * Gets the value of the effectiveOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEffectiveOn() {
        return effectiveOn;
    }

    /**
     * Sets the value of the effectiveOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEffectiveOn(XMLGregorianCalendar value) {
        this.effectiveOn = value;
    }

    /**
     * Gets the value of the createdOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreatedOn() {
        return createdOn;
    }

    /**
     * Sets the value of the createdOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreatedOn(XMLGregorianCalendar value) {
        this.createdOn = value;
    }

    /**
     * Gets the value of the handicapValue property.
     * 
     */
    public short getHandicapValue() {
        return handicapValue;
    }

    /**
     * Sets the value of the handicapValue property.
     * 
     */
    public void setHandicapValue(short value) {
        this.handicapValue = value;
    }

    /**
     * Gets the value of the sourceUserId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceUserId() {
        return sourceUserId;
    }

    /**
     * Sets the value of the sourceUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceUserId(String value) {
        this.sourceUserId = value;
    }

}
