
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Player complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Player">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PlayerType" type="{http://ibsservices.org/}CodeDescriptionPair" minOccurs="0"/>
 *         &lt;element name="PlayerNote" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InventoryItemID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Player", propOrder = {
    "number",
    "name",
    "playerType",
    "playerNote",
    "inventoryItemID"
})
public class Player {

    @XmlElement(name = "Number")
    protected String number;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "PlayerType")
    protected CodeDescriptionPair playerType;
    @XmlElement(name = "PlayerNote")
    protected String playerNote;
    @XmlElement(name = "InventoryItemID")
    protected String inventoryItemID;

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
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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
     * Gets the value of the playerNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlayerNote() {
        return playerNote;
    }

    /**
     * Sets the value of the playerNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlayerNote(String value) {
        this.playerNote = value;
    }

    /**
     * Gets the value of the inventoryItemID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInventoryItemID() {
        return inventoryItemID;
    }

    /**
     * Sets the value of the inventoryItemID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInventoryItemID(String value) {
        this.inventoryItemID = value;
    }

}
