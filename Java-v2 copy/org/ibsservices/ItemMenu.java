
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ItemMenu complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ItemMenu">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="guid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FBPrinter" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RetailPrice" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="AddToCovers" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RepeatRound" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PromptForPrice" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="SkipModifiers" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemMenu", propOrder = {
    "guid",
    "fbPrinter",
    "retailPrice",
    "addToCovers",
    "repeatRound",
    "promptForPrice",
    "skipModifiers"
})
public class ItemMenu {

    protected String guid;
    @XmlElement(name = "FBPrinter")
    protected boolean fbPrinter;
    @XmlElement(name = "RetailPrice")
    protected float retailPrice;
    @XmlElement(name = "AddToCovers")
    protected boolean addToCovers;
    @XmlElement(name = "RepeatRound")
    protected boolean repeatRound;
    @XmlElement(name = "PromptForPrice")
    protected boolean promptForPrice;
    @XmlElement(name = "SkipModifiers")
    protected boolean skipModifiers;

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
     * Gets the value of the fbPrinter property.
     * 
     */
    public boolean isFBPrinter() {
        return fbPrinter;
    }

    /**
     * Sets the value of the fbPrinter property.
     * 
     */
    public void setFBPrinter(boolean value) {
        this.fbPrinter = value;
    }

    /**
     * Gets the value of the retailPrice property.
     * 
     */
    public float getRetailPrice() {
        return retailPrice;
    }

    /**
     * Sets the value of the retailPrice property.
     * 
     */
    public void setRetailPrice(float value) {
        this.retailPrice = value;
    }

    /**
     * Gets the value of the addToCovers property.
     * 
     */
    public boolean isAddToCovers() {
        return addToCovers;
    }

    /**
     * Sets the value of the addToCovers property.
     * 
     */
    public void setAddToCovers(boolean value) {
        this.addToCovers = value;
    }

    /**
     * Gets the value of the repeatRound property.
     * 
     */
    public boolean isRepeatRound() {
        return repeatRound;
    }

    /**
     * Sets the value of the repeatRound property.
     * 
     */
    public void setRepeatRound(boolean value) {
        this.repeatRound = value;
    }

    /**
     * Gets the value of the promptForPrice property.
     * 
     */
    public boolean isPromptForPrice() {
        return promptForPrice;
    }

    /**
     * Sets the value of the promptForPrice property.
     * 
     */
    public void setPromptForPrice(boolean value) {
        this.promptForPrice = value;
    }

    /**
     * Gets the value of the skipModifiers property.
     * 
     */
    public boolean isSkipModifiers() {
        return skipModifiers;
    }

    /**
     * Sets the value of the skipModifiers property.
     * 
     */
    public void setSkipModifiers(boolean value) {
        this.skipModifiers = value;
    }

}
