
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MemberData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MemberData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MemberID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="guid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Extension" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Level" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Person" type="{http://ibsservices.org/}Person" minOccurs="0"/>
 *         &lt;element name="MemberType" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="AwardLevel" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="PlayerType" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="Balance" type="{http://ibsservices.org/}Balance" minOccurs="0"/>
 *         &lt;element name="Addresses" type="{http://ibsservices.org/}ArrayOfAddress" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MemberData", propOrder = {
    "memberID",
    "guid",
    "extension",
    "status",
    "level",
    "comment",
    "person",
    "memberType",
    "awardLevel",
    "playerType",
    "balance",
    "addresses"
})
@XmlSeeAlso({
    DemographicData.class,
    SchedulingData.class
})
public class MemberData {

    @XmlElement(name = "MemberID")
    protected String memberID;
    protected String guid;
    @XmlElement(name = "Extension")
    protected String extension;
    protected String status;
    @XmlElement(name = "Level")
    protected String level;
    @XmlElement(name = "Comment")
    protected String comment;
    @XmlElement(name = "Person")
    protected Person person;
    @XmlElement(name = "MemberType")
    protected CodeDescriptionPair memberType;
    @XmlElement(name = "AwardLevel")
    protected CodeDescriptionPair awardLevel;
    @XmlElement(name = "PlayerType")
    protected CodeDescriptionPair playerType;
    @XmlElement(name = "Balance")
    protected Balance balance;
    @XmlElement(name = "Addresses")
    protected ArrayOfAddress addresses;

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
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevel(String value) {
        this.level = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the person property.
     * 
     * @return
     *     possible object is
     *     {@link Person }
     *     
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     * 
     * @param value
     *     allowed object is
     *     {@link Person }
     *     
     */
    public void setPerson(Person value) {
        this.person = value;
    }

    /**
     * Gets the value of the memberType property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getMemberType() {
        return memberType;
    }

    /**
     * Sets the value of the memberType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setMemberType(CodeDescriptionPair value) {
        this.memberType = value;
    }

    /**
     * Gets the value of the awardLevel property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getAwardLevel() {
        return awardLevel;
    }

    /**
     * Sets the value of the awardLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setAwardLevel(CodeDescriptionPair value) {
        this.awardLevel = value;
    }

    /**
     * Gets the value of the playerType property.
     * 
     * @return
     *     possible object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public CodeDescriptionPair getPlayerType() {
        return playerType;
    }

    /**
     * Sets the value of the playerType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeDescriptionPair }
     *     
     */
    public void setPlayerType(CodeDescriptionPair value) {
        this.playerType = value;
    }

    /**
     * Gets the value of the balance property.
     * 
     * @return
     *     possible object is
     *     {@link Balance }
     *     
     */
    public Balance getBalance() {
        return balance;
    }

    /**
     * Sets the value of the balance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Balance }
     *     
     */
    public void setBalance(Balance value) {
        this.balance = value;
    }

    /**
     * Gets the value of the addresses property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAddress }
     *     
     */
    public ArrayOfAddress getAddresses() {
        return addresses;
    }

    /**
     * Sets the value of the addresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAddress }
     *     
     */
    public void setAddresses(ArrayOfAddress value) {
        this.addresses = value;
    }

}
