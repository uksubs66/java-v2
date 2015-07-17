
package org.ibsservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ItemPricing complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ItemPricing">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SecondLevelQuantity" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SecondLevelPrice" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="ThirdLevelQuantity" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ThirdLevelPrice" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="LabelExtraLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemPricing", propOrder = {
    "secondLevelQuantity",
    "secondLevelPrice",
    "thirdLevelQuantity",
    "thirdLevelPrice",
    "labelExtraLine"
})
public class ItemPricing {

    @XmlElement(name = "SecondLevelQuantity")
    protected int secondLevelQuantity;
    @XmlElement(name = "SecondLevelPrice")
    protected float secondLevelPrice;
    @XmlElement(name = "ThirdLevelQuantity")
    protected int thirdLevelQuantity;
    @XmlElement(name = "ThirdLevelPrice")
    protected float thirdLevelPrice;
    @XmlElement(name = "LabelExtraLine")
    protected String labelExtraLine;

    /**
     * Gets the value of the secondLevelQuantity property.
     * 
     */
    public int getSecondLevelQuantity() {
        return secondLevelQuantity;
    }

    /**
     * Sets the value of the secondLevelQuantity property.
     * 
     */
    public void setSecondLevelQuantity(int value) {
        this.secondLevelQuantity = value;
    }

    /**
     * Gets the value of the secondLevelPrice property.
     * 
     */
    public float getSecondLevelPrice() {
        return secondLevelPrice;
    }

    /**
     * Sets the value of the secondLevelPrice property.
     * 
     */
    public void setSecondLevelPrice(float value) {
        this.secondLevelPrice = value;
    }

    /**
     * Gets the value of the thirdLevelQuantity property.
     * 
     */
    public int getThirdLevelQuantity() {
        return thirdLevelQuantity;
    }

    /**
     * Sets the value of the thirdLevelQuantity property.
     * 
     */
    public void setThirdLevelQuantity(int value) {
        this.thirdLevelQuantity = value;
    }

    /**
     * Gets the value of the thirdLevelPrice property.
     * 
     */
    public float getThirdLevelPrice() {
        return thirdLevelPrice;
    }

    /**
     * Sets the value of the thirdLevelPrice property.
     * 
     */
    public void setThirdLevelPrice(float value) {
        this.thirdLevelPrice = value;
    }

    /**
     * Gets the value of the labelExtraLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabelExtraLine() {
        return labelExtraLine;
    }

    /**
     * Sets the value of the labelExtraLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabelExtraLine(String value) {
        this.labelExtraLine = value;
    }

}
