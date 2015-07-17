
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RetailInventoryItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RetailInventoryItem">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ibsservices.org/}InventoryItemBaseClass">
 *       &lt;sequence>
 *         &lt;element name="IsTeeTimeItem" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Holes" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DefaultReservableItem" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="UseTeeTimePrice" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PromptForPrice" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AskForMemberSwipe" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="SellableAtPointOfSale" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="SpecialOrderItem" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="CreditBookItem" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AverageCost" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="RetailPrice" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="IsCreditBookItem" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AskGuestInfo" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="HotKeyLookupCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pricing" type="{http://ibsservices.org/}ItemPricing" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RetailInventoryItem", propOrder = {
    "isTeeTimeItem",
    "holes",
    "defaultReservableItem",
    "useTeeTimePrice",
    "promptForPrice",
    "askForMemberSwipe",
    "sellableAtPointOfSale",
    "specialOrderItem",
    "creditBookItem",
    "averageCost",
    "retailPrice",
    "isCreditBookItem",
    "askGuestInfo",
    "hotKeyLookupCode",
    "pricing"
})
public class RetailInventoryItem
    extends InventoryItemBaseClass
{

    @XmlElement(name = "IsTeeTimeItem")
    protected boolean isTeeTimeItem;
    @XmlElement(name = "Holes")
    protected int holes;
    @XmlElement(name = "DefaultReservableItem")
    protected boolean defaultReservableItem;
    @XmlElement(name = "UseTeeTimePrice")
    protected boolean useTeeTimePrice;
    @XmlElement(name = "PromptForPrice")
    protected boolean promptForPrice;
    @XmlElement(name = "AskForMemberSwipe")
    protected boolean askForMemberSwipe;
    @XmlElement(name = "SellableAtPointOfSale")
    protected boolean sellableAtPointOfSale;
    @XmlElement(name = "SpecialOrderItem")
    protected boolean specialOrderItem;
    @XmlElement(name = "CreditBookItem")
    protected boolean creditBookItem;
    @XmlElement(name = "AverageCost")
    protected float averageCost;
    @XmlElement(name = "RetailPrice")
    protected float retailPrice;
    @XmlElement(name = "IsCreditBookItem")
    protected boolean isCreditBookItem;
    @XmlElement(name = "AskGuestInfo")
    protected boolean askGuestInfo;
    @XmlElement(name = "HotKeyLookupCode")
    protected String hotKeyLookupCode;
    @XmlElement(name = "Pricing")
    protected ItemPricing pricing;

    /**
     * Gets the value of the isTeeTimeItem property.
     * 
     */
    public boolean isIsTeeTimeItem() {
        return isTeeTimeItem;
    }

    /**
     * Sets the value of the isTeeTimeItem property.
     * 
     */
    public void setIsTeeTimeItem(boolean value) {
        this.isTeeTimeItem = value;
    }

    /**
     * Gets the value of the holes property.
     * 
     */
    public int getHoles() {
        return holes;
    }

    /**
     * Sets the value of the holes property.
     * 
     */
    public void setHoles(int value) {
        this.holes = value;
    }

    /**
     * Gets the value of the defaultReservableItem property.
     * 
     */
    public boolean isDefaultReservableItem() {
        return defaultReservableItem;
    }

    /**
     * Sets the value of the defaultReservableItem property.
     * 
     */
    public void setDefaultReservableItem(boolean value) {
        this.defaultReservableItem = value;
    }

    /**
     * Gets the value of the useTeeTimePrice property.
     * 
     */
    public boolean isUseTeeTimePrice() {
        return useTeeTimePrice;
    }

    /**
     * Sets the value of the useTeeTimePrice property.
     * 
     */
    public void setUseTeeTimePrice(boolean value) {
        this.useTeeTimePrice = value;
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
     * Gets the value of the askForMemberSwipe property.
     * 
     */
    public boolean isAskForMemberSwipe() {
        return askForMemberSwipe;
    }

    /**
     * Sets the value of the askForMemberSwipe property.
     * 
     */
    public void setAskForMemberSwipe(boolean value) {
        this.askForMemberSwipe = value;
    }

    /**
     * Gets the value of the sellableAtPointOfSale property.
     * 
     */
    public boolean isSellableAtPointOfSale() {
        return sellableAtPointOfSale;
    }

    /**
     * Sets the value of the sellableAtPointOfSale property.
     * 
     */
    public void setSellableAtPointOfSale(boolean value) {
        this.sellableAtPointOfSale = value;
    }

    /**
     * Gets the value of the specialOrderItem property.
     * 
     */
    public boolean isSpecialOrderItem() {
        return specialOrderItem;
    }

    /**
     * Sets the value of the specialOrderItem property.
     * 
     */
    public void setSpecialOrderItem(boolean value) {
        this.specialOrderItem = value;
    }

    /**
     * Gets the value of the creditBookItem property.
     * 
     */
    public boolean isCreditBookItem() {
        return creditBookItem;
    }

    /**
     * Sets the value of the creditBookItem property.
     * 
     */
    public void setCreditBookItem(boolean value) {
        this.creditBookItem = value;
    }

    /**
     * Gets the value of the averageCost property.
     * 
     */
    public float getAverageCost() {
        return averageCost;
    }

    /**
     * Sets the value of the averageCost property.
     * 
     */
    public void setAverageCost(float value) {
        this.averageCost = value;
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
     * Gets the value of the isCreditBookItem property.
     * 
     */
    public boolean isIsCreditBookItem() {
        return isCreditBookItem;
    }

    /**
     * Sets the value of the isCreditBookItem property.
     * 
     */
    public void setIsCreditBookItem(boolean value) {
        this.isCreditBookItem = value;
    }

    /**
     * Gets the value of the askGuestInfo property.
     * 
     */
    public boolean isAskGuestInfo() {
        return askGuestInfo;
    }

    /**
     * Sets the value of the askGuestInfo property.
     * 
     */
    public void setAskGuestInfo(boolean value) {
        this.askGuestInfo = value;
    }

    /**
     * Gets the value of the hotKeyLookupCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotKeyLookupCode() {
        return hotKeyLookupCode;
    }

    /**
     * Sets the value of the hotKeyLookupCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotKeyLookupCode(String value) {
        this.hotKeyLookupCode = value;
    }

    /**
     * Gets the value of the pricing property.
     * 
     * @return
     *     possible object is
     *     {@link ItemPricing }
     *     
     */
    public ItemPricing getPricing() {
        return pricing;
    }

    /**
     * Sets the value of the pricing property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemPricing }
     *     
     */
    public void setPricing(ItemPricing value) {
        this.pricing = value;
    }

}
